/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.analysis;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.nutch.util.NutchConf;

/** The analyzer used for Nutch documents.  Uses the JavaCC-defined lexical
 * analyzer {@link NutchDocumentTokenizer}, with no stop list.  This keeps it
 * consistent with query parsing. */
public class NutchDocumentAnalyzer extends Analyzer {
	private Set stopSet;
	private static Boolean STOP_WORDS_KEY_COMMAND = NutchConf.get().getBoolean("index.stopwords.enable", true);
	  /**
	   * Specifies whether deprecated acronyms should be replaced with HOST type.
	   * This is false by default to support backward compatibility.
	   *
	   * @deprecated this should be removed in the next release (3.0).
	   *
	   * See https://issues.apache.org/jira/browse/LUCENE-1068
	   */
	  private boolean replaceInvalidAcronym = defaultReplaceInvalidAcronym;
          public static final int INTER_ANCHOR_GAP = 4;
	  private static boolean defaultReplaceInvalidAcronym;

	  // Default to true (fixed the bug), unless the system prop is set
	  static {
	    final String v = System.getProperty("org.apache.lucene.analysis.standard.StandardAnalyzer.replaceInvalidAcronym");
	    if (v == null || v.equals("true"))
	      defaultReplaceInvalidAcronym = true;
	    else
	      defaultReplaceInvalidAcronym = false;
	  }

	  /**
	   *
	   * @return true if new instances of StandardTokenizer will
	   * replace mischaracterized acronyms
	   *
	   * See https://issues.apache.org/jira/browse/LUCENE-1068
	   * @deprecated This will be removed (hardwired to true) in 3.0
	   */
	  public static boolean getDefaultReplaceInvalidAcronym() {
	    return defaultReplaceInvalidAcronym;
	  }

	  /**
	   *
	   * @param replaceInvalidAcronym Set to true to have new
	   * instances of StandardTokenizer replace mischaracterized
	   * acronyms by default.  Set to false to preseve the
	   * previous (before 2.4) buggy behavior.  Alternatively,
	   * set the system property
	   * org.apache.lucene.analysis.standard.StandardAnalyzer.replaceInvalidAcronym
	   * to false.
	   *
	   * See https://issues.apache.org/jira/browse/LUCENE-1068
	   * @deprecated This will be removed (hardwired to true) in 3.0
	   */
	  public static void setDefaultReplaceInvalidAcronym(boolean replaceInvalidAcronym) {
	    defaultReplaceInvalidAcronym = replaceInvalidAcronym;
	  }


	  /** An array containing some common English words that are usually not
	  useful for searching. */
	  public static final String[] STOP_WORDS = StopAnalyzer.ENGLISH_STOP_WORDS;

	  /** Builds an analyzer with the default stop words ({@link #STOP_WORDS}). */
	  public NutchDocumentAnalyzer() {
	    this(STOP_WORDS);
	  }

	  /** Builds an analyzer with the given stop words. */
	  public NutchDocumentAnalyzer(Set stopWords) {
	    stopSet = stopWords;
	  }

	  /** Builds an analyzer with the given stop words. */
	  public NutchDocumentAnalyzer(String[] stopWords) {
		stopWords=STOP_WORDS_KEY_COMMAND?stopWords:new String[]{};
	    stopSet = StopFilter.makeStopSet(stopWords);
	  }

	  /** Builds an analyzer with the stop words from the given file.
	   * @see WordlistLoader#getWordSet(File)
	   */
	  public NutchDocumentAnalyzer(File stopwords) throws IOException {
	    stopSet = WordlistLoader.getWordSet(stopwords);
	  }

	  /** Builds an analyzer with the stop words from the given reader.
	   * @see WordlistLoader#getWordSet(Reader)
	   */
	  public NutchDocumentAnalyzer(Reader stopwords) throws IOException {
	    stopSet = WordlistLoader.getWordSet(stopwords);
	  }

	  /**
	   *
	   * @param replaceInvalidAcronym Set to true if this analyzer should replace mischaracterized acronyms in the StandardTokenizer
	   *
	   * See https://issues.apache.org/jira/browse/LUCENE-1068
	   *
	   * @deprecated Remove in 3.X and make true the only valid value
	   */
	  public NutchDocumentAnalyzer(boolean replaceInvalidAcronym) {
	    this(STOP_WORDS);
	    this.replaceInvalidAcronym = replaceInvalidAcronym;
	  }

	  /**
	   *  @param stopwords The stopwords to use
	   * @param replaceInvalidAcronym Set to true if this analyzer should replace mischaracterized acronyms in the StandardTokenizer
	   *
	   * See https://issues.apache.org/jira/browse/LUCENE-1068
	   *
	   * @deprecated Remove in 3.X and make true the only valid value
	   */
	  public NutchDocumentAnalyzer(Reader stopwords, boolean replaceInvalidAcronym) throws IOException{
	    this(stopwords);
	    this.replaceInvalidAcronym = replaceInvalidAcronym;
	  }

	  /**
	   * @param stopwords The stopwords to use
	   * @param replaceInvalidAcronym Set to true if this analyzer should replace mischaracterized acronyms in the StandardTokenizer
	   *
	   * See https://issues.apache.org/jira/browse/LUCENE-1068
	   *
	   * @deprecated Remove in 3.X and make true the only valid value
	   */
	  public NutchDocumentAnalyzer(File stopwords, boolean replaceInvalidAcronym) throws IOException{
	    this(stopwords);
	    this.replaceInvalidAcronym = replaceInvalidAcronym;
	  }

	  /**
	   *
	   * @param stopwords The stopwords to use
	   * @param replaceInvalidAcronym Set to true if this analyzer should replace mischaracterized acronyms in the StandardTokenizer
	   *
	   * See https://issues.apache.org/jira/browse/LUCENE-1068
	   *
	   * @deprecated Remove in 3.X and make true the only valid value
	   */
	  public NutchDocumentAnalyzer(String [] stopwords, boolean replaceInvalidAcronym) throws IOException{
	    this(stopwords);
	    this.replaceInvalidAcronym = replaceInvalidAcronym;
	  }

	  /**
	   * @param stopwords The stopwords to use
	   * @param replaceInvalidAcronym Set to true if this analyzer should replace mischaracterized acronyms in the StandardTokenizer
	   *
	   * See https://issues.apache.org/jira/browse/LUCENE-1068
	   *
	   * @deprecated Remove in 3.X and make true the only valid value
	   */
	  public NutchDocumentAnalyzer(Set stopwords, boolean replaceInvalidAcronym) throws IOException{
	    this(stopwords);
	    this.replaceInvalidAcronym = replaceInvalidAcronym;
	  }

	  /** Constructs a {@link StandardTokenizer} filtered by a {@link
	  StandardFilter}, a {@link LowerCaseFilter} and a {@link StopFilter}. */
	  public TokenStream tokenStream(String fieldName, Reader reader) {
	    StandardTokenizer tokenStream = new StandardTokenizer(reader, replaceInvalidAcronym);
	    tokenStream.setMaxTokenLength(maxTokenLength);
	    TokenStream result = new StandardFilter(tokenStream);
	    result = new LowerCaseFilter(result);
	    result = new StopFilter(result, stopSet);
	    return result;
	  }

	  private static final class SavedStreams {
	    StandardTokenizer tokenStream;
	    TokenStream filteredTokenStream;
	  }

	  /** Default maximum allowed token length */
	  public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

	  private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

    /**
	   * Set maximum allowed token length.  If a token is seen
	   * that exceeds this length then it is discarded.  This
	   * setting only takes effect the next time tokenStream or
	   * reusableTokenStream is called.
	   */
	  public void setMaxTokenLength(int length) {
	    maxTokenLength = length;
	  }

	  /**
	   * @see #setMaxTokenLength
	   */
	  public int getMaxTokenLength() {
	    return maxTokenLength;
	  }

	  public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
	    SavedStreams streams = (SavedStreams) getPreviousTokenStream();
	    if (streams == null) {
	      streams = new SavedStreams();
	      setPreviousTokenStream(streams);
	      streams.tokenStream = new StandardTokenizer(reader);
	      streams.filteredTokenStream = new StandardFilter(streams.tokenStream);
	      streams.filteredTokenStream = new LowerCaseFilter(streams.filteredTokenStream);
	      streams.filteredTokenStream = new StopFilter(streams.filteredTokenStream, stopSet);
	    } else {
	      streams.tokenStream.reset(reader);
	    }
	    streams.tokenStream.setMaxTokenLength(maxTokenLength);

	    streams.tokenStream.setReplaceInvalidAcronym(replaceInvalidAcronym);

	    return streams.filteredTokenStream;
	  }

	  /**
	   *
	   * @return true if this Analyzer is replacing mischaracterized acronyms in the StandardTokenizer
	   *
	   * See https://issues.apache.org/jira/browse/LUCENE-1068
	   * @deprecated This will be removed (hardwired to true) in 3.0
	   */
	  public boolean isReplaceInvalidAcronym() {
	    return replaceInvalidAcronym;
	  }

	  /**
	   *
	   * @param replaceInvalidAcronym Set to true if this Analyzer is replacing mischaracterized acronyms in the StandardTokenizer
	   *
	   * See https://issues.apache.org/jira/browse/LUCENE-1068
	   * @deprecated This will be removed (hardwired to true) in 3.0
	   */
	  public void setReplaceInvalidAcronym(boolean replaceInvalidAcronym) {
	    this.replaceInvalidAcronym = replaceInvalidAcronym;
	  }
}
