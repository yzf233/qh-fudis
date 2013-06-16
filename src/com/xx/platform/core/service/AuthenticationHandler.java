package com.xx.platform.core.service;

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
*/import org.apache.log4j.Logger;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.exchange.InMessage;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.jdom.*;
import org.hibernate.criterion.DetachedCriteria;
import com.opensymphony.xwork2.Action;
import org.hibernate.criterion.Restrictions;

import com.xx.platform.core.SearchContext;
import com.xx.platform.domain.model.user.User;
import com.xx.platform.util.tools.MD5;
import com.xx.platform.web.actions.SessionAware;

public class AuthenticationHandler
    extends AbstractHandler {

  public void invoke(MessageContext context) throws Exception {

    InMessage message = context.getInMessage();
    final Namespace TOKEN_NS = Namespace.getNamespace("xdtech",
        "http://www.xd-tech.com.cn");
    if (message.getHeader() == null) {
      throw new XFireFault(" GetRelation Service Should be Authenticated ",
                           XFireFault.SENDER);
    }

    Element token = message.getHeader().getChild("AuthenticationToken",
                                                 TOKEN_NS);
    if (token == null) {
      throw new XFireFault(" Request must include authentication token. ",
                           XFireFault.SENDER);
    }
    System.out.println(token.toString());
    String username = token.getChild("Username", TOKEN_NS).getValue();
    String password = token.getChild("Password", TOKEN_NS).getValue();

    if (username == null || password == null)
      throw new XFireFault("请使用安全认证方式访问",
                           XFireFault.SENDER);
    User user = new User();
    user.setUsername(username);
    user.setPassword(MD5.encoding(password));
    if (SearchContext.webServiceAuthUser == null) {
      java.util.List<User> userList = SearchContext.getDataHandler().
          findAllByCriteria(DetachedCriteria.forClass(User.class).
                            add(Restrictions.eq("username", user.getUsername())).
                            add(Restrictions.eq(
                                "password", user.getPassword())));
      if (userList != null && userList.size() > 0) {
        user = userList.remove(0);
        SearchContext.webServiceAuthUser = user ;
      }
      else {
        throw new XFireFault("认证失败! 请检查用户名和密码", XFireFault.SENDER);
      }
    }else
    {
      if(!SearchContext.webServiceAuthUser.getPassword().equals(user.getPassword()))
        throw new XFireFault("认证失败! 请检查用户名和密码", XFireFault.SENDER);
    }
  }
}
