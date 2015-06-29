/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.jms;

import com.dubic.scribbleit.utils.HClient;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author dubem
 */
@Transactional("jmstrans")
public class Listener implements MessageListener {

    private Logger log = Logger.getLogger(getClass());//org.springframework.mail.javamail.JavaMailSenderImpl

//    @Override
    public void onMessage(Message msg) {
        log.debug("...................Message Recieved on listener..................");
        log.debug(String.format("[%s]", msg));
//        throw new RuntimeException("testing JMS transaction");
    }
    
    public static void main(String[] args) {
        FileInputStream in = null;
        try {
            in = new FileInputStream("C:\\Users\\Dubic\\Pictures\\IMG_20150329_171004.jpg");
           
            new HClient().post(IOUtils.toByteArray(in),"http://localhost:7070/services/s3/add");
            in.close();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
        }
//            finally {
//            try {
//                in.close();
//            } catch (IOException ex) {
//                java.util.logging.Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }

}
