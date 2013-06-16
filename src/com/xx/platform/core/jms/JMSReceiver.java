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


import javax.jms.Message;
import javax.jms.TextMessage;
import org.springframework.jms.core.JmsTemplate102;

import com.xx.platform.core.nutch.CrawlTool;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.web.actions.crawl.SwingWorker;


/**
 * @author naveen balani
 */
public class JMSReceiver {

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

    public void processMessage() {
        Message msg = jmsTemplate102.receive("JMS_RequestResponseQueue");
        try {
            TextMessage textMessage = (TextMessage) msg;
            if (msg != null) {
                System.out.println("Task:"+textMessage.getText());
//                if(textMessage.getText()!=null && textMessage.getText().equals(CrawlCommand.CRAWL_COMMAND))
//                {
//                    final CrawlTool crawlTool = new CrawlTool(1);
//                    SwingWorker swingWork = new SwingWorker() {
//                        public Object construct() {
//                            return crawlTool;
//                        }
//
//                    };
//                    swingWork.start();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
