package com.xx.platform.core.analyzer;

/*
 * Created on 2005-2-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.DESFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.nutch.util.NutchConf;

import com.xx.platform.core.StringUtils;

/**
 * @author Jia Mi
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class XDChineseAnalyzer extends Analyzer {
    private Set stopWords;
    private static String STOP_WORDS_KEY = NutchConf.get().get("index.stop.words", "");
    private static Boolean STOP_WORDS_KEY_COMMAND = NutchConf.get().getBoolean("index.stopwords.enable", true);
    /**
     * An array containing some common English words that are not usually useful
     * for searching.
     */
    public static final String[] STOP_WORDS = STOP_WORDS_KEY!=null?STOP_WORDS_KEY.split("[,， ]"):new String[]{} ;

//        { "a",  "as", "at", "be", "but",
//            "by","these", "they", "this", "to", "was", "for", "if", "in", "into", "is", "it",
//            "no",  "such", "t", "that", "the", "or","an", "and", "are", "s", "there",
//            "their", "then","not", "of", "on", "will", "with", "是", "的", "不", "可", "好", "无" };

    public XDChineseAnalyzer() {
        stopWords = StopFilter.makeStopSet(STOP_WORDS);
    }

    public XDChineseAnalyzer(String[] stopWords) {
        this.stopWords = StopFilter.makeStopSet(stopWords);
    }

    public final TokenStream tokenStream(String filename, Reader reader) {
    	TokenStream result=STOP_WORDS_KEY_COMMAND?new StopFilter(new XDChineseTokenizer(reader), stopWords):new StopFilter(new XDChineseTokenizer(reader), new String[]{});
        if(StringUtils.isEncryptField(filename))
        	result = new DESFilter(result);
    	return result;
    }
}
