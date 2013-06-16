package com.xx.platform.core.service;

public class UserQuery   implements java.io.Serializable {
	public String [][] RequireKey;//��������ؼ���
	public String [][] ProhibitKey;//���벻�����ؼ���
	public String [][] RequireOneMoreKey;//�����������һ�����߸���ؼ��֣�����һ��
	public String [][] ProhibitOneMoreKey;//���벻��������һ�����߸���ؼ��֣�����һ��
	public UserQuery [] RequireComplexKey;
	public UserQuery [] ProhibitComplexKey;
	public String str="";
	public String getStr() {
		return str;
	}
	public UserQuery[] getProhibitComplexKey() {
		return ProhibitComplexKey;
	}
	public void setProhibitComplexKey(UserQuery[] prohibitComplexKey) {
		ProhibitComplexKey = prohibitComplexKey;
	}
	public UserQuery[] getRequireComplexKey() {
		return RequireComplexKey;
	}
	public void setRequireComplexKey(UserQuery[] requireComplexKey) {
		RequireComplexKey = requireComplexKey;
	}
	public void setStr(String str) {
		this.str = str;
	}
	public UserQuery(){
		this.RequireKey=null;
		this.ProhibitKey=null;
		this.RequireOneMoreKey=null;
		this.ProhibitOneMoreKey=null;
	}
	public UserQuery(String [][] RequireKey,String [][] ProhibitKey,String [][] RequireOneMoreKey,String [][] ProhibitOneMoreKey,UserQuery [] RequireComplexKey,UserQuery [] ProhibitComplexKey)
	{
		this.RequireKey=RequireKey;
		this.ProhibitKey=ProhibitKey;
		this.RequireOneMoreKey=RequireOneMoreKey;
		this.ProhibitOneMoreKey=ProhibitOneMoreKey;
	}
	
	public String[][] getRequireKey() {
		return RequireKey;
	}
	public void setRequireKey(String[][] requireKey) {
		RequireKey = requireKey;
	}
	
	public String[][] getProhibitKey() {
		return ProhibitKey;
	}
	public void setProhibitKey(String[][] prohibitKey) {
		ProhibitKey = prohibitKey;
	}
	public String[][] getRequireOneMoreKey() {
		return RequireOneMoreKey;
	}
	public void setRequireOneMoreKey(String[][] requireOneMoreKey) {
		RequireOneMoreKey = requireOneMoreKey;
	}
	public String[][] getProhibitOneMoreKey() {
		return ProhibitOneMoreKey;
	}
	public void setProhibitOneMoreKey(String[][] prohibitOneMoreKey) {
		ProhibitOneMoreKey = prohibitOneMoreKey;
	}

	
}
