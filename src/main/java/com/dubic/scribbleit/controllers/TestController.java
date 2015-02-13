/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import com.dubic.scribbleit.dto.UserData;
import com.dubic.scribbleit.idm.spi.IdentityService;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author dubem
 */
@Controller
@RequestMapping("/test")
public class TestController {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private IdentityService idservice;

    @RequestMapping("/simple-mail")
    public @ResponseBody
    String simpleMessageMail(@RequestParam(value = "to", defaultValue = "dubem.uzuegbu@crowninteractive.com") String recipient) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setStartTLSEnabled(true);
//            email.setStartTLSRequired(true);
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(25);
//            email.setSslSmtpPort("25");
            System.out.println("port is - " + email.getSmtpPort());
            email.setAuthenticator(new DefaultAuthenticator("udubic@gmail.com", "dcamic4602"));
            email.setSSLOnConnect(true);
            email.setFrom("udubic@gmail.com");
            email.setSubject("A test mail");
            email.setHtmlMsg("This is a test mail from my application");

            email.addTo(recipient);
            email.send();
            return "Mail sent to >> " + recipient;
        } catch (EmailException e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    @RequestMapping("/createusers")
    public @ResponseBody
    String createUsers(@RequestParam("n") int num,@RequestParam("name") String name) {
        int complete = 0;
        try {
            for (int i = 0; i < num; i++) {
                idservice.userRegistration(new UserData(name + i, name + i + "@gmail.com", "password" + i));
                complete++;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
        return "completed : " + complete;
    }

    @RequestMapping("/load")
    public String loadPage(@RequestParam("page") String page) {
        return page;
    }

}
