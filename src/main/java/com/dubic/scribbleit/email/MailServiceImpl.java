/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.email;

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * sends a mail using mail server configured values from spring file.
 *
 * @author DUBIC
 * @since notifications 1.0.5
 */
@Named
public class MailServiceImpl {

    private final Logger log = Logger.getLogger(getClass());
    @Autowired
    private VelocityEngine velocityEngine;
    @Value("${mail.host}")
    private String host;
    @Value("${mail.port}")
    private int port;
    @Value("${mail.username.info}")
    private String username;
    private String password;
    @Value("${mail.sender}")
    private String sender;
    @Value("${mail.sender.name}")
    private String senderName;
    private boolean sslOnConnect;
    @Value("${mail.tls}")
    private boolean tls;
    private boolean sslCheckServer;
    private boolean startTlsEnabled;
    @Value("${mail.template.path}")
    private String templatePath;
    private String sslPort = "25";

    @PostConstruct
    public void inited() {
        log.debug("sslOnConnect = " + sslOnConnect);
        log.debug("sslCheckServer = " + sslCheckServer);
        log.debug("startTlsEnabled = " + startTlsEnabled);
    }

    @Async
    public void queueMail(SimpleMailEvent mailEvent) {
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
            email.setSubject(mailEvent.getSubject());
            email.setHtmlMsg(mailEvent.getMessage());
            List<String> tos = mailEvent.getTo();
            for (String rec : tos) {
                email.addTo(rec);
            }
//            log.info("about to send >> " + new Gson().toJson(mailEvent));
            email.send();
            log.info("Mail sent to >> " + mailEvent.getTo());
        } catch (EmailException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * sends a mail asynchronously using apache mail.if sender is not specified,
     * uses system configured sender
     *
     * @param mailEvent
     * @param template template name
     * @param model template name-value params
     * @since notifications 1.0.5
     */
//    @Async
//    public void queueMail(SimpleMailEvent mailEvent) {
//        try {
////            Email email = new HtmlEmail();
//////            email.setSSLCheckServerIdentity(sslCheckServer);
////            email.setStartTLSEnabled(true);
//////            email.setStartTLSRequired(true);
////            email.setHostName(host);
////            email.setSmtpPort(465);
////            email.setSslSmtpPort("465");
////            System.out.println("port is - " + email.getSmtpPort());
////            email.setAuthenticator(new DefaultAuthenticator(username, password));
////            email.setSSLOnConnect(false);
////
////            email.setSubject(mailEvent.subject);
////
////            email.setFrom(mailEvent.getFrom() == null ? this.sender : mailEvent.getFrom(), senderName);
////
////            if (template != null) {
////                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath + template, "UTF-8", model);
////                System.out.println(text);
////                email.setMsg(text);
////            } else {
////                email.setMsg(mailEvent.getMessage());
////            }
////
////            for (String rec : mailEvent.getTo()) {
////                email.addTo(rec);
////            }
////
////            email.send();
//            System.out.println("sending mail .................");
//            Properties props = new Properties();
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.smtp.socketFactory.port", "25");
//            props.put("mail.smtp.socketFactory.class",
//                    "javax.net.ssl.SSLSocketFactory");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.port", "25");
//            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
//            Session session = Session.getDefaultInstance(props,
//                    new javax.mail.Authenticator() {
//                        @Override
//                        protected PasswordAuthentication getPasswordAuthentication() {
//                            return new PasswordAuthentication(username, password);
//                        }
//                    });
//
//            try {
//
//                Message message = new MimeMessage(session);
//                message.setFrom(new InternetAddress("udubic@gmail.com"));
//                message.setRecipients(Message.RecipientType.TO,
//                        InternetAddress.parse(mailEvent.getTo().get(0)));
//                message.setSubject("Testing Subject");
//                message.setText("Dear Mail Crawler,"
//                        + "\n\n No spam to my email, please!");
//
//                Transport.send(message);
//
//                System.out.println("Done");
//
//            } catch (MessagingException e) {
//                throw new RuntimeException(e);
//            }
//            log.info(String.format("sent mail to %s", Arrays.toString(mailEvent.getTo().toArray())));
//        } catch (Exception ex) {
//            log.error("Error sending mail : " + ex.getMessage(), ex);
//        }
//    }
    private String[] getRecipients(String to) {
        String[] split = to.split(",");
        return split;
    }

    @Async
    public void sendMail(SimpleMailEvent mail, String template, Map model) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName(this.host);
            email.setSmtpPort(this.port);
//            email.setSslSmtpPort("25");
            log.debug("port is - " + email.getSmtpPort());
            email.setAuthenticator(new DefaultAuthenticator(this.username, "dcamic4602{}"));
//            email.setSSLOnConnect(false);
            email.setFrom(this.sender,this.senderName);
            email.setSubject(mail.getSubject());
//            email.setHtmlMsg("This is a test mail from my application");
            if (template != null) {
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath + template, "UTF-8", model);
                System.out.println(text);
                email.setHtmlMsg(text);
            } else {
                email.setMsg(mail.getMessage());
            }
            for (String rec : mail.getTo()) {
                email.addTo(rec);
            }
            email.send();
        } catch (EmailException e) {
            log.error(e.getMessage(), e);
        }
    }
}
