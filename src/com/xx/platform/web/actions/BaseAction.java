package com.xx.platform.web.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.Action;
import com.xx.platform.dao.GeneraDAO;
import com.xx.platform.domain.model.crawl.Crawler;
import com.xx.platform.domain.model.database.Dbconfig;
import com.xx.platform.domain.model.database.Dbtable;
import com.xx.platform.util.constants.IbeaProperty;

import org.apache.nutch.util.NutchConf;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.web.context.WebApplicationContext;

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
public class BaseAction implements ServletHandler{
    public static final String DISABLE_SEARCH = "disable_search" ;
    public static Logger log = IbeaProperty.log ;
    public static Crawler gcrawler;
    public static Dbconfig sdbconfig; //复制的database
    public static Dbtable sdbtable; //复制的Dbtable
    //配置文件中用 ， 需要构造的URL 的参数和参数值 ，和  urlParam 组合使用
    protected Map<String , String> urlMap = new HashMap<String,String>();
    protected HttpServletRequest request ;
    protected HttpServletResponse response ;
    //配置文件中用 ， 需要构造的URL 的参数和参数值 ，和  urlMap 组合使用
    private StringBuffer urlParam ;
    protected WebApplicationContext wac  ;
    protected static GeneraDAO service ;
    protected String message ;
    protected final int THR_PAGE_SIZE = 30 ;
    protected final int TW_PAGE_SIZE = 20 ;
    protected final int FIVT_PAGE_SIZE = 15 ;
    protected final int TEN_PAGE_SIZE = 10 ;
    protected final int FIV_PAGE_SIZE = 5 ;
    private long freeM ;
    protected String userIP;//访问者IP
    public static  String companyForShort;//公司简称
    public static  String companyFull;//公司全称
    public static  String companyUrl;//公司地址
    public static  String isBottomInfo;//是否显示底部的信息
    public static  String resultRightTitle;//前台右侧标题
    public static  String resultRightUrl;//前台右侧地址
    public static  String isDefaultParse;//前台右侧地址
    public static  String versionName;//版本名称
    public static int fileCrawlerCount=10;//文件爬虫数量
	public static long default_parse_time;
	public static long select_per_num ;
    static{
       companyForShort=(NutchConf.get().get("company.short")!=null?NutchConf.get().get("company.short"):"");
       companyFull=(NutchConf.get().get("company.full")!=null?NutchConf.get().get("company.full"):"");
       companyUrl=(NutchConf.get().get("company.url")!=null?NutchConf.get().get("company.url"):"");
       isBottomInfo=(NutchConf.get().get("is.bottom.info")!=null?NutchConf.get().get("is.bottom.info"):"");
       resultRightTitle=(NutchConf.get().get("result.right.title")!=null?NutchConf.get().get("result.right.title"):"");
       resultRightUrl=(NutchConf.get().get("result.right.url")!=null?NutchConf.get().get("result.right.url"):"");
       isDefaultParse=(NutchConf.get().get("search.query.parse")!=null?NutchConf.get().get("search.query.parse"):"");
       versionName=(NutchConf.get().get("version.name")!=null?NutchConf.get().get("version.name"):"");
       fileCrawlerCount = NutchConf.get().getInt("filefetcher.threads.fetch", 10);
      try{ default_parse_time = Long.parseLong((NutchConf.get().get("default.parse.time")!=null?NutchConf.get().get("default.parse.time"):"1800000"));}catch (Exception e) {default_parse_time=1800000;}
      try{ select_per_num = Long.parseLong((NutchConf.get().get("select.per.num")!=null?NutchConf.get().get("select.per.num"):"50000"));}catch (Exception e) {select_per_num=50000;}
    }

    /**
     * 需要在配置 Struts.xml 文件中构造的URL参数 , 参数获取方式 ${urmMap} 或者 ${parames} 或者 ${param}
     * @return String
     */
    public String getUrlMap() {
        urlParam = new StringBuffer() ;

        for(String param : urlMap.keySet())
        {
            urlParam = (urlParam.length()!=0?urlParam.append("&"):urlParam).append(param).append("=").append(urlMap.get(param)) ;
        }
        return urlParam.toString();
    }
    /**
     * 需要在配置 Struts.xml 文件中构造的URL参数 , 参数获取方式 ${urmMap} 或者 ${parames} 或者 ${param}
     * @return String
     */
    public String getParames() {
        return getUrlMap();
    }
    /**
     * 需要在配置 Struts.xml 文件中构造的URL参数 , 参数获取方式 ${urmMap} 或者 ${parames} 或者 ${param}
     * @return String
     */
    public String getParam() {
        return getUrlMap();
    }

    /**
     * 从拦截器中注入 Request
     * @param request HttpServletRequest
     * @throws Exception
     */
    public void setServletRequest(HttpServletRequest request) throws Exception {
        this.request = request ;
    }
    /**
     * 从拦截器中注入 Response
     * @param response HttpServletResponse
     * @throws Exception
     */
    public void setServletResponse(HttpServletResponse response) throws
            Exception {
        this.response = response ;
    }
    /**
     * 从拦截器中注入 WebApplicationContext
     * @param wac WebApplicationContext
     * @throws Exception
     */
    public void setWebApplicationContext(WebApplicationContext wac) throws
            Exception {
        this.wac = wac ;
    }
    /**
     * 从 Spring 构造Bean ， 获得 Service 、Dao 对象
     * @param serviceName String
     * @return GeneraDAO
     */
    public GeneraDAO getService(String serviceName)
    {
        return (GeneraDAO)wac.getBean(serviceName) ;
    }

    public void setGeneraDAO(GeneraDAO dao) throws Exception {
        service = service==null?(service = wac!=null?(GeneraDAO)wac.getBean(IbeaProperty.DAO_NAME_SPACE):null):service ;
    }
    /**
     * 默认的 方法 ， 比ActionSuport 性能稍好 , 如果有数据需要填入， 则在子类中覆盖此方法
     * 使用SkipValidation ，不采用 注释的方式验证提交的表单， 性能较 不加 SkipValidation
     * 稍好
     * @return String
     * @throws Exception
     */
    @SkipValidation
    public String execute() throws Exception {
        return Action.SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public long getFreeM() {
        Runtime r = Runtime.getRuntime();
        long totalMemory = r.totalMemory();
        long freeMemory = r.freeMemory();
        long userMemory = r.totalMemory() - r.freeMemory();
        long freeM = (freeMemory + (1 << ((freeMemory > 1 << 20) ? 20 : 10) - 1) >>
                      ((freeMemory > 1 << 20) ? 20 : 10));

        return freeM;
    }

    public Crawler getGcrawler() {
        return gcrawler;
    }

    public String getCompanyForShort() {
        return companyForShort;
    }

    public String getCompanyFull() {
        return companyFull;
    }

    public String getCompanyUrl() {
        return companyUrl;
    }

    public String getIsBottomInfo() {
        return isBottomInfo;
    }

    public String getResultRightTitle() {
        return resultRightTitle;
    }

    public String getResultRightUrl() {
        return resultRightUrl;
    }

    public Dbconfig getSdbconfig() {
        return sdbconfig;
    }

    public Dbtable getSdbtable() {
        return sdbtable;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFreeM(long freeM) {
        this.freeM = freeM;
    }

    public void setGcrawler(Crawler gcrawler) {
        this.gcrawler = gcrawler;
    }

    public void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }

    public void setCompanyFull(String companyFull) {
        this.companyFull = companyFull;
    }

    public void setCompanyForShort(String companyForShort) {
        this.companyForShort = companyForShort;
    }

    public void setIsBottomInfo(String isBottomInfo) {
        this.isBottomInfo = isBottomInfo;
    }

    public void setResultRightTitle(String resultRightTitle) {
        this.resultRightTitle = resultRightTitle;
    }

    public void setResultRightUrl(String resultRightUrl) {
        this.resultRightUrl = resultRightUrl;
    }

    public void setSdbconfig(Dbconfig sdbconfig) {
        this.sdbconfig = sdbconfig;
    }

    public void setSdbtable(Dbtable sdbtable) {
        this.sdbtable = sdbtable;
    }
	public String getUserIP() {
		return userIP;
	}
	public void setUserIP(String userIP) {
		this.userIP = userIP;
	}
}
