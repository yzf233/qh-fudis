package com.xx.platform.core.analyzer;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.DESFilter;
import org.apache.lucene.analysis.TokenStream;

import com.xx.platform.core.StringUtils;
/**
 * 自定义分词解析类
 * @author 胡俊
 */
public class XdAnalyzer extends Analyzer {
	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream result=new XdTokenizer(reader);
		if(StringUtils.isEncryptField(fieldName))
			result = new DESFilter(result);
		return result;
	}


}
