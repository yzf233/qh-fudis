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
         * First : ��� Session ���Ƿ����û���½��Ϣ �� ����У� ��֤ͨ��
         * Sec   : ��� Session ��û���û���½��Ϣ������ Cookies ���Ƿ�����Ч��
         *         �û���½��Ϣ
         * Thr   : ��� Cookies ��û���û���ݱ�ʶ��Ϣ �� ��ת�� ��½��ʾ��Ϣ
         */
        if (user != null && user.getId() != null && user.getUsername() != null) {
            return ai.invoke();
        } else {
            return Action.LOGIN;
        }
    }
}
