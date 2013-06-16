package org.apache.nutch.fetcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.lucene.index.IndexWriter;
import org.apache.nutch.db.Page;
import org.apache.nutch.ipc.RPC;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.util.NutchConf;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.util.tools.IndexMessage;
import com.xx.platform.util.tools.MD5;

public class FetcherDataPro {
	public final static String FILEDATA = "FileData";// ��ʶ�Ƿ����ļ��ɼ�����
	public final static String DATABASEDATA = "DatabaseData";// ��ʶ�Ƿ������ݿ�ɼ�����

	private WebDbAdminTool webDbAdminTool;
	private IndexWriter indexWriter;
	private Page page = null;
	private String flag;
	private static final List<String> dbNoReperat = new ArrayList<String>();// ���ݿ�����ƴȥ���ֶ�ʱ����Ե���������е��ֶ�ֵ
	static {
		String field=NutchConf.get().get("crawler.db.repeat.fix");
		if(field!=null){
			String[] fields=field.trim().split(",");
			if(fields.length>0){
				for(String f:fields){
					if(f.trim().length()>0){
						dbNoReperat.add(f.trim());
					}
				}
			}
		}
	}

	public FetcherDataPro(WebDbAdminTool webDbAdminTool,
			IndexWriter indexWriter, Page page, String flag) {
		this.indexWriter = indexWriter;
		if (webDbAdminTool == null) {
			this.webDbAdminTool = new WebDbAdminTool();
		} else {
			this.webDbAdminTool = webDbAdminTool;
		}
		if (page == null) {
			try {
				this.page = new Page("http://www.www.com", 1f, 0);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			this.page = page;
		}
		this.flag = flag;
		if (flag == null) {
			try {
				throw new Exception("����FetcherDataPro����ʱflag���Բ���Ϊ�գ�");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean dataPro(Map<String, String> map, boolean command)
			throws Exception {
		StringBuffer strb = new StringBuffer();
		if (flag.equals(FILEDATA)) {
			strb.append(map.get("content"));
			strb.append(map.get("dataSource"));
			strb.append(map.get("docType"));
			strb.append(map.get("title"));
		} else if (flag.equals(DATABASEDATA)) {
			Object[] key = map.keySet().toArray();
			Arrays.sort(key);
			for (int i = 0; i < key.length; i++) {
				if (dbNoReperat.contains(key[i])) {
					continue;
				} else {
					String value = (String) map.get(key[i]);
					strb.append(value == null ? "" : value).append(" ");
				}
			}
		}
		String md5Content = MD5.encoding(strb.toString());
		boolean isOk = webDbAdminTool.addContents(md5Content);
		if (isOk) {
			// String url = page.getURL().toString();
			Properties properties = new Properties();
			Content content = null;

			if (flag.equals(FILEDATA)) {
				String con = map.get("content") != null ? map.get("content")
						: "";
				content = new Content("", map.get("title") == null ? "title"
						: map.get("title").toString(), con.getBytes(),
						"text/html", properties);
				content.setFlag(flag);
			} else if (flag.equals(DATABASEDATA)) {
				content = new Content("", map.get("title") == null ? "title"
						: map.get("title").toString(), strb.toString()
						.getBytes(), "text/html", properties);
				content.setFlag(flag);
			}
			properties.put("type", md5Content);
			properties.putAll(map);
			outputPage(content);
			if (command) {
				List<Synchro> synchro = SearchContext.getSynchroList();
				if (ImDistributedTool.isRuning && ImDistributedTool.isReady
						&& synchro != null && synchro.size() > 0) {
					for (Synchro s : synchro) {// ����ÿ���ڵ�
						try {
							IndexMessage im = new IndexMessage(flag, map);
							if (s.getIm() == null)
								s.setIm(((ImInterface) RPC.getProxy(
										ImInterface.class, ImDistributedTool
												.getNode(s.getIpaddress()),
										SearchContext.synChroTiomeOut)));

							s.getIm().dataPro(im);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return isOk;
	}

	private void outputPage(Content content) {
		outputPage(content, true);
	}

	private void outputPage(Content content, boolean command) {
		try {
			/**
			 * URL ���� �ɼ�����������صķǽṹ����ȡ���� , ���� ���������� ��ȡ���� ��������� �� �����Ĭ�ϵ���������
			 * ����ָ��Ĭ�ϵķ����ֶ�
			 */
			String docNo = IndexFetcher.indexpage(content);
		} catch (Throwable t) {
			// LOG.severe("error writing output:" + t.toString());
			t.printStackTrace();
		}
	}

}
