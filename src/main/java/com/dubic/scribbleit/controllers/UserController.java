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
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author dubem
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private IdentityService idmService;
    @Autowired
    private MailServiceImpl mailService;
    @Value("${picture.location}")
    private String picturePath;
    @Value("${default.profile.picture}")
    private String avatar;

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
    
    @RequestMapping(value = "/img/{pic}")
    public void processImage(HttpServletRequest request, HttpServletResponse response, @PathVariable("pic") String picId) {
        ServletOutputStream responseStream = null;
        FileInputStream pictureStream = null;
        try {
            response.setContentType("image/jpeg");
            responseStream = response.getOutputStream();

            if (!picId.equalsIgnoreCase("male") || picId.equalsIgnoreCase("404")) {
                pictureStream = new FileInputStream(picturePath + picId);
                IOUtils.copy(pictureStream, responseStream);
            } else {
                //send avatar
                pictureStream = new FileInputStream(picturePath + "male.jpg");
                IOUtils.copy(pictureStream, responseStream);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            try {
                responseStream.close();
                pictureStream.close();
            } catch (IOException ex) {
                log.fatal(ex.getMessage());
            }
        }
    }
    
    @RequestMapping(value = {"/picture/upload"}, method = {RequestMethod.POST}, produces = "text/plain")
    @ResponseBody
    public String uploadPicture(@RequestParam("file") MultipartFile file,@RequestParam("username") String username) {
        if (!file.isEmpty()) {
            System.out.println("username - " + username);
            System.out.println("name - " + file.getName());
            System.out.println("type - " + file.getContentType());
            try {
                return this.idmService.changePicture(file.getInputStream());
            } catch (Exception e) {
                this.log.error(e.getMessage(),e);
                return this.avatar;
            }

        }
        this.log.warn("No file was recieved");
        return "male.jpg";
    }
}
