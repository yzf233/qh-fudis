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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
import com.xx.platform.core.nutch.FetchListTool;
import com.xx.platform.core.nutch.NetDirectoryFethListTool;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.core.nutch.WebDB;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.util.tools.ArraysObjectTool;
import com.xx.platform.util.tools.IndexMessage;
import com.xx.platform.util.tools.MD5;
import com.xx.platform.web.actions.system.ControlAction;

/**
 * The fetcher. Most of the work is done by plugins.
 * 
 * <p>
 * Note by John Xing: As of 20041022, option -noParsing is introduced. Without
 * this option, fetcher behaves the old way, i.e., it not only crawls but also
 * parses content. With option -noParsing, fetcher does crawl only. Use
 * ParseSegment.java to parse fetched contents. Check FetcherOutput.java and
 * ParseSegment.java for further description.
 */
public class FileFetcher {
//	private static int renum = 0; // 重复数据，测试用
//	public static final Logger LOG = LogFormatter
//			.getLogger("org.apache.nutch.fetcher.FileFetcher");
//	public static List<Map<String, String>> RMdatas = new ArrayList<Map<String, String>>();// 数据缓存，供节点采集。
//	static {
//		System.setProperty("sun.net.client.defaultConnectTimeout", "30000"); // 设置HTTP连接请求超时时间
//		System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 设置HTTP连接数据读取超时时间
//
//		if (NutchConf.get().getBoolean("fetcher.verbose", false)) {
//			setLogLevel(Level.FINE);
//		}
//	}
//
//	// private DirFile dirTool = new DirFile();
//	private WebDbAdminTool webDbAdminTool = new WebDbAdminTool();
//	private FetchListTool fetchList; // the input
//	// private static ArrayFile.Writer fetcherWriter; // the output
//	// private static ArrayFile.Writer contentWriter;
//	// private static ArrayFile.Writer parseTextWriter;
//	// private static ArrayFile.Writer parseDataWriter;
//	private static IndexWriter indexWriter;
//	// private static NFSDataOutputStream index; // 文档索引信息 ， 记录文档数量
//	// private static NFSDataInputStream indexReader;
//	private static String dictory;
//	private long start; // start time of fetcher run
//	private long bytes; // total bytes fetched
//	private static int pages; // total pages fetched
//	private int errors; // total pages errored
//
//	private static boolean parsing = true; // whether do parsing
//
//	private int threadCount = NutchConf.get().getInt(
//			"filefetcher.threads.fetch", 10);
//
//	private int filefetchercount = NutchConf.get().getInt(
//			"filefetcher.everytime.count", 500);
//
//	// All threads (FetcherThread or thread started by it) belong to
//	// group "fetcher". Each FetcherThread is named as "fetcherXX",
//	// where XX is the order it's started.
//	private static final String THREAD_GROUP_NAME = "fetcher";
//
//	private ThreadGroup group = new ThreadGroup(THREAD_GROUP_NAME); // our group
//
//	// count of FetcherThreads that are through the loop and just about to
//	// return
//	private int atCompletion = 0;
//
//	public static long paseDataNum = 0; // 记录 为开始爬取之前的 文档数量
//
//	// private static boolean isCloseWriter = false;
//	// private static boolean isOpenWriter = false;
//	private static boolean hasRegistHook = false;
//
//	private Page page = new Page("http://www.www.com", 1f, 0);
//
//	private static Thread hookThread;
//
//	/***************************************************************************
//	 * Fetcher thread
//	 **************************************************************************/
//	private class FetcherThread extends Thread {
//
//		public FetcherThread(String name) {
//			super(group, name);
//		}
//
//		private List<Map<String, String>> datas;
//		private NetDirectoryFethListTool tool = new NetDirectoryFethListTool();
//
//		/**
//		 * This thread keeps looping, grabbing an item off the list of URLs to
//		 * be fetched (in a thread-safe way). It checks whether the URL is OK to
//		 * download. If so, we do it.
//		 */
//		public void run() {
//			FetcherDataPro fdp=new FetcherDataPro(webDbAdminTool,indexWriter,page,FetcherDataPro.FILEDATA);
//			Map dataMap = null;
//			// boolean isFile = false;
//			{
//				try {
//					while (NutchCommand.isCrawl() && pages < filefetchercount
//							&& (dataMap = fetchList.next(dataMap)) != null) {
//						if (dataMap != null) {
//							long start = System.currentTimeMillis();
//							// isFile = true;
//							datas = tool.getData(dataMap);
//							String url1 = null;
//
//							if (dataMap.get(NetDirectoryFethListTool.SIGN_FILE) != null
//									&& dataMap.get(
//											NetDirectoryFethListTool.SIGN_FILE)
//											.toString().length() > 0) {
//								url1 = dataMap.get(
//										NetDirectoryFethListTool.SIGN_FILE)
//										.toString();
//
//							} else if (dataMap
//									.get(NetDirectoryFethListTool.NET_FILE) != null
//									&& dataMap.get(
//											NetDirectoryFethListTool.NET_FILE)
//											.toString().length() > 0) {
//								url1 = dataMap.get(
//										NetDirectoryFethListTool.NET_FILE)
//										.toString();
//							}
//							if (datas == null) {
//								ControlAction.addXdmessage("文件[" + url1
//										+ "]采集出错");
//								errors++;
//								continue;
//							}
//
//							if (datas.size() == 0) {
//								ControlAction.addXdmessage("文件[" + url1
//										+ "]已经被索引");
//								renum++;
//								continue;
//							}
//							if (RuntimeDataCollect.diserver != 0)// 如果启动分布式，则不在本地建立索引。
//							{
//								while (datas.size() != 0) {
//									RMdatas.add(datas.remove(0));
//								}
//								while (RMdatas.size() != 0)// 如果没有节点取数据，那么一直等待
//								{
//									try {
//										Thread.sleep(500);
//									} catch (InterruptedException e1) {
//									}
//								}
//							}
//							// System.out.println("文件解析LIST大小："+datas.size()+"   应该为一...............");
//							for (Map<String, String> map : datas) {
//								boolean isOk=fdp.dataPro(map,true);
//								if(isOk){
//									pages = pages + 1;
//								}else{
//									renum++;
//								}
//							}
//							double timeOfSearch1 = (System.currentTimeMillis() - start) / 1000.0;
//							ControlAction.addXdmessage("采集文件[" + url1
//									+ "]完毕，用时：" + timeOfSearch1 + "秒。");
//							continue;
//						}
//					}
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//			}
//
//			// Explicitly invoke shutDown() for all possible plugins.
//			// Done by the FetcherThread finished the last.
//			synchronized (FileFetcher.this) {
//				try {
//					PluginRepository.getInstance().finalize();
//				} catch (java.lang.Throwable t) {
//					// do nothing
//				}
//			}
//			return;
//		}
//	}
//
//
//	public static void setDictory() {
//		FileFetcher.dictory = null;
//	}
//
//	public FileFetcher(NutchFileSystem nfs) throws IOException {
//		this(nfs, dictory);
//	}
//
//	public FileFetcher(NutchFileSystem nfs, String dir, boolean parsing)
//			throws IOException {
//		this(nfs, dictory != null ? dictory : dir);
//	}
//
//	public FileFetcher(NutchFileSystem nfs, String directory)
//			throws IOException {
//		/**
//		 * 记录 文档数量
//		 */
//		if (!IndexMerger.isMergering) {
//			initIndexWriter(nfs, directory);
//			//			
//		}
//	}
//
//	/** Set thread count */
//	public void setThreadCount(int threadCount) {
//		this.threadCount = threadCount;
//	}
//
//	/** Set the logging level. */
//	public static void setLogLevel(Level level) {
//		LOG.setLevel(level);
//		PluginRepository.LOG.setLevel(level);
//		ParserFactory.LOG.setLevel(level);
//		// LOG.info("logging at " + level);
//	}
//
//	/** Runs the fetcher. */
//	public void run() throws IOException, InterruptedException {
//		fetchList = new FetchListTool(false);
//		start = System.currentTimeMillis();
//		int threadnum = threadCount;
//		for (int i = 0; i < threadnum
//				&& fetchList != null
//				&& ((fetchList.getDbList() != null && fetchList.getDbList()
//						.size() > 0) || fetchList.getPdfurl() != null
//						&& fetchList.getPdfurl().size() > 0); i++) { // spawn
//																		// threads
//			if (SearchContext.isShutDown) {
//				break;
//			}
//			FetcherThread thread = new FetcherThread(THREAD_GROUP_NAME + i);
//			thread.start();
//		}
//
//		// Quit monitoring if all FetcherThreads are gone.
//		// There could still be other threads, which may well be runaway threads
//		// started by external libs via FetcherThreads and it is generally safe
//		// to ignore them because our main FetcherThreads have finished their
//		// jobs.
//		// In fact we are a little more cautious here by making sure
//		// there is no more outstanding page fetches via monitoring
//		// changes of pages, errors and bytes.
//		int pages0 = pages;
//		int errors0 = errors;
//		long bytes0 = bytes;
//
//		while (true) {
//			Thread.sleep(1000);
//			int n = group.activeCount();
//			Thread[] list = new Thread[n];
//			group.enumerate(list);
//			RuntimeDataCollect.crawl_thread_num = n;
//			boolean noMoreFetcherThread = true; // assumption
//			for (int i = 0; i < n; i++) {
//				// this thread may have gone away in the meantime
//				if (list[i] == null)
//					continue;
//
//				String tname = list[i].getName();
//				if (tname.startsWith(THREAD_GROUP_NAME)) // prove it
//					noMoreFetcherThread = false;
//				if (LOG.isLoggable(Level.FINE))
//					LOG.fine(list[i].toString());
//			}
//			if (noMoreFetcherThread) {
//				if (LOG.isLoggable(Level.FINE))
//					if (pages == pages0 && errors == errors0 && bytes == bytes0)
//						break;
//				status();
//
//				pages0 = pages;
//				errors0 = errors;
//				bytes0 = bytes;
//				break;
//			}
//		}
//	}
//
//	public static class FetcherStatus {
//		private String name;
//		private long startTime, curTime;
//		private int pageCount, errorCount;
//		private long byteCount;
//
//		/**
//		 * FetcherStatus encapsulates a snapshot of the Fetcher progress status.
//		 * 
//		 * @param name
//		 *            short name of the segment being processed
//		 * @param start
//		 *            the time in millisec. this fetcher was started
//		 * @param pages
//		 *            number of pages fetched
//		 * @param errors
//		 *            number of fetching errors
//		 * @param bytes
//		 *            number of bytes fetched
//		 */
//		public FetcherStatus(String name, long start, int pages, int errors,
//				long bytes) {
//			this.name = name;
//			this.startTime = start;
//			this.curTime = System.currentTimeMillis();
//			this.pageCount = pages;
//			this.errorCount = errors;
//			this.byteCount = bytes;
//		}
//
//		public String getName() {
//			return name;
//		}
//
//		public long getStartTime() {
//			return startTime;
//		}
//
//		public long getCurTime() {
//			return curTime;
//		}
//
//		public long getElapsedTime() {
//			return curTime - startTime;
//		}
//
//		public int getPageCount() {
//			return pageCount;
//		}
//
//		public int getErrorCount() {
//			return errorCount;
//		}
//
//		public long getByteCount() {
//			return byteCount;
//		}
//
//		public String toString() {
//			if (RuntimeDataCollect.diserver != 0)// 如果启动分布式
//			{
//				ControlAction.addXdmessage("采集文件完毕。");
//				ControlAction.addXdmessage("   ");
//			} else
//				ControlAction.addXdmessage("采集到文件数据" + pageCount + "项，重复数据"
//						+ renum + "条，错误数据" + errorCount + "条");
//			return "运行数据: 索引块名称 " + name + ", " + pageCount + " 项, "
//					+ errorCount + " 错误项, " + byteCount + " bytes, "
//					+ (curTime - startTime) + " ms" + " 重复数据 " + renum + " 条";
//		}
//	}
//
//	public synchronized FetcherStatus getStatus() {
//		return new FetcherStatus("文件索引", start, pages, errors, bytes);
//	}
//
//	/** Display the status of the fetcher run. */
//	public synchronized void status() {
//		FetcherStatus status = getStatus();
//		LOG.info(status.toString());
//		LOG
//				.info("运行状态: "
//						+ (((float) status.getPageCount()) / (status
//								.getElapsedTime() / 1000.0f))
//						+ " 页/秒, "
//						+ (((float) status.getByteCount() * 8 / 1024) / (status
//								.getElapsedTime() / 1000.0f))
//						+ " kb/s, "
//						+ (((float) status.getByteCount()) / status
//								.getPageCount()) + " bytes/page");
//		renum = 0;
//		pages = 0;
//		errors = 0;
//	}
//
//	/** Run the fetcher. */
//	public static void main(String[] args) throws Exception {
//		int threadCount = -1;
//		String logLevel = "info";
//		boolean parsing = true;
//		boolean showThreadID = false;
//		String directory = null;
//
//		String usage = "Usage: Fetcher (-local | -ndfs <namenode:port>) [-logLevel level] [-noParsing] [-showThreadID] [-threads n] <dir>";
//
//		if (args.length == 0) {
//			System.err.println(usage);
//			System.exit(-1);
//		}
//
//		int i = 0;
//		NutchFileSystem nfs = NutchFileSystem.parseArgs(args, i);
//		a.a(false);
//		for (; i < args.length; i++) { // parse command line
//			if (args[i] == null) {
//				continue;
//			} else if (args[i].equals("-threads")) { // found -threads option
//				threadCount = Integer.parseInt(args[++i]);
//			} else if (args[i].equals("-logLevel")) {
//				logLevel = args[++i];
//			} else if (args[i].equals("-noParsing")) {
//				parsing = false;
//			} else if (args[i].equals("-showThreadID")) {
//				showThreadID = true;
//			} else
//				// root is required parameter
//				directory = args[i];
//		}
//		/**
//		 * 初始化索引模块
//		 * 
//		 * @param <any>
//		 *            nfs
//		 */
//
//		FileFetcher fetcher = new FileFetcher(nfs, directory, parsing); // make
//		// a
//		// Fetcher
//		if (threadCount != -1) { // set threadCount option
//			fetcher.setThreadCount(threadCount);
//		}
//
//		// set log level
//		setLogLevel(Level.parse(logLevel.toUpperCase()));
//
//		if (showThreadID) {
//			LogFormatter.setShowThreadIDs(showThreadID);
//		}
//
//		try {
//			// 设置 运行时爬虫状态为 Running
//			RuntimeDataCollect
//					.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
//			fetcher.run(); // run the Fetcher
//		} finally {
//			nfs.close();
//		}
//
//	}
//
//	private static void initIndexWriter(NutchFileSystem nfs, String directory)
//			throws IOException {
//		RuntimeDataCollect.setCrawl_start_time(new Date());
//		indexWriter = IndexFetcher.initIndexWriter(nfs, directory);
//
//	}
}
