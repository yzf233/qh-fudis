package com.xx.platform.core.analyzer;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.DESFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.nutch.util.NutchConf;

import com.xx.platform.core.StringUtils;

public class XdOneKeyAnalyzer  extends Analyzer {
    private static String FILTER_WORDS_KEY = NutchConf.get().get("index.filter.words", ""); 
	public static final String[] FILTER_WORDS =FILTER_WORDS_KEY!=null?getFilterWord(FILTER_WORDS_KEY):new String[]{} ;
    private static Boolean FILTER_WORDS_KEY_COMMAND = NutchConf.get().getBoolean("index.filterwords.enable", true);
	private static String [] getFilterWord(String str)
	{
		String word []=new String [str.length()];
		for(int i=0;i<word.length;i++)
		{
			word[i]=String.valueOf(str.charAt(i));
		}
		return word;
	}
    public final TokenStream tokenStream(String filename, Reader reader) {
    	TokenStream result=FILTER_WORDS_KEY_COMMAND?new StopFilter(new XdOneKeyTokenizer(reader), FILTER_WORDS):new StopFilter(new XdOneKeyTokenizer(reader), new String[]{});
        if(StringUtils.isEncryptField(filename))
        	result = new DESFilter(result);
    	return  result;
    }

}
