package com.xx.platform.util.constants;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import java.util.logging.Logger;
import org.apache.nutch.util.LogFormatter;

import com.xx.platform.domain.model.crawl.Proregion;
import com.xx.platform.web.interceptor.TimerInterceptor;

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
public class IbeaProperty {
    public static final String DAO_NAME_SPACE = "dao";
    public final static Logger log = LogFormatter.getLogger(IbeaProperty.class.toString());
    public final static String TIMER_TASK_TYPE_CRAWLER = "crawler";
    public final static String TIMER_TASK_TYPE_FILECRAWLER="filecrawler";
    public final static String TIMER_TASK_TYPE_URL = "urlgen";
    public final static float SOURCE_DEFUALT = 4.3356f ;// 页面平均分值(作为加权重的依据) qh 07/07/30
    public static String contextPath ;
}
