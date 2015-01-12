/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.email;

import com.google.gson.Gson;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * sends a mail using mail server configured values from spring file.
 *
 * @author DUBIC
 * @since notifications 1.0.5
 */
public class MailServiceImpl {

    private Logger log = Logger.getLogger(getClass());
    private VelocityEngine velocityEngine;
    private String host;
    private int port;
    private String username;
    private String password;
    private String sender;
    private String senderName;
    private boolean sslOnConnect;
    private boolean tls = true;
    private boolean sslCheckServer;
    private boolean startTlsEnabled;
    private String templatePath = "resources/";
    private String sslPort = "25";

    @PostConstruct
    public void inited() {
        log.debug("sslOnConnect = " + sslOnConnect);
        log.debug("sslCheckServer = " + sslCheckServer);
        log.debug("startTlsEnabled = " + startTlsEnabled);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSslOnConnect() {
        return sslOnConnect;
    }

    public void setSslOnConnect(boolean sslOnConnect) {
        this.sslOnConnect = sslOnConnect;
    }

    public boolean isTls() {
        return tls;
    }

    public void setTls(boolean tls) {
        this.tls = tls;
    }

    public boolean isSslCheckServer() {
        return sslCheckServer;
    }

    public void setSslCheckServer(boolean sslCheckServer) {
        this.sslCheckServer = sslCheckServer;
    }

    public boolean isStartTlsEnabled() {
        return startTlsEnabled;
    }

    public void setStartTlsEnabled(boolean startTlsEnabled) {
        this.startTlsEnabled = startTlsEnabled;
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getSslPort() {
        return sslPort;
    }

    public void setSslPort(String sslPort) {
        this.sslPort = sslPort;
    }

    @Async
    public void queueMail(SimpleMailEvent mailEvent) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.biggamesng.com");
            email.setSmtpPort(25);
            email.setSslSmtpPort("25");
//            email.setSSLCheckServerIdentity(true);
            email.setAuthenticator(new DefaultAuthenticator("notifications@biggamesng.com", "crown@123"));
            email.setSSLOnConnect(false);
            email.setFrom("notifications@biggamesng.com");
            email.setSubject(mailEvent.getSubject());
            email.setHtmlMsg(mailEvent.getMessage());
            List<String> tos = mailEvent.getTo();
            for (String rec : tos) {
                email.addTo(rec);
            }
            log.info("about to send >> " + new Gson().toJson(mailEvent));
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
            email.setHostName("smtp.biggamesng.com");
            email.setSmtpPort(25);
            email.setSslSmtpPort("25");
            System.out.println("port is - " + email.getSmtpPort());
            email.setAuthenticator(new DefaultAuthenticator("notifications@biggamesng.com", "crown@123"));
            email.setSSLOnConnect(false);
            email.setFrom("notifications@biggamesng.com");
            email.setSubject(email.getSubject());
            email.setHtmlMsg("This is a test mail from my application");
            if (template != null) {
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templatePath + template, "UTF-8", model);
                System.out.println(text);
                email.setMsg(text);
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
