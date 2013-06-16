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
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.interceptor.*;
import org.apache.struts2.*;
import org.springframework.web.context.support.*;

import com.xx.platform.dao.GeneraDAO;
import com.xx.platform.util.constants.IbeaProperty;
import com.xx.platform.web.actions.*;

public class HttpNoCacheInterceptor extends AbstractInterceptor {

    @Override
    public String intercept(ActionInvocation ai) throws Exception {
        String result = ai.invoke();
        {
            ServletActionContext.getResponse().setHeader("Cache-Control",
                    "no-cache"); //Forces caches to obtain a new copy of the page from the origin server
            ServletActionContext.getResponse().setHeader("Cache-Control",
                    "no-store"); //Directs caches not to store the page under any circumstance
            ServletActionContext.getResponse().setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
            ServletActionContext.getResponse().setHeader("Pragma", "no-cache"); //HTTP 1.0 backward compatibility
        }
        return result ;

    }
}
