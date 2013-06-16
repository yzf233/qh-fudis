package com.xx.platform.plugin.url;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.xx.platform.dao.*;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

/**
 * <p>Title: �Ա����ݲɼ�URL��������ֻ��õ�һҳ���ݣ���������ҳ��Ϣ����Ҫ���ȫ������Ч��ַ
 *           ��Ҫ�޸� productList ���� categoryList ���� �Ի��ȫ���� ��ҳ
 *    ע�⣺�Ա�ȫ����Ϣ��500�����ϵ����� �� ���ȫ��������Ҫ�ܳ�ʱ�䣬ʵ��Ӧ�ù����У��뾡��ֻ
 *         ��ȡĳ���ض��������Ϣ������ ����������� �н�ʮ��������
 *    ��ʾ���޸ķ������λ��92��
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
     * ʹ�ó��߳���ע��ͬ�� categoryList
     * @return List
     * @throws Exception
     */
    public List<String> generator() throws Exception {
        List<String> productList = new ArrayList();
        List<String>
                categoryList = categoryList(getContext("http://www.taobao.com"));
        /**
         * ������չ�����߳��ڼ�  connectionManager ֧�ֳ��߳� ����Ҫ�� categoryList ͬ��
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
//             * �ʵ����ӹ��˹�����ɸѡ����
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
     * ֻ�ɼ�������ҳ �����ɼ���ҳ��Ϣ �� ֧�� get ��ʽ��ȡ��ҳ����
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
//            System.out.println("ץȡ����URL��������������������������������"+productL);
//        }
        return productList;
    }

    /**
     * ʹ��POST��ʽ�ύ����

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
     * ֧�ֳ��̷߳��ʣ�����ʹ�ó��߳�
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
            HttpMethod method = new GetMethod(inputURL); //ʹ��get��ʽ�ύ����
            client.executeMethod(method);
            //��ӡ���������ص�״̬
            StringBuffer strb = new StringBuffer();
            //��ӡ���ҳ��
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
         * ������չ�����߳��ڼ�  connectionManager ֧�ֳ��߳� ����Ҫ�� categoryList ͬ��
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
