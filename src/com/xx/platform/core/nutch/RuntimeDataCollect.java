package com.xx.platform.core.nutch;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.lucene.search.IndexSearcher;
import org.apache.nutch.ipc.RPC;

import com.sleepycat.je.DatabaseException;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.BerkeleyDB;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.util.tools.ArraysObjectTool;
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
public class RuntimeDataCollect {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private static String crawl_status = NutchCommand.CRAWL_STATUS_NOT_RUNNING; //爬虫状态 Running Not Running Idle
    public static boolean crawl_status_restore_data = false; //是否修复数据
    public static long crawl_restore_num = 0; //需要修复的中数据量
    public static long crawl_restored_num = 0; //已修复的数据

    private static String search_status = "Running";  //搜索服务器状态 Running Not Running Idle
    private static boolean search = NutchCommand.getSearch() ;
    private static int    search_num =0;     //搜索请求数
    private static float  return_time = 0.0f;    //总响应时间
    private static String  eval_return_time = "0" ;//平均响应时间
    private static int    server_num =1;     //检索服务器数量
    private static int    segments =0;       //索引数据块数量
    private static int    index_rec_num =0 ;  //索引记录数量
    private static long   index_file_size =0;//索引文件尺寸
    private static String   index_file_size_string = "0";//索引文件尺寸
    private static final long   start_run_time = System.currentTimeMillis(); //服务器运行时间
    public static int thread_num = SearchContext.getXdtechsite().getCthreads() ;   //爬虫运行线程数
    public static int diserver =  (SearchContext.getXdtechsite().getSudis()!=null && SearchContext.getXdtechsite().getSudis())?1:0 ;   //分布式状态
    
    private static boolean synchroIsRunning=false;//集群状态
    
    public static int crawl_thread_num = 0 ;
    public static int crawl_depth = 1 ;    //爬虫爬行深度
    public static boolean is_update = false ;  //是否处理网页更新 ， 用户表中存放相关信息
    private static long  update_times = 1000 * 60 * 60 *24 *7 ;//默认的数据更新周期
    private static long crawl_page_num = 0 ;   //爬虫获得页面数据数目
    private static long fileCrawl_page_num = 0 ;   //file爬虫获得页面数据数目
    private static long crawl_times =0;        //爬虫运行时间
    private static long crawl_parse_num = 0 ;  //爬虫解析数据数量
    private static long process_num = 0 ;      //处理页面数量
    private static long fileProcess_num = 0 ;      //处理页面数量
    private static int crawl_speed = 0 ; // 爬虫爬行速度
    private static int fileCrawl_speed = 0 ; // 爬虫爬行速度
    private static Date crawl_start_time ;    //爬虫开始运行时间
    private static Date crawl_database_start_time ;    //数据库开始运行时间
    private static Date crawl_file_start_time ;    //文件开始运行时间
    private static String index_file_dir = SearchContext.search_dir;
    public static boolean has_new_index = false ; //新的索引合并完毕
    private static final String   start_run_time_string = timeFormat.format(new Date()); //服务器运行时间
    {
    	IndexSearcher reader = null ;
        try {
            File file = new File(SearchContext.search_dir+ File.separator + "index") ;
            if(file.exists())
            {
            	reader = new IndexSearcher(SearchContext.
                        search_dir + File.separator + "index");
            	if(reader!=null && reader.getIndexReader()!=null)
            		index_rec_num = reader.getIndexReader().numDocs();
                
            }
        }catch(Exception ex){}
        finally{
        	try{
        		reader.close();
        	}catch(Exception ex){}
        }
        try {
            File file = new File(SearchContext.search_dir,"index") ;
            if(file.exists())
            {
                index_file_size = getSize(file);
                if (index_file_size >= 1024 && index_file_size < (1024 * 1024)) {
                    index_file_size_string = String.valueOf(index_file_size /
                            (1024)) + "K";
                } else if (index_file_size >= (1024 * 1024) &&
                           index_file_size < (1024 * 1024 * 1024)) {
                    index_file_size_string = String.valueOf(index_file_size /
                            (1024 * 1024)) + "M";
                } else if (index_file_size >= (1024 * 1024 * 1024)) {
                    index_file_size_string = String.valueOf(index_file_size /
                            (1024 * 1024 * 1024)) + "G";
                } else {
                    index_file_size_string = String.valueOf(index_file_size) +
                                             "B";
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    public static String getCrawl_status() {
        return crawl_status;
    }

    public static String getEval_return_time() {
        return eval_return_time;
    }

    public static long getIndex_file_size() {
        return index_file_size;
    }

    public static int getIndex_rec_num() {
        try {
			return (index_rec_num-(int)(BerkeleyDB.getDelDocDB().count()))<0?0:(index_rec_num-(int)(BerkeleyDB.getDelDocDB().count()));
		} catch (DatabaseException e) {
			return index_rec_num;
		}
    }

    public static float getReturn_time() {
        return return_time;
    }

    public static int getSearch_num() {
        return search_num;
    }

    public static int getSegments() {
        return segments;
    }

    public static String getSearch_status() {
        return search_status;
    }

    public static int getServer_num() {
        return server_num;
    }
    
    public static void setCrawl_status(String crawl_status) {
    	setCrawl_status(crawl_status,true);
    }
    public static void setCrawl_status(String crawl_status,boolean command) {
//    	if(command){
//			List<Synchro> synchro = SearchContext.getSynchroList();
//			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
//					&& synchro.size() > 0)
//				for (Synchro s : synchro) {// 遍历每个节点
//					try {
//						((ImInterface) RPC.getProxy(ImInterface.class,
//								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
//								.setCrawl_status(ArraysObjectTool.ObjectToArrays(crawl_status));
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//		}
        RuntimeDataCollect.crawl_status = crawl_status;
    }

    public static void setServer_num(int server_num) {
        RuntimeDataCollect.server_num = server_num;
    }

    public static void setSegments(int segments) {
        RuntimeDataCollect.segments = segments;
    }

    public static void setSearch_status(String search_status) {
        RuntimeDataCollect.search_status = search_status;
    }

    public static void setSearch_num(int search_num) {
        RuntimeDataCollect.search_num += search_num;
    }

    public static void setReturn_time(float return_time) {
        RuntimeDataCollect.return_time += return_time;
    }

    public static void setIndex_rec_num(int index_rec_num) {
        RuntimeDataCollect.index_rec_num = index_rec_num;
    }

    public static void setIndex_file_size(long index_file_size) {
        RuntimeDataCollect.index_file_size = index_file_size;
        if (index_file_size >= 1024 && index_file_size < (1024 * 1024)) {
            index_file_size_string = String.valueOf(index_file_size /
                    (1024)) + "K";
        } else if (index_file_size >= (1024 * 1024) &&
                   index_file_size < (1024 * 1024 * 1024)) {
            index_file_size_string = String.valueOf(index_file_size /
                    (1024 * 1024)) + "M";
        } else if (index_file_size >= (1024 * 1024 * 1024)) {
            index_file_size_string = String.valueOf(index_file_size /
                    (1024 * 1024 * 1024)) + "G";
        } else {
            index_file_size_string = String.valueOf(index_file_size) +
                                     "B";
        }
    }

    public static void setEval_return_time(String eval_return_time) {
        RuntimeDataCollect.eval_return_time = eval_return_time;
    }
    public static void setIndex_file_dir(String index_file_dir) {
            RuntimeDataCollect.index_file_dir = index_file_dir ;
    }
    public static String getStart_run_time_string() {
            return RuntimeDataCollect.start_run_time_string;
    }
    public static String getIndex_file_size_string() {
            return RuntimeDataCollect.index_file_size_string;
    }
    public static String getIndex_file_dir() {
            return RuntimeDataCollect.index_file_dir;
    }

    public static long getSize(Object path) {
        if (path == null)
            return 0;
        File file = (path instanceof String) ? new File((String) path) :
                    (File) path;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            long sum = 0;
            for (int i = 0; i < files.length; ++i)
                sum += files[i].isDirectory() ? getSize(files[i]) :
                        files[i].length();
            return sum;
        } else
            return file.length();
    }
//    private static long crawl_page_num = 0 ;   //爬虫获得页面数据数目
//    private static long crawl_times =0;        //爬虫运行时间
//    private static long crawl_parse_num = 0 ;  //爬虫解析数据数量
//    private static float crawl_speed = 0.0f ; // 爬虫爬行速度
//    private static Date crawl_start_time ;    //爬虫开始运行时间
    public static long getCrawl_page_num()
    {
        return crawl_page_num ;
    }
    public static long getFileCrawl_page_num()
    {
        return fileCrawl_page_num ;
    }

    public static long getCrawl_times()
    {
        return crawl_times ;
    }
    public static long getCrawl_parse_num()
    {
        return crawl_parse_num ;
    }
    public static int getCrawl_speed()
    {
        return crawl_speed ;
    }
    public static int getFileCrawl_speed()
   {
       return fileCrawl_speed ;
   }

    public static int getCrawl_thread_num()
    {
        return crawl_thread_num ;
    }
    public static boolean getSearch()
    {
        return search ;
    }
    public static int getDiserver()
    {
      return diserver;
    }
    public static Date getCrawl_start_time()
    {
        if(crawl_start_time==null)
        {
            crawl_start_time = new Date() ;
        }
        return crawl_start_time ;
    }

    public static long getProcess_num() {
        return process_num;
    }
    public static long getFileProcess_num() {
        return fileProcess_num;
    }


    public static long getCrawl_restore_num() {
        return crawl_restore_num;
    }

    public static long getCrawl_restored_num() {
        return crawl_restored_num;
    }

    public static boolean getCrawl_status_restore_data() {
        return crawl_status_restore_data;
    }

    public static void setCrawl_page_num(long crawl_page_num) {
        if(crawl_page_num==0)
        {
            RuntimeDataCollect.crawl_page_num = 0  ;
            RuntimeDataCollect.process_num = 0 ;
        }else
        {
            RuntimeDataCollect.crawl_page_num += crawl_page_num;
        }
    }
    public static void changeCrawl_page_num(long crawl_page_num) {
            RuntimeDataCollect.crawl_page_num = crawl_page_num;
    }
    public static void setFileCrawl_page_num(long crawl_page_num) {
        if(crawl_page_num==0)
        {
            RuntimeDataCollect.fileCrawl_page_num = 0  ;
            RuntimeDataCollect.fileProcess_num = 0 ;
        }else
        {
            RuntimeDataCollect.fileCrawl_page_num += crawl_page_num;
        }
    }
    public static void changeFileCrawl_page_num(long crawl_page_num) {
            RuntimeDataCollect.fileCrawl_page_num = crawl_page_num;
    }

    public static void setCrawl_times(long crawl_times) {
        RuntimeDataCollect.crawl_times = crawl_times;
    }

    public static void setCrawl_parse_num(long crawl_speed) {
        RuntimeDataCollect.crawl_speed += crawl_speed;
    }

    public static void setCrawl_speed(int crawl_speed) {
        RuntimeDataCollect.crawl_speed = crawl_speed;
    }
    public static void setFileCrawl_speed(int crawl_speed) {
        RuntimeDataCollect.fileCrawl_speed = crawl_speed;
    }


    public static void setCrawl_start_time(Date crawl_start_time) {
        RuntimeDataCollect.crawl_start_time = crawl_start_time;
    }
    public static void setSearch(boolean search) {
        if(search)
        {
            search_status = "Running" ;
        }else
        {
            search_status = "Not Running" ;
        }
        RuntimeDataCollect.search = search;
        NutchCommand.setSearch(search);
    }

    public static void setProcess_num(long process_num) {
        RuntimeDataCollect.process_num = process_num;
    }
    public static void setFileProcess_num(long process_num) {
        RuntimeDataCollect.fileProcess_num = process_num;
    }

    public static void setDiserver(int diserver)
    {
      RuntimeDataCollect.diserver = diserver ;
    }

	public static Date getCrawl_database_start_time() {
		return crawl_database_start_time;
	}

	public static void setCrawl_database_start_time(Date crawl_database_start_time) {
		RuntimeDataCollect.crawl_database_start_time = crawl_database_start_time;
	}

	public static Date getCrawl_file_start_time() {
		return crawl_file_start_time;
	}

	public static void setCrawl_file_start_time(Date crawl_file_start_time) {
		RuntimeDataCollect.crawl_file_start_time = crawl_file_start_time;
	}

	public static boolean isSynchroIsRunning() {
		RuntimeDataCollect.synchroIsRunning =ImDistributedTool.isRuning;
		return synchroIsRunning;
	}

	public static void setSynchroIsRunning(boolean synchroIsRunning) {
		RuntimeDataCollect.synchroIsRunning = synchroIsRunning;
		ImDistributedTool.isRuning=RuntimeDataCollect.synchroIsRunning;
	}

}
