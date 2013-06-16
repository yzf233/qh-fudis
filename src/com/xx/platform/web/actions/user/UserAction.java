package com.xx.platform.web.actions.user;

import java.io.IOException;
import java.io.PrintWriter;

import com.xx.platform.core.SearchContext;
import com.xx.platform.domain.model.user.User;
import com.xx.platform.util.tools.MD5;
import com.xx.platform.web.actions.BaseAction;
import com.xx.platform.web.actions.SessionAware;

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
public class UserAction extends BaseAction{
    private String npwd ;
    private String opwd ;
    private String message ;
    public String changePwd() throws IOException
    {
    	String flag="0";
        User user  = (User)request.getSession(true).getAttribute(SessionAware.SESSION_LOGIN_ID) ;
        if(npwd!=null && opwd!=null && MD5.encoding(opwd).equals(user.getPassword()))
        {
            user.setPassword(MD5.encoding(npwd));
            service.updateIObject(user);
            message = "√‹¬Î–ﬁ∏ƒ≥…π¶" ;
            flag="1";
            if(SearchContext.webServiceAuthUser==null)
            {
              SearchContext.webServiceAuthUser = user ;
            }
            SearchContext.webServiceAuthUser.setPassword(user.getPassword());
        }else{
            message = "√‹¬Î–ﬁ∏ƒ ß∞‹£¨√‹¬Î¥ÌŒÛ" ;
        }
        response.setCharacterEncoding("utf-8");
        PrintWriter out= response.getWriter();
        out.write(flag);
        out.flush();
        out.close();
        return null;
    }
    public String getNpwd() {
        return npwd;
    }

    public String getOpwd() {
        return opwd;
    }

    public String getMessage() {
        return message;
    }

    public void setNpwd(String npwd) {
        this.npwd = npwd;
    }

    public void setOpwd(String opwd) {
        this.opwd = opwd;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
