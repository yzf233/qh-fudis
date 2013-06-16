package org.apache.nutch.fetcher;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.nutch.analysis.NutchDocumentAnalyzer;
import org.apache.nutch.fs.FileUtil;
import org.apache.nutch.fs.NutchFileSystem;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilters;
import org.apache.nutch.indexer.NutchSimilarity;
import org.apache.nutch.ipc.RPC;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseText;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.util.LogFormatter;
import org.apache.nutch.util.NutchConf;

import com.sleepycat.je.DatabaseException;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.BerkeleyDB;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.util.tools.ArraysObjectTool;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class IndexFetcher {
	public static long docNum = 0;
	public static final String DONE_NAME = "index.done";
	public static final Logger LOG = LogFormatter
			.getLogger("org.apache.nutch.index.IndexSegment");
	
	private static final java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(
			"yyyy-MM-dd");
	public static int LOG_STEP = 20000;

	private static final int maxFieldLength = NutchConf.get().getInt(
			"indexer.max.tokens", 10000);

	private static final int MERGE_FACTOR = NutchConf.get().getInt(
			"indexer.mergeFactor", LogDocMergePolicy.DEFAULT_MERGE_FACTOR);

	private static final int MIN_MERGE_DOCS = NutchConf.get().getInt(
			"indexer.minMergeDocs", LogDocMergePolicy.DEFAULT_MIN_MERGE_DOCS);

	private static final int MAX_MERGE_DOCS = NutchConf.get().getInt(
			"indexer.maxMergeDocs", LogDocMergePolicy.DEFAULT_MAX_MERGE_DOCS);

	private static final int TERM_INDEX_INTERVAL = NutchConf.get().getInt(
			"indexer.termIndexInterval",
			IndexWriter.DEFAULT_TERM_INDEX_INTERVAL);
	
	private static final int RAM_MERGE_DOCS = NutchConf.get().getInt(
			"default.rammergedocs.limit",2048);

	private static final boolean boostByLinkCount = NutchConf.get().getBoolean(
			"indexer.boost.by.link.count", false);

	private static final float scorePower = NutchConf.get().getFloat(
			"indexer.score.power", 0.5f);
	
	public static int indexDocumentNumber = 0 ;
	private static File file = new File(SearchContext.search_dir + File.separator);
	static {
		
		IndexSearcher reader = null;
		try {

				initIndexWriter(NutchFileSystem.get(), file.toString(), file.exists()?false:true);
			{// 如果索引目录为空或未创建索引目录，则创建索引目录，并读取文档数
				reader = new IndexSearcher(SearchContext.search_dir
						+ File.separator + "index");
				if (reader != null && reader.getIndexReader() != null)
					IndexFetcher.docNum = reader.getIndexReader().numDocs();

			}
		} catch (Exception ex) {
			/**
			 * 初始化异常后需要手动删除索引文件后在执行初始化操作，如果直接初始化，，可能会导致 数据丢失
			 */
			// try{
			// initIndexWriter(NutchFileSystem.get(),file.toString(),true) ;
			// }catch(Exception e)
			// {
			// e.printStackTrace() ;
			// }
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {
			}
		}
		{
			Thread hookThread = null;
			if (hookThread != null) {
				Runtime.getRuntime().removeShutdownHook(hookThread);
				// hasRegistHook = false ;
			}
			{
				/**
				 * 1  停止push接口接受数据
				 * 2  阻止生成新的爬虫线程
				 * 3  关闭 indexWriter 和 BDB
				 */
				hookThread = new Thread(new Runnable() {
					public void run() {
						SearchContext.isShutDown = true;
						
						if (indexWriter != null) {
							LOG.info("准备结束索引输出,等待爬虫结束......");
							while(true){
								try {
									if(SearchContext.isFethcerShutDown)
										break;
								} catch (Exception e1) {
									break;
									// TODO Auto-generated catch block
//									e1.printStackTrace();
								}
							}
							synchronized (indexWriter) {
								try {								
////									BerkeleyDB.close() ;
//									if (IndexWriter
//											.isLocked(SearchContext.search_dir
//													+ File.separator + "index")) {
//										LOG.info("索引目录被锁定，可能是JVM异常退出导致的，开始尝试解除锁");
//										IndexWriter
//												.unlock(FSDirectory
//														.getDirectory(SearchContext.search_dir
//																+ File.separator
//																+ "index"));
//										if (!IndexWriter
//												.isLocked(SearchContext.search_dir
//														+ File.separator + "index"))
//											LOG.info("索引目录解锁成功");
//										else {
//											LOG.info("索引目录解锁失败,需要手动删除锁文件");
//										}
//									}
	
									//								
									{ // 优化索引，然后关闭
										BerkeleyDB.close();
										if (indexWriter != null) {
											indexWriter.close();
											indexWriter = null;
										}
	
										FileUtil.fullyDelete(localWorkingDir);
									}
	
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						
					}
				});
			}
			{
				Runtime.getRuntime().addShutdownHook(hookThread);
			}
		}
	}
	/**
	 * @deprecated
	 * @throws Exception
	 */
	public static void commit() throws Exception {
		commit(true,false);
	}
	public static void commit(boolean command,boolean cmd) throws Exception {
		if(command){
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// 遍历每个节点
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut)).commit();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		commit(false); 
	}
	/**
	 * 
	 * @param auto
	 * @throws Exception
	 */
	public static void commit(boolean auto) throws Exception {
			synchronized (indexWriter) {
				if (indexWriter != null) {
					indexWriter.commit();
					if(!SearchContext.isShutDown && (indexWriter.getSegmentsSize()>SearchContext.mergeNum || auto)){
//						System.out.println("索引优化合并");
						indexWriter.optimize();
					  }
					RuntimeDataCollect.has_new_index = true;
					BerkeleyDB.syncDb();
					indexDocumentNumber = 0 ;
				}
			}
	}

	private static IndexWriter indexWriter;
	private static File localWorkingDir;

	private static synchronized IndexWriter initIndexWriter(
			NutchFileSystem nfs, String directory, boolean isCreate)
			throws IOException {
		File srcDir = new File(directory, "index");
		{
			if (indexWriter == null) {
				try {
					if (IndexWriter.isLocked(directory + File.separator
							+ "index")) {
						LOG.info("索引目录被锁定，可能是JVM异常退出导致的，开始尝试解除锁");
						IndexWriter.unlock(FSDirectory.getDirectory(directory
								+ File.separator + "index"));
						if (!IndexWriter.isLocked(directory + File.separator
								+ "index"))
							LOG.info("索引目录解锁成功");
						else {
							LOG.info("索引目录解锁失败,需要手动删除锁文件");
							NutchCommand.setCrawl(false) ;
							throw new IOException("索引目录解锁失败,请手动删除锁文件");
						}
					}
					RuntimeDataCollect.setCrawl_start_time(new Date());
					localWorkingDir = new File(srcDir,
							"indexsegment-workingdir");
					//
					// OK, fine. Build the writer to the local file, set params
					//
					File outputIndex = srcDir;
					File tmpOutputIndex = new File(localWorkingDir, "index");

					File localOutput = nfs.startLocalOutput(outputIndex,
							tmpOutputIndex);
					
					MaxFieldLength fieldlength;
					if(SearchContext.getXdtechsite().getDlength()==null ||(SearchContext.getXdtechsite().getDlength()!=null&&SearchContext.getXdtechsite().getDlength().equals("")) ||!SearchContext.getXdtechsite().getDlength().matches("[\\d]?")  || SearchContext.getXdtechsite().getDlength().equals("0"))
					{
						fieldlength= IndexWriter.MaxFieldLength.UNLIMITED;
					}
					else
					{
						fieldlength= new MaxFieldLength(Integer.valueOf(SearchContext.getXdtechsite().getDlength()));
					}
					indexWriter = new IndexWriter(localOutput,
							new NutchDocumentAnalyzer(), isCreate,
							fieldlength);
					indexWriter.setMergeFactor(SearchContext.getXdtechsite()
							.getMergefactor());
//					indexWriter.setMergeFactor(5);
					
					indexWriter.setMaxMergeDocs(MAX_MERGE_DOCS);
					if(SearchContext.getXdtechsite().getMinmergedocs()>RAM_MERGE_DOCS)
					{
						indexWriter.setMaxBufferedDocs(SearchContext.getXdtechsite().getMinmergedocs()) ;
					}else
					{	
						indexWriter.setRAMBufferSizeMB(SearchContext.getXdtechsite().getMinmergedocs());
					}

					indexWriter.setTermIndexInterval(TERM_INDEX_INTERVAL);
//					indexWriter.setMaxFieldLength(maxFieldLength);
					indexWriter.setInfoStream(LogFormatter.getLogStream(LOG,
							Level.FINE));
					indexWriter.setUseCompoundFile(true);
					indexWriter.setSimilarity(new NutchSimilarity());

				} catch (java.io.FileNotFoundException ex) {
					LOG.info("发现异常信息，索引未创建，开始创建...");
					{
//						srcDir.renameTo(new File(
//										new java.text.SimpleDateFormat(
//												"yyyyMMddHHmmss")
//												.format(new Date())));
						if (!srcDir.exists())
							srcDir.mkdir();
					}
					initIndexWriter(nfs, directory, true);
				}
			}
		}
		return indexWriter;
	}

	public static IndexWriter initIndexWriter(NutchFileSystem nfs,
			String directory) throws IOException {

		return initIndexWriter(nfs, directory, false);
	}
	
	public synchronized static String indexpage(Content content) throws Exception{
		String flag=content.getFlag();
		if (FetcherDataPro.FILEDATA.equals(flag)) {
			RuntimeDataCollect.setFileCrawl_page_num(1);
		}
		if (FetcherDataPro.DATABASEDATA.equals(flag)) {
			RuntimeDataCollect.setCrawl_page_num(1);
		}
		// build initial document w/ core fields
		Document doc = new Document();
		makeDocument(doc );
		doc = IndexingFilters.filter(doc, null, null, content);				
		content.getMetadata().put("boost", doc.get("boost")) ;
		return indexpage(true ,doc,content.getMetadata());
	}
	public synchronized static String indexpage(boolean command,Document doc,Properties data) throws Exception{
		String code="";
		try {
			synchronized (indexWriter) {		
				code = BerkeleyDB.getRecordNum() ;
				doc.add(Field.UnIndexed("docNo", code));
				data.put("docNo", code);
				BerkeleyDB.saveRecord(code, data);
				indexWriter.addDocument(doc);
			}
			indexDocumentNumber++ ;
		} catch (IOException ex) {
			ex.printStackTrace();
			LOG.info("IO错误:" + ex.getMessage());
			NutchCommand.setCrawl(false) ;
			
			throw new IOException(ex.getMessage());
		} catch (DatabaseException ex) {
			LOG.info("Berkeley DB错误:" + ex.getMessage());
			NutchCommand.setCrawl(false) ;
			throw new IOException(ex.getMessage());
		}
		return code;
	}
	private static String hashCodeGen(Properties data)
	{
		return "" ;
	}
	/**
	 * Add core fields, required by other core components & features (i.e.,
	 * merge, dedup, explain).
	 */
	private static void makeDocument(Document doc) {

		
		// add docno & segment, used to map from merged index back to segment
		// files
		// doc.add(Field.UnIndexed("docNo", Long.toString(docNo, 16)));
		// doc.add(Field.UnIndexed("segment", segmentName));
		// add digest, used by dedup
		float boost = 1.0f;
		// 4. Apply boost to all indexed fields.
		doc.setBoost(boost);
		// store boost for use by explain and dedup
		doc.add(Field.UnIndexed("boost", Float.toString(boost)));

	}

	public static float calculateBoost(float pageScore, float scorePower,
			boolean boostByLinkCount, int linkCount) {
		// 1. Start with page's score from DB -- 1.0 if no link analysis.
		float res = pageScore;
		// 2. Apply scorePower to this.
		res = (float) Math.pow(pageScore, scorePower);
		// 3. Optionally boost by log of incoming anchor count.
		if (boostByLinkCount)
			res *= (float) Math.log(Math.E + linkCount);
		return res;
	}

	/**
	 * 链接密度 ，链接密度越低 ， 得分越高，内链越多 ， 得分越高，加入页面的时候 ， 内链的URL 分值低 ， 外链的分值高
	 * 
	 * @param outlinks
	 *            Outlink[]
	 * @param text
	 *            String
	 * @return float
	 */
	public static float anchorDensity(Outlink[] outlinks, String text,
			String host) {
		if (text == null)
			text = "";
		int linkTextLength = 0, textLength = 0, innerLink = 0, outerLink = 0;
		for (Outlink outLink : outlinks) {
			linkTextLength += outLink.getAnchor().length() + 1;
			if (outLink.getToUrl() != null) {
				if (outLink.getToUrl().indexOf(host) >= 0)
					innerLink++;
				else
					outerLink++;
			}
		}
		textLength = text.length() - linkTextLength;
		float density1 = outlinks.length / textLength;
		float density2 = outlinks.length / text.length();
		return 0f;
	}
}
