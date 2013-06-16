package com.xx.platform.core.task;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.*;
import com.xx.platform.web.actions.crawl.*;

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
public class TimerTask implements Task {
    public static boolean crawl_ing = false ;
    public void doTask() {
//        JMSReceiver jmsReceiver = (JMSReceiver)WebApplicationContextUtils.getWebApplicationContext(ServletActionContext.getServletContext()).getBean("jmsReceiver");
//        jmsReceiver.processMessage();
        com.xdtech.platform.util.tools.a.a(false) ;
        //判断授权文件，看是否有分布式授权
        if(!SearchContext.CONTROL.isDisLicense()){
        	RuntimeDataCollect.diserver=0;
        }
        if (!SearchContext.isShutDown&& NutchCommand.isCrawl() && !crawl_ing && SearchContext.getDao()!=null && !SearchContext.URL_GENERATOR_RUNNING) {
            crawl_ing = true ;
            crawl() ;
            NutchCommand.setCrawl(false) ;
            crawl_ing = false ;
        }
    }

    public void crawl() {
        final CrawlTool crawlTool = new CrawlTool();
    }
}
