package com.xx.platform.core.service;

import java.util.*;

import javax.jws.*;

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
@WebService(targetNamespace = "http://www.xd-tech.com.cn")
public interface ServiceInterface {
    /**
     *��ҳ��ѯ����
     * @param keyword String
     * @param start_index int
     * @param page_size int
     * @return List
     * @throws Exception
     */
    @WebResult(name = "search")
    List<WebServiceSearchResult>   search(String
            keyword, int start_index, int page_size) throws Exception;
    /**
     *��ҳ��ѯ���ܡ�����ָ��field�ֶι���
     * @param keyword String
     * @param start_index int
     * @param page_size int
     * @return List
     * @throws Exception
     */
    @WebResult(name = "search")
    List<WebServiceSearchResult>   search(String
            keyword,String[][] field, int start_index, int page_size) throws Exception;
    
    /**
     *��ҳ��ѯ���ܡ�����ָ��field�ֶΡ������ֶ�,�����ֶ�
     * @param keyword String
     * @param start_index int
     * @param page_size int
     * @return List
     * @throws Exception
     */
    @WebResult(name = "search")
    List<WebServiceSearchResult>   search(String
    		keyword,String[][] field,int start_index, int page_size,String [][] sortReg, String dedupField) throws Exception;
    /**
     *��ҳ��ѯ���ܡ�����ָ��field�ֶΡ������ֶ�,�����ֶ�,����ֱ�������ѯ���,ͨ�����ӱ��ʽת������
     * @param keyword String
     * @param start_index int
     * @param page_size int
     * @return List
     * @throws Exception
     */
    @WebResult(name = "search")
    List<WebServiceSearchResult>   search(String
    		summary_key,UserQuery [] userquery,String[][] field,int start_index, int page_size,String [][] sortReg, String dedupField) throws Exception;
    
    /**
     *��ҳ��ѯ���ܡ�����ָ��field�ֶΡ������ֶΡ������ֶΡ�xdClauses,
     * @param keyword String
     * @param start_index int
     * @param page_size int
     * @return List
     * @throws Exception
     */
    @WebResult(name = "search")
    List<WebServiceSearchResult>   search(String summary_key,String[] field,XDClause[] xdClauses,int start_index, int page_size,String [][] sortReg, String dedupField) throws Exception;
    
    /**
     * ȡ�ִ�
     * @param str
     * @return
     * @throws Exception
     */
    @WebResult(name = "getWords")
    String [] getWords(String str) throws Exception;

    /**
     * ȡ��һ��������¼
     * @param keyword String
     * @param start_index int
     * @param page_size int
     * @param sortField String[]
     * @param reverse boolean
     * @param dumpField String
     * @return List
     * @throws Exception
     */
    @WebResult(name = "search")

    public WebServiceSearchResult search(String No , int idx) throws
                Exception ;
    /**
     * �߼���ѯ����
     * @param keyword String
     * @param start_index int
     * @param page_size int
     * @param sortField String[]
     * @param reverse boolean
     * @param dumpField String
     * @return List
     * @throws Exception
     */
    @WebResult(name = "search_1")
    List<com.xx.platform.core.service.WebServiceSearchResult> search_1(String
            keyword, int start_index, int page_size, String sortField,
            boolean reverse, String dedupField) throws Exception;

    /**
    * �߼���ѯ����
    * @param keyword String
    * @param start_index int
    * @param page_size int
    * @param sortField String[]
    * @param reverse boolean
    * @param dumpField String
    * @return List
    * @throws Exception
    */
   @WebResult(name = "search_2")
   List<com.xx.platform.core.service.WebServiceSearchResult> search_2(String
           keyword,XDClause[] xdClause, int start_index, int page_size, String sortField,
           boolean reverse, String dedupField) throws Exception;

    /**
     * �߼���ѯ����
     * @param keyword String
     * @param start_index int
     * @param page_size int
     * @param sortField String[]
     * @param reverse boolean
     * @param dumpField String
     * @return List
     * @throws Exception
     */
    @WebResult(name = "search_2_dotnet")
    List<com.xx.platform.core.service.WebServiceSearchResult> search_2_dotNET(String
            keyword,XDClauseDotNET[] xdClauseDotNET, int start_index, int page_size, String sortField,
            boolean reverse, String dedupField) throws Exception;
    /**
     * ȡ��һ��������¼
     * @param keyword String
     * @param start_index int
     * @param page_size int
     * @param sortField String[]
     * @param reverse boolean
     * @param dumpField String
     * @return List
     * @throws Exception
     */
    @WebResult(name = "search_3")

    public WebServiceSearchResult search_3(long docNo , int idx) throws
                Exception ;
}
