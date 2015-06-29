/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.idm.spi;

import com.dubic.scribbleit.dto.UserData;
import com.dubic.scribbleit.models.Role;
import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.utils.InvalidException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

/**
 *
 * @author dubem
 */
public interface IdentityService {

    public User userRegistration(UserData userData) throws PersistenceException, ConstraintViolationException;

    /**
     * queries the db for the existence of an email address
     *
     * @param email the email to query
     * @return the email address if exists or null
     * @throws PersistenceException if db error
     * @since idm 1.0.0
     */
    public String getUniqueEmail(String email) throws PersistenceException;

    /**
     * queries the db for the existence of a screen name
     *
     * @param screenName
     * @return the screen name if exists or null
     * @throws PersistenceException
     * @since idm 1.0.0
     */
    public String validateScreenName(String screenName) throws PersistenceException;

    public void updateUser(User user) throws PersistenceException;

    /**
     * the implementation should
     * <ul><li>GENERATE RANDOM TOKEN USING TIME MILLIS</li>
     * <li>CREATE AND SAVE TOKEN IN DB</li>
     * <li>SEND MAIL TO USER</li>
     *
     * @param email
     */
    public void getChangePwdToken(String email) throws PersistenceException;

    /**
     * resets the user password
     * <ul><li>GETS USER FROM TOKEN</li>
     * <li>UPDATE USER PASSWORD IN DB</li>
     * <li>UPDATE TOKEN IN DB</li>
     *
     * @param token
     * @param pwd
     * @throws com.dubic.scribbleit.idm.spi.LinkExpiredException if token has
     * expired
     */
    public void resetPassword(String token, String pwd) throws InvalidTokenException, LinkExpiredException;

    /**
     * change password initiated not from forgot password
     *
     * @param current
     * @param newpword
     * @throws InvalidException
     */
    public void changePassword(String current, String newpword) throws InvalidException;

    public User findUserByEmailandPasword(String email, String pwd);

    public User findUserByEmail(String email);

    public User findUserByScreenName(String screenName);

    public Role createRole(String name, String desc) throws PersistenceException;

    public User activateUser(String ua) throws LinkExpiredException, PersistenceException, InvalidTokenException;

    public User deactivateUser(String ua) throws LinkExpiredException, PersistenceException, InvalidTokenException;
    
    public String encodePassword(String email, String password);

    /**
     * get user currently signed in
     *
     * @return user in session or null
     */
    public User getUserLoggedIn();

    public void assignRole(Long roleId, Long userId) throws EntityNotFoundException, PersistenceException;

    public void resetPassword(User user);

    public String changePicture(InputStream inputStream) throws IOException, FileNotFoundException, URISyntaxException;

    public void changeEmail(String string, String password) throws InvalidException;

    public User getFacebookUser(String id, String email) throws PersistenceException;

    public User getGoogleUser(String id, String email) throws PersistenceException;

    public String addSocialPix(User user, String token,String acctType,String picurl) throws IOException;
}
