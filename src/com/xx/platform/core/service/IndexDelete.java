package com.xx.platform.core.service;

import javax.jws.WebService;
import javax.jws.WebResult;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.TermDocs;

import java.io.File;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.*;
import org.apache.nutch.searcher.Hit;
import org.apache.nutch.searcher.NutchBean;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.domain.model.system.Delaydelindex;
import com.xx.platform.util.tools.SLOG;
import com.xx.platform.util.tools.ipcheck.CheckIPUtil;

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
@WebService(serviceName = "index", endpointInterface = "com.xx.platform.core.service.IndexInterface")
public class IndexDelete implements IndexInterface {
	private WebDbAdminTool webDbAdminTool;

	@WebResult(name = "index")
	public boolean deleteOneIndexByID(int ino, int docId) throws Exception {
		if(!CheckIPUtil.checkIpFacadeWebService()){
			  return false;
		}
		synchronized (SearchContext.indexLock) {
			String field = "docNo";
			String value = Integer.toHexString(docId + 1);
			if (field == null || value == null || field.equals("")
					|| value.equals(""))
				return false;
			int delDoces = 0;
			NutchBean bean = NutchBean.getBean();
			Hit hit = new Hit(ino, docId);
			//开始删除
			if (RuntimeDataCollect.diserver != 0) {
				return bean.deleteDocuments(ino, docId);
			}else {//if (RuntimeDataCollect.getCrawl_status().equals(NutchCommand.CRAWL_STATUS_NOT_RUNNING)) {
				boolean sucesess = false;
				try{
					sucesess = bean.deleteDocuments(ino, docId);
				}catch(Exception e){
				}
				return sucesess;
				//删除结束，判断状态
			}
		}
	}
/**
 * 返回-500，IP地址不合法
 */
	@WebResult(name = "index_field")
	public long deleteOneIndexByField(String field, String value)
			throws Exception {
		if(!CheckIPUtil.checkIpFacadeWebService()){
			  return -500;
		  }
		synchronized (SearchContext.indexLock) {
			if (field == null || value == null || field.equals("")
					|| value.equals("")){
				return 0;
			}
			int delDoces = 0;
			NutchBean bean = NutchBean.getBean();
			if (RuntimeDataCollect.diserver != 0) {			
				delDoces = bean.deleteDocuments(field, value);
				return delDoces;
			}
			boolean sucesess = false;
			try{
				delDoces = bean.deleteDocuments(field, value);
			}catch(Exception e){
				e.printStackTrace();
				return -1;
			}
			return delDoces;
		}

	}

//	public void addDelQuery(File f, String field, String value)
//			throws Exception//把延迟删除的信息加入到队列里-胡俊
//	{
//		Directory directory = FSDirectory.getDirectory(f, false);
//		IndexReader reader = IndexReader.open(directory);
//		Delaydelindex dl = new Delaydelindex();
//		TermDocs docs = reader.termDocs(new Term(field, value));
//		String docno = "";
//		int i = 0;
//		while (docs.next()) {
//			Document d = reader.document(docs.doc());
//			docno += d.getField("docNo").stringValue() + ",";//取得索引编号
//			i++;
//			if (i > 330)//如果删除过多，则先插入一部分。
//			{
//				dl.setFname(field);
//				dl.setFvalue(value);
//				dl.setFdocno(docno);
//				SearchContext.getDao().saveIObject(dl);
//				dl = new Delaydelindex();
//				i = 0;
//				docno = "";
//			}
//		}
//		dl.setFname(field);
//		dl.setFvalue(value);
//		dl.setFdocno(docno);
//		SearchContext.getDao().saveIObject(dl);
//		reader.close();
//		directory.close();
//	}

//	public int deleteDocuments(Term term, IndexReader reader) throws Exception {
//		TermDocs docs = reader.termDocs(term);
//		if (docs == null)
//			return 0;
//		int n = 0;
//		webDbAdminTool = new WebDbAdminTool();
//		try {
//			while (docs.next()) {
//				Document d = reader.document(docs.doc());
//				String docSource = d.getField("docSource").stringValue();
//				if (docSource.equals("database")) {
//					webDbAdminTool.removeContents(d.getField("type")
//							.stringValue());
//				} else if (docSource.equals("file")) {
//					webDbAdminTool.removeContents(d.getField("type")
//							.stringValue());
//					webDbAdminTool.removeContents(d.getField("subType")
//							.stringValue());
//				}
//				reader.deleteDocument(docs.doc());
//				n++;
//			}
//		} catch (StaleReaderException e)//reader对象过期
//		{
//			return -1;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			docs.close();
//		}
//		return n;
//	}
	

}
