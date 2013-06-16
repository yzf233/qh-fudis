package com.xx.platform.plugin.url;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.xx.platform.dao.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

/**
 * <p>Title: 百方据采集URL发生器，只获得第一页数据，不包含翻页信息，需要获得全部的有效地址
 *           需要修改 productList 或者 categoryList 方法 以获得全部的 分页
 *
 * </p>
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
public class BaifangUrl implements com.xx.platform.plugin.url.UrlGenerator {
    private static MultiThreadedHttpConnectionManager connectionManager = new
            MultiThreadedHttpConnectionManager();
    private static final String THREAD_GROUP = "BAIFANG_GROUP";
    private static HttpClient client = new HttpClient(connectionManager);
    /**
     * 使用超线程请注意同步 categoryList
     * @return List
     * @throws Exception
     */
    public List<String> generator() throws Exception {
        List<String> productList = new ArrayList();
        List<String>
                categoryList = categoryList(getContext("http://pro.byf.com/"));
        /**
         * 可以扩展到多线程在即  connectionManager 支持超线程 ，但要对 categoryList 同步
         */
        BaifangThread thread;

        for (int i = 0; i < 50; i++) {
            thread = new BaifangThread(categoryList, productList,
                                      THREAD_GROUP + "_" + i, null, null);
            thread.start();
        } while (true) {
            Thread.sleep(3000);
            int n = group.activeCount();
            Thread[] list = new Thread[n];
            group.enumerate(list);
            boolean noMoreFetcherThread = true; // assumption
            for (int i = 0; i < n; i++) {
                // this thread may have gone away in the meantime
                if (list[i] == null)
                    continue;

                String tname = list[i].getName();
                if (tname.startsWith(THREAD_GROUP)) // prove it
                    noMoreFetcherThread = false;
            }
            if (noMoreFetcherThread)
                break;
        }
        return productList;
    }

    public List<String> categoryList(String content) throws Exception {
        List<String> categoryList = new ArrayList();

        int start = content.indexOf("<div class=msrepeat>");
        int end = content.indexOf("<div class=\"right\">");

        content = start > 0 && end > 0 ? content.substring(start, end) :
                  content;
        Pattern pattern = null;
        Matcher matcher = null;
        pattern = Pattern.compile(
                "<div class=mtitle><a href=http://pro.byf.com/productlist/([\\S\\s]*?).shtml target=_blank><strong>");
        matcher = pattern.matcher(content);
        String category;
        while (matcher.find()) {
            category = matcher.group(1);
            /**
             * 适当增加过滤规则以筛选分类
             */
//            categoryList.add(
//                            "http://pro.byf.com/pro_list.aspx?action=0&fid=" +
//                            category + "&page=1");
            categoryList.addAll(nextPageCategoryList("http://pro.byf.com/pro_list.aspx?action=0&fid=" +
                    category + "&page=1","http://pro.byf.com/pro_list.aspx?action=0&fid=" +
                            category + "&page=")) ;
        }
        return categoryList;
    }

    public List nextPageCategoryList(String url , String categoryStr)
    {
        List pageCategoryList = new ArrayList() ;
        String pageContext = getContext(url) ;
        Pattern pattern = null ;//
        Matcher matcher = null;
        pattern = Pattern.compile(
                "<font style='font-size:12px'>共([\\s\\S]*?) 页");
        matcher = pattern.matcher(pageContext);
        String pageNum= "" ;
        if(matcher.find())
        {
            pageNum = matcher.group(1) ;
            int page = 1 ;
            if(pageNum!=null && pageNum.trim().length()>0)
            {
                page = Integer.parseInt(pageNum) ;
                for(int i=1 ; i<=page ; i++)
                {
                    pageCategoryList.add(categoryStr+String.valueOf(i)) ;
                    System.out.println("找到分类翻页："+categoryStr+String.valueOf(i));
                }
            }
        }
        return pageCategoryList ;
    }
    /**
     * 只采集分类首页 、不采集分页信息 ， 支持 get 方式获取分页数据
     * @param content String
     * @return List
     */


    public List productList(String content) {
        List<String> productList = new ArrayList();
        Pattern pattern = Pattern.compile("<a href=([\\S\\s]*?)class=pro_small target=_blank>");
        Matcher matcher = pattern.matcher(content);
        String product = "";
        String match = "";
        while (matcher.find()) {
            if (matcher.groupCount() >= 1)
                product = matcher.group(1);

            if (product != null && product.trim().length() > 0) {
                if(product.equals(match))
                {
                    continue ;
                }else
                {
                    productList.add(product);
                    match = product;
                }
            }
        }
//        for(String productL:productList)
//        {
//            System.out.println("抓取到的URL－－－－－－－－－－－－－－－："+productL);
//        }
        return productList;
    }

    /**
     * 使用POST方式提交数据

     * @return

     */

    private static HttpMethod getPostMethod(String viewState, String page,
                                            String postAction) {
        ///SearchResult.aspx?occIDList=1009001&isInterView=1
        PostMethod post = new PostMethod(postAction);
        NameValuePair simcard = new NameValuePair("", viewState);
        NameValuePair eventGet = new NameValuePair("",
                "");
        NameValuePair eventArg = new NameValuePair("", page);
        post.setRequestBody(new NameValuePair[] {simcard, eventGet, eventArg});
        return post;

    }

    public static String getNextPage(byte[] input, String page,
                                     String postAction) throws Exception {

        return null;
    }

    /**
     * 支持超线程访问，可以使用超线程
     * @param inputURL String
     * @param port int
     * @param protocl String
     * @return String
     * @throws Exception
     */
    public static String getContext(String inputURL) {
        String response = null;
        try {
//            client.getHostConfiguration().setHost(inputURL);
            HttpMethod method = new GetMethod(inputURL); //使用get方式提交数据
            client.executeMethod(method);
            //打印服务器返回的状态
            StringBuffer strb = new StringBuffer();
            //打印结果页面
            byte[] responseByte = new byte[1024];
            int byteLength = 0;
            while ((byteLength = (int) method.getResponseBodyAsStream().read(
                    responseByte)) > 0) {
                strb.append(new String(responseByte, 0, byteLength,"UTF-8"));
                responseByte = new byte[1024];
            }
            response = strb.toString(); //method.getResponseBodyAsString();//

            method.releaseConnection();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response != null ? response : "";
    }


    public static void main(String[] args) {
        BaifangUrl taobao = new BaifangUrl();
        try {
            taobao.generator();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ThreadGroup group = new ThreadGroup(THREAD_GROUP); // our group
    public class BaifangThread extends Thread {
        private List<String> categoryList;
        private List<String> productList;
        private String category;
        private IBase service;
        private InnerURL innerUrl;
        public BaifangThread(List categoryList, List productList,
                            String threadId, IBase service, InnerURL innerUrl) {
            super(group, threadId);
            this.productList = productList;
            this.categoryList = categoryList;
            this.service = service;
            this.innerUrl = innerUrl;
        }

        public void run() {
            {
                try {
                    while (categoryList != null && categoryList.size() > 0 &&
                           (category = categoryList.remove(0)) != null) {
                        if (service != null && innerUrl != null) {
                            List<String> list = productList(getContext(category));
                            for (String url : list) {
                                innerUrl.setUrl(url);
                                service.saveIObject(innerUrl);
                            }
                        } else {
                            productList.addAll(productList(getContext(category)));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };
    public void generator(IBase service, InnerURL innerUrl) throws Exception {
        List<String> productList = new ArrayList();
        List<String>
                categoryList = categoryList(getContext("http://pro.byf.com/"));
        /**
         * 可以扩展到多线程在即  connectionManager 支持超线程 ，但要对 categoryList 同步
         */
        BaifangThread thread;

        for (int i = 0; i < 50; i++) {
            thread = new BaifangThread(categoryList, productList,
                                      THREAD_GROUP + "_" + i, service, innerUrl);
            thread.start();
        } while (true) {
            Thread.sleep(3000);
            int n = group.activeCount();
            Thread[] list = new Thread[n];
            group.enumerate(list);
            boolean noMoreFetcherThread = true; // assumption
            for (int i = 0; i < n; i++) {
                // this thread may have gone away in the meantime
                if (list[i] == null)
                    continue;

                String tname = list[i].getName();
                if (tname.startsWith(THREAD_GROUP)) // prove it
                    noMoreFetcherThread = false;
            }
            if (noMoreFetcherThread)
                break;
        }
    }
}
