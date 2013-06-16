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
 * <p>Title: �۰���Ѷ ��Ʊ/���� ���� ����</p>
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
        LogFormatter.getLogger("").info("��ַ������������......");
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
        "000567", "���¹ɷ�", "http://000567.hnbilun.com/cgi-bin/index.dll?column9?webid]=hnbilun&userid]=1888069&columnno]=24&pageno]=0"};
    urls[1] = new String[] {
        "001696", "���궯��",
        "http://218.201.40.192/zongshen/www/cn/top4/top4-1.jsp"};
    urls[2] = new String[] {
        "600331", "���ɷ�", "http://218.6.161.66/hdgf/news/gb/list_2_1.html"};
    urls[3] = new String[] {
        "600750", "����ҩҵ", "http://218.65.95.164/web/index.asp?tab]=1&bigclass_id]=8&bigclass_name]=%BD%AD%D6%D0%C8%C8%D1%B6"};
    urls[4] = new String[] {
        "000810", "�������", "http://221.10.36.139/Chinese/Bs_News.asp?Action]=Co"};
    urls[5] = new String[] {
        "600466", "*ST�Ͽ�",
        "http://222.209.223.224:81/dk/publicPage.jsp?contentId]=5&pageIndex]=1"};
    urls[6] = new String[] {
        "600583", "���͹���",
        "http://60.28.77.221:8080/haiyou/4xwzx/4xwzx_1xwfb.jsp"};
    urls[7] = new String[] {
        "000677", "ɽ������", "http://61.133.99.98/pub/HAILONGJT/XWZX/default.htm"};
    urls[8] = new String[] {
        "600969", "�������",
        "http://61.187.187.19:8080/chinacdi/news/news.jsp?menuId]=4"};
    urls[9] = new String[] {
        "601998", "��������", "http://bank.ecitic.com/about/news.jsp"};
    urls[10] = new String[] {
        "000417", "�Ϸʰٻ�", "http://bd.hfbh.com.cn/news.asp"};
    urls[11] = new String[] {
        "600320", "�񻪸ۻ�", "http://cn.zpmc.com/article_list.asp?column_id]=40"};
    urls[12] = new String[] {
        "600530", "������",
        "http://comp.onlly.cn/v2003/gongsi/functionmodel/shichang/NEWS/NEWS_VIEW.ASP"};
    urls[13] = new String[] {
        "002095", "��ʢ�Ƽ�", "http://corp.netsun.com/news/zxdt/index.html"};
    urls[14] = new String[] {
        "000584", "�濨�ɷ�", "http://irm.p5w.net/000584/GSDT/GD.html"};
    urls[15] = new String[] {
        "000667", "������ҵ", "http://irm.p5w.net/000667/GSDT/GD.html"};
    urls[16] = new String[] {
        "600287", "����˴��", "http://irm.p5w.net/600287/GSDT/GD.html"};
    urls[17] = new String[] {
        "600873", "��������", "http://minovo.cn/Release/list.asp?id]=6"};
    urls[18] = new String[] {
        "600052", "*ST����",
        "http://news.gsgf.com/bin/newsroom/index/guangsha_1.html"};
    urls[19] = new String[] {
        "600712", "�����ٻ�", "http://news.nnbh.cn/news/news_2_1.htm"};
    urls[20] = new String[] {
        "600085", "ͬ����", "http://news.tongrentang.com/cgi-bin/visitor/page.cgi?class]=1&template]=page_template"};
    urls[21] = new String[] {
        "600689", "�Ϻ���ë", "http://shsanmao.sds.cn/sanmao/ju1.jsp"};
    urls[22] = new String[] {
        "600826", "�����ɷ�", "http://test.dsh.cn/lansheng/news.asp"};
    urls[23] = new String[] {
        "600562", "�ߴ��մ�", "http://test2.heeyee.cn/news.php"};
    urls[24] = new String[] {
        "600725", "��ά�ɷ�", "http://web.ywgf.cn/ywHome.nsf/Cnpage?openPage"};
    urls[25] = new String[] {
        "000502", "�̾��ز�", "http://www.000502.cn/admin/news.asp"};
    urls[26] = new String[] {
        "000509", "S*ST����", "http://www.000509.com/home/news.asp"};
    urls[27] = new String[] {
        "000551", "��Ԫ�Ƽ�", "http://www.000551.cn/web-ia/web/index.asp"};
    urls[28] = new String[] {
        "000639", "��·�չ", "http://www.000639.com/News.asp"};
    urls[29] = new String[] {
        "000669", "���ȿƼ�", "http://www.000669.com/Articles.aspx?cid]=2&mt]=1"};
    urls[30] = new String[] {
        "000793", "���Ŵ�ý",
        "http://www.000793.com/affiche/news.php?news_class_code]=01"};
    urls[31] = new String[] {
        "000860", "˳��ũҵ", "http://www.000860.com/news_center/catalog.asp"};
    urls[32] = new String[] {
        "000861", "��ӡ�ɷ�", "http://www.000861.com/news_02.asp"};
    urls[33] = new String[] {
        "000889", "��������", "http://www.000889.cn/file/zhongda/index.htm"};
    urls[34] = new String[] {
        "000899", "���ܹɷ�", "http://www.000899.com/mainpages/lanm_XinWen.aspx"};
    urls[35] = new String[] {
        "000040", "����", "http://www.0040.com.cn/update.asp"};
    urls[36] = new String[] {
        "000507", "������", "http://www.0507.com.cn/more.asp"};
    urls[37] = new String[] {
        "000615", "������", "http://www.0615.cn/news1.asp?smallclass]=��˾����"};
    urls[38] = new String[] {
        "600639", "�ֶ�����", "http://www.58991818.com/website/news/newsMain.jsp"};
    urls[39] = new String[] {
        "600064", "�Ͼ��߿�", "http://www.600064.com/newslist.php?newstype]=143"};
    urls[40] = new String[] {
        "600083", "*ST��Ѷ", "http://www.600083.com/news.asp"};
    urls[41] = new String[] {
        "600168", "�人�ع�",
        "http://www.600168.com.cn/news.asp?owen1]=��������&owen2]=��˾����"};
    urls[42] = new String[] {
        "600193", "���˿Ƽ�", "http://www.600193.com/news.asp"};
    urls[43] = new String[] {
        "600219", "��ɽ��ҵ", "http://www.600219.com.cn/new/index.htm"};
    urls[44] = new String[] {
        "600282", "�ϸֹɷ�",
        "http://www.600282.net/List_News.asp?Class]=1&News_Xclass]=1"};
    urls[45] = new String[] {
        "600339", "��������", "http://www.600339.com:8080/tlgx/dtxw/dtxw.jsp"};
    urls[46] = new String[] {
        "600396", "��ɽ�ɷ�", "http://www.600396.com/webnews/jrjs/jrjs1.htm"};
    urls[47] = new String[] {
        "600463", "�ո۹ɷ�",
        "http://www.600463.com.cn/cn/news/index_g1.asp?id]=374&g]=��������&grp]=8"};
    urls[48] = new String[] {
        "600496", "��������", "http://www.600496.com/new.asp"};
    urls[49] = new String[] {
        "600610", "S*ST�з�", "http://www.600610.com/news/more.asp?typeid]=9"};
    urls[50] = new String[] {
        "600614", "*ST����", "http://www.600614.com/news_lately.aspx"};
    urls[51] = new String[] {
        "600617", "��������", "http://www.600617.cn/news.asp"};
    urls[52] = new String[] {
        "600638", "�»���", "http://www.600638.com/comote/main.htm"};
    urls[53] = new String[] {
        "600683", "��̩�ɷ�", "http://www.600683.com/news.php?class_name]=��˾��̬"};
    urls[54] = new String[] {
        "600733", "Sǰ��",
        "http://www.600733.com.cn/news/list.asp?ClassID]=02010102"};
    urls[55] = new String[] {
        "600768", "��������", "http://www.600768.com.cn/2j.asp?id]=41&cid]=51"};
    urls[56] = new String[] {
        "600795", "�������", "http://www.600795.com.cn/gddlwww/gongsiyaowen.jsp"};
    urls[57] = new String[] {
        "600840", "�º���ҵ", "http://www.600840.com.cn/venture/xinwen.asp"};
    urls[58] = new String[] {
        "600869", "����ҩҵ", "http://www.600869.com/new/newslist.asp?AL_SORT]=1"};
    urls[59] = new String[] {
        "600895", "�Ž��߿�", "http://www.600895.com/htmls/news_media.asp"};
    urls[60] = new String[] {
        "600662", "ǿ���ع�", "http://www.62580000.com.cn/new.asp"};
    urls[61] = new String[] {
        "600741", "��ʿ�ɷ�",
        "http://www.84000.com.cn/newslist.aspx?categoryID]=1&Div]=Menu1"};
    urls[62] = new String[] {
        "600611", "���ڽ�ͨ", "http://www.96822.com/dz-news/index.asp"};
    urls[63] = new String[] {
        "000999", "S����",
        "http://www.999.com.cn/Portals/Portal999/news/news.aspx"};
    urls[64] = new String[] {
        "600207", "*ST����",
        "http://www.acbc.com.cn/acbcnews.asp?parentid]=3&columnid]=68"};
    urls[65] = new String[] {
        "600469", "����ɷ�", "http://www.aeolustyre.com/news.asp"};
    urls[66] = new String[] {
        "600677", "����ͨ��", "http://www.aerocom.cn/news/newsmore1.asp?id]=1"};
    urls[67] = new String[] {
        "600501", "���쳿��", "http://www.aerosun.cn/news.asp"};
    urls[68] = new String[] {
        "600971", "��Դú��", "http://www.ahhymd.com.cn/info/dispatchInfoAction.do?action]=list&progid]=4028b18506769e1b01067941692d01cb&view]=true&Forward]=ViewMoreType2"};
    urls[69] = new String[] {
        "600255", "�οƲ���",
        "http://www.ahxinke.com/index.nsf/$$ViewTemplate+for+forwai?OpenForm"};
    urls[70] = new String[] {
        "601111", "�й�����",
        "http://www.airchina.com.cn/maintenance/ghxw.jsp?location]=gb"};
    urls[71] = new String[] {
        "600773", "*ST����",
        "http://www.alongtibet.com/news/gb/index.asp?bigclassname]=���ö�̬"};
    urls[72] = new String[] {
        "600057", "���µ���", "http://www.amoi.com.cn/html/news_other.asp?cid]=2"};
    urls[73] = new String[] {
        "002136", "���ɴ�", "http://www.andty.com/news/index.asp"};
    urls[74] = new String[] {
        "600298", "������ĸ", "http://www.angel.com.cn/angel-news.htm"};
    urls[75] = new String[] {
        "600012", "��ͨ����", "http://www.anhui-expressway.cn/infocn/infocn.asp"};
    urls[76] = new String[] {
        "000868", "�����ͳ�", "http://www.ankai.com/ankai2006/news.asp"};
    urls[77] = new String[] {
        "000898", "���ֹɷ�", "http://www.ansteel.com.cn/main/gsdt.jsp"};
    urls[78] = new String[] {
        "600408", "��̩����", "http://www.antaigroup.com/docc/news/comnews.asp"};
    urls[79] = new String[] {
        "600816", "��������",
        "http://www.anxintrust.com/cn/aboutus/news_corp.asp?nwtypeid]=3"};
    urls[80] = new String[] {
        "600397", "��Դ�ɷ�", "http://www.anyuan1999.com/Chinese/xr_News.asp"};
    urls[81] = new String[] {
        "000739", "���念ԣ", "http://www.apeloa.com/cgi/search-cn.cgi?f]=news_cn+company_cn_1_&t]=news_cn2&cate1]=%C9%CF%CA%D0%B9%AB%CB%BE%B6%AF%CC%AC&Submit]=Search"};
    urls[82] = new String[] {
        "000922", "*ST����", "http://www.arc.com.cn/todayARC/news.htm"};
    urls[83] = new String[] {
        "600855", "���쳤��", "http://www.ascf.com.cn/news/qiye.asp"};
    urls[84] = new String[] {
        "000969", "��̩�Ƽ�", "http://www.atmcn.com/Chinese/News/news.asp"};
    urls[85] = new String[] {
        "600336", "�Ŀ���", "http://www.aucmahitech.com/web/xinwen_1.asp"};
    urls[86] = new String[] {
        "000918", "S*ST�ǻ�", "http://www.avaholdings.com/gsdt/yhdt.asp"};
    urls[87] = new String[] {
        "600569", "��������",
        "http://www.aysteel.com.cn/xinwen/readnews.asp?b_id]=1&flmdm]=061117170024"};
    urls[88] = new String[] {
        "600865", "S�ٴ�", "http://www.baidagroup.com/baidagroup/xwzx/xwzx.jsp"};
    urls[89] = new String[] {
        "000949", "���绯��", "http://www.bailu.com/news.asp"};
    urls[90] = new String[] {
        "600004", "���ƻ���",
        "http://www.baiyunairport.com/jcNewsServlet?jumpPage]=1"};
    urls[91] = new String[] {
        "601328", "��ͨ����", "http://www.bankcomm.com/jh/cn/more.jsp?categoryStr]=ROOT%253E%25D6%25D0%25CE%25C4%253E%25D0%25C5%25CF%25A2%25B7%25D6%25C7%25F8%253E%25BD%25BB%25D0%25D0%25D0%25C2%25CE%25C5&showTime]=y&showNewPic]=y&c]=1121231485100&"};
    urls[92] = new String[] {
        "600379", "S����", "http://www.baoguang.com.cn/cn/News.asp?channel_id]=9"};
    urls[93] = new String[] {
        "600988", "*ST����",
        "http://www.baolong.com.cn/baolong/News.asp?ClassID]=11"};
    urls[94] = new String[] {
        "600845", "�������", "http://www.baosight.com/companynews.do?flag]=news"};
    urls[95] = new String[] {
        "600019", "���ֹɷ�",
        "http://www.baosteel.com/plc/02news/ShowClass.asp?ClassID]=8"};
    urls[96] = new String[] {
        "600456", "���ѹɷ�",
        "http://www.baoti.com/chinese/main/news/news_list.asp"};
    urls[97] = new String[] {
        "000153", "��ԭҩҵ", "http://www.bbcayy.com/news.asp?class_id]=12&class_name]=%C6%F3%D2%B5%B6%AF%CC%AC"};
    urls[98] = new String[] {
        "600258", "���ùɷ�", "http://www.bct2000.com/news.asp"};
    urls[99] = new String[] {
        "601588", "����ʵҵ", "http://www.beijingns.com.cn/"};
    urls[100] = new String[] {
        "600860", "���˹ɷ�", "http://www.beirengf.com/JianTiZW/Menu.asp?DM]=2"};
    urls[101] = new String[] {
        "600705", "S*ST����", "http://www.beiya.com.cn/News/news.asp"};
    urls[102] = new String[] {
        "600171", "�Ϻ�����",
        "http://www.belling.com.cn/col150/col175/index.htm1?id]=175"};
    urls[103] = new String[] {
        "600468", "��������",
        "http://www.benefo.tj.cn/SmallClass.asp?BigClassName]=��˾����&SmallClassName]=��˾��̬"};
    urls[104] = new String[] {
        "600037", "�軪����", "http://www.bgctv.com.cn/65/more/73/73more_1.htm"};
    urls[105] = new String[] {
        "600960", "���ݻ���", "http://www.bhpiston.com/newsmore.asp?NewsTypeID]=0"};
    urls[106] = new String[] {
        "000582", "������",
        "http://www.bhport.cn/main.do?show]=subject&subjectLeveId]=004001"};
    urls[107] = new String[] {
        "000695", "������Դ", "http://www.binhaienergy.com/"};
    urls[108] = new String[] {
        "000627", "��ï����", "http://www.biocause.com/new/news/news.htm"};
    urls[109] = new String[] {
        "600226", "�����ݿ�", "http://www.biok.com/news/biok/"};
    urls[110] = new String[] {
        "002066", "��̩�Ƽ�", "http://www.bjruitai.com/default1.asp"};
    urls[111] = new String[] {
        "000802", "��������", "http://www.bj-tour.com.cn/web/mid/news"};
    urls[112] = new String[] {
        "000786", "���½���", "http://www.bnbm.com.cn/news/category.asp?cate]=1"};
    urls[113] = new String[] {
        "601988", "�й�����", "http://www.boc.cn/cn/common/fourth.jsp?category]=ROOT%3E%D6%D0%D0%D0%D7%DC%D0%D0%3E%B9%D8%D3%DA%D6%D0%D0%D0%3E%D6%D0%D0%D0%D0%C2%CE%C5"};
    urls[114] = new String[] {
        "600289", "������ͨ", "http://www.boco.com.cn/boco/cn/newslist.asp"};
    urls[115] = new String[] {
        "000725", "*ST����A",
        "http://www.boe.com.cn/0821/cn/news/NewsMore.aspx?catalogid]=2"};
    urls[116] = new String[] {
        "600966", "����ֽҵ", "http://www.bohui.net/info/default.asp"};
    urls[117] = new String[] {
        "600880", "���𴫲�", "http://www.b-raymedia.com/newEbiz1/EbizPortalFG/portal/html/InfoFocusMultiPage.html?InfoFocusList150_action]=List"};
    urls[118] = new String[] {
        "600597", "������ҵ", "http://www.brightdairy.com/main/news_all.php"};
    urls[119] = new String[] {
        "600556", "ST����",
        "http://www.bsyy.com.cn/DesktopDefault.aspx?tabid]=999"};
    urls[120] = new String[] {
        "600266", "�����ǽ�", "http://www.bucid.com/news/index.shtml"};
    urls[121] = new String[] {
        "600455", "����ͨ", "http://www.butone.com/more_huodong.php"};
    urls[122] = new String[] {
        "600581", "��һ����", "http://www.bygt.com.cn/news/gsxwlist.jsp"};
    urls[123] = new String[] {
        "600195", "�����ɷ�", "http://www.cahic.com.cn/otype.asp?owen1]=��ҵ����"};
    urls[124] = new String[] {
        "600502", "����ˮ��", "http://www.cahsl.com/cnews.asp"};
    urls[125] = new String[] {
        "600375", "��������", "http://www.camc.biz/info/report/"};
    urls[126] = new String[] {
        "002051", "�й�����",
        "http://www.camce.com.cn/cn/articlelist/article_3_adddate_desc_1.asp"};
    urls[127] = new String[] {
        "600008", "�״��ɷ�",
        "http://www.capitalwater.cn/info/listinfo.asp?class]=5"};
    urls[128] = new String[] {
        "000004", "ST��ũ", "http://www.cau-tech.com/news/news.htm"};
    urls[129] = new String[] {
        "600415", "С��Ʒ��", "http://www.cccgroup.com.cn/Active.asp?id]=2"};
    urls[130] = new String[] {
        "000661", "��������", "http://www.cchn.com.cn/ziye/xinwendongtai.asp"};
    urls[131] = new String[] {
        "000504", "���ϴ�ý", "http://www.ccidmedia.com/infolist.asp?children]=0&parentID]=0&infosortID]=16&pSort]=service"};
    urls[132] = new String[] {
        "000042", "���", "http://www.cctzkg.com/gw_news/index.asp"};
    urls[133] = new String[] {
        "600148", "����һ��", "http://www.ccyd.com.cn/yidongdongtai.asp"};
    urls[134] = new String[] {
        "600357", "�е·���",
        "http://www.cdft.com.cn/web/index.asp?classid]=10&Nclassid]=10"};
    urls[135] = new String[] {
        "000809", "�л�ҽҩ",
        "http://www.cdzhonghui.com/News/newslist.asp?class]=15"};
    urls[136] = new String[] {
        "600115", "��������", "http://www.ce-air.com/cea2/zh_CN/eastern/news/company_news/0,15153,500301,00.html?sid]=500301"};
    urls[137] = new String[] {
        "600764", "�е��ͨ", "http://www.cecgt.com/d3.htm"};
    urls[138] = new String[] {
        "600675", "�л���ҵ", "http://www.cecl.com.cn/news01.asp"};
    urls[139] = new String[] {
        "000935", "S��˫��", "http://www.cement.com.cn/news"};
    urls[140] = new String[] {
        "000931", "�йش�", "http://www.centek.com.cn/news.htm"};
    urls[141] = new String[] {
        "600176", "�й�����", "http://www.cfgcl.com.cn/morereport.htm"};
    urls[142] = new String[] {
        "600991", "��������", "http://www.cfmotors.com/docc/news/news.asp"};
    urls[143] = new String[] {
        "000987", "��������", "http://www.cgzfs.com/NewsClass.asp?BigClass]=���¶�̬"};
    urls[144] = new String[] {
        "601600", "�й���ҵ", "http://www.chalco.com.cn/chalco.3w/chinese/pages/index2.jsp?channelid]=40&siteid]=0001"};
    urls[145] = new String[] {
        "000972", "���л�",
        "http://www.chalkistomato.com/main/SuperCMS.asp?typeid]=1"};
    urls[146] = new String[] {
        "000625", "��������", "http://www.changan.com.cn/News.htm"};
    urls[147] = new String[] {
        "000570", "�ճ����",
        "http://www.changchai.com.cn/04_news/index_01.asp?typeid]=0"};
    urls[148] = new String[] {
        "600372", "���ӹɷ�", "http://www.changheauto.com/xwzx/xwzx_qyxw.htm"};
    urls[149] = new String[] {
        "600839", "�Ĵ�����",
        "http://www.changhong.com.cn/changhong/china/7944.htm"};
    urls[150] = new String[] {
        "600710", "���ֹɷ�", "http://www.changlin.com.cn/clweb.nsf/webadmin.fm?open&fid]=1A9E6F3DBBC9E1CF48257264002AA81E"};
    urls[151] = new String[] {
        "000561", "SST����",
        "http://www.changling.com.cn/News.asp?channel_id]=13"};
    urls[152] = new String[] {
        "000158", "��ɽ�ɷ�", "http://www.changshantex.com/newsroom.asp"};
    urls[153] = new String[] {
        "600706", "*ST����",
        "http://www.changxin.com/news/index2.asp?typeid]=60&borderid]=109"};
    urls[154] = new String[] {
        "000869", "��ԣ��",
        "http://www.changyu.com.cn/chinese/history/newsmore_company.asp"};
    urls[155] = new String[] {
        "600525", "��԰�²�", "http://www.changyuan.com/news/list.asp?catid]=244"};
    urls[156] = new String[] {
        "600739", "�����ɴ�", "http://www.chengda.com.cn/chengda/news/event.asp"};
    urls[157] = new String[] {
        "000990", "��־�ɷ�", "http://www.chengzhi.com.cn/news/cznews/default.asp?categoryid]=3&data]=1&page]=1&time]=1&id]=5&ako]=ddd"};
    urls[158] = new String[] {
        "000488", "����ֽҵ",
        "http://www.chenmingpaper.com/xxlr.asp?tab]=&menuid]=241&menujb]=3"};
    urls[159] = new String[] {
        "000009", "S���A", "http://www.chinabaoan.com/dongtai.asp"};
    urls[160] = new String[] {
        "600153", "�����ɷ�", "http://www.chinacdc.com/gb/news/"};
    urls[161] = new String[] {
        "000881", "��������", "http://www.china-cdig.com/xwzx/xwzx.htm"};
    urls[162] = new String[] {
        "000099", "���ź�ֱ", "http://www.china-cohc.com/03news/01companynews.asp"};
    urls[163] = new String[] {
        "002009", "����ɷ�", "http://www.chinaconveyor.com/news/class1.htm"};
    urls[164] = new String[] {
        "600510", "��ĵ��",
        "http://www.chinadenim.com/update/more.asp?type]=company"};
    urls[165] = new String[] {
        "600565", "����ɷ�", "http://www.chinadima.com/dima/newslist?classid]=1"};
    urls[166] = new String[] {
        "002056", "��궫��",
        "http://www.chinadmegc.com/chinadmegc/chinese/web/about5_1.asp"};
    urls[167] = new String[] {
        "600136", "*ST����", "http://www.china-double.com/dbxw/dbxw/dbxw1.htm"};
    urls[168] = new String[] {
        "600295", "������˹", "http://www.chinaerdos.com/chinese/news/Default.asp"};
    urls[169] = new String[] {
        "000681", "STԶ��", "http://www.chinafareast.com/news.asp"};
    urls[170] = new String[] {
        "600615", "ST�Ừ", "http://www.chinafenghwa.com.cn/newEbiz1/EbizPortalFG/portal/html/InfoListMultiPage.html?folderID]=c373e9075bfff1d88fef99b0a402d7bf"};
    urls[171] = new String[] {
        "600599", "*ST����",
        "http://www.chinafirework.cn/e_news.asp?BigClassID]=100"};
    urls[172] = new String[] {
        "600612", "��һǦ��", "http://www.chinafirstpencil.com/news"};
    urls[173] = new String[] {
        "000541", "��ɽ����",
        "http://www.chinafsl.com/News/News.asp?cateiD]=News_Company"};
    urls[174] = new String[] {
        "000926", "���ǿƼ�", "http://www.chinafxkj.com.cn/a_news.php"};
    urls[175] = new String[] {
        "000902", "�й���װ",
        "http://www.chinagarments.net.cn/cn/news/title.asp?newstype]=��˾����"};
    urls[176] = new String[] {
        "600685", "�㴬����", "http://www.chinagsi.com/cn/news/index.asp"};
    urls[177] = new String[] {
        "000822", "ɽ������",
        "http://www.chinahaihua.com/lm21.asp?typeid]=21&bigclassid]=30&smallclassid]=33"};
    urls[178] = new String[] {
        "600730", "�й��߿�", "http://www.chinahitech.com.cn/news.php?classid]=2"};
    urls[179] = new String[] {
        "002062", "������", "http://www.chinahongrun.com/hongrun/news.asp"};
    urls[180] = new String[] {
        "600343", "���춯��",
        "http://www.china-htdl.com:8081/system/_owners/htdl/_webprj/sys_xwzx.jsp"};
    urls[181] = new String[] {
        "000850", "��ï�ɷ�", "http://www.chinahuamao.net/news.asp"};
    urls[182] = new String[] {
        "000801", "�Ĵ���ɽ",
        "http://www.china-hushan.com/admin/showclass1.asp?classid]=2"};
    urls[183] = new String[] {
        "000056", "�����", "http://www.china-ia.com/webapp/main4.asp?sid]=119"};
    urls[184] = new String[] {
        "600180", "�ŷ��ɷ�", "http://www.china-jiufa.com/"};
    urls[185] = new String[] {
        "000880", "ST����", "http://www.chinajuli.com/juli/news/newslist.asp"};
    urls[186] = new String[] {
        "000939", "���ϵ���", "http://www.china-kaidi.com/kddt/kdyw.asp"};
    urls[187] = new String[] {
        "000035", "*ST�ƽ�", "http://www.chinakejian.net/news/group/index.asp"};
    urls[188] = new String[] {
        "600123", "�����ƴ�",
        "http://www.chinalanhua.com/news/default.asp?lx]=%B9%AB%CB%BE%D0%C2%CE%C5"};
    urls[189] = new String[] {
        "000532", "���Ϲɷ�", "http://www.chinalihe.com/news2.asp"};
    urls[190] = new String[] {
        "600186", "����ζ��", "http://www.chinalotus.com.cn/NewsList.asp?Sid]=29"};
    urls[191] = new String[] {
        "600175", "�����ع�",
        "http://www.chinameidu.com/hotnews/news06-newcenter.htm"};
    urls[192] = new String[] {
        "600868", "÷��ˮ��", "http://www.chinameiyan.com/NewWeb/myNews.asp"};
    urls[193] = new String[] {
        "600262", "�����ɷ�",
        "http://www.chinanhl.com/info/SortContent.asp?sortid]=334"};
    urls[194] = new String[] {
        "600811", "��������", "http://www.china-orient.com/news/news.htm"};
    urls[195] = new String[] {
        "000779", "ST����", "http://www.chinapaishen.com/dt_gsgs1_z.asp"};
    urls[196] = new String[] {
        "600775", "�Ͼ���è", "http://www.chinapanda.com.cn/pandaouterweb/newscreate/news_more.asp?lm]=&lm2]=91&open]=_blank&tj]=0&hot]=0&lineh]=8&alert]=1&top]=50"};
    urls[197] = new String[] {
        "600792", "������ҵ",
        "http://www.chinaphos.cn/info/sortContent.asp?sortId]=344"};
    urls[198] = new String[] {
        "002131", "��ŷ�ɷ�", "http://www.chinapumps.com/news.asp?type]=��˾����"};
    urls[199] = new String[] {
        "002002", "������", "http://www.chinaqionghua.com/qykx.php"};
    urls[200] = new String[] {
        "600104", "�Ϻ�����", "http://www.china-sa.com/cn/main4_news.htm"};
    urls[201] = new String[] {
        "600640", "��������", "http://www.chinasatcomgm.com/xwzx/xwzx.htm"};
    urls[202] = new String[] {
        "600835", "�Ϻ�����", "http://www.chinasec.cn/news.asp"};
    urls[203] = new String[] {
        "000777", "�к˿Ƽ�", "http://www.chinasufa.com/news/newslist.asp?class]=1"};
    urls[204] = new String[] {
        "000813", "��ɽ��֯", "http://www.chinatianshan.com/xwzx/gsxw.asp"};
    urls[205] = new String[] {
        "600520", "���ѿƼ�",
        "http://www.chinatrinity.com/aboutus/aboutsunka.aspx?catagory]=25"};
    urls[206] = new String[] {
        "600702", "��������",
        "http://www.chinatuopai.com/news2/newslist.asp?bigclassid]=1&smallclassid]=1"};
    urls[207] = new String[] {
        "600050", "�й���ͨ", "http://www.chinatypical.com/defaultroot/typical/front/jtzx.go?id]=9&frontNode]=jtxw&flow]=1"};
    urls[208] = new String[] {
        "600302", "��׼�ɷ�", "http://www.chinatypical.com/defaultroot/typical/front/jtzx.go?id]=9&frontNode]=jtxw&flow]=1"};
    urls[209] = new String[] {
        "600247", "�ﻪ�ɷ�", "http://www.china-well.com/cn/02_1.php"};
    urls[210] = new String[] {
        "002016", "�����Ƽ�", "http://www.china-well.com/cn/02_1.php?sort2]=1"};
    urls[211] = new String[] {
        "000797", "�й�����", "http://www.chinawuyi.com.cn/news"};
    urls[212] = new String[] {
        "002015", "ϼ�ͻ���", "http://www.chinaxiake.com/bar7.htm"};
    urls[213] = new String[] {
        "600692", "��ͨ�ɷ�", "http://www.chinayatong.com/news/Index.asp"};
    urls[214] = new String[] {
        "600673", "��֮��", "http://www.chinayaxing.com/news1.asp"};
    urls[215] = new String[] {
        "600533", "��ϼ����", "http://www.chixia.com/web/news7.asp"};
    urls[216] = new String[] {
        "600132", "����ơ��", "http://www.chongqingbeer.com/newslist.asp"};
    urls[217] = new String[] {
        "600227", "���컯", "http://www.chth.com.cn/xwzx.html?classid]=2"};
    urls[218] = new String[] {
        "000885", "S*ST����", "http://www.chundu.com.cn/index31.asp?cataid]=��˾����"};
    urls[219] = new String[] {
        "600854", "*ST����", "http://www.chunlan.com/2news/list_1news.asp"};
    urls[220] = new String[] {
        "600497", "�ۺ�п��",
        "http://www.chxz.com/newscompany.asp?mainid]=9&secondid]=0"};
    urls[221] = new String[] {
        "601166", "��ҵ����",
        "http://www.cib.com.cn/netbank/cn/About_IB/Whatxs_New/"};
    urls[222] = new String[] {
        "000039", "�м�����", "http://www.cimc.com/web/418/Default.asp?id]=420"};
    urls[223] = new String[] {
        "600584", "����Ƽ�", "http://www.cj-elec.com/gsxw.asp"};
    urls[224] = new String[] {
        "600109", "�ɶ���Ͷ", "http://www.cjgf.com/news/more.asp?langmu2]=58"};
    urls[225] = new String[] {
        "600119", "����Ͷ��", "http://www.cjtz.cn/news.asp?sorts]=��˾����"};
    urls[226] = new String[] {
        "600876", "��������", "http://www.clfg.com/news/gsxw.asp"};
    urls[227] = new String[] {
        "600016", "��������",
        "http://www.cmbc.com.cn/cmbc/column/newest/newest.xml"};
    urls[228] = new String[] {
        "600036", "��������", "http://www.cmbchina.com/cmb+info/news/cmbnews"};
    urls[229] = new String[] {
        "601872", "�����ִ�", "http://www.cmenergyshipping.com/Catalog_12.aspx"};
    urls[230] = new String[] {
        "000024", "���̵ز�", "http://www.cmpd.cn/zhaoshang/news/news1.jsp"};
    urls[231] = new String[] {
        "600511", "��ҩ�ɷ�",
        "http://www.cncm.com.cn/cncm/news/listnews.asp?ClassID]=9"};
    urls[232] = new String[] {
        "600818", "�Ϻ�����", "http://www.cnforever.com/news/2006NEWS.HTML"};
    urls[233] = new String[] {
        "600586", "�𾧿Ƽ�", "http://www.cnggg.cn/2j/3-1.jsp?id]=7"};
    urls[234] = new String[] {
        "600232", "��ӥ�ɷ�", "http://www.cn-goldeneagle.com/news.htm"};
    urls[235] = new String[] {
        "600068", "���ް�",
        "http://www.cngzb.com/news/newslist2.asp?t1]=6&class]=7"};
    urls[236] = new String[] {
        "002001", "�ºͳ�", "http://www.cnhu.com/cn/QYWH/CATEGORY.ASP"};
    urls[237] = new String[] {
        "600325", "�����ɷ�", "http://www.cnhuafas.com/news_2.asp?isindex]=1"};
    urls[238] = new String[] {
        "600260", "���ֿƼ�", "http://www.cnkaile.com/XWWH/index.asp"};
    urls[239] = new String[] {
        "002076", "ѩ����", "http://www.cnlight.com/news/default.asp"};
    urls[240] = new String[] {
        "000564", "��������", "http://www.cnminsheng.com/chinese/xwzx/mskx.asp"};
    urls[241] = new String[] {
        "600626", "���ɷ�",
        "http://www.cnshenda.com.cn/chinese/jckxw/BrowseNews.asp"};
    urls[242] = new String[] {
        "600026", "�к���չ", "http://www.cnshippingdev.com/xwzx.asp"};
    urls[243] = new String[] {
        "002024", "��������",
        "http://www.cnsuning.com/website/news/suningnews/index.html"};
    urls[244] = new String[] {
        "000798", "��ˮ��ҵ", "http://www.cofc.com.cn/xinxi.asp?stype]=��˾����"};
    urls[245] = new String[] {
        "000031", "�����ز�",
        "http://www.cofco-property.cn/new_news/news_list.asp?id]=5&type]=0&picture]=17"};
    urls[246] = new String[] {
        "000151", "�гɹɷ�", "http://www.complant-ltd.com.cn/xwdt.htm"};
    urls[247] = new String[] {
        "600572", "������",
        "http://www.conba.com.cn/xwzx/newslist1.asp?classname]=%D0%C2%CE%C5%B6%AF%CC%AC"};
    urls[248] = new String[] {
        "000619", "�����Ͳ�", "http://www.conch.cn/sm2111111116.asp"};
    urls[249] = new String[] {
        "600585", "����ˮ��", "http://www.conch.cn/sm2111111116.asp"};
    urls[250] = new String[] {
        "600476", "���ʿƼ�", "http://www.copote.com/qydt/qydt-index.jsp"};
    urls[251] = new String[] {
        "600428", "��Զ����", "http://www.coscol.com.cn/chinese/news.asp"};
    urls[252] = new String[] {
        "002052", "ͬ�޵���",
        "http://www.coship.com/main/News/NewsIndex.aspx?CatalogID]=6810"};
    urls[253] = new String[] {
        "002133", "�����", "http://www.cosmosgroup.com.cn/new_gsgs.asp"};
    urls[254] = new String[] {
        "600729", "����ٻ�", "http://www.cqbhdl.com.cn/News/"};
    urls[255] = new String[] {
        "600369", "ST����", "http://www.cqcjsy.com/docc/gongsixinwen.html"};
    urls[256] = new String[] {
        "601005", "�������", "http://www.cqgt.cn/News/newslist.asp?class]=2"};
    urls[257] = new String[] {
        "600116", "��Ͽˮ��",
        "http://www.cqsxsl.com/gb/news/newslist.asp?gateid]=1&class]=4"};
    urls[258] = new String[] {
        "600263", "·�Ž���", "http://www.crbcint.com/lq/xwzx.asp"};
    urls[259] = new String[] {
        "600528", "��������", "http://www.crec.com.cn/gsxw.asp"};
    urls[260] = new String[] {
        "600890", "*ST�з�", "http://www.cred.com/news/news.asp"};
    urls[261] = new String[] {
        "600029", "S�Ϻ�", "http://www.cs-air.com/cn/news/11/26/48/list_1.asp"};
    urls[262] = new String[] {
        "000520", "�������", "http://www.csc-hy.com.cn/xwen2.asp?kind]=1"};
    urls[263] = new String[] {
        "000012", "�ϲ���", "http://www.csgholding.com/news/nanbo.asp"};
    urls[264] = new String[] {
        "600536", "�й����",
        "http://www.css.com.cn/subpage.aspx?ctabid]=33&stabid]=32"};
    urls[265] = new String[] {
        "000569", "*ST����", "http://www.cssc.com.cn/news.asp"};
    urls[266] = new String[] {
        "600623", "��̥��", "http://www.cstarc.com/news/news.asp"};
    urls[267] = new String[] {
        "600088", "���Ӵ�ý", "http://www.ctv-media.com.cn/xwzx/xwzx.htm"};
    urls[268] = new String[] {
        "600358", "��������", "http://www.cutc.com.cn/news.asp?id]=3"};
    urls[269] = new String[] {
        "600007", "�й���ó", "http://www.cwtc.com/chinese/news/index.asp"};
    urls[270] = new String[] {
        "600658", "��ά�Ƽ�", "http://www.cwtech.com.cn/news_look.asp"};
    urls[271] = new String[] {
        "600549", "������ҵ", "http://www.cxtc.com/NewsInfo.asp?ClassID]=1"};
    urls[272] = new String[] {
        "600551", "�ƴ���",
        "http://www.cx-ustc.com/new/Article_Class.asp?ClassID]=1"};
    urls[273] = new String[] {
        "000966", "��Դ����", "http://www.cydl.com.cn/news_list.asp?id]=11"};
    urls[274] = new String[] {
        "600900", "��������", "http://www.cypc.com.cn/NewsList.jsp?cateid]=102186"};
    urls[275] = new String[] {
        "600138", "������", "http://www.cytsonline.com/default1.htm"};
    urls[276] = new String[] {
        "000780", "*ST�˷�", "http://www.cyxf.com/news/"};
    urls[277] = new String[] {
        "600230", "���ݴ�",
        "http://www.czdh.com.cn/layer2/news.asp?classid]=1&bz]=0"};
    urls[278] = new String[] {
        "002108", "��������", "http://www.cz-mz.com/main/news.htm"};
    urls[279] = new String[] {
        "000733", "�񻪿Ƽ�", "http://www.czst.com.cn/news.asp"};
    urls[280] = new String[] {
        "002030", "�ﰲ����", "http://www.daangene.com/news/"};
    urls[281] = new String[] {
        "600695", "*ST��", "http://www.dajiang.com/newEbiz1/EbizPortalFG/portal/html/news2.html?folderID]=c373e90a2ae75d828f6a7d57d518fe6c"};
    urls[282] = new String[] {
        "000530", "����ɷ�", "http://www.daleng.cn/news/index.jsp"};
    urls[283] = new String[] {
        "601006", "������·", "http://www.daqintielu.com/region/00008.shtml"};
    urls[284] = new String[] {
        "000910", "���ǿƼ�", "http://www.daretechnology.com/cn/news.asp"};
    urls[285] = new String[] {
        "600198", "*ST����", "http://www.datang.com/news.asp"};
    urls[286] = new String[] {
        "600747", "���Թɷ�",
        "http://www.daxian.cn/DAXIAN/NewsClist.asp?CID]=48&supID]=40"};
    urls[287] = new String[] {
        "002041", "�Ǻ���ҵ", "http://www.denghai.com/denghai_news.asp"};
    urls[288] = new String[] {
        "002055", "�������", "http://www.deren.com.cn/newnews/default.asp"};
    urls[289] = new String[] {
        "000049", "�������",
        "http://www.desaybattery.com/asp-bin/GB/?page]=7&class]=35"};
    urls[290] = new String[] {
        "600081", "����Ƽ�", "http://www.detc.com.cn/df1-51all.htm"};
    urls[291] = new String[] {
        "600006", "��������", "http://www.dfac.com/news/gsxw.asp"};
    urls[292] = new String[] {
        "600875", "�������", "http://www.dfem.com.cn/News.asp?C]=A"};
    urls[293] = new String[] {
        "002077", "��۹ɷ�", "http://www.dggf.cn/qydt.asp"};
    urls[294] = new String[] {
        "000828", "��ݸ�ع�",
        "http://www.dgholdings.cn/news-Search.asp?columnname]=&columncode]=001001"};
    urls[295] = new String[] {
        "002065", "�����ϴ�",
        "http://www.dhcc.com.cn/news/news_link.asp?kind]=%CD%BC%C6%AC%D0%C2%CE%C5"};
    urls[296] = new String[] {
        "600354", "�ػ���ҵ", "http://www.dhseed.com/Html/gsnews/index.html"};
    urls[297] = new String[] {
        "002043", "�ñ���", "http://www.dhwooden.com/news.asp"};
    urls[298] = new String[] {
        "600288", "���Ƽ�", "http://www.dhxjy.com.cn/dh_news.htm"};
    urls[299] = new String[] {
        "600830", "���ӥ", "http://www.dhyinvest.com/more.asp?ttt]=6&sss]=��˾����"};
    urls[300] = new String[] {
        "600159", "�����ز�", "http://www.dldc.com.cn/gsxw.htm"};
    urls[301] = new String[] {
        "000961", "������ţ", "http://www.dljn.com/news/index_gs.jsp"};
    urls[302] = new String[] {
        "000679", "��������", "http://www.dlyy.com.cn/jtyw/index.asp"};
    urls[303] = new String[] {
        "600693", "���ټ���", "http://www.dongbai.com/new.asp"};
    urls[304] = new String[] {
        "000423", "S����", "http://www.dongeejiao.com/news/more.aspx?type]=��˾��̬"};
    urls[305] = new String[] {
        "000682", "��������", "http://www.dongfang-china.com/showall.asp?typeid]=1"};
    urls[306] = new String[] {
        "002082", "�����²�",
        "http://www.dongfang-china.com/xwzx.asp?typeid]=1&img]=xwzx.gif"};
    urls[307] = new String[] {
        "002135", "��������", "http://www.dongnanwangjia.com/newEbiz1/EbizPortalFG/portal/html/InfoMultiPage.html?InfoList150_action]=more&InfoPublish_CategoryID]=c373e90b4c646f9f8febbba7b39c9c36"};
    urls[308] = new String[] {
        "600113", "�㽭����",
        "http://www.dongri.com/otype.asp?owen1]=��˾����&owen2]=��˾����&n]=20"};
    urls[309] = new String[] {
        "000599", "�ൺ˫��", "http://www.doublestar.com.cn/xinwenlie.asp"};
    urls[310] = new String[] {
        "600405", "����Դ", "http://www.dpc.com.cn/xwysj.asp"};
    urls[311] = new String[] {
        "600804", "����ʿ", "http://www.drpeng.com.cn/(fjkulg55mvwkboqnhitaoq45)/ShowPage/SystemTemp1/ThreeLevelPage/NewsList.aspx?CataID]=bba13a83-5d09-4765-a60b-912b83f1e4e8"};
    urls[312] = new String[] {
        "600694", "���̹ɷ�", "http://www.dsjt.com/news.asp"};
    urls[313] = new String[] {
        "600335", "��ʢ�칤", "http://www.dstg.com.cn/news/index.asp"};
    urls[314] = new String[] {
        "601001", "��ͬúҵ", "http://www.dtmy.com.cn/lb.asp?lb]=1"};
    urls[315] = new String[] {
        "601991", "���Ʒ���", "http://www.dtpower.com/sp/library/notice.jsp"};
    urls[316] = new String[] {
        "002011", "�ܰ�����", "http://www.dunan.net/news.asp?classid]=5"};
    urls[317] = new String[] {
        "600833", "��һҽҩ", "http://www.dyyy.com.cn/newEbiz1/EbizPortalFG/portal/html/InfoMultiPage.html?InfoList150_action]=more&InfoPublish_CategoryID]=c373e9033fdcde088ffbdd55c0235ee8"};
    urls[318] = new String[] {
        "600635", "���ڹ���", "http://www.dzug.cn/"};
    urls[319] = new String[] {
        "600776", "����ͨ��", "http://www.eastcom.com:8080/news/news_focus.jsp"};
    urls[320] = new String[] {
        "002017", "���ź�ƽ",
        "http://www.eastcompeace.com/Home_Data_More.asp?ID]=01"};
    urls[321] = new String[] {
        "601628", "�й�����", "http://www.e-chinalife.com/news/company/index.html"};
    urls[322] = new String[] {
        "000826", "�ϼ���Դ", "http://www.eguard-rd.com/infolist.aspx?dataid]=111"};
    urls[323] = new String[] {
        "000562", "��Դ֤ȯ", "http://www.ehongyuan.com/news/more.jsp?hynav]=abouthy&menuId]=1&parentId]=1971&catId]=1972"};
    urls[324] = new String[] {
        "600340", "����ɷ�",
        "http://www.ekingair.com/html/news/listz.asp?catid]=89"};
    urls[325] = new String[] {
        "002005", "�º����", "http://www.electech.com.cn/Catalog_86.aspx"};
    urls[326] = new String[] {
        "002059", "�����ɷ�",
        "http://www.expo99km.gov.cn/expo/Wpublisher/displaypages/ContentList_22.aspx"};
    urls[327] = new String[] {
        "600651", "��������", "http://www.facs.com.cn/news.php"};
    urls[328] = new String[] {
        "000055", "�����", "http://www.fangda.com/news/newslist.asp?class]=14"};
    urls[329] = new String[] {
        "600563", "��������", "http://www.faratronic.com/cnnews.asp"};
    urls[330] = new String[] {
        "000890", "����ʤ", "http://www.fasten.com.cn/news/1/"};
    urls[331] = new String[] {
        "000800", "һ���γ�",
        "http://www.fawcar.com.cn/xwdt.jsp?Type]=1&Main]=xxdt#"};
    urls[332] = new String[] {
        "600742", "һ���Ļ�", "http://www.fawsh.com.cn/hydt/default.jsp"};
    urls[333] = new String[] {
        "600526", "�ƴﻷ��", "http://www.feida.biz/CN/newslist.asp"};
    urls[334] = new String[] {
        "600654", "���ֹɷ�", "http://www.feilo.com.cn/news/news.aspx"};
    urls[335] = new String[] {
        "002042", "���ǹɷ�", "http://www.feiyatex.com/news.asp"};
    urls[336] = new String[] {
        "000636", "�绪�߿�", "http://www.fenghua-advanced.com/news.asp?id]=166"};
    urls[337] = new String[] {
        "000713", "������ҵ",
        "http://www.fengle.com.cn/news/indexnew.asp?typeid]=1"};
    urls[338] = new String[] {
        "600809", "ɽ���ھ�", "http://www.fenjiu.com.cn/docc/news/news.asp"};
    urls[339] = new String[] {
        "000046", "��������", "http://www.fhjs.cn/news/xwzx.asp"};
    urls[340] = new String[] {
        "600498", "���ͨ��", "http://www.fiberhome.com.cn/news/news.asp"};
    urls[341] = new String[] {
        "600616", "��һʳƷ", "http://www.firstfood.com.cn/"};
    urls[342] = new String[] {
        "000026", "S���Ǵ�A", "http://www.fiyta.com.cn/news.asp"};
    urls[343] = new String[] {
        "600802", "����ˮ��", "http://www.fjcement.com/news.asp?more]=1"};
    urls[344] = new String[] {
        "600033", "��������",
        "http://www.fjgs.com.cn/htdocs/xxlr.asp?tab]=&menulb]=129��������&menujb]=2"};
    urls[345] = new String[] {
        "600452", "�������", "http://www.flepc.com/news.asp"};
    urls[346] = new String[] {
        "600196", "����ҽҩ",
        "http://www.fosunpharma.com/Default.aspx?tabid]=399&modulesID]=583"};
    urls[347] = new String[] {
        "600166", "��������", "http://www.foton.com.cn/news/xwzx/gsxw_list.html"};
    urls[348] = new String[] {
        "600601", "�����Ƽ�", "http://www.foundertech.com/tabid/69/Default.aspx"};
    urls[349] = new String[] {
        "600399", "��˳�ظ�", "http://www.fs-ss.com/qydt/gsxw-qt1.jsp"};
    urls[350] = new String[] {
        "600965", "�������", "http://www.fucheng.net/cn/news.asp"};
    urls[351] = new String[] {
        "600724", "��������",
        "http://www.fuda.com/asp/list.asp?classid]=00000000000000000731"};
    urls[352] = new String[] {
        "600203", "���չɷ�", "http://www.furielec.com/xwzx.asp"};
    urls[353] = new String[] {
        "600660", "��ҫ����",
        "http://www.fuyaogroup.com/news/article_class.aspx?articleclass_id]=1"};
    urls[354] = new String[] {
        "000576", "�㶫�ʻ�", "http://www.ganhua.com.cn/02-2.php"};
    urls[355] = new String[] {
        "600067", "�ڳǴ�ͨ", "http://www.gcdt.net/newsmore.asp?cid]=19"};
    urls[356] = new String[] {
        "600310", "�𶫵���", "http://www.gdep.com.cn/EnviProtection.asp"};
    urls[357] = new String[] {
        "000823", "��������", "http://www.gd-goworld.com/cn/info/index.html"};
    urls[358] = new String[] {
        "600098", "���ݿع�",
        "http://www.gdih.cn/chinese/news-Search.asp?columnname]=&columncode]=001001"};
    urls[359] = new String[] {
        "600382", "�㶫����", "http://www.gdmzh.com/article/conews.asp?cataid]=64"};
    urls[360] = new String[] {
        "002060", "��ˮ��", "http://www.gdsdej.com/news.asp"};
    urls[361] = new String[] {
        "601002", "����ʵҵ", "http://www.gem-year.com/news/more1.asp"};
    urls[362] = new String[] {
        "002045", "���ݹ���", "http://www.ggec.com.cn/news.asp"};
    urls[363] = new String[] {
        "600236", "��ڵ���",
        "http://www.ggep.com.cn/Article/news/conews/Index.html"};
    urls[364] = new String[] {
        "600894", "��ֹɷ�", "http://www.gglts.cn/jsp/sinoec/index4/consultantcenter/index.jsp?pageNo]=1&pageSize]=20&choice]=current"};
    urls[365] = new String[] {
        "002101", "�㶫��ͼ", "http://www.ght-china.com/news.asp"};
    urls[366] = new String[] {
        "600080", "*ST��", "http://www.ginwa.com/news/index.asp"};
    urls[367] = new String[] {
        "600182", "S��ͨ", "http://www.gititire.com/gititirecorp/news.asp?menuid]=4&classifaction]=4&subid]=0&language]=1&ryear]=0"};
    urls[368] = new String[] {
        "600800", "S*ST�ſ�", "http://www.gmcc.com.cn/news/index.asp"};
    urls[369] = new String[] {
        "600538", "��������",
        "http://www.gofar.com.cn/gofarnew/list.asp?boardid]=26&parent]=109"};
    urls[370] = new String[] {
        "000851", "�ߺ�ɷ�", "http://www.gohigh.com.cn/guanyugaohong_list.jsp?leibieid]=1&subleibieid]=2&gen2name]=��˾����"};
    urls[371] = new String[] {
        "600086", "��������", "http://www.goldjade.cn/new.asp?typeid]=3"};
    urls[372] = new String[] {
        "002081", "�����", "http://www.goldmantis.com/web/news.asp?newsSortId]=1"};
    urls[373] = new String[] {
        "002079", "���ݹ��", "http://www.goodark.com/news.asp"};
    urls[374] = new String[] {
        "600332", "����ҩҵ", "http://www.gpc.com.cn/list_all.asp"};
    urls[375] = new String[] {
        "000429", "�����٣�",
        "http://www.gpedcl.com/default.asp?ChannelId]=12&ColumnId]=72"};
    urls[376] = new String[] {
        "002031", "���ֹɷ�",
        "http://www.greatoo.com/greatoo_cn/CONN_NEWS1.asp?REVE]=A"};
    urls[377] = new String[] {
        "000066", "���ǵ���", "http://www.greatwall.com.cn/news/news.asp"};
    urls[378] = new String[] {
        "000651", "��������", "http://www.gree.com.cn/gree_news/news_index01.jsp"};
    urls[379] = new String[] {
        "600206", "���й��", "http://www.gritek.com/qyzs/main.html"};
    urls[380] = new String[] {
        "601333", "������·", "http://www.gsrc.com/dongtai/dongtai.htm"};
    urls[381] = new String[] {
        "002091", "���չ�̩", "http://www.gtiggm.com/news.asp?cid]=5"};
    urls[382] = new String[] {
        "002102", "�ڸ�����", "http://www.guanfu.com/html/news2.asp"};
    urls[383] = new String[] {
        "002103", "�㲩�ɷ�",
        "http://www.guangbo.net/chinese/news/more.asp?l_id]=1"};
    urls[384] = new String[] {
        "000952", "���ҩҵ",
        "http://www.guangjipharm.com/docc/shownews.asp?lb_id]=1"};
    urls[385] = new String[] {
        "000587", "S*ST����",
        "http://www.guangming.com/manager/info/show.asp?id]=4&cate_id]=1&type]=info"};
    urls[386] = new String[] {
        "002111", "������̩", "http://www.guangtai.com.cn/new/news.asp"};
    urls[387] = new String[] {
        "000557", "ST������", "http://www.guangxia.com.cn/newlist.htm"};
    urls[388] = new String[] {
        "600433", "�ں�����", "http://www.guanhao.com/news_1.asp"};
    urls[389] = new String[] {
        "000750", "S*ST����",
        "http://www.guilinjiqi.com.cn/jqdongtai/jqdongtai.asp"};
    urls[390] = new String[] {
        "000978", "��������",
        "http://www.guilintravel.com/blog/Article/ShowArticle.asp?ArticleID]=11"};
    urls[391] = new String[] {
        "000833", "���ǹɷ�", "http://www.guitang.com/webpage/news.html"};
    urls[392] = new String[] {
        "000589", "ǭ��̥��", "http://www.guizhoutyre.com/gsxw.asp"};
    urls[393] = new String[] {
        "000596", "�ž�����", "http://www.gujing.com/news/zxzx.asp"};
    urls[394] = new String[] {
        "600321", "��������", "http://www.guodong.cn/News/newslist.asp?class]=2"};
    urls[395] = new String[] {
        "002093", "�����Ƽ�", "http://www.guomaitech.com/news.asp"};
    urls[396] = new String[] {
        "600444", "��ͨ��ҵ", "http://www.guotone.com/news.asp?NewsType]=��˾����"};
    urls[397] = new String[] {
        "000748", "������Ϣ",
        "http://www.gwi.com.cn/new.asp?title]=���ſ��&langmu2]=2"};
    urls[398] = new String[] {
        "000537", "���չ", "http://www.gyfz000537.com/news/gongsinewsmore.asp"};
    urls[399] = new String[] {
        "600348", "��������", "http://www.gyne.com.cn/SEC.ASP"};
    urls[400] = new String[] {
        "000522", "����ɽ��", "http://www.gzbys.com/news.asp"};
    urls[401] = new String[] {
        "002025", "�������", "http://www.gzhtdq.com.cn/news.asp"};
    urls[402] = new String[] {
        "600048", "�����ز�", "http://www.gzpoly.com/news/"};
    urls[403] = new String[] {
        "600594", "�����ҩ", "http://www.gz-yibai.com/news.asp"};
    urls[404] = new String[] {
        "600684", "�齭ʵҵ", "http://www.gzzjsy.com/news.asp?nb]=9"};
    urls[405] = new String[] {
        "600202", "���յ�",
        "http://www.hac.com.cn/news_list.php?mname]=%D0%C2%CE%C5%D6%D0%D0%C4&mid]=1"};
    urls[406] = new String[] {
        "600598", "�����", "http://www.hacl.cn/gsdt.asp"};
    urls[407] = new String[] {
        "600038", "���ɹɷ�", "http://www.hafei.com/InfoPublish/Listcompany.asp"};
    urls[408] = new String[] {
        "600690", "�ൺ����",
        "http://www.haier.com/cn/news/haier/more.asp?itemID]=25"};
    urls[409] = new String[] {
        "600516", "*ST����", "http://www.hailongkeji.com/news/index.htm"};
    urls[410] = new String[] {
        "600896", "�к���ʢ", "http://www.haishengshipping.com/xinwen.asp"};
    urls[411] = new String[] {
        "002116", "�й�����", "http://www.haisum.com/news/index.asp"};
    urls[412] = new String[] {
        "000566", "���Ϻ�ҩ", "http://www.haiyao.com.cn/news/news3.asp"};
    urls[413] = new String[] {
        "600570", "��������", "http://www.handsome.com.cn/news/index.php?news_type_id]=1&news_type]=%B9%AB%CB%BE%D0%C2%CE%C5"};
    urls[414] = new String[] {
        "002008", "���弤��",
        "http://www.hanslaser.com/content_manage/news.asp?bigclassname]=��˾����"};
    urls[415] = new String[] {
        "002013", "�к�����", "http://www.hapm.cn/news-1.asp"};
    urls[416] = new String[] {
        "600664", "S��ҩ", "http://www.hapm.cn/news-1.asp"};
    urls[417] = new String[] {
        "600708", "�����ɷ�", "http://www.hb600708.com/news.asp"};
    urls[418] = new String[] {
        "600035", "�������",
        "http://www.hbctgs.com/ctgs/info/listArticle.jsp?artColumn]=03020103"};
    urls[419] = new String[] {
        "000916", "��������", "http://www.hbgsgl.com.cn/index_dt_zt.asp"};
    urls[420] = new String[] {
        "600566", "��ǹɷ�", "http://www.hbhc.com.cn/Chinese/qyxw/qyxw.asp"};
    urls[421] = new String[] {
        "600184", "�»���", "http://www.hbnhg.com/new/dt00.asp#"};
    urls[422] = new String[] {
        "000923", "S����", "http://www.hbxg.com/gongsi2.asp"};
    urls[423] = new String[] {
        "000422", "�����˻�", "http://www.hbyh.cn/news/news1.htm"};
    urls[424] = new String[] {
        "000727", "�����Ƽ�", "http://www.hdeg.com/"};
    urls[425] = new String[] {
        "600001", "��������", "http://www.hdgt.com.cn/new.asp?pic]=0"};
    urls[426] = new String[] {
        "600027", "�������",
        "http://www.hdpi.com.cn/st/TZ/XWG/news_list.aspx?nian]=2007"};
    urls[427] = new String[] {
        "000953", "�ӳػ���", "http://www.hechihuagong.com.cn/hh2/gongsixinwen.htm"};
    urls[428] = new String[] {
        "002027", "��ϲ�ع�", "http://www.hedy.com.cn/Chinese/news/co/month05.htm"};
    urls[429] = new String[] {
        "600760", "ST�ڱ�", "http://www.heibao.com/cn/newsgs.asp"};
    urls[430] = new String[] {
        "600093", "*ST�̼�", "http://www.hejia.com/Article_Class2.asp?ClassID]=7"};
    urls[431] = new String[] {
        "600761", "���պ���", "http://www.helichina.com/news.asp"};
    urls[432] = new String[] {
        "002104", "�㱦�ɷ�", "http://www.hengbao.com/new/index.asp"};
    urls[433] = new String[] {
        "600356", "���ֽҵ",
        "http://www.hengfengpaper.com/Article/ShowClass.asp?ClassID]=1"};
    urls[434] = new String[] {
        "600305", "��˳��ҵ", "http://www.hengshun.cn/news.php"};
    urls[435] = new String[] {
        "002132", "���ǿƼ�", "http://www.hengxingchinese.com/newxp/SmallClass.asp?BigClassID]=1&BigClassName]=��������&SmallClassID]=2&SmallClassName]=��Ϣ����&SmallClassType]=1"};
    urls[436] = new String[] {
        "000531", "����ˣ�", "http://www.hengyun.com.cn/xwzx.asp"};
    urls[437] = new String[] {
        "600448", "���Ĺɷ�", "http://www.hfgf.cn/Cn/News.asp?Types]=3"};
    urls[438] = new String[] {
        "600076", "*ST����", "http://www.hg.com.cn/news/sub1_gsxw.asp"};
    urls[439] = new String[] {
        "600095", "���߿�", "http://www.hgk-group.com/our_news.htm"};
    urls[440] = new String[] {
        "000988", "�����Ƽ�", "http://www.hgtech.com.cn/new1-1.asp"};
    urls[441] = new String[] {
        "600150", "�����ػ�", "http://www.hhm.com.cn/news.htm"};
    urls[442] = new String[] {
        "600172", "�ƺ�����", "http://www.hhxf.com/chinese/more.asp"};
    urls[443] = new String[] {
        "000886", "���ϸ���", "http://www.hi-expressway.com/zxdt.asp"};
    urls[444] = new String[] {
        "600619", "�����ɷ�", "http://www.highly.cc/hlxw/hlxw.htm"};
    urls[445] = new String[] {
        "000921", "*ST����", "http://www.hisense.com/news/info.jsp"};
    urls[446] = new String[] {
        "600267", "����ҩҵ", "http://www.hisunpharm.com/03news/"};
    urls[447] = new String[] {
        "000609", "�����ɷ�", "http://www.hi-tec609.com/News.asp"};
    urls[448] = new String[] {
        "600082", "��̩��չ", "http://www.hitech-develop.com/news/morenews.htm"};
    urls[449] = new String[] {
        "600162", "�㽭�ع�", "http://www.hkhc.com.cn/index/xwzx/xwsd/"};
    urls[450] = new String[] {
        "000751", "пҵ�ɷ�", "http://www.hldxygf.com/neiye1-004.html"};
    urls[451] = new String[] {
        "600426", "��³����", "http://www.hl-hengsheng.com/docc/news_1.asp"};
    urls[452] = new String[] {
        "600987", "����ɷ�", "http://www.hmgf.com/news/index.asp?myclass]=3"};
    urls[453] = new String[] {
        "600221", "���Ϻ���",
        "http://www.hnair.com/hnairweb/ABOUTHNAIR/NEWS/wfmNewS.aspx?strNewsType]=1"};
    urls[454] = new String[] {
        "600731", "���Ϻ���", "http://www.hndtsz.com/new.asp?id2]=138"};
    urls[455] = new String[] {
        "600257", "��ͥˮֳ", "http://www.hndtsz.com/new.asp?id2]=138"};
    urls[456] = new String[] {
        "000989", "��֥��", "http://www.hnjzt.com/newsmore.asp?action]=��֥����"};
    urls[457] = new String[] {
        "002096", "������", "http://www.hnnlmb.com/news.asp?classname]=ȫ������"};
    urls[458] = new String[] {
        "000548", "����Ͷ��", "http://www.hntz.com.cn/Release/list1.asp?id]=2"};
    urls[459] = new String[] {
        "000790", "������", "http://www.hoist.com.cn/1/homenews.asp?cataID]=40"};
    urls[460] = new String[] {
        "600097", "�����Ƽ�", "http://www.holleykj.com/news/index.php"};
    urls[461] = new String[] {
        "000607", "����ҩҵ", "http://www.holleypharm.com/��������/tabid/55/articleType/CategoryView/categoryId/1/.aspx"};
    urls[462] = new String[] {
        "600400", "�춹�ɷ�", "http://www.hongdou.com.cn/news.asp"};
    urls[463] = new String[] {
        "600316", "�鶼����", "http://www.hongdu-aviation.com/cn/html/more.asp"};
    urls[464] = new String[] {
        "000524", "��������", "http://www.hoteldongfang.com/news.asp"};
    urls[465] = new String[] {
        "600011", "���ܹ���",
        "http://www.hpi.com.cn/chinese/investor/pressrelease/index.jsp"};
    urls[466] = new String[] {
        "600276", "����ҽҩ", "http://www.hrs.com.cn/xwzx01.asp"};
    urls[467] = new String[] {
        "600687", "��ʢ��", "http://www.hsdchina.com/html/2_1.asp?classname]=tmpClassName&classid]=3&nclassname]=%B9%AB%CB%BE%D0%C2%CE%C5&nclassid]=3"};
    urls[468] = new String[] {
        "000980", "����ɷ�", "http://www.hsjinma.com/gsxw/"};
    urls[469] = new String[] {
        "600487", "��ͨ���", "http://www.htgd.com.cn/new1.asp"};
    urls[470] = new String[] {
        "000901", "����Ƽ�",
        "http://www.htkjgroup.com/fenye.aspx?typeid]=73&select_name]=������Ϣ"};
    urls[471] = new String[] {
        "600371", "���ڿƼ�", "http://www.huaguankeji.com/hgkj/news_more.asp?lm]=&lm2]=67&open]=_blank&tj]=0&hot]=0"};
    urls[472] = new String[] {
        "600521", "����ҩҵ", "http://www.huahaipharm.com/web/newsB0001.asp?FirstKind]=MT_00002_100002702&KindID]=MT_00002_100002703"};
    urls[473] = new String[] {
        "000985", "���컪��", "http://www.huake.com/hkjs.asp"};
    urls[474] = new String[] {
        "002007", "��������", "http://www.hualanbio.com/hlnews.asp"};
    urls[475] = new String[] {
        "600054", "��ɽ����",
        "http://www.huangshan.com.cn/news/newsMore.do?colid]=4"};
    urls[476] = new String[] {
        "002004", "������ҩ",
        "http://www.huapont.cn/hb_web/Default.aspx?tabid]=138"};
    urls[477] = new String[] {
        "600308", "��̩�ɷ�", "http://www.huatai.com/col1/col15/index.htm1?id]=15"};
    urls[478] = new String[] {
        "000428", "����Ƶ�", "http://www.huatian-hotel.com/news/news.jsp"};
    urls[479] = new String[] {
        "002048", "��������", "http://www.huaxianggroup.com/chinese/new.php"};
    urls[480] = new String[] {
        "002018", "���ǻ���", "http://www.huaxingchem.com/news.asp"};
    urls[481] = new String[] {
        "600532", "�����Ƽ�", "http://www.huayang.com/lm23.asp"};
    urls[482] = new String[] {
        "000404", "����ѹ��", "http://www.hua-yi.cn/HYRC/Simplified/NewsList.asp"};
    urls[483] = new String[] {
        "600573", "��Ȫơ��", "http://www.huiquan-beer.com/news.asp"};
    urls[484] = new String[] {
        "000415", "��ͨˮ��",
        "http://www.huitonggroup.com.cn/news_list.asp?action]=more&c_id]=89&s_id]=106"};
    urls[485] = new String[] {
        "600079", "�˸��Ƽ�",
        "http://www.humanwell.com.cn/web/listnewsclient?type]=1"};
    urls[486] = new String[] {
        "600360", "��΢����", "http://www.hwdz.com.cn/tzgx/gsdt.jsp"};
    urls[487] = new String[] {
        "600015", "��������",
        "http://www.hxb.com.cn/chinese/abouthxb/index.jsp?cid2]=73&cid3]=732"};
    urls[488] = new String[] {
        "600367", "���Ƿ�չ", "http://www.hxfz.com.cn/hongxinbd.asp"};
    urls[489] = new String[] {
        "600477", "�����ֹ�",
        "http://www.hxss.com.cn/news/?PHPSESSID]=38183e769c0b14efe350d7cd5b8e68fb"};
    urls[490] = new String[] {
        "000963", "����ҽҩ", "http://www.hzhdyy.com/news.asp"};
    urls[491] = new String[] {
        "600126", "���ֹɷ�", "http://www.hzsteel.com/web/NewsList.asp?FirstKind]=MT_00002_100017466&KindID]=MT_00002_100017469"};
    urls[492] = new String[] {
        "600608", "S*ST����", "http://www.i600608.com/cn/news/news.asp"};
    urls[493] = new String[] {
        "601398", "��������", "http://www.icbc.com.cn/news/hotspot.jsp?column]=%B9%A4%D0%D0%BF%EC%D1%B6&row]=1&length]=15"};
    urls[494] = new String[] {
        "600797", "�������", "http://www.insigma.com.cn/news/index.php?func]=listAll&catalog]=0101&PHPSESSID]=4d2c71d84cb2d19b8a0f7a92616c3cd3"};
    urls[495] = new String[] {
        "600755", "���Ź�ó", "http://www.itg.com.cn:81/itgweb/NewsServlet?action]=NewsListByNewsType&NewsTypeID]=1"};
    urls[496] = new String[] {
        "600418", "��������", "http://www.jac.com.cn/jac/showCatalogAction.do?method]=print&catalogid]=ff8080811102a3c6011168d7e9ba045d"};
    urls[497] = new String[] {
        "600315", "�Ϻ��һ�", "http://www.jahwa.com.cn/jahwa/news/news.php"};
    urls[498] = new String[] {
        "000897", "�����չ",
        "http://www.jbdc.com.cn/News/InfoList.aspx?CategoryID]=1"};
    urls[499] = new String[] {
        "600622", "�α�����", "http://www.jbjt.com/2-1.asp"};
    urls[500] = new String[] {
        "600546", "���ͻ���",
        "http://www.jccc.com.cn/HuaJian/News/Important/?type_id]=7"};
    urls[501] = new String[] {
        "000816", "��������",
        "http://www.jdchina.com/docc/news/newscomp.asp?kindid]=14"};
    urls[502] = new String[] {
        "600661", "��������", "http://www.jd-ny.com/zxdt_001.asp"};
    urls[503] = new String[] {
        "000401", "����ˮ��", "http://www.jdsn.com.cn/news.asp?classid]=8"};
    urls[504] = new String[] {
        "600265", "������ҵ", "http://www.jgly.cn/news/index.aspx"};
    urls[505] = new String[] {
        "600160", "�޻��ɷ�", "http://www.jhgf.com.cn/more.asp"};
    urls[506] = new String[] {
        "600877", "�й�����", "http://www.jialing.com.cn/newjl/cn_web/cntop1-1.php"};
    urls[507] = new String[] {
        "600668", "��弯��", "http://www.jianfeng.com.cn/news/news1.aspx"};
    urls[508] = new String[] {
        "000950", "���廯��", "http://www.jianfengchemicals.com/cgi/search-cn.cgi?f]=contact_cn+news_cn+company_cn_1_&t]=news_cn&w]=news_cn"};
    urls[509] = new String[] {
        "600212", "��Ȫʵҵ", "http://www.jiangquan.com.cn/news.asp?cat_id]=1"};
    urls[510] = new String[] {
        "002061", "��ɽ����", "http://www.jiangshanchem.com/news.asp"};
    urls[511] = new String[] {
        "000617", "ʯ�ͼò�", "http://www.jichai.com/chinese/xinwen.php"};
    urls[512] = new String[] {
        "600836", "����ʵҵ", "http://www.jielong-printing.com/xwzx/xwzxmore.asp"};
    urls[513] = new String[] {
        "600022", "���ϸ���", "http://www.jigang.com.cn/invinfo/WEB_SIDE/jgdt.jsp"};
    urls[514] = new String[] {
        "002118", "����ҩҵ", "http://www.jilinzixin.com.cn/category.asp?id]=88"};
    urls[515] = new String[] {
        "600577", "����ɷ�", "http://www.jingda.cn/news/index.asp?lx]=��˾����"};
    urls[516] = new String[] {
        "002020", "����ҩҵ", "http://www.jingxinpharm.com/news.asp?lb]=2"};
    urls[517] = new String[] {
        "002049", "��Դ����", "http://www.jingyuan.com/info.asp"};
    urls[518] = new String[] {
        "000818", "�����ȼ�", "http://www.jinhuagroup.com/news-c.htm#"};
    urls[519] = new String[] {
        "600621", "�Ϻ�����", "http://www.jin-ling.com/xwzx.asp"};
    urls[520] = new String[] {
        "601007", "���극��",
        "http://www.jinlinghotel.com/CN/presscenter/NewsRelease.asp"};
    urls[521] = new String[] {
        "000510", "��·����", "http://www.jinlugroup.cn/zhongc/web57/newssmall.asp?class]=��˾��̬&classid]=2293&id]=25947"};
    urls[522] = new String[] {
        "600201", "�����", "http://www.jinyu.com.cn/news.php"};
    urls[523] = new String[] {
        "600190", "���ݸ�", "http://www.jinzhouport.com/rdxw.asp"};
    urls[524] = new String[] {
        "600307", "�Ƹֺ���", "http://www.jiugang.com/structure/jgxw/jgxw"};
    urls[525] = new String[] {
        "600292", "��������", "http://www.jiulongep.com/gsyw.asp"};
    urls[526] = new String[] {
        "600650", "����Ͷ��", "http://www.jjtz.com/webapp/china/template1/xwzx-mt.jsp?org_item_id]=048895d9-4347-4efd-b8e9-65cbad3d9bfe&sub_org_item_id]=b0d4e2a6-797e-4def-8cf4-846c52190a64"};
    urls[527] = new String[] {
        "600806", "��������", "http://www.jkht.com/info/news.aspx"};
    urls[528] = new String[] {
        "000623", "���ְ���", "http://www.jlaod.com/yeNews.asp"};
    urls[529] = new String[] {
        "600432", "������ҵ", "http://www.jlnickel.com.cn/second.asp?parentcol]=02&ColumnName]=��ҵ��̬&classidd]=0201"};
    urls[530] = new String[] {
        "601008", "���Ƹ�", "http://www.jlpcl.com/companynews.aspx"};
    urls[531] = new String[] {
        "600189", "����ɭ��", "http://www.jlsg.com.cn/aspx/newsmore.aspx"};
    urls[532] = new String[] {
        "000928", "*ST��̿", "http://www.jlts.cn/Chinese/Bs_News.asp?Action]=Co"};
    urls[533] = new String[] {
        "000919", "����ҩҵ", "http://www.jlyy000919.com/news/news.asp"};
    urls[534] = new String[] {
        "000550", "��������", "http://www.jmc.com.cn/chinese/info/news_events.asp"};
    urls[535] = new String[] {
        "600527", "���ϸ���", "http://www.jngx.cn/news/newslist.asp?class]=19"};
    urls[536] = new String[] {
        "600072", "�����ع�", "http://www.jnhi.com/news.php"};
    urls[537] = new String[] {
        "000937", "��ţ��Դ",
        "http://www.jnny.com.cn/news.asp?i]=%BD%F0%C5%A3%BF%C6%BC%BC&c]=��ţ�Ƽ���̬"};
    urls[538] = new String[] {
        "600380", "����Ԫ", "http://www.joincare.com/news.asp"};
    urls[539] = new String[] {
        "600888", "�½��ں�", "http://www.joinworld.com/news.asp"};
    urls[540] = new String[] {
        "600872", "�о����",
        "http://www.jonjee.com/cn/?op]=list_news&id]=4&mid]=10"};
    urls[541] = new String[] {
        "000402", "���ڽ�",
        "http://www.jrjkg.com.cn/detail.aspx?id]=1&ttype]=200000&table]=news&pid]=04"};
    urls[542] = new String[] {
        "600389", "��ɽ�ɷ�", "http://www.jsac.com.cn/gxxw.asp?submenu]=more"};
    urls[543] = new String[] {
        "002074", "��Դ����", "http://www.jsdydq.com/web/xwzx.asp"};
    urls[544] = new String[] {
        "600377", "��������",
        "http://www.jsexpressway.com/col2/col20/articlecolumn.php?colid]=20"};
    urls[545] = new String[] {
        "000821", "��ɽ���", "http://www.jspackmach.com/c_news.asp"};
    urls[546] = new String[] {
        "600981", "���տ�Ԫ",
        "http://www.jstex.com/html_cn/index_cn.php?menu]=about_cn"};
    urls[547] = new String[] {
        "600522", "����Ƽ�", "http://www.jszt.com.cn/2007/News/index.asp"};
    urls[548] = new String[] {
        "000666", "��γ�Ļ�", "http://www.jwgf.com/news_qy.aspx"};
    urls[549] = new String[] {
        "600362", "����ͭҵ", "http://www.jxcc.com/chinese/jtgs/jtyw/xwda.htm"};
    urls[550] = new String[] {
        "600561", "��������", "http://www.jxcy.com.cn/Gsxw/list.asp?type]=1"};
    urls[551] = new String[] {
        "600269", "��������", "http://www.jxexpressway.com/news/SmallClass.asp?BigClassID]=2&BigClassName]=��������&BigClassType]=1&SmallClassID]=7&SmallClassName]=��˾Ҫ��&SmallClassType]=1"};
    urls[552] = new String[] {
        "600461", "���ˮҵ",
        "http://www.jxhcsy.com/release/list_22.asp?id]=3&pid]=8"};
    urls[553] = new String[] {
        "600676", "���˹ɷ�", "http://www.jygf.cn/cn/news/"};
    urls[554] = new String[] {
        "000700", "ģ�ܿƼ�",
        "http://www.jymosu.com/sinonews/index.asp?classid]=51&Nclassid]=78"};
    urls[555] = new String[] {
        "000021", "���ǿ���", "http://www.kaifa.com.cn/news/news.asp"};
    urls[556] = new String[] {
        "600537", "��ͨ����", "http://www.kaiz.com/News/newslist.asp?class]=11"};
    urls[557] = new String[] {
        "600518", "����ҩҵ", "http://www.kangmei.com.cn/Class/dt/dt01.html"};
    urls[558] = new String[] {
        "002119", "��ǿ����", "http://www.kangqiang.com/dynamic/kanqiang_list.php"};
    urls[559] = new String[] {
        "600557", "��Եҩҵ", "http://www.kanion.com/news/news_more.asp?lm2]=67"};
    urls[560] = new String[] {
        "600986", "�ƴ�ɷ�",
        "http://www.keda-group.com.cn/xxlr.asp?tab]=&menuid]=241&menujb]=3"};
    urls[561] = new String[] {
        "600499", "�ƴ����", "http://www.kedagroup.com/"};
    urls[562] = new String[] {
        "000852", "����ɷ�", "http://www.kingdream.com.cn/news_more.asp"};
    urls[563] = new String[] {
        "600143", "�𷢿Ƽ�", "http://www.kingfa.com.cn/news.asp?nb]=97"};
    urls[564] = new String[] {
        "600390", "����Ƽ�", "http://www.king-ray.com.cn/lh_news.asp"};
    urls[565] = new String[] {
        "600110", "�п�Ӣ��", "http://www.kinwa.com.cn/news/xinwen-1.html"};
    urls[566] = new String[] {
        "600997", "���йɷ�", "http://www.kkcc.com.cn:8010/cn/news/index.jsp"};
    urls[567] = new String[] {
        "002068", "��è�ɷ�", "http://www.kmzh.com/new/show1.asp?n_type]=0"};
    urls[568] = new String[] {
        "000048", "*ST����", "http://www.kondarl.com/news.asp"};
    urls[569] = new String[] {
        "000016", "��ѣ�", "http://www.konka.com/ad/ad_news.jsp"};
    urls[570] = new String[] {
        "000979", "ST��Է", "http://www.koyogroup.com/article/4/14/index.html"};
    urls[571] = new String[] {
        "600422", "������ҩ", "http://www.kpc.com.cn/NewsShow/NewsSearchShow.aspx?name]=&typename]=%e9%9b%86%e5%9b%a2%e5%8a%a8%e6%80%81"};
    urls[572] = new String[] {
        "002106", "�����߿�", "http://www.laibao.com.cn/news/news.asp"};
    urls[573] = new String[] {
        "600102", "���ֹɷ�", "http://www.laigang.com/otype.asp?owen1]=��ҵ����"};
    urls[574] = new String[] {
        "000981", "S����",
        "http://www.languang.com/docc/news_list.asp?class_name]=��˾��̬&class_id]=76"};
    urls[575] = new String[] {
        "600328", "��̫ʵҵ",
        "http://www.lantaicn.com/news/bestnews.asp?lb_name]=��˾����&lb_id]=14"};
    urls[576] = new String[] {
        "600209", "*ST�޶�", "http://www.lawtonfz.com.cn/zxdt/zxdt.htm"};
    urls[577] = new String[] {
        "600513", "����ҩҵ", "http://www.lhpharma.com/News/Default.asp?LmBh]=16"};
    urls[578] = new String[] {
        "600285", "����ɷ�",
        "http://www.lingrui.com/script/moreNews.php?sortid]=1&lid]=1"};
    urls[579] = new String[] {
        "600885", "��ŵ̫��",
        "http://www.linuo-solar.com.cn/cn/news.asp?act]=list&channelid]=26"};
    urls[580] = new String[] {
        "000418", "С����",
        "http://www.littleswan.com/news/news.asp?n_type]=��ҵ��̬"};
    urls[581] = new String[] {
        "000528", "����", "http://www.liugong.com.cn/"};
    urls[582] = new String[] {
        "601003", "���ֹɷ�", "http://www.liuzhousteel.com/Article_List.asp"};
    urls[583] = new String[] {
        "000513", "���鼯��", "http://www.livzon.com.cn/news/livzon.jsp?catid]=65"};
    urls[584] = new String[] {
        "600765", "��ԴҺѹ", "http://www.liyuanhydraulic.com/news.jsp"};
    urls[585] = new String[] {
        "600090", "STơ�ƻ�", "http://www.ljjn.com/news/news.htm"};
    urls[586] = new String[] {
        "600663", "½����",
        "http://www.ljz.com.cn/cn/news/list.asp?idtree]=.0.1.4."};
    urls[587] = new String[] {
        "600789", "³��ҽҩ", "http://www.lkpc.com/news/index.jsp?synth_type_id]=3"};
    urls[588] = new String[] {
        "600985", "�����ƻ�", "http://www.lmkh.com/news/news.cfm"};
    urls[589] = new String[] {
        "600249", "������", "http://www.lmz.com.cn/news_hot.asp"};
    urls[590] = new String[] {
        "600241", "����ʱ��",
        "http://www.lntimes.cn/newEbiz1/EbizPortalFG/portal/html/InfoMultiPage.html"};
    urls[591] = new String[] {
        "000848", "�е�¶¶", "http://www.lolo.com.cn/news.htm"};
    urls[592] = new String[] {
        "600853", "�����ɷ�", "http://www.longjianlq.com/index/zxxx.asp"};
    urls[593] = new String[] {
        "600388", "��������", "http://www.longking.cn/qygk/xinwen.htm"};
    urls[594] = new String[] {
        "600352", "�㽭��ʢ", "http://www.longsheng.com/cn/default1.htm"};
    urls[595] = new String[] {
        "000523", "��������", "http://www.lonkey.com.cn/about/news.asp"};
    urls[596] = new String[] {
        "000998", "¡ƽ�߿�", "http://www.lpht.com.cn/Gsdt.asp"};
    urls[597] = new String[] {
        "600592", "��Ϫ�ɷ�", "http://www.ls.com.cn/news.asp?type]=1"};
    urls[598] = new String[] {
        "600644", "��ɽ����",
        "http://www.lsep.com.cn/gskx.asp?typeid]=15&BigClassid]=73"};
    urls[599] = new String[] {
        "000726", "³̩��", "http://www.lttc.com.cn/News/newslist.asp?class]=2"};
    urls[600] = new String[] {
        "601699", "º������", "http://www.luanhn.com/NobigClass.asp?typeid]=15"};
    urls[601] = new String[] {
        "600135", "S�ֿ�", "http://www.luckyfilm.com.cn/news.php?column]=%D0%C2%CE%C5%B6%AF%CC%AC&newsid]=5"};
    urls[602] = new String[] {
        "000735", "*ST��ţ",
        "http://www.luoniushan.com/newlns/news/more.php?infotype]=dt"};
    urls[603] = new String[] {
        "002088", "³���ɷ�", "http://www.luyangwool.com/News.asp?Mid]=48&Sid]=51"};
    urls[604] = new String[] {
        "000830", "³������", "http://www.lxchemical.com/browse/MainFrame.asp?MenuId]=2568&InfoId]=0&Find]=&Title]=&MainId]="};
    urls[605] = new String[] {
        "600783", "³�Ÿ���", "http://www.lxgx.com/"};
    urls[606] = new String[] {
        "600491", "��Ԫ����", "http://www.lycg.com.cn/cn/gsdt/nbdt/nbdt.htm"};
    urls[607] = new String[] {
        "600478", "��Ԫ�²�", "http://www.lyrun.com/main/lyruninfo-list.asp?ty]=1"};
    urls[608] = new String[] {
        "600192", "���ǵ繤",
        "http://www.lz-gwe.com.cn/docc/news.asp?department]=7"};
    urls[609] = new String[] {
        "600423", "�����ɷ�", "http://www.lzhg.cn/news/index.asp"};
    urls[610] = new String[] {
        "000568", "�����Ͻ�", "http://www.lzlj.com/news/news.aspx"};
    urls[611] = new String[] {
        "000533", "�����", "http://www.macro.com.cn/News/index.asp"};
    urls[612] = new String[] {
        "600808", "��ֹɷ�", "http://www.magang.com.cn/list.asp?boardid]=11"};
    urls[613] = new String[] {
        "600980", "����Ų�", "http://www.magmat.com/news_1.htm"};
    urls[614] = new String[] {
        "600107", "������", "http://www.mailyard.com.cn/lm.asp?big]=1"};
    urls[615] = new String[] {
        "600337", "���˹ɷ�", "http://www.markorfurniture.com/0410.html"};
    urls[616] = new String[] {
        "600993", "��Ӧ��",
        "http://www.mayinglong.cn/news/newslist.aspx?newsid]=3"};
    urls[617] = new String[] {
        "000536", "SST����", "http://www.mddjg.com/xcfb.htm"};
    urls[618] = new String[] {
        "000782", "����ɷ�", "http://www.meidanylon.com/chinese/news-list.asp?ColumnCode]=001001&ColumnName]=��ҵ����"};
    urls[619] = new String[] {
        "000521", "S����", "http://www.meiling.com/sm2111111346.asp"};
    urls[620] = new String[] {
        "600297", "����ҩҵ", "http://www.merro.com.cn/merrohealth/yellowpage/result.asp?stype]=1&txtkey]=&txtlanmu]=2&merro]=���޶�̬"};
    urls[621] = new String[] {
        "002073", "�ൺ���", "http://www.mesnac.com/news_company.asp"};
    urls[622] = new String[] {
        "000637", "Sïʵ��", "http://www.mhsh0637.com.cn/other/news.asp"};
    urls[623] = new String[] {
        "000527", "���ĵ���",
        "http://www.midea.com.cn/midea2005/news/news1.jsp?id]=4"};
    urls[624] = new String[] {
        "600235", "�����ֽ",
        "http://www.minfenggroup.com/newslist1.aspx?folderID]=1&foldername]=��ᶯ̬"};
    urls[625] = new String[] {
        "002034", "������",
        "http://www.mizuda.com/Article/ShowClass.asp?ClassID]=1"};
    urls[626] = new String[] {
        "600131", "ẽ�ˮ��",
        "http://www.mjsdgs.com/bigClass.asp?typeid]=28&BigClassid]=107"};
    urls[627] = new String[] {
        "600543", "Ī�߹ɷ�",
        "http://www.mogao.com/Article/ShowClass.asp?ClassID]=4"};
    urls[628] = new String[] {
        "600519", "����ę́", "http://www.moutaichina.com/news/news.asp"};
    urls[629] = new String[] {
        "600101", "*ST����",
        "http://www.mxdl.com.cn/news/gb/index.asp?BigClassName]=��˾����"};
    urls[630] = new String[] {
        "000976", "���͹ɷ�", "http://www.my0976.com/news/gsxw.asp"};
    urls[631] = new String[] {
        "000737", "�Ϸ绯��", "http://www.nafine.com/News_Title.php?topic]=�Ϸ�����"};
    urls[632] = new String[] {
        "600163", "������ֽ", "http://www.nanping-paper.com/news.htm"};
    urls[633] = new String[] {
        "600250", "�ϷĹɷ�", "http://www.nantex.com.cn/ch/news/news.php"};
    urls[634] = new String[] {
        "000948", "������Ϣ", "http://www.nantian.com.cn/nantian/templet/templet1/newslist.asp?Areano]=001&classno]=110"};
    urls[635] = new String[] {
        "600406", "��������", "http://www.naritech.cn/news/subject.asp?lei]=���Ŷ�̬"};
    urls[636] = new String[] {
        "600798", "��������", "http://www.nbmc.com.cn/news/news_more.asp?lm]=51&lm2]=52&open]=_blank&tj]=0&hot]=0"};
    urls[637] = new String[] {
        "600889", "�Ͼ�����", "http://www.ncfc.cn/news.asp"};
    urls[638] = new String[] {
        "600812", "������ҩ",
        "http://www.ncpc.com.cn/news/News_List_Lanmu.asp?lanmu_ID]=1"};
    urls[639] = new String[] {
        "000585", "��������", "http://www.nee.com.cn/news.php?id]=1"};
    urls[640] = new String[] {
        "000597", "������ҩ",
        "http://www.negpf.com.cn/Article/ShowClass.asp?ClassID]=1"};
    urls[641] = new String[] {
        "000078", "��������", "http://www.neptunus.com/xw.php"};
    urls[642] = new String[] {
        "600718", "����ɷ�", "http://www.neusoft.com/news/index.jsp?type]=41"};
    urls[643] = new String[] {
        "000876", "��ϣ��", "http://www.newhopegroup.com/newhope.asp?id]=8"};
    urls[644] = new String[] {
        "000997", "�´�½", "http://www.newlandcomputer.com/PubInfo/PubInfoDo/pageDefineAct.do?pageDefineKey]=2&containerKey]=3#"};
    urls[645] = new String[] {
        "600975", "�����", "http://www.newwf.com/xwzx/new.asp"};
    urls[646] = new String[] {
        "600628", "������", "http://www.newworld-china.com/htm/yaowen1.asp"};
    urls[647] = new String[] {
        "000758", "��ɫ�ɷ�", "http://www.nfc.com.cn/col23/col36/index.htm1?id]=36"};
    urls[648] = new String[] {
        "000906", "S�Ͻ���", "http://www.nfjc.com.cn/nfxw/nfxw_02.asp?classid]=1"};
    urls[649] = new String[] {
        "600323", "�Ϻ���չ", "http://www.nhd.net.cn/news.htm"};
    urls[650] = new String[] {
        "600555", "����ɽ", "http://www.ninedragon.com.cn/news.html"};
    urls[651] = new String[] {
        "002040", "�Ͼ���", "http://www.nj-port.com/news/newsmoretitle.php3?func]=DispMoreTit&NewsClassid]=2&date]=&mod]=6&img]=1"};
    urls[652] = new String[] {
        "600713", "�Ͼ�ҽҩ", "http://www.njyy.com/njyyb_bt.asp?bm]=030101"};
    urls[653] = new String[] {
        "600301", "�ϻ��ɷ�", "http://www.nnchem.com/cn/news.asp?type]=1"};
    urls[654] = new String[] {
        "000911", "������ҵ", "http://www.nnsugar.com/news_zx.asp"};
    urls[655] = new String[] {
        "000060", "�н�����", "http://www.nonfemet.com/chinese/news-list.asp?ColumnCode]=001001&ColumnName]=��ҵ����"};
    urls[656] = new String[] {
        "600817", "��ʢ�Ƽ�", "http://www.norcent.com.cn/chinese/News.asp"};
    urls[657] = new String[] {
        "000065", "��������", "http://www.norinco-intl.com/cn/news/index.asp"};
    urls[658] = new String[] {
        "600003", "��������",
        "http://www.northeast-expressway.cn/class3.asp?classid]=23"};
    urls[659] = new String[] {
        "002014", "���¹ɷ�", "http://www.novel.com.cn/xwzx2.asp?lbid]=102101"};
    urls[660] = new String[] {
        "000037", "���ϵ��", "http://www.nsrd.com.cn/China/Htm/NewsCenter/list.asp?type]=%B9%AB%CB%BE%D0%C2%CE%C5"};
    urls[661] = new String[] {
        "002089", "�º���", "http://www.nsu.com.cn/news.html"};
    urls[662] = new String[] {
        "600087", "�Ͼ�ˮ��",
        "http://www.nwti.com.cn/admin/news/default.asp?cataid]=1"};
    urls[663] = new String[] {
        "600165", "���ĺ���", "http://www.nxhengli.com.cn/News.asp"};
    urls[664] = new String[] {
        "000595", "�������", "http://www.nxz.com.cn/file/page_2.jsp?code]=00006"};
    urls[665] = new String[] {
        "000069", "���ȳǣ�", "http://www.octholding.com/news/index.php"};
    urls[666] = new String[] {
        "600278", "������ҵ", "http://www.oie.com.cn/NEWS/NEWS.htm"};
    urls[667] = new String[] {
        "600832", "��������", "http://www.opg.cn/news_main.php"};
    urls[668] = new String[] {
        "000962", "������ҵ", "http://www.otic.com.cn/news_list.asp"};
    urls[669] = new String[] {
        "601318", "�й�ƽ��", "http://www.pa18.com/pa18Web/framework/aboutus.jsp?content]=/pa18Web/aboutus/cn/news_blackout.jsp&advert]=/pa18Web/aboutus/cn/adv_abouts.jsp"};
    urls[670] = new String[] {
        "600284", "�ֶ�����",
        "http://www.pdjs.com.cn/template/template_1/news/list.asp?Cata_Id]=92&"};
    urls[671] = new String[] {
        "600529", "ɽ��ҩ��", "http://www.pharmglass.com/news.aspx"};
    urls[672] = new String[] {
        "600078", "���ǹɷ�",
        "http://www.phosphatechina.com/aaa/news/dynNewsList.htm"};
    urls[673] = new String[] {
        "000697", "����ƫת", "http://www.pianzhuan.com.cn/pianzxw.asp"};
    urls[674] = new String[] {
        "600312", "ƽ�ߵ���", "http://www.pinggao.com/news/news.asp"};
    urls[675] = new String[] {
        "600114", "�����ɷ�", "http://www.pm-china.com/cn-f-1.php?type]=2"};
    urls[676] = new String[] {
        "600018", "�ϸۼ���", "http://www.portshanghai.com.cn/sipg/listbankuai.php"};
    urls[677] = new String[] {
        "600717", "����", "http://www.ptacn.com/news/userlist.asp?type]=xwfb"};
    urls[678] = new String[] {
        "000515", "������ҵ", "http://www.pyty.cn/news.asp"};
    urls[679] = new String[] {
        "000629", "�ʸַָ�", "http://www.pzhsteel.com.cn/xwzx/gsyw.aspx"};
    urls[680] = new String[] {
        "600248", "*ST�ط�",
        "http://www.qfny.com/qfxw.asp?InfoClassID]=7&InfoClassName]=��˾����"};
    urls[681] = new String[] {
        "600479", "ǧ��ҩҵ", "http://www.qian-jin.com/docc/news2.asp"};
    urls[682] = new String[] {
        "600576", "*ST���", "http://www.qingfengchina.com/template/qfxw.htm"};
    urls[683] = new String[] {
        "600698", "SST����", "http://www.qingqi.com.cn/qqr.asp"};
    urls[684] = new String[] {
        "600103", "��ɽֽҵ", "http://www.qingshan.com.cn/News.asp"};
    urls[685] = new String[] {
        "600217", "*ST����", "http://www.qinling.com/news.asp"};
    urls[686] = new String[] {
        "000913", "Ǯ��Ħ��", "http://www.qjmotor.com/News.asp"};
    urls[687] = new String[] {
        "600283", "Ǯ��ˮ��", "http://www.qjwater.com/news/news.html"};
    urls[688] = new String[] {
        "600568", "*STǱҩ", "http://www.qjzy.com/more.asp"};
    urls[689] = new String[] {
        "600720", "����ɽ", "http://www.qlssn.com/asp/news/more.asp"};
    urls[690] = new String[] {
        "600218", "ȫ����", "http://www.quanchai.com.cn/news.asp"};
    urls[691] = new String[] {
        "600649", "ԭˮ�ɷ�", "http://www.rawwater.com.cn/news/index.asp"};
    urls[692] = new String[] {
        "600439", "�𱴿�", "http://www.rebecca.com.cn/rbkgu/servlet/ForumServlet?jumpPage]=1&url]=/index/newlist.jsp"};
    urls[693] = new String[] {
        "600111", "ϡ���߿�", "http://www.reht.com/new/list.php"};
    urls[694] = new String[] {
        "600879", "����ɷ�", "http://www.rocketstock.com.cn/website/XinWenZhongXinList.aspx?PageSize]=21&TitleNumber]=30&ImgType]=0&CateID]=19&CateName]="};
    urls[695] = new String[] {
        "002123", "���Źɷ�", "http://www.rxpe.com/news/index.asp"};
    urls[696] = new String[] {
        "600848", "���ǹɷ�", "http://www.sac-china.com/news/index.php"};
    urls[697] = new String[] {
        "600268", "��������", "http://www.sac-china.com/news/index.php"};
    urls[698] = new String[] {
        "600482", "�緫�ɷ�", "http://www.sail.com.cn/xinwen/xinwendt.asp"};
    urls[699] = new String[] {
        "600449", "����ʵҵ", "http://www.saimasy.com.cn/newscode.asp?lm]=0&lm2]=18&hot]=0&tj]=0&t]=0&week]=0&font]=9&line]=14&lmname]=0&n]=50&list]=16&more]=1&hit]=0&open]=1&icon]=1&bg]=ffffff"};
    urls[700] = new String[] {
        "002112", "����Ƽ�", "http://www.sanbian.cn/company_wf.asp"};
    urls[701] = new String[] {
        "000970", "�п�����", "http://www.san-huan.com.cn/touzizhe_1.asp?id]=3"};
    urls[702] = new String[] {
        "600829", "������ҩ", "http://www.sanjing.com.cn/company2_1.asp"};
    urls[703] = new String[] {
        "000632", "��ľ����", "http://www.san-mu.com/news.asp"};
    urls[704] = new String[] {
        "000565", "����Ͽ��",
        "http://www.sanxia.com/News.aspx?l]=0&p]=186&c]=186&s]=0&u]=1&f]=News"};
    urls[705] = new String[] {
        "600031", "��һ�ع�", "http://www.sany.com.cn/zg/china/sys/default.jsp?qxid]=01010201&sjid]=010102010201&menuid]=1"};
    urls[706] = new String[] {
        "002044", "��������",
        "http://www.sanyou-chem.com.cn/chpage/c607/doclist.htm"};
    urls[707] = new String[] {
        "600429", "ST��Ԫ", "http://www.sanyuan.com.cn/channel.php?id]=21"};
    urls[708] = new String[] {
        "000019", "�����", "http://www.sbsy.com.cn/news/zixun_gsxw.asp"};
    urls[709] = new String[] {
        "002098", "��˹ɷ�", "http://www.sbszipper.com.cn/xwzx/xxxw/"};
    urls[710] = new String[] {
        "600979", "�㰲����", "http://www.sc-aaa.com/ywxx02.asp"};
    urls[711] = new String[] {
        "600618", "�ȼ��", "http://www.scacc.com/xwgg/news.htm"};
    urls[712] = new String[] {
        "600391", "�ɷ��Ƽ�",
        "http://www.scfast.com/News/InfoList.aspx?Category]=2"};
    urls[713] = new String[] {
        "600170", "�Ϻ�����", "http://www.scg.com.cn/jggf/xwzx.asp"};
    urls[714] = new String[] {
        "002023", "���ظ���", "http://www.schtgx.com/more.asp?ttt]=3&sss]=��������"};
    urls[715] = new String[] {
        "000586", "��Դͨ��",
        "http://www.schy.com.cn/docc/news_class.asp?newscontent_id]=58"};
    urls[716] = new String[] {
        "600678", "�Ĵ���", "http://www.scjd.cn/N0000231_more.aspx"};
    urls[717] = new String[] {
        "000912", "���컯", "http://www.sclth.com/news.asp"};
    urls[718] = new String[] {
        "600984", "*ST����", "http://www.scmc-xa.com/Scmc_Chinese/scmc_zong1.htm"};
    urls[719] = new String[] {
        "000731", "�Ĵ�����",
        "http://www.scmeif.com/gb/news/newslist.asp?class]=50&ParentID]="};
    urls[720] = new String[] {
        "600674", "��Ͷ��Դ", "http://www.scte.com.cn/p2.asp"};
    urls[721] = new String[] {
        "000155", "�����ɷ�", "http://www.scwltd.com/more_news.php"};
    urls[722] = new String[] {
        "000001", "S�չA", "http://www.sdb.com.cn/sdbsite/category/66696c6573/77636d73/534442/7a68/7a685f434e/534442496e666f/5344424e657773"};
    urls[723] = new String[] {
        "600841", "�ϲ�ɷ�", "http://www.sdec.com.cn/sec.asp?tuneid]=7"};
    urls[724] = new String[] {
        "600350", "ɽ������",
        "http://www.sdecl.com.cn/news.asp?bigclassname]=%B9%AB%CB%BE%D0%C2%CE%C5"};
    urls[725] = new String[] {
        "000070", "�ط���Ϣ", "http://www.sdgi.com.cn/news/news.asp?flag]=1"};
    urls[726] = new String[] {
        "600467", "�õ���", "http://www.sdhaodangjia.com/news.asp"};
    urls[727] = new String[] {
        "600886", "��Ͷ����", "http://www.sdicpower.com/main.asp?action]=pu"};
    urls[728] = new String[] {
        "600962", "��Ͷ��³", "http://www.sdiczl.com/news/index.asp"};
    urls[729] = new String[] {
        "000655", "�����ҵ", "http://www.sdjlky.com/Class.asp?ClassID]=97"};
    urls[730] = new String[] {
        "000720", "³��̩ɽ",
        "http://www.sdlnts.com/Article_class2.asp?ClassID]=15&sid]=15"};
    urls[731] = new String[] {
        "000915", "ɽ����", "http://www.sd-wit.com/xinwenzhongxin73.htm"};
    urls[732] = new String[] {
        "002084", "��Ÿ��ԡ", "http://www.seagullgroup.cn/new.jsp"};
    urls[733] = new String[] {
        "000503", "����ع�", "http://www.searainbow.com/more.asp?moreImg]=&catalogid]=���¶�̬&img]=pot_pink.gif&symbol]=&nextpage]=t&site]=www.searainbow.com&len]=20"};
    urls[734] = new String[] {
        "600185", "���ǿƼ�", "http://www.seastar.net.cn/new_zy_sort.asp?leibie]=a"};
    urls[735] = new String[] {
        "000032", "��ɣ���", "http://www.sedind.com/news.asp"};
    urls[736] = new String[] {
        "000058", "*ST����", "http://www.segcl.com.cn/article.php"};
    urls[737] = new String[] {
        "002029", "��ƥ��", "http://www.septwolves.com/news.htm"};
    urls[738] = new String[] {
        "600303", "���ɷ�",
        "http://www.sgautomotive.com/html/01_sgxw/news/index.asp"};
    urls[739] = new String[] {
        "000959", "�׸ֹɷ�",
        "http://www.sggf.com.cn/asp-bin/GB/index.asp?page]=7&class]=37"};
    urls[740] = new String[] {
        "002110", "��������", "http://www.sgmg.com.cn/mgnews/gsxw/xwdt/xwdt1.htm"};
    urls[741] = new String[] {
        "600843", "�Ϲ��건", "http://www.sgsbgroup.com/news.asp"};
    urls[742] = new String[] {
        "600636", "������",
        "http://www.sh3f.com/sf/internet/ShowListServlet?menucd]=A002"};
    urls[743] = new String[] {
        "000014", "ɳ�ӹɷ�", "http://www.shahe.cn/cn/News_dynamic.asp"};
    urls[744] = new String[] {
        "600009", "�Ϻ�����", "http://www.shairport.com/travel/news.jsp"};
    urls[745] = new String[] {
        "600882", "��ɹɷ�", "http://www.shandongdacheng.com/news/index.asp"};
    urls[746] = new String[] {
        "000967", "*ST�Ϸ�", "http://www.shangfeng.net/cn-e-1.php"};
    urls[747] = new String[] {
        "600591", "�Ϻ�����", "http://www.shanghai-air.com/shdt.html"};
    urls[748] = new String[] {
        "600630", "��ͷ�ɷ�", "http://www.shanghaidragon.com.cn/news/"};
    urls[749] = new String[] {
        "600073", "�Ϻ�÷��", "http://www.shanghaimaling.com/newEbiz1/EbizPortalFG/portal/html/InfoCategoryInfoMultiPage.html?InfoCategoryInfoList150_action]=more&InfoPublish_CategoryID]=c373e908fb3406128f6ed50404cc03bd&InfoCategoryInfoList150_InfoCurCategoryID]=c373e908fb3406128f6ed50404cc03bd"};
    urls[750] = new String[] {
        "000680", "ɽ�ƹɷ�", "http://www.shantui.com/news.asp"};
    urls[751] = new String[] {
        "600567", "ɽӥֽҵ", "http://www.shanyingpaper.com/news.asp"};
    urls[752] = new String[] {
        "000601", "���ܹɷ�", "http://www.shaoneng.com.cn/moneyman/gg.asp"};
    urls[753] = new String[] {
        "600059", "��Խ��ɽ", "http://www.shaoxingwine.com.cn/jt/news.asp"};
    urls[754] = new String[] {
        "600849", "�Ϻ�ҽҩ",
        "http://www.shaphar.com.cn:9080/shapharWeb/default/news.jsp"};
    urls[755] = new String[] {
        "600850", "��������", "http://www.shecc.com/xwzx.asp"};
    urls[756] = new String[] {
        "600604", "���Ļ�", "http://www.shefj.com/html/news_list.asp"};
    urls[757] = new String[] {
        "000933", "���ɷ�", "http://www.shenhuo.com/much0.asp?id]=2"};
    urls[758] = new String[] {
        "600823", "��ï�ɷ�", "http://www.shimao.com.cn/morenews.asp"};
    urls[759] = new String[] {
        "600587", "�»�ҽ��", "http://www.shinva.com/xw/DetailList.asp?id]=004"};
    urls[760] = new String[] {
        "600653", "�껪�ع�", "http://www.shkg.com.cn/news/news.asp"};
    urls[761] = new String[] {
        "000707", "˫���Ƽ�", "http://www.shkj.cn/docc/news/news.asp"};
    urls[762] = new String[] {
        "600680", "�Ϻ�����",
        "http://www.shpte.com.cn/main?main_colid]=214&colid]=215"};
    urls[763] = new String[] {
        "600620", "��巹ɷ�", "http://www.shstc.com/news/news.htm"};
    urls[764] = new String[] {
        "600834", "��ͨ����", "http://www.shtmetro.com/xwfb/main.asp"};
    urls[765] = new String[] {
        "000835", "�Ĵ�ʥ��",
        "http://www.shuangdeng.com.cn/news/news.asp?n_type]=9"};
    urls[766] = new String[] {
        "000895", "S˫��", "http://www.shuanghui.net/www/news/index.html"};
    urls[767] = new String[] {
        "600481", "˫���ɷ�", "http://www.shuangliang.com/newpage/gsxw.htm"};
    urls[768] = new String[] {
        "600648", "�����", "http://www.shwgq.com/list_titleb.asp"};
    urls[769] = new String[] {
        "600757", "*STԴ��", "http://www.shworldbest.com/news.asp"};
    urls[770] = new String[] {
        "600732", "�Ϻ���÷", "http://www.shxinmei.com/cn/news/"};
    urls[771] = new String[] {
        "600748", "��ʵ��չ", "http://www.sidlgroup.com/gsyw/index.htm"};
    urls[772] = new String[] {
        "600460", "ʿ��΢", "http://www.silan.com.cn/info/info.aspx"};
    urls[773] = new String[] {
        "000301", "˿��ɷ�", "http://www.silkgroup.com/html/info.htm"};
    urls[774] = new String[] {
        "600270", "���˷�չ", "http://www.sinoair.com/chn/02/0201/"};
    urls[775] = new String[] {
        "600500", "�л�����", "http://www.sinochemintl.com/cn/2media/1news.asp"};
    urls[776] = new String[] {
        "600970", "�вĹ���",
        "http://www.sinoma.com.cn/news_list.asp?sort]=%B9%AB%CB%BE%B6%AF%CC%AC"};
    urls[777] = new String[] {
        "002080", "�вĿƼ�", "http://www.sinomatech.com/html-cn/news-list.php"};
    urls[778] = new String[] {
        "000877", "��ɽ�ɷ�", "http://www.sinoma-tianshan.cn/News.asp"};
    urls[779] = new String[] {
        "600028", "�й�ʯ��", "http://www.sinopec.com/newsevent/index.shtml"};
    urls[780] = new String[] {
        "600459", "���в�ҵ", "http://www.sino-platinum.com.cn/news.htm"};
    urls[781] = new String[] {
        "600061", "�з�Ͷ��",
        "http://www.sinotex-ctrc.com.cn/news_list.asp?type]=3"};
    urls[782] = new String[] {
        "600607", "��ʵҽҩ",
        "http://www.siph.com.cn/(ywqu5bylvbir1o455k1ozdvs)/Default.aspx"};
    urls[783] = new String[] {
        "000563", "�¹�Ͷ��", "http://www.siti.com.cn/news.asp?C_rootID]=A00160005"};
    urls[784] = new String[] {
        "002022", "�ƻ�����", "http://www.skhb.com/cn/news/"};
    urls[785] = new String[] {
        "000783", "S*STʯ��", "http://www.slhec.com/"};
    urls[786] = new String[] {
        "002038", "˫��ҩҵ",
        "http://www.slpharm.com.cn/web/news/news.jsp?NewsType_ID]=21001"};
    urls[787] = new String[] {
        "000410", "��������", "http://www.smtcl.com/web/news/"};
    urls[788] = new String[] {
        "002109", "�˻��ɷ�", "http://www.snxhchem.com/chpage/c701/doclist.asp"};
    urls[789] = new String[] {
        "600746", "��������", "http://www.sopo.com.cn/news.asp?typeid]=1"};
    urls[790] = new String[] {
        "000920", "*ST��ͨ",
        "http://www.southhuiton.com/pages/xwzx/default.asp?xwfl]=1"};
    urls[791] = new String[] {
        "000909", "��Դ�Ƽ�", "http://www.soyea.com.cn/allnews.asp?type]=hot"};
    urls[792] = new String[] {
        "600118", "�й�����", "http://www.spacesat.com.cn/3_News/nw_default.htm"};
    urls[793] = new String[] {
        "002064", "���就��", "http://www.spandex.com.cn/cn/news/"};
    urls[794] = new String[] {
        "600688", "S��ʯ��",
        "http://www.spc.com.cn/cnspc/newsroommore.php?cid]=119&Dlev]=3"};
    urls[795] = new String[] {
        "600000", "�ַ�����", "http://www.spdb.com.cn/chpage/c446/doclist.aspx"};
    urls[796] = new String[] {
        "600421", "����ɷ�", "http://www.spring.com.cn/news.asp"};
    urls[797] = new String[] {
        "600627", "�ϵ�ɷ�", "http://www.sptd.com.cn/News/newslist.asp?class]=1"};
    urls[798] = new String[] {
        "000068", "S����", "http://www.ssg.com.cn/news.asp"};
    urls[799] = new String[] {
        "600884", "ɼɼ�ɷ�", "http://www.ssgf.net/news.asp"};
    urls[800] = new String[] {
        "000788", "���Ϻϳ�", "http://www.sspgf.com/news/index.htm"};
    urls[801] = new String[] {
        "000676", "˼��߿�", "http://www.starhi-tech.com/more/more.asp?typeid]=11&typename]=%CB%BC%B4%EF%B6%AF%CC%AC"};
    urls[802] = new String[] {
        "600820", "����ɷ�", "http://www.stec.net/Directorate/Directorate.asp"};
    urls[803] = new String[] {
        "000982", "S*STѩ��", "http://www.st-edenw.com/news.asp"};
    urls[804] = new String[] {
        "600633", "��è�ɷ�", "http://www.stof.com.cn/news/news.asp"};
    urls[805] = new String[] {
        "600593", "����ʥ��", "http://www.sunasia.com/sunasia/news.asp"};
    urls[806] = new String[] {
        "600990", "�Ĵ�����", "http://www.sun-create.com/sc/news/gsxw.php"};
    urls[807] = new String[] {
        "000571", "�´��ޣ�", "http://www.sundiro.com/main.asp?Url]=news&UrlID]=2"};
    urls[808] = new String[] {
        "002078", "̫��ֽҵ", "http://www.sunpapergroup.com/express/index.htm"};
    urls[809] = new String[] {
        "600728", "S*ST��̫",
        "http://www.suntektech.com/apps/news/news_center.asp?typename]=news_44"};
    urls[810] = new String[] {
        "002083", "���չɷ�", "http://www.sunvim.com/news-center.asp"};
    urls[811] = new String[] {
        "002097", "ɽ������", "http://www.sunward.com.cn/xwzx/xwbb.asp"};
    urls[812] = new String[] {
        "002115", "��άͨ��", "http://www.sunwave.com.cn/web/news.asp?FirstKind]=MT_00002_100017458&KindID]=MT_00002_100017477&KindID2]=MT_00002_100017478"};
    urls[813] = new String[] {
        "600571", "���Ŵ�", "http://www.sunyard.com/news/company.jsp"};
    urls[814] = new String[] {
        "000608", "����ɷ�", "http://www.supershine.com.cn/news/ygdt.jsp"};
    urls[815] = new String[] {
        "002032", "�ղ���", "http://www.supor.com.cn/news/list.asp?id]=1"};
    urls[816] = new String[] {
        "600602", "������", "http://www.sva-e.com/cn/news/"};
    urls[817] = new String[] {
        "600637", "�����Ϣ", "http://www.svainfo.com/cn/news/news03.asp"};
    urls[818] = new String[] {
        "600145", "��ά��ҵ", "http://www.swell.com.cn/dt.aspx"};
    urls[819] = new String[] {
        "000831", "�����ɷ�", "http://www.sxglgf.com/NEWS/news_company.asp"};
    urls[820] = new String[] {
        "600740", "ɽ������", "http://www.sxjh.com.cn/docc/new/news.asp"};
    urls[821] = new String[] {
        "600293", "��Ͽ�²�",
        "http://www.sxxc.com.cn/newEbiz1/EbizPortalFG/portal/html/info-gsxw.html"};
    urls[822] = new String[] {
        "000698", "��������",
        "http://www.sychem.com/cn/company/news.asp?category]=2"};
    urls[823] = new String[] {
        "002028", "˼Դ����", "http://www.syec.com.cn/news/"};
    urls[824] = new String[] {
        "600490", "�пƺϳ�", "http://www.synica.com.cn/zk/cn/aboutus.asp?id]=26"};
    urls[825] = new String[] {
        "600819", "ҫƤ����", "http://www.sypglass.com/news.asp"};
    urls[826] = new String[] {
        "600183", "����Ƽ�",
        "http://www.syst.com.cn/web/syst.nsf/program/004001?opendocument"};
    urls[827] = new String[] {
        "000028", "һ��ҩҵ", "http://www.szaccord.com.cn/test/docc/news/news.php?PHPSESSID]=ce5ca6af121f3c9d80984cc31e6c2ceb"};
    urls[828] = new String[] {
        "000089", "���ڻ���", "http://www.szairport.com/Catalog_211.aspx?t]=2433"};
    urls[829] = new String[] {
        "000061", "ũ��Ʒ", "http://www.szap.com/news/gsxw.asp"};
    urls[830] = new String[] {
        "002047", "���عɷ�",
        "http://www.szcl.com.cn/news/disp.asp?tid]=7&title]=��˾����"};
    urls[831] = new String[] {
        "002121", "��½����", "http://www.szclou.com/kelu/news_next.asp?yw]=y"};
    urls[832] = new String[] {
        "000022", "������", "http://www.szcwh.com/webapp/main.asp?sid]=118"};
    urls[833] = new String[] {
        "600548", "�����", "http://www.sz-expressway.com/docc/tzzhd_xwzx.php"};
    urls[834] = new String[] {
        "600446", "��֤�ɷ�",
        "http://www.szkingdom.com/news/moreNews.asp?newsSort]=��˾��̬"};
    urls[835] = new String[] {
        "000011", "S����ҵA",
        "http://www.szwuye.com.cn/docc/xinwen/dongtai.asp?sort]=��������"};
    urls[836] = new String[] {
        "600129", "̫������", "http://www.taiji.com/allnews.asp"};
    urls[837] = new String[] {
        "600222", "̫��ҩҵ",
        "http://www.taloph.com/news/showNews.asp?typeid]=134479872"};
    urls[838] = new String[] {
        "600665", "���Դ", "http://www.tande.cn/news.asp"};
    urls[839] = new String[] {
        "000856", "��ɽ�մ�", "http://www.tangshanceramic.com/news.cfm"};
    urls[840] = new String[] {
        "000709", "�Ƹֹɷ�",
        "http://www.tangsteel.com.cn/list.jsp?nav2_Id]=33&&navId]=137"};
    urls[841] = new String[] {
        "600535", "��ʿ��", "http://www.tasly.com/list.aspx?cid]=107"};
    urls[842] = new String[] {
        "600089", "�ر�繤",
        "http://www.tbea.com.cn/Modules/News/TextNews/Default.aspx?SortID]=1"};
    urls[843] = new String[] {
        "000100", "*STTCL", "http://www.tcl.com/main/NEWS/groupNews/"};
    urls[844] = new String[] {
        "002100", "�쿵����", "http://www.tcsw.com.cn/otype.asp?owen1]=���Ŷ�̬"};
    urls[845] = new String[] {
        "600330", "��ͨ�ɷ�", "http://www.tdgcore.com/xwzx/index.asp?cat_id]=1"};
    urls[846] = new String[] {
        "600582", "��ؿƼ�", "http://www.tdtec.com/News/News_Index.asp?Type]=In"};
    urls[847] = new String[] {
        "000630", "ͭ��ͭҵ",
        "http://www.tdty.com/appframe.asp?list1]=info&list2]=gsxx"};
    urls[848] = new String[] {
        "600410", "��ʤ���", "http://www.teamsun.com.cn/index.php?option]=com_content&task]=category&sectionid]=5&id]=15&Itemid]=112"};
    urls[849] = new String[] {
        "000555", "*ST̫��", "http://www.techo.cn/news/default.asp"};
    urls[850] = new String[] {
        "000652", "̩��ɷ�", "http://www.tedastock.com/news1.asp"};
    urls[851] = new String[] {
        "600590", "̩���Ƽ�", "http://www.tellhow.com/news.asp"};
    urls[852] = new String[] {
        "000025", "������", "http://www.tellus.cn/tl/news.aspx"};
    urls[853] = new String[] {
        "600512", "�ڴｨ��", "http://www.tengdajs.com/xw.htm"};
    urls[854] = new String[] {
        "600322", "�췿��չ", "http://www.tfgroup.com.cn/newscenter/index.htm"};
    urls[855] = new String[] {
        "600509", "�츻�ȵ�",
        "http://www.tfrd.com.cn/news_list.asp?c_id]=8&s_id]=134"};
    urls[856] = new String[] {
        "600867", "ͨ������", "http://www.thdb.com/list.aspx?cid]=17"};
    urls[857] = new String[] {
        "000766", "ͨ������", "http://www.thjm.cn/news/list_show.php?sortid]=4"};
    urls[858] = new String[] {
        "600100", "ͬ���ɷ�", "http://www.thtf.com.cn/www/web/news/index_news.aspx"};
    urls[859] = new String[] {
        "000938", "�Ϲ�ɷ�",
        "http://www.thunis.com/thunis/news/news/2007/news.htm"};
    urls[860] = new String[] {
        "002124", "���ɷ�", "http://www.tianbang.com/news/index.asp?ClassID]=5"};
    urls[861] = new String[] {
        "600791", "�촴��ҵ",
        "http://www.tianchuang-zy.com/news/index.php?c_id]=87"};
    urls[862] = new String[] {
        "600376", "��豦ҵ",
        "http://www.tianhong-baoye.com.cn/news/all.asp?Dname]=��˾����&flag]=1"};
    urls[863] = new String[] {
        "600378", "��ƹɷ�", "http://www.tianke.com/news.htm"};
    urls[864] = new String[] {
        "000050", "�������", "http://www.tianma.cn/docc/news/gsdt.asp"};
    urls[865] = new String[] {
        "600435", "��������",
        "http://www.tianniao.com.cn/chinese/morenews.asp?publishColumn]=��˾����"};
    urls[866] = new String[] {
        "600703", "S*ST����", "http://www.tianyi.cc/Html/NewsList.asp"};
    urls[867] = new String[] {
        "600749", "����ʥ��", "http://www.tibetshengdi.com/xzsd/dtxx.asp"};
    urls[868] = new String[] {
        "600392", "̫�����", "http://www.tichn.com/news/news.pl"};
    urls[869] = new String[] {
        "000917", "��㴫ý", "http://www.tik.com.cn/z_x_g.asp"};
    urls[870] = new String[] {
        "600874", "��ҵ����", "http://www.tjcep.com/news/InfoList.aspx?Category]=6"};
    urls[871] = new String[] {
        "000927", "һ������", "http://www.tjfaw.com.cn/news/index.asp"};
    urls[872] = new String[] {
        "000711", "������ҵ", "http://www.tlzy.com.cn/news.asp"};
    urls[873] = new String[] {
        "000090", "���콡", "http://www.tonge.com.cn/docc/news/groupnews.asp"};
    urls[874] = new String[] {
        "600237", "ͭ�����",
        "http://www.tong-feng.com/tfnews/more.asp?ttt]=4&sss]=��˾����"};
    urls[875] = new String[] {
        "600438", "ͨ���ɷ�", "http://www.tongwei.com.cn/news/crop.asp"};
    urls[876] = new String[] {
        "600862", "S*STͨ��", "http://www.tonmac.com.cn/cpl/news.php"};
    urls[877] = new String[] {
        "600253", "�췽ҩҵ",
        "http://www.topfond.com/cn/news/newslist.asp?catalog]=1"};
    urls[878] = new String[] {
        "002134", "�������", "http://www.toppcb.com/news/new-shiji.asp"};
    urls[879] = new String[] {
        "600771", "��ʢ�Ƽ�", "http://www.topsun.com/tdsxw.asp"};
    urls[880] = new String[] {
        "600233", "�����", "http://www.trands.com/news.asp"};
    urls[881] = new String[] {
        "002010", "�����ɷ�", "http://www.transfarchem.com/cgi/search-cn.cgi?f]=news_cn+company_cn_1_&t]=news_cn&w]=news_cn&cate1]=��˾��̬"};
    urls[882] = new String[] {
        "000883", "�����ɷ�", "http://www.triring.cn/xwfb/xwfb.aspx?catagory]=184"};
    urls[883] = new String[] {
        "600458", "ʱ���²�", "http://www.trp.cn/list_b.asp?b_id]=4"};
    urls[884] = new String[] {
        "600600", "�ൺơ��", "http://www.tsingtao.com.cn/list/www_tsingtao_com_cn/2004/cn/information/news.jsp"};
    urls[885] = new String[] {
        "002117", "���۹ɷ�", "http://www.tungkong.com.cn/news05.asp"};
    urls[886] = new String[] {
        "600737", "�����ͺ�", "http://www.tunhe.com/cn/news/class0.asp?ClassID]=5"};
    urls[887] = new String[] {
        "002057", "�и���Դ", "http://www.tunhe.com/cn/news/class0.asp?ClassID]=5"};
    urls[888] = new String[] {
        "600550", "��������",
        "http://www.twbb.com/web/news.asp?bid]=2&sid]=23&bcid]=2"};
    urls[889] = new String[] {
        "000795", "̫ԭ����", "http://www.twin-tower.com/news/gsxw.htm"};
    urls[890] = new String[] {
        "600488", "��ҩ�ɷ�", "http://www.tygf-jy.com/xinwen.asp"};
    urls[891] = new String[] {
        "000036", "�����ع�", "http://www.udcgroup.com/news.asp"};
    urls[892] = new String[] {
        "600588", "�������", "http://www.ufida.com.cn/news/list_quanbu.aspx"};
    urls[893] = new String[] {
        "000932", "�������", "http://www.valin.cn/xwmore.asp"};
    urls[894] = new String[] {
        "000002", "��ƣ�", "http://www.vanke.com/main/catalogNews_10633.aspx"};
    urls[895] = new String[] {
        "600246", "��ͨ�ȷ�", "http://www.vantonepioneer.com.cn/news.asp"};
    urls[896] = new String[] {
        "600152", "ά�ƾ���", "http://www.vekenelite.com/news/"};
    urls[897] = new String[] {
        "600803", "��Զ����", "http://www.veyong.com/xinwenzx.asp"};
    urls[898] = new String[] {
        "000407", "ʤ���ɷ�", "http://www.vicome.com/Article/Index.asp"};
    urls[899] = new String[] {
        "600300", "άά�ɷ�", "http://www.vvgroup.com/news/index.php"};
    urls[900] = new String[] {
        "600055", "��ҽ��", "http://www.wandong.com.cn/gk_qydt2.htm"};
    urls[901] = new String[] {
        "600223", "*ST���", "http://www.wanjie.com/html/news/index.htm"};
    urls[902] = new String[] {
        "600847", "ST������", "http://www.wanli.net.cn/news.asp"};
    urls[903] = new String[] {
        "000544", "��ԭ����",
        "http://www.wdg.com.cn/listnewsnormal.html?newsColumnId]=1"};
    urls[904] = new String[] {
        "000338", "Ϋ����", "http://www.weichai.com/about/channel/news.shtml"};
    urls[905] = new String[] {
        "002026", "ɽ������", "http://www.weidapeacock.com/news.asp"};
    urls[906] = new String[] {
        "002003", "ΰ�ǹɷ�", "http://www.weixing.cn/docc/news/news.asp"};
    urls[907] = new String[] {
        "002058", "����̩", "http://www.welltech.com.cn/companynews.asp"};
    urls[908] = new String[] {
        "000543", "���ܵ���", "http://www.wenergy.com.cn/xwdt1.asp"};
    urls[909] = new String[] {
        "002085", "������", "http://www.wfaw.com.cn/news_2.php"};
    urls[910] = new String[] {
        "600859", "������", "http://www.wfj.com.cn/index/more.aspx?TypeID]=1"};
    urls[911] = new String[] {
        "600976", "�人����", "http://www.whjm.com/news/news.jsp?type]=1"};
    urls[912] = new String[] {
        "000668", "S��ʯ��", "http://www.whoil.com/xwsd.htm"};
    urls[913] = new String[] {
        "000759", "�人�а�", "http://www.whzb.com/zygg/index.asp"};
    urls[914] = new String[] {
        "600681", "S*ST���", "http://www.winowner.com/02index01.asp?id]=24"};
    urls[915] = new String[] {
        "600005", "��ֹɷ�",
        "http://www.wisco.com.cn/wisco/news/wisco_news/2007news/index.shtml"};
    urls[916] = new String[] {
        "002090", "���ǿƼ�", "http://www.wiscom.com.cn/news.php?id]=12"};
    urls[917] = new String[] {
        "000789", "����ˮ��", "http://www.wnq.com.cn/Get/qyxw/index.htm"};
    urls[918] = new String[] {
        "002130", "�ֶ��˲�", "http://www.woer.com/news/InfoList.aspx?Category]=3"};
    urls[919] = new String[] {
        "002107", "�ֻ�ҽҩ", "http://www.wohua.cn/news.asp"};
    urls[920] = new String[] {
        "600580", "��������", "http://www.wolong.com.cn/chinese/news/list_info.php?list_action]=list_all&news_type]=0120&news_lan]=gb"};
    urls[921] = new String[] {
        "600094", "*ST��Դ", "http://www.worldbest.sh.cn/files_cn/news.asp"};
    urls[922] = new String[] {
        "600995", "��ɽ����", "http://www.wsdl.com.cn/news/news_index.asp"};
    urls[923] = new String[] {
        "600575", "�ߺ���", "http://www.wuhuport.com/news/news.cfm?type]=1"};
    urls[924] = new String[] {
        "000858", "����Һ", "http://www.wuliangye.com.cn/pages/newsList.xml"};
    urls[925] = new String[] {
        "600200", "��������", "http://www.wuzhong.com/branch/js/list.asp?class]=1"};
    urls[926] = new String[] {
        "600063", "��ά����", "http://www.wwgf.com.cn/News/gsnews.asp"};
    urls[927] = new String[] {
        "000559", "����Ǯ��", "http://www.wxqc.com.cn/news.asp"};
    urls[928] = new String[] {
        "600667", "̫��ʵҵ", "http://www.wxtj.com/cn/zxdt.php"};
    urls[929] = new String[] {
        "000862", "������Դ", "http://www.wzyb.com.cn/news/index.asp"};
    urls[930] = new String[] {
        "600252", "�к㼯��", "http://www.wz-zhongheng.com/zhsd/108.htm"};
    urls[931] = new String[] {
        "000425", "�칤�Ƽ�", "http://www.xcmg.com/xinwen/default.asp"};
    urls[932] = new String[] {
        "000721", "������ʳ", "http://www.xcsg.com/news/index.asp"};
    urls[933] = new String[] {
        "600723", "�����̳�", "http://www.xdsc.com.cn/xinxi/qydt.asp"};
    urls[934] = new String[] {
        "000900", "�ִ�Ͷ��", "http://www.xdtz.net/release/list.asp?id]=9"};
    urls[935] = new String[] {
        "600416", "���ɷ�",
        "http://www.xemw.com/cn/news/Aboute_company_list.asp?r_id]=24&m_id]=59"};
    urls[936] = new String[] {
        "600825", "�»���ý", "http://www.xhmedia.com/news.asp"};
    urls[937] = new String[] {
        "000756", "�»���ҩ", "http://www.xhyyjt.com/admin/news_more.asp?lm]=&lm2]=109&open]=_blank&tj]=0&hot]=0"};
    urls[938] = new String[] {
        "600897", "���ſո�", "http://www.xiac.com.cn/info.asp?sort]=3"};
    urls[939] = new String[] {
        "000799", "S*ST�ƹ�",
        "http://www.xiangjiugui.cn/web/news/news_list.php?news_type_id]=1"};
    urls[940] = new String[] {
        "600596", "�°��ɷ�", "http://www.xinanchem.com/xahg_news.php"};
    urls[941] = new String[] {
        "600777", "�³�ʵҵ", "http://www.xinchaoshiye.com/dongtai.asp"};
    urls[942] = new String[] {
        "002019", "�θ�ҩҵ",
        "http://www.xinfupharm.com/Chinese/News.asp?Action]=Co"};
    urls[943] = new String[] {
        "600141", "�˷�����", "http://www.xingfagroup.com/news_list.asp"};
    urls[944] = new String[] {
        "002120", "�º��ɷ�", "http://www.xinhaigroup.com/cn/about_news.asp"};
    urls[945] = new String[] {
        "000955", "�����ع�",
        "http://www.xinlong-nonwovens.com/news/xldt.asp?type]=1"};
    urls[946] = new String[] {
        "000836", "��ï�Ƽ�", "http://www.xinmaokeji.com.cn/xwzx/xw/"};
    urls[947] = new String[] {
        "000778", "��������", "http://www.xinxing-pipes.com/xinwenlist.asp"};
    urls[948] = new String[] {
        "002087", "��Ұ��֯", "http://www.xinye-tex.com/news.asp?nodeid]=N11101"};
    urls[949] = new String[] {
        "600545", "�½��ǽ�", "http://www.xjcj.com/newslist.asp"};
    urls[950] = new String[] {
        "000159", "����ʵҵ", "http://www.xjgjsy.com/fore/xw.htm"};
    urls[951] = new String[] {
        "600256", "���ɷ�",
        "http://www.xjguanghui.com/information/news/list.asp?path]=news"};
    urls[952] = new String[] {
        "600425", "���ɽ���", "http://www.xjqscc.com/infosort.php?id]=18"};
    urls[953] = new String[] {
        "600075", "�½���ҵ", "http://www.xj-tianye.com/news/"};
    urls[954] = new String[] {
        "000723", "S����", "http://www.xj-ty.com/info/newslist2.asp?class]=69"};
    urls[955] = new String[] {
        "600778", "�Ѻü���",
        "http://www.xjyh.com.cn/yhtest/ReadSClass.jsp?SClassID]=4"};
    urls[956] = new String[] {
        "000905", "���Ÿ���", "http://www.xmgw.com.cn/news/news.asp?ttype]=1"};
    urls[957] = new String[] {
        "600686", "��������", "http://www.xmklm.com.cn/news.jsp?header]=��˾����"};
    urls[958] = new String[] {
        "002127", "����Ƽ�", "http://www.xmtex.com/gsjj/dsj.htm"};
    urls[959] = new String[] {
        "600117", "�����ظ�", "http://www.xntg.com/yw.htm"};
    urls[960] = new String[] {
        "600870", "�û�����", "http://www.xoceco.com.cn/news02.asp"};
    urls[961] = new String[] {
        "600353", "���ɷ�", "http://www.xuguang.com/cn/NewsView.screen"};
    urls[962] = new String[] {
        "600403", "������Ѷ",
        "http://www.xwtech.com/index.php?page]=news_company&class]=1&name]=��˾����"};
    urls[963] = new String[] {
        "600373", "���¹ɷ�", "http://www.xxgf.com.cn/news/webnews/gzhh/gzhh1.htm"};
    urls[964] = new String[] {
        "600326", "������·", "http://www.xztianlu.com/news.asp"};
    urls[965] = new String[] {
        "600211", "S��ҩҵ", "http://www.xzyy.cn/news.asp"};
    urls[966] = new String[] {
        "600351", "�Ǳ�ҩҵ", "http://www.yabao.com.cn/index/more2.asp"};
    urls[967] = new String[] {
        "000729", "�ྩơ��",
        "http://www.yanjing.com.cn/Content.asp?MainId]=2&BigClassid]=1"};
    urls[968] = new String[] {
        "600261", "�㽭����", "http://www.yankon.com/docc/news/xinwen.asp"};
    urls[969] = new String[] {
        "000811", "��̨����", "http://www.yantaimoon.com/chinese/news/index.asp"};
    urls[970] = new String[] {
        "000088", "�����", "http://www.yantian-port.com/news/"};
    urls[971] = new String[] {
        "600716", "*STҫ��", "http://www.yaohuaglass.com.cn/xinwen/index.asp"};
    urls[972] = new String[] {
        "600188", "����úҵ", "http://www.yasheng.com.cn/news/index1.asp"};
    urls[973] = new String[] {
        "600108", "��ʢ����", "http://www.yasheng.com.cn/news/index1.asp"};
    urls[974] = new String[] {
        "600881", "��̩����",
        "http://www.yatai.com/home/news/index.asp?channelid]=9"};
    urls[975] = new String[] {
        "600871", "S�ǻ�",
        "http://www.ycfc.com/SYCF_showcontent.aspx?categoryID]=sycf.060.000"};
    urls[976] = new String[] {
        "600345", "����ͨ��", "http://www.ycig.com/web/cn/news/index.php"};
    urls[977] = new String[] {
        "600238", "����Ҭ��", "http://www.yedao.com/info/ANclassshow.asp?id]=1"};
    urls[978] = new String[] {
        "000616", "�ڳǹɷ�",
        "http://www.yeland.com.cn/ggsj/newg.asp?typeid]=1&bigclassid]=1&smallclassid]="};
    urls[979] = new String[] {
        "000929", "���ݻƺ�", "http://www.yellowriver.net.cn/www/sContentsMain.asp?ClassId]=2&Page]=1&Number1]=1"};
    urls[980] = new String[] {
        "600531", "ԥ���Ǧ", "http://www.yggf.com.cn/xinwenfabu.asp"};
    urls[981] = new String[] {
        "002063", "Զ�����", "http://www.ygsoft.com/news/news.htm"};
    urls[982] = new String[] {
        "000519", "���Ӷ���",
        "http://www.yhdle.com/news/News_List.php?ClassName]=��˾����&ID]=6&Language]=GB"};
    urls[983] = new String[] {
        "000792", "�κ��ط�", "http://www.yhjf.com/news_qy.asp"};
    urls[984] = new String[] {
        "600978", "�˻�ľҵ", "http://www.yihuatimber.com/News.asp"};
    urls[985] = new String[] {
        "600887", "�����ɷ�", "http://www.yili.com/news/yilinews/index.html"};
    urls[986] = new String[] {
        "600197", "������", "http://www.yilispirit.com/info/list.asp?Sortid]=336"};
    urls[987] = new String[] {
        "600824", "����ٻ�", "http://www.yimingroup.com/news/news_index.php"};
    urls[988] = new String[] {
        "600069", "����Ͷ��",
        "http://www.yinge.com.cn/Article/ShowClass.asp?ClassID]=1"};
    urls[989] = new String[] {
        "000806", "���ӿƼ�", "http://www.yinhetech.com/News.asp"};
    urls[990] = new String[] {
        "002126", "���ֹɷ�", "http://www.yinlun.cn/list.asp?boardid]=11"};
    urls[991] = new String[] {
        "600858", "�����ɷ�", "http://www.yinzuostock.com/news.asp"};
    urls[992] = new String[] {
        "600515", "STһͶ", "http://www.yitou.com/cgi-bin/gsdt/"};
    urls[993] = new String[] {
        "600317", "Ӫ�ڸ�", "http://www.ykplc.com/?id]=2"};
    urls[994] = new String[] {
        "000807", "�����ɷ�", "http://www.ylgf.com/xinxi/more.asp?news_class]=no"};
    urls[995] = new String[] {
        "600486", "��ũ����", "http://www.yngf.com/news/SmallClass.asp?BigClassID]=19&BigClassName]=��ũ�ٵ�&SmallClassID]=24&SmallClassName]=����Ҫ��"};
    urls[996] = new String[] {
        "002053", "�����λ�",
        "http://www.ynyh.com/outnews/news_list.asp?type_n]=news"};
    urls[997] = new String[] {
        "600105", "��������", "http://www.yongding.com.cn/b.asp"};
    urls[998] = new String[] {
        "600177", "�Ÿ��", "http://www.youngor.com/News/Index.asp"};
    urls[999] = new String[] {
        "002086", "��������",
        "http://www.yt-fishery.com/news/newslist.asp?class]=12"};
    urls[1000] = new String[] {
        "000960", "��ҵ�ɷ�", "http://www.ytl.com.cn/newytl/xwbd/all.asp"};
    urls[1001] = new String[] {
        "600309", "��̨��", "http://www.ytpu.com/News.asp"};
    urls[1002] = new String[] {
        "000683", "��Ȼ��", "http://www.yuanxing.com/dongtai/news_more.asp?lm]=&lm2]=18&lmname]=0&open]=1&n]=48&tj]=&hot]=0"};
    urls[1003] = new String[] {
        "600805", "�ô�Ͷ��", "http://www.yueda.com/col8/col10/index.htm1?id]=10"};
    urls[1004] = new String[] {
        "002033", "��������", "http://www.yulongtour.com/nescenter/index.htm"};
    urls[1005] = new String[] {
        "001896", "ԥ�ܿع�", "http://www.yuneng.com.cn/listnews.php?sortid]=1"};
    urls[1006] = new String[] {
        "000538", "���ϰ�ҩ", "http://www.yunnanbaiyao.com.cn/news/listNews.do?method]=listNews&dirId]=1&sortId]=1&sortName]=��˾����"};
    urls[1007] = new String[] {
        "000903", "���ڶ���", "http://www.yunneidongli.com/ynxw.asp"};
    urls[1008] = new String[] {
        "600366", "��������", "http://www.yunsheng.com/news1.asp"};
    urls[1009] = new String[] {
        "600066", "��ͨ�ͳ�", "http://www.yutong.com/chinese/group/news/ytnews.asp"};
    urls[1010] = new String[] {
        "600655", "ԥ԰�̳�", "http://www.yuyuantm.com.cn/meiti_xinwen.htm"};
    urls[1011] = new String[] {
        "600963", "����ֽҵ", "http://www.yypaper.com/gonggao/view.asp"};
    urls[1012] = new String[] {
        "600096", "���컯", "http://www.yyth.com.cn/news/newslist.jsp?classid]=1"};
    urls[1013] = new String[] {
        "000819", "�����˳�", "http://www.yyxc0819.com/article/"};
    urls[1014] = new String[] {
        "600485", "�д��Ų�", "http://www.zcxc.com.cn/news/index.asp"};
    urls[1015] = new String[] {
        "600595", "����ʵҵ", "http://www.zfsy.com.cn/cn/news.asp"};
    urls[1016] = new String[] {
        "002075", "������ͭ", "http://www.zhangtong.com.cn/china/qyxw.php"};
    urls[1017] = new String[] {
        "000767", "�������",
        "http://www.zhangzepower.com/main/qyxw.asp?class_id]=2"};
    urls[1018] = new String[] {
        "002069", "��ӵ�", "http://www.zhangzidao.com/docc/news.asp"};
    urls[1019] = new String[] {
        "000006", "����ҵ��", "http://www.zhenye.com/Catalog_8.aspx"};
    urls[1020] = new String[] {
        "600517", "���ŵ���", "http://www.zhixindianqi.com.cn/ci8.aspx"};
    urls[1021] = new String[] {
        "000421", "�Ͼ��б�", "http://www.zhong-bei.com/default.asp?cataid]=30"};
    urls[1022] = new String[] {
        "600704", "�д�ɷ�", "http://www.zhongda.com/news/index_c.asp"};
    urls[1023] = new String[] {
        "000659", "�麣�и�",
        "http://www.zhongfu.com.cn/link_news.asp?EDI]=CN&TT_ID]=1&T_ID]=33"};
    urls[1024] = new String[] {
        "002070", "�ں͹ɷ�",
        "http://www.zhonghe.com/CNewsShow.aspx?LanID]=1&ID]=8"};
    urls[1025] = new String[] {
        "000785", "�人����",
        "http://www.zhongshang.com.cn/content/news.asp?TypeID]=2"};
    urls[1026] = new String[] {
        "000957", "��ͨ�ͳ�",
        "http://www.zhongtong.com/Classc.asp?Classc]=CA991384A0E84DC4-9E7D3286A966984D"};
    urls[1027] = new String[] {
        "600329", "����ҩҵ", "http://www.zhongxinp.com/news/main.htm"};
    urls[1028] = new String[] {
        "600210", "�Ͻ���ҵ", "http://www.zijiangqy.com/news.asp"};
    urls[1029] = new String[] {
        "600489", "�н�ƽ�",
        "http://www.zjgold.com/dynamic_sub_4_11.asp?newsclassid]=16"};
    urls[1030] = new String[] {
        "002067", "����ֽҵ", "http://www.zjjxjt.com/news-p3.htm"};
    urls[1031] = new String[] {
        "002012", "�����ɷ�", "http://www.zjkan.com/news.asp"};
    urls[1032] = new String[] {
        "600120", "�㽭����", "http://www.zjorient.com/news.asp"};
    urls[1033] = new String[] {
        "002050", "�����ɷ�",
        "http://www.zjshc.com/new1.asp?BigClassName]=��%20ҵ%20��%20��"};
    urls[1034] = new String[] {
        "002122", "����ɷ�",
        "http://www.zjtmb.com/information/company_news.asp?stype]=1"};
    urls[1035] = new String[] {
        "000705", "�㽭��Ԫ", "http://www.zjzy.com/news.asp"};
    urls[1036] = new String[] {
        "000157", "�����ؿ�",
        "http://www.zljt.com/Article/Article_Class2.asp?ClassID]=37"};
    urls[1037] = new String[] {
        "002021", "�нݹɷ�", "http://www.zoje.com/zoje/news.asp"};
    urls[1038] = new String[] {
        "000685", "���ÿƼ�", "http://www.zpus000685.net/news.php?category]=���ű���"};
    urls[1039] = new String[] {
        "000540", "*ST����", "http://www.ztcn.cn/ggao/"};
    urls[1040] = new String[] {
        "000063", "����ͨѶ",
        "http://www.zte.com.cn/main/include/list.jsp?catalogId]=12084&date]=2007"};
    urls[1041] = new String[] {
        "002092", "��̩��ѧ",
        "http://www.zthx.com/BigClass.asp?typeid]=25&BigClassid]=107"};
    urls[1042] = new String[] {
        "000715", "������ҵ",
        "http://www.zxbusiness.com/news/index.php?classid]=74&classname]=ҵ������"};
    urls[1043] = new String[] {
        "000678", "�������", "http://www.zxy.com.cn/news/news_show.asp?type]=1"};
    urls[1044] = new String[] {
        "600020", "��ԭ����", "http://www.zygs.com/news/index.asp"};
    urls[1045] = new String[] {
        "600121", "֣��ú��", "http://www.zzce.com.cn/news.asp"};
    urls[1046] = new String[] {
        "600436", "Ƭ���", "http://www.zzpzh.com/class.asp?newsid]=zzpzh_gsdt"};

  }

}
