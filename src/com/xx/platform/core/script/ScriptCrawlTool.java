package com.xx.platform.core.script;

import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.nutch.parse.*;
import org.apache.nutch.parse.html.*;
import org.apache.nutch.protocol.*;
import org.apache.nutch.pagedb.FetchListEntry;

import com.xx.platform.core.nutch.WebDB;

import org.apache.nutch.net.URLFilters;
import org.apache.nutch.util.LogFormatter;
import org.apache.nutch.db.Page;

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
public class ScriptCrawlTool {
  private static MultiThreadedHttpConnectionManager connectionManager = new
      MultiThreadedHttpConnectionManager();
  private static HttpClient client = new HttpClient(connectionManager);

  /**
   * 支持超线程访问，可以使用超线程
   * @param inputURL String
   * @param port int
   * @param protocl String
   * @return String
   * @throws Exception
   */
  public String getContent(String inputURL, String encoding) {
    String response = null;
    try {
//            client.getHostConfiguration().setHost(inputURL);
      Content content = getByteContent(inputURL);
      response = new String(content!=null?content.getContent():"".getBytes(),encoding) ;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return response != null ? response : "";
  }

  /**
   *
   * @param url String
   * @param urlReg String
   * @return List
   */
  public List<String> getContentA(String url, String urlReg) {
    List<String> urlList = new ArrayList<String> ();
    Content content = getByteContent(url);
    if (content != null) {
      Parser parser = null;
      Parse parse = null;
      try {
        parser = new org.apache.nutch.parse.html.HtmlParser();
        parse = parser.getParse(content);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      Outlink[] outlinks = parse.getData().getOutlinks();
      if (outlinks != null)
        for (Outlink outlink : outlinks) {
          if (outlink.getToUrl() != null && outlink.getToUrl().matches(urlReg))
            urlList.add(outlink.getToUrl());
        }
    }
    return urlList;
  }

  /**
   *
   * @param url String
   * @return Content
   */
  private Content getByteContent(String url) {
    FetchListEntry fle = new FetchListEntry();
    Content content = null;
    {
      try {
        fle = new FetchListEntry(false,
                                 new Page(url, 1.0f, 1), new String[] {
        });

      }
      catch (MalformedURLException ex) {}
      try {
        boolean refetch = false;
        int redirCnt = 0;
        do {
          refetch = false;
          Protocol protocol = new org.apache.nutch.protocol.http.Http();
          ProtocolOutput output = protocol.getProtocolOutput(fle);
          ProtocolStatus pstat = output.getStatus();
          content = output.getContent();
          switch (pstat.getCode()) {
            case ProtocolStatus.SUCCESS:
              break;
            case ProtocolStatus.MOVED: // try to redirect immediately
            case ProtocolStatus.TEMP_MOVED: // try to redirect immediately

              String newurl = pstat.getMessage();
              newurl = URLFilters.filter(newurl);
              if (newurl != null && !newurl.equals(url)) {
                refetch = true;
                url = newurl;
                redirCnt++;
                // create new entry.
                fle = new FetchListEntry(true,
                                         new Page(url,
                                                  2.0f,
                                                  fle.getIsparseoutlink()),
                                         new String[0]);
              }
              else {
              }
              break;
            case ProtocolStatus.GONE:
            case ProtocolStatus.NOTFOUND:
            case ProtocolStatus.ACCESS_DENIED:
            case ProtocolStatus.ROBOTS_DENIED:
            case ProtocolStatus.RETRY:
            case ProtocolStatus.NOTMODIFIED:
              break;
            case ProtocolStatus.EXCEPTION:
              break;
            default:
          }
        }
        while (refetch && (redirCnt < 3));
      }
      catch (Throwable t) { // an unchecked exception
        t.printStackTrace();
      }
    }
    return content;
  }

  /**
   *
   * @param content String
   * @param regex String
   * @return List
   * @throws Exception
   */
  public List getURL(String content, String regex) throws Exception {
    Pattern pattern = null;
    Matcher matcher = null;
    pattern = Pattern.compile(regex);
    matcher = pattern.matcher(content);
    List<String> resList = new ArrayList();
    while (matcher.find()) {
      if (matcher.groupCount() >= 1)
        resList.add(matcher.group(1));
    }

    return resList;
  }

  /**
   *
   * @param content String
   * @param regex String
   * @param startIndexRegex String
   * @param endIndexRegex String
   * @return List
   * @throws Exception
   */
  public List getURL(String content, String regex, String startIndexRegex,
                     String endIndexRegex) throws Exception {
    int start = content.indexOf(startIndexRegex);
    int end = content.indexOf(endIndexRegex);
    if (start >= 0 && end > 0)
      content = content.substring(start, end);
    else if (start >= 0 && end <= 0)
      content = content.substring(start);
    else if (start < 0 && end > 0)
      content = content.substring(0, end);
    else
      ;
    Pattern pattern = null;
    Matcher matcher = null;
    pattern = Pattern.compile(regex);
    matcher = pattern.matcher(content);
    List<String> resList = new ArrayList();
    while (matcher.find()) {
      resList.add(matcher.group(1));
    }
    return resList;
  }

  /**
   *
   * @param content String
   * @param regex String
   * @return List
   * @throws Exception
   */
  public List getURLA(String content, String regex) throws Exception {
    Pattern pattern = null;
    Matcher matcher = null;
    pattern = Pattern.compile(regex);
    matcher = pattern.matcher(content);
    List<String[]> resList = new ArrayList();

    while (matcher.find()) {
      if (matcher.groupCount() > 1) {
        resList.add(matcherToArray(matcher));
      }
    }

    return resList;
  }

  private String[] matcherToArray(Matcher matcher) {
    String[] group = null;
    if (matcher.groupCount() >= 1) {
      group = new String[matcher.groupCount()];
      for (int i = 1; i <= matcher.groupCount(); i++) {
        group[i - 1] = matcher.group(i);
      }
    }
    return group;
  }

  public List getURLB(String content, String regex, String startIndexRegex,
                      String endIndexRegex) throws Exception {
    int start = content.indexOf(startIndexRegex);
    int end = content.indexOf(endIndexRegex);
    if (start >= 0 && end > 0)
      content = content.substring(start, end);
    else if (start >= 0 && end <= 0)
      content = content.substring(start);
    else if (start < 0 && end > 0)
      content = content.substring(0, end);
    else
      ;
    Pattern pattern = null;
    Matcher matcher = null;
    pattern = Pattern.compile(regex);
    matcher = pattern.matcher(content);
    List<String[]> resList = new ArrayList();
    while (matcher.find()) {
      if (matcher.groupCount() > 1) {
        resList.add(matcherToArray(matcher));
      }
    }

    return resList;
  }

  public List<String> getURLByAuto(String content, String url) {
    List urlList = null;
    try {
      Parse parse = new HtmlParser().getParse(new Content(url, url,
          content.getBytes(), "text/html",
          new Properties()));
      URL host = new URL(url);
      Outlink[] outlinks = parse.getData().getOutlinks();
      Integer urlLength = 0;
      Map<Integer, Integer> lengthMap = new HashMap<Integer, Integer> ();
      java.util.regex.Pattern pattern = Pattern.compile(
          "(http://[\\S\\s]*?.(gif|jpeg|jpg|png|js|css))",
          Pattern.CASE_INSENSITIVE);
      java.util.regex.Matcher matcher;
      Outlink outlink = null;
      for (int i = 0; outlinks != null && i < outlinks.length; i++) {
        outlink = outlinks[i];
        if ( (matcher = pattern.matcher(outlink.getToUrl())).find() &&
            matcher.groupCount() >= 1) {
          outlinks[i] = null;
          continue;
        }
        if (outlink.getToUrl() != null &&
            outlink.getToUrl().length() == url.length()) {
          outlinks[i] = null;
          continue;
        }
        if (outlink.getToUrl() != null && outlink.getToUrl().startsWith("/")) {
          outlink = new Outlink("http://" + host.getHost() + outlink.getToUrl(),
                                outlink.getAnchor());
        }
        if (outlink.getToUrl() != null &&
            !outlink.getToUrl().startsWith("http")) {
          outlink = new Outlink("http://" + outlink.getToUrl(),
                                outlink.getAnchor());
        }
        if (urlPathFileLength(outlink.getToUrl()) < 3) {
          outlinks[i] = null;
          continue;
        }
        if ( (urlLength = lengthMap.get(urlPathFileLength(outlink.getToUrl()))) != null) {
          lengthMap.remove(urlPathFileLength(outlink.getToUrl()));
          lengthMap.put(urlPathFileLength(outlink.getToUrl()), urlLength + 1);
        }
        else {
          lengthMap.put(urlPathFileLength(outlink.getToUrl()), 1);
        }
      }
      Iterator<Integer> iterator = lengthMap.keySet().iterator();
      Object lm = null;
      urlLength = 0;
      while (iterator.hasNext()) {
        lm = iterator.next();
        if (lengthMap.get(lm).intValue() < 5) {
          continue;
        }
//          lengthMap.remove(lm) ;

        if (lm != null && lm instanceof Integer) {
          if ( ( (Integer) lm).intValue() > urlLength) {
            urlLength = ( (Integer) lm).intValue();
          }
        }
      }
      urlList = new ArrayList();
      for (int i = 0; outlinks != null && i < outlinks.length; i++) {
        outlink = outlinks[i];
        if (outlink != null && outlink.getToUrl() != null &&
            urlPathFileLength(outlink.getToUrl()) == urlLength) {
          urlList.add(outlink.getToUrl());
        }
      }
    }
    catch (MalformedURLException ex) {
    }
    return urlList;
  }

  private int urlPathFileLength(String urlStr) {
    String urlFilePath = null;
    try {
      URL url = new URL(urlStr);
      urlFilePath = url.getPath() + url.getFile();
    }
    catch (MalformedURLException ex) {
    }
    return urlFilePath != null ? urlFilePath.length() : 0;
  }
}
