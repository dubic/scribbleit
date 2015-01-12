/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.idm.spi;

import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.dto.Registration;
import com.dubic.scribbleit.dto.UserActivation;
import com.dubic.scribbleit.dto.UserData;
import com.dubic.scribbleit.email.MailServiceImpl;
import com.dubic.scribbleit.email.SimpleMailEvent;
import com.dubic.scribbleit.idm.models.Group;
import com.dubic.scribbleit.idm.models.Role;
import com.dubic.scribbleit.idm.models.Token;
import com.dubic.scribbleit.idm.models.User;
import com.dubic.scribbleit.models.Profile;
import com.dubic.scribbleit.utils.IdmCrypt;
import com.dubic.scribbleit.utils.IdmUtils;
import com.google.gson.Gson;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.JAXB;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**performs all identity management ops.
 *<br>config properties
 * <ul><li>actExpirationMins</li>
 * @author dubem
 * @since idm 1.0.0
 */
@Named("identityService")
public class IdentityServiceImpl implements IdentityService,UserDetailsService{
    

    private final Logger log = Logger.getLogger(IdentityServiceImpl.class);
    
    @Inject
    private Database db;
    @Inject
    private MailServiceImpl mailService;
    
    @Value("${default.profile.picture}")
    private String avatar;
    @Value("${activation.url}")
    private String activationURL;
    
    @PostConstruct
    public void inited(){
        log.debug("AVATAR >>>>>>>>>> "+avatar);
//        db.createQuery("SELECT c FROM JKComment c").getResultList();
    }
    /**
     * encrypts password and creates a new user.email and screen name validation
     * should be done at client side
     *
     * @param userData
     * @return the created user
     * @since idm 1.0.0
     */
    @Override
//    @Transactional("dbtrans")
    public User userRegistration(UserData userData) throws PersistenceException ,ConstraintViolationException{
        log.info("userRegistration - " + userData.toString());
        User user = new User(userData);
        //BUILD PROFILE
        Profile profile = new Profile();
        profile.setPicture(avatar);
        user.setProfile(profile);
        user.setActivated(false);
        IdmUtils.validate(user, User.class);
        String enc_pwd = IdmCrypt.encodeMD5(user.getPassword(),user.getEmail());
        user.setPassword(enc_pwd);
        
        //SAVE USER,USER ACTIVATION TOKEN
        db.persist(user);
        //CREATE AND SOFT VALIDATE ACTIVATION TOKEN
        Token token = createActivationToken(user);
        try {
            IdmUtils.validate(token, Token.class);
        } catch (ConstraintViolationException cve) {
            log.fatal(IdmUtils.printValidationMsg(cve.getConstraintViolations()));
            throw new IdentityServiceException("service unavailaible.Try again later");
        }
        db.persist(token);
        //SEND MAIL
        SimpleMailEvent mail = new SimpleMailEvent(user.getEmail());
        Map model = new HashMap();
        model.put("name", user.getScreenName());
        model.put("url", activationURL+token.getToken());
        mailService.sendMail(mail, "reg.vm", model);
        log.info(String.format("User registration initiated successfullly...[%s]", user.getScreenName()));
        return user;
    }

    /**
     * queries the db for the existence of an email address
     *
     * @param email the email to query
     * @return the email address if exists or null
     * @since idm 1.0.0
     */
    @Override
    public String getUniqueEmail(String email) throws PersistenceException {
        log.debug("getUniqueEmail - " + email);
        List<String> mails = db.namedQuery("user.findmail.email", String.class).setParameter("email", email).getResultList();
        return IdmUtils.getFirstOrNull(mails);
    }

    /**
     * queries the db for the existence of a screen name
     *
     * @param screenName
     * @return the screen name if exists or null
     * @since idm 1.0.0
     */
    @Override
    public String validateScreenName(String screenName) throws PersistenceException {
        log.debug(String.format("validate Screen Name(%s)", screenName));
        List<String> snameList = db.createQuery("SELECT u.screenName FROM User u WHERE u.screenName = :scname", String.class).setParameter("scname", screenName).getResultList();
        return IdmUtils.getFirstOrNull(snameList);
    }

    /**
     * saves a modified user to the db
     *
     * @param user modified user details
     * @since idm 1.0.0
     */
    @Override
    public void updateUser(User user) throws PersistenceException {
        log.info("updateUser - " + user);
        user.setModifiedDate(new Date());
        db.merge(user);
    }

    @Override
    public void getChangePwdToken(String email) throws PersistenceException{
        log.info(String.format("getChangePwdToken(%s)", email));
        User user = findUserByEmail(email);
        if (user == null) {
            throw new ProviderNotFoundException(String.format("User with email[%s] not found", email));
        }
        //GENERATE RANDOM TOKEN USING TIME MILLIS
        String tokenStr = IdmUtils.generateTimeToken();
        //CREATE AND SAVE TOKEN
        Token token = new Token(user, tokenStr);
        token.setExpiryDt(IdmUtils.getPwordResetTokenExpiryDt());
        token.setType(Token.PASSWORD_RESET_TOKEN);
        //SAVE TOKEN
        db.persist(token);
        
        //send mail
    }

    @Override
    public void changePassword(String tokenStr, String password) throws InvalidTokenException,LinkExpiredException{
        log.info("changePassword(****)");
        //GET TOKEN FROM DB
        Token token = getActiveToken(tokenStr);
        if(token == null){
            throw new InvalidTokenException("the token has expired");
        }
        if(new Date().after(token.getExpiryDt())){
            throw new LinkExpiredException("Token has expired");
        }
        //UPDATE PASSWORD
        User user = token.getUser();
        user.setPassword(IdmCrypt.encodeMD5(password, user.getUsername()));
        user.setPasswordChangeDate(new Date());
        //FLAG TOKEN AS USED
        useToken(token);
        db.merge(user,token);
        log.info(String.format("[%s] password changed", user.getEmail()));
    }

    /**used in authentication.queries the db for user matching the email and
     * password.
     *
     * @param email
     * @param pwd the encrypted password
     * @return single user matching the params
     * @since idm 1.0.0
     */
    @Override
    public User findUserByEmailandPasword(String email, String pwd) {
        log.debug(String.format("find User By Email and Pasword [%s] [****]" , email));
        List<User> ulist = db.namedQuery("user.find.mail.password", User.class).setParameter("email", email).setParameter("pwd", pwd).getResultList();
        return IdmUtils.getFirstOrNull(ulist);
    }
    
    
    @Override
    public User findUserByEmail(String email) {
        log.debug(String.format("find User By Email [%s]" , email));
        List<User> ulist = db.namedQuery("user.find.mail", User.class).setParameter("email", email).getResultList();
        return IdmUtils.getFirstOrNull(ulist);
    }
    @Override
    public User findUserByScreenName(String screenName){
        log.debug(String.format("find User By Screen name [%s]" , screenName));
        List<User> ulist = db.namedQuery("user.find.screenName", User.class).setParameter("screenName", screenName).getResultList();
        return IdmUtils.getFirstOrNull(ulist);
    }

    /**
     * creates a new role in the db with role name and description
     *
     * @param name role name
     * @param desc
     * @return the created role
     * @since idm 1.0.0
     */
    @Override
    public Role createRole(String name, String desc) throws NullPointerException, PersistenceException {
        log.info("createRole - " + name);
        Role role = new Role(name, desc);
        db.persist(role);
        return role;
    }

    /**
     * create a group in db
     *
     * @param grp
     * @return created group
     * @since idm 1.0.0
     */
    @Override
    public Group createGroup(Group grp) throws PersistenceException {
        log.info("createGroup - " + grp.getName());
        db.persist(grp);
        return grp;
    }

    /**
     * creates a group from parameters and calls createGroup method
     *
     * @param name
     * @param desc description
     * @param creator the user who is creating the group
     * @param parent the parent id if this group is a child of another
     * @return created group
     * @see IdentityServiceImpl#createGroup(com.dubic.module.idm.models.Group)
     * @since idm 1.0.0
     */
    @Override
    public Group createGroup(String name, String desc, User creator, Long parent) throws PersistenceException {
        Group g = new Group(name, desc, creator, parent);
        return createGroup(g);
    }

    /**creates,activates and returns an encrypted activation json token based on UserActivation class using user id,email,current date
     *
     * @param user user details to use for activation token
     * @return the activation token
     * @throws NullPointerException if user id is null
     * @see UserActivation
     */
    @Override
    public Token createActivationToken(User user) {
        log.info("createActivationToken - " + user.getId());
        if(user.getId() == null){
            throw new NullPointerException("user id cannot be null");
        }
        UserActivation userActivation = new UserActivation(user.getId(),user.getEmail(), new Date().getTime());
        String uajson = new Gson().toJson(userActivation);
        Token token = new Token();
        token.setActive(true);
        token.setToken(IdmCrypt.encodeMD5(uajson,null));
        token.setType(Token.USER_ACTIVATION_TOKEN);
        token.setUser(user);
        token.setExpiryDt(IdmUtils.getActivationTokenExpiryDt());
        return token;
    }

    /**retrieve a user from token table:
     * <br><ul><li>activation params - email and user id</li><li>if activation link has expired</li>
     *
     * @param tokenStr
     * @return 
     * @throws NullPointerException if user was not found with activation params
     * @throws LinkExpiredException if link has expired
     * @throws com.dubic.scribbleit.idm.spi.InvalidTokenException
     */
    @Override
    public User activateUser(String tokenStr) throws LinkExpiredException, PersistenceException, InvalidTokenException{
        log.info(String.format("activateUser(%s)", tokenStr));
        Token token = getToken(tokenStr);
        //continue and activate user
        User user = token.getUser();
        user.setActivated(true);
        user.setModifiedDate(new Date());
        useToken(token);
        db.merge(user,token);
        log.info(String.format("user activated [%s]", user.getScreenName()));
        return user;
    }

    @Override
    public User getUserLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = null;
        try {
            principal = auth.getPrincipal();
        } catch (NullPointerException e) {
            return null;
        }
        if(principal == null){
            return null;
        }
        String email = (String) principal;
        return findUserByEmail(email);
    }

    /**adds a role to the list of roles a user has
     *
     * @param roleId
     * @param userId
     * @throws EntityNotFoundException if role or user not found
     * @since idm 1.0.0
     */
    @Override
    public void assignRole(Long roleId, Long userId) throws EntityNotFoundException,PersistenceException{
        log.debug("assignRole : find role : role id - "+roleId);
        Role role = db.find(Role.class, roleId);
        log.debug("assignRole : find user : user id - "+userId);
        User user = db.find(User.class, userId);
        user.getRoles().add(role);
        db.merge(user);
        log.debug(user.getEmail()+" : role assigned to user");
    }

    /**adds a group to the list of a users groups
     *
     * @param userId
     * @param grpId
     * @throws EntityNotFoundException
     */
    @Override
    public void addUserToGroup(Long userId, Long grpId) throws EntityNotFoundException,PersistenceException{
       log.debug("addUserToGroup : find group : group id - "+grpId);
        Group group = db.find(Group.class, grpId);
        log.debug("assignRole : find user : user id - "+userId);
        User user = db.find(User.class, userId);
        user.getGroups().add(group);
        db.merge(user);
        log.debug(user.getEmail()+" : group added to user");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException(username+" not found");
        }
        return user;
    }

//    private void saveToken(Token token) throws PersistenceException{
//        try {
//            IdmUtils.validate(token, Token.class);
//            db.persist(token);
//            log.info(String.format("token saved [%s] for %s", token.getToken(), token.getUser().getEmail()));
//        } catch (ConstraintViolationException cve) {
//            StringBuilder sb = new StringBuilder();
//            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
//                sb.append(violation.getMessage()).append(",");
//            }
////            log.error(sb.toString());
//            throw new PersistenceException(sb.toString());
//        }
//    }

    private Token getActiveToken(String tokenStr) throws PersistenceException{
        List<Token> resultList = db.createQuery("SELECT t FROM Token t WHERE t.token = :token AND t.active = TRUE", Token.class)
                .setParameter("token", tokenStr).getResultList();
        return IdmUtils.getFirstOrNull(resultList);
    }

    private void useToken(Token token) {
        token.setActive(false);
        token.setUsedDt(new Date());
    }

    /**get token by token string and performs the following checks
     * <ul><li>if token exists</li>
     * <li>if token has not expired</li></ul>
     *
     * @param tokenStr
     * @return token or throws exception
     */
    public Token getToken(String tokenStr) {
        //get user from token
        Token token = getActiveToken(tokenStr);
        if (token == null) {
            throw new InvalidTokenException(String.format("token [%s] not found", tokenStr));
        }
        //if token has expired, deactivate and update
        if(new Date().after(token.getExpiryDt())){
            token.setActive(false);
            JAXB.marshal(token, System.out);
            db.merge(token);
            throw new  LinkExpiredException("Activation link has expired");
        }
        return token;
    }

    @Override
    public User deactivateUser(String ua) throws LinkExpiredException, PersistenceException, InvalidTokenException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
