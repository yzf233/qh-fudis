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
     *分页查询功能
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
     *分页查询功能——带指定field字段过滤
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
     *分页查询功能——带指定field字段、排序字段,过滤字段
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
     *分页查询功能——带指定field字段、排序字段,过滤字段,可以直接输入查询语句,通过复杂表达式转换工具
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
     *分页查询功能——带指定field字段、排序字段、过滤字段、xdClauses,
     * @param keyword String
     * @param start_index int
     * @param page_size int
     * @return List
     * @throws Exception
     */
    @WebResult(name = "search")
    List<WebServiceSearchResult>   search(String summary_key,String[] field,XDClause[] xdClauses,int start_index, int page_size,String [][] sortReg, String dedupField) throws Exception;
    
    /**
     * 取分词
     * @param str
     * @return
     * @throws Exception
     */
    @WebResult(name = "getWords")
    String [] getWords(String str) throws Exception;

    /**
     * 取得一条索引记录
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
     * 高级查询功能
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
    * 高级查询功能
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
     * 高级查询功能
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
     * 取得一条索引记录
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
