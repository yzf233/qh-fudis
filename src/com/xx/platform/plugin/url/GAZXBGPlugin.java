package com.xx.platform.plugin.url;

import java.util.List;
import org.apache.nutch.db.Page;
import java.util.ArrayList;
import org.apache.nutch.util.LogFormatter;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.nutch.WebDB;
import com.xx.platform.core.script.FetcherThread;
import com.xx.platform.core.script.InnerHook;
import com.xx.platform.core.script.ScriptCrawlTool;
import com.xx.platform.dao.IBase;

/**
 * <p>Title: 港澳资讯 股票/基金 报告 分类</p>
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
public class GAZXBGPlugin
    implements com.xx.platform.plugin.url.UrlGenerator {
  private static final String[][] urls = new String[1047][3];

  public List generator() throws Exception {
    return null;
  }

  public void generator(IBase service, InnerURL innerUrl) throws Exception {
    List<WebDB> urlsList = new ArrayList() ;
    Page page = null ;
    for (int i = 0; i<urls.length; i++) {

      String url = urls[i][2];
      page = new Page(url , 1.0f , 1 ,"docType:gj,subDocType:gpxw,chuchu:"+urls[i][1]+",gupiao:"+urls[i][0]) ;
      urlsList.add(new WebDB(page)) ;
//      String content = null;
//      try {
//        content = scriptTool.getContent(url, "gb2312");
//      }
//      catch (Exception ex) {
//        ex.printStackTrace();
//      }
//      List<String> list = scriptTool.getURLByAuto(content, url);
//      int num = list != null ? list.size() : 0;
//      for (int j = 0; j < num; j++) {
//        innerUrl.setUrl(list.remove(0));
//        innerUrl.setExtra("docType:gj,subDocType:ga");
//        service.saveIObject(innerUrl);
//      }
    }
    FetcherThread ft = new FetcherThread(urlsList,new InnerHook(){
      public void start() {
        SearchContext.URL_GENERATOR_RUNNING = true ;
      }

      public void stop() {
        SearchContext.URL_GENERATOR_RUNNING = false;
        LogFormatter.getLogger("").info("地址发生器插件完成......");
        try {
          WebDbAdminTool.reloadReader();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }) ;
    ft.start();
    while(SearchContext.URL_GENERATOR_RUNNING)
      Thread.sleep(1000);
  }
  public static void main(String[] args)
  {
    GAZXBGPlugin plugin = new GAZXBGPlugin();
    try {
      plugin.generator(null, null);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  static {
    urls[0] = new String[] {
        "000567", "海德股份", "http://000567.hnbilun.com/cgi-bin/index.dll?column9?webid]=hnbilun&userid]=1888069&columnno]=24&pageno]=0"};
    urls[1] = new String[] {
        "001696", "宗申动力",
        "http://218.201.40.192/zongshen/www/cn/top4/top4-1.jsp"};
    urls[2] = new String[] {
        "600331", "宏达股份", "http://218.6.161.66/hdgf/news/gb/list_2_1.html"};
    urls[3] = new String[] {
        "600750", "江中药业", "http://218.65.95.164/web/index.asp?tab]=1&bigclass_id]=8&bigclass_name]=%BD%AD%D6%D0%C8%C8%D1%B6"};
    urls[4] = new String[] {
        "000810", "华润锦华", "http://221.10.36.139/Chinese/Bs_News.asp?Action]=Co"};
    urls[5] = new String[] {
        "600466", "*ST迪康",
        "http://222.209.223.224:81/dk/publicPage.jsp?contentId]=5&pageIndex]=1"};
    urls[6] = new String[] {
        "600583", "海油工程",
        "http://60.28.77.221:8080/haiyou/4xwzx/4xwzx_1xwfb.jsp"};
    urls[7] = new String[] {
        "000677", "山东海龙", "http://61.133.99.98/pub/HAILONGJT/XWZX/default.htm"};
    urls[8] = new String[] {
        "600969", "郴电国际",
        "http://61.187.187.19:8080/chinacdi/news/news.jsp?menuId]=4"};
    urls[9] = new String[] {
        "601998", "中信银行", "http://bank.ecitic.com/about/news.jsp"};
    urls[10] = new String[] {
        "000417", "合肥百货", "http://bd.hfbh.com.cn/news.asp"};
    urls[11] = new String[] {
        "600320", "振华港机", "http://cn.zpmc.com/article_list.asp?column_id]=40"};
    urls[12] = new String[] {
        "600530", "交大昂立",
        "http://comp.onlly.cn/v2003/gongsi/functionmodel/shichang/NEWS/NEWS_VIEW.ASP"};
    urls[13] = new String[] {
        "002095", "网盛科技", "http://corp.netsun.com/news/zxdt/index.html"};
    urls[14] = new String[] {
        "000584", "舒卡股份", "http://irm.p5w.net/000584/GSDT/GD.html"};
    urls[15] = new String[] {
        "000667", "名流置业", "http://irm.p5w.net/000667/GSDT/GD.html"};
    urls[16] = new String[] {
        "600287", "江苏舜天", "http://irm.p5w.net/600287/GSDT/GD.html"};
    urls[17] = new String[] {
        "600873", "五洲明珠", "http://minovo.cn/Release/list.asp?id]=6"};
    urls[18] = new String[] {
        "600052", "*ST广厦",
        "http://news.gsgf.com/bin/newsroom/index/guangsha_1.html"};
    urls[19] = new String[] {
        "600712", "南宁百货", "http://news.nnbh.cn/news/news_2_1.htm"};
    urls[20] = new String[] {
        "600085", "同仁堂", "http://news.tongrentang.com/cgi-bin/visitor/page.cgi?class]=1&template]=page_template"};
    urls[21] = new String[] {
        "600689", "上海三毛", "http://shsanmao.sds.cn/sanmao/ju1.jsp"};
    urls[22] = new String[] {
        "600826", "兰生股份", "http://test.dsh.cn/lansheng/news.asp"};
    urls[23] = new String[] {
        "600562", "高淳陶瓷", "http://test2.heeyee.cn/news.php"};
    urls[24] = new String[] {
        "600725", "云维股份", "http://web.ywgf.cn/ywHome.nsf/Cnpage?openPage"};
    urls[25] = new String[] {
        "000502", "绿景地产", "http://www.000502.cn/admin/news.asp"};
    urls[26] = new String[] {
        "000509", "S*ST华塑", "http://www.000509.com/home/news.asp"};
    urls[27] = new String[] {
        "000551", "创元科技", "http://www.000551.cn/web-ia/web/index.asp"};
    urls[28] = new String[] {
        "000639", "金德发展", "http://www.000639.com/News.asp"};
    urls[29] = new String[] {
        "000669", "领先科技", "http://www.000669.com/Articles.aspx?cid]=2&mt]=1"};
    urls[30] = new String[] {
        "000793", "华闻传媒",
        "http://www.000793.com/affiche/news.php?news_class_code]=01"};
    urls[31] = new String[] {
        "000860", "顺鑫农业", "http://www.000860.com/news_center/catalog.asp"};
    urls[32] = new String[] {
        "000861", "海印股份", "http://www.000861.com/news_02.asp"};
    urls[33] = new String[] {
        "000889", "渤海物流", "http://www.000889.cn/file/zhongda/index.htm"};
    urls[34] = new String[] {
        "000899", "赣能股份", "http://www.000899.com/mainpages/lanm_XinWen.aspx"};
    urls[35] = new String[] {
        "000040", "深鸿基", "http://www.0040.com.cn/update.asp"};
    urls[36] = new String[] {
        "000507", "粤富华", "http://www.0507.com.cn/more.asp"};
    urls[37] = new String[] {
        "000615", "湖北金环", "http://www.0615.cn/news1.asp?smallclass]=公司新闻"};
    urls[38] = new String[] {
        "600639", "浦东金桥", "http://www.58991818.com/website/news/newsMain.jsp"};
    urls[39] = new String[] {
        "600064", "南京高科", "http://www.600064.com/newslist.php?newstype]=143"};
    urls[40] = new String[] {
        "600083", "*ST博讯", "http://www.600083.com/news.asp"};
    urls[41] = new String[] {
        "600168", "武汉控股",
        "http://www.600168.com.cn/news.asp?owen1]=新闻中心&owen2]=公司新闻"};
    urls[42] = new String[] {
        "600193", "创兴科技", "http://www.600193.com/news.asp"};
    urls[43] = new String[] {
        "600219", "南山铝业", "http://www.600219.com.cn/new/index.htm"};
    urls[44] = new String[] {
        "600282", "南钢股份",
        "http://www.600282.net/List_News.asp?Class]=1&News_Xclass]=1"};
    urls[45] = new String[] {
        "600339", "天利高新", "http://www.600339.com:8080/tlgx/dtxw/dtxw.jsp"};
    urls[46] = new String[] {
        "600396", "金山股份", "http://www.600396.com/webnews/jrjs/jrjs1.htm"};
    urls[47] = new String[] {
        "600463", "空港股份",
        "http://www.600463.com.cn/cn/news/index_g1.asp?id]=374&g]=新闻中心&grp]=8"};
    urls[48] = new String[] {
        "600496", "长江精工", "http://www.600496.com/new.asp"};
    urls[49] = new String[] {
        "600610", "S*ST中纺", "http://www.600610.com/news/more.asp?typeid]=9"};
    urls[50] = new String[] {
        "600614", "*ST鼎立", "http://www.600614.com/news_lately.aspx"};
    urls[51] = new String[] {
        "600617", "联华合纤", "http://www.600617.cn/news.asp"};
    urls[52] = new String[] {
        "600638", "新黄浦", "http://www.600638.com/comote/main.htm"};
    urls[53] = new String[] {
        "600683", "银泰股份", "http://www.600683.com/news.php?class_name]=公司动态"};
    urls[54] = new String[] {
        "600733", "S前锋",
        "http://www.600733.com.cn/news/list.asp?ClassID]=02010102"};
    urls[55] = new String[] {
        "600768", "宁波富邦", "http://www.600768.com.cn/2j.asp?id]=41&cid]=51"};
    urls[56] = new String[] {
        "600795", "国电电力", "http://www.600795.com.cn/gddlwww/gongsiyaowen.jsp"};
    urls[57] = new String[] {
        "600840", "新湖创业", "http://www.600840.com.cn/venture/xinwen.asp"};
    urls[58] = new String[] {
        "600869", "三普药业", "http://www.600869.com/new/newslist.asp?AL_SORT]=1"};
    urls[59] = new String[] {
        "600895", "张江高科", "http://www.600895.com/htmls/news_media.asp"};
    urls[60] = new String[] {
        "600662", "强生控股", "http://www.62580000.com.cn/new.asp"};
    urls[61] = new String[] {
        "600741", "巴士股份",
        "http://www.84000.com.cn/newslist.aspx?categoryID]=1&Div]=Menu1"};
    urls[62] = new String[] {
        "600611", "大众交通", "http://www.96822.com/dz-news/index.asp"};
    urls[63] = new String[] {
        "000999", "S三九",
        "http://www.999.com.cn/Portals/Portal999/news/news.aspx"};
    urls[64] = new String[] {
        "600207", "*ST安彩",
        "http://www.acbc.com.cn/acbcnews.asp?parentid]=3&columnid]=68"};
    urls[65] = new String[] {
        "600469", "风神股份", "http://www.aeolustyre.com/news.asp"};
    urls[66] = new String[] {
        "600677", "航天通信", "http://www.aerocom.cn/news/newsmore1.asp?id]=1"};
    urls[67] = new String[] {
        "600501", "航天晨光", "http://www.aerosun.cn/news.asp"};
    urls[68] = new String[] {
        "600971", "恒源煤电", "http://www.ahhymd.com.cn/info/dispatchInfoAction.do?action]=list&progid]=4028b18506769e1b01067941692d01cb&view]=true&Forward]=ViewMoreType2"};
    urls[69] = new String[] {
        "600255", "鑫科材料",
        "http://www.ahxinke.com/index.nsf/$$ViewTemplate+for+forwai?OpenForm"};
    urls[70] = new String[] {
        "601111", "中国国航",
        "http://www.airchina.com.cn/maintenance/ghxw.jsp?location]=gb"};
    urls[71] = new String[] {
        "600773", "*ST雅砻",
        "http://www.alongtibet.com/news/gb/index.asp?bigclassname]=雅砻动态"};
    urls[72] = new String[] {
        "600057", "夏新电子", "http://www.amoi.com.cn/html/news_other.asp?cid]=2"};
    urls[73] = new String[] {
        "002136", "安纳达", "http://www.andty.com/news/index.asp"};
    urls[74] = new String[] {
        "600298", "安琪酵母", "http://www.angel.com.cn/angel-news.htm"};
    urls[75] = new String[] {
        "600012", "皖通高速", "http://www.anhui-expressway.cn/infocn/infocn.asp"};
    urls[76] = new String[] {
        "000868", "安凯客车", "http://www.ankai.com/ankai2006/news.asp"};
    urls[77] = new String[] {
        "000898", "鞍钢股份", "http://www.ansteel.com.cn/main/gsdt.jsp"};
    urls[78] = new String[] {
        "600408", "安泰集团", "http://www.antaigroup.com/docc/news/comnews.asp"};
    urls[79] = new String[] {
        "600816", "安信信托",
        "http://www.anxintrust.com/cn/aboutus/news_corp.asp?nwtypeid]=3"};
    urls[80] = new String[] {
        "600397", "安源股份", "http://www.anyuan1999.com/Chinese/xr_News.asp"};
    urls[81] = new String[] {
        "000739", "普洛康裕", "http://www.apeloa.com/cgi/search-cn.cgi?f]=news_cn+company_cn_1_&t]=news_cn2&cate1]=%C9%CF%CA%D0%B9%AB%CB%BE%B6%AF%CC%AC&Submit]=Search"};
    urls[82] = new String[] {
        "000922", "*ST阿继", "http://www.arc.com.cn/todayARC/news.htm"};
    urls[83] = new String[] {
        "600855", "航天长峰", "http://www.ascf.com.cn/news/qiye.asp"};
    urls[84] = new String[] {
        "000969", "安泰科技", "http://www.atmcn.com/Chinese/News/news.asp"};
    urls[85] = new String[] {
        "600336", "澳柯玛", "http://www.aucmahitech.com/web/xinwen_1.asp"};
    urls[86] = new String[] {
        "000918", "S*ST亚华", "http://www.avaholdings.com/gsdt/yhdt.asp"};
    urls[87] = new String[] {
        "600569", "安阳钢铁",
        "http://www.aysteel.com.cn/xinwen/readnews.asp?b_id]=1&flmdm]=061117170024"};
    urls[88] = new String[] {
        "600865", "S百大", "http://www.baidagroup.com/baidagroup/xwzx/xwzx.jsp"};
    urls[89] = new String[] {
        "000949", "新乡化纤", "http://www.bailu.com/news.asp"};
    urls[90] = new String[] {
        "600004", "白云机场",
        "http://www.baiyunairport.com/jcNewsServlet?jumpPage]=1"};
    urls[91] = new String[] {
        "601328", "交通银行", "http://www.bankcomm.com/jh/cn/more.jsp?categoryStr]=ROOT%253E%25D6%25D0%25CE%25C4%253E%25D0%25C5%25CF%25A2%25B7%25D6%25C7%25F8%253E%25BD%25BB%25D0%25D0%25D0%25C2%25CE%25C5&showTime]=y&showNewPic]=y&c]=1121231485100&"};
    urls[92] = new String[] {
        "600379", "S宝光", "http://www.baoguang.com.cn/cn/News.asp?channel_id]=9"};
    urls[93] = new String[] {
        "600988", "*ST宝龙",
        "http://www.baolong.com.cn/baolong/News.asp?ClassID]=11"};
    urls[94] = new String[] {
        "600845", "宝信软件", "http://www.baosight.com/companynews.do?flag]=news"};
    urls[95] = new String[] {
        "600019", "宝钢股份",
        "http://www.baosteel.com/plc/02news/ShowClass.asp?ClassID]=8"};
    urls[96] = new String[] {
        "600456", "宝钛股份",
        "http://www.baoti.com/chinese/main/news/news_list.asp"};
    urls[97] = new String[] {
        "000153", "丰原药业", "http://www.bbcayy.com/news.asp?class_id]=12&class_name]=%C6%F3%D2%B5%B6%AF%CC%AC"};
    urls[98] = new String[] {
        "600258", "首旅股份", "http://www.bct2000.com/news.asp"};
    urls[99] = new String[] {
        "601588", "北辰实业", "http://www.beijingns.com.cn/"};
    urls[100] = new String[] {
        "600860", "北人股份", "http://www.beirengf.com/JianTiZW/Menu.asp?DM]=2"};
    urls[101] = new String[] {
        "600705", "S*ST北亚", "http://www.beiya.com.cn/News/news.asp"};
    urls[102] = new String[] {
        "600171", "上海贝岭",
        "http://www.belling.com.cn/col150/col175/index.htm1?id]=175"};
    urls[103] = new String[] {
        "600468", "百利电气",
        "http://www.benefo.tj.cn/SmallClass.asp?BigClassName]=公司新闻&SmallClassName]=公司动态"};
    urls[104] = new String[] {
        "600037", "歌华有线", "http://www.bgctv.com.cn/65/more/73/73more_1.htm"};
    urls[105] = new String[] {
        "600960", "滨州活塞", "http://www.bhpiston.com/newsmore.asp?NewsTypeID]=0"};
    urls[106] = new String[] {
        "000582", "北海港",
        "http://www.bhport.cn/main.do?show]=subject&subjectLeveId]=004001"};
    urls[107] = new String[] {
        "000695", "滨海能源", "http://www.binhaienergy.com/"};
    urls[108] = new String[] {
        "000627", "天茂集团", "http://www.biocause.com/new/news/news.htm"};
    urls[109] = new String[] {
        "600226", "升华拜克", "http://www.biok.com/news/biok/"};
    urls[110] = new String[] {
        "002066", "瑞泰科技", "http://www.bjruitai.com/default1.asp"};
    urls[111] = new String[] {
        "000802", "北京旅游", "http://www.bj-tour.com.cn/web/mid/news"};
    urls[112] = new String[] {
        "000786", "北新建材", "http://www.bnbm.com.cn/news/category.asp?cate]=1"};
    urls[113] = new String[] {
        "601988", "中国银行", "http://www.boc.cn/cn/common/fourth.jsp?category]=ROOT%3E%D6%D0%D0%D0%D7%DC%D0%D0%3E%B9%D8%D3%DA%D6%D0%D0%D0%3E%D6%D0%D0%D0%D0%C2%CE%C5"};
    urls[114] = new String[] {
        "600289", "亿阳信通", "http://www.boco.com.cn/boco/cn/newslist.asp"};
    urls[115] = new String[] {
        "000725", "*ST东方A",
        "http://www.boe.com.cn/0821/cn/news/NewsMore.aspx?catalogid]=2"};
    urls[116] = new String[] {
        "600966", "博汇纸业", "http://www.bohui.net/info/default.asp"};
    urls[117] = new String[] {
        "600880", "博瑞传播", "http://www.b-raymedia.com/newEbiz1/EbizPortalFG/portal/html/InfoFocusMultiPage.html?InfoFocusList150_action]=List"};
    urls[118] = new String[] {
        "600597", "光明乳业", "http://www.brightdairy.com/main/news_all.php"};
    urls[119] = new String[] {
        "600556", "ST北生",
        "http://www.bsyy.com.cn/DesktopDefault.aspx?tabid]=999"};
    urls[120] = new String[] {
        "600266", "北京城建", "http://www.bucid.com/news/index.shtml"};
    urls[121] = new String[] {
        "600455", "交大博通", "http://www.butone.com/more_huodong.php"};
    urls[122] = new String[] {
        "600581", "八一钢铁", "http://www.bygt.com.cn/news/gsxwlist.jsp"};
    urls[123] = new String[] {
        "600195", "中牧股份", "http://www.cahic.com.cn/otype.asp?owen1]=企业新闻"};
    urls[124] = new String[] {
        "600502", "安徽水利", "http://www.cahsl.com/cnews.asp"};
    urls[125] = new String[] {
        "600375", "星马汽车", "http://www.camc.biz/info/report/"};
    urls[126] = new String[] {
        "002051", "中工国际",
        "http://www.camce.com.cn/cn/articlelist/article_3_adddate_desc_1.asp"};
    urls[127] = new String[] {
        "600008", "首创股份",
        "http://www.capitalwater.cn/info/listinfo.asp?class]=5"};
    urls[128] = new String[] {
        "000004", "ST国农", "http://www.cau-tech.com/news/news.htm"};
    urls[129] = new String[] {
        "600415", "小商品城", "http://www.cccgroup.com.cn/Active.asp?id]=2"};
    urls[130] = new String[] {
        "000661", "长春高新", "http://www.cchn.com.cn/ziye/xinwendongtai.asp"};
    urls[131] = new String[] {
        "000504", "赛迪传媒", "http://www.ccidmedia.com/infolist.asp?children]=0&parentID]=0&infosortID]=16&pSort]=service"};
    urls[132] = new String[] {
        "000042", "深长城", "http://www.cctzkg.com/gw_news/index.asp"};
    urls[133] = new String[] {
        "600148", "长春一东", "http://www.ccyd.com.cn/yidongdongtai.asp"};
    urls[134] = new String[] {
        "600357", "承德钒钛",
        "http://www.cdft.com.cn/web/index.asp?classid]=10&Nclassid]=10"};
    urls[135] = new String[] {
        "000809", "中汇医药",
        "http://www.cdzhonghui.com/News/newslist.asp?class]=15"};
    urls[136] = new String[] {
        "600115", "东方航空", "http://www.ce-air.com/cea2/zh_CN/eastern/news/company_news/0,15153,500301,00.html?sid]=500301"};
    urls[137] = new String[] {
        "600764", "中电广通", "http://www.cecgt.com/d3.htm"};
    urls[138] = new String[] {
        "600675", "中华企业", "http://www.cecl.com.cn/news01.asp"};
    urls[139] = new String[] {
        "000935", "S川双马", "http://www.cement.com.cn/news"};
    urls[140] = new String[] {
        "000931", "中关村", "http://www.centek.com.cn/news.htm"};
    urls[141] = new String[] {
        "600176", "中国玻纤", "http://www.cfgcl.com.cn/morereport.htm"};
    urls[142] = new String[] {
        "600991", "长丰汽车", "http://www.cfmotors.com/docc/news/news.asp"};
    urls[143] = new String[] {
        "000987", "广州友谊", "http://www.cgzfs.com/NewsClass.asp?BigClass]=最新动态"};
    urls[144] = new String[] {
        "601600", "中国铝业", "http://www.chalco.com.cn/chalco.3w/chinese/pages/index2.jsp?channelid]=40&siteid]=0001"};
    urls[145] = new String[] {
        "000972", "新中基",
        "http://www.chalkistomato.com/main/SuperCMS.asp?typeid]=1"};
    urls[146] = new String[] {
        "000625", "长安汽车", "http://www.changan.com.cn/News.htm"};
    urls[147] = new String[] {
        "000570", "苏常柴Ａ",
        "http://www.changchai.com.cn/04_news/index_01.asp?typeid]=0"};
    urls[148] = new String[] {
        "600372", "昌河股份", "http://www.changheauto.com/xwzx/xwzx_qyxw.htm"};
    urls[149] = new String[] {
        "600839", "四川长虹",
        "http://www.changhong.com.cn/changhong/china/7944.htm"};
    urls[150] = new String[] {
        "600710", "常林股份", "http://www.changlin.com.cn/clweb.nsf/webadmin.fm?open&fid]=1A9E6F3DBBC9E1CF48257264002AA81E"};
    urls[151] = new String[] {
        "000561", "SST长岭",
        "http://www.changling.com.cn/News.asp?channel_id]=13"};
    urls[152] = new String[] {
        "000158", "常山股份", "http://www.changshantex.com/newsroom.asp"};
    urls[153] = new String[] {
        "600706", "*ST长信",
        "http://www.changxin.com/news/index2.asp?typeid]=60&borderid]=109"};
    urls[154] = new String[] {
        "000869", "张裕Ａ",
        "http://www.changyu.com.cn/chinese/history/newsmore_company.asp"};
    urls[155] = new String[] {
        "600525", "长园新材", "http://www.changyuan.com/news/list.asp?catid]=244"};
    urls[156] = new String[] {
        "600739", "辽宁成大", "http://www.chengda.com.cn/chengda/news/event.asp"};
    urls[157] = new String[] {
        "000990", "诚志股份", "http://www.chengzhi.com.cn/news/cznews/default.asp?categoryid]=3&data]=1&page]=1&time]=1&id]=5&ako]=ddd"};
    urls[158] = new String[] {
        "000488", "晨鸣纸业",
        "http://www.chenmingpaper.com/xxlr.asp?tab]=&menuid]=241&menujb]=3"};
    urls[159] = new String[] {
        "000009", "S深宝安A", "http://www.chinabaoan.com/dongtai.asp"};
    urls[160] = new String[] {
        "600153", "建发股份", "http://www.chinacdc.com/gb/news/"};
    urls[161] = new String[] {
        "000881", "大连国际", "http://www.china-cdig.com/xwzx/xwzx.htm"};
    urls[162] = new String[] {
        "000099", "中信海直", "http://www.china-cohc.com/03news/01companynews.asp"};
    urls[163] = new String[] {
        "002009", "天奇股份", "http://www.chinaconveyor.com/news/class1.htm"};
    urls[164] = new String[] {
        "600510", "黑牡丹",
        "http://www.chinadenim.com/update/more.asp?type]=company"};
    urls[165] = new String[] {
        "600565", "迪马股份", "http://www.chinadima.com/dima/newslist?classid]=1"};
    urls[166] = new String[] {
        "002056", "横店东磁",
        "http://www.chinadmegc.com/chinadmegc/chinese/web/about5_1.asp"};
    urls[167] = new String[] {
        "600136", "*ST道博", "http://www.china-double.com/dbxw/dbxw/dbxw1.htm"};
    urls[168] = new String[] {
        "600295", "鄂尔多斯", "http://www.chinaerdos.com/chinese/news/Default.asp"};
    urls[169] = new String[] {
        "000681", "ST远东", "http://www.chinafareast.com/news.asp"};
    urls[170] = new String[] {
        "600615", "ST丰华", "http://www.chinafenghwa.com.cn/newEbiz1/EbizPortalFG/portal/html/InfoListMultiPage.html?folderID]=c373e9075bfff1d88fef99b0a402d7bf"};
    urls[171] = new String[] {
        "600599", "*ST花炮",
        "http://www.chinafirework.cn/e_news.asp?BigClassID]=100"};
    urls[172] = new String[] {
        "600612", "第一铅笔", "http://www.chinafirstpencil.com/news"};
    urls[173] = new String[] {
        "000541", "佛山照明",
        "http://www.chinafsl.com/News/News.asp?cateiD]=News_Company"};
    urls[174] = new String[] {
        "000926", "福星科技", "http://www.chinafxkj.com.cn/a_news.php"};
    urls[175] = new String[] {
        "000902", "中国服装",
        "http://www.chinagarments.net.cn/cn/news/title.asp?newstype]=公司新闻"};
    urls[176] = new String[] {
        "600685", "广船国际", "http://www.chinagsi.com/cn/news/index.asp"};
    urls[177] = new String[] {
        "000822", "山东海化",
        "http://www.chinahaihua.com/lm21.asp?typeid]=21&bigclassid]=30&smallclassid]=33"};
    urls[178] = new String[] {
        "600730", "中国高科", "http://www.chinahitech.com.cn/news.php?classid]=2"};
    urls[179] = new String[] {
        "002062", "宏润建设", "http://www.chinahongrun.com/hongrun/news.asp"};
    urls[180] = new String[] {
        "600343", "航天动力",
        "http://www.china-htdl.com:8081/system/_owners/htdl/_webprj/sys_xwzx.jsp"};
    urls[181] = new String[] {
        "000850", "华茂股份", "http://www.chinahuamao.net/news.asp"};
    urls[182] = new String[] {
        "000801", "四川湖山",
        "http://www.china-hushan.com/admin/showclass1.asp?classid]=2"};
    urls[183] = new String[] {
        "000056", "深国商", "http://www.china-ia.com/webapp/main4.asp?sid]=119"};
    urls[184] = new String[] {
        "600180", "九发股份", "http://www.china-jiufa.com/"};
    urls[185] = new String[] {
        "000880", "ST巨力", "http://www.chinajuli.com/juli/news/newslist.asp"};
    urls[186] = new String[] {
        "000939", "凯迪电力", "http://www.china-kaidi.com/kddt/kdyw.asp"};
    urls[187] = new String[] {
        "000035", "*ST科健", "http://www.chinakejian.net/news/group/index.asp"};
    urls[188] = new String[] {
        "600123", "兰花科创",
        "http://www.chinalanhua.com/news/default.asp?lx]=%B9%AB%CB%BE%D0%C2%CE%C5"};
    urls[189] = new String[] {
        "000532", "力合股份", "http://www.chinalihe.com/news2.asp"};
    urls[190] = new String[] {
        "600186", "莲花味精", "http://www.chinalotus.com.cn/NewsList.asp?Sid]=29"};
    urls[191] = new String[] {
        "600175", "美都控股",
        "http://www.chinameidu.com/hotnews/news06-newcenter.htm"};
    urls[192] = new String[] {
        "600868", "梅雁水电", "http://www.chinameiyan.com/NewWeb/myNews.asp"};
    urls[193] = new String[] {
        "600262", "北方股份",
        "http://www.chinanhl.com/info/SortContent.asp?sortid]=334"};
    urls[194] = new String[] {
        "600811", "东方集团", "http://www.china-orient.com/news/news.htm"};
    urls[195] = new String[] {
        "000779", "ST派神", "http://www.chinapaishen.com/dt_gsgs1_z.asp"};
    urls[196] = new String[] {
        "600775", "南京熊猫", "http://www.chinapanda.com.cn/pandaouterweb/newscreate/news_more.asp?lm]=&lm2]=91&open]=_blank&tj]=0&hot]=0&lineh]=8&alert]=1&top]=50"};
    urls[197] = new String[] {
        "600792", "马龙产业",
        "http://www.chinaphos.cn/info/sortContent.asp?sortId]=344"};
    urls[198] = new String[] {
        "002131", "利欧股份", "http://www.chinapumps.com/news.asp?type]=公司新闻"};
    urls[199] = new String[] {
        "002002", "江苏琼花", "http://www.chinaqionghua.com/qykx.php"};
    urls[200] = new String[] {
        "600104", "上海汽车", "http://www.china-sa.com/cn/main4_news.htm"};
    urls[201] = new String[] {
        "600640", "中卫国脉", "http://www.chinasatcomgm.com/xwzx/xwzx.htm"};
    urls[202] = new String[] {
        "600835", "上海机电", "http://www.chinasec.cn/news.asp"};
    urls[203] = new String[] {
        "000777", "中核科技", "http://www.chinasufa.com/news/newslist.asp?class]=1"};
    urls[204] = new String[] {
        "000813", "天山纺织", "http://www.chinatianshan.com/xwzx/gsxw.asp"};
    urls[205] = new String[] {
        "600520", "三佳科技",
        "http://www.chinatrinity.com/aboutus/aboutsunka.aspx?catagory]=25"};
    urls[206] = new String[] {
        "600702", "沱牌曲酒",
        "http://www.chinatuopai.com/news2/newslist.asp?bigclassid]=1&smallclassid]=1"};
    urls[207] = new String[] {
        "600050", "中国联通", "http://www.chinatypical.com/defaultroot/typical/front/jtzx.go?id]=9&frontNode]=jtxw&flow]=1"};
    urls[208] = new String[] {
        "600302", "标准股份", "http://www.chinatypical.com/defaultroot/typical/front/jtzx.go?id]=9&frontNode]=jtxw&flow]=1"};
    urls[209] = new String[] {
        "600247", "物华股份", "http://www.china-well.com/cn/02_1.php"};
    urls[210] = new String[] {
        "002016", "威尔科技", "http://www.china-well.com/cn/02_1.php?sort2]=1"};
    urls[211] = new String[] {
        "000797", "中国武夷", "http://www.chinawuyi.com.cn/news"};
    urls[212] = new String[] {
        "002015", "霞客环保", "http://www.chinaxiake.com/bar7.htm"};
    urls[213] = new String[] {
        "600692", "亚通股份", "http://www.chinayatong.com/news/Index.asp"};
    urls[214] = new String[] {
        "600673", "阳之光", "http://www.chinayaxing.com/news1.asp"};
    urls[215] = new String[] {
        "600533", "栖霞建设", "http://www.chixia.com/web/news7.asp"};
    urls[216] = new String[] {
        "600132", "重庆啤酒", "http://www.chongqingbeer.com/newslist.asp"};
    urls[217] = new String[] {
        "600227", "赤天化", "http://www.chth.com.cn/xwzx.html?classid]=2"};
    urls[218] = new String[] {
        "000885", "S*ST春都", "http://www.chundu.com.cn/index31.asp?cataid]=公司新闻"};
    urls[219] = new String[] {
        "600854", "*ST春兰", "http://www.chunlan.com/2news/list_1news.asp"};
    urls[220] = new String[] {
        "600497", "驰宏锌锗",
        "http://www.chxz.com/newscompany.asp?mainid]=9&secondid]=0"};
    urls[221] = new String[] {
        "601166", "兴业银行",
        "http://www.cib.com.cn/netbank/cn/About_IB/Whatxs_New/"};
    urls[222] = new String[] {
        "000039", "中集集团", "http://www.cimc.com/web/418/Default.asp?id]=420"};
    urls[223] = new String[] {
        "600584", "长电科技", "http://www.cj-elec.com/gsxw.asp"};
    urls[224] = new String[] {
        "600109", "成都建投", "http://www.cjgf.com/news/more.asp?langmu2]=58"};
    urls[225] = new String[] {
        "600119", "长江投资", "http://www.cjtz.cn/news.asp?sorts]=公司新闻"};
    urls[226] = new String[] {
        "600876", "洛阳玻璃", "http://www.clfg.com/news/gsxw.asp"};
    urls[227] = new String[] {
        "600016", "民生银行",
        "http://www.cmbc.com.cn/cmbc/column/newest/newest.xml"};
    urls[228] = new String[] {
        "600036", "招商银行", "http://www.cmbchina.com/cmb+info/news/cmbnews"};
    urls[229] = new String[] {
        "601872", "招商轮船", "http://www.cmenergyshipping.com/Catalog_12.aspx"};
    urls[230] = new String[] {
        "000024", "招商地产", "http://www.cmpd.cn/zhaoshang/news/news1.jsp"};
    urls[231] = new String[] {
        "600511", "国药股份",
        "http://www.cncm.com.cn/cncm/news/listnews.asp?ClassID]=9"};
    urls[232] = new String[] {
        "600818", "上海永久", "http://www.cnforever.com/news/2006NEWS.HTML"};
    urls[233] = new String[] {
        "600586", "金晶科技", "http://www.cnggg.cn/2j/3-1.jsp?id]=7"};
    urls[234] = new String[] {
        "600232", "金鹰股份", "http://www.cn-goldeneagle.com/news.htm"};
    urls[235] = new String[] {
        "600068", "葛洲坝",
        "http://www.cngzb.com/news/newslist2.asp?t1]=6&class]=7"};
    urls[236] = new String[] {
        "002001", "新和成", "http://www.cnhu.com/cn/QYWH/CATEGORY.ASP"};
    urls[237] = new String[] {
        "600325", "华发股份", "http://www.cnhuafas.com/news_2.asp?isindex]=1"};
    urls[238] = new String[] {
        "600260", "凯乐科技", "http://www.cnkaile.com/XWWH/index.asp"};
    urls[239] = new String[] {
        "002076", "雪莱特", "http://www.cnlight.com/news/default.asp"};
    urls[240] = new String[] {
        "000564", "西安民生", "http://www.cnminsheng.com/chinese/xwzx/mskx.asp"};
    urls[241] = new String[] {
        "600626", "申达股份",
        "http://www.cnshenda.com.cn/chinese/jckxw/BrowseNews.asp"};
    urls[242] = new String[] {
        "600026", "中海发展", "http://www.cnshippingdev.com/xwzx.asp"};
    urls[243] = new String[] {
        "002024", "苏宁电器",
        "http://www.cnsuning.com/website/news/suningnews/index.html"};
    urls[244] = new String[] {
        "000798", "中水渔业", "http://www.cofc.com.cn/xinxi.asp?stype]=公司新闻"};
    urls[245] = new String[] {
        "000031", "中粮地产",
        "http://www.cofco-property.cn/new_news/news_list.asp?id]=5&type]=0&picture]=17"};
    urls[246] = new String[] {
        "000151", "中成股份", "http://www.complant-ltd.com.cn/xwdt.htm"};
    urls[247] = new String[] {
        "600572", "康恩贝",
        "http://www.conba.com.cn/xwzx/newslist1.asp?classname]=%D0%C2%CE%C5%B6%AF%CC%AC"};
    urls[248] = new String[] {
        "000619", "海螺型材", "http://www.conch.cn/sm2111111116.asp"};
    urls[249] = new String[] {
        "600585", "海螺水泥", "http://www.conch.cn/sm2111111116.asp"};
    urls[250] = new String[] {
        "600476", "湘邮科技", "http://www.copote.com/qydt/qydt-index.jsp"};
    urls[251] = new String[] {
        "600428", "中远航运", "http://www.coscol.com.cn/chinese/news.asp"};
    urls[252] = new String[] {
        "002052", "同洲电子",
        "http://www.coship.com/main/News/NewsIndex.aspx?CatalogID]=6810"};
    urls[253] = new String[] {
        "002133", "广宇集团", "http://www.cosmosgroup.com.cn/new_gsgs.asp"};
    urls[254] = new String[] {
        "600729", "重庆百货", "http://www.cqbhdl.com.cn/News/"};
    urls[255] = new String[] {
        "600369", "ST长运", "http://www.cqcjsy.com/docc/gongsixinwen.html"};
    urls[256] = new String[] {
        "601005", "重庆钢铁", "http://www.cqgt.cn/News/newslist.asp?class]=2"};
    urls[257] = new String[] {
        "600116", "三峡水利",
        "http://www.cqsxsl.com/gb/news/newslist.asp?gateid]=1&class]=4"};
    urls[258] = new String[] {
        "600263", "路桥建设", "http://www.crbcint.com/lq/xwzx.asp"};
    urls[259] = new String[] {
        "600528", "中铁二局", "http://www.crec.com.cn/gsxw.asp"};
    urls[260] = new String[] {
        "600890", "*ST中房", "http://www.cred.com/news/news.asp"};
    urls[261] = new String[] {
        "600029", "S南航", "http://www.cs-air.com/cn/news/11/26/48/list_1.asp"};
    urls[262] = new String[] {
        "000520", "长航凤凰", "http://www.csc-hy.com.cn/xwen2.asp?kind]=1"};
    urls[263] = new String[] {
        "000012", "南玻Ａ", "http://www.csgholding.com/news/nanbo.asp"};
    urls[264] = new String[] {
        "600536", "中国软件",
        "http://www.css.com.cn/subpage.aspx?ctabid]=33&stabid]=32"};
    urls[265] = new String[] {
        "000569", "*ST长钢", "http://www.cssc.com.cn/news.asp"};
    urls[266] = new String[] {
        "600623", "轮胎橡胶", "http://www.cstarc.com/news/news.asp"};
    urls[267] = new String[] {
        "600088", "中视传媒", "http://www.ctv-media.com.cn/xwzx/xwzx.htm"};
    urls[268] = new String[] {
        "600358", "国旅联合", "http://www.cutc.com.cn/news.asp?id]=3"};
    urls[269] = new String[] {
        "600007", "中国国贸", "http://www.cwtc.com/chinese/news/index.asp"};
    urls[270] = new String[] {
        "600658", "兆维科技", "http://www.cwtech.com.cn/news_look.asp"};
    urls[271] = new String[] {
        "600549", "厦门钨业", "http://www.cxtc.com/NewsInfo.asp?ClassID]=1"};
    urls[272] = new String[] {
        "600551", "科大创新",
        "http://www.cx-ustc.com/new/Article_Class.asp?ClassID]=1"};
    urls[273] = new String[] {
        "000966", "长源电力", "http://www.cydl.com.cn/news_list.asp?id]=11"};
    urls[274] = new String[] {
        "600900", "长江电力", "http://www.cypc.com.cn/NewsList.jsp?cateid]=102186"};
    urls[275] = new String[] {
        "600138", "中青旅", "http://www.cytsonline.com/default1.htm"};
    urls[276] = new String[] {
        "000780", "*ST兴发", "http://www.cyxf.com/news/"};
    urls[277] = new String[] {
        "600230", "沧州大化",
        "http://www.czdh.com.cn/layer2/news.asp?classid]=1&bz]=0"};
    urls[278] = new String[] {
        "002108", "沧州明珠", "http://www.cz-mz.com/main/news.htm"};
    urls[279] = new String[] {
        "000733", "振华科技", "http://www.czst.com.cn/news.asp"};
    urls[280] = new String[] {
        "002030", "达安基因", "http://www.daangene.com/news/"};
    urls[281] = new String[] {
        "600695", "*ST大江", "http://www.dajiang.com/newEbiz1/EbizPortalFG/portal/html/news2.html?folderID]=c373e90a2ae75d828f6a7d57d518fe6c"};
    urls[282] = new String[] {
        "000530", "大冷股份", "http://www.daleng.cn/news/index.jsp"};
    urls[283] = new String[] {
        "601006", "大秦铁路", "http://www.daqintielu.com/region/00008.shtml"};
    urls[284] = new String[] {
        "000910", "大亚科技", "http://www.daretechnology.com/cn/news.asp"};
    urls[285] = new String[] {
        "600198", "*ST大唐", "http://www.datang.com/news.asp"};
    urls[286] = new String[] {
        "600747", "大显股份",
        "http://www.daxian.cn/DAXIAN/NewsClist.asp?CID]=48&supID]=40"};
    urls[287] = new String[] {
        "002041", "登海种业", "http://www.denghai.com/denghai_news.asp"};
    urls[288] = new String[] {
        "002055", "得润电子", "http://www.deren.com.cn/newnews/default.asp"};
    urls[289] = new String[] {
        "000049", "德赛电池",
        "http://www.desaybattery.com/asp-bin/GB/?page]=7&class]=35"};
    urls[290] = new String[] {
        "600081", "东风科技", "http://www.detc.com.cn/df1-51all.htm"};
    urls[291] = new String[] {
        "600006", "东风汽车", "http://www.dfac.com/news/gsxw.asp"};
    urls[292] = new String[] {
        "600875", "东方电机", "http://www.dfem.com.cn/News.asp?C]=A"};
    urls[293] = new String[] {
        "002077", "大港股份", "http://www.dggf.cn/qydt.asp"};
    urls[294] = new String[] {
        "000828", "东莞控股",
        "http://www.dgholdings.cn/news-Search.asp?columnname]=&columncode]=001001"};
    urls[295] = new String[] {
        "002065", "东华合创",
        "http://www.dhcc.com.cn/news/news_link.asp?kind]=%CD%BC%C6%AC%D0%C2%CE%C5"};
    urls[296] = new String[] {
        "600354", "敦煌种业", "http://www.dhseed.com/Html/gsnews/index.html"};
    urls[297] = new String[] {
        "002043", "兔宝宝", "http://www.dhwooden.com/news.asp"};
    urls[298] = new String[] {
        "600288", "大恒科技", "http://www.dhxjy.com.cn/dh_news.htm"};
    urls[299] = new String[] {
        "600830", "大红鹰", "http://www.dhyinvest.com/more.asp?ttt]=6&sss]=公司新闻"};
    urls[300] = new String[] {
        "600159", "大龙地产", "http://www.dldc.com.cn/gsxw.htm"};
    urls[301] = new String[] {
        "000961", "大连金牛", "http://www.dljn.com/news/index_gs.jsp"};
    urls[302] = new String[] {
        "000679", "大连友谊", "http://www.dlyy.com.cn/jtyw/index.asp"};
    urls[303] = new String[] {
        "600693", "东百集团", "http://www.dongbai.com/new.asp"};
    urls[304] = new String[] {
        "000423", "S阿胶", "http://www.dongeejiao.com/news/more.aspx?type]=公司动态"};
    urls[305] = new String[] {
        "000682", "东方电子", "http://www.dongfang-china.com/showall.asp?typeid]=1"};
    urls[306] = new String[] {
        "002082", "栋梁新材",
        "http://www.dongfang-china.com/xwzx.asp?typeid]=1&img]=xwzx.gif"};
    urls[307] = new String[] {
        "002135", "东南网架", "http://www.dongnanwangjia.com/newEbiz1/EbizPortalFG/portal/html/InfoMultiPage.html?InfoList150_action]=more&InfoPublish_CategoryID]=c373e90b4c646f9f8febbba7b39c9c36"};
    urls[308] = new String[] {
        "600113", "浙江东日",
        "http://www.dongri.com/otype.asp?owen1]=公司新闻&owen2]=公司新闻&n]=20"};
    urls[309] = new String[] {
        "000599", "青岛双星", "http://www.doublestar.com.cn/xinwenlie.asp"};
    urls[310] = new String[] {
        "600405", "动力源", "http://www.dpc.com.cn/xwysj.asp"};
    urls[311] = new String[] {
        "600804", "鹏博士", "http://www.drpeng.com.cn/(fjkulg55mvwkboqnhitaoq45)/ShowPage/SystemTemp1/ThreeLevelPage/NewsList.aspx?CataID]=bba13a83-5d09-4765-a60b-912b83f1e4e8"};
    urls[312] = new String[] {
        "600694", "大商股份", "http://www.dsjt.com/news.asp"};
    urls[313] = new String[] {
        "600335", "鼎盛天工", "http://www.dstg.com.cn/news/index.asp"};
    urls[314] = new String[] {
        "601001", "大同煤业", "http://www.dtmy.com.cn/lb.asp?lb]=1"};
    urls[315] = new String[] {
        "601991", "大唐发电", "http://www.dtpower.com/sp/library/notice.jsp"};
    urls[316] = new String[] {
        "002011", "盾安环境", "http://www.dunan.net/news.asp?classid]=5"};
    urls[317] = new String[] {
        "600833", "第一医药", "http://www.dyyy.com.cn/newEbiz1/EbizPortalFG/portal/html/InfoMultiPage.html?InfoList150_action]=more&InfoPublish_CategoryID]=c373e9033fdcde088ffbdd55c0235ee8"};
    urls[318] = new String[] {
        "600635", "大众公用", "http://www.dzug.cn/"};
    urls[319] = new String[] {
        "600776", "东方通信", "http://www.eastcom.com:8080/news/news_focus.jsp"};
    urls[320] = new String[] {
        "002017", "东信和平",
        "http://www.eastcompeace.com/Home_Data_More.asp?ID]=01"};
    urls[321] = new String[] {
        "601628", "中国人寿", "http://www.e-chinalife.com/news/company/index.html"};
    urls[322] = new String[] {
        "000826", "合加资源", "http://www.eguard-rd.com/infolist.aspx?dataid]=111"};
    urls[323] = new String[] {
        "000562", "宏源证券", "http://www.ehongyuan.com/news/more.jsp?hynav]=abouthy&menuId]=1&parentId]=1971&catId]=1972"};
    urls[324] = new String[] {
        "600340", "国祥股份",
        "http://www.ekingair.com/html/news/listz.asp?catid]=89"};
    urls[325] = new String[] {
        "002005", "德豪润达", "http://www.electech.com.cn/Catalog_86.aspx"};
    urls[326] = new String[] {
        "002059", "世博股份",
        "http://www.expo99km.gov.cn/expo/Wpublisher/displaypages/ContentList_22.aspx"};
    urls[327] = new String[] {
        "600651", "飞乐音响", "http://www.facs.com.cn/news.php"};
    urls[328] = new String[] {
        "000055", "方大Ａ", "http://www.fangda.com/news/newslist.asp?class]=14"};
    urls[329] = new String[] {
        "600563", "法拉电子", "http://www.faratronic.com/cnnews.asp"};
    urls[330] = new String[] {
        "000890", "法尔胜", "http://www.fasten.com.cn/news/1/"};
    urls[331] = new String[] {
        "000800", "一汽轿车",
        "http://www.fawcar.com.cn/xwdt.jsp?Type]=1&Main]=xxdt#"};
    urls[332] = new String[] {
        "600742", "一汽四环", "http://www.fawsh.com.cn/hydt/default.jsp"};
    urls[333] = new String[] {
        "600526", "菲达环保", "http://www.feida.biz/CN/newslist.asp"};
    urls[334] = new String[] {
        "600654", "飞乐股份", "http://www.feilo.com.cn/news/news.aspx"};
    urls[335] = new String[] {
        "002042", "飞亚股份", "http://www.feiyatex.com/news.asp"};
    urls[336] = new String[] {
        "000636", "风华高科", "http://www.fenghua-advanced.com/news.asp?id]=166"};
    urls[337] = new String[] {
        "000713", "丰乐种业",
        "http://www.fengle.com.cn/news/indexnew.asp?typeid]=1"};
    urls[338] = new String[] {
        "600809", "山西汾酒", "http://www.fenjiu.com.cn/docc/news/news.asp"};
    urls[339] = new String[] {
        "000046", "泛海建设", "http://www.fhjs.cn/news/xwzx.asp"};
    urls[340] = new String[] {
        "600498", "烽火通信", "http://www.fiberhome.com.cn/news/news.asp"};
    urls[341] = new String[] {
        "600616", "第一食品", "http://www.firstfood.com.cn/"};
    urls[342] = new String[] {
        "000026", "S飞亚达A", "http://www.fiyta.com.cn/news.asp"};
    urls[343] = new String[] {
        "600802", "福建水泥", "http://www.fjcement.com/news.asp?more]=1"};
    urls[344] = new String[] {
        "600033", "福建高速",
        "http://www.fjgs.com.cn/htdocs/xxlr.asp?tab]=&menulb]=129新闻中心&menujb]=2"};
    urls[345] = new String[] {
        "600452", "涪陵电力", "http://www.flepc.com/news.asp"};
    urls[346] = new String[] {
        "600196", "复星医药",
        "http://www.fosunpharma.com/Default.aspx?tabid]=399&modulesID]=583"};
    urls[347] = new String[] {
        "600166", "福田汽车", "http://www.foton.com.cn/news/xwzx/gsxw_list.html"};
    urls[348] = new String[] {
        "600601", "方正科技", "http://www.foundertech.com/tabid/69/Default.aspx"};
    urls[349] = new String[] {
        "600399", "抚顺特钢", "http://www.fs-ss.com/qydt/gsxw-qt1.jsp"};
    urls[350] = new String[] {
        "600965", "福成五丰", "http://www.fucheng.net/cn/news.asp"};
    urls[351] = new String[] {
        "600724", "宁波富达",
        "http://www.fuda.com/asp/list.asp?classid]=00000000000000000731"};
    urls[352] = new String[] {
        "600203", "福日股份", "http://www.furielec.com/xwzx.asp"};
    urls[353] = new String[] {
        "600660", "福耀玻璃",
        "http://www.fuyaogroup.com/news/article_class.aspx?articleclass_id]=1"};
    urls[354] = new String[] {
        "000576", "广东甘化", "http://www.ganhua.com.cn/02-2.php"};
    urls[355] = new String[] {
        "600067", "冠城大通", "http://www.gcdt.net/newsmore.asp?cid]=19"};
    urls[356] = new String[] {
        "600310", "桂东电力", "http://www.gdep.com.cn/EnviProtection.asp"};
    urls[357] = new String[] {
        "000823", "超声电子", "http://www.gd-goworld.com/cn/info/index.html"};
    urls[358] = new String[] {
        "600098", "广州控股",
        "http://www.gdih.cn/chinese/news-Search.asp?columnname]=&columncode]=001001"};
    urls[359] = new String[] {
        "600382", "广东明珠", "http://www.gdmzh.com/article/conews.asp?cataid]=64"};
    urls[360] = new String[] {
        "002060", "粤水电", "http://www.gdsdej.com/news.asp"};
    urls[361] = new String[] {
        "601002", "晋亿实业", "http://www.gem-year.com/news/more1.asp"};
    urls[362] = new String[] {
        "002045", "广州国光", "http://www.ggec.com.cn/news.asp"};
    urls[363] = new String[] {
        "600236", "桂冠电力",
        "http://www.ggep.com.cn/Article/news/conews/Index.html"};
    urls[364] = new String[] {
        "600894", "广钢股份", "http://www.gglts.cn/jsp/sinoec/index4/consultantcenter/index.jsp?pageNo]=1&pageSize]=20&choice]=current"};
    urls[365] = new String[] {
        "002101", "广东鸿图", "http://www.ght-china.com/news.asp"};
    urls[366] = new String[] {
        "600080", "*ST金花", "http://www.ginwa.com/news/index.asp"};
    urls[367] = new String[] {
        "600182", "S佳通", "http://www.gititire.com/gititirecorp/news.asp?menuid]=4&classifaction]=4&subid]=0&language]=1&ryear]=0"};
    urls[368] = new String[] {
        "600800", "S*ST磁卡", "http://www.gmcc.com.cn/news/index.asp"};
    urls[369] = new String[] {
        "600538", "北海国发",
        "http://www.gofar.com.cn/gofarnew/list.asp?boardid]=26&parent]=109"};
    urls[370] = new String[] {
        "000851", "高鸿股份", "http://www.gohigh.com.cn/guanyugaohong_list.jsp?leibieid]=1&subleibieid]=2&gen2name]=公司新闻"};
    urls[371] = new String[] {
        "600086", "东方金钰", "http://www.goldjade.cn/new.asp?typeid]=3"};
    urls[372] = new String[] {
        "002081", "金螳螂", "http://www.goldmantis.com/web/news.asp?newsSortId]=1"};
    urls[373] = new String[] {
        "002079", "苏州固锝", "http://www.goodark.com/news.asp"};
    urls[374] = new String[] {
        "600332", "广州药业", "http://www.gpc.com.cn/list_all.asp"};
    urls[375] = new String[] {
        "000429", "粤高速Ａ",
        "http://www.gpedcl.com/default.asp?ChannelId]=12&ColumnId]=72"};
    urls[376] = new String[] {
        "002031", "巨轮股份",
        "http://www.greatoo.com/greatoo_cn/CONN_NEWS1.asp?REVE]=A"};
    urls[377] = new String[] {
        "000066", "长城电脑", "http://www.greatwall.com.cn/news/news.asp"};
    urls[378] = new String[] {
        "000651", "格力电器", "http://www.gree.com.cn/gree_news/news_index01.jsp"};
    urls[379] = new String[] {
        "600206", "有研硅股", "http://www.gritek.com/qyzs/main.html"};
    urls[380] = new String[] {
        "601333", "广深铁路", "http://www.gsrc.com/dongtai/dongtai.htm"};
    urls[381] = new String[] {
        "002091", "江苏国泰", "http://www.gtiggm.com/news.asp?cid]=5"};
    urls[382] = new String[] {
        "002102", "冠福家用", "http://www.guanfu.com/html/news2.asp"};
    urls[383] = new String[] {
        "002103", "广博股份",
        "http://www.guangbo.net/chinese/news/more.asp?l_id]=1"};
    urls[384] = new String[] {
        "000952", "广济药业",
        "http://www.guangjipharm.com/docc/shownews.asp?lb_id]=1"};
    urls[385] = new String[] {
        "000587", "S*ST光明",
        "http://www.guangming.com/manager/info/show.asp?id]=4&cate_id]=1&type]=info"};
    urls[386] = new String[] {
        "002111", "威海广泰", "http://www.guangtai.com.cn/new/news.asp"};
    urls[387] = new String[] {
        "000557", "ST银广夏", "http://www.guangxia.com.cn/newlist.htm"};
    urls[388] = new String[] {
        "600433", "冠豪高新", "http://www.guanhao.com/news_1.asp"};
    urls[389] = new String[] {
        "000750", "S*ST集琦",
        "http://www.guilinjiqi.com.cn/jqdongtai/jqdongtai.asp"};
    urls[390] = new String[] {
        "000978", "桂林旅游",
        "http://www.guilintravel.com/blog/Article/ShowArticle.asp?ArticleID]=11"};
    urls[391] = new String[] {
        "000833", "贵糖股份", "http://www.guitang.com/webpage/news.html"};
    urls[392] = new String[] {
        "000589", "黔轮胎Ａ", "http://www.guizhoutyre.com/gsxw.asp"};
    urls[393] = new String[] {
        "000596", "古井贡酒", "http://www.gujing.com/news/zxzx.asp"};
    urls[394] = new String[] {
        "600321", "国栋建设", "http://www.guodong.cn/News/newslist.asp?class]=2"};
    urls[395] = new String[] {
        "002093", "国脉科技", "http://www.guomaitech.com/news.asp"};
    urls[396] = new String[] {
        "600444", "国通管业", "http://www.guotone.com/news.asp?NewsType]=公司新闻"};
    urls[397] = new String[] {
        "000748", "长城信息",
        "http://www.gwi.com.cn/new.asp?title]=新闻快递&langmu2]=2"};
    urls[398] = new String[] {
        "000537", "广宇发展", "http://www.gyfz000537.com/news/gongsinewsmore.asp"};
    urls[399] = new String[] {
        "600348", "国阳新能", "http://www.gyne.com.cn/SEC.ASP"};
    urls[400] = new String[] {
        "000522", "白云山Ａ", "http://www.gzbys.com/news.asp"};
    urls[401] = new String[] {
        "002025", "航天电器", "http://www.gzhtdq.com.cn/news.asp"};
    urls[402] = new String[] {
        "600048", "保利地产", "http://www.gzpoly.com/news/"};
    urls[403] = new String[] {
        "600594", "益佰制药", "http://www.gz-yibai.com/news.asp"};
    urls[404] = new String[] {
        "600684", "珠江实业", "http://www.gzzjsy.com/news.asp?nb]=9"};
    urls[405] = new String[] {
        "600202", "哈空调",
        "http://www.hac.com.cn/news_list.php?mname]=%D0%C2%CE%C5%D6%D0%D0%C4&mid]=1"};
    urls[406] = new String[] {
        "600598", "北大荒", "http://www.hacl.cn/gsdt.asp"};
    urls[407] = new String[] {
        "600038", "哈飞股份", "http://www.hafei.com/InfoPublish/Listcompany.asp"};
    urls[408] = new String[] {
        "600690", "青岛海尔",
        "http://www.haier.com/cn/news/haier/more.asp?itemID]=25"};
    urls[409] = new String[] {
        "600516", "*ST方大", "http://www.hailongkeji.com/news/index.htm"};
    urls[410] = new String[] {
        "600896", "中海海盛", "http://www.haishengshipping.com/xinwen.asp"};
    urls[411] = new String[] {
        "002116", "中国海诚", "http://www.haisum.com/news/index.asp"};
    urls[412] = new String[] {
        "000566", "海南海药", "http://www.haiyao.com.cn/news/news3.asp"};
    urls[413] = new String[] {
        "600570", "恒生电子", "http://www.handsome.com.cn/news/index.php?news_type_id]=1&news_type]=%B9%AB%CB%BE%D0%C2%CE%C5"};
    urls[414] = new String[] {
        "002008", "大族激光",
        "http://www.hanslaser.com/content_manage/news.asp?bigclassname]=公司新闻"};
    urls[415] = new String[] {
        "002013", "中航精机", "http://www.hapm.cn/news-1.asp"};
    urls[416] = new String[] {
        "600664", "S哈药", "http://www.hapm.cn/news-1.asp"};
    urls[417] = new String[] {
        "600708", "海博股份", "http://www.hb600708.com/news.asp"};
    urls[418] = new String[] {
        "600035", "楚天高速",
        "http://www.hbctgs.com/ctgs/info/listArticle.jsp?artColumn]=03020103"};
    urls[419] = new String[] {
        "000916", "华北高速", "http://www.hbgsgl.com.cn/index_dt_zt.asp"};
    urls[420] = new String[] {
        "600566", "洪城股份", "http://www.hbhc.com.cn/Chinese/qyxw/qyxw.asp"};
    urls[421] = new String[] {
        "600184", "新华光", "http://www.hbnhg.com/new/dt00.asp#"};
    urls[422] = new String[] {
        "000923", "S宣工", "http://www.hbxg.com/gongsi2.asp"};
    urls[423] = new String[] {
        "000422", "湖北宜化", "http://www.hbyh.cn/news/news1.htm"};
    urls[424] = new String[] {
        "000727", "华东科技", "http://www.hdeg.com/"};
    urls[425] = new String[] {
        "600001", "邯郸钢铁", "http://www.hdgt.com.cn/new.asp?pic]=0"};
    urls[426] = new String[] {
        "600027", "华电国际",
        "http://www.hdpi.com.cn/st/TZ/XWG/news_list.aspx?nian]=2007"};
    urls[427] = new String[] {
        "000953", "河池化工", "http://www.hechihuagong.com.cn/hh2/gongsixinwen.htm"};
    urls[428] = new String[] {
        "002027", "七喜控股", "http://www.hedy.com.cn/Chinese/news/co/month05.htm"};
    urls[429] = new String[] {
        "600760", "ST黑豹", "http://www.heibao.com/cn/newsgs.asp"};
    urls[430] = new String[] {
        "600093", "*ST禾嘉", "http://www.hejia.com/Article_Class2.asp?ClassID]=7"};
    urls[431] = new String[] {
        "600761", "安徽合力", "http://www.helichina.com/news.asp"};
    urls[432] = new String[] {
        "002104", "恒宝股份", "http://www.hengbao.com/new/index.asp"};
    urls[433] = new String[] {
        "600356", "恒丰纸业",
        "http://www.hengfengpaper.com/Article/ShowClass.asp?ClassID]=1"};
    urls[434] = new String[] {
        "600305", "恒顺醋业", "http://www.hengshun.cn/news.php"};
    urls[435] = new String[] {
        "002132", "恒星科技", "http://www.hengxingchinese.com/newxp/SmallClass.asp?BigClassID]=1&BigClassName]=恒星新闻&SmallClassID]=2&SmallClassName]=信息中心&SmallClassType]=1"};
    urls[436] = new String[] {
        "000531", "穗恒运Ａ", "http://www.hengyun.com.cn/xwzx.asp"};
    urls[437] = new String[] {
        "600448", "华纺股份", "http://www.hfgf.cn/Cn/News.asp?Types]=3"};
    urls[438] = new String[] {
        "600076", "*ST华光", "http://www.hg.com.cn/news/sub1_gsxw.asp"};
    urls[439] = new String[] {
        "600095", "哈高科", "http://www.hgk-group.com/our_news.htm"};
    urls[440] = new String[] {
        "000988", "华工科技", "http://www.hgtech.com.cn/new1-1.asp"};
    urls[441] = new String[] {
        "600150", "沪东重机", "http://www.hhm.com.cn/news.htm"};
    urls[442] = new String[] {
        "600172", "黄河旋风", "http://www.hhxf.com/chinese/more.asp"};
    urls[443] = new String[] {
        "000886", "海南高速", "http://www.hi-expressway.com/zxdt.asp"};
    urls[444] = new String[] {
        "600619", "海立股份", "http://www.highly.cc/hlxw/hlxw.htm"};
    urls[445] = new String[] {
        "000921", "*ST科龙", "http://www.hisense.com/news/info.jsp"};
    urls[446] = new String[] {
        "600267", "海正药业", "http://www.hisunpharm.com/03news/"};
    urls[447] = new String[] {
        "000609", "绵世股份", "http://www.hi-tec609.com/News.asp"};
    urls[448] = new String[] {
        "600082", "海泰发展", "http://www.hitech-develop.com/news/morenews.htm"};
    urls[449] = new String[] {
        "600162", "香江控股", "http://www.hkhc.com.cn/index/xwzx/xwsd/"};
    urls[450] = new String[] {
        "000751", "锌业股份", "http://www.hldxygf.com/neiye1-004.html"};
    urls[451] = new String[] {
        "600426", "华鲁恒升", "http://www.hl-hengsheng.com/docc/news_1.asp"};
    urls[452] = new String[] {
        "600987", "航民股份", "http://www.hmgf.com/news/index.asp?myclass]=3"};
    urls[453] = new String[] {
        "600221", "海南航空",
        "http://www.hnair.com/hnairweb/ABOUTHNAIR/NEWS/wfmNewS.aspx?strNewsType]=1"};
    urls[454] = new String[] {
        "600731", "湖南海利", "http://www.hndtsz.com/new.asp?id2]=138"};
    urls[455] = new String[] {
        "600257", "洞庭水殖", "http://www.hndtsz.com/new.asp?id2]=138"};
    urls[456] = new String[] {
        "000989", "九芝堂", "http://www.hnjzt.com/newsmore.asp?action]=九芝新闻"};
    urls[457] = new String[] {
        "002096", "南岭民爆", "http://www.hnnlmb.com/news.asp?classname]=全部新闻"};
    urls[458] = new String[] {
        "000548", "湖南投资", "http://www.hntz.com.cn/Release/list1.asp?id]=2"};
    urls[459] = new String[] {
        "000790", "华神集团", "http://www.hoist.com.cn/1/homenews.asp?cataID]=40"};
    urls[460] = new String[] {
        "600097", "华立科技", "http://www.holleykj.com/news/index.php"};
    urls[461] = new String[] {
        "000607", "华立药业", "http://www.holleypharm.com/新闻中心/tabid/55/articleType/CategoryView/categoryId/1/.aspx"};
    urls[462] = new String[] {
        "600400", "红豆股份", "http://www.hongdou.com.cn/news.asp"};
    urls[463] = new String[] {
        "600316", "洪都航空", "http://www.hongdu-aviation.com/cn/html/more.asp"};
    urls[464] = new String[] {
        "000524", "东方宾馆", "http://www.hoteldongfang.com/news.asp"};
    urls[465] = new String[] {
        "600011", "华能国际",
        "http://www.hpi.com.cn/chinese/investor/pressrelease/index.jsp"};
    urls[466] = new String[] {
        "600276", "恒瑞医药", "http://www.hrs.com.cn/xwzx01.asp"};
    urls[467] = new String[] {
        "600687", "华盛达", "http://www.hsdchina.com/html/2_1.asp?classname]=tmpClassName&classid]=3&nclassname]=%B9%AB%CB%BE%D0%C2%CE%C5&nclassid]=3"};
    urls[468] = new String[] {
        "000980", "金马股份", "http://www.hsjinma.com/gsxw/"};
    urls[469] = new String[] {
        "600487", "亨通光电", "http://www.htgd.com.cn/new1.asp"};
    urls[470] = new String[] {
        "000901", "航天科技",
        "http://www.htkjgroup.com/fenye.aspx?typeid]=73&select_name]=最新消息"};
    urls[471] = new String[] {
        "600371", "华冠科技", "http://www.huaguankeji.com/hgkj/news_more.asp?lm]=&lm2]=67&open]=_blank&tj]=0&hot]=0"};
    urls[472] = new String[] {
        "600521", "华海药业", "http://www.huahaipharm.com/web/newsB0001.asp?FirstKind]=MT_00002_100002702&KindID]=MT_00002_100002703"};
    urls[473] = new String[] {
        "000985", "大庆华科", "http://www.huake.com/hkjs.asp"};
    urls[474] = new String[] {
        "002007", "华兰生物", "http://www.hualanbio.com/hlnews.asp"};
    urls[475] = new String[] {
        "600054", "黄山旅游",
        "http://www.huangshan.com.cn/news/newsMore.do?colid]=4"};
    urls[476] = new String[] {
        "002004", "华邦制药",
        "http://www.huapont.cn/hb_web/Default.aspx?tabid]=138"};
    urls[477] = new String[] {
        "600308", "华泰股份", "http://www.huatai.com/col1/col15/index.htm1?id]=15"};
    urls[478] = new String[] {
        "000428", "华天酒店", "http://www.huatian-hotel.com/news/news.jsp"};
    urls[479] = new String[] {
        "002048", "宁波华翔", "http://www.huaxianggroup.com/chinese/new.php"};
    urls[480] = new String[] {
        "002018", "华星化工", "http://www.huaxingchem.com/news.asp"};
    urls[481] = new String[] {
        "600532", "华阳科技", "http://www.huayang.com/lm23.asp"};
    urls[482] = new String[] {
        "000404", "华意压缩", "http://www.hua-yi.cn/HYRC/Simplified/NewsList.asp"};
    urls[483] = new String[] {
        "600573", "惠泉啤酒", "http://www.huiquan-beer.com/news.asp"};
    urls[484] = new String[] {
        "000415", "汇通水利",
        "http://www.huitonggroup.com.cn/news_list.asp?action]=more&c_id]=89&s_id]=106"};
    urls[485] = new String[] {
        "600079", "人福科技",
        "http://www.humanwell.com.cn/web/listnewsclient?type]=1"};
    urls[486] = new String[] {
        "600360", "华微电子", "http://www.hwdz.com.cn/tzgx/gsdt.jsp"};
    urls[487] = new String[] {
        "600015", "华夏银行",
        "http://www.hxb.com.cn/chinese/abouthxb/index.jsp?cid2]=73&cid3]=732"};
    urls[488] = new String[] {
        "600367", "红星发展", "http://www.hxfz.com.cn/hongxinbd.asp"};
    urls[489] = new String[] {
        "600477", "杭萧钢构",
        "http://www.hxss.com.cn/news/?PHPSESSID]=38183e769c0b14efe350d7cd5b8e68fb"};
    urls[490] = new String[] {
        "000963", "华东医药", "http://www.hzhdyy.com/news.asp"};
    urls[491] = new String[] {
        "600126", "杭钢股份", "http://www.hzsteel.com/web/NewsList.asp?FirstKind]=MT_00002_100017466&KindID]=MT_00002_100017469"};
    urls[492] = new String[] {
        "600608", "S*ST沪科", "http://www.i600608.com/cn/news/news.asp"};
    urls[493] = new String[] {
        "601398", "工商银行", "http://www.icbc.com.cn/news/hotspot.jsp?column]=%B9%A4%D0%D0%BF%EC%D1%B6&row]=1&length]=15"};
    urls[494] = new String[] {
        "600797", "浙大网新", "http://www.insigma.com.cn/news/index.php?func]=listAll&catalog]=0101&PHPSESSID]=4d2c71d84cb2d19b8a0f7a92616c3cd3"};
    urls[495] = new String[] {
        "600755", "厦门国贸", "http://www.itg.com.cn:81/itgweb/NewsServlet?action]=NewsListByNewsType&NewsTypeID]=1"};
    urls[496] = new String[] {
        "600418", "江淮汽车", "http://www.jac.com.cn/jac/showCatalogAction.do?method]=print&catalogid]=ff8080811102a3c6011168d7e9ba045d"};
    urls[497] = new String[] {
        "600315", "上海家化", "http://www.jahwa.com.cn/jahwa/news/news.php"};
    urls[498] = new String[] {
        "000897", "津滨发展",
        "http://www.jbdc.com.cn/News/InfoList.aspx?CategoryID]=1"};
    urls[499] = new String[] {
        "600622", "嘉宝集团", "http://www.jbjt.com/2-1.asp"};
    urls[500] = new String[] {
        "600546", "中油化建",
        "http://www.jccc.com.cn/HuaJian/News/Important/?type_id]=7"};
    urls[501] = new String[] {
        "000816", "江淮动力",
        "http://www.jdchina.com/docc/news/newscomp.asp?kindid]=14"};
    urls[502] = new String[] {
        "600661", "交大南洋", "http://www.jd-ny.com/zxdt_001.asp"};
    urls[503] = new String[] {
        "000401", "冀东水泥", "http://www.jdsn.com.cn/news.asp?classid]=8"};
    urls[504] = new String[] {
        "600265", "景谷林业", "http://www.jgly.cn/news/index.aspx"};
    urls[505] = new String[] {
        "600160", "巨化股份", "http://www.jhgf.com.cn/more.asp"};
    urls[506] = new String[] {
        "600877", "中国嘉陵", "http://www.jialing.com.cn/newjl/cn_web/cntop1-1.php"};
    urls[507] = new String[] {
        "600668", "尖峰集团", "http://www.jianfeng.com.cn/news/news1.aspx"};
    urls[508] = new String[] {
        "000950", "建峰化工", "http://www.jianfengchemicals.com/cgi/search-cn.cgi?f]=contact_cn+news_cn+company_cn_1_&t]=news_cn&w]=news_cn"};
    urls[509] = new String[] {
        "600212", "江泉实业", "http://www.jiangquan.com.cn/news.asp?cat_id]=1"};
    urls[510] = new String[] {
        "002061", "江山化工", "http://www.jiangshanchem.com/news.asp"};
    urls[511] = new String[] {
        "000617", "石油济柴", "http://www.jichai.com/chinese/xinwen.php"};
    urls[512] = new String[] {
        "600836", "界龙实业", "http://www.jielong-printing.com/xwzx/xwzxmore.asp"};
    urls[513] = new String[] {
        "600022", "济南钢铁", "http://www.jigang.com.cn/invinfo/WEB_SIDE/jgdt.jsp"};
    urls[514] = new String[] {
        "002118", "紫鑫药业", "http://www.jilinzixin.com.cn/category.asp?id]=88"};
    urls[515] = new String[] {
        "600577", "精达股份", "http://www.jingda.cn/news/index.asp?lx]=公司新闻"};
    urls[516] = new String[] {
        "002020", "京新药业", "http://www.jingxinpharm.com/news.asp?lb]=2"};
    urls[517] = new String[] {
        "002049", "晶源电子", "http://www.jingyuan.com/info.asp"};
    urls[518] = new String[] {
        "000818", "锦化氯碱", "http://www.jinhuagroup.com/news-c.htm#"};
    urls[519] = new String[] {
        "600621", "上海金陵", "http://www.jin-ling.com/xwzx.asp"};
    urls[520] = new String[] {
        "601007", "金陵饭店",
        "http://www.jinlinghotel.com/CN/presscenter/NewsRelease.asp"};
    urls[521] = new String[] {
        "000510", "金路集团", "http://www.jinlugroup.cn/zhongc/web57/newssmall.asp?class]=公司动态&classid]=2293&id]=25947"};
    urls[522] = new String[] {
        "600201", "金宇集团", "http://www.jinyu.com.cn/news.php"};
    urls[523] = new String[] {
        "600190", "锦州港", "http://www.jinzhouport.com/rdxw.asp"};
    urls[524] = new String[] {
        "600307", "酒钢宏兴", "http://www.jiugang.com/structure/jgxw/jgxw"};
    urls[525] = new String[] {
        "600292", "九龙电力", "http://www.jiulongep.com/gsyw.asp"};
    urls[526] = new String[] {
        "600650", "锦江投资", "http://www.jjtz.com/webapp/china/template1/xwzx-mt.jsp?org_item_id]=048895d9-4347-4efd-b8e9-65cbad3d9bfe&sub_org_item_id]=b0d4e2a6-797e-4def-8cf4-846c52190a64"};
    urls[527] = new String[] {
        "600806", "昆明机床", "http://www.jkht.com/info/news.aspx"};
    urls[528] = new String[] {
        "000623", "吉林敖东", "http://www.jlaod.com/yeNews.asp"};
    urls[529] = new String[] {
        "600432", "吉恩镍业", "http://www.jlnickel.com.cn/second.asp?parentcol]=02&ColumnName]=企业动态&classidd]=0201"};
    urls[530] = new String[] {
        "601008", "连云港", "http://www.jlpcl.com/companynews.aspx"};
    urls[531] = new String[] {
        "600189", "吉林森工", "http://www.jlsg.com.cn/aspx/newsmore.aspx"};
    urls[532] = new String[] {
        "000928", "*ST吉炭", "http://www.jlts.cn/Chinese/Bs_News.asp?Action]=Co"};
    urls[533] = new String[] {
        "000919", "金陵药业", "http://www.jlyy000919.com/news/news.asp"};
    urls[534] = new String[] {
        "000550", "江铃汽车", "http://www.jmc.com.cn/chinese/info/news_events.asp"};
    urls[535] = new String[] {
        "600527", "江南高纤", "http://www.jngx.cn/news/newslist.asp?class]=19"};
    urls[536] = new String[] {
        "600072", "江南重工", "http://www.jnhi.com/news.php"};
    urls[537] = new String[] {
        "000937", "金牛能源",
        "http://www.jnny.com.cn/news.asp?i]=%BD%F0%C5%A3%BF%C6%BC%BC&c]=金牛科技动态"};
    urls[538] = new String[] {
        "600380", "健康元", "http://www.joincare.com/news.asp"};
    urls[539] = new String[] {
        "600888", "新疆众和", "http://www.joinworld.com/news.asp"};
    urls[540] = new String[] {
        "600872", "中炬高新",
        "http://www.jonjee.com/cn/?op]=list_news&id]=4&mid]=10"};
    urls[541] = new String[] {
        "000402", "金融街",
        "http://www.jrjkg.com.cn/detail.aspx?id]=1&ttype]=200000&table]=news&pid]=04"};
    urls[542] = new String[] {
        "600389", "江山股份", "http://www.jsac.com.cn/gxxw.asp?submenu]=more"};
    urls[543] = new String[] {
        "002074", "东源电器", "http://www.jsdydq.com/web/xwzx.asp"};
    urls[544] = new String[] {
        "600377", "宁沪高速",
        "http://www.jsexpressway.com/col2/col20/articlecolumn.php?colid]=20"};
    urls[545] = new String[] {
        "000821", "京山轻机", "http://www.jspackmach.com/c_news.asp"};
    urls[546] = new String[] {
        "600981", "江苏开元",
        "http://www.jstex.com/html_cn/index_cn.php?menu]=about_cn"};
    urls[547] = new String[] {
        "600522", "中天科技", "http://www.jszt.com.cn/2007/News/index.asp"};
    urls[548] = new String[] {
        "000666", "经纬纺机", "http://www.jwgf.com/news_qy.aspx"};
    urls[549] = new String[] {
        "600362", "江西铜业", "http://www.jxcc.com/chinese/jtgs/jtyw/xwda.htm"};
    urls[550] = new String[] {
        "600561", "江西长运", "http://www.jxcy.com.cn/Gsxw/list.asp?type]=1"};
    urls[551] = new String[] {
        "600269", "赣粤高速", "http://www.jxexpressway.com/news/SmallClass.asp?BigClassID]=2&BigClassName]=新闻中心&BigClassType]=1&SmallClassID]=7&SmallClassName]=公司要闻&SmallClassType]=1"};
    urls[552] = new String[] {
        "600461", "洪城水业",
        "http://www.jxhcsy.com/release/list_22.asp?id]=3&pid]=8"};
    urls[553] = new String[] {
        "600676", "交运股份", "http://www.jygf.cn/cn/news/"};
    urls[554] = new String[] {
        "000700", "模塑科技",
        "http://www.jymosu.com/sinonews/index.asp?classid]=51&Nclassid]=78"};
    urls[555] = new String[] {
        "000021", "长城开发", "http://www.kaifa.com.cn/news/news.asp"};
    urls[556] = new String[] {
        "600537", "海通集团", "http://www.kaiz.com/News/newslist.asp?class]=11"};
    urls[557] = new String[] {
        "600518", "康美药业", "http://www.kangmei.com.cn/Class/dt/dt01.html"};
    urls[558] = new String[] {
        "002119", "康强电子", "http://www.kangqiang.com/dynamic/kanqiang_list.php"};
    urls[559] = new String[] {
        "600557", "康缘药业", "http://www.kanion.com/news/news_more.asp?lm2]=67"};
    urls[560] = new String[] {
        "600986", "科达股份",
        "http://www.keda-group.com.cn/xxlr.asp?tab]=&menuid]=241&menujb]=3"};
    urls[561] = new String[] {
        "600499", "科达机电", "http://www.kedagroup.com/"};
    urls[562] = new String[] {
        "000852", "江钻股份", "http://www.kingdream.com.cn/news_more.asp"};
    urls[563] = new String[] {
        "600143", "金发科技", "http://www.kingfa.com.cn/news.asp?nb]=97"};
    urls[564] = new String[] {
        "600390", "金瑞科技", "http://www.king-ray.com.cn/lh_news.asp"};
    urls[565] = new String[] {
        "600110", "中科英华", "http://www.kinwa.com.cn/news/xinwen-1.html"};
    urls[566] = new String[] {
        "600997", "开滦股份", "http://www.kkcc.com.cn:8010/cn/news/index.jsp"};
    urls[567] = new String[] {
        "002068", "黑猫股份", "http://www.kmzh.com/new/show1.asp?n_type]=0"};
    urls[568] = new String[] {
        "000048", "*ST康达", "http://www.kondarl.com/news.asp"};
    urls[569] = new String[] {
        "000016", "深康佳Ａ", "http://www.konka.com/ad/ad_news.jsp"};
    urls[570] = new String[] {
        "000979", "ST科苑", "http://www.koyogroup.com/article/4/14/index.html"};
    urls[571] = new String[] {
        "600422", "昆明制药", "http://www.kpc.com.cn/NewsShow/NewsSearchShow.aspx?name]=&typename]=%e9%9b%86%e5%9b%a2%e5%8a%a8%e6%80%81"};
    urls[572] = new String[] {
        "002106", "莱宝高科", "http://www.laibao.com.cn/news/news.asp"};
    urls[573] = new String[] {
        "600102", "莱钢股份", "http://www.laigang.com/otype.asp?owen1]=企业新闻"};
    urls[574] = new String[] {
        "000981", "S兰光",
        "http://www.languang.com/docc/news_list.asp?class_name]=公司动态&class_id]=76"};
    urls[575] = new String[] {
        "600328", "兰太实业",
        "http://www.lantaicn.com/news/bestnews.asp?lb_name]=公司新闻&lb_id]=14"};
    urls[576] = new String[] {
        "600209", "*ST罗顿", "http://www.lawtonfz.com.cn/zxdt/zxdt.htm"};
    urls[577] = new String[] {
        "600513", "联环药业", "http://www.lhpharma.com/News/Default.asp?LmBh]=16"};
    urls[578] = new String[] {
        "600285", "羚锐股份",
        "http://www.lingrui.com/script/moreNews.php?sortid]=1&lid]=1"};
    urls[579] = new String[] {
        "600885", "力诺太阳",
        "http://www.linuo-solar.com.cn/cn/news.asp?act]=list&channelid]=26"};
    urls[580] = new String[] {
        "000418", "小天鹅Ａ",
        "http://www.littleswan.com/news/news.asp?n_type]=企业动态"};
    urls[581] = new String[] {
        "000528", "柳工", "http://www.liugong.com.cn/"};
    urls[582] = new String[] {
        "601003", "柳钢股份", "http://www.liuzhousteel.com/Article_List.asp"};
    urls[583] = new String[] {
        "000513", "丽珠集团", "http://www.livzon.com.cn/news/livzon.jsp?catid]=65"};
    urls[584] = new String[] {
        "600765", "力源液压", "http://www.liyuanhydraulic.com/news.jsp"};
    urls[585] = new String[] {
        "600090", "ST啤酒花", "http://www.ljjn.com/news/news.htm"};
    urls[586] = new String[] {
        "600663", "陆家嘴",
        "http://www.ljz.com.cn/cn/news/list.asp?idtree]=.0.1.4."};
    urls[587] = new String[] {
        "600789", "鲁抗医药", "http://www.lkpc.com/news/index.jsp?synth_type_id]=3"};
    urls[588] = new String[] {
        "600985", "雷鸣科化", "http://www.lmkh.com/news/news.cfm"};
    urls[589] = new String[] {
        "600249", "两面针", "http://www.lmz.com.cn/news_hot.asp"};
    urls[590] = new String[] {
        "600241", "辽宁时代",
        "http://www.lntimes.cn/newEbiz1/EbizPortalFG/portal/html/InfoMultiPage.html"};
    urls[591] = new String[] {
        "000848", "承德露露", "http://www.lolo.com.cn/news.htm"};
    urls[592] = new String[] {
        "600853", "龙建股份", "http://www.longjianlq.com/index/zxxx.asp"};
    urls[593] = new String[] {
        "600388", "龙净环保", "http://www.longking.cn/qygk/xinwen.htm"};
    urls[594] = new String[] {
        "600352", "浙江龙盛", "http://www.longsheng.com/cn/default1.htm"};
    urls[595] = new String[] {
        "000523", "广州浪奇", "http://www.lonkey.com.cn/about/news.asp"};
    urls[596] = new String[] {
        "000998", "隆平高科", "http://www.lpht.com.cn/Gsdt.asp"};
    urls[597] = new String[] {
        "600592", "龙溪股份", "http://www.ls.com.cn/news.asp?type]=1"};
    urls[598] = new String[] {
        "600644", "乐山电力",
        "http://www.lsep.com.cn/gskx.asp?typeid]=15&BigClassid]=73"};
    urls[599] = new String[] {
        "000726", "鲁泰Ａ", "http://www.lttc.com.cn/News/newslist.asp?class]=2"};
    urls[600] = new String[] {
        "601699", "潞安环能", "http://www.luanhn.com/NobigClass.asp?typeid]=15"};
    urls[601] = new String[] {
        "600135", "S乐凯", "http://www.luckyfilm.com.cn/news.php?column]=%D0%C2%CE%C5%B6%AF%CC%AC&newsid]=5"};
    urls[602] = new String[] {
        "000735", "*ST罗牛",
        "http://www.luoniushan.com/newlns/news/more.php?infotype]=dt"};
    urls[603] = new String[] {
        "002088", "鲁阳股份", "http://www.luyangwool.com/News.asp?Mid]=48&Sid]=51"};
    urls[604] = new String[] {
        "000830", "鲁西化工", "http://www.lxchemical.com/browse/MainFrame.asp?MenuId]=2568&InfoId]=0&Find]=&Title]=&MainId]="};
    urls[605] = new String[] {
        "600783", "鲁信高新", "http://www.lxgx.com/"};
    urls[606] = new String[] {
        "600491", "龙元建设", "http://www.lycg.com.cn/cn/gsdt/nbdt/nbdt.htm"};
    urls[607] = new String[] {
        "600478", "力元新材", "http://www.lyrun.com/main/lyruninfo-list.asp?ty]=1"};
    urls[608] = new String[] {
        "600192", "长城电工",
        "http://www.lz-gwe.com.cn/docc/news.asp?department]=7"};
    urls[609] = new String[] {
        "600423", "柳化股份", "http://www.lzhg.cn/news/index.asp"};
    urls[610] = new String[] {
        "000568", "泸州老窖", "http://www.lzlj.com/news/news.aspx"};
    urls[611] = new String[] {
        "000533", "万家乐", "http://www.macro.com.cn/News/index.asp"};
    urls[612] = new String[] {
        "600808", "马钢股份", "http://www.magang.com.cn/list.asp?boardid]=11"};
    urls[613] = new String[] {
        "600980", "北矿磁材", "http://www.magmat.com/news_1.htm"};
    urls[614] = new String[] {
        "600107", "美尔雅", "http://www.mailyard.com.cn/lm.asp?big]=1"};
    urls[615] = new String[] {
        "600337", "美克股份", "http://www.markorfurniture.com/0410.html"};
    urls[616] = new String[] {
        "600993", "马应龙",
        "http://www.mayinglong.cn/news/newslist.aspx?newsid]=3"};
    urls[617] = new String[] {
        "000536", "SST闽东", "http://www.mddjg.com/xcfb.htm"};
    urls[618] = new String[] {
        "000782", "美达股份", "http://www.meidanylon.com/chinese/news-list.asp?ColumnCode]=001001&ColumnName]=企业新闻"};
    urls[619] = new String[] {
        "000521", "S美菱", "http://www.meiling.com/sm2111111346.asp"};
    urls[620] = new String[] {
        "600297", "美罗药业", "http://www.merro.com.cn/merrohealth/yellowpage/result.asp?stype]=1&txtkey]=&txtlanmu]=2&merro]=美罗动态"};
    urls[621] = new String[] {
        "002073", "青岛软控", "http://www.mesnac.com/news_company.asp"};
    urls[622] = new String[] {
        "000637", "S茂实华", "http://www.mhsh0637.com.cn/other/news.asp"};
    urls[623] = new String[] {
        "000527", "美的电器",
        "http://www.midea.com.cn/midea2005/news/news1.jsp?id]=4"};
    urls[624] = new String[] {
        "600235", "民丰特纸",
        "http://www.minfenggroup.com/newslist1.aspx?folderID]=1&foldername]=民丰动态"};
    urls[625] = new String[] {
        "002034", "美欣达",
        "http://www.mizuda.com/Article/ShowClass.asp?ClassID]=1"};
    urls[626] = new String[] {
        "600131", "岷江水电",
        "http://www.mjsdgs.com/bigClass.asp?typeid]=28&BigClassid]=107"};
    urls[627] = new String[] {
        "600543", "莫高股份",
        "http://www.mogao.com/Article/ShowClass.asp?ClassID]=4"};
    urls[628] = new String[] {
        "600519", "贵州茅台", "http://www.moutaichina.com/news/news.asp"};
    urls[629] = new String[] {
        "600101", "*ST明星",
        "http://www.mxdl.com.cn/news/gb/index.asp?BigClassName]=公司新闻"};
    urls[630] = new String[] {
        "000976", "春晖股份", "http://www.my0976.com/news/gsxw.asp"};
    urls[631] = new String[] {
        "000737", "南风化工", "http://www.nafine.com/News_Title.php?topic]=南风新闻"};
    urls[632] = new String[] {
        "600163", "福建南纸", "http://www.nanping-paper.com/news.htm"};
    urls[633] = new String[] {
        "600250", "南纺股份", "http://www.nantex.com.cn/ch/news/news.php"};
    urls[634] = new String[] {
        "000948", "南天信息", "http://www.nantian.com.cn/nantian/templet/templet1/newslist.asp?Areano]=001&classno]=110"};
    urls[635] = new String[] {
        "600406", "国电南瑞", "http://www.naritech.cn/news/subject.asp?lei]=新闻动态"};
    urls[636] = new String[] {
        "600798", "宁波海运", "http://www.nbmc.com.cn/news/news_more.asp?lm]=51&lm2]=52&open]=_blank&tj]=0&hot]=0"};
    urls[637] = new String[] {
        "600889", "南京化纤", "http://www.ncfc.cn/news.asp"};
    urls[638] = new String[] {
        "600812", "华北制药",
        "http://www.ncpc.com.cn/news/News_List_Lanmu.asp?lanmu_ID]=1"};
    urls[639] = new String[] {
        "000585", "东北电气", "http://www.nee.com.cn/news.php?id]=1"};
    urls[640] = new String[] {
        "000597", "东北制药",
        "http://www.negpf.com.cn/Article/ShowClass.asp?ClassID]=1"};
    urls[641] = new String[] {
        "000078", "海王生物", "http://www.neptunus.com/xw.php"};
    urls[642] = new String[] {
        "600718", "东软股份", "http://www.neusoft.com/news/index.jsp?type]=41"};
    urls[643] = new String[] {
        "000876", "新希望", "http://www.newhopegroup.com/newhope.asp?id]=8"};
    urls[644] = new String[] {
        "000997", "新大陆", "http://www.newlandcomputer.com/PubInfo/PubInfoDo/pageDefineAct.do?pageDefineKey]=2&containerKey]=3#"};
    urls[645] = new String[] {
        "600975", "新五丰", "http://www.newwf.com/xwzx/new.asp"};
    urls[646] = new String[] {
        "600628", "新世界", "http://www.newworld-china.com/htm/yaowen1.asp"};
    urls[647] = new String[] {
        "000758", "中色股份", "http://www.nfc.com.cn/col23/col36/index.htm1?id]=36"};
    urls[648] = new String[] {
        "000906", "S南建材", "http://www.nfjc.com.cn/nfxw/nfxw_02.asp?classid]=1"};
    urls[649] = new String[] {
        "600323", "南海发展", "http://www.nhd.net.cn/news.htm"};
    urls[650] = new String[] {
        "600555", "九龙山", "http://www.ninedragon.com.cn/news.html"};
    urls[651] = new String[] {
        "002040", "南京港", "http://www.nj-port.com/news/newsmoretitle.php3?func]=DispMoreTit&NewsClassid]=2&date]=&mod]=6&img]=1"};
    urls[652] = new String[] {
        "600713", "南京医药", "http://www.njyy.com/njyyb_bt.asp?bm]=030101"};
    urls[653] = new String[] {
        "600301", "南化股份", "http://www.nnchem.com/cn/news.asp?type]=1"};
    urls[654] = new String[] {
        "000911", "南宁糖业", "http://www.nnsugar.com/news_zx.asp"};
    urls[655] = new String[] {
        "000060", "中金岭南", "http://www.nonfemet.com/chinese/news-list.asp?ColumnCode]=001001&ColumnName]=企业新闻"};
    urls[656] = new String[] {
        "600817", "宏盛科技", "http://www.norcent.com.cn/chinese/News.asp"};
    urls[657] = new String[] {
        "000065", "北方国际", "http://www.norinco-intl.com/cn/news/index.asp"};
    urls[658] = new String[] {
        "600003", "东北高速",
        "http://www.northeast-expressway.cn/class3.asp?classid]=23"};
    urls[659] = new String[] {
        "002014", "永新股份", "http://www.novel.com.cn/xwzx2.asp?lbid]=102101"};
    urls[660] = new String[] {
        "000037", "深南电Ａ", "http://www.nsrd.com.cn/China/Htm/NewsCenter/list.asp?type]=%B9%AB%CB%BE%D0%C2%CE%C5"};
    urls[661] = new String[] {
        "002089", "新海宜", "http://www.nsu.com.cn/news.html"};
    urls[662] = new String[] {
        "600087", "南京水运",
        "http://www.nwti.com.cn/admin/news/default.asp?cataid]=1"};
    urls[663] = new String[] {
        "600165", "宁夏恒力", "http://www.nxhengli.com.cn/News.asp"};
    urls[664] = new String[] {
        "000595", "西北轴承", "http://www.nxz.com.cn/file/page_2.jsp?code]=00006"};
    urls[665] = new String[] {
        "000069", "华侨城Ａ", "http://www.octholding.com/news/index.php"};
    urls[666] = new String[] {
        "600278", "东方创业", "http://www.oie.com.cn/NEWS/NEWS.htm"};
    urls[667] = new String[] {
        "600832", "东方明珠", "http://www.opg.cn/news_main.php"};
    urls[668] = new String[] {
        "000962", "东方钽业", "http://www.otic.com.cn/news_list.asp"};
    urls[669] = new String[] {
        "601318", "中国平安", "http://www.pa18.com/pa18Web/framework/aboutus.jsp?content]=/pa18Web/aboutus/cn/news_blackout.jsp&advert]=/pa18Web/aboutus/cn/adv_abouts.jsp"};
    urls[670] = new String[] {
        "600284", "浦东建设",
        "http://www.pdjs.com.cn/template/template_1/news/list.asp?Cata_Id]=92&"};
    urls[671] = new String[] {
        "600529", "山东药玻", "http://www.pharmglass.com/news.aspx"};
    urls[672] = new String[] {
        "600078", "澄星股份",
        "http://www.phosphatechina.com/aaa/news/dynNewsList.htm"};
    urls[673] = new String[] {
        "000697", "咸阳偏转", "http://www.pianzhuan.com.cn/pianzxw.asp"};
    urls[674] = new String[] {
        "600312", "平高电气", "http://www.pinggao.com/news/news.asp"};
    urls[675] = new String[] {
        "600114", "东睦股份", "http://www.pm-china.com/cn-f-1.php?type]=2"};
    urls[676] = new String[] {
        "600018", "上港集团", "http://www.portshanghai.com.cn/sipg/listbankuai.php"};
    urls[677] = new String[] {
        "600717", "天津港", "http://www.ptacn.com/news/userlist.asp?type]=xwfb"};
    urls[678] = new String[] {
        "000515", "攀渝钛业", "http://www.pyty.cn/news.asp"};
    urls[679] = new String[] {
        "000629", "攀钢钢钒", "http://www.pzhsteel.com.cn/xwzx/gsyw.aspx"};
    urls[680] = new String[] {
        "600248", "*ST秦丰",
        "http://www.qfny.com/qfxw.asp?InfoClassID]=7&InfoClassName]=公司新闻"};
    urls[681] = new String[] {
        "600479", "千金药业", "http://www.qian-jin.com/docc/news2.asp"};
    urls[682] = new String[] {
        "600576", "*ST庆丰", "http://www.qingfengchina.com/template/qfxw.htm"};
    urls[683] = new String[] {
        "600698", "SST轻骑", "http://www.qingqi.com.cn/qqr.asp"};
    urls[684] = new String[] {
        "600103", "青山纸业", "http://www.qingshan.com.cn/News.asp"};
    urls[685] = new String[] {
        "600217", "*ST秦岭", "http://www.qinling.com/news.asp"};
    urls[686] = new String[] {
        "000913", "钱江摩托", "http://www.qjmotor.com/News.asp"};
    urls[687] = new String[] {
        "600283", "钱江水利", "http://www.qjwater.com/news/news.html"};
    urls[688] = new String[] {
        "600568", "*ST潜药", "http://www.qjzy.com/more.asp"};
    urls[689] = new String[] {
        "600720", "祁连山", "http://www.qlssn.com/asp/news/more.asp"};
    urls[690] = new String[] {
        "600218", "全柴动力", "http://www.quanchai.com.cn/news.asp"};
    urls[691] = new String[] {
        "600649", "原水股份", "http://www.rawwater.com.cn/news/index.asp"};
    urls[692] = new String[] {
        "600439", "瑞贝卡", "http://www.rebecca.com.cn/rbkgu/servlet/ForumServlet?jumpPage]=1&url]=/index/newlist.jsp"};
    urls[693] = new String[] {
        "600111", "稀土高科", "http://www.reht.com/new/list.php"};
    urls[694] = new String[] {
        "600879", "火箭股份", "http://www.rocketstock.com.cn/website/XinWenZhongXinList.aspx?PageSize]=21&TitleNumber]=30&ImgType]=0&CateID]=19&CateName]="};
    urls[695] = new String[] {
        "002123", "荣信股份", "http://www.rxpe.com/news/index.asp"};
    urls[696] = new String[] {
        "600848", "自仪股份", "http://www.sac-china.com/news/index.php"};
    urls[697] = new String[] {
        "600268", "国电南自", "http://www.sac-china.com/news/index.php"};
    urls[698] = new String[] {
        "600482", "风帆股份", "http://www.sail.com.cn/xinwen/xinwendt.asp"};
    urls[699] = new String[] {
        "600449", "赛马实业", "http://www.saimasy.com.cn/newscode.asp?lm]=0&lm2]=18&hot]=0&tj]=0&t]=0&week]=0&font]=9&line]=14&lmname]=0&n]=50&list]=16&more]=1&hit]=0&open]=1&icon]=1&bg]=ffffff"};
    urls[700] = new String[] {
        "002112", "三变科技", "http://www.sanbian.cn/company_wf.asp"};
    urls[701] = new String[] {
        "000970", "中科三环", "http://www.san-huan.com.cn/touzizhe_1.asp?id]=3"};
    urls[702] = new String[] {
        "600829", "三精制药", "http://www.sanjing.com.cn/company2_1.asp"};
    urls[703] = new String[] {
        "000632", "三木集团", "http://www.san-mu.com/news.asp"};
    urls[704] = new String[] {
        "000565", "渝三峡Ａ",
        "http://www.sanxia.com/News.aspx?l]=0&p]=186&c]=186&s]=0&u]=1&f]=News"};
    urls[705] = new String[] {
        "600031", "三一重工", "http://www.sany.com.cn/zg/china/sys/default.jsp?qxid]=01010201&sjid]=010102010201&menuid]=1"};
    urls[706] = new String[] {
        "002044", "江苏三友",
        "http://www.sanyou-chem.com.cn/chpage/c607/doclist.htm"};
    urls[707] = new String[] {
        "600429", "ST三元", "http://www.sanyuan.com.cn/channel.php?id]=21"};
    urls[708] = new String[] {
        "000019", "深深宝Ａ", "http://www.sbsy.com.cn/news/zixun_gsxw.asp"};
    urls[709] = new String[] {
        "002098", "浔兴股份", "http://www.sbszipper.com.cn/xwzx/xxxw/"};
    urls[710] = new String[] {
        "600979", "广安爱众", "http://www.sc-aaa.com/ywxx02.asp"};
    urls[711] = new String[] {
        "600618", "氯碱化工", "http://www.scacc.com/xwgg/news.htm"};
    urls[712] = new String[] {
        "600391", "成发科技",
        "http://www.scfast.com/News/InfoList.aspx?Category]=2"};
    urls[713] = new String[] {
        "600170", "上海建工", "http://www.scg.com.cn/jggf/xwzx.asp"};
    urls[714] = new String[] {
        "002023", "海特高新", "http://www.schtgx.com/more.asp?ttt]=3&sss]=海特新闻"};
    urls[715] = new String[] {
        "000586", "汇源通信",
        "http://www.schy.com.cn/docc/news_class.asp?newscontent_id]=58"};
    urls[716] = new String[] {
        "600678", "四川金顶", "http://www.scjd.cn/N0000231_more.aspx"};
    urls[717] = new String[] {
        "000912", "泸天化", "http://www.sclth.com/news.asp"};
    urls[718] = new String[] {
        "600984", "*ST建机", "http://www.scmc-xa.com/Scmc_Chinese/scmc_zong1.htm"};
    urls[719] = new String[] {
        "000731", "四川美丰",
        "http://www.scmeif.com/gb/news/newslist.asp?class]=50&ParentID]="};
    urls[720] = new String[] {
        "600674", "川投能源", "http://www.scte.com.cn/p2.asp"};
    urls[721] = new String[] {
        "000155", "川化股份", "http://www.scwltd.com/more_news.php"};
    urls[722] = new String[] {
        "000001", "S深发展A", "http://www.sdb.com.cn/sdbsite/category/66696c6573/77636d73/534442/7a68/7a685f434e/534442496e666f/5344424e657773"};
    urls[723] = new String[] {
        "600841", "上柴股份", "http://www.sdec.com.cn/sec.asp?tuneid]=7"};
    urls[724] = new String[] {
        "600350", "山东高速",
        "http://www.sdecl.com.cn/news.asp?bigclassname]=%B9%AB%CB%BE%D0%C2%CE%C5"};
    urls[725] = new String[] {
        "000070", "特发信息", "http://www.sdgi.com.cn/news/news.asp?flag]=1"};
    urls[726] = new String[] {
        "600467", "好当家", "http://www.sdhaodangjia.com/news.asp"};
    urls[727] = new String[] {
        "600886", "国投电力", "http://www.sdicpower.com/main.asp?action]=pu"};
    urls[728] = new String[] {
        "600962", "国投中鲁", "http://www.sdiczl.com/news/index.asp"};
    urls[729] = new String[] {
        "000655", "金岭矿业", "http://www.sdjlky.com/Class.asp?ClassID]=97"};
    urls[730] = new String[] {
        "000720", "鲁能泰山",
        "http://www.sdlnts.com/Article_class2.asp?ClassID]=15&sid]=15"};
    urls[731] = new String[] {
        "000915", "山大华特", "http://www.sd-wit.com/xinwenzhongxin73.htm"};
    urls[732] = new String[] {
        "002084", "海鸥卫浴", "http://www.seagullgroup.cn/new.jsp"};
    urls[733] = new String[] {
        "000503", "海虹控股", "http://www.searainbow.com/more.asp?moreImg]=&catalogid]=最新动态&img]=pot_pink.gif&symbol]=&nextpage]=t&site]=www.searainbow.com&len]=20"};
    urls[734] = new String[] {
        "600185", "海星科技", "http://www.seastar.net.cn/new_zy_sort.asp?leibie]=a"};
    urls[735] = new String[] {
        "000032", "深桑达Ａ", "http://www.sedind.com/news.asp"};
    urls[736] = new String[] {
        "000058", "*ST赛格", "http://www.segcl.com.cn/article.php"};
    urls[737] = new String[] {
        "002029", "七匹狼", "http://www.septwolves.com/news.htm"};
    urls[738] = new String[] {
        "600303", "曙光股份",
        "http://www.sgautomotive.com/html/01_sgxw/news/index.asp"};
    urls[739] = new String[] {
        "000959", "首钢股份",
        "http://www.sggf.com.cn/asp-bin/GB/index.asp?page]=7&class]=37"};
    urls[740] = new String[] {
        "002110", "三钢闽光", "http://www.sgmg.com.cn/mgnews/gsxw/xwdt/xwdt1.htm"};
    urls[741] = new String[] {
        "600843", "上工申贝", "http://www.sgsbgroup.com/news.asp"};
    urls[742] = new String[] {
        "600636", "三爱富",
        "http://www.sh3f.com/sf/internet/ShowListServlet?menucd]=A002"};
    urls[743] = new String[] {
        "000014", "沙河股份", "http://www.shahe.cn/cn/News_dynamic.asp"};
    urls[744] = new String[] {
        "600009", "上海机场", "http://www.shairport.com/travel/news.jsp"};
    urls[745] = new String[] {
        "600882", "大成股份", "http://www.shandongdacheng.com/news/index.asp"};
    urls[746] = new String[] {
        "000967", "*ST上风", "http://www.shangfeng.net/cn-e-1.php"};
    urls[747] = new String[] {
        "600591", "上海航空", "http://www.shanghai-air.com/shdt.html"};
    urls[748] = new String[] {
        "600630", "龙头股份", "http://www.shanghaidragon.com.cn/news/"};
    urls[749] = new String[] {
        "600073", "上海梅林", "http://www.shanghaimaling.com/newEbiz1/EbizPortalFG/portal/html/InfoCategoryInfoMultiPage.html?InfoCategoryInfoList150_action]=more&InfoPublish_CategoryID]=c373e908fb3406128f6ed50404cc03bd&InfoCategoryInfoList150_InfoCurCategoryID]=c373e908fb3406128f6ed50404cc03bd"};
    urls[750] = new String[] {
        "000680", "山推股份", "http://www.shantui.com/news.asp"};
    urls[751] = new String[] {
        "600567", "山鹰纸业", "http://www.shanyingpaper.com/news.asp"};
    urls[752] = new String[] {
        "000601", "韶能股份", "http://www.shaoneng.com.cn/moneyman/gg.asp"};
    urls[753] = new String[] {
        "600059", "古越龙山", "http://www.shaoxingwine.com.cn/jt/news.asp"};
    urls[754] = new String[] {
        "600849", "上海医药",
        "http://www.shaphar.com.cn:9080/shapharWeb/default/news.jsp"};
    urls[755] = new String[] {
        "600850", "华东电脑", "http://www.shecc.com/xwzx.asp"};
    urls[756] = new String[] {
        "600604", "二纺机", "http://www.shefj.com/html/news_list.asp"};
    urls[757] = new String[] {
        "000933", "神火股份", "http://www.shenhuo.com/much0.asp?id]=2"};
    urls[758] = new String[] {
        "600823", "世茂股份", "http://www.shimao.com.cn/morenews.asp"};
    urls[759] = new String[] {
        "600587", "新华医疗", "http://www.shinva.com/xw/DetailList.asp?id]=004"};
    urls[760] = new String[] {
        "600653", "申华控股", "http://www.shkg.com.cn/news/news.asp"};
    urls[761] = new String[] {
        "000707", "双环科技", "http://www.shkj.cn/docc/news/news.asp"};
    urls[762] = new String[] {
        "600680", "上海普天",
        "http://www.shpte.com.cn/main?main_colid]=214&colid]=215"};
    urls[763] = new String[] {
        "600620", "天宸股份", "http://www.shstc.com/news/news.htm"};
    urls[764] = new String[] {
        "600834", "申通地铁", "http://www.shtmetro.com/xwfb/main.asp"};
    urls[765] = new String[] {
        "000835", "四川圣达",
        "http://www.shuangdeng.com.cn/news/news.asp?n_type]=9"};
    urls[766] = new String[] {
        "000895", "S双汇", "http://www.shuanghui.net/www/news/index.html"};
    urls[767] = new String[] {
        "600481", "双良股份", "http://www.shuangliang.com/newpage/gsxw.htm"};
    urls[768] = new String[] {
        "600648", "外高桥", "http://www.shwgq.com/list_titleb.asp"};
    urls[769] = new String[] {
        "600757", "*ST源发", "http://www.shworldbest.com/news.asp"};
    urls[770] = new String[] {
        "600732", "上海新梅", "http://www.shxinmei.com/cn/news/"};
    urls[771] = new String[] {
        "600748", "上实发展", "http://www.sidlgroup.com/gsyw/index.htm"};
    urls[772] = new String[] {
        "600460", "士兰微", "http://www.silan.com.cn/info/info.aspx"};
    urls[773] = new String[] {
        "000301", "丝绸股份", "http://www.silkgroup.com/html/info.htm"};
    urls[774] = new String[] {
        "600270", "外运发展", "http://www.sinoair.com/chn/02/0201/"};
    urls[775] = new String[] {
        "600500", "中化国际", "http://www.sinochemintl.com/cn/2media/1news.asp"};
    urls[776] = new String[] {
        "600970", "中材国际",
        "http://www.sinoma.com.cn/news_list.asp?sort]=%B9%AB%CB%BE%B6%AF%CC%AC"};
    urls[777] = new String[] {
        "002080", "中材科技", "http://www.sinomatech.com/html-cn/news-list.php"};
    urls[778] = new String[] {
        "000877", "天山股份", "http://www.sinoma-tianshan.cn/News.asp"};
    urls[779] = new String[] {
        "600028", "中国石化", "http://www.sinopec.com/newsevent/index.shtml"};
    urls[780] = new String[] {
        "600459", "贵研铂业", "http://www.sino-platinum.com.cn/news.htm"};
    urls[781] = new String[] {
        "600061", "中纺投资",
        "http://www.sinotex-ctrc.com.cn/news_list.asp?type]=3"};
    urls[782] = new String[] {
        "600607", "上实医药",
        "http://www.siph.com.cn/(ywqu5bylvbir1o455k1ozdvs)/Default.aspx"};
    urls[783] = new String[] {
        "000563", "陕国投Ａ", "http://www.siti.com.cn/news.asp?C_rootID]=A00160005"};
    urls[784] = new String[] {
        "002022", "科华生物", "http://www.skhb.com/cn/news/"};
    urls[785] = new String[] {
        "000783", "S*ST石炼", "http://www.slhec.com/"};
    urls[786] = new String[] {
        "002038", "双鹭药业",
        "http://www.slpharm.com.cn/web/news/news.jsp?NewsType_ID]=21001"};
    urls[787] = new String[] {
        "000410", "沈阳机床", "http://www.smtcl.com/web/news/"};
    urls[788] = new String[] {
        "002109", "兴化股份", "http://www.snxhchem.com/chpage/c701/doclist.asp"};
    urls[789] = new String[] {
        "600746", "江苏索普", "http://www.sopo.com.cn/news.asp?typeid]=1"};
    urls[790] = new String[] {
        "000920", "*ST汇通",
        "http://www.southhuiton.com/pages/xwzx/default.asp?xwfl]=1"};
    urls[791] = new String[] {
        "000909", "数源科技", "http://www.soyea.com.cn/allnews.asp?type]=hot"};
    urls[792] = new String[] {
        "600118", "中国卫星", "http://www.spacesat.com.cn/3_News/nw_default.htm"};
    urls[793] = new String[] {
        "002064", "华峰氨纶", "http://www.spandex.com.cn/cn/news/"};
    urls[794] = new String[] {
        "600688", "S上石化",
        "http://www.spc.com.cn/cnspc/newsroommore.php?cid]=119&Dlev]=3"};
    urls[795] = new String[] {
        "600000", "浦发银行", "http://www.spdb.com.cn/chpage/c446/doclist.aspx"};
    urls[796] = new String[] {
        "600421", "春天股份", "http://www.spring.com.cn/news.asp"};
    urls[797] = new String[] {
        "600627", "上电股份", "http://www.sptd.com.cn/News/newslist.asp?class]=1"};
    urls[798] = new String[] {
        "000068", "S三星", "http://www.ssg.com.cn/news.asp"};
    urls[799] = new String[] {
        "600884", "杉杉股份", "http://www.ssgf.net/news.asp"};
    urls[800] = new String[] {
        "000788", "西南合成", "http://www.sspgf.com/news/index.htm"};
    urls[801] = new String[] {
        "000676", "思达高科", "http://www.starhi-tech.com/more/more.asp?typeid]=11&typename]=%CB%BC%B4%EF%B6%AF%CC%AC"};
    urls[802] = new String[] {
        "600820", "隧道股份", "http://www.stec.net/Directorate/Directorate.asp"};
    urls[803] = new String[] {
        "000982", "S*ST雪绒", "http://www.st-edenw.com/news.asp"};
    urls[804] = new String[] {
        "600633", "白猫股份", "http://www.stof.com.cn/news/news.asp"};
    urls[805] = new String[] {
        "600593", "大连圣亚", "http://www.sunasia.com/sunasia/news.asp"};
    urls[806] = new String[] {
        "600990", "四创电子", "http://www.sun-create.com/sc/news/gsxw.php"};
    urls[807] = new String[] {
        "000571", "新大洲Ａ", "http://www.sundiro.com/main.asp?Url]=news&UrlID]=2"};
    urls[808] = new String[] {
        "002078", "太阳纸业", "http://www.sunpapergroup.com/express/index.htm"};
    urls[809] = new String[] {
        "600728", "S*ST新太",
        "http://www.suntektech.com/apps/news/news_center.asp?typename]=news_44"};
    urls[810] = new String[] {
        "002083", "孚日股份", "http://www.sunvim.com/news-center.asp"};
    urls[811] = new String[] {
        "002097", "山河智能", "http://www.sunward.com.cn/xwzx/xwbb.asp"};
    urls[812] = new String[] {
        "002115", "三维通信", "http://www.sunwave.com.cn/web/news.asp?FirstKind]=MT_00002_100017458&KindID]=MT_00002_100017477&KindID2]=MT_00002_100017478"};
    urls[813] = new String[] {
        "600571", "信雅达", "http://www.sunyard.com/news/company.jsp"};
    urls[814] = new String[] {
        "000608", "阳光股份", "http://www.supershine.com.cn/news/ygdt.jsp"};
    urls[815] = new String[] {
        "002032", "苏泊尔", "http://www.supor.com.cn/news/list.asp?id]=1"};
    urls[816] = new String[] {
        "600602", "广电电子", "http://www.sva-e.com/cn/news/"};
    urls[817] = new String[] {
        "600637", "广电信息", "http://www.svainfo.com/cn/news/news03.asp"};
    urls[818] = new String[] {
        "600145", "四维瓷业", "http://www.swell.com.cn/dt.aspx"};
    urls[819] = new String[] {
        "000831", "关铝股份", "http://www.sxglgf.com/NEWS/news_company.asp"};
    urls[820] = new String[] {
        "600740", "山西焦化", "http://www.sxjh.com.cn/docc/new/news.asp"};
    urls[821] = new String[] {
        "600293", "三峡新材",
        "http://www.sxxc.com.cn/newEbiz1/EbizPortalFG/portal/html/info-gsxw.html"};
    urls[822] = new String[] {
        "000698", "沈阳化工",
        "http://www.sychem.com/cn/company/news.asp?category]=2"};
    urls[823] = new String[] {
        "002028", "思源电气", "http://www.syec.com.cn/news/"};
    urls[824] = new String[] {
        "600490", "中科合臣", "http://www.synica.com.cn/zk/cn/aboutus.asp?id]=26"};
    urls[825] = new String[] {
        "600819", "耀皮玻璃", "http://www.sypglass.com/news.asp"};
    urls[826] = new String[] {
        "600183", "生益科技",
        "http://www.syst.com.cn/web/syst.nsf/program/004001?opendocument"};
    urls[827] = new String[] {
        "000028", "一致药业", "http://www.szaccord.com.cn/test/docc/news/news.php?PHPSESSID]=ce5ca6af121f3c9d80984cc31e6c2ceb"};
    urls[828] = new String[] {
        "000089", "深圳机场", "http://www.szairport.com/Catalog_211.aspx?t]=2433"};
    urls[829] = new String[] {
        "000061", "农产品", "http://www.szap.com/news/gsxw.asp"};
    urls[830] = new String[] {
        "002047", "成霖股份",
        "http://www.szcl.com.cn/news/disp.asp?tid]=7&title]=公司新闻"};
    urls[831] = new String[] {
        "002121", "科陆电子", "http://www.szclou.com/kelu/news_next.asp?yw]=y"};
    urls[832] = new String[] {
        "000022", "深赤湾Ａ", "http://www.szcwh.com/webapp/main.asp?sid]=118"};
    urls[833] = new String[] {
        "600548", "深高速", "http://www.sz-expressway.com/docc/tzzhd_xwzx.php"};
    urls[834] = new String[] {
        "600446", "金证股份",
        "http://www.szkingdom.com/news/moreNews.asp?newsSort]=公司动态"};
    urls[835] = new String[] {
        "000011", "S深物业A",
        "http://www.szwuye.com.cn/docc/xinwen/dongtai.asp?sort]=集团新闻"};
    urls[836] = new String[] {
        "600129", "太极集团", "http://www.taiji.com/allnews.asp"};
    urls[837] = new String[] {
        "600222", "太龙药业",
        "http://www.taloph.com/news/showNews.asp?typeid]=134479872"};
    urls[838] = new String[] {
        "600665", "天地源", "http://www.tande.cn/news.asp"};
    urls[839] = new String[] {
        "000856", "唐山陶瓷", "http://www.tangshanceramic.com/news.cfm"};
    urls[840] = new String[] {
        "000709", "唐钢股份",
        "http://www.tangsteel.com.cn/list.jsp?nav2_Id]=33&&navId]=137"};
    urls[841] = new String[] {
        "600535", "天士力", "http://www.tasly.com/list.aspx?cid]=107"};
    urls[842] = new String[] {
        "600089", "特变电工",
        "http://www.tbea.com.cn/Modules/News/TextNews/Default.aspx?SortID]=1"};
    urls[843] = new String[] {
        "000100", "*STTCL", "http://www.tcl.com/main/NEWS/groupNews/"};
    urls[844] = new String[] {
        "002100", "天康生物", "http://www.tcsw.com.cn/otype.asp?owen1]=新闻动态"};
    urls[845] = new String[] {
        "600330", "天通股份", "http://www.tdgcore.com/xwzx/index.asp?cat_id]=1"};
    urls[846] = new String[] {
        "600582", "天地科技", "http://www.tdtec.com/News/News_Index.asp?Type]=In"};
    urls[847] = new String[] {
        "000630", "铜都铜业",
        "http://www.tdty.com/appframe.asp?list1]=info&list2]=gsxx"};
    urls[848] = new String[] {
        "600410", "华胜天成", "http://www.teamsun.com.cn/index.php?option]=com_content&task]=category&sectionid]=5&id]=15&Itemid]=112"};
    urls[849] = new String[] {
        "000555", "*ST太光", "http://www.techo.cn/news/default.asp"};
    urls[850] = new String[] {
        "000652", "泰达股份", "http://www.tedastock.com/news1.asp"};
    urls[851] = new String[] {
        "600590", "泰豪科技", "http://www.tellhow.com/news.asp"};
    urls[852] = new String[] {
        "000025", "特力Ａ", "http://www.tellus.cn/tl/news.aspx"};
    urls[853] = new String[] {
        "600512", "腾达建设", "http://www.tengdajs.com/xw.htm"};
    urls[854] = new String[] {
        "600322", "天房发展", "http://www.tfgroup.com.cn/newscenter/index.htm"};
    urls[855] = new String[] {
        "600509", "天富热电",
        "http://www.tfrd.com.cn/news_list.asp?c_id]=8&s_id]=134"};
    urls[856] = new String[] {
        "600867", "通化东宝", "http://www.thdb.com/list.aspx?cid]=17"};
    urls[857] = new String[] {
        "000766", "通化金马", "http://www.thjm.cn/news/list_show.php?sortid]=4"};
    urls[858] = new String[] {
        "600100", "同方股份", "http://www.thtf.com.cn/www/web/news/index_news.aspx"};
    urls[859] = new String[] {
        "000938", "紫光股份",
        "http://www.thunis.com/thunis/news/news/2007/news.htm"};
    urls[860] = new String[] {
        "002124", "天邦股份", "http://www.tianbang.com/news/index.asp?ClassID]=5"};
    urls[861] = new String[] {
        "600791", "天创置业",
        "http://www.tianchuang-zy.com/news/index.php?c_id]=87"};
    urls[862] = new String[] {
        "600376", "天鸿宝业",
        "http://www.tianhong-baoye.com.cn/news/all.asp?Dname]=公司新闻&flag]=1"};
    urls[863] = new String[] {
        "600378", "天科股份", "http://www.tianke.com/news.htm"};
    urls[864] = new String[] {
        "000050", "深天马Ａ", "http://www.tianma.cn/docc/news/gsdt.asp"};
    urls[865] = new String[] {
        "600435", "北方天鸟",
        "http://www.tianniao.com.cn/chinese/morenews.asp?publishColumn]=公司新闻"};
    urls[866] = new String[] {
        "600703", "S*ST天颐", "http://www.tianyi.cc/Html/NewsList.asp"};
    urls[867] = new String[] {
        "600749", "西藏圣地", "http://www.tibetshengdi.com/xzsd/dtxx.asp"};
    urls[868] = new String[] {
        "600392", "太工天成", "http://www.tichn.com/news/news.pl"};
    urls[869] = new String[] {
        "000917", "电广传媒", "http://www.tik.com.cn/z_x_g.asp"};
    urls[870] = new String[] {
        "600874", "创业环保", "http://www.tjcep.com/news/InfoList.aspx?Category]=6"};
    urls[871] = new String[] {
        "000927", "一汽夏利", "http://www.tjfaw.com.cn/news/index.asp"};
    urls[872] = new String[] {
        "000711", "天伦置业", "http://www.tlzy.com.cn/news.asp"};
    urls[873] = new String[] {
        "000090", "深天健", "http://www.tonge.com.cn/docc/news/groupnews.asp"};
    urls[874] = new String[] {
        "600237", "铜峰电子",
        "http://www.tong-feng.com/tfnews/more.asp?ttt]=4&sss]=公司新闻"};
    urls[875] = new String[] {
        "600438", "通威股份", "http://www.tongwei.com.cn/news/crop.asp"};
    urls[876] = new String[] {
        "600862", "S*ST通科", "http://www.tonmac.com.cn/cpl/news.php"};
    urls[877] = new String[] {
        "600253", "天方药业",
        "http://www.topfond.com/cn/news/newslist.asp?catalog]=1"};
    urls[878] = new String[] {
        "002134", "天津普林", "http://www.toppcb.com/news/new-shiji.asp"};
    urls[879] = new String[] {
        "600771", "东盛科技", "http://www.topsun.com/tdsxw.asp"};
    urls[880] = new String[] {
        "600233", "大杨创世", "http://www.trands.com/news.asp"};
    urls[881] = new String[] {
        "002010", "传化股份", "http://www.transfarchem.com/cgi/search-cn.cgi?f]=news_cn+company_cn_1_&t]=news_cn&w]=news_cn&cate1]=公司动态"};
    urls[882] = new String[] {
        "000883", "三环股份", "http://www.triring.cn/xwfb/xwfb.aspx?catagory]=184"};
    urls[883] = new String[] {
        "600458", "时代新材", "http://www.trp.cn/list_b.asp?b_id]=4"};
    urls[884] = new String[] {
        "600600", "青岛啤酒", "http://www.tsingtao.com.cn/list/www_tsingtao_com_cn/2004/cn/information/news.jsp"};
    urls[885] = new String[] {
        "002117", "东港股份", "http://www.tungkong.com.cn/news05.asp"};
    urls[886] = new String[] {
        "600737", "中粮屯河", "http://www.tunhe.com/cn/news/class0.asp?ClassID]=5"};
    urls[887] = new String[] {
        "002057", "中钢天源", "http://www.tunhe.com/cn/news/class0.asp?ClassID]=5"};
    urls[888] = new String[] {
        "600550", "天威保变",
        "http://www.twbb.com/web/news.asp?bid]=2&sid]=23&bcid]=2"};
    urls[889] = new String[] {
        "000795", "太原刚玉", "http://www.twin-tower.com/news/gsxw.htm"};
    urls[890] = new String[] {
        "600488", "天药股份", "http://www.tygf-jy.com/xinwen.asp"};
    urls[891] = new String[] {
        "000036", "华联控股", "http://www.udcgroup.com/news.asp"};
    urls[892] = new String[] {
        "600588", "用友软件", "http://www.ufida.com.cn/news/list_quanbu.aspx"};
    urls[893] = new String[] {
        "000932", "华菱管线", "http://www.valin.cn/xwmore.asp"};
    urls[894] = new String[] {
        "000002", "万科Ａ", "http://www.vanke.com/main/catalogNews_10633.aspx"};
    urls[895] = new String[] {
        "600246", "万通先锋", "http://www.vantonepioneer.com.cn/news.asp"};
    urls[896] = new String[] {
        "600152", "维科精华", "http://www.vekenelite.com/news/"};
    urls[897] = new String[] {
        "600803", "威远生化", "http://www.veyong.com/xinwenzx.asp"};
    urls[898] = new String[] {
        "000407", "胜利股份", "http://www.vicome.com/Article/Index.asp"};
    urls[899] = new String[] {
        "600300", "维维股份", "http://www.vvgroup.com/news/index.php"};
    urls[900] = new String[] {
        "600055", "万东医疗", "http://www.wandong.com.cn/gk_qydt2.htm"};
    urls[901] = new String[] {
        "600223", "*ST万杰", "http://www.wanjie.com/html/news/index.htm"};
    urls[902] = new String[] {
        "600847", "ST渝万里", "http://www.wanli.net.cn/news.asp"};
    urls[903] = new String[] {
        "000544", "中原环保",
        "http://www.wdg.com.cn/listnewsnormal.html?newsColumnId]=1"};
    urls[904] = new String[] {
        "000338", "潍柴动力", "http://www.weichai.com/about/channel/news.shtml"};
    urls[905] = new String[] {
        "002026", "山东威达", "http://www.weidapeacock.com/news.asp"};
    urls[906] = new String[] {
        "002003", "伟星股份", "http://www.weixing.cn/docc/news/news.asp"};
    urls[907] = new String[] {
        "002058", "威尔泰", "http://www.welltech.com.cn/companynews.asp"};
    urls[908] = new String[] {
        "000543", "皖能电力", "http://www.wenergy.com.cn/xwdt1.asp"};
    urls[909] = new String[] {
        "002085", "万丰奥威", "http://www.wfaw.com.cn/news_2.php"};
    urls[910] = new String[] {
        "600859", "王府井", "http://www.wfj.com.cn/index/more.aspx?TypeID]=1"};
    urls[911] = new String[] {
        "600976", "武汉健民", "http://www.whjm.com/news/news.jsp?type]=1"};
    urls[912] = new String[] {
        "000668", "S武石油", "http://www.whoil.com/xwsd.htm"};
    urls[913] = new String[] {
        "000759", "武汉中百", "http://www.whzb.com/zygg/index.asp"};
    urls[914] = new String[] {
        "600681", "S*ST万鸿", "http://www.winowner.com/02index01.asp?id]=24"};
    urls[915] = new String[] {
        "600005", "武钢股份",
        "http://www.wisco.com.cn/wisco/news/wisco_news/2007news/index.shtml"};
    urls[916] = new String[] {
        "002090", "金智科技", "http://www.wiscom.com.cn/news.php?id]=12"};
    urls[917] = new String[] {
        "000789", "江西水泥", "http://www.wnq.com.cn/Get/qyxw/index.htm"};
    urls[918] = new String[] {
        "002130", "沃尔核材", "http://www.woer.com/news/InfoList.aspx?Category]=3"};
    urls[919] = new String[] {
        "002107", "沃华医药", "http://www.wohua.cn/news.asp"};
    urls[920] = new String[] {
        "600580", "卧龙电气", "http://www.wolong.com.cn/chinese/news/list_info.php?list_action]=list_all&news_type]=0120&news_lan]=gb"};
    urls[921] = new String[] {
        "600094", "*ST华源", "http://www.worldbest.sh.cn/files_cn/news.asp"};
    urls[922] = new String[] {
        "600995", "文山电力", "http://www.wsdl.com.cn/news/news_index.asp"};
    urls[923] = new String[] {
        "600575", "芜湖港", "http://www.wuhuport.com/news/news.cfm?type]=1"};
    urls[924] = new String[] {
        "000858", "五粮液", "http://www.wuliangye.com.cn/pages/newsList.xml"};
    urls[925] = new String[] {
        "600200", "江苏吴中", "http://www.wuzhong.com/branch/js/list.asp?class]=1"};
    urls[926] = new String[] {
        "600063", "皖维高新", "http://www.wwgf.com.cn/News/gsnews.asp"};
    urls[927] = new String[] {
        "000559", "万向钱潮", "http://www.wxqc.com.cn/news.asp"};
    urls[928] = new String[] {
        "600667", "太极实业", "http://www.wxtj.com/cn/zxdt.php"};
    urls[929] = new String[] {
        "000862", "银星能源", "http://www.wzyb.com.cn/news/index.asp"};
    urls[930] = new String[] {
        "600252", "中恒集团", "http://www.wz-zhongheng.com/zhsd/108.htm"};
    urls[931] = new String[] {
        "000425", "徐工科技", "http://www.xcmg.com/xinwen/default.asp"};
    urls[932] = new String[] {
        "000721", "西安饮食", "http://www.xcsg.com/news/index.asp"};
    urls[933] = new String[] {
        "600723", "西单商场", "http://www.xdsc.com.cn/xinxi/qydt.asp"};
    urls[934] = new String[] {
        "000900", "现代投资", "http://www.xdtz.net/release/list.asp?id]=9"};
    urls[935] = new String[] {
        "600416", "湘电股份",
        "http://www.xemw.com/cn/news/Aboute_company_list.asp?r_id]=24&m_id]=59"};
    urls[936] = new String[] {
        "600825", "新华传媒", "http://www.xhmedia.com/news.asp"};
    urls[937] = new String[] {
        "000756", "新华制药", "http://www.xhyyjt.com/admin/news_more.asp?lm]=&lm2]=109&open]=_blank&tj]=0&hot]=0"};
    urls[938] = new String[] {
        "600897", "厦门空港", "http://www.xiac.com.cn/info.asp?sort]=3"};
    urls[939] = new String[] {
        "000799", "S*ST酒鬼",
        "http://www.xiangjiugui.cn/web/news/news_list.php?news_type_id]=1"};
    urls[940] = new String[] {
        "600596", "新安股份", "http://www.xinanchem.com/xahg_news.php"};
    urls[941] = new String[] {
        "600777", "新潮实业", "http://www.xinchaoshiye.com/dongtai.asp"};
    urls[942] = new String[] {
        "002019", "鑫富药业",
        "http://www.xinfupharm.com/Chinese/News.asp?Action]=Co"};
    urls[943] = new String[] {
        "600141", "兴发集团", "http://www.xingfagroup.com/news_list.asp"};
    urls[944] = new String[] {
        "002120", "新海股份", "http://www.xinhaigroup.com/cn/about_news.asp"};
    urls[945] = new String[] {
        "000955", "欣龙控股",
        "http://www.xinlong-nonwovens.com/news/xldt.asp?type]=1"};
    urls[946] = new String[] {
        "000836", "鑫茂科技", "http://www.xinmaokeji.com.cn/xwzx/xw/"};
    urls[947] = new String[] {
        "000778", "新兴铸管", "http://www.xinxing-pipes.com/xinwenlist.asp"};
    urls[948] = new String[] {
        "002087", "新野纺织", "http://www.xinye-tex.com/news.asp?nodeid]=N11101"};
    urls[949] = new String[] {
        "600545", "新疆城建", "http://www.xjcj.com/newslist.asp"};
    urls[950] = new String[] {
        "000159", "国际实业", "http://www.xjgjsy.com/fore/xw.htm"};
    urls[951] = new String[] {
        "600256", "广汇股份",
        "http://www.xjguanghui.com/information/news/list.asp?path]=news"};
    urls[952] = new String[] {
        "600425", "青松建化", "http://www.xjqscc.com/infosort.php?id]=18"};
    urls[953] = new String[] {
        "600075", "新疆天业", "http://www.xj-tianye.com/news/"};
    urls[954] = new String[] {
        "000723", "S天宇", "http://www.xj-ty.com/info/newslist2.asp?class]=69"};
    urls[955] = new String[] {
        "600778", "友好集团",
        "http://www.xjyh.com.cn/yhtest/ReadSClass.jsp?SClassID]=4"};
    urls[956] = new String[] {
        "000905", "厦门港务", "http://www.xmgw.com.cn/news/news.asp?ttype]=1"};
    urls[957] = new String[] {
        "600686", "金龙汽车", "http://www.xmklm.com.cn/news.jsp?header]=公司新闻"};
    urls[958] = new String[] {
        "002127", "新民科技", "http://www.xmtex.com/gsjj/dsj.htm"};
    urls[959] = new String[] {
        "600117", "西宁特钢", "http://www.xntg.com/yw.htm"};
    urls[960] = new String[] {
        "600870", "厦华电子", "http://www.xoceco.com.cn/news02.asp"};
    urls[961] = new String[] {
        "600353", "旭光股份", "http://www.xuguang.com/cn/NewsView.screen"};
    urls[962] = new String[] {
        "600403", "欣网视讯",
        "http://www.xwtech.com/index.php?page]=news_company&class]=1&name]=公司新闻"};
    urls[963] = new String[] {
        "600373", "鑫新股份", "http://www.xxgf.com.cn/news/webnews/gzhh/gzhh1.htm"};
    urls[964] = new String[] {
        "600326", "西藏天路", "http://www.xztianlu.com/news.asp"};
    urls[965] = new String[] {
        "600211", "S藏药业", "http://www.xzyy.cn/news.asp"};
    urls[966] = new String[] {
        "600351", "亚宝药业", "http://www.yabao.com.cn/index/more2.asp"};
    urls[967] = new String[] {
        "000729", "燕京啤酒",
        "http://www.yanjing.com.cn/Content.asp?MainId]=2&BigClassid]=1"};
    urls[968] = new String[] {
        "600261", "浙江阳光", "http://www.yankon.com/docc/news/xinwen.asp"};
    urls[969] = new String[] {
        "000811", "烟台冰轮", "http://www.yantaimoon.com/chinese/news/index.asp"};
    urls[970] = new String[] {
        "000088", "盐田港", "http://www.yantian-port.com/news/"};
    urls[971] = new String[] {
        "600716", "*ST耀华", "http://www.yaohuaglass.com.cn/xinwen/index.asp"};
    urls[972] = new String[] {
        "600188", "兖州煤业", "http://www.yasheng.com.cn/news/index1.asp"};
    urls[973] = new String[] {
        "600108", "亚盛集团", "http://www.yasheng.com.cn/news/index1.asp"};
    urls[974] = new String[] {
        "600881", "亚泰集团",
        "http://www.yatai.com/home/news/index.asp?channelid]=9"};
    urls[975] = new String[] {
        "600871", "S仪化",
        "http://www.ycfc.com/SYCF_showcontent.aspx?categoryID]=sycf.060.000"};
    urls[976] = new String[] {
        "600345", "长江通信", "http://www.ycig.com/web/cn/news/index.php"};
    urls[977] = new String[] {
        "600238", "海南椰岛", "http://www.yedao.com/info/ANclassshow.asp?id]=1"};
    urls[978] = new String[] {
        "000616", "亿城股份",
        "http://www.yeland.com.cn/ggsj/newg.asp?typeid]=1&bigclassid]=1&smallclassid]="};
    urls[979] = new String[] {
        "000929", "兰州黄河", "http://www.yellowriver.net.cn/www/sContentsMain.asp?ClassId]=2&Page]=1&Number1]=1"};
    urls[980] = new String[] {
        "600531", "豫光金铅", "http://www.yggf.com.cn/xinwenfabu.asp"};
    urls[981] = new String[] {
        "002063", "远光软件", "http://www.ygsoft.com/news/news.htm"};
    urls[982] = new String[] {
        "000519", "银河动力",
        "http://www.yhdle.com/news/News_List.php?ClassName]=公司新闻&ID]=6&Language]=GB"};
    urls[983] = new String[] {
        "000792", "盐湖钾肥", "http://www.yhjf.com/news_qy.asp"};
    urls[984] = new String[] {
        "600978", "宜华木业", "http://www.yihuatimber.com/News.asp"};
    urls[985] = new String[] {
        "600887", "伊利股份", "http://www.yili.com/news/yilinews/index.html"};
    urls[986] = new String[] {
        "600197", "伊力特", "http://www.yilispirit.com/info/list.asp?Sortid]=336"};
    urls[987] = new String[] {
        "600824", "益民百货", "http://www.yimingroup.com/news/news_index.php"};
    urls[988] = new String[] {
        "600069", "银鸽投资",
        "http://www.yinge.com.cn/Article/ShowClass.asp?ClassID]=1"};
    urls[989] = new String[] {
        "000806", "银河科技", "http://www.yinhetech.com/News.asp"};
    urls[990] = new String[] {
        "002126", "银轮股份", "http://www.yinlun.cn/list.asp?boardid]=11"};
    urls[991] = new String[] {
        "600858", "银座股份", "http://www.yinzuostock.com/news.asp"};
    urls[992] = new String[] {
        "600515", "ST一投", "http://www.yitou.com/cgi-bin/gsdt/"};
    urls[993] = new String[] {
        "600317", "营口港", "http://www.ykplc.com/?id]=2"};
    urls[994] = new String[] {
        "000807", "云铝股份", "http://www.ylgf.com/xinxi/more.asp?news_class]=no"};
    urls[995] = new String[] {
        "600486", "扬农化工", "http://www.yngf.com/news/SmallClass.asp?BigClassID]=19&BigClassName]=扬农速递&SmallClassID]=24&SmallClassName]=人物要闻"};
    urls[996] = new String[] {
        "002053", "云南盐化",
        "http://www.ynyh.com/outnews/news_list.asp?type_n]=news"};
    urls[997] = new String[] {
        "600105", "永鼎光缆", "http://www.yongding.com.cn/b.asp"};
    urls[998] = new String[] {
        "600177", "雅戈尔", "http://www.youngor.com/News/Index.asp"};
    urls[999] = new String[] {
        "002086", "东方海洋",
        "http://www.yt-fishery.com/news/newslist.asp?class]=12"};
    urls[1000] = new String[] {
        "000960", "锡业股份", "http://www.ytl.com.cn/newytl/xwbd/all.asp"};
    urls[1001] = new String[] {
        "600309", "烟台万华", "http://www.ytpu.com/News.asp"};
    urls[1002] = new String[] {
        "000683", "天然碱", "http://www.yuanxing.com/dongtai/news_more.asp?lm]=&lm2]=18&lmname]=0&open]=1&n]=48&tj]=&hot]=0"};
    urls[1003] = new String[] {
        "600805", "悦达投资", "http://www.yueda.com/col8/col10/index.htm1?id]=10"};
    urls[1004] = new String[] {
        "002033", "丽江旅游", "http://www.yulongtour.com/nescenter/index.htm"};
    urls[1005] = new String[] {
        "001896", "豫能控股", "http://www.yuneng.com.cn/listnews.php?sortid]=1"};
    urls[1006] = new String[] {
        "000538", "云南白药", "http://www.yunnanbaiyao.com.cn/news/listNews.do?method]=listNews&dirId]=1&sortId]=1&sortName]=公司新闻"};
    urls[1007] = new String[] {
        "000903", "云内动力", "http://www.yunneidongli.com/ynxw.asp"};
    urls[1008] = new String[] {
        "600366", "宁波韵升", "http://www.yunsheng.com/news1.asp"};
    urls[1009] = new String[] {
        "600066", "宇通客车", "http://www.yutong.com/chinese/group/news/ytnews.asp"};
    urls[1010] = new String[] {
        "600655", "豫园商城", "http://www.yuyuantm.com.cn/meiti_xinwen.htm"};
    urls[1011] = new String[] {
        "600963", "岳阳纸业", "http://www.yypaper.com/gonggao/view.asp"};
    urls[1012] = new String[] {
        "600096", "云天化", "http://www.yyth.com.cn/news/newslist.jsp?classid]=1"};
    urls[1013] = new String[] {
        "000819", "岳阳兴长", "http://www.yyxc0819.com/article/"};
    urls[1014] = new String[] {
        "600485", "中创信测", "http://www.zcxc.com.cn/news/index.asp"};
    urls[1015] = new String[] {
        "600595", "中孚实业", "http://www.zfsy.com.cn/cn/news.asp"};
    urls[1016] = new String[] {
        "002075", "高新张铜", "http://www.zhangtong.com.cn/china/qyxw.php"};
    urls[1017] = new String[] {
        "000767", "漳泽电力",
        "http://www.zhangzepower.com/main/qyxw.asp?class_id]=2"};
    urls[1018] = new String[] {
        "002069", "獐子岛", "http://www.zhangzidao.com/docc/news.asp"};
    urls[1019] = new String[] {
        "000006", "深振业Ａ", "http://www.zhenye.com/Catalog_8.aspx"};
    urls[1020] = new String[] {
        "600517", "置信电气", "http://www.zhixindianqi.com.cn/ci8.aspx"};
    urls[1021] = new String[] {
        "000421", "南京中北", "http://www.zhong-bei.com/default.asp?cataid]=30"};
    urls[1022] = new String[] {
        "600704", "中大股份", "http://www.zhongda.com/news/index_c.asp"};
    urls[1023] = new String[] {
        "000659", "珠海中富",
        "http://www.zhongfu.com.cn/link_news.asp?EDI]=CN&TT_ID]=1&T_ID]=33"};
    urls[1024] = new String[] {
        "002070", "众和股份",
        "http://www.zhonghe.com/CNewsShow.aspx?LanID]=1&ID]=8"};
    urls[1025] = new String[] {
        "000785", "武汉中商",
        "http://www.zhongshang.com.cn/content/news.asp?TypeID]=2"};
    urls[1026] = new String[] {
        "000957", "中通客车",
        "http://www.zhongtong.com/Classc.asp?Classc]=CA991384A0E84DC4-9E7D3286A966984D"};
    urls[1027] = new String[] {
        "600329", "中新药业", "http://www.zhongxinp.com/news/main.htm"};
    urls[1028] = new String[] {
        "600210", "紫江企业", "http://www.zijiangqy.com/news.asp"};
    urls[1029] = new String[] {
        "600489", "中金黄金",
        "http://www.zjgold.com/dynamic_sub_4_11.asp?newsclassid]=16"};
    urls[1030] = new String[] {
        "002067", "景兴纸业", "http://www.zjjxjt.com/news-p3.htm"};
    urls[1031] = new String[] {
        "002012", "凯恩股份", "http://www.zjkan.com/news.asp"};
    urls[1032] = new String[] {
        "600120", "浙江东方", "http://www.zjorient.com/news.asp"};
    urls[1033] = new String[] {
        "002050", "三花股份",
        "http://www.zjshc.com/new1.asp?BigClassName]=企%20业%20新%20闻"};
    urls[1034] = new String[] {
        "002122", "天马股份",
        "http://www.zjtmb.com/information/company_news.asp?stype]=1"};
    urls[1035] = new String[] {
        "000705", "浙江震元", "http://www.zjzy.com/news.asp"};
    urls[1036] = new String[] {
        "000157", "中联重科",
        "http://www.zljt.com/Article/Article_Class2.asp?ClassID]=37"};
    urls[1037] = new String[] {
        "002021", "中捷股份", "http://www.zoje.com/zoje/news.asp"};
    urls[1038] = new String[] {
        "000685", "公用科技", "http://www.zpus000685.net/news.php?category]=新闻报道"};
    urls[1039] = new String[] {
        "000540", "*ST中天", "http://www.ztcn.cn/ggao/"};
    urls[1040] = new String[] {
        "000063", "中兴通讯",
        "http://www.zte.com.cn/main/include/list.jsp?catalogId]=12084&date]=2007"};
    urls[1041] = new String[] {
        "002092", "中泰化学",
        "http://www.zthx.com/BigClass.asp?typeid]=25&BigClassid]=107"};
    urls[1042] = new String[] {
        "000715", "中兴商业",
        "http://www.zxbusiness.com/news/index.php?classid]=74&classname]=业内新闻"};
    urls[1043] = new String[] {
        "000678", "襄阳轴承", "http://www.zxy.com.cn/news/news_show.asp?type]=1"};
    urls[1044] = new String[] {
        "600020", "中原高速", "http://www.zygs.com/news/index.asp"};
    urls[1045] = new String[] {
        "600121", "郑州煤电", "http://www.zzce.com.cn/news.asp"};
    urls[1046] = new String[] {
        "600436", "片仔癀", "http://www.zzpzh.com/class.asp?newsid]=zzpzh_gsdt"};

  }

}
