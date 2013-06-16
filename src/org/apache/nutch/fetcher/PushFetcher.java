/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.fetcher;

import java.io.*;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.nutch.*;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.core.service.PushObject;
import com.xx.platform.domain.model.database.Dbtable;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.util.tools.ArraysObjectTool;
import com.xx.platform.util.tools.MD5;

import org.apache.lucene.index.IndexWriter;
import org.apache.nutch.analysis.NutchDocumentAnalyzer;
import org.apache.nutch.db.Page;
import org.apache.nutch.fs.*;
import org.apache.nutch.indexer.IndexMerger;
import org.apache.nutch.indexer.NutchSimilarity;
import org.apache.nutch.io.ArrayFile;
import org.apache.nutch.io.MD5Hash;
import org.apache.nutch.ipc.RPC;
import org.apache.nutch.pagedb.FetchListEntry;
import org.apache.nutch.parse.*;
import org.apache.nutch.plugin.PluginRepository;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.protocol.ProtocolStatus;
import org.apache.nutch.util.LogFormatter;
import org.apache.nutch.util.NutchConf;

/**
 * The fetcher. Most of the work is done by plugins.
 *
 * <p>
 * Note by John Xing: As of 20041022, option -noParsing is introduced.
 * Without this option, fetcher behaves the old way, i.e., it not only
 * crawls but also parses content. With option -noParsing, fetcher
 * does crawl only. Use ParseSegment.java to parse fetched contents.
 * Check FetcherOutput.java and ParseSegment.java for further description.
 */
public class PushFetcher {
//    public static String PushFilePath = FetchListTool.createSegment().getPath();
//    public static String PushFilePath = SearchContext.search_dir+File.separator+"push";
  public static final Logger LOG =
      LogFormatter.getLogger("org.apache.nutch.fetcher.Fetcher");

  private WebDbAdminTool webDbAdminTool = new WebDbAdminTool();

  private static IndexWriter pushIndexWriter;

  private static String dictory;

  private static boolean parsing = true; // whether do parsings

  

  private Page page = new Page("http://www.www.com", 1f, 0);

  private static Thread hookThread;
  /********************************************
   * Fetcher thread
   ********************************************/
  private class FetcherThread{
      private String fetcherThreadname;

    public FetcherThread(String name) {
     this.fetcherThreadname = name;
    }


    private void outputPage(FetcherOutput fo, Content content,
                            ParseText text, ParseData parseData) {
      try {
        {
          /**
           * URL 满足 采集规则后进行相关的非结构化抽取处理 , 处理 规则来自于 抽取规则
           * 如果不满足 ， 则采用默认的索引处理 ，并指定默认的分类字段
           */
        	IndexFetcher.indexpage(content);
        	
				List<Synchro> synchro = SearchContext.getSynchroList();
				if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
						&& synchro.size() > 0){
					for (Synchro s : synchro) {// 遍历每个节点
						try {
							byte[] bfo=ArraysObjectTool.ObjectToArrays(fo);
							byte[] bcontent=ArraysObjectTool.ObjectToArrays(content);
							byte[] btext=ArraysObjectTool.ObjectToArrays(text);
							byte[] bparseData=ArraysObjectTool.ObjectToArrays(parseData);
							ImInterface imInterface=(ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut);
							imInterface.outputPage(bfo,bcontent,btext,bparseData);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
            RuntimeDataCollect.setCrawl_page_num(1);

        }
      }
      catch (Throwable t) {
//                LOG.severe("error writing output:" + t.toString());
          t.printStackTrace();
      }
//      }finally{
//          closeWriter();
//      }
    }
  }



  /**
   * 数据推送服务
   * @param docType String
   * @param subDocType String
   * @param title String
   * @param date String
   * @param author String
   * @param chuchu String
   * @param gupiao String
   * @param context String
   * @param sourcef String
   * @param sourceHost String
   */
  public void pushData(String docType, String title, String[] fields,
                       String[] value, String url) throws Exception {
      FetcherThread fetcherThread = new FetcherThread("push");
      try {

        Properties properties = new Properties();
        StringBuffer strb = new StringBuffer();


        {
          List<Dbtable> dbtableList = SearchContext.getDbtableList();
          Dbtable pdbtable = null;
          for (Dbtable dbtable : dbtableList) {
            if (docType != null && docType.equals(dbtable.getCode())) {
              pdbtable = dbtable;
              properties.put("docType",dbtable.getCode());

              String dburl = dbtable.getDbid().getDburl();
              String dbtype = dbtable.getDbid().getDbtype();
              String ip="";
              String dbname="";
              try
              {
              if (dbtype.equals("oracle")) {
      			int start = dburl.indexOf("@") + 1;
      			int end = dburl.indexOf(":", start);
      			ip = dburl.substring(start, end);
      			int portend = dburl.indexOf(":", end + 1);
      			//dbname= dburl.substring(portend + 1, dburl.length());
      			dbname=dbtable.getDbid().getDbuser();
      		} else if (dbtype.equals("mysql")) {
      			int start = dburl.indexOf("//") + 2;
      			int end = dburl.indexOf(":", start);
      			ip = dburl.substring(start, end);
      			int portend = dburl.indexOf("/", end + 1);
                  dbname = dburl.substring(portend + 1);

      		} else if (dbtype.equals("mssqlserver")) {
      			int start = dburl.indexOf("//") + 2;
      			int end = dburl.indexOf(":", start);
      			ip = dburl.substring(start, end);
      			int portend = dburl.indexOf(";", end + 1);
      			int dbend = dburl.indexOf("=", portend + 1);
      			dbname = dburl.substring(dbend+1);
      		} else if (dbtype.equals("db2")) {
      			int start = dburl.indexOf("//") + 2;
      			int end = dburl.indexOf(":", start);
      			ip = dburl.substring(start, end);
      			int portend = dburl.indexOf("/", end + 1);
      			dbname = dburl.substring(portend + 1);
      		} else if (dbtype.equals("sybase")) {
      			int start = dburl.indexOf("Tds:") + 4;
      			int end = dburl.indexOf(":", start);
      			ip = dburl.substring(start, end);
      			int portend = dburl.indexOf("?", end + 1);
      			int dbstart = dburl.indexOf("=", portend + 1);
      			dbname = dburl.substring(dbstart + 1);
      		}
              }catch(Exception e)
              {
              	dbname="";
              	ip="";
              	//用户输入非法的参数
              }
              properties.put("dataSource", dbname);
              properties.put("url", ip);
              properties.put("docSource","database");
              for (int i = 0; i < fields.length; i++) {
                  if (strb.length() > 0)
                      strb.append(" ");
                  strb.append(value[i]);
                  for(com.xx.platform.domain.model.database.Tableproperty t : pdbtable.getTableproperty()){
                      if(fields[i]!=null&&t!=null&&fields[i].equals(t.getCode())){
                          properties.put(t.getIndexfield(),
                                         value[i]);
                          break;
                      }
                  }
              }
              break;
            }
          }
          FetcherDataPro fdp=new FetcherDataPro(webDbAdminTool,null,page,FetcherDataPro.DATABASEDATA);
          Map dataMap= new HashMap();
          dataMap.putAll(properties) ;
          fdp.dataPro(dataMap , true) ;
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
  }
  public PushFetcher(NutchFileSystem nfs, String directory) throws
      IOException {

    /**
     * 记录 文档数量
     */
    {     
      initIndexWriter(nfs, directory);
    }
  }

  /** Set the logging level. */
  public static void setLogLevel(Level level) {
    LOG.setLevel(level);
    PluginRepository.LOG.setLevel(level);
    ParserFactory.LOG.setLevel(level);
//        LOG.info("logging at " + level);
  }


  private void initIndexWriter(NutchFileSystem nfs, String directory) throws
      IOException {
    RuntimeDataCollect.setCrawl_start_time(new Date());
    pushIndexWriter = IndexFetcher.initIndexWriter(nfs, directory) ;


  }
//  public static File createSegment() {
//    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//    File[] fileList = new File(PushFilePath).listFiles();
//    File segmentFile = null;
//    if (fileList != null) {
//      for (File file : fileList) {
//        File contentFileData = new File(file.getAbsoluteFile() +
//                                        File.separator + "content",
//                                        "data");
//        if (contentFileData.length() > (long) (400 * 1000 * 1024)) {
//          continue;
//        }
//        else {
//          segmentFile = file;
//          break;
//        }
//      }
//    }
//    if (segmentFile == null)
//      segmentFile = new File(PushFilePath,
//                             format.format(new java.util.Date()));
//    if (!segmentFile.exists()) {
//      segmentFile.mkdirs();
//    }
//    return segmentFile;
//  }

    /**
     * pushData
     *
     * @param pd PushObject
     */
    public void pushData(PushObject pd) throws Exception {
        pushData(pd.getDocType(),pd.getTitle(),pd.getField(),pd.getValue(),pd.getUrl());
    }

}
