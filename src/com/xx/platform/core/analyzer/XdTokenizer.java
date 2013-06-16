package com.xx.platform.core.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.CharTokenizer;

import com.xx.platform.core.SearchContext;
/***
 * �Զ���ִʽ�����
 * @author ����
 * ���ݳ�����Tokenizer��ԱtokenString��ͨ��ʵ�ָ���CharTokenizer�ĳ�����isTokenChar���жϵ�ǰ�ַ��Ƿ��Ƿָ��ַ�
 */
public class XdTokenizer extends CharTokenizer  {
	  public XdTokenizer(Reader in) {
	    super(in);
	  }
	  protected boolean isTokenChar(char c) {
		  String tokenString=SearchContext.getSplitwords();//��õ�ǰ�ָ��ַ�
		  if(tokenString.indexOf(c)>=0)
		  return false;
		  else
		  return true;
	  }
}
