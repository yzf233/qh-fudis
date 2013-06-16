package com.xx.platform.core.jms;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.jms.core.JmsTemplate102;
import org.springframework.jms.core.MessageCreator;

/**
 * @author naveen balani
 */
public class JMSSender {

    private JmsTemplate102 jmsTemplate102;


    /**
     * @return Returns the jmsTemplate102.
     */
    public JmsTemplate102 getJmsTemplate102() {
        return jmsTemplate102;
    }

    /**
     * @param jmsTemplate102 The jmsTemplate102 to set.
     */
    public void setJmsTemplate102(JmsTemplate102 jmsTemplate102) {
        this.jmsTemplate102 = jmsTemplate102;
    }

    public void sendMesage(final String command) {
        jmsTemplate102.send("JMS_RequestResponseQueue", new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(command);
            }
        });

    }
}
