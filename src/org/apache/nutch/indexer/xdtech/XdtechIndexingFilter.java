package org.apache.nutch.indexer.xdtech;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.nutch.analysis.NutchDocumentAnalyzer;
import org.apache.nutch.fetcher.*;
import org.apache.nutch.indexer.*;
import org.apache.nutch.parse.*;
import org.apache.nutch.protocol.*;
import org.apache.nutch.util.*;

import com.xx.platform.core.*;
import com.xx.platform.core.analyzer.XdAnalyzer;
import com.xx.platform.core.analyzer.XdOneKeyAnalyzer;
import com.xx.platform.core.db.BerkeleyDB;
import com.xx.platform.core.nutch.*;
import com.xx.platform.domain.model.database.*;
import com.xx.platform.util.tools.MD5;

import java.io.StringReader;
import java.net.InetAddress;

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
public class XdtechIndexingFilter implements IndexingFilter {
	private static final int MAX_TITLE_LENGTH = NutchConf.get().getInt("indexer.max.title.length", 100);
	private static final boolean DEFAULT_INDEX_CONTENT = NutchConf.get().getBoolean("index.content.field", true);

	private FetcherOutput fetcherOutput = null;
	private static List<IndexFieldImpl> indexList; // 索引字段
	private String encoding = "GBK";
	static {
		indexList = SearchContext.getDataHandler().findAllByIObjectCType(IndexFieldImpl.class);
	}

	public static void reloadRules() {
		indexList = SearchContext.getDataHandler().findAllByIObjectCType(IndexFieldImpl.class);
	}

	private Field field = null;
	private StringBuffer context = new StringBuffer();
	private Analyzer ANALYZER;

	/**
	 * 
	 * @param doc
	 *            Document
	 * @param parse
	 *            Parse
	 * @param fo
	 *            FetcherOutput
	 * @param content
	 *            Content
	 * @return Document
	 * @throws IndexingException
	 */
	public Document filter(Document doc, Parse parse, FetcherOutput fo, Content content) throws IndexingException {
		Properties returnProperty = new Properties();
		try {
			Properties properties = content.getMetadata();
			String indexValue = "";
			String doctype = (String) properties.get("docType");
			String docSource = (String) properties.get("docSource");
			String dataSource = (String) properties.get("dataSource");

			String url = (String) properties.get("url");
			doc.add(createField(returnProperty, "docType", doctype, Field.Store.NO, Field.Index.NOT_ANALYZED));
			doc.add(createField(returnProperty, "docSource", (String) properties.get("docSource"), Field.Store.NO,
					Field.Index.NOT_ANALYZED));
			boolean hasTitle = false;
			boolean formdb = !docSource.equals("file");
			context.setLength(0);
			if (indexList != null) {
				for (IndexFieldImpl indexField : indexList) {
					if (indexField.getCode().equals("title"))
						hasTitle = true;

					indexValue = ""; // clear indexValue
					{
						indexValue = properties.getProperty(indexField.getId());
						indexValue = (indexValue != null && !indexValue.trim().equals("") ? indexValue : "");
						{
							if (formdb) {
								if (context.length() > 0)
									context.append(" ");
								context.append(indexValue);
							}
						}
					}
					field = createField(returnProperty, indexField.getCode(), indexValue != null ? (indexField
							.isIndex() ? indexValue : indexValue) : "", Field.Store.NO,
							indexField.isToken() ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED);
					field.setBoost((float) indexField.getBoost());
					doc.add(field);
				}
			}
			// 对content字段内容做处理 如果是文件，则把文件类型和路径存放在content的后面
			if (!formdb && context.length() < 1) {
				context.append(properties.getProperty("content"));
				// context.append(" ").append(doctype);
				// context.append(" ").append(properties.getProperty("docSource"));
				// context.append(" ").append(dataSource);
			}

			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmss");
			if (formdb)// 如果是来自数据库的
			{
				String contents = (context == null || context.toString().length() < 1) ? new String(content
						.getContent()) : context.toString();
				doc.add(createField(returnProperty, "database", "content", DEFAULT_INDEX_CONTENT ? contents : "",
						Field.Store.NO, Field.Index.ANALYZED));
				int contentlength = 0;
				if (context != null)
					contentlength = context.toString().length() - 1;
				if (contentlength == 0)// 有可能是推送来的数据
				{
					contentlength = contents.length();
				}
				if (contentlength < 0)
					contentlength = 0;
				// InetAddress addr = InetAddress.getLocalHost();
				// String ip=addr.getHostAddress().toString();//本地ip
				// String address=addr.getHostName().toString();//获得本机名称
				url = (url == null) ? "" : url;
				doc.add(createField(returnProperty, "url", url, Field.Store.NO, Field.Index.NOT_ANALYZED));
				if (dataSource == null)
					dataSource = "";
				String type = properties.getProperty("type") != null ? properties.getProperty("type") : "";
				doc.add(createField(returnProperty, "type", type, Field.Store.NO, Field.Index.NOT_ANALYZED));
				doc.add(createField(returnProperty, "dataSource", dataSource, Field.Store.NO, Field.Index.ANALYZED));
				doc.add(createField(returnProperty, "updataDate", sdf.format(d), Field.Store.NO,
						Field.Index.NOT_ANALYZED));
				doc.add(createField(returnProperty, "contentLength", String.valueOf(contentlength), Field.Store.NO,
						Field.Index.NOT_ANALYZED));
				doc.add(createField(returnProperty, "subType", "", Field.Store.NO, Field.Index.NOT_ANALYZED));

			} else// 如果来自文件
			{
				String contentt = properties.getProperty("content") != null ? properties.getProperty("content")
						: (context.length() < 1) ? "" : context.toString();
				// 当content字段为空时改为存储，不然没有content字段
				doc.add(createField(returnProperty, "content", contentt.length() < 1 ? "" : contentt, Field.Store.NO,
						Field.Index.ANALYZED));
				// System.out.println("添加CONTENT：---"+doc.get("content")+"---"+properties.getProperty("url"));
				doc.add(createField(returnProperty, "url", properties.getProperty("url"), Field.Store.NO,
						Field.Index.NOT_ANALYZED));
				doc.add(createField(returnProperty, "dataSource", dataSource, Field.Store.NO, Field.Index.ANALYZED));
				// doc.add(createField(returnProperty,"title",properties.getProperty("title"),Field.Store.NO,Field.Index.TOKENIZED));
				doc.add(createField(returnProperty, "updataDate", sdf.format(new Date(Long.valueOf(properties
						.getProperty("updataDate")))), Field.Store.NO, Field.Index.NOT_ANALYZED));
				if (properties.get("contentLength") == null)
					doc
							.add(createField(returnProperty, "contentLength", "0", Field.Store.NO,
									Field.Index.NOT_ANALYZED));
				else if (!((String) properties.get("contentLength")).equals("0"))
					doc.add(createField(returnProperty, "contentLength", (String) properties.get("contentLength"),
							Field.Store.NO, Field.Index.NOT_ANALYZED));
				else
					doc
							.add(createField(returnProperty, "contentLength", "1", Field.Store.NO,
									Field.Index.NOT_ANALYZED));
				String type = properties.getProperty("type") != null ? properties.getProperty("type") : "";
				doc.add(createField(returnProperty, "type", type, Field.Store.NO, Field.Index.NOT_ANALYZED));
				String subtype = properties.getProperty("subType") != null ? properties.getProperty("subType") : "";
				doc.add(createField(returnProperty, "subType", subtype, Field.Store.NO, Field.Index.NOT_ANALYZED));

			}
			// 内容长度

			doc.add(createField(returnProperty, "createDate", sdf.format(d), Field.Store.NO, Field.Index.NOT_ANALYZED));// 创建索引的时间
			// 预留字段处理
			doc.add(createField(returnProperty, "site", "", Field.Store.NO, Field.Index.NOT_ANALYZED));

			if (!hasTitle)
				doc.add(createField(returnProperty, "title", content.getTitle() != null ? content.getTitle() : "",
						Field.Store.NO, Field.Index.ANALYZED));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			content.setMetadata(returnProperty);
		}
		return doc;
	}

	private Field createField(Properties returnProperty, String fieldName, String fieldValue, Store store, Index index)
			throws Exception {
		return createField(returnProperty, "default", fieldName, fieldValue, store, index);
	}

	private Field createField(Properties returnProperty, String type, String fieldName, String fieldValue, Store store,
			Index index) throws Exception {
		if (!"content".equals(fieldName)&&!"attaContent".equals(fieldName)) {
			fieldValue = filterText(fieldValue);
		}
		if (StringUtils.isEncryptField(fieldName))
			returnProperty.put(fieldName, "content".equals(fieldName) && "database".equals(type) ? "" : StringUtils
					.encrypt(fieldValue));
		else
			returnProperty.put(fieldName, "content".equals(fieldName) && "database".equals(type) ? "" : fieldValue);
		return new Field(fieldName, fieldValue, store, index);
	}

	/**
	 * 对索引内容不可见字符进行过滤
	 * 
	 * @param text
	 * @return
	 */
	private String filterText(String text) {
		if (text == null)
			return null;
		char[] temps = text.toCharArray();
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < temps.length; i++) {
			int c = (int) temps[i];
			if (c > 31 && c != 127) {
				temp.append((char) c);
			}
		}
		return temp.toString();
	}

	public String getEncoding() {
		return encoding;
	}

	public FetcherOutput getFetcherOutput() {
		return fetcherOutput;
	}

	public static List<IndexFieldImpl> getIndexList() {
		return indexList;
	}
}
