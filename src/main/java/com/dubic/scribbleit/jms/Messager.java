/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dubic.scribbleit.jms;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.JmsUtils;

/**
 *
 * @author dubem
 */
@Named
public class Messager {

    @Inject
    private JmsTemplate jmsTemplate;
    @Inject
    private Destination destination;

    public void send(final String msg) {
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(msg);
                return message;
            }
        });
    }

    public String receive() {
        TextMessage message = (TextMessage) jmsTemplate.receive(destination);
        try {
            if (message == null) {
                return null;
            }
            
            return message.getText();
        } catch (JMSException e) {
            throw JmsUtils.convertJmsAccessException(e);
        }
    }
}
