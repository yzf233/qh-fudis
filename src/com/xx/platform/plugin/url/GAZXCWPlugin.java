package com.xx.platform.plugin.url;

import java.util.List;

import com.xx.platform.core.script.ScriptCrawlTool;
import com.xx.platform.dao.IBase;

import java.util.ArrayList;

/**
 * <p>Title: 港澳资讯 股票/基金 评论 分类</p>
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
public class GAZXCWPlugin
    implements com.xx.platform.plugin.url.UrlGenerator {
  private static List<String> urlList = new ArrayList();

  public List generator() throws Exception {
    return null;
  }

  public void generator(IBase service, InnerURL innerUrl) throws Exception {
    ScriptCrawlTool scriptTool = new ScriptCrawlTool();

    for (int i = 0; urlList.size() > 0; i++) {

      String url = urlList.remove(i);
      String content = null;
      try {
              content = scriptTool.getContent(url, "gb2312");
            }
            catch (Exception ex) {
              ex.printStackTrace();
      }
      List<String> list = scriptTool.getURLByAuto(content, url);
      int num = list != null ? list.size() : 0;
      for (int j = 0; j < num; j++) {
        innerUrl.setUrl(list.remove(0));
        innerUrl.setExtra("docType:gj,subDocType:cw");
        service.saveIObject(innerUrl);
      }
    }
  }

  static {
    urlList.add("http://finance.qq.com/stock/gscwbq01.htm");
    urlList.add("http://www.guminzhijia.com/sccw/index.html");
    urlList.add("http://sccw.news.cnfol.com/");
    urlList.add("http://www.gutx.com/more_news/sccw.htm");
    urlList.add("http://stock.eastmoney.com/channel/1024.html");
    urlList.add("http://finance.sina.com.cn/column/sccw.html");
    urlList.add("http://money.163.com/special/00251LR5/sccw.html");
    urlList.add("http://stock.eastmoney.com/channel/1024.html");

  }

}
