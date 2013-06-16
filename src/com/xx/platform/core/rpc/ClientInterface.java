package com.xx.platform.core.rpc;

import com.xx.platform.core.nutch.IndexFieldImpl;
import com.xx.platform.domain.model.crawl.*;
import com.xx.platform.domain.model.system.Xdtechsite;

public interface ClientInterface<T> extends RpcInterface {

	public abstract boolean start_Db() throws Exception;
	
	public abstract boolean start_File() throws Exception;

	public abstract boolean stop() throws Exception;

	public abstract boolean pause() throws Exception;

	public abstract void restartServer() throws Exception;

	public abstract void stopServer() throws Exception;

	public abstract void setIndexFieldList(IndexFieldImpl[] index)
			throws Exception;

	public abstract void setCrawlerList(Crawler[] crawler) throws Exception;

	public abstract void setParseRuleList(ParserRule[] parserRule)
			throws Exception;

	public abstract void getCategoryList(Category[] category) throws Exception;

	public abstract void setMetaProcessRuleList(
			MetaProcessRule[] metaProcessRule) throws Exception;

	public abstract void setUrlFilterList(Urlfilterreg[] urlFilter)
			throws Exception;

	public abstract void setXdtechsite(Xdtechsite obj) throws Exception;

	public CrawlInfo getCrawlInfo() throws Exception;

	public String getCrawlStatus() throws Exception;

	public long getIndexNum() throws Exception;

	public void addNewIndex(boolean hasNewIndex) throws Exception;

	public void deleteIndexfield(IndexFieldImpl indexfield) throws Exception;

	public void updateIndexfield(IndexFieldImpl indexfield) throws Exception;

	public void addIndexfield(IndexFieldImpl indexfield) throws Exception;

	public void merger() throws Exception;
	
	public long getCrawl_page_num();
	
	public long getFileCrawl_page_num();
	
	public long getIndexFileSize();
	
	public int push(String docType, String[] field, String[][] value) throws Exception;
}
