package com.xx.platform.web.interceptor;

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
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.util.tools.ipcheck.CheckIPUtil;
import com.xx.platform.web.actions.ServletHandler;

public class ServletInterceptor extends AbstractInterceptor {

    @Override
    public String intercept(ActionInvocation ai) throws Exception {
    	Object o = ai.getAction();
        if (o instanceof ServletHandler) {
        	HttpServletRequest request=ServletActionContext.getRequest();
            /**
             * Request
             */
            ((ServletHandler)o).setServletRequest(request);
            /**
             * Response
             */
            ((ServletHandler)o).setServletResponse(ServletActionContext.getResponse());
            /**
             * WebApplicationContext
             */
            WebApplicationContext wac=WebApplicationContextUtils.getWebApplicationContext(ServletActionContext.getServletContext());
            ((ServletHandler)o).setWebApplicationContext(wac);
            /**
             * GeneraDAO
             */
            ((ServletHandler)o).setGeneraDAO(null);
            /**
             * 
             */
            ServletHandler action=(ServletHandler)o;
            String ip=CheckIPUtil.getIPAddressForRequest(request);
            action.setUserIP(ip);
            if(!checkIP(request)){
            	return CheckIPUtil.CHECK_IP_ERROR;
            }
        }
        return ai.invoke();

    }
    private boolean checkIP(HttpServletRequest request){
    	return CheckIPUtil.checkIpFacadeHttp(request); 
    }
}
