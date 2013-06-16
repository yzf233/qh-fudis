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

package org.apache.nutch.searcher;

import java.io.*;
import java.util.*;

import com.xx.platform.core.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.nutch.analysis.*;
import org.apache.nutch.searcher.Summary.*;

/** Implements hit summarization. */
public class Summarizer {

  /** The number of context terms to display preceding and following matches.*/
//  private static final int SUM_CONTEXT = SearchContext.getXdtechsite().
//      getSummary();
  private static final int SUM_CONTEXT =5;
  /** The total number of terms to display in a summary.*/
  private static final int SUM_LENGTH = SearchContext.getXdtechsite().
      getSummary() * 4;
 // private static final int SUM_LENGTH = 200;

  /** Converts text to tokens. */
  private static final Analyzer ANALYZER = new NutchDocumentAnalyzer();
  private static  String cssfront = "<font color=red>" ;
  private static  String cssend = "</font>" ;
  /**
   * Class Excerpt represents a single passage found in the
   * document, with some appropriate regions highlit.
   */
  class Excerpt {
    Vector passages = new Vector();
    SortedSet tokenSet = new TreeSet();
    int numTerms = 0;

    /**
     */
    public Excerpt() {
    }

    /**
     */
    public void addToken(String token) {
      tokenSet.add(token);
    }

    /**
     * Return how many unique toks we have
     */
    public int numUniqueTokens() {
      return tokenSet.size();
    }

    /**
     * How many fragments we have.
     */
    public int numFragments() {
      return passages.size();
    }

    public void setNumTerms(int numTerms) {
      this.numTerms = numTerms;
    }

    public int getNumTerms() {
      return numTerms;
    }

    /**
     * Add a frag to the list.
     */
    public void add(Fragment fragment) {
      passages.add(fragment);
    }

    /**
     * Return an Enum for all the fragments
     */
    public Enumeration elements() {
      return passages.elements();
    }
  }
  StringBuffer strb = new StringBuffer() ;
  private StringBuffer oText(String input)
  {
    StringBuffer temp = new StringBuffer() ;
    for(int i=0 ; input!=null && i<input.length() ; i++)
    {
      if(input.charAt(i)!='*' && input.charAt(i)!='?' && input.charAt(i)!='('&& input.charAt(i)!=')'&& input.charAt(i)!='+'&& input.charAt(i)!='-')
        temp.append(input.charAt(i));
    }
    return temp;
  }
  /** Returns a summary for the given pre-tokenized text. */
  public Summary getSummary(String text, Query query) throws IOException {
	  text =text.replaceAll("<", "&lt;");
	  text =text.replaceAll(">", "&gt;");
	  text = text.replaceAll("\"", "&quot;");
	  String[] terms = query.getTerms();
    //String[] terms = query.split(" ");
    //~~��terms����
    Set realterms = termsfilter(terms); //�˴�ֻ�Ǽ򵥵�ȥ��,���Զ�"�й���/�й�"��ʽ�Ĵ������������
    Summary s = new Summary();
    List<Integer> indexs = new ArrayList<Integer>();//��¼���йؼ��ʵ�(��ʼ)λ��
    int average = 0; //�ؼ���ƽ���ܶ�
    int textlen = text.length();
    for (Iterator i = realterms.iterator();i.hasNext();){
        StringBuffer hilight = oText((String)i.next());
        text = text.replaceAll(hilight.toString(),hilight.insert(0 , cssfront).append(cssend).toString());
    }
    //System.out.println("texts:"+text);
   //~~�ܶ�ͳ�ƿ�ʼ
   int st = text.indexOf(cssfront);
   if(st>=0){
       indexs.add(st);
       for (;;) { //ȡ���йؼ�����ʼλ��
           st=text.indexOf(cssfront,st+cssfront.length());
           if(st>=0)
               indexs.add(st);
           else
               break;
       }
       textlen = text.length();//����text����
       average = (textlen)/indexs.size(); //�ؼ���ƽ�����
       int start=0;//��ʼλ��
       int end =0;//����λ��(�߼�����λ��,����ʵ�ʽ���λ��)
       int next=0;//�¸��ؼ��ʵĿ�ʼλ��
       int temp=0;//�����ϸ��ؼ��ʵĿ�ʼλ��
       SortedSet excerptSet = new TreeSet(new Comparator() {
               public int compare(Object o1, Object o2) {
                   if (o1 == null && o2 != null) {
                       return -1;
                   } else if (o1 != null && o2 == null) {
                       return 1;
                   } else if (o1 == null && o2 == null) {
                       return 0;
                   }
                   if (((int[])o1)[2] < ((int[])o2)[2]) {
                       return -1;
                   } else if (((int[])o1)[2] == ((int[])o2)[2]) {
                       return 0;
                   } else {
                       return 1;
                   }
               }
           });// ��ժҪ�����򴢴�
       int[] summarizer ;
       for(int i=0;i<indexs.size();i++){
           summarizer = new int[]{0,0,0};//1 ��ʼλ��,2����λ��,3�ؼ��ʸ���
           int num=1; //�ؼ��ʸ���
           start = indexs.get(i);
           summarizer[0]=start;
           temp = start;
           end = (temp+SUM_LENGTH)>textlen?textlen:(temp+SUM_LENGTH);
           for(int j=i+1;j<indexs.size();j++){
               next = indexs.get(j);
               if(next<end&&(next-temp)<average){
                   num++;
                   temp = next;
                   summarizer[2] = num;
                   summarizer[1]=temp;
               }else{                            //������֮��ľ������ƽ���ܶȶ���nextС��end
                   summarizer[2] = num;
                   summarizer[1]=temp;
                   if(num==1)
                       i=j-1;
                   else
                       i = j;
                   break;
               }
           }
           if(summarizer[1]==0||summarizer[1]==start){ //û����һ����
               summarizer[1] =end;
           }
           excerptSet.add(summarizer);
       }
       //~~�ܶ�ͳ�ƽ���
       summarizer = (int[])excerptSet.last();
       int addlength = (SUM_LENGTH - (summarizer[1]-summarizer[0]))/2; //ͷ(β)������
      //��ժҪͷ��
       if(summarizer[0]>0){
           //s.add(new Ellipsis());
           String front =text.substring(0,summarizer[0]).replaceAll(cssfront,"").replaceAll(cssend,"");
           int fr_length = front.length();
           if(fr_length>addlength){
               s.add(new Ellipsis());
               s.add(new Fragment(front.substring(fr_length-addlength,fr_length)));
           }else
               s.add(new Fragment(front));
       }
       //ժҪ����
       int tend = text.indexOf(cssend, summarizer[1]) >= 0 ?(text.indexOf(cssend, summarizer[1]) + 7) : summarizer[1];
       int realend = tend ;
       int tlength = SUM_LENGTH+summarizer[2]*20;//ժҪ����(����ֵ)
       if((realend-summarizer[0])>tlength){
           realend = text.lastIndexOf(cssend,summarizer[0]+tlength)+7;
       }
//       s.add(new Fragment(text.substring(summarizer[0],(realend-summarizer[0])>SUM_LENGTH?(summarizer[0]+SUM_LENGTH):realend)));
       s.add(new Fragment(text.substring(summarizer[0], realend)));
       addlength += tlength - (realend - summarizer[0]);
       if(addlength<=0)
           addlength = 10;

        //��ժҪβ��
       if(realend<textlen){
           String foot =text.substring(realend,textlen).replaceAll(cssfront,"").replaceAll(cssend,"");
           int foot_length = foot.length();
           if(foot_length>addlength){
               s.add(new Fragment(foot.substring(0,addlength)));
               s.add(new Ellipsis());
           }else
               s.add(new Fragment(foot));
       }
   }else{
       s.add(new Fragment(text.substring(0, textlen>SUM_LENGTH?SUM_LENGTH:textlen)));
       if(textlen>SUM_LENGTH)
           s.add(new Ellipsis());
   }

   return s;
  }
    public static String getsummary(String queryString,List<String> valuelist,Analyzer analyzer) 
	{
      StringBuffer content=new StringBuffer();
	  for(String s:valuelist)
	  {
		  content.append(s).append(" ");
	  }
	  return getsummary(queryString,content.toString(),analyzer);
	}
	public static String getsummary(String queryString,String content,Analyzer analyzer) 
	{
		if(queryString==null&&content!=null)
		{
			if(content.length()>SUM_LENGTH)
				return content.substring(0,(SUM_LENGTH)-1);
			else
				return content;
		}
		else if(queryString!=null&&content==null)
			return "";
		else if(queryString==null&&content==null)
			return "";
		SimpleHTMLFormatter sHtmlF = new SimpleHTMLFormatter(cssfront, cssend);
		
		org.apache.lucene.search.Query summarizerQuery=null;
		QueryParser queryParse = new QueryParser("content",analyzer);
		try
		{
			summarizerQuery= queryParse.parse(queryString);
	    }
	    catch (ParseException ex) {
			if(content.length()>SUM_LENGTH)
				return content.substring(0,(SUM_LENGTH)-1);
			else
				return content;
	    }
	    QueryScorer qs= new QueryScorer(summarizerQuery);
		Highlighter highlighter = new Highlighter(sHtmlF,qs);
		highlighter.setTextFragmenter(new SimpleFragmenter(SUM_LENGTH));
		TokenStream tokenStream = analyzer.tokenStream("content",new StringReader(content));
		String str;
		try {
			str = highlighter.getBestFragment(tokenStream,content);
		} catch (IOException e) {
			str=null;
		}
        if(str==null)
        {
			if(content.length()>SUM_LENGTH)
				str=content.substring(0,(SUM_LENGTH)-1);
			else
				str=content;
        }
        return str;
	}
    /**
     * termsfilter
     *
     * @param terms String[]
     * @return Set
     */
    private Set termsfilter(String[] terms) {
        Set hilights = new TreeSet();
        for (int i = 0; terms!=null && i < terms.length; i++){
            if (terms[i].trim().length() > 0)
                hilights.add(terms[i]);
        }
        return hilights;
    }

    private Token[] getTokens(String text) throws IOException {
    ArrayList result = new ArrayList();
//        XDChineseTokenizer xdToken = new XDChineseTokenizer(new StringReader(
//                text));
    TokenStream ts = ANALYZER.tokenStream("content",
                                          new StringReader(text.toUpperCase()));
    for (Token token = ts.next(); token != null; token = ts.next()) {
      result.add(token);
    }
    return (Token[]) result.toArray(new Token[result.size()]);
  }

  /**
   * Tests Summary-generation.  User inputs the name of a
   * text file and a query string
   */
  public static void main(String argv[]) throws IOException {
    // Test arglist
//    if (argv.length < 2) {
//      System.out.println(
//          "Usage: java org.apache.nutch.searcher.Summarizer <textfile> <queryStr>");
//      return;
//    }

    Summarizer s = new Summarizer();

    //
    // Parse the args
    //
//    File textFile = new File("c:\\pku_test.txt");//new File(argv[0]);
//    StringBuffer queryBuf = new StringBuffer();
//    //for (int i = 1; i < argv.length; i++) {
//      //queryBuf.append(argv[i]);
//      queryBuf.append("�й�");
//      queryBuf.append(" ");
//      queryBuf.append("��չ");
//      queryBuf.append(" ");
//    //}
//
//    //
//    // Load the text file into a single string.
//    //
//    StringBuffer body = new StringBuffer();
//    BufferedReader in = new BufferedReader(new FileReader(textFile));
//    try {
//      System.out.println("About to read " + textFile + " from " + in);
//      String str = in.readLine();
//      while (str != null) {
//        body.append(str);
//        str = in.readLine();
//      }
//    }
//    finally {
//      in.close();
//    }
//    Query query = new Query();
//    query.addRequiredTerm("111");
//    System.out.println(s.getSummary("�Һ�123111��", query));
    // Convert the query string into a proper Query
    //Query query = Query.parse(queryBuf.toString());
    //System.out.println(Entities.encode("<font color=\"red\">"));//eplaceAll(Entities.encode("�ձ�"),"<font color=red>"+Entities.encode("�ձ�")+"</font>"));
    //System.out.println(s.getSummary(body.toString(), queryBuf.toString()));
  }
}
