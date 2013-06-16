package com.xx.platform.core.rpc;

import java.util.Properties;

import org.apache.lucene.document.Document;

public class IndexContentBean{
	private Document doc;
	private Properties pro;
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}
	public Properties getPro() {
		return pro;
	}
	public void setPro(Properties pro) {
		this.pro = pro;
	}
}
