package com.xx.platform.core.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.CharTokenizer;

import com.xx.platform.core.SearchContext;
/***
 * 自定义分词解析类
 * @author 胡俊
 * 根据抽象类Tokenizer成员tokenString，通过实现父类CharTokenizer的抽象函数isTokenChar来判断当前字符是否是分隔字符
 */
public class XdTokenizer extends CharTokenizer  {
	  public XdTokenizer(Reader in) {
	    super(in);
	  }
	  protected boolean isTokenChar(char c) {
		  String tokenString=SearchContext.getSplitwords();//获得当前分隔字符
		  if(tokenString.indexOf(c)>=0)
		  return false;
		  else
		  return true;
	  }
}
