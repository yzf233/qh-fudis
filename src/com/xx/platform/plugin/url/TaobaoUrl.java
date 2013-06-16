package com.xx.platform.plugin.url;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.xx.platform.dao.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

/**
 * <p>Title: 淘宝数据采集URL发生器，只获得第一页数据，不包含翻页信息，需要获得全部的有效地址
 *           需要修改 productList 或者 categoryList 方法 以获得全部的 分页
 *    注意：淘宝全部信息有500万以上的数据 ， 获得全部数据需要很长时间，实际应用过程中，请尽量只
 *         获取某个特定分类的信息，例如 数码相机分类 有近十万条数据
 *    提示：修改分类代码位于92行
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
public class TaobaoUrl implements com.xx.platform.plugin.url.UrlGenerator {
    private static MultiThreadedHttpConnectionManager connectionManager = new
            MultiThreadedHttpConnectionManager();
    private static final String THREAD_GROUP = "TAOBAO_GROUP";
    private static HttpClient client = new HttpClient(connectionManager);
    /**
     * 使用超线程请注意同步 categoryList
     * @return List
     * @throws Exception
     */
    public List<String> generator() throws Exception {
        List<String> productList = new ArrayList();
        List<String>
                categoryList = categoryList(getContext("http://www.taobao.com"));
        /**
         * 可以扩展到多线程在即  connectionManager 支持超线程 ，但要对 categoryList 同步
         */
        TaobaoThread thread;

        for (int i = 0; i < 50; i++) {
            thread = new TaobaoThread(categoryList, productList,
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

//        int start = content.indexOf("<div id=\"CategoryList\">");
//        int end = content.indexOf("<div id=\"PromBanner\">");
//
//        content = start > 0 && end > 0 ? content.substring(start, end) :
//                  content;
//        content = content.replaceAll(" class=\"H\"", "");
//        Pattern pattern = null;
//        Matcher matcher = null;
//        pattern = Pattern.compile(
//                "<li><a href=\"http://list.taobao.com/browse([\\s\\S]*?)\" target=\"_blank\">([\\s\\S]*?)</a></li>");
//        matcher = pattern.matcher(content);
//        String category;
//        while (matcher.find()) {
//            category = matcher.group(1);
//            /**
//             * 适当增加过滤规则以筛选分类
//             */
//            if (category != null && category.trim().length() > 0) {
//                categoryList.add("http://list.taobao.com/browse" + category);
//            }
//        }
        categoryList.add("http://list.taobao.com/browse/50005523-1101/t-g,giydcnjuhizdcnrugi--g,giydcnjuhjeueti--------------------y-80-list-commend-0-1,2-1101.htm") ;
        categoryList.add("http://list.taobao.com/browse/50005523-1101/t-g,giydcnjuhizdcnruhe--g,giydcnjuhk553rwvf5efa--------------------y-80-list-commend-0-1,2-1101.htm") ;
        categoryList.add("http://list.taobao.com/browse/50005523-1101/t-g,giydcnjuhizdcnruhe--g,giydcnjuhk553rwvf5efa--------------------y-80-list-commend-0-1,2-1101.htm") ;
        return categoryList;
    }

    /**
     * 只采集分类首页 、不采集分页信息 ， 支持 get 方式获取分页数据
     * @param content String
     * @return List
     */
    private static final Pattern pattern = Pattern.compile(
            "<div class=\"Name\">([\\s\\S]*?)<a href=\"([\\s\\S]*?)\"([\\s\\S]*?)</div>");
    ;


    public List productList(String content) {
        List<String> productList = new ArrayList();
//        content = content.substring(content.indexOf("<div id=\"CategoryList\">"),content.indexOf("<div id=\"PromBanner\">")) ;
//        content = content.replaceAll(" class=\"H\"","") ;
        Matcher matcher = pattern.matcher(content);
        String product = "";

        while (matcher.find()) {
            if (matcher.groupCount() > 2)
                product = matcher.group(2);
            if (product != null && product.trim().length() > 0 &&
                product.startsWith("http://auction1.taobao.com/auction")) {
                productList.add(product);

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
                strb.append(new String(responseByte, 0, byteLength));
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
        TaobaoUrl taobao = new TaobaoUrl();
        try {
            taobao.generator();
        } catch (Exception ex) {
        }
    }

    private ThreadGroup group = new ThreadGroup(THREAD_GROUP); // our group
    public class TaobaoThread extends Thread {
        private List<String> categoryList;
        private List<String> productList;
        private String category;
        private IBase service;
        private InnerURL innerUrl;
        public TaobaoThread(List categoryList, List productList,
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
                categoryList = categoryList(getContext("http://www.taobao.com"));
        /**
         * 可以扩展到多线程在即  connectionManager 支持超线程 ，但要对 categoryList 同步
         */
        TaobaoThread thread;

        for (int i = 0; i < 50; i++) {
            thread = new TaobaoThread(categoryList, productList,
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
