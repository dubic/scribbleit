/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.controllers;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.dubic.scribbleit.dto.UserActivation;
import com.dubic.scribbleit.dto.UserData;
import com.dubic.scribbleit.idm.spi.IdentityServiceImpl;
import com.google.gson.Gson;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private IdentityServiceImpl idservice;
    @Autowired
    private BasicTextEncryptor basicEncryptor;
    @Value("${mail.host}")
    private String host;
    @Value("${mail.port}")
    private int port;
    @Value("${mail.username.info}")
    private String username;
    @Value("${mail.sender}")
    private String sender;
    @Value("${mail.sender.name}")
    private String senderName;
    private boolean sslOnConnect;
    @Value("${mail.tls}")
    private boolean tls;
    @Value("${mail.template.path}")
    private String templatePath;
    @Value("${aws.key.id}")
    private String awsKeyId;
    @Value("${aws.key.secret}")
    private String awsKeySecret;

    @RequestMapping("/simple-mail")
    public @ResponseBody
    String simpleMessageMail(@RequestParam(value = "to", defaultValue = "dubem.uzuegbu@crowninteractive.com") String recipient) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setStartTLSEnabled(true);
//            email.setStartTLSRequired(true);
            email.setHostName("email-smtp.us-west-2.amazonaws.com");
            email.setSmtpPort(25);
//            email.setSslSmtpPort("25");
            System.out.println("port is - " + email.getSmtpPort());
            email.setAuthenticator(new DefaultAuthenticator("", ""));
//            email.setSSLOnConnect(true);
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
    
    @RequestMapping("/mail-config")
    public @ResponseBody
    String mailConfig(@RequestParam(value = "to", defaultValue = "dubem.uzuegbu@crowninteractive.com") String recipient) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(this.host);
            email.setSmtpPort(this.port);
            email.setStartTLSEnabled(true);
//            email.setSslSmtpPort("25");
            log.debug("port is - " + email.getSmtpPort());
            email.setAuthenticator(new DefaultAuthenticator(this.username, "dcamic4602{}"));
//            email.setSSLOnConnect(false);
            email.setFrom(this.sender,this.senderName);
            email.setSubject("Mail from configuration");
            email.setHtmlMsg("This is a test mail from my application from configuration properties");
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
    
    @RequestMapping("/s3/save")
    @ResponseBody
    public String saveS3(@RequestParam("name") String name,@RequestParam("file") String file) {
        AWSCredentials credentials = new AWSCredentials() {

            @Override
            public String getAWSAccessKeyId() {
                return TestController.this.awsKeyId;
            }

            @Override
            public String getAWSSecretKey() {
                return TestController.this.awsKeySecret;
            }
        };
        AmazonS3Client s3Client = new AmazonS3Client(credentials);
        PutObjectRequest request = new PutObjectRequest("scribblespost", name, new File(file));
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        PutObjectResult res = s3Client.putObject(request);
        return res.getETag();
        
    }
    
    @RequestMapping("/encode")
    public @ResponseBody String encode() {
        try {
//            String encodedUrl = URLEncoder.encode("u30WDDqVuzqH9fIilim0fa/Jwy6iFx9b6uATMs9pTyW1tj+a5r8jcQ==", "UTF-8");
            String encodedUrl = URLEncoder.encode(basicEncryptor.encrypt(new Gson().toJson(new UserActivation(3L))), "UTF-8");
//            return basicEncryptor.encrypt(new Gson().toJson(new Activation(1L, IdmUtils.getActivationTokenExpiryDt())));
            System.out.println("encodedUrl : "+encodedUrl);
            return encodedUrl;
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(TestController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "error";
    }
    
//    @RequestMapping("/encrypt")
//    public @ResponseBody String encrypt(@RequestParam("text") String txt) {
//        Share sh = new Share();
//        sh.setName("dubic");
//        sh.setUser(true);
//        return basicEncryptor.encrypt(new Gson().toJson(sh));
//    }
    
    @RequestMapping("/decrypt")
    public @ResponseBody UserActivation decrypt(@RequestParam("text") String txt) {
//        String t = basicEncryptor.decrypt("u30WDDqVuzqH9fIilim0fa/Jwy6iFx9b6uATMs9pTyW1tj+a5r8jcQ==");
        String t = basicEncryptor.decrypt(txt);
        
        UserActivation s = new Gson().fromJson(t, UserActivation.class);
        System.out.println(s);
//        System.out.println("name : "+s.getId());
//        System.out.println("date : "+s.getExpDate());
        return s;
    }

}
