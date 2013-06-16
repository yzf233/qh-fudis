package com.xx.platform.web.actions.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.Action;
import com.xx.platform.dao.FunctionParameters;
import com.xx.platform.dao.IDaoManager;
import com.xx.platform.domain.model.search.SearchReport;
import com.xx.platform.web.actions.BaseAction;

public class SearchReportAction extends BaseAction{
	List<SearchReport> searchReportList=new ArrayList<SearchReport>();
	private String[] sorts;
	private String[] sortGroup;
	private String startTime;
	private String endTime;
	/**
	 * 热点检索次数统计
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String hotspotStatistics(){
		if(sorts==null||sorts.length==0||sortGroup==null||sortGroup.length==0){
			return Action.SUCCESS;
		}
		StringBuilder sbHql=new StringBuilder();
		StringBuilder whereHql=new StringBuilder();
		StringBuilder groupHql=new StringBuilder();
		
		for(String sort:sorts){
			if(whereHql.length()==0){
				whereHql.append(" where type in(").append(sort);
			}else{
				whereHql.append(",").append(sort);
			}
		}
		if(whereHql.length()>0){
			whereHql.append(")");
		}
		for(String group:sortGroup){
			if(groupHql.length()==0){
				groupHql.append(" group by ").append(group);
			}else{
				groupHql.append(",").append(group);
			}
		}
		groupHql.append(",type ");
		sbHql.append("select count(*) as queryTimes,keyword,type from SearchReport");
		sbHql.append(whereHql).append(groupHql);
		sbHql.append(" order by count(*) desc");
		String hql=sbHql.toString();
		//System.out.println(hql);
		IDaoManager dao=(IDaoManager)service;
		FunctionParameters[] fields={new FunctionParameters("setQueryTimes",Long.class),new FunctionParameters("setKeyword",String.class),new FunctionParameters("setType",Long.class)};
		searchReportList=dao.hqlListBox(SearchReport.class,hql,fields,0,20);
		Collections.sort(searchReportList);
		return Action.SUCCESS;
	}
	@SuppressWarnings("unchecked")
	public String noResult(){
		if(sorts==null||sorts.length==0||sortGroup==null||sortGroup.length==0){
			return Action.SUCCESS;
		}
		StringBuilder sbHql=new StringBuilder();
		StringBuilder whereHql=new StringBuilder();
		StringBuilder groupHql=new StringBuilder();
		
		for(String sort:sorts){
			if(whereHql.length()==0){
				whereHql.append(" vtype in(").append(sort);
			}else{
				whereHql.append(",").append(sort);
			}
		}
		if(whereHql.length()>0){
			whereHql.append(")");
		}
		for(String group:sortGroup){
			if(groupHql.length()==0){
				groupHql.append(" group by ").append(group);
			}else{
				groupHql.append(",").append(group);
			}
		}
		groupHql.append(",vtype ");
		sbHql.append("select count(*) as queryTimes,keyword,vtype from searchreport where resultsnum=0 and ").append(whereHql).append(" group by keyword,vtype");
		//sbHql.append(groupHql).append(") cc where cc.aa=0 and ").append(whereHql).append(groupHql).append(" order by count(*) desc");
		IDaoManager dao=(IDaoManager)service;
		FunctionParameters[] fields={new FunctionParameters("setQueryTimes",Long.class),new FunctionParameters("setKeyword",String.class),new FunctionParameters("setType",Long.class)};
		//System.out.println(sbHql.toString());
		searchReportList=dao.sqlListBox(SearchReport.class,sbHql.toString(),fields,0,20);
		Collections.sort(searchReportList);
		return Action.SUCCESS;
	}
	@SuppressWarnings("unchecked")
	public String timeSeg() throws ParseException{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date startDate=null;
		Date endDate=null;
		if(startTime!=null){
			startDate=sdf.parse(startTime);
		}
		if(endTime!=null){
			endDate=sdf.parse(endTime);
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DATE, 1);
			endDate=calendar.getTime();
		}
		if(sorts==null||sorts.length==0||sortGroup==null||sortGroup.length==0){
			return Action.SUCCESS;
		}
		StringBuilder sbHql=new StringBuilder();
		StringBuilder whereHql=new StringBuilder();
		StringBuilder groupHql=new StringBuilder();
		for(String sort:sorts){
			if(whereHql.length()==0){
				whereHql.append(" where type in(").append(sort);
			}else{
				whereHql.append(",").append(sort);
			}
		}
		if(whereHql.length()>0){
			whereHql.append(")");
		}
		whereHql.append(" and createtime>=? and createtime<=? ");
		for(String group:sortGroup){
			if(groupHql.length()==0){
				groupHql.append(" group by ").append(group);
			}else{
				groupHql.append(",").append(group);
			}
		}
		groupHql.append(",type ");
		sbHql.append("select count(*) as queryTimes,keyword,type from SearchReport");
		sbHql.append(whereHql).append(groupHql).append(" order by count(*) desc");
		//String hql="select count(*) as queryTimes,id,keyword,query,createtime,resultsnum,type,username from SearchReport where type in(1,0,2) and createtime>? and createtime<? group by keyword,query order by count(*) desc";
		IDaoManager dao=(IDaoManager)service;
		FunctionParameters[] fields={new FunctionParameters("setQueryTimes",Long.class),new FunctionParameters("setKeyword",String.class),new FunctionParameters("setType",Long.class)};
		//System.out.println(sbHql.toString());
		searchReportList=dao.hqlListBoxBetween(SearchReport.class,sbHql.toString(),fields,0,20,startDate,endDate);
		Collections.sort(searchReportList);
		return Action.SUCCESS;
	}
	public List<SearchReport> getSearchReportList() {
		return searchReportList;
	}
	public String[] getSorts() {
		return sorts;
	}
	public void setSorts(String[] sorts) {
		this.sorts = sorts;
	}
	public String[] getSortGroup() {
		return sortGroup;
	}
	public void setSortGroup(String[] sortGroup) {
		this.sortGroup = sortGroup;
	}
	public void setSearchReportList(List<SearchReport> searchReportList) {
		this.searchReportList = searchReportList;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
