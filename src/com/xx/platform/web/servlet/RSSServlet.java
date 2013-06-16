package com.xx.platform.web.servlet;

import java.util.*;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

import com.xx.platform.core.service.*;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import java.util.regex.Pattern;
import org.apache.nutch.html.Entities;

/**
 * 请求格式 ：/rss/rss?q=博客&t=atom&h=50&s=50
 * 其中
 *   q:关键词
 *   t:类型，可选值为 atom /rss
 *   h:是每页数据条数
 *   s:是start index ，翻页起始记录数
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
public class RSSServlet
    extends HttpServlet {
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws
      ServletException, IOException {
    String q = req.getParameter("q"); // query keyword
    String s = req.getParameter("s"); //start index
    String t = req.getParameter("t"); //type atom / rss
    String h = req.getParameter("h"); //hits per page
    String l = req.getParameter("l"); //Language
    res.setContentType("text/xml;charset=UTF-8");
    res.setCharacterEncoding("UTF-8");
    SyndFeedImpl feed = new SyndFeedImpl();
    feed.setFeedType(t != null && t.equals("atom") ? "atom_0.3" : "rss_2.0");
    feed.setDescription( ("\"" + q + "\"的搜索结果"));
    feed.setLink(toUTF_8(req.getRequestURI()));
    feed.setTitle(q);

    SyndEntry entry = null;
    List entryList = new ArrayList();
    feed.setEntries(entryList);
    try {
      if (q != null && q.length() > 0) {

        SearchService searchService = new SearchService();
        List<WebServiceSearchResult>
            webServiceList = searchService.search(q,
                                                  s != null && s.length() > 0 ?
                                                  Integer.parseInt(s) : 0,
                                                  h != null && h.length() > 0 ?
                                                  Integer.parseInt(h) : 0);

        for (WebServiceSearchResult result : webServiceList) {
          entry = new SyndEntryImpl();
          entry.setTitle( (result.getTitle()));
          entry.setUri( (result.getUrl()));
          entry.setLink( (result.getUrl()));
          SyndContentImpl content = new SyndContentImpl();
          content.setValue(toUTF_8(result.getSummaries()));
          entry.setDescription(content);
          entry.setPublishedDate(new Date());
          entryList.add(entry);
        }
      }
      SyndFeedOutput output = new SyndFeedOutput();
      output.output(feed, res.getWriter());
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) throws
      ServletException, IOException {
    doPost(req, res);
  }

  private String toUTF_8(String in) throws IOException {
    try {
      StringBuffer strb = new StringBuffer();
//           java.util.regex.Pattern pattern = Pattern.compile("&#[\\d]{1,6};") ;
//           java.util.regex.Matcher matcher = pattern.matcher(in) ;
//           while(matcher.find())
//           {
//               String res = matcher.group() ;
//           }
      for (int i = 0; i < in.length(); i++) {
        char c = in.charAt(i);
        if (c == '&') {
          if ( (i + 1) < in.length()) {
            if (in.charAt(i + 1) == '#') {
              int lastCharIndex = in.indexOf(";", i);
              if (lastCharIndex <= 0) {
                continue;
              }
              String temp = in.substring(i + 2, lastCharIndex);
              boolean isDigit = true;
              if (temp.replaceAll("[\\d]", "").length() > 0) {
                isDigit = false;
              }
              if (isDigit) {
                int code = Integer.parseInt(temp);
                char con = (char) code;
                strb.append(con);
              }
              i = lastCharIndex;
              continue;
            }
          }
        }
        strb.append(c);
      }
      return strb.toString().replaceAll("&nbsp;", " "); //new String(strb.toString().getBytes("GBK"), "UTF-8");
    }
    catch (Throwable throwsable) {
      throw new IOException(throwsable.getMessage());
    }
  }

}
