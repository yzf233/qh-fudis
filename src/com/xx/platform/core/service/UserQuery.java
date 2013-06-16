package com.xx.platform.core.service;

public class UserQuery   implements java.io.Serializable {
	public String [][] RequireKey;//必须包含关键字
	public String [][] ProhibitKey;//必须不包含关键字
	public String [][] RequireOneMoreKey;//必须包含其中一个或者更多关键字，最少一个
	public String [][] ProhibitOneMoreKey;//必须不包含其中一个或者更多关键字，最少一个
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
