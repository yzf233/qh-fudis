package com.xx.platform.core.script;

import java.util.*;

import org.apache.nutch.db.*;
import org.apache.nutch.fetcher.*;
import org.apache.nutch.io.*;
import org.apache.nutch.net.*;
import org.apache.nutch.pagedb.*;
import org.apache.nutch.parse.*;
import org.apache.nutch.plugin.*;
import org.apache.nutch.protocol.*;
import org.apache.nutch.util.*;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.xx.platform.core.nutch.FetchListTool;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.core.nutch.WebDB;
import com.xx.platform.core.nutch.WebDBWriter;

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


public class FetcherThread
    extends Thread {
  private static final String THREAD_GROUP_NAME = "script";
  private static final ThreadGroup group = new ThreadGroup(THREAD_GROUP_NAME); // our group
  private static final float NEW_INJECTED_PAGE_SCORE =
      NutchConf.get().getFloat("db.score.injected", 2.0f);

  public static final Logger log =
      LogFormatter.getLogger("com.xx.platform.core.script.FetcherThread");
  private List<WebDB> urlList = null;
  private InnerHook inner;
  private static final int MAX_REDIRECT =
      NutchConf.get().getInt("http.redirect.max", 3);

  static {
    System.setProperty("sun.net.client.defaultConnectTimeout", "30000"); //设置HTTP连接请求超时时间
    System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //设置HTTP连接数据读取超时时间

  }

  public FetcherThread(List<WebDB> urlList, InnerHook inner) {
    this.urlList = urlList;
    this.inner = inner;
  }

  private static int threadCount = 0;
  public void run() {
    inner.start();
    int threadNum = 0;
    while (threadCount < 50 && this.urlList.size() > 0) {
      InnerThread innerThread = new InnerThread(THREAD_GROUP_NAME+String.valueOf(threadNum));
      innerThread.start();
      threadCount++;
    }
    while (true) {
      try {
        Thread.sleep(2000);
      }
      catch (InterruptedException ex) {
      }
      int n = group.activeCount();
      Thread[] list = new Thread[n];
      group.enumerate(list);
      boolean noMoreFetcherThread = true; // assumption
      for (int i = 0; i < n; i++) {
        // this thread may have gone away in the meantime
        if (list[i] == null)
          continue;

        String tname = list[i].getName();
        if (tname.startsWith(THREAD_GROUP_NAME)) // prove it
          noMoreFetcherThread = false;
      }
      if (noMoreFetcherThread) {
        break;
      }

    }
    inner.stop();
  }

  class InnerThread
      extends Thread {
    public InnerThread(String name) {
      super(group, name);
    }

    public void run() {
      inner.start();
      FetchListEntry fle = new FetchListEntry();
      WebDB webDb = null;
      String url = null;
      while (urlList != null && urlList.size() > 0) {
        if (LogFormatter.hasLoggedSevere()) // something bad happened
          break; // exit
        synchronized (urlList) {
          webDb = urlList.remove(0);
          url = webDb.getUrl();
        }
        try {
          fle = new FetchListEntry(false,
                                   new Page(webDb.getUrl(), 1.0f, 1,
                                            webDb.getExtra()), new String[] {
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
            Content content = output.getContent();
            switch (pstat.getCode()) {
              case ProtocolStatus.SUCCESS:
                if (content != null) {
                  ParseStatus ps = handleFetch(fle, output);
                  if (ps != null &&
                      ps.getMinorCode() ==
                      ParseStatus.SUCCESS_REDIRECT) {
                    String newurl = ps.getMessage();
                    newurl = URLFilters.filter(newurl);
                    if (newurl != null && !newurl.equals(url)) {
                      refetch = true;
                      url = newurl;
                      redirCnt++;
                      fle = new FetchListEntry(true,
                                               new Page(url,
                          NEW_INJECTED_PAGE_SCORE,
                          fle.getIsparseoutlink()),
                                               new String[0]);
                    }
                    else {
                    }
                  }
                }
                break;
              case ProtocolStatus.MOVED: // try to redirect immediately
              case ProtocolStatus.TEMP_MOVED: // try to redirect immediately

                // record the redirect. perhaps the DB will want to know this.
                handleFetch(fle, output);
                String newurl = pstat.getMessage();
                newurl = URLFilters.filter(newurl);
                if (newurl != null && !newurl.equals(url)) {
                  refetch = true;
                  url = newurl;
                  redirCnt++;
                  // create new entry.
                  fle = new FetchListEntry(true,
                                           new Page(url,
                      NEW_INJECTED_PAGE_SCORE,
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
                handleFetch(fle, output);
                break;
              case ProtocolStatus.EXCEPTION:
                handleFetch(fle, output);
                break;
              default:
                handleFetch(fle, output);
            }
          }
          while (refetch && (redirCnt < MAX_REDIRECT));
        }
        catch (Throwable t) { // an unchecked exception
          t.printStackTrace();
          if (fle != null) {
            handleFetch(fle,
                        new ProtocolOutput(null,
                                           new ProtocolStatus(t)));
          }
        }
        threadCount--;
      }

      return;
    }

    private ParseStatus handleFetch(FetchListEntry fle,
                                    ProtocolOutput output) {
      Content content = output.getContent();

      String inurl = fle.getPage().getURL().toString();
      if (content == null) {
        content = new Content(inurl, inurl, new byte[0], "", new Properties());
      }

      Parser parser = null;
      Parse parse = null;
      ParseStatus status = null;
      try {
        parser = new org.apache.nutch.parse.html.HtmlParser();
        parse = parser.getParse(content);
        status = parse.getData().getStatus();
      }
      catch (Exception e) {
        e.printStackTrace();
        status = new ParseStatus(e);
      }
      if (status.isSuccess()) {
        parse.getData().getOutlinks();
        ScriptCrawlTool sct = new ScriptCrawlTool();
        String encode = parse.getData().get("CharEncodingForConversion");
        encode = encode != null && !encode.trim().equals("") ? encode : "GBK";
        try {
          List<String>
              urlsList = sct.getURLByAuto(new String(content.getContent(),
              encode),
                                          fle.getUrl().toString());
          Page linkPage = null;
          WebDB wedb = null;
          IWebDBWriter webdb = new WebDBWriter();

          for (String url : urlsList) {
            linkPage = new Page(url, 1.0f, 0,
                                fle.getPage() != null && fle.getPage().getExtra() != null ?
                                fle.getPage().getExtra() : "");
            linkPage.setIsFilter(true);
            wedb = webdb.addWebDB(linkPage);
          }
        }
        catch (Exception ex) {
        }
      }
      else {
      }
      return status;
    }
  }
}
