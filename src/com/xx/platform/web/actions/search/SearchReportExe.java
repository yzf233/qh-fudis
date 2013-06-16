package com.xx.platform.web.actions.search;

import java.util.Arrays;
import java.util.Date;

import com.xx.platform.dao.GeneraDAO;
import com.xx.platform.domain.model.search.SearchReport;

public class SearchReportExe {
	private String keyWord;
	private String queryString;
	private long resultSize;
	private GeneraDAO service; 
	private String userName;
	private Date curTime;
	private long type;
	public SearchReportExe(String keyWord,String queryString,long resultSize,String userName,int type,GeneraDAO service){
		this.keyWord=keyWord;
		this.queryString=queryString;
		this.resultSize=resultSize;
		this.service=service;
		this.userName=userName;
		this.type=type;
		this.curTime=new Date();
	}
	/**
	 * ��ô����Ĺؼ���
	 * @return
	 */
	private void getKeyWord(){
		StringBuilder sbKeys=new StringBuilder();
		if(keyWord==null){
			try {
				throw new Exception("��ѯ�ؼ��ʲ�����Ϊ�գ�");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			keyWord=keyWord.trim();
			keyWord=keyWord.replaceAll("[ ]{1,}"," ");
			String[] keys=keyWord.split(" ");
			Arrays.sort(keys);
			for(String key:keys){
				if(sbKeys.length()>0){
					sbKeys.append(" ").append(key);
				}else{
					sbKeys.append(key);
				}
			}
		}
		keyWord=sbKeys.toString();
	}
	/**
	 * ��ô����Ĳ�ѯ���
	 */
	private void getQueryString(){
		if(queryString!=null){
			queryString=queryString.replace("\\","");
		}
	}
	/**
	 * �������ݵ����ݿ�
	 */
	public void saveRecord(){
		getKeyWord();
		getQueryString();
		SearchReport report=new SearchReport();
		report.setKeyword(keyWord);
		report.setQuery(queryString);
		report.setResultsnum(resultSize);
		report.setType(type);
		report.setUsername(userName);
		report.setCreatetime(curTime);
		service.saveIObject(report);
	}
	public static void main(String[] args){
	}
}
