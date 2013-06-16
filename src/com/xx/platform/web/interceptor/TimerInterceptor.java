package com.xx.platform.web.interceptor;

import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.ActionInvocation;
import com.xx.platform.util.constants.IbeaProperty;

import java.util.logging.Logger;
import org.apache.nutch.util.LogFormatter;

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
public class TimerInterceptor extends AbstractInterceptor{

    public String intercept(ActionInvocation invocation) throws Exception {
        long startTime = System.currentTimeMillis();
        String result = invocation.invoke();
        long executionTime = System.currentTimeMillis() - startTime;

        StringBuffer message = new StringBuffer(100);
        message.append("Ö´ÐÐ£º [");
        String namespace = invocation.getProxy().getNamespace();
        if ((namespace != null) && (namespace.trim().length() > 0)) {
            message.append(namespace).append("/");
        }
        message.append(invocation.getProxy().getActionName());
        message.append("!");
        message.append(invocation.getProxy().getMethod());
        message.append("] »¨·Ñ ").append(executionTime).append(" ms.");
        IbeaProperty.log.info(message.toString());
        return result ;
    }

}
