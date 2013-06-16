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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.index.IndexWriter;
import org.apache.nutch.db.Page;
import org.apache.nutch.fs.NutchFileSystem;
import org.apache.nutch.indexer.IndexMerger;
import org.apache.nutch.io.MD5Hash;
import org.apache.nutch.ipc.RPC;
import org.apache.nutch.pagedb.FetchListEntry;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseStatus;
import org.apache.nutch.parse.ParseText;
import org.apache.nutch.parse.ParserFactory;
import org.apache.nutch.plugin.PluginRepository;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.protocol.ProtocolStatus;
import org.apache.nutch.util.LogFormatter;
import org.apache.nutch.util.NutchConf;

import com.xdtech.platform.util.tools.a;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.nutch.DirectoryFethListTool;
import com.xx.platform.core.nutch.FetchListTool;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.core.nutch.WebDB;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.domain.model.database.Dbtable;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.util.tools.ArraysObjectTool;
import com.xx.platform.util.tools.MD5;
import com.xx.platform.web.actions.system.ControlAction;

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
public class Fetcher {
  private static int renum = 0; //重复数据，测试用
  public static final Logger LOG =
      LogFormatter.getLogger("org.apache.nutch.fetcher.Fetcher");
 
  static {
    System.setProperty("sun.net.client.defaultConnectTimeout", "30000"); //设置HTTP连接请求超时时间
    System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //设置HTTP连接数据读取超时时间

    if (NutchConf.get().getBoolean("fetcher.verbose", false)) {
      setLogLevel(Level.FINE);
    }
  }

//  private DirFile dirTool = new DirFile();
  private WebDbAdminTool webDbAdminTool = new WebDbAdminTool();
  private FetchListTool fetchList; // the input
//  private static ArrayFile.Writer fetcherWriter; // the output
//  private static ArrayFile.Writer contentWriter;
//  private static ArrayFile.Writer parseTextWriter;
//  private static ArrayFile.Writer parseDataWriter;
//  private static NFSDataOutputStream index; //文档索引信息 ， 记录文档数量
//  private static NFSDataInputStream indexReader;
  private static String dictory;
  private long start; // start time of fetcher run
  private long bytes; // total bytes fetched
  private int pages; // total pages fetched
  private int errors; // total pages errored
  private static Date beginTime=new Date() ;
  private static boolean parsing = true; // whether do parsing

  private int threadCount = // max number of threads
      NutchConf.get().getInt("fetcher.threads.fetch", 10);

  private static final float NEW_INJECTED_PAGE_SCORE =
      NutchConf.get().getFloat("db.score.injected", 2.0f);

  private static final int MAX_REDIRECT =
      NutchConf.get().getInt("http.redirect.max", 3);

  // All threads (FetcherThread or thread started by it) belong to
  // group "fetcher". Each FetcherThread is named as "fetcherXX",
  // where XX is the order it's started.
  private static final String THREAD_GROUP_NAME = "fetcher";
  

  private ThreadGroup group = new ThreadGroup(THREAD_GROUP_NAME); // our group

  // count of FetcherThreads that are through the loop and just about to return
  private int atCompletion = 0;

  public static long paseDataNum = 0; //记录 为开始爬取之前的 文档数量

//  private static boolean isCloseWriter = false;
//  private static boolean isOpenWriter = true;
  private static boolean hasRegistHook = false;

  private Page page = new Page("http://www.www.com", 1f, 0);

  private static Thread hookThreadc;
  private static Thread savaDataHookThread;
  
  /********************************************
   * Fetcher thread
   ********************************************/
  private class FetcherThread
      extends Thread {

    public FetcherThread(String name) {
      super(group, name);
    }

    /**
     * This thread keeps looping, grabbing an item off the list
     * of URLs to be fetched (in a thread-safe way).  It checks
     * whether the URL is OK to download.  If so, we do it.
     */
    public void run() {
      Map<String,String> dataMap = null;
      FetcherDataPro fdp=new FetcherDataPro(webDbAdminTool,null,page,FetcherDataPro.DATABASEDATA);
      {
        try {
          while (!SearchContext.isShutDown && (dataMap = fetchList.next(dataMap)) != null) {
    		boolean isOk=fdp.dataPro(dataMap,true);
            if (isOk) {
              pages = pages + 1;
            }else {
              renum++;
            }
          }
        }catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      // Explicitly invoke shutDown() for all possible plugins.
      // Done by the FetcherThread finished the last.
      synchronized (Fetcher.this) {
        atCompletion++;
        if (atCompletion == threadCount) {
          try {
            PluginRepository.getInstance().finalize();
          }
          catch (java.lang.Throwable t) {
            // do nothing
          }
        }
      }
      return;
    }
    
  }


//  /**
//   * 合并索引内容
//   */
//  public void merginData() {
//    try {
//      Thread.sleep(5000);
//    }
//    catch (InterruptedException ex) {
//      ex.printStackTrace();
//    }
//    if (!TimerTask.crawl_ing && isOpenWriter) {
//      Thread merginThread = new Thread(new java.lang.Runnable() {
//        public void run() {
//          if (!NutchCommand.isCrawl()) {
//            TimerTask.crawl_ing = true;
//            closeWriter(); //关闭索引 然后开始合并索引 同时保持合并索引的锁定 避免同时合并索引
//            try {
//              IndexMerger.main(new String[] {SearchContext.search_dir +
//                               File.separator + "index",
//                               SearchContext.search_dir +
//                               File.separator + "segments" + File.separator +
//                               name});
//              RuntimeDataCollect.has_new_index = true;
//            }
//            catch (Exception ex) {
//              ex.printStackTrace();
//            }
//            finally {
//              TimerTask.crawl_ing = false;
//            }
//          }
//        }
//      });
//      merginThread.run();
//    }
//  }

  public static void setDictory() {
    Fetcher.dictory = null;
  }

  public Fetcher(NutchFileSystem nfs) throws
      IOException {
    this(nfs,
         dictory != null ? dictory :(dictory = SearchContext.search_dir+File.separator+"index"));
  }

  public Fetcher(NutchFileSystem nfs, String dir, boolean parsing) throws
      IOException {
    this(nfs, dictory != null ? dictory : dir);
    if (savaDataHookThread != null) {
		Runtime.getRuntime().removeShutdownHook(savaDataHookThread);
		// hasRegistHook = false ;
	}
    if (savaDataHookThread == null) {
	    //jvm 结束时保存数据
	    savaDataHookThread = new Thread(new Runnable() {
			public void run() {
				SearchContext.isShutDown = true;
			    if(fetchList!=null && fetchList.getDbList()!=null && fetchList.getDbList().size()>0){
			    	saveData();
			    }
			    while (true) {
			        try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
	//					e.printStackTrace();
					}
			        int n = group.activeCount();
			        Thread[] list = new Thread[n];
			        group.enumerate(list);
			        RuntimeDataCollect.crawl_thread_num = n;
			        boolean noMoreFetcherThread = true; // assumption
			        for (int i = 0; i < n; i++) {
			          // this thread may have gone away in the meantime
			          if (list[i] == null)
			            continue;
	
			          String tname = list[i].getName();
			          if (tname.startsWith(THREAD_GROUP_NAME)) // prove it
			            noMoreFetcherThread = false;
			          if (LOG.isLoggable(Level.FINE))
			            LOG.fine(list[i].toString());
			        }
			        if (noMoreFetcherThread) {
			        	SearchContext.isFethcerShutDown = true ;
			        	break;
			        }
			}
			}
	    });
    }
    Runtime.getRuntime().addShutdownHook(savaDataHookThread);
  }

  public Fetcher(NutchFileSystem nfs, String directory) throws
      IOException {

    /**
     * 记录 文档数量
     */
    if (!IndexMerger.isMergering) {

      initIndexWriter(nfs, directory);
    }
  }

  /** Set thread count */
  public void setThreadCount(int threadCount) {
    this.threadCount = threadCount;
  }

  /** Set the logging level. */
  public static void setLogLevel(Level level) {
    LOG.setLevel(level);
    PluginRepository.LOG.setLevel(level);
    ParserFactory.LOG.setLevel(level);
//        LOG.info("logging at " + level);
  }

  /** Runs the fetcher. */
  public void run() throws IOException, InterruptedException {
    fetchList = new FetchListTool(this);
//    synchronized(fetchList){
//        try{
//            wait();
//        }catch(InterruptedException ie){}
//    }
 
    start = System.currentTimeMillis();
    for (int i = 0;
         i < threadCount && fetchList != null ; i++) { // spawn threads
    	if(SearchContext.isShutDown){
//    		saveData();
			break;
		}
      FetcherThread thread = new FetcherThread(THREAD_GROUP_NAME + i);
      thread.start();
      SearchContext.isFethcerShutDown = false ;
    }
    

    // Quit monitoring if all FetcherThreads are gone.
    // There could still be other threads, which may well be runaway threads
    // started by external libs via FetcherThreads and it is generally safe
    // to ignore them because our main FetcherThreads have finished their jobs.
    // In fact we are a little more cautious here by making sure
    // there is no more outstanding page fetches via monitoring
    // changes of pages, errors and bytes.
    int pages0 = pages;
    int errors0 = errors;
    long bytes0 = bytes;
    beginTime=new Date();
    while (true) {
      Thread.sleep(1000);
      int n = group.activeCount();
      Thread[] list = new Thread[n];
      group.enumerate(list);
      RuntimeDataCollect.crawl_thread_num = n;
      boolean noMoreFetcherThread = true; // assumption
      for (int i = 0; i < n; i++) {
        // this thread may have gone away in the meantime
        if (list[i] == null)
          continue;

        String tname = list[i].getName();
        if (tname.startsWith(THREAD_GROUP_NAME)) // prove it
          noMoreFetcherThread = false;
        if (LOG.isLoggable(Level.FINE))
          LOG.fine(list[i].toString());
      }
      if (noMoreFetcherThread) {    	
        if (LOG.isLoggable(Level.FINE))
          if (pages == pages0 && errors == errors0 && bytes == bytes0)
            break;
        status();

        pages0 = pages;
        errors0 = errors;
        bytes0 = bytes;
        break;
      }
      
    }
    SearchContext.isFethcerShutDown = true;
    //fethcer结束，关闭钩子
    if (!SearchContext.isShutDown&&savaDataHookThread != null) {
		Runtime.getRuntime().removeShutdownHook(savaDataHookThread);
		// hasRegistHook = false ;
	}
//    closeWriter();
  }

  /** Runs the fetcher. */
 public void run_1(FetchListTool ft) throws IOException, InterruptedException {
   fetchList = ft;
   beginTime = new Date();  //每次建索引之前 ，起始时间恢复  不准确.....
//    synchronized(fetchList){
//        try{
//            wait();
//        }catch(InterruptedException ie){}
//    }
   start = System.currentTimeMillis();
   for (int i = 0;
        i < threadCount && fetchList != null; i++) { // spawn threads
     FetcherThread thread = new FetcherThread(THREAD_GROUP_NAME + i);
     thread.start();
   }

   // Quit monitoring if all FetcherThreads are gone.
   // There could still be other threads, which may well be runaway threads
   // started by external libs via FetcherThreads and it is generally safe
   // to ignore them because our main FetcherThreads have finished their jobs.
   // In fact we are a little more cautious here by making sure
   // there is no more outstanding page fetches via monitoring
   // changes of pages, errors and bytes.
   int pages0 = pages;
   int errors0 = errors;
   long bytes0 = bytes;

   while (true) {
     Thread.sleep(1000);
     int n = group.activeCount();
     Thread[] list = new Thread[n];
     group.enumerate(list);
     RuntimeDataCollect.crawl_thread_num = n;
     boolean noMoreFetcherThread = true; // assumption
     for (int i = 0; i < n; i++) {
       // this thread may have gone away in the meantime
       if (list[i] == null)
         continue;

       String tname = list[i].getName();
       if (tname.startsWith(THREAD_GROUP_NAME)) // prove it
         noMoreFetcherThread = false;
       if (LOG.isLoggable(Level.FINE))
         LOG.fine(list[i].toString());
     }
     if (noMoreFetcherThread) {
       if (LOG.isLoggable(Level.FINE))
         if (pages == pages0 && errors == errors0 && bytes == bytes0)
           break;
       status();

       start=pages=errors=0;bytes=0;
       pages0 = pages;
       errors0 = errors;
       bytes0 = bytes;
       break;
     }
   }
   //fethcer结束，关闭钩子
   if (!SearchContext.isShutDown&&savaDataHookThread != null) {
		Runtime.getRuntime().removeShutdownHook(savaDataHookThread);
		// hasRegistHook = false ;
	}
 }


  public static class FetcherStatus {
    private String name;
    private long startTime, curTime;
    private int pageCount, errorCount;
    private long byteCount;

    /**
     * FetcherStatus encapsulates a snapshot of the Fetcher progress status.
     * @param name short name of the segment being processed
     * @param start the time in millisec. this fetcher was started
     * @param pages number of pages fetched
     * @param errors number of fetching errors
     * @param bytes number of bytes fetched
     */
    public FetcherStatus(String name, long start, int pages, int errors,
                         long bytes) {
      this.name = name;
      this.startTime = start;
      this.curTime = System.currentTimeMillis();
      this.pageCount = pages;
      this.errorCount = errors;
      this.byteCount = bytes;
    }

    public String getName() {
      return name;
    }

    public long getStartTime() {
      return startTime;
    }

    public long getCurTime() {
      return curTime;
    }

    public long getElapsedTime() {
      return curTime - startTime;
    }

    public int getPageCount() {
      return pageCount;
    }

    public int getErrorCount() {
      return errorCount;
    }

    public long getByteCount() {
      return byteCount;
    }

    public String toString() {
    String str= "运行数据: 索引块名称 " + name + ", "
          + pageCount + " 项, "
          + errorCount + " 错误项, "
          + byteCount + " bytes, "
          + (curTime - startTime) + " ms" + " 重复数据 " + renum + " 条";
    Date endTime = new Date();
    double timeOfSearch1 = (endTime.getTime() - beginTime.getTime())/1000.0;
 ControlAction.addXdmessage("采集到数据库数据"+ pageCount + "项，建立索引"+pageCount+"条，重复数据"+renum+ "条,用时约："+timeOfSearch1+"秒");
 ControlAction.addXdmessage("  ");
      renum=0;//重复数据清零
      return str;
    }
  }

//  public static void closeWriter() {
//    try {
//      {
////    fetchList.close();                            // close databases
////        fetcherWriter.close();
////        contentWriter.close();
////        if (index != null) {
////          index.writeLong(IndexFetcher.docNum);
////          index.close();
////        }
////        if (parsing) {
////          parseTextWriter.close();
////          parseDataWriter.close();
////        }
////        { //优化索引，然后关闭
////          if (indexWriter != null) {
////            indexWriter.optimize();
////            indexWriter.close();
////            File doneFile = new File(srcDir, "index.done");
////            if (!doneFile.exists()) {
////              doneFile.createNewFile();
////            }
////          }
////          FileUtil.fullyDelete(localWorkingDir);
////        }
//        isCloseWriter = true;
//        isOpenWriter = false;
//      }
//    }
//    catch (Exception ex) {
//      System.out.println("关闭索引放生错误【"+ex.getMessage()+"】");
//    }
//
//  }

  public synchronized FetcherStatus getStatus() {
    return new FetcherStatus("数据库索引", start, pages, errors, bytes);
  }

  /** Display the status of the fetcher run. */
  public synchronized void status() {
    FetcherStatus status = getStatus();
    LOG.info(status.toString());
    LOG.info("运行状态: "
             +
             ( ( (float) status.getPageCount()) /
              (status.getElapsedTime() / 1000.0f)) +
             " 页/秒, "
             +
             ( ( (float) status.getByteCount() * 8 / 1024) /
              (status.getElapsedTime() / 1000.0f)) + " kb/s, "
             + ( ( (float) status.getByteCount()) / status.getPageCount()) +
             " bytes/page");
    renum = 0;
  }

  /** Run the fetcher. */
  public static void main(String[] args) throws Exception {
    int threadCount = -1;
    String logLevel = "info";
    boolean parsing = true;
    boolean showThreadID = false;
    String directory = null;

    String usage = "Usage: Fetcher (-local | -ndfs <namenode:port>) [-logLevel level] [-noParsing] [-showThreadID] [-threads n] <dir>";

    if (args.length == 0) {
      System.err.println(usage);
      System.exit( -1);
    }

    int i = 0;
    NutchFileSystem nfs = NutchFileSystem.parseArgs(args, i);
    a.a(false);
    for (; i < args.length; i++) { // parse command line
      if (args[i] == null) {
        continue;
      }
      else if (args[i].equals("-threads")) { // found -threads option
        threadCount = Integer.parseInt(args[++i]);
      } 
      else if (args[i].equals("-logLevel")) {
        logLevel = args[++i];
      }
      else if (args[i].equals("-noParsing")) {
        parsing = false;
      }
      else if (args[i].equals("-showThreadID")) {
        showThreadID = true;
      }
      else // root is required parameter
        directory = args[i];
    }
    /**
     * 初始化索引模块
     * @param <any> nfs
     */

    Fetcher fetcher = new Fetcher(nfs, directory, parsing); // make a Fetcher
    if (threadCount != -1) { // set threadCount option
      fetcher.setThreadCount(threadCount);
    }

    // set log level
    setLogLevel(Level.parse(logLevel.toUpperCase()));

    if (showThreadID) {
      LogFormatter.setShowThreadIDs(showThreadID);
    }

    try {
      //设置 运行时爬虫状态为 Running
      RuntimeDataCollect.setCrawl_status(NutchCommand.
                                         CRAWL_STATUS_RUNNING);
      fetcher.run(); // run the Fetcher
    }
    finally {
      nfs.close();
    }

  }
  public static File localWorkingDir;
  private static void initIndexWriter(NutchFileSystem nfs, String directory) throws
      IOException {
    RuntimeDataCollect.setCrawl_start_time(new Date());
    IndexFetcher.initIndexWriter(nfs, directory) ;
  }
  
  private void saveData(){
	  LOG.info("共需回写"+fetchList.getDbList().size()+"条缓存数据！");
	  try {
		File file = new File(FetchListTool.DATABASE_TEMP_FILE_PATH);		
		if(!file.exists()){
			try{
				file.mkdir();	
				file = new File(file,FetchListTool.DATABASE_TEMP_FILE_NAME);
			}catch(Exception e){
				LOG.info("保存数据库临时数据出错："+e.getMessage()) ;
			}
		}else{
			file = new File(file,FetchListTool.DATABASE_TEMP_FILE_NAME);
			if(file.exists()){
				file.delete();
			}
		}   
		FetchListTool.creatSFile(fetchList.getDbList(),file);

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  LOG.info("回写缓存数据完成！");
  }
  
//  private FetchListTool readSFile(File file){
//	  FetchListTool tetchListTool = null;
//      try {
//          System.out.print("read file now-------->filename:" +
//                             file.getAbsoluteFile());
//          fileInput = new FileInputStream(file);
//          objInput = new ObjectInputStream(fileInput);
//          map = (Object) objInput.readObject();
//          objInput.close();
//          System.out.println("       succeed!");
//      } catch (Exception e) {
//          System.out.println("read file(" + file.getAbsoluteFile() +
//                             ") faild");
//          e.printStackTrace();
//      }
//      return map;
//	  
//  }
//  
  
}
