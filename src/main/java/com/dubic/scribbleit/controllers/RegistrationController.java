/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import com.dubic.scribbleit.application.GsonMessageConverter;
import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.dto.FBMe;
import com.dubic.scribbleit.dto.GooggleUser;
import com.dubic.scribbleit.dto.UserActivation;
import com.dubic.scribbleit.dto.UserData;
import com.dubic.scribbleit.email.MailServiceImpl;
import com.dubic.scribbleit.email.SimpleMailEvent;
import com.dubic.scribbleit.idm.auth.CustomAuthenticationProvider;
import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.idm.spi.IdentityServiceImpl;
import com.dubic.scribbleit.models.Profile;
import com.dubic.scribbleit.utils.IdmCrypt;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.apache.log4j.Logger;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author dubem
 */
@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private IdentityServiceImpl idmService;
    @Autowired
    private MailServiceImpl mailService;
    @Autowired
    private Database db;
    @Value("${fb.api}")
    private String fburl;
    @Value("${gg.api}")
    private String gapiurl;
    @Value("${logo}")
    private String logo;
    @Autowired
    private BasicTextEncryptor basicEncryptor;

    @Autowired
    private CustomAuthenticationProvider authService;
    @Autowired
    private GsonMessageConverter gsonMessageConverter;
    private List<HttpMessageConverter<?>> converters;

    @PostConstruct
    public void created() {
        this.converters = new ArrayList<HttpMessageConverter<?>>();
        this.converters.add(gsonMessageConverter);
    }

    @RequestMapping("/load")
    public String loadPage(@RequestParam("p") String page) {
        return page;
    }

    @RequestMapping("/signup")
    public @ResponseBody
    JsonObject signup(@RequestBody UserData userData) {
        JsonObject resp = new JsonObject();
        try {
            idmService.userRegistration(userData);
            resp.addProperty("code", 0);
            resp.addProperty("msg", "your account has been created. An email will be sent to you shortly to activate your account");
        } catch (ConstraintViolationException cve) {
            for (ConstraintViolation<?> constraintViolation : cve.getConstraintViolations()) {
                resp.addProperty("code", 500);
                resp.addProperty("msg", constraintViolation.getMessage());
                log.error(constraintViolation.getMessage());
                break;
            }
            log.error(cve.getMessage());

        } catch (EntityExistsException e) {
            log.warn(e.getMessage());
            resp.addProperty("code", 403);
            resp.addProperty("msg", e.getMessage());
        } catch (Exception e) {
            log.fatal(e.getMessage(), e);
            resp.addProperty("code", 500);
            resp.addProperty("msg", "An error occurred");
        }
        return resp;
    }

    @RequestMapping("/resend")
    public @ResponseBody
    JsonObject resendActivation(@RequestParam("email") String email) {
        JsonObject resp = new JsonObject();
        try {
            User user = idmService.findUserByEmail(email);
            if (user == null) {
                log.warn(String.format("/resend : findUserByEmail(%s) = null", email));
                resp.addProperty("code", 404);
                resp.addProperty("msg", "Error. Account not found");
                return resp;
            }
            idmService.sendActivationMail(user);
            resp.addProperty("code", 0);
            resp.addProperty("msg", "An email will be sent to you shortly to activate your account");
        } catch (Exception e) {
            log.fatal(e.getMessage(), e);
            resp.addProperty("code", 500);
            resp.addProperty("msg", "An error occurred");
        }
        return resp;
    }

    @RequestMapping(value = "/activate")
    @ResponseBody
    public JsonObject activate(@RequestParam("t") String token) {
        JsonObject resp = new JsonObject();
        try {
            if (StringUtils.isEmpty(token)) {
                resp.addProperty("msg", "The Link you clicked does not exist");
                log.warn("Empty Token submmitted");
                return resp;
            }
            //decode
            log.debug(token);

            UserActivation a = new Gson().fromJson(basicEncryptor.decrypt(token), UserActivation.class);
            log.info(a);
            //check exipire
            if (a.expired()) {
                log.warn("ACTIVATION TOKEN EXPIRED FROM USER : " + a.getId());
                resp.addProperty("msg", "The Link you clicked has expired");
                return resp;
            }
            //activate
            idmService.activateUser(a.getId());
            resp.addProperty("msg", "Welcome to Scribbles. your account has been activated. you can proceed to login");
            return resp;

        } catch (EncryptionOperationNotPossibleException ex) {
            resp.addProperty("msg", "Service unavailable. please try again later");
            log.error("Token : " + token, ex);
            return resp;

        } catch (Exception ex) {
            resp.addProperty("msg", "Service unavailable. please try again later");
            log.fatal(ex.getMessage(), ex);
            return resp;
        }

    }

    @RequestMapping(value = "password/forgot")
    public @ResponseBody
    JsonObject forgotPassword(@RequestParam("email") String email, HttpSession session) {
        log.info(String.format("forgotPassword(%s)", email));
        JsonObject resp = new JsonObject();
//find if user is in system
        User user = idmService.findUserByEmail(email);
        if (user == null) {
            log.warn(String.format("/password/forgot : findUserByEmail(%s) = null", email));
            resp.addProperty("code", 404);
            resp.addProperty("msg", String.format("%s does not exist", email));
            return resp;
        }
//generate pin and store user in session
        String pin = IdmCrypt.generateTimeToken();
        log.debug(pin);
        session.setAttribute("password-reset-email", email);
        session.setAttribute("password-reset-pin", pin);

        SimpleMailEvent mail = new SimpleMailEvent(user.getEmail());
        mail.setSubject("Password Reset");
        Map model = new HashMap();
        model.put("username", user.getScreenName());
        model.put("passcode", pin);
        model.put("logo", logo);
        mailService.sendMail(mail, "password-reset.vm", model);
        log.info(String.format("Initiated forgot password(%s)", email));
        resp.addProperty("code", 0);
        resp.addProperty("msg", String.format("Enter passcode sent to your email [%s]", email));
        return resp;
    }

    @RequestMapping(value = "password/reset")
    public @ResponseBody
    JsonObject resetPassword(@RequestBody UserData userData, HttpSession session) {
        JsonObject resp = new JsonObject();
//find if pin is in session
        try {
            String email = (String) session.getAttribute("password-reset-email");
            String pin = (String) session.getAttribute("password-reset-pin");
            if (StringUtils.isEmpty(email) || StringUtils.isEmpty(pin)) {
                log.warn(String.format("/password/reset : session.getAttribute(%s,%s) = null", email, pin));
                resp.addProperty("code", 404);
                resp.addProperty("msg", "the passcode you entered has expired");
                return resp;
            }
            if (!pin.equals(userData.getPasscode())) {
                log.warn(String.format("/password/reset : pin.equals(%s) = false", userData.getPasscode()));
                resp.addProperty("code", 405);
                resp.addProperty("msg", "wrong passcode");
                return resp;
            }
            User user = idmService.findUserByEmail(email);
//generate pin and store user in session
            user.setPassword(userData.getPassword());
            idmService.resetPassword(user);
            log.info(String.format("Password reset successful(%s)", email));
            resp.addProperty("code", 0);
            resp.addProperty("msg", "your password has been changed");
        } catch (Exception e) {
            resp.addProperty("code", 500);
            resp.addProperty("msg", "Unexpected error occurred");
        }
        return resp;
    }

    @RequestMapping(value = "facebook/token")
    public @ResponseBody
    JsonObject facebookLogin(@RequestParam("t") String token, HttpSession session) {
        JsonObject resp = new JsonObject();
        try {
            session.setAttribute("fbtoken", token);

            RestTemplate rt = new RestTemplate(this.converters);
            UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(fburl);
            url.path("/me").queryParam("access_token", token);
            String u = url.build().toString();
            FBMe me = rt.getForObject(u, FBMe.class);
            
            if (me.getError() != null) {
                log.warn(String.format("Facebook token validation err : %s", me.getError().getMessage()));
                resp.addProperty("code", me.getError().getCode());
                return resp;
            }

            //check if user is in system
            User user = idmService.getFacebookUser(me.getId(), me.getEmail());
            if (user == null) {
                //account not found in system. prompt user to supply username
                log.info(String.format("Account by Facebook[%s] does not exist", me.getId()));
                session.setAttribute("SocialAccount", me);
                if (StringUtils.isEmpty(me.getEmail())) {
                    resp.addProperty("email", me.getId() + "f@pleasechange");
                    resp.addProperty("hasEmail", Boolean.FALSE);
                } else {
                    resp.addProperty("email", me.getEmail());
                }
                resp.addProperty("name", me.getName());
                resp.addProperty("account_type", "Facebook");
                resp.addProperty("code", 200);
                return resp;
            }
            //account is in system
            if (!StringUtils.isEmpty(user.getFacebookId())) {
                //account is a facebook account. authenticate
                log.info(String.format("Account [%s] exists and is linked to facebook account [%s]", user.getEmail(), me.getId()));
                authService.socialAuthentication(user, "Facebook");
                resp.addProperty("code", 201);
                return resp;
//            
            }
            if (user.getEmail().equalsIgnoreCase(me.getEmail())) {
                session.setAttribute("SocialAccount", me);
                log.info(String.format("Account [%s] exists but is NOT linked to facebook account [%s]", user.getEmail(), me.getId()));
                resp.addProperty("code", 202);
                resp.addProperty("account_type", "Facebook");
                resp.addProperty("email", me.getEmail());
                return resp;
            }

        } catch (Exception e) {
            resp.addProperty("code", 500);
            log.error(e.getMessage(), e);
        }
        return resp;
    }

    @RequestMapping(value = "google/token")
    public @ResponseBody
    JsonObject googleLogin(@RequestParam("t") String token, HttpSession session) {
        JsonObject resp = new JsonObject();

        try {
            session.setAttribute("ggtoken", token);
            RestTemplate rt = new RestTemplate(this.converters);
            UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(gapiurl);
            url.path("/userinfo").queryParam("access_token", token);
            String u = url.build().toString();
            GooggleUser me = rt.getForObject(u, GooggleUser.class);
            session.setAttribute("SocialAccount", me);
            //check if user is in system
            User user = idmService.getGoogleUser(me.getSub(), me.getEmail());
            if (user == null) {
                //account not found in system. prompt user to supply username
                log.info(String.format("Account by Google [%s] does not exist", me.getSub()));

                if (StringUtils.isEmpty(me.getEmail())) {
                    resp.addProperty("email", me.getSub() + "g@pleasechange");
                    resp.addProperty("hasEmail", Boolean.FALSE);
                } else {
                    resp.addProperty("email", me.getEmail());
                }
                resp.addProperty("name", me.getName());
                resp.addProperty("pix", me.getPicture());
                resp.addProperty("account_type", "Google");
                resp.addProperty("code", 200);
                return resp;
            }
            //account is in system
            if (!StringUtils.isEmpty(user.getGoogleId())) {
                //account is a Google account. authenticate
                log.info(String.format("Account [%s] exists and is linked to Google account [%s]", user.getEmail(), me.getSub()));
                authService.socialAuthentication(user, "Google");
                resp.addProperty("code", 201);
                return resp;

            }
            if (user.getEmail().equalsIgnoreCase(me.getEmail())) {
                log.info(String.format("Account [%s] exists but is NOT linked to Google account [%s]", user.getEmail(), me.getSub()));
                resp.addProperty("code", 202);
                resp.addProperty("account_type", "Google");
                resp.addProperty("email", me.getEmail());
                resp.addProperty("pix", me.getPicture());
                return resp;
            }
        } catch (Exception e) {
            resp.addProperty("code", 500);
            log.error(e.getMessage(), e);
        }
        return resp;
        //last facebook id 888701074502643
    }

    @RequestMapping(value = "social/create")
    public @ResponseBody
    JsonObject newSocialAccount(@RequestParam("username") String username, @RequestParam("type") String type, HttpSession session) {
        log.info(String.format("creating new User from %s account", type));
        JsonObject resp = new JsonObject();
        Object attribute = session.getAttribute("SocialAccount");
        if (attribute == null) {
            log.warn(String.format("/social/create : session.getAttribute(SocialAccount) null"));
            resp.addProperty("code", 404);
            resp.addProperty("msg", "Unexpected error occurred. Please refresh and try again");
            return resp;
        }
        User user = new User();
        user.setScreenName(username);
        user.setActivated(true);
        user.setProfile(new Profile());
        if ("Facebook".equalsIgnoreCase(type)) {
            try {
                FBMe me = (FBMe) attribute;

                user.setEmail(me.getEmail());
                if (StringUtils.isEmpty(me.getEmail())) {
                    user.setEmail(me.getId() + "f@pleasechange");
                }
                user.setFacebookId(me.getId());
                user.setFirstname(me.getFirst_name());
                user.setLastname(me.getLast_name());
                //create User account in system
                db.persist(user);
                log.info(String.format("user [%s] created through facebook", user.getScreenName()));
                //update user model with picture
                user.setPicture(idmService.addSocialPix(user, (String) session.getAttribute("fbtoken"), "FB", null));
                user.setModifiedDate(new Date());
                db.merge(user);
                log.info(String.format("user [%s] pix added from facebook", user.getScreenName()));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                resp.addProperty("code", 500);
                resp.addProperty("msg", "Unexpected error occurred");
                return resp;
            }

        } else {
            try {
                GooggleUser me = (GooggleUser) attribute;
                user.setEmail(me.getEmail());
                if (StringUtils.isEmpty(me.getEmail())) {
                    user.setEmail(me.getSub() + "g@pleasechange");
                }
                user.setGoogleId(me.getSub());
                user.setFirstname(me.getGiven_name());
                user.setLastname(me.getFamily_name());
                //create User account in system
                db.persist(user);
                log.info(String.format("user [%s] created through Google", user.getScreenName()));
                //update user model with picture
                user.setPicture(idmService.addSocialPix(user, (String) session.getAttribute("ggtoken"), "GG", me.getPicture()));
                user.setModifiedDate(new Date());
                db.merge(user);
                log.info(String.format("user [%s] pix added from google", user.getScreenName()));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                resp.addProperty("code", 500);
                resp.addProperty("msg", "Unexpected error occurred");
                return resp;
            }

        }

        //Login user
        authService.socialAuthentication(user, type);
        resp.addProperty("code", 200);
        return resp;
    }

    @RequestMapping(value = "social/link")
    public @ResponseBody
    JsonObject linkSocialAccount(@RequestParam("password") String pword, @RequestParam("type") String type, HttpSession session) {
        log.info(String.format("linking %s account to system", type));
        JsonObject resp = new JsonObject();
        Object attribute = session.getAttribute("SocialAccount");
        if (attribute == null) {
            log.warn(String.format("/social/link : session.getAttribute(SocialAccount) null"));
            resp.addProperty("code", 404);
            resp.addProperty("msg", "Unexpected error occurred. Please refresh and try again");
            return resp;
        }

        if ("Facebook".equalsIgnoreCase(type)) {
            try {
                FBMe me = (FBMe) attribute;
                User user = idmService.findUserByEmailandPasword(me.getEmail(), idmService.encodePassword(me.getEmail().toLowerCase(),pword));
                if (user == null) {
                    log.warn(String.format("/social/link FB : idmService.findUserByEmailandPasword(%s) null", me.getEmail()));
                    resp.addProperty("code", 404);
                    resp.addProperty("msg", "Wrong password entered");
                    return resp;
                }
                user.setFacebookId(me.getId());
                user.setModifiedDate(new Date());
                user.setFirstname(me.getFirst_name());
                user.setLastname(me.getLast_name());
                db.merge(user);
                log.info(String.format("user [%s] linked to facebook account", user.getScreenName()));
                //Login user
                authService.socialAuthentication(user, type);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                resp.addProperty("code", 500);
                resp.addProperty("msg", "Unexpected error occurred");
                return resp;
            }

        } else {
            try {
                GooggleUser me = (GooggleUser) attribute;
                User user = idmService.findUserByEmailandPasword(me.getEmail(), idmService.encodePassword(me.getEmail().toLowerCase(),pword));
                if (user == null) {
                    log.warn(String.format("/social/link Google : idmService.findUserByEmailandPasword(%s) null", me.getEmail()));
                    resp.addProperty("code", 404);
                    resp.addProperty("msg", "Wrong password entered");
                    return resp;
                }
                user.setGoogleId(me.getSub());
                user.setModifiedDate(new Date());
                user.setFirstname(me.getGiven_name());
                user.setLastname(me.getFamily_name());
                db.merge(user);
                log.info(String.format("user [%s] linked to google account", user.getScreenName()));
                //Login user
                authService.socialAuthentication(user, type);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                resp.addProperty("code", 500);
                resp.addProperty("msg", "I/O error occurred");
                return resp;
            }

        }

        resp.addProperty("code", 200);
        return resp;
    }
}
