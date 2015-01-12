/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import com.dubic.scribbleit.dto.Registration;
import com.dubic.scribbleit.dto.Response;
import com.dubic.scribbleit.dto.UniqueValidation;
import com.dubic.scribbleit.dto.UserData;
import com.dubic.scribbleit.email.MailServiceImpl;
import com.dubic.scribbleit.email.SimpleMailEvent;
import com.dubic.scribbleit.idm.models.User;
import com.dubic.scribbleit.idm.spi.IdentityService;
import com.dubic.scribbleit.idm.spi.InvalidTokenException;
import com.dubic.scribbleit.idm.spi.LinkExpiredException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(value = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    Response register(@RequestBody @Valid UserData userData) {
        Response resp = new Response();
        log.debug(new Gson().toJson(userData));
        try {
            idmService.userRegistration(userData);
            resp.data(userData).msg(1, "your account has been created. An email will be sent to you shortly to activate your account");//.msg(1, "");
        } catch (ConstraintViolationException cve) {
            log.error(cve.getMessage(), cve);
            for (ConstraintViolation<?> constraintViolation : cve.getConstraintViolations()) {
                resp.msg(-1, constraintViolation.getMessage());
            }
        }catch(Exception e){
            log.fatal(e.getMessage(), e);
            resp.msg(-1, "Cannot create account at this momment. Try again later");
        }
        return resp;
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

    @RequestMapping(value = "/activate/{token}")
    public String activate(Model m, @PathVariable String token) {
        try {
            idmService.activateUser(token);
            //redirect to success activation
            m.addAttribute("error", false);
        } catch (InvalidTokenException ex) {
            m.addAttribute("error", true);
            m.addAttribute("linkError", Boolean.TRUE);
            log.error(ex.getMessage());
        } catch (LinkExpiredException ex) {
            m.addAttribute("error", true);
            m.addAttribute("linkExpired", Boolean.TRUE);
            log.error(ex.getMessage());
        } catch (Exception ex) {
            m.addAttribute("error", true);
            m.addAttribute("Service error, Try some time later", Boolean.TRUE);
            log.error(ex.getMessage());
        }
        return "activation";
    }
}
