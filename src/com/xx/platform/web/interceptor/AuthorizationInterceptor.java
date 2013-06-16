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
import java.util.*;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.interceptor.*;
import org.apache.struts2.ServletActionContext;
import javax.servlet.http.HttpSession;

import com.xx.platform.domain.model.user.User;
import com.xx.platform.web.actions.*;

public class AuthorizationInterceptor extends AbstractInterceptor {

    @Override
    public String intercept(ActionInvocation ai) throws Exception {
        HttpSession session = ServletActionContext.getRequest().getSession(true);
        User user = (User) session.getAttribute(SessionAware.SESSION_LOGIN_ID);
        /**
         * First : 检查 Session 中是否有用户登陆信息 ， 如果有， 验证通过
         * Sec   : 如果 Session 中没有用户登陆信息，则检查 Cookies 中是否有有效的
         *         用户登陆信息
         * Thr   : 如果 Cookies 中没有用户身份标识信息 ， 调转到 登陆提示信息
         */
        if (user != null && user.getId() != null && user.getUsername() != null) {
            return ai.invoke();
        } else {
            return Action.LOGIN;
        }
    }
}
