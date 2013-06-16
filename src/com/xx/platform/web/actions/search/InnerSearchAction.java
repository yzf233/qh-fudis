package com.xx.platform.web.actions.search;

import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.*;
import com.opensymphony.xwork2.*;
import com.xx.platform.core.analyzer.*;
import com.xx.platform.domain.model.search.*;

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
public class InnerSearchAction extends SearchAction{
  private Vcode vcode ;
  private List<Vcode> vcodeList ;
  public String jgSearch() {
    String q = request.getParameter("q");
    String sub = request.getParameter("sty") ;
    String ty = request.getParameter("ty") ;
    String xdtype = "";
    if(sub!=null && sub.equals("xggp"))
      xdtype = "h" ;
    else if(sub!=null && sub.equals("cg"))
      xdtype = "j" ;
    XDChineseTokenizer xdToken = new XDChineseTokenizer(new StringReader(
        q));
    StringBuffer sQuery = new StringBuffer();
    try {
      for (Token token = xdToken.next(); token != null && token.termText()!=null && !token.termText().replaceAll("[ ¡¡]","").equals("");
           token = xdToken.next()) {
        if (sQuery.length() > 2) {
          sQuery.append("%");
        }
        sQuery.append("%").append(token.termText()).append("%");
      }
    }
    catch (IOException ex) {
    }
    vcodeList = service.hqlList(
        "from Vcode where name like '" + sQuery.toString() + "' and xdtype='"+xdtype+"' group by name order by id",
        Vcode.class, 10);
    if(vcodeList!=null && vcodeList.size()>0)
    {
      vcode = (Vcode) vcodeList.get(0) ;
    }
    return Action.SUCCESS;
  }

  public void setVcode(Vcode vcode) {
    this.vcode = vcode;
  }

  public void setVcodeList(List vcodeList) {
    this.vcodeList = vcodeList;
  }

  public Vcode getVcode() {
    return vcode;
  }

  public List getVcodeList() {
    return vcodeList;
  }

}
