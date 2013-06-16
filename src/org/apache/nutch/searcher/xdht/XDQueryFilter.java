package org.apache.nutch.searcher.xdht;

import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.QueryException;
import org.apache.nutch.searcher.QueryFilter;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.IndexField;

/**
 * <p>Title: 片短语查询</p>
 *
 * <p>Description: 会导致其他的queryfilter都不可用,content字段范围搜索等都不可用</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author quhuan
 * @version 1.0
 */
public class XDQueryFilter
    implements QueryFilter {

  private static final float[] FIELD_BOOSTS = {5.0f};
  private static final String[] FIELDS = {"content"};
  private static float PHRASE_BOOST = 0f;//NutchConf.get().getFloat("query.phrase.boost", 1.0f);
  private static String DEFAULT_SEARCH_FIELD = null;
  private static String[] DEFAULT_SEARCH_FIELDS = null;
  static{
	  reloadDefaultField() ;
  }
  public static void main(String[] args) throws Exception{
	  XDQueryFilter a = new XDQueryFilter() ;
	  System.out.println(a.parseInit("(+0412)")) ;
  }
  public static void reloadDefaultField(){
	  IndexField[] indexFields = SearchContext.getIndexFieldSet().toArray(new IndexField[SearchContext.getIndexFieldSet().size()]) ;
	  StringBuffer fieldBuffer = new StringBuffer("content,title") ;
	  try{
		  for(IndexField indexField : indexFields)
		  {
			  if(indexField.isStorge())
			  {
				  if(fieldBuffer.length()>0)
					  fieldBuffer.append(",") ;
				  fieldBuffer.append(indexField.getCode()) ;
			  }
		  }
		  DEFAULT_SEARCH_FIELDS = fieldBuffer.toString().split(",") ;
	  }catch(Exception ex){}

	  DEFAULT_SEARCH_FIELD = fieldBuffer.toString() ;
  }
  public BooleanQuery filter(Query input, BooleanQuery output) throws
      QueryException {
//    BooleanQuery base = parse(input);
//    base = addSloppyPhrases(base);
    return parse(input,output);
  }
  private java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("([\\S]{1,})") ;
  /**
   * 2009-8-26日添加，修正无法查询关键词的功能
   * @param query
   * @return
   * @throws Exception
   */
  private String parseInit(String query) throws Exception{
	  java.util.regex.Matcher matcher = pattern.matcher(query) ;
	  StringBuffer reToken = new StringBuffer() ;
	  StringBuffer tokenBuffer =  null; 

	  while(matcher.find()){
		  if(reToken.length()>0){
			  reToken.append(" ");
		  }
		  if(matcher.groupCount()>=1){
			  String phare = matcher.group(1) ;			  
			  tokenBuffer = new StringBuffer() ;
			  if(phare.equalsIgnoreCase("AND")||phare.equalsIgnoreCase("OR")||phare.equalsIgnoreCase("NOT"))
			  {
				  if(tokenBuffer.length()>0)
					  tokenBuffer.append(" ") ;
				  tokenBuffer.append(phare.toUpperCase()) ;
				  if(tokenBuffer!=null && tokenBuffer.length()>0){
					  reToken.append(tokenBuffer).append(" ") ;
				  }
				  continue ;
			  }
			  if(phare.indexOf(":")>0){
				  if(tokenBuffer.length()>0)
					  tokenBuffer.append(" ") ;
				  tokenBuffer.append(phare) ;
				  if(tokenBuffer!=null && tokenBuffer.length()>0){
					  reToken.append(tokenBuffer).append(" ") ;
				  }
				  continue ;
			  }else
			  {
				  com.xx.platform.core.analyzer.XDChineseTokenizer xdToken = new com.xx.platform.core.analyzer.XDChineseTokenizer(new java.io.StringReader(phare)) ;
				  org.apache.lucene.analysis.Token token = null ;
				  StringBuffer sbTemp=new StringBuffer();
				  int i=0;
				  while((token = xdToken.next())!=null){
					  if(i>0&&i<2){
						  sbTemp.insert(0, "(");
					  }
					  i++;
					  if(sbTemp.length()>0 && !(token.term().equals(" ") || token.term().trim().equals("(") || token.term().trim().equals(")") || token.term().trim().equals("+") || token.term().trim().equals("+") || token.term().trim().equals("-") || token.term().trim().equals("\\*") || token.term().trim().equals("\\?"))){
						  sbTemp.append(" AND ") ;
					  }
					  String term=token.term();
					  sbTemp.append(term) ;
				  }
				  if(i>1){
					  sbTemp.append(")").append(" ");
				  }
				  tokenBuffer.append(sbTemp);
				  if(tokenBuffer!=null && tokenBuffer.length()>0){
					  reToken.append(tokenBuffer).append(" ") ;
				  }
			  }
			  
		  }
	  }
	  return query;
  }
  /**
   * 解析query
   * @param input Query
   * @return BooleanQuery
   */
  private BooleanQuery parse(Query input,BooleanQuery output) {
    QueryParser queryParse;
    
//    if (input.getXdht() == null || input.getXdht().trim().length() < 1)
//      output.add(new org.apache.lucene.search.TermQuery(new org.apache.lucene.index.Term("content", "")),false,false);
    queryParse = new MultiFieldQueryParser(DEFAULT_SEARCH_FIELDS,
                                 new org.apache.lucene.analysis.standard.StandardAnalyzer());
    try {
    	if(SearchContext.getXdtechsite()!=null && SearchContext.getXdtechsite().getProxy()!=null && SearchContext.getXdtechsite().getProxy().equals("1"))
    		output.add(queryParse.parse(input.getXdht()),true,false);
    	else
    		output.add(queryParse.parse(parseInit(input.getXdht())),true,false);
    }
    catch (Exception ex) {
//      output.add(new org.apache.lucene.search.TermQuery(new org.apache.lucene.index.Term("content", "")),false,false);
    }
    return output;
  }
}
