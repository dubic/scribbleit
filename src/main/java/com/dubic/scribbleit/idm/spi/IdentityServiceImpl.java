/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.idm.spi;

import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.dto.FBMe;
import com.dubic.scribbleit.dto.UserData;
import com.dubic.scribbleit.email.MailServiceImpl;
import com.dubic.scribbleit.models.Role;
import com.dubic.scribbleit.models.Token;
import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.models.Profile;
import com.dubic.scribbleit.utils.IdmCrypt;
import com.dubic.scribbleit.utils.IdmUtils;
import com.dubic.scribbleit.utils.InvalidException;
import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.xml.bind.JAXB;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * performs all identity management ops.
 * <br>config properties
 * <ul><li>actExpirationMins</li>
 *
 * @author dubem
 * @since idm 1.0.0
 */
@Named("identityService")
public class IdentityServiceImpl implements IdentityService, UserDetailsService {

    private final Logger log = Logger.getLogger(IdentityServiceImpl.class);

    @Autowired
    private Database db;
    @Autowired
    private MailServiceImpl mailService;

    @Value("${default.profile.picture}")
    private String avatar;
//    @Value("${activation.url}")
//    private String activationURL;
    @Value("${picture.location}")
    private String picturePath;
    @Value("${fb.api}")
    private String fburl;
    @Value("${gg.api}")
    private String gapiurl;

    @PostConstruct
    public void inited() {
        log.info(String.format("%s CREATED", getClass().getSimpleName()));
        log.info(String.format("default.profile.picture = %s", avatar));
        log.info(String.format("picture.location = %s", picturePath));
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
    public User userRegistration(UserData userData) throws PersistenceException, ConstraintViolationException {
        if (getUniqueEmail(userData.getEmail().toLowerCase().trim()) != null) {
            throw new EntityExistsException(String.format("User with email %s already exists", userData.getEmail()));
        }
        User user = new User(userData);
        //BUILD PROFILE
        Profile profile = new Profile();
        user.setProfile(profile);
        user.setActivated(false);
        IdmUtils.validate(user, User.class);
        user.setPicture(avatar);
        user.setPassword(encodePassword(user));

        //SAVE USER,USER ACTIVATION TOKEN
        //CREATE AND SAVE VALIDATE ACTIVATION TOKEN
        Token t = new Token(user, IdmCrypt.encodeMD5(new Gson().toJson(user), "token"));
        t.setExpiryDt(IdmUtils.getActivationTokenExpiryDt());
        t.setType(Token.TYPE_ACTIVATION);

        db.persist(user, t);
        //SEND MAIL
//        SimpleMailEvent mail = new SimpleMailEvent(user.getEmail());
//        Map model = new HashMap();
//        model.put("name", user.getScreenName());
//        model.put("url", activationURL + t.getToken());
//        mailService.sendMail(mail, "reg.vm", model);
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
    public void getChangePwdToken(String email) throws PersistenceException {
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
        token.setType(Token.TYPE_PASSWORD_RESET);
        //SAVE TOKEN
        db.persist(token);

        //send mail
    }

    @Override
    public void resetPassword(String tokenStr, String password) throws InvalidTokenException, LinkExpiredException {
        log.info("resetPassword(****)");
        //GET TOKEN FROM DB
        Token token = getActiveToken(tokenStr);
        if (token == null) {
            throw new InvalidTokenException("the token has expired");
        }
        if (new Date().after(token.getExpiryDt())) {
            throw new LinkExpiredException("Token has expired");
        }
        //UPDATE PASSWORD
        User user = token.getUser();
        user.setPassword(IdmCrypt.encodeMD5(password, user.getUsername()));
        //FLAG TOKEN AS USED
        useToken(token);
        db.merge(user, token);
        log.info(String.format("[%s] password changed", user.getEmail()));
    }

    @Override
    public void changePassword(String current, String newpword) throws InvalidException {
        User user = getUserLoggedIn();
        if (!StringUtils.isEmpty(user.getPassword())) {
            String current_enc_password = encodePassword(user.getEmail(), current);
//        String new_enc_password = Crypto.encodeMD5(reg.getConfirmPassword().trim(), current.getMdn());
            if (!user.getPassword().equals(current_enc_password)) {
                throw new InvalidException("Your current password is wrong");
            }
        }

        user.setPassword(encodePassword(user.getEmail(), newpword));
        updateUser(user);
        log.info(String.format("[%s] password changed", user.getEmail()));
    }

    /**
     * used in authentication.queries the db for user matching the email and
     * password.
     *
     * @param email
     * @param pwd the encrypted password
     * @return single user matching the params
     * @since idm 1.0.0
     */
    @Override
    public User findUserByEmailandPasword(String email, String pwd) {
        log.debug(String.format("find User By Email and Pasword [%s] [****]", email));
        List<User> ulist = db.namedQuery("user.find.mail.password", User.class).setParameter("email", email).setParameter("pwd", pwd).getResultList();
        return IdmUtils.getFirstOrNull(ulist);
    }

    @Override
    public User findUserByEmail(String email) {
        log.debug(String.format("find User By Email [%s]", email));
        List<User> ulist = db.namedQuery("user.find.mail", User.class).setParameter("email", email).getResultList();
        return IdmUtils.getFirstOrNull(ulist);
    }

    @Override
    public User findUserByScreenName(String screenName) {
        log.debug(String.format("find User By Screen name [%s]", screenName));
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
     * retrieve a user from token table:
     * <br><ul><li>activation params - email and user id</li><li>if activation
     * link has expired</li>
     *
     * @param tokenStr
     * @return
     * @throws NullPointerException if user was not found with activation params
     * @throws LinkExpiredException if link has expired
     * @throws com.dubic.scribbleit.idm.spi.InvalidTokenException
     */
    @Override
    public User activateUser(String tokenStr) throws LinkExpiredException, PersistenceException, InvalidTokenException {
        log.info(String.format("activateUser(%s)", tokenStr));
        Token token = getToken(tokenStr);
        //continue and activate user
        User user = token.getUser();
        user.setActivated(true);
        user.setModifiedDate(new Date());
        useToken(token);
        db.merge(user, token);
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
        if (principal == null) {
            return null;
        }
        String email = (String) principal;
        return findUserByEmail(email);
    }

    /**
     * adds a role to the list of roles a user has
     *
     * @param roleId
     * @param userId
     * @throws EntityNotFoundException if role or user not found
     * @since idm 1.0.0
     */
    @Override
    public void assignRole(Long roleId, Long userId) throws EntityNotFoundException, PersistenceException {
        log.debug("assignRole : find role : role id - " + roleId);
        Role role = db.find(Role.class, roleId);
        log.debug("assignRole : find user : user id - " + userId);
        User user = db.find(User.class, userId);
        user.getRoles().add(role);
        db.merge(user);
        log.debug(user.getEmail() + " : role assigned to user");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return user;
    }

    private Token getActiveToken(String tokenStr) throws PersistenceException {
        List<Token> resultList = db.createQuery("SELECT t FROM Token t WHERE t.token = :token AND t.active = TRUE", Token.class)
                .setParameter("token", tokenStr).getResultList();
        return IdmUtils.getFirstOrNull(resultList);
    }

    private void useToken(Token token) {
        token.setActive(false);
        token.setUsedDt(new Date());
    }

    /**
     * get token by token string and performs the following checks
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
        if (new Date().after(token.getExpiryDt())) {
            token.setActive(false);
            JAXB.marshal(token, System.out);
            db.merge(token);
            throw new LinkExpiredException("Activation link has expired");
        }
        return token;
    }

    @Override
    public User deactivateUser(String ua) throws LinkExpiredException, PersistenceException, InvalidTokenException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String encodePassword(User user) {
        return IdmCrypt.encodeMD5(user.getPassword().trim(), user.getEmail().trim().toLowerCase());
    }

    @Override
    public String encodePassword(String email, String password) {
        return IdmCrypt.encodeMD5(password.trim(), email.trim().toLowerCase());
    }

    @Override
    public void resetPassword(User user) {
        user.setPassword(encodePassword(user));
        user.setActivated(true);
        user.setModifiedDate(new Date());
        db.merge(user);
    }

    @Override
    public String changePicture(InputStream inputStream) throws IOException, FileNotFoundException, URISyntaxException {
        //current user
        User user = getUserLoggedIn();
        //hold picture
        String currentPicture = user.getPicture();

        //delete picture 
        File f = new File(this.picturePath + currentPicture);
        if (currentPicture != null) {
            if (!f.isDirectory() && !currentPicture.contains(this.avatar)) {
                boolean deleted = f.delete();
                if (deleted) {
                    log.info("Profile picture deleted for : " + user.getId());
                } else {
                    log.warn("PROFILE PICTURE NOT DELETED : " + currentPicture);
                }
            }
        }
//        create MD5 hash of user id
        String newPicture = IdmCrypt.encodeMD5(String.valueOf(System.currentTimeMillis()), "pix") + "_" + user.getId();
        FileOutputStream fileOutputStream = new FileOutputStream(this.picturePath + newPicture);
        //resize image to reduce memory footprint
        BufferedImage image = ImageIO.read(inputStream);
        ImageIO.write(IdmUtils.resizeImage(380, 500, image), "jpg", fileOutputStream);
        IOUtils.closeQuietly(fileOutputStream);
        //update user model with picture
        user.setPicture(newPicture);
        user.setModifiedDate(new Date());
        db.merge(user);
        return user.getPicture();
    }

    @Override
    public void changeEmail(String newEmail, String password) throws InvalidException {
        //VALIDATE PASSWORD
        User user = getUserLoggedIn();
        if (!user.getPassword().equals(IdmCrypt.encodeMD5(password.trim(), user.getEmail().trim().toLowerCase()))) {
            throw new InvalidException("Incorrect password entered");
        }
        String oldEmail = user.getEmail();
        user.setEmail(newEmail);
        user.setPassword(password);
        user.setPassword(encodePassword(user));
        user.setActivated(true);
        updateUser(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), user.getRoles());
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.info(String.format("Email updated from [%s] to [%s]", oldEmail, newEmail));
    }

    @Override
    public User getFacebookUser(String id, String email) throws PersistenceException {
        TypedQuery<User> q;
        if (IdmUtils.isEmpty(email)) {
            q = db.createQuery("SELECT u FROM User u WHERE u.facebookId = :id", User.class).setParameter("id", id);
        } else {
            q = db.createQuery("SELECT u FROM User u WHERE u.facebookId = :id OR u.email = :email", User.class).setParameter("id", id).setParameter("email", email);
        }
        return IdmUtils.getFirstOrNull(q.getResultList());
    }

    @Override
    public User getGoogleUser(String id, String email) throws PersistenceException {
        TypedQuery<User> q;
        if (IdmUtils.isEmpty(email)) {
            q = db.createQuery("SELECT u FROM User u WHERE u.googleId = :id", User.class).setParameter("id", id);
        } else {
            q = db.createQuery("SELECT u FROM User u WHERE u.googleId = :id OR u.email = :email", User.class).setParameter("id", id).setParameter("email", email);
        }
        return IdmUtils.getFirstOrNull(q.getResultList());
    }

    /**downloads a user profile picture from FB or Google and creates picture string.
     * picture is saved in default picture location. current picture is first deleted to ensure user has only one picture in folder
     *
     * @param user
     * @param token
     * @param acctType
     * @param picurl
     * @return
     * @throws IOException
     */
    @Override
    public String addSocialPix(User user, String token, String acctType,String picurl) throws IOException{
        RestTemplate rt = new RestTemplate();
        byte[] data;
        if (acctType.equals("FB")) {
            UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(fburl);
            url.path("/me").path("/picture").queryParam("width", 180).queryParam("height", 200).queryParam("access_token", token);
            String u = url.build().toString();
            log.info(String.format("retreiving picture from : %s", u));
            ResponseEntity<byte[]> entity = rt.getForEntity(u, byte[].class);
            data = entity.getBody();
            
        } else {
            UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(picurl);
            String u = url.build().toString();
            log.info(String.format("retreiving picture from : %s", u));
            ResponseEntity<byte[]> entity = rt.getForEntity(u, byte[].class);
            data = entity.getBody();
        }
                //hold picture
                String currentPicture = user.getPicture();

                //delete picture 
                File f = new File(this.picturePath + currentPicture);
                if (currentPicture != null) {
                    if (!f.isDirectory() && !currentPicture.contains(this.avatar)) {
                        boolean deleted = f.delete();
                        if (deleted) {
                            log.info("Profile picture deleted for : " + user.getId());
                        } else {
                            log.warn("PROFILE PICTURE NOT DELETED : " + currentPicture);
                        }
                    }
                }
//        create MD5 hash of user id
                String newPicture = IdmCrypt.encodeMD5(String.valueOf(System.currentTimeMillis()), "pix") + "_" + user.getId();
                FileOutputStream fileOutputStream = new FileOutputStream(this.picturePath + newPicture);
                IOUtils.write(data, fileOutputStream);
                IOUtils.closeQuietly(fileOutputStream);
                return newPicture;
    }
}
