package com.xx.platform.web.actions.search;
import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.nutch.searcher.*;
import org.apache.nutch.util.NutchConf;
import org.apache.nutch.searcher.Summary.*;
import org.apache.nutch.analysis.NutchDocumentAnalyzer;
import org.apache.nutch.html.Entities;

import com.xx.platform.core.SearchContext;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Summarizer {
    public static class Fragments extends Fragment{
        Fragments(String text){super(text);}
       public String toString() { return super.getText(); }
  }

    /** The number of context terms to display preceding and following matches.*/
   private static final int SUM_CONTEXT =
           NutchConf.get().getInt("searcher.summary.context", 5);

   /** The total number of terms to display in a summary.*/
   private static final int SUM_LENGTH =
           NutchConf.get().getInt("searcher.summary.length", 100);

   /** Converts text to tokens. */
   private static final Analyzer ANALYZER = SearchContext.KEYWORD_ANALYZER; //new NutchDocumentAnalyzer();

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
       public void add(Fragments fragment) {
           passages.add(fragment);
       }

       /**
        * Return an Enum for all the fragments
        */
       public Enumeration elements() {
           return passages.elements();
       }
   }


   /** Returns a summary for the given pre-tokenized text. */
   public Summary getSummary(String text, String query) throws IOException {

       // Simplistic implementation.  Finds the first fragments in the document
       // containing any query terms.
       //
       // TODO: check that phrases in the query are matched in the fragment
	   text =text.replaceAll("<", "&lt;");
		  text =text.replaceAll(">", "&gt;");
		  text = text.replaceAll("\"", "&quot;");
       Token[] tokens = getTokens(text); // parse text to token array

       if (tokens.length == 0) {
           //System.out.println("÷¥––Œ Ã‚£∫");
           return new Summary();
       }
       Token[] querys = getTokens(query);

       HashSet highlight = new HashSet(); // put query terms in table
       for (int i = 0; i < querys.length; i++)
           highlight.add(querys[i].termText());

       //
       // Create a SortedSet that ranks excerpts according to
       // how many query terms are present.  An excerpt is
       // a Vector full of Fragments and Highlights
       //
       SortedSet excerptSet = new TreeSet(new Comparator() {
           public int compare(Object o1, Object o2) {
               Excerpt excerpt1 = (Excerpt) o1;
               Excerpt excerpt2 = (Excerpt) o2;

               if (excerpt1 == null && excerpt2 != null) {
                   return -1;
               } else if (excerpt1 != null && excerpt2 == null) {
                   return 1;
               } else if (excerpt1 == null && excerpt2 == null) {
                   return 0;
               }

               int numToks1 = excerpt1.numUniqueTokens();
               int numToks2 = excerpt2.numUniqueTokens();

               if (numToks1 < numToks2) {
                   return -1;
               } else if (numToks1 == numToks2) {
                   return excerpt1.numFragments() - excerpt2.numFragments();
               } else {
                   return 1;
               }
           }
       }
       );

       int lastExcerptPos = 0;
       int offset = 0;

       Summary s = new Summary();
       int strLength = 0;
       for (int i = 0; i < tokens.length; i++) {
           int exceNum = 0;
           strLength = strLength + tokens[i].termText().length();
           if (strLength >= text.length()) {
               break;
           }
           if (highlight.contains(tokens[i].termText())) {
               Excerpt excerpt = new Excerpt();
               Token t = tokens[i];
               {
//                excerpt.add();
                   s.add(new Fragments(text.substring(offset,
                           t.startOffset())));
               }

               Token a = null;
               {
                   Highlight hight = new Highlight(t.termText());
                   s.add(hight);
                   exceNum++;
               //    a = (Token) t.cloneToken();
                   offset = t.endOffset();
               }
           }
       }
       {
           {

               if (offset < text.length() && tokens.length > 0 &&
                   text.length() > 0 && offset >= 0) {
                   if(offset<text.length())
                   {
                       s.add(new Fragments(text.substring(offset,
                               text.length())));
                   }
               }
               if (text.length() >text.length()) {
                   s.add(new Ellipsis());
               }
           }
       }

       return s;
   }

   private Token[] getTokens(String text) throws IOException {
       ArrayList result = new ArrayList();
    TokenStream xdToken = ANALYZER.tokenStream("content", new StringReader(text));
       for (Token token = xdToken.next(); token != null; token = xdToken.next()) {
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
//        if (argv.length < 2) {
//            System.out.println(
//                    "Usage: java org.apache.nutch.searcher.Summarizer <textfile> <queryStr>");
//            return;
//        }

       Summarizer s = new Summarizer();

       //
       // Parse the args
       //
       StringBuffer queryBuf = new StringBuffer();
       for (int i = 1; i < argv.length; i++) {
           queryBuf.append(argv[i]);
           queryBuf.append(" ");
       }

       //
       // Load the text file into a single string.
       //
//        StringBuffer body = new StringBuffer();
//        BufferedReader in = new BufferedReader(new FileReader(textFile));
//        try {
//            System.out.println("About to read " + textFile + " from " + in);
//            String str = in.readLine();
//            while (str != null) {
//                body.append(str);
//                str = in.readLine();
//            }
//        } finally {
//            in.close();
//        }

       // Convert the query string into a proper Query
      // Query query = Query.parse("¿¨");
    System.out.println("Summary: '" + s.getSummary("2004-03-24000000000.000test2insitese", "00") +
                         "'");
   }

}
