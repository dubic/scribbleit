/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import com.dubic.scribbleit.db.Database;
import com.dubic.scribbleit.dto.FBMe;
import com.dubic.scribbleit.dto.GooggleUser;
import com.dubic.scribbleit.dto.UserData;
import com.dubic.scribbleit.email.MailServiceImpl;
import com.dubic.scribbleit.email.SimpleMailEvent;
import com.dubic.scribbleit.idm.auth.CustomAuthenticationProvider;
import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.idm.spi.IdentityService;
import com.dubic.scribbleit.idm.spi.InvalidTokenException;
import com.dubic.scribbleit.idm.spi.LinkExpiredException;
import com.dubic.scribbleit.models.Profile;
import com.dubic.scribbleit.utils.IdmCrypt;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
    private IdentityService idmService;
    @Autowired
    private MailServiceImpl mailService;
    @Autowired
    private Database db;
    @Value("${fb.api}")
    private String fburl;
    @Value("${gg.api}")
    private String gapiurl;
    
    @Autowired
    private CustomAuthenticationProvider authService;

    @RequestMapping("/load")
    public String loadPage(@RequestParam("p") String page) {
        return page;
    }

    @RequestMapping("/signup")
    public @ResponseBody
    JsonObject signup(@RequestBody UserData userData) {
        log.debug("SIGNUP : " + new Gson().toJson(userData));
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

    @RequestMapping(value = "/activate/{token}")
    public String activate(Model m, @PathVariable String token) {
        try {
            idmService.activateUser(token);
            //redirect to success activation
            m.addAttribute("error", false);
            m.addAttribute("linkExpired", Boolean.FALSE);
            m.addAttribute("linkError", Boolean.FALSE);
        } catch (InvalidTokenException ex) {
            m.addAttribute("error", true);
            m.addAttribute("linkExpired", Boolean.FALSE);
            m.addAttribute("linkError", Boolean.TRUE);
            log.error(ex.getMessage());
        } catch (LinkExpiredException ex) {
            m.addAttribute("error", true);
            m.addAttribute("linkExpired", Boolean.TRUE);
            m.addAttribute("linkError", Boolean.FALSE);
            log.error(ex.getMessage());
        } catch (Exception ex) {
            m.addAttribute("error", true);
            m.addAttribute("linkExpired", Boolean.FALSE);
            m.addAttribute("linkError", Boolean.FALSE);
            m.addAttribute("Service error, Try some time later", Boolean.TRUE);
            log.error(ex.getMessage());
        }
        return "activation-outcome";
    }

    @RequestMapping(value = "password/forgot")
    public @ResponseBody
    JsonObject forgotPassword(@RequestParam("email") String email, HttpSession session) {
        JsonObject resp = new JsonObject();
//find if user is in system
        User user = idmService.findUserByEmail(email);
        if (user == null) {
            resp.addProperty("code", 404);
            resp.addProperty("msg", String.format("%s does not exist", email));
            return resp;
        }
//generate pin and store user in session
        String pin = IdmCrypt.generateTimeToken();
        log.info(pin);
        session.setAttribute("password-reset-email", email);
        session.setAttribute("password-reset-pin", pin);

        SimpleMailEvent mail = new SimpleMailEvent(user.getEmail());
        Map model = new HashMap();
        model.put("token", pin);
        mailService.sendMail(mail, "password.vm", model);
        resp.addProperty("code", 0);
        resp.addProperty("msg", String.format("Enter passcode sent to your email [%s]", email));
        return resp;
    }

    @RequestMapping(value = "password/reset")
    public @ResponseBody
    JsonObject resetPassword(@RequestBody UserData userData, HttpSession session) {
        JsonObject resp = new JsonObject();
//find if pin is in session
        String email = (String) session.getAttribute("password-reset-email");
        String pin = (String) session.getAttribute("password-reset-pin");
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(pin)) {
            resp.addProperty("code", 404);
            resp.addProperty("msg", "the passcode you entered has expired");
            return resp;
        }
        if (!pin.equals(userData.getPasscode())) {
            resp.addProperty("code", 405);
            resp.addProperty("msg", "wrong passcode");
            return resp;
        }
        User user = idmService.findUserByEmail(email);
//generate pin and store user in session
        user.setPassword(userData.getPassword());
        idmService.resetPassword(user);

        resp.addProperty("code", 0);
        resp.addProperty("msg", "your password has been changed");
        return resp;
    }

    @RequestMapping(value = "facebook/token")
    public @ResponseBody
    JsonObject facebookLogin(@RequestParam("t") String token, HttpSession session) {
        session.setAttribute("fbtoken", token);
        JsonObject resp = new JsonObject();
        RestTemplate rt = new RestTemplate();
        UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(fburl);
        url.path("/me").queryParam("access_token", token);
        String u = url.build().toString();
        FBMe me = rt.getForObject(u, FBMe.class);

        //check if user is in system
        User user = idmService.getFacebookUser(me.getId(), me.getEmail());
        if (user == null) {
            //account not found in system. prompt user to supply username
            log.info(String.format("Account does not exist %s", me.getId()));
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
            log.info(String.format("Account [%s] exists and is linked to facebook account ", user.getEmail()));
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getFacebookId(), user.getRoles());
            SecurityContextHolder.getContext().setAuthentication(auth);
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
        return resp;
        //last facebook id 888701074502643
    }

    @RequestMapping(value = "google/token")
    public @ResponseBody
    JsonObject googleLogin(@RequestParam("t") String token, HttpSession session) {
        session.setAttribute("ggtoken", token);

        JsonObject resp = new JsonObject();
        RestTemplate rt = new RestTemplate();
        UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(gapiurl);
        url.path("/userinfo").queryParam("access_token", token);
        String u = url.build().toString();
        GooggleUser me = rt.getForObject(u, GooggleUser.class);
        session.setAttribute("SocialAccount", me);
        //check if user is in system
        User user = idmService.getGoogleUser(me.getSub(), me.getEmail());
        if (user == null) {
            //account not found in system. prompt user to supply username
            log.info(String.format("Account does not exist %s", me.getSub()));

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
            log.info(String.format("Account [%s] exists and is linked to Google account ", user.getEmail()));
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getGoogleId(), user.getRoles());
            SecurityContextHolder.getContext().setAuthentication(auth);
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
                //update user model with picture
                user.setPicture(idmService.addSocialPix(user, (String) session.getAttribute("fbtoken"), "FB", null));
                user.setModifiedDate(new Date());
                db.merge(user);
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
                resp.addProperty("code", 500);
                resp.addProperty("msg", "I/O error occurred");
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
                //update user model with picture
                user.setPicture(idmService.addSocialPix(user, (String) session.getAttribute("ggtoken"), "GG", me.getPicture()));
                user.setModifiedDate(new Date());
                db.merge(user);
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
                resp.addProperty("code", 500);
                resp.addProperty("msg", "I/O error occurred");
                return resp;
            }

        }

        //Login user
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), "no password", user.getRoles());
        SecurityContextHolder.getContext().setAuthentication(auth);
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
            resp.addProperty("code", 404);
            resp.addProperty("msg", "Unexpected error occurred. Please refresh and try again");
            return resp;
        }

        if ("Facebook".equalsIgnoreCase(type)) {
            try {
                FBMe me = (FBMe) attribute;
                User user = idmService.findUserByEmailandPasword(me.getEmail(), IdmCrypt.encodeMD5(pword, me.getEmail().toLowerCase()));
                if (user == null) {
                    resp.addProperty("code", 404);
                    resp.addProperty("msg", "Wrong password entered");
                    return resp;
                }
                user.setFacebookId(me.getId());
                user.setModifiedDate(new Date());
                db.merge(user);
                //Login user
                authService.socialAuthentication(user);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                resp.addProperty("code", 500);
                resp.addProperty("msg", "Unexpected error occurred");
                return resp;
            }

        } else {
            try {
                GooggleUser me = (GooggleUser) attribute;
                User user = idmService.findUserByEmailandPasword(me.getEmail(), idmService.encodePassword(pword, me.getEmail().toLowerCase()));
                if (user == null) {
                    resp.addProperty("code", 404);
                    resp.addProperty("msg", "Wrong password entered");
                    return resp;
                }
                user.setGoogleId(me.getSub());
                user.setModifiedDate(new Date());
                db.merge(user);
                //Login user
                authService.socialAuthentication(user);
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
