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
public class GAZXPLPlugin
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
        innerUrl.setExtra("docType:gj,subDocType:pl");
        service.saveIObject(innerUrl);
      }
    }
  }

  static {
    urlList.add("http://www.cz318.com.cn/info_new/default.asp?curMenu=9&menuId=-1&ClassID=658001&parentID=656001");
    urlList.add("http://www.htsc.com.cn/study/gsyj.jsp?Nid=17&SNid=337");
    urlList.add("http://www.guodu.com/guodu/public/infolist8.jsp?childClassID=10635&daohang1=4&daohang=公司研究");
    urlList.add("http://www.ehongyuan.com/news/more.jsp?hynav=consultation&menuId=4&parentId=1947&catId=1947");
    urlList.add("http://www.cjis.cn/cjis/homepage/research/index.jsp?index=4");
    urlList.add("http://www.ghzq.com.cn/ghresearch/list.asp?boardid=43");
    urlList.add(
        "http://www.ccnew.com/infocenter/InfoMore.jsp?catName=ccs_company_research");
    urlList.add("http://www.thope.com.cn/thope/xtsd/index_sd.jsp");
    urlList.add(
        "http://www.thope.com.cn/thope/xtsd/index_sd.jsp?classID2=10217");
    urlList.add(
        "http://www.glsc.com.cn/glnews/list.jsp?boardID=149&boardParentID=44");
    urlList.add(
        "http://www.glsc.com.cn/glnews/list.jsp?boardID=153&boardParentID=44");
    urlList.add(
        "http://www.glsc.com.cn/glnews/list.jsp?boardID=156&boardParentID=44");
    urlList.add("http://www.wlzq.com.cn/wlnewV2/c/home/index_html");
    urlList.add("http://www.hczq.com/index.php?name=华创视点");
    urlList.add("http://www.qlzq.com.cn/xinxi/qlks_ggtj.jsp");
    urlList.add(
        "http://www.china598.com/info/info_research_gsfx_touyan11.asp?ID=m34");
    urlList.add(
        "http://www.nesc.cn/jsp/front/Study/StudyMore.jsp?catname=company_research#");
    urlList.add("http://www.dtsbc.com.cn/News/NewsList.aspx?Cat_ID=8");
    urlList.add(
        "http://www.mszq.com/mszq/study/index.jsp?order=4&classid=22394");
    urlList.add(
        "http://221.192.130.195/cdzq2/index/cdtzfw_list.jsp?classid=0002000400010002");
    urlList.add("http://www.ewww.com.cn/bhyjs/More.aspx?TypeID=020604");
    urlList.add("http://www.ewww.com.cn/bhyjs/More.aspx?TypeID=020606");
    urlList.add("http://www.cnstock.com/stock/focus/");
    urlList.add("http://www.cnstock.com/stock/best/");
    urlList.add("http://www.cnstock.com/livenews/index.htm");
    urlList.add("http://www.cnstock.com/stock/block/");
    urlList.add("http://www.cnstock.com/stock/gegu/");
    urlList.add("http://www.cnstock.com/gupiao/moring/index.htm");
    urlList.add("http://www.cnstock.com/stock/forward/");
    urlList.add("http://www.cnstock.com/stock/personality/");
    urlList.add("http://www.cs.com.cn/ssgs/03/");
    urlList.add("http://www.cs.com.cn/ssgs/04/");
    urlList.add("http://www.cs.com.cn/pl/05/");
    urlList.add("http://210.22.10.229/gpzq/");
    urlList.add("http://210.22.10.229/zzkp/");
    urlList.add("http://210.22.10.229/ggjsb/");
    urlList.add("http://210.22.10.229/hmdjt/");
    urlList.add("http://www.p5w.net/stock/hydx/ggdp/index.htm");
    urlList.add("http://www.p5w.net/stock/news/zonghe/");
    urlList.add("http://news.thebeijingnews.com/newslist.htm?id=725");
    urlList.add("http://chinese.wsj.com/gb/stk.asp");
    urlList.add("http://www.cesnew.com/list.aspx?cid=8");
    urlList.add("http://zhoukan.hexun.com/");
    urlList.add("http://www.nbd.com.cn/ClassNews.asp?D_SClassID=79");
    urlList.add("http://finance.sina.com.cn/column/ggdp.shtml");
    urlList.add("http://business.sohu.com/7/0702/01/column202400129.shtml");
    urlList.add("http://business.sohu.com/7/0803/30/column211933091.shtml");
    urlList.add("http://business.sohu.com/7/0102/77/column200117776.shtml");
    urlList.add("http://business.sohu.com/7/0102/86/column200118666.shtml");
    urlList.add("http://money.163.com/special/g/00251LR5/gptj.html");
    urlList.add("http://money.163.com/special/00252630/bkdianping.html");
    urlList.add("http://stock.eastmoney.com/channel/1019.html");
    urlList.add("http://stock.eastmoney.com/channel/1033.html");
    urlList.add("http://www.cnstock.com/gupiao/ggpj/index.htm");
    urlList.add("http://www.cnstock.com/stock/forward");
    urlList.add("http://210.22.10.229/ggjsb/");
    urlList.add("http://210.22.10.229/tzpj/");
    urlList.add("http://210.22.10.229/hmdjt/");
    urlList.add("http://www.p5w.net/stock/hydx/ggdp/index.htm");
    urlList.add("http://www.p5w.net/stock/lzft/gsyj/");
    urlList.add("http://www.p5w.net/stock/lzft/zaiyao/");
    urlList.add(
        "http://www.eeo.com.cn/research_report/firm_research/index.html");
    urlList.add(
        "http://www.china-cbn.com/Stock/StockNewsList.aspx?NewsChannelId=001008");
    urlList.add("http://finance.sina.com.cn/column/companyresearch.shtml");
    urlList.add("http://finance.sina.com.cn/column/ggdp.shtml");
    urlList.add("http://stock.eastmoney.com/channel/1028.html");
    urlList.add("http://stock.eastmoney.com/channel/1030.html");
    urlList.add("http://web5.jrj.com.cn/NewsList.Aspx?type=ggyj");
    urlList.add("http://web5.jrj.com.cn/NewsList.Aspx?type=ggdj");
    urlList.add("http://stock.hexun.com/stock/research/");
    urlList.add("http://cn.biz.yahoo.com/special/ggxw/index.html");
    urlList.add("http://business.sohu.com/7/0102/86/column200118666.shtml");
    urlList.add("http://business.sohu.com/7/0102/77/column200117776.shtml");
    urlList.add("http://money.163.com/special/00251LR5/gsdy.html");
    urlList.add("http://money.163.com/special/g/00251LR5/gptj.html");
    urlList.add("http://news.eefoo.com/newsmorelist.do?key=jgjg");

  }

}
