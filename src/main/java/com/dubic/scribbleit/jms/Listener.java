/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.jms;

import javax.jms.Message;
import javax.jms.MessageListener;
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

}
