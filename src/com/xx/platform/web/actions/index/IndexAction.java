package com.xx.platform.web.actions.index;

import java.util.*;

import org.apache.nutch.searcher.*;
import org.hibernate.criterion.*;
import com.opensymphony.xwork2.*;
import com.xx.platform.core.*;
import com.xx.platform.core.nutch.*;
import com.xx.platform.core.service.*;
import com.xx.platform.domain.model.database.*;
import com.xx.platform.web.actions.*;
import com.xx.platform.web.actions.search.*;

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
public class IndexAction extends BaseAction {
	private List<IndexFieldImpl> indexFieldList;
	private List searchResult;
	private IndexFieldImpl indexField;
	private SearchResult indexSearchResult;
	String[] forbidWords = new String[] { "Title", "subType", "segment",
			"type", "contentLength", "boost", "docNo", "site", "url",
			"content", "docType", "docSource", "createDate", "updataDate",
			"dataSource" };
	private String message;
	private int page = 1;
	private String q;
	private String ty;
	private String sty;
	private String sf;
	private int num;
	private RuntimeDataCollect runtime;
	public String indexmessage;

	public String list() {
		indexFieldList = service.findAllByIObjectCType(IndexFieldImpl.class);
		runtime = new RuntimeDataCollect();
		return Action.SUCCESS;
	}

	public String addDo() throws Exception {

		if (indexField != null) {
			String indexCode = indexField.getCode();
			for (int i = 0; i < forbidWords.length; i++) {
				if (indexCode.equalsIgnoreCase(forbidWords[i])) {
					message = "索引代码不能为关键字" + forbidWords[i] + "，<br/>请参考页面中的提示";
					return Action.INPUT;
				}
			}
			if (service.findPageByCriteria(
					DetachedCriteria.forClass(IndexFieldImpl.class).add(
							Restrictions.or(Restrictions.eq("name", indexField
									.getName()), Restrictions.eq("code",
									indexField.getCode())))).size() > 0) {
				message = "名称或者代码重复";
				return Action.INPUT;
			} else {
				if (indexField.getTokentype() == 1)
					indexField.setTokenString("");
				service.saveIObject(indexField);
				if(RuntimeDataCollect.diserver!=0)//如果启动分布式
				SearchContext.addIndex(indexField);//同步新增节点索引-胡俊
				SearchContext.reloadRules();
			}
		}
		return Action.SUCCESS;
	}

	public String edit() throws Exception {
		runtime = new RuntimeDataCollect();
		if ((indexField != null && indexField.getId() != null)) {
			indexField = (IndexFieldImpl) service.getIObjectByPK(
					IndexFieldImpl.class, indexField.getId());
		} else if (request.getParameter("id") != null) {
			indexField = (IndexFieldImpl) service.getIObjectByPK(
					IndexFieldImpl.class, request.getParameter("id"));
		}
		return Action.SUCCESS;

	}

	public String delIndex() throws Exception {
//		String id = request.getParameter("id"); //分类
//		String idx = request.getParameter("idx"); //分类
//		IndexDelete indexDelete = new IndexDelete();
//		if (id != null && id.matches("[\\d]{1,}")) {
//			boolean rs = indexDelete.deleteOneIndexByID(Integer.parseInt(id));
//		}还有用么？？胡俊
		return Action.SUCCESS;
	}

	public String viewIndex() throws Exception {
		String id = request.getParameter("id"); //分类
		String idx = request.getParameter("idx"); //分类
		NutchBean nutchBean = NutchBean.getBean();
		Hit hit = null;
		// 'id' is hit.indexDocNo
		// 'idx' is hit.indexNo
		hit = new Hit(0, Integer.parseInt(id));
		HitDetails details = nutchBean.getDetails(hit);
		indexSearchResult = new SearchResult();
		indexSearchResult.setTitle(details.getValue("title"));

		indexSearchResult.setDetails(details);
		indexSearchResult.setUrl(details.getValue("url"));

		return Action.SUCCESS;
	}

	public String editDo() throws Exception {

		if (request.getParameter("type") != null
				&& request.getParameter("type").equals("1")) {
			if (indexField != null && indexField.getId() != null) {

				List<Tableproperty> l = service
						.findAllByIObjectCType(Tableproperty.class);
				for (int i = 0; l != null && i < l.size(); i++) {
					Tableproperty t = l.get(i);
					if (t.getIndexfield() != null
							&& t.getIndexfield().equals(indexField.getId())) {
						t.setIndexfield(null);
						service.updateIObject(t);
					}
				}
				if(RuntimeDataCollect.diserver!=0)//如果启动分布式
				SearchContext.delIndex(indexField);//同步删除节点索引-胡俊
				service.deleteIObject(indexField);
			}
		} else {
			if (indexField != null && indexField.getId() != null) {
				String indexCode = indexField.getCode();
				for (int i = 0; i < forbidWords.length; i++) {
					if (indexCode.equalsIgnoreCase(forbidWords[i])) {
						message = "索引代码不能为关键字" + forbidWords[i]
								+ "，<br/>请参考页面中的提示";
						return Action.INPUT;
					}
				}
				if (service
						.findPageByCriteria(
								DetachedCriteria
										.forClass(IndexFieldImpl.class)
										.add(
												Restrictions
														.and(
																Restrictions
																		.not(Restrictions
																				.eq(
																						"id",
																						indexField
																								.getId())),
																Restrictions
																		.or(
																				Restrictions
																						.eq(
																								"name",
																								indexField
																										.getName()),
																				Restrictions
																						.eq(
																								"code",
																								indexField
																										.getCode())))))
						.size() > 0) {
					message = "名称或者代码重复";
					return Action.INPUT;
				} else {
					if (indexField.isToken() == false)
						indexField.setTokentype(0);
					service.updateIObject(indexField);
					if(RuntimeDataCollect.diserver!=0)//如果启动分布式
					SearchContext.updateIndex(indexField);//同步更新节点索引-胡俊
				}
			}
		}
		SearchContext.reloadRules();
		return Action.SUCCESS;
	}

	public IndexFieldImpl getIndexField() {
		return indexField;
	}

	public List getIndexFieldList() {
		return indexFieldList;
	}

	public int getNum() {
		return num;
	}

	public int getPage() {
		return page;
	}

	public String getMessage() {
		return message;
	}

	public List getSearchResult() {
		return searchResult;
	}

	public String getSf() {
		return sf;
	}

	public String getSty() {
		return sty;
	}

	public String getTy() {
		return ty;
	}

	public String getQ() {
		return q;
	}

	public SearchResult getIndexSearchResult() {
		return indexSearchResult;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setIndexFieldList(List indexFieldList) {
		this.indexFieldList = indexFieldList;
	}

	public void setIndexField(IndexFieldImpl indexField) {
		this.indexField = indexField;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSearchResult(List searchResult) {
		this.searchResult = searchResult;
	}

	public void setTy(String ty) {
		this.ty = ty;
	}

	public void setSty(String sty) {
		this.sty = sty;
	}

	public void setSf(String sf) {
		this.sf = sf;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public void setIndexSearchResult(SearchResult indexSearchResult) {
		this.indexSearchResult = indexSearchResult;
	}

	public RuntimeDataCollect getRuntime() {
		return runtime;
	}

	public void setRuntime(RuntimeDataCollect runtime) {
		this.runtime = runtime;
	}

	public String getIndexmessage() {
		return indexmessage;
	}

	public void setIndexmessage(String indexmessage) {
		this.indexmessage = indexmessage;
	}
}
