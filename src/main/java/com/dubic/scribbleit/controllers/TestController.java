/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
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

    @RequestMapping("/simple-mail")
    public @ResponseBody String simpleMessageMail(@RequestParam(value = "to", defaultValue = "dubem.uzuegbu@crowninteractive.com") String recipient) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.biggamesng.com");
            email.setSmtpPort(25);
            email.setSslSmtpPort("25");
            System.out.println("port is - " + email.getSmtpPort());
            email.setAuthenticator(new DefaultAuthenticator("notifications@biggamesng.com", "crown@123"));
            email.setSSLOnConnect(false);
            email.setFrom("notifications@biggamesng.com");
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

}
