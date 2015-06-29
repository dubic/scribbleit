/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import com.dubic.scribbleit.email.SimpleMailEvent;
import com.dubic.scribbleit.idm.spi.IdentityService;
import com.dubic.scribbleit.models.User;
import com.dubic.scribbleit.posts.PostService;
import com.dubic.scribbleit.utils.IdmCrypt;
import com.dubic.scribbleit.utils.IdmUtils;
import com.dubic.scribbleit.utils.InvalidException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author dubem
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private IdentityService identityService;
    @Autowired
    private PostService postService;

    @RequestMapping("/details/{name}")
    public @ResponseBody
    JsonObject profileDetails(@PathVariable("name") String username) {
        JsonObject resp = new JsonObject();
        User user = identityService.findUserByScreenName(username);
        if (user == null) {
            resp.addProperty("exists", false);
            return resp;
        }
        if (user.equals(identityService.getUserLoggedIn())) {
            resp.addProperty("isMe", Boolean.TRUE);
        } else {
            resp.addProperty("isMe", Boolean.FALSE);
        }
        resp.addProperty("exists", true);
        resp.addProperty("screenName", user.getScreenName());
        resp.addProperty("picture", user.getPicture());
        resp.addProperty("name", user.getScreenName());
        resp.addProperty("firstname", user.getFirstname());
        resp.addProperty("lastname", user.getLastname());
        resp.addProperty("email", user.getEmail());
        resp.addProperty("date", user.getCreateDate().getTime());
        resp.addProperty("jokes", user.getProfile().getJokes());
        resp.addProperty("proverbs", user.getProfile().getProverbs());
        resp.addProperty("quotes", user.getProfile().getQuotes());
        resp.addProperty("hasPwd", !StringUtils.isEmpty(user.getPassword()));
        return resp;
    }

    @RequestMapping("/posts/{name}")
    public @ResponseBody
    JsonArray profileActivity(@PathVariable("name") String username) {
//        JsonObject resp = new JsonObject();
        return postService.getLatestsUserPosts(username, 0, 10);
    }

    @RequestMapping({"/update-account"})
    @ResponseBody
    public JsonObject updateAccount(@RequestBody JsonObject params) {
        JsonObject resp = new JsonObject();
        try {
            String firstname = params.get("firstname") == null ? null : params.get("firstname").getAsString();
            String lastname = params.get("lastname") == null ? null : params.get("lastname").getAsString();

            User user = identityService.getUserLoggedIn();
            user.setFirstname(firstname);
            user.setLastname(lastname);
            this.identityService.updateUser(user);

            resp.addProperty("code", 0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resp.addProperty("code", 500);
            resp.addProperty("msg", "unxepected server error occurred");
        }
        return resp;
    }

    @RequestMapping({"/change-password"})
    @ResponseBody
    public JsonObject changePassword(@RequestBody JsonObject params) {
        JsonObject resp = new JsonObject();
        try {
            String current = params.get("current") == null ? null : params.get("current").getAsString();
            String newpword = params.get("newpword") == null ? null : params.get("newpword").getAsString();

//            IdmUtils.validate(current).notEmpty("current passsword not supplied");
            IdmUtils.validate(newpword).notEmpty("new passsword not supplied");
            this.identityService.changePassword(current, newpword);

            resp.addProperty("code", 0);
        } catch (InvalidException e) {
            log.warn(e.getMessage());
            resp.addProperty("code", 500);
            resp.addProperty("msg", e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            resp.addProperty("code", 500);
            resp.addProperty("msg", "unxepected server error occurred");
        }
        return resp;
    }

    @RequestMapping(value = "/validate-email")
    public @ResponseBody
    JsonObject validateEmail(@RequestParam("p") String email, HttpSession session) {
        JsonObject resp = new JsonObject();

        try {
            IdmUtils.validate(email).notEmpty("email is required");
            String passcode = IdmCrypt.generateTimeToken();
            session.setAttribute(passcode, email.trim());
            //send email
            User user = identityService.getUserLoggedIn();
            SimpleMailEvent mail = new SimpleMailEvent(email);
            mail.setSubject("Email Reset");
            Map model = new HashMap();
//            model.put("logo", logo);
//            model.put("username", user.getScreenName());
//            model.put("passcode", passcode);
//            mailService.sendMail(mail, "email-reset.vm", model);
            resp.addProperty("code", 0);
            log.debug("TOKEN : " + passcode);
        } catch (InvalidException ex) {
            log.warn(ex.getMessage(), ex);
            resp.addProperty("code", 302);
            resp.addProperty("msg", ex.getMessage());
        } catch (Exception ex) {
            log.fatal(ex.getMessage(), ex);
            resp.addProperty("code", 500);
            resp.addProperty("msg", "unexpected server error");
        }
        return resp;
    }

    @RequestMapping(value = "/change-email")
    public @ResponseBody
    JsonObject updateEmail(@RequestBody JsonObject prms, HttpSession session) {
        JsonObject resp = new JsonObject();
        System.out.println(new Gson().toJson(prms));
        try {
            String password = prms.get("pword") == null ? null : prms.get("pword").getAsString();
            String passcode = prms.get("pcode") == null ? null : prms.get("pcode").getAsString();
            //VALIDATE PASSCODE
            Object newEmail = session.getAttribute(passcode);
            if (newEmail == null) {
                throw new InvalidException("your passcode has expired");
            }

            identityService.changeEmail((String) newEmail, password);
            resp.addProperty("code", 0);
        } catch (InvalidException ex) {
            log.warn(ex.getMessage());
            resp.addProperty("code", 500);
            resp.addProperty("msg", ex.getMessage());

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            resp.addProperty("code", 500);
            resp.addProperty("msg", "unxepected server error occurred");
        }
        return resp;
    }

}
