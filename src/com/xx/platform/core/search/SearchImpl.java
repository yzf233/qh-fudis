package com.xx.platform.core.search;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.*;
import com.xx.platform.util.constants.*;
import com.xx.platform.web.actions.search.*;

import org.apache.nutch.searcher.*;
import org.apache.nutch.searcher.Summarizer;
import org.apache.nutch.util.NutchConf;

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
public class SearchImpl {
  public static final int HITS_PER_PAGE = 20;
  public static Logger log = IbeaProperty.log;
  private static final SimpleDateFormat timeFormat = new SimpleDateFormat(
      "yyyy-MM-dd");
  private static final NumberFormat numberFormat = NumberFormat.getInstance(
      Locale.US);
  public static String isDefaultParse;
  static {
    numberFormat.setMaximumFractionDigits(3);
    numberFormat.setMinimumFractionDigits(2);
    isDefaultParse = NutchConf.get().get("search.query.parse", "basic");
  }

  private SearchResult searchResult = null;
  private List resultList;
  private int end = 0, tlength = 0; // first hit to display
  private org.apache.nutch.searcher.Query query = null;
  private Hits hits = null;
  /**
   * 传入查询关键字
   * @param bean NutchBean
   * @param queryString String
   * @param start int
   * @param hits_per_page int
   * @return List
   * @throws IOException
   */
  public List search(NutchBean bean, String queryString, final int start,
                     final int hits_per_page) throws IOException {
    if (isDefaultParse != null && isDefaultParse.equals("xdht")) {
      query = new org.apache.nutch.searcher.Query(); //.parse(queryString);
      query.setXdht(queryString);
    }
    else {
      query = org.apache.nutch.searcher.Query.parse(queryString); //.parse(queryString);
    }
    
    return search(bean, queryString, query, start, hits_per_page, null, false, "docNo");
  }

  /**
   * 传入查询关键字
   * @param bean NutchBean
   * @param queryString String
   * @param start int
   * @param hits_per_page int
   * @return List
   * @throws IOException
   */
  public List search(NutchBean bean, String queryString, Query query,
                     final int start,
                     final int hits_per_page) throws IOException {
    return search(bean, queryString, query, start, hits_per_page, null, false, "docNo");
  }

  /**
   * 传入查询关键字
   * @param bean NutchBean
   * @param queryString String
   * @param start int
   * @param hits_per_page int
   * @return List
   * @throws IOException
   */
  public List search(NutchBean bean, String queryString, final int start,
                     final int hits_per_page, String sortField, boolean reverse,
                     String dedupField) throws IOException {
    if (isDefaultParse != null && isDefaultParse.equals("xdht")) {
      query = new org.apache.nutch.searcher.Query(); //.parse(queryString);
      query.setXdht(queryString);
    }
    else {
      query = org.apache.nutch.searcher.Query.parse(queryString); //.parse(queryString);
    }
    return search(bean, queryString, query, start, hits_per_page, sortField,
                  reverse, dedupField);
  }

  /**
   * 传入Query
   * @param bean NutchBean
   * @param query Query
   * @param start int
   * @param hits_per_page int
   * @return List
   * @throws IOException
   */
  public List search(NutchBean bean, String queryString, Query query, int start,
                     final int hits_per_page, String sortField, boolean reverse,
                     String dedupField) throws IOException {
    long startTime = System.currentTimeMillis();
    String content="";//拼成content
    if (sortField == null) {
      hits = bean.search(query, start + hits_per_page, 1, dedupField);
    }
    else {
      hits = bean.search(query, start + hits_per_page, 1, dedupField, sortField,
                         reverse);
    }
    end = (int) Math.min(hits.getLength(), start + hits_per_page);
    start = (start =
             (start > hits.getLength() ?
              (hits.getLength() - hits.getLength() % hits_per_page) : start)) >
        0 ? start : 0;

    Hit[] show = hits.getHits(start, end - start);
    HitDetails[] details = bean.getDetails(show);
//    String [] qs=query.getTerms();
//    boolean hasfile=false;
//    
//    for(int i=0;i<qs.length;i++)
//    {
//     if(qs[i].equals("file"))
//    	 hasfile=true;
//    }
//    String[] summaries =null; 
//    if(hasfile)
//    summaries= bean.getSummary(details, Query.parse(query.getXdht()));
    int length = end - start;
    resultList = new SearchList();
    ( (SearchList) resultList).setStart(start);
    ( (SearchList) resultList).setEnd(end);
    {
      ( (SearchList) resultList).setTotal( (end - start) < hits_per_page ?
                                          hits.getLength() :
                                          (int) hits.getTotal());
    }
    long endTime = System.currentTimeMillis();
    ( (SearchList) resultList).setTime( (float) (endTime - startTime) / 1000.0f);
    {
      for (int i = 0; i < length; i++) {
        searchResult = new SearchResult();
        if (show.length > i) {
          searchResult.setHit(show[i]);
        }
        searchResult.setTitle(details[i].getValue("title"));
        if (details.length > i) {
          searchResult.setScore(show[i].getScore());
          searchResult.setDocNo(show[i].getIndexDocNo());
          float score=show[i].getScore();
          String sc=String.valueOf(score);
          searchResult.setScore(score);
          searchResult.setDetails(details[i]);
          searchResult.setUrl(details[i].getValue("url"));
          searchResult.setTime(String.valueOf( (endTime - startTime) / 1000.0f));
//          String ustr="";
          List indexMessage=new ArrayList();
          content="";
//          try
//          {
//        	  List<IndexField> l = SearchContext.getDao().findAllByIObjectCType(IndexField.class);//取到用户设置的所有索引字段
//        	  if(l!=null&&l.size()>0)
//        	  for(IndexField f:l)
//        	  {
//        		  ustr=f.getCode()+"：";//取索引名
//        		  String str=details[i].getValue(f.getCode());
////        		  if(str!=null)
////        		  content+=str+" ";//拼成content
//        		  if(str!=null&&str.trim().length()>50)//保留50个字
//                      str=str.trim().substring(0,49)+"...";
//        		  ustr+=str;//取索引内容
//        		  indexMessage.add(ustr);
//        	  }
//          }
//          catch(Exception e)
//          {
//        	  e.printStackTrace();
//        	  //取索引失败
//          }
//         if(content==null||content.toString().replace(" ","").equals(""))
         boolean isfile=details[i].getValue("docSource")!=null && details[i].getValue("docSource").equals("file");
         if(isfile)//如果是文件，取content内容
         {
//          String contentFra="";
//        	  Properties metaData = bean.getParseData(details[i]).getMetadata();
//              
//                  String encoding = (String) metaData.get("CharEncodingForConversion");
//                  if (encoding != null) {
//                          try {
//                              contentFra = new String(bean.getContent(details[i]), encoding);
//                          } catch (java.io.UnsupportedEncodingException e) {
//                              // fallback to windows-1252
//                              contentFra = new String(bean.getContent(details[i]),"windows-1252");
//                          }
//                  }else
//                  {
//                	  contentFra = new String(bean.getContent(details[i]));
//                  }
        	 content = details[i].getValue("content") ;
//          content=contentFra;
         }
          searchResult.setContent(content);
          searchResult.setUstr(indexMessage);
          
          String contentLength = details[i].getValue("contentLength");
          long contentLen = Long.parseLong(contentLength != null &&
                                           contentLength.trim().length() > 0 ?
                                           contentLength : "0");
          long contentLengM = (contentLen +
                               (1 << ( (contentLen > 1 << 20) ? 20 : 10) - 1) >>
                               ( (contentLen > 1 << 20) ? 20 : 10));
          searchResult.setLength(contentLengM);
        }

//      if(!content.replace(" ","").equals(""))//如果content字段不为空，则进行摘要
//        {
//            if (summaries[i] != null && summaries.length > i) {
//                searchResult.setSummaries(summaries[i].replaceAll(
//                    "amp;",
//                    "").replaceAll("&amp;",
//                                   "").replaceAll("#160;",
//                                                  ""));
//              }
//        }
//        else
//          {
//          	searchResult.setSummaries("");
//          }
        if(!content.replace(" ","").equals(""))//如果content字段不为空，则进行摘要
        {
        	Summarizer s = new Summarizer();
            searchResult.setSummaries((s.getSummary(content, query)).toString().replaceAll(
              "amp;",
              "").replaceAll("&amp;",
                             "").replaceAll("#160;",
                                            ""));
        }
        else
        {
        	searchResult.setSummaries("");
        }
        resultList.add(searchResult);
      }
      //摘要或信息过滤
    }

    runtimeData(bean.getSegmentNames().length,
                (float) (endTime - startTime) / 1000.0f);
    return resultList;
  }

  private static void runtimeData(int segmentsLength, float time) {
    { //获得 查询服务器的 Segments 数量 ， 获得 查询服务器数量和 查询 索引数量 通过 数据接口访问获得
      RuntimeDataCollect.setSegments(segmentsLength);
      RuntimeDataCollect.setSearch_num(1);
      RuntimeDataCollect.setSearch_status("Running");
      RuntimeDataCollect.setReturn_time(time);
      if (RuntimeDataCollect.getSearch_num() == 0) {
        RuntimeDataCollect.setEval_return_time(String.valueOf(0.001f));
      }
      else {
        RuntimeDataCollect.setEval_return_time(String.valueOf(
            numberFormat.format(RuntimeDataCollect.getReturn_time() /
                                RuntimeDataCollect.getSearch_num())));
        if ( (float) (RuntimeDataCollect.getReturn_time() /
                      RuntimeDataCollect.getSearch_num()) >= 1.0f) {
          RuntimeDataCollect.setEval_return_time(String.valueOf(0.90f));
        }
      }
      /**
       * 补充 获得服务器运行的数据信息 ，如 ：服务器数量、服务器Segment数量、索引数量等，数据
       * 访问接口是 ServerData
       */
    }

  }
}
