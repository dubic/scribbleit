/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import com.dubic.scribbleit.dto.UniqueValidation;
import com.dubic.scribbleit.dto.UserData;
import com.dubic.scribbleit.email.MailServiceImpl;
import com.dubic.scribbleit.idm.spi.IdentityService;
import com.dubic.scribbleit.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author dubem
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private final Logger log = Logger.getLogger(getClass());
    @Inject
    private IdentityService idmService;
    @Inject
    private MailServiceImpl mailService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)//headers="Accept=application/json"
    public UserData screenNameExists() {
        System.out.println("SCREEN NAME EXISTS");
        return new UserData("dubic", "udubic@gmail.com", "dcamic");
    }

    @RequestMapping(value = "/email-unique")
    public @ResponseBody
    JsonObject emailIsValid(@RequestBody UniqueValidation validation) {
        JsonObject resp = new JsonObject();
        resp.addProperty("value", validation.getValue());
        resp.addProperty("isValid", idmService.getUniqueEmail(validation.getValue()) == null);
        return resp;
    }

    @RequestMapping(value = "/name-unique")
    public @ResponseBody
    JsonObject nameIsValid(@RequestBody UniqueValidation validation) {
        JsonObject resp = new JsonObject();
        resp.addProperty("value", validation.getValue());
        resp.addProperty("isValid", idmService.validateScreenName(validation.getValue()) == null);
        return resp;
    }

    @RequestMapping(value = "/current")
    public @ResponseBody
    JsonObject currentUser() {
        JsonObject resp = new JsonObject();
        User user = idmService.getUserLoggedIn();
        if(user == null){
            resp.addProperty("code", 404);
            return resp;
        }
        resp.addProperty("code", 0);
        resp.addProperty("id", user.getId());
        resp.addProperty("email", user.getEmail());
        resp.addProperty("picture", user.getPicture());
        resp.addProperty("screenName", user.getScreenName());
        resp.add("profile", new Gson().toJsonTree(user.getProfile()));
        return resp;
    }
}
