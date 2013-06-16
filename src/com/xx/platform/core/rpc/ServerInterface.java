package com.xx.platform.core.rpc;

import java.util.List;
import java.util.Map;

import org.apache.nutch.fetcher.FileFetcher;

import com.xx.platform.core.nutch.*;
import com.xx.platform.domain.model.crawl.*;
import com.xx.platform.domain.model.database.Dbtable;
import com.xx.platform.domain.model.distributed.*;
import com.xx.platform.domain.model.system.*;
import com.xx.platform.util.tools.MD5;

public interface ServerInterface<T> extends RpcInterface {

	public IndexFieldImpl[] indexFieldList() throws Exception;

	public Diserver getBindSearchServer(String ip, int port) throws Exception;

	//  public  WebDB[] fetchURList(long start, long page_size) throws
	//      Exception;

	public Proregion[] getProregionList() throws Exception;

	public Urlfilterreg[] getUrlFilterList() throws Exception;

	public String[][] fetchURList() throws Exception;

	public Dbtable getDbtable() throws Exception;

	public Xdtechsite getSiteinfo() throws Exception;

	public void putParseURL(WebDB[] webDb) throws Exception;

	public void updateWebDB(WebDB webDb) throws Exception;

	public void addWebDB(WebDB webDb) throws Exception;

	public void reloadWebDB() throws Exception;

	public void putParseData(Object[] object) throws Exception;

	public void updateStatus(String ip, int port, boolean status)
			throws Exception;

	public byte[] getFileContent(String path);

	public DBList getFetchList() throws Exception;
	
	public DBList getFileFetchList() throws Exception;

	public boolean getNutchCommand_CRAWL();

	public boolean getNutchCommand_FILECRAWL();
	
    public boolean addContents(String str) throws Exception;

    public boolean removeContents(String str) throws Exception;
    
    public void removeAllContents(byte[] data) throws Exception;
    
	public String[][] fileFetchURList();
	
	public int getRMdatasSize();
	
}
