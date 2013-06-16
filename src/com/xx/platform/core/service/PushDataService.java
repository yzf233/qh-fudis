package com.xx.platform.core.service;

import javax.jws.WebService;
import javax.jws.WebResult;
import org.apache.nutch.fetcher.Fetcher;
import org.apache.nutch.fetcher.IndexFetcher;
import org.apache.nutch.fs.NutchFileSystem;
import org.apache.nutch.indexer.IndexMerger;
import org.apache.nutch.ipc.RPC;
import org.apache.nutch.searcher.Hit;
import org.apache.nutch.searcher.NutchBean;
import java.net.InetSocketAddress;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.core.rpc.ClientInterface;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.domain.model.distributed.Diserver;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.domain.model.system.Delaydelindex;
import com.xx.platform.util.tools.ArraysObjectTool;
import com.xx.platform.util.tools.SLOG;
import com.xx.platform.util.tools.ipcheck.CheckIPUtil;

import org.apache.lucene.index.Term;
import java.io.File;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.TermDocs;
import org.apache.nutch.fetcher.PushFetcher;

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
@WebService(serviceName = "pushData", endpointInterface = "com.xx.platform.core.service.IndexPushInterface")
public class PushDataService implements IndexPushInterface {

	private static volatile java.util.List<PushObject> pushudatas = new java.util.ArrayList<PushObject>();
	private static volatile java.util.List<PushObject> delay_pushudatas = new java.util.ArrayList<PushObject>();
	private static boolean ismerger = false;

	/**
	 * 推送数据函数
	 * 
	 * @param docType
	 *            String 分类 默认为 page
	 * @param field
	 *            String[] 推送数据 字段 ， 和 value对应
	 * @param value
	 *            String[] 推送数据 值 ， 和 field对应
	 * @return int 返回值 0:数据校验错误 , 日期格式错误或
	 *         docType/subDocType/title/context/sourceHost 中的某些为NULL或者空字符
	 *         ；1:推送数据成功
	 *         ;-1:推送失败，系统目前正在执行并入数据操作，不接受新数据，请稍候在推送数据；-9：//系统停止中，不再接受新的请求
	 * @throws Exception
	 */

	@WebResult(name = "push")
	public int push(String docType, String[] field, String[][] value)
			throws Exception {
		if (!CheckIPUtil.checkIpFacadeWebService()) {
			return -500;// IP地址被禁用
		}
		if (SearchContext.isShutDown) {
			return -9;// 系统停止中，不再接受新的请求
		}
		synchronized (SearchContext.indexLock) {
			if (RuntimeDataCollect.diserver != 0) {
				List<Diserver> diservers = SearchContext.getDiserverList();
				for (Diserver d : diservers) {
					if (d.getStatus() == true && d.getPush() == 1) {
						InetSocketAddress defaultAddresses = new InetSocketAddress(
								d.getIpaddress(), d.getDismport());
						ClientInterface client = (ClientInterface) RPC.getProxy(ClientInterface.class, defaultAddresses);
						int num = client.push(docType, field, value);
						RuntimeDataCollect.has_new_index = true;
						return num;
					}
				}
				return -8;// 没有节点能够连接
			}
			String title = "";
			String url = "";
			for (String[] v : value) {
				if (v.length == field.length)
					delay_pushudatas.add(new PushObject(docType, title, field,
							v, url));
				Thread t = new Thread() {
					public void run() {
						while (delay_pushudatas != null
								&& delay_pushudatas.size() != 0) {

							try {
								PushFetcher fetcher = new PushFetcher(
										NutchFileSystem.get(),
										SearchContext.search_dir);
								PushObject pd = null;
								while (delay_pushudatas.size() > 0) {
									pd = delay_pushudatas.remove(0);
									fetcher.pushData(pd);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				};
				t.start();
			}
			RuntimeDataCollect.has_new_index = true;
			return 1;

		}

	}

	/**
	 * 并入索引
	 * 
	 * @param docId
	 *            int
	 * @return boolean
	 * @throws Exception
	 */
	@WebResult(name = "merger")
	public void merger() throws Exception {
		if (CheckIPUtil.checkIpFacadeWebService()) {
			synchronized (SearchContext.indexLock) {
				/**
				 * 通知分布式节点数据更新完毕
				 */
				if (RuntimeDataCollect.diserver != 0) {
					List<Diserver> diservers = SearchContext.getDiserverList();
					for (Diserver d : diservers) {
						if (d.getStatus() == true) {
							InetSocketAddress defaultAddresses = new InetSocketAddress(
									d.getIpaddress(), d.getDismport());
							((ClientInterface) RPC.getProxy(
									ClientInterface.class, defaultAddresses))
									.merger();
						}
					}
				} else {
					if (ismerger)// 如果已经调用合并接口，那么直接返回。
						return;
					ismerger = true;
					Thread t = new Thread(new Runnable() {
						public void run() {
							try {
								IndexFetcher.commit(true);

								RuntimeDataCollect.has_new_index = true;
								ismerger = false;
								List<Synchro> synchro = SearchContext
										.getSynchroList();
								if (ImDistributedTool.isRuning
										&& ImDistributedTool.isReady
										&& synchro != null
										&& synchro.size() > 0) {
									for (Synchro s : synchro) {// 遍历每个节点
										try {
											ImInterface imInterface = (ImInterface) RPC
													.getProxy(
															ImInterface.class,
															ImDistributedTool
																	.getNode(s
																			.getIpaddress()),
															SearchContext.synChroTiomeOut);
											imInterface.pushDataMerger();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								ismerger = false;
							}
						}
					});
					t.start();
				}
			}
		}
	}

	@WebResult(name = "updateById")
	public int updateById(int docId, String docType, String[] field,
			String[] value) throws Exception {
		synchronized (SearchContext.indexLock) {
			try {
				if (!RuntimeDataCollect.getCrawl_status().equals(
						NutchCommand.CRAWL_STATUS_IDLE)) {
					String title = "";
					String url = "";
					deleteOneIndexByID(docId);
					// File segmentFile = PushFetcher.createSegment();
					PushFetcher fetcher = new PushFetcher(
							NutchFileSystem.get(), SearchContext.search_dir);
					PushObject ps = new PushObject(docType, title, field,
							value, url);
					fetcher.pushData(ps);
				} else
					return 0;
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
			return 1;
		}
	}

	@WebResult(name = "updateByField")
	public int updateByField(String delField, String delValue, String docType,
			String[] field, String[] value) throws Exception {
		synchronized (SearchContext.indexLock) {
			try {
				if (!RuntimeDataCollect.getCrawl_status().equals(
						NutchCommand.CRAWL_STATUS_IDLE)) {
					String title = "";
					String url = "";
					deleteOneIndexByField(delField, delValue);
					// File segmentFile = PushFetcher.createSegment();
					PushFetcher fetcher = new PushFetcher(
							NutchFileSystem.get(), SearchContext.search_dir);
					PushObject ps = new PushObject(docType, title, field,
							value, url);
					fetcher.pushData(ps);
				} else
					return 0;
			} catch (Exception e) {
				return 0;
			}
			return 1;
		}
	}

	public boolean deleteOneIndexByID(int docId) throws Exception {
		synchronized (SearchContext.indexLock) {
			String field = "docNo";
			String value = Integer.toHexString(docId);
			if (field == null || value == null || field.equals("")
					|| value.equals(""))
				return false;
			int delDoces = 0;
			if (RuntimeDataCollect.diserver != 0) {
				NutchBean bean = NutchBean.getBean();
				delDoces = bean.deleteDocuments(field, value);
				if (delDoces > 0)
					return true;
				else
					return false;
			}
			if (RuntimeDataCollect.getCrawl_status().equals(
					NutchCommand.CRAWL_STATUS_NOT_RUNNING)) {
				File file = new File(SearchContext.search_dir + File.separator
						+ "index");
				if (!file.exists())
					return false;
				Directory directory = FSDirectory.getDirectory(file, false);
				IndexReader reader = IndexReader.open(directory);
				if (IndexReader.isLocked(directory)) {
					SLOG.addMessage("Webservice删除接口调用 field:" + field
							+ "  value:" + value + "  索引被锁定，等待服务器空闲时延迟删除。");
					addDelQuery(file, field, value);
					delDoces = -1;
					reader.close();
					directory.close();
					return false;
				}
				try {
					delDoces = reader.deleteDocuments(new Term(field, value));
					if (delDoces == -1)// 如果reader过期，则保存队列里面，延迟删除。
					{
						SLOG.addMessage("Webservice删除接口调用 field:" + field
								+ "  value:" + value
								+ "  删除数据出现异常，等待服务器空闲时延迟删除。");
						addDelQuery(file, field, value);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
				} finally {
					reader.close();
					directory.close();
				}

				if (delDoces > 0) {
					if (RuntimeDataCollect.diserver == 1)// 启动分布式
						SearchContext.addNewIndex("127.0.0.1",
								SearchContext.search_dir);
					else
						RuntimeDataCollect.has_new_index = true;
					return true;
				} else
					return false;
			} else {
				File file = new File(SearchContext.search_dir + File.separator
						+ "index");
				if (!file.exists())
					return false;
				SLOG.addMessage("Webservice删除接口调用 field:" + field + "  value:"
						+ value + "  由于索引正在合并，等待服务器空闲时延迟删除。");
				addDelQuery(file, field, value);
				delDoces = -1;
			}

			return false;
		}
	}

	public long deleteOneIndexByField(String field, String value)
			throws Exception {
		synchronized (SearchContext.indexLock) {
			if (field == null || value == null || field.equals("")
					|| value.equals(""))
				return 0;
			int delDoces = 0;
			if (RuntimeDataCollect.diserver != 0) {
				NutchBean bean = NutchBean.getBean();
				delDoces = bean.deleteDocuments(field, value);
				return delDoces;
			}
			if (RuntimeDataCollect.getCrawl_status().equals(
					NutchCommand.CRAWL_STATUS_NOT_RUNNING)) {
				File file = new File(SearchContext.search_dir + File.separator
						+ "index");
				if (!file.exists())
					return 0;
				Directory directory = FSDirectory.getDirectory(file, false);
				IndexReader reader = IndexReader.open(directory);
				if (IndexReader.isLocked(directory)) {
					SLOG.addMessage("Webservice删除接口调用 field:" + field
							+ "  value:" + value + "  索引被锁定，等待服务器空闲时延迟删除。");
					addDelQuery(file, field, value);
					delDoces = -1;
					reader.close();
					directory.close();
					return delDoces;
				}
				try {
					delDoces = reader.deleteDocuments(new Term(field, value));
					if (delDoces == -1)// 如果reader过期，则保存队列里面，延迟删除。
					{
						SLOG.addMessage("Webservice删除接口调用 field:" + field
								+ "  value:" + value
								+ "  删除数据出现异常，等待服务器空闲时延迟删除。");
						addDelQuery(file, field, value);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
				} finally {
					reader.close();
					directory.close();
				}

				if (delDoces > 0) {
					if (RuntimeDataCollect.diserver == 1)// 启动分布式
						SearchContext.addNewIndex("127.0.0.1",
								SearchContext.search_dir);
					else
						RuntimeDataCollect.has_new_index = true;
				}
			} else {
				File file = new File(SearchContext.search_dir + File.separator
						+ "index");
				if (!file.exists())
					return 0;
				SLOG.addMessage("Webservice删除接口调用 field:" + field + "  value:"
						+ value + "  由于索引正在合并，等待服务器空闲时延迟删除。");
				addDelQuery(file, field, value);
				delDoces = -1;
			}

			return delDoces;
		}
	}

	public void addDelQuery(File f, String field, String value)
			throws Exception// 把延迟删除的信息加入到队列里-胡俊
	{
		Directory directory = FSDirectory.getDirectory(f, false);
		IndexReader reader = IndexReader.open(directory);
		Delaydelindex dl = new Delaydelindex();
		TermDocs docs = reader.termDocs(new Term(field, value));
		String docno = "";
		int i = 0;
		while (docs.next()) {
			Document d = reader.document(docs.doc());
			docno += d.getField("docNo").stringValue() + ",";// 取得索引编号
			i++;
			if (i > 330)// 如果删除过多，则先插入一部分。
			{
				dl.setFname(field);
				dl.setFvalue(value);
				dl.setFdocno(docno);
				SearchContext.getDao().saveIObject(dl);
				dl = new Delaydelindex();
				i = 0;
				docno = "";
			}
		}
		dl.setFname(field);
		dl.setFvalue(value);
		dl.setFdocno(docno);
		SearchContext.getDao().saveIObject(dl);
		reader.close();
		directory.close();
	}

	public static java.util.List<PushObject> getDelay_pushudatas() {
		return delay_pushudatas;
	}

	public static void setDelay_pushudatas(
			java.util.List<PushObject> delay_pushudatas) {
		PushDataService.delay_pushudatas = delay_pushudatas;
	}

}
