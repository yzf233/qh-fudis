package com.xx.platform.core.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

import com.xx.platform.core.PYContext;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.IndexField;

public class UserQueryConvert {
	public UserQueryConvert(){};
	public String ConvertUserQueryToString(UserQuery [] urs)
	{
		String str="";
		for(UserQuery ur:urs)
		{
			String [][] reqs=ur.getRequireKey();//获取必须包含条件
			String reqstr="";
			try
			{
				for(String [] s:reqs)
				{
//				   if(s[2].equals("true"))
//				   {
//				    Token[] tokens = getTokens(s[0],s[1]);
//				    for (Token token : tokens) {
//				      reqstr+="+"+strfilter(s[0],"false")+":"+strfilter(token.termText(),"false")+" ";
//				    }
//				   }
//				   else
//				   {
					   if(s[2].equals("range")&&isRangeSearch(s[1]))
						   reqstr+="+"+strfilter(s[0],"false")+":"+s[1]+" ";
					   else if(s[2].equals("like"))
						   reqstr+="+"+strfilter(s[0],"false")+":"+strfilter(s[1],s[2])+"* ";
					   else if(s[2].equals("search"))
						   reqstr+="+"+strfilter(s[0],"false")+":("+strfilter(strSpacefilter(s[0],s[1]),"true")+") "; 
					   else if(s[2].equals("searchReq"))
						   reqstr+="+"+strfilter(s[0],"false")+":("+strReqSpacefilter(s[0],s[1])+") "; 
					   else if(s[2].equals("pinyin"))
						   reqstr+="+"+strfilter(s[0],"false")+":("+pyfilter(s[0],s[1])+") ";
					   else
						   reqstr+="+"+strfilter(s[0],"false")+":("+strfilter(s[1],s[2])+") ";
//				   }
				}
			}
			catch(Exception e)
			{
				reqstr="";//解析有误
			}	
			str+=reqstr;
		
			String [][] pros=ur.getProhibitKey();//获取必须不包含条件
			String prostr="";
			try
			{
				for(String [] s:pros)
				{
//				   if(s[2].equals("true"))
//				   {
//				    Token[] tokens = getTokens(s[0],s[1]);
//				    for (Token token : tokens) {
//				    	prostr+="-"+strfilter(s[0],"false")+":"+strfilter(token.termText(),"false")+" ";
//				    }
//				   }
//				   else
//				   {
					   if(s[2].equals("range")&&isRangeSearch(s[1]))
						   prostr+="-"+strfilter(s[0],"false")+":"+s[1]+" ";
					   else if(s[2].equals("like"))
						   prostr+="-"+strfilter(s[0],"false")+":"+strfilter(s[1],s[2])+"* ";
					   else if(s[2].equals("search"))
						   prostr+="-"+strfilter(s[0],"false")+":("+strfilter(strSpacefilter(s[0],s[1]),"true")+") "; 
					   else if(s[2].equals("searchReq"))
						   prostr+="-"+strfilter(s[0],"false")+":("+strReqSpacefilter(s[0],s[1])+") "; 
					   else if(s[2].equals("pinyin"))
						   prostr+="-"+strfilter(s[0],"false")+":("+pyfilter(s[0],s[1])+") ";
					   else
						   prostr+="-"+strfilter(s[0],"false")+":("+strfilter(s[1],s[2])+") ";   
//				   }
				}
			}
			catch(Exception e)
			{
				prostr="";//解析有误
			}
			str+=prostr;
		
			String [][] reqonemore=ur.getRequireOneMoreKey();//获取包含一个或更多
			String reqonemorestr="+(";
			try
			{
				for(String [] s:reqonemore)
				{
//				   if(s[2].equals("true"))
//				   {
//				    Token[] tokens = getTokens(s[0],s[1]);
//				    for (Token token : tokens) {
//				    	reqonemorestr+=strfilter(s[0],"false")+":"+strfilter(token.termText(),"false")+" ";
//				    }
//				   }
//				   else
//				   {
					   if(s[2].equals("range")&&isRangeSearch(s[1]))
						   reqonemorestr+=strfilter(s[0],"false")+":"+s[1]+" ";
					   else if(s[2].equals("like"))
						   reqonemorestr+=strfilter(s[0],"false")+":"+strfilter(s[1],s[2])+"* ";
					   else if(s[2].equals("search"))
						   reqonemorestr+=strfilter(s[0],"false")+":("+strfilter(strSpacefilter(s[0],s[1]),"true")+") "; 
					   else if(s[2].equals("searchReq"))
						   reqonemorestr+=strfilter(s[0],"false")+":("+strReqSpacefilter(s[0],s[1])+") "; 
					   else if(s[2].equals("pinyin"))
						   reqonemorestr+=strfilter(s[0],"false")+":("+pyfilter(s[0],s[1])+") ";
					   else
						   reqonemorestr+=strfilter(s[0],"false")+":("+strfilter(s[1],s[2])+") ";	   
//				   }
				}
			}
			catch(Exception e)
			{
				reqonemorestr="+(";//解析有误
			}
			reqonemorestr+=") ";
			if(!reqonemorestr.equals("+() "))
				str+=reqonemorestr;
			
			
		
			String [][] proonemore=ur.getProhibitOneMoreKey();//获取包含一个或更多
			String proonemorestr="-(";
			try
			{
				for(String [] s:proonemore)
				{
//				   if(s[2].equals("true"))
//				   {
//				    Token[] tokens = getTokens(s[0],s[1]);
//				    for (Token token : tokens) {
//				    	proonemorestr+=strfilter(s[0],"false")+":"+strfilter(token.termText(),"false")+" ";
//				    }
//				   }
//				   else
//				   {
					   if(s[2].equals("range")&&isRangeSearch(s[1]))
						   proonemorestr+=strfilter(s[0],"false")+":"+s[1]+" ";
					   else if(s[2].equals("like"))
						   proonemorestr+=strfilter(s[0],"false")+":"+strfilter(s[1],s[2])+"* ";
					   else if(s[2].equals("search"))
						   proonemorestr+=strfilter(s[0],"false")+":("+strfilter(strSpacefilter(s[0],s[1]),"true")+") "; 
					   else if(s[2].equals("searchReq"))
						   proonemorestr+=strfilter(s[0],"false")+":("+strReqSpacefilter(s[0],s[1])+") "; 
					   else if(s[2].equals("pinyin"))
						   proonemorestr+=strfilter(s[0],"false")+":("+pyfilter(s[0],s[1])+") ";
					   else
						   proonemorestr+=strfilter(s[0],"false")+":("+strfilter(s[1],s[2])+") "; 
//				   }
				}
			}
			catch(Exception e)
			{
				proonemorestr="-(";//解析有误
			}
			proonemorestr+=") ";
			if(!proonemorestr.equals("-() "))
				str+=proonemorestr;
			
			if(ur.getRequireComplexKey()!=null&&ur.getRequireComplexKey().length>0)
			{
				String complexmorestr="+(";
				UserQuery user[]=ur.getRequireComplexKey();
				for(UserQuery u:user)
				{
					complexmorestr+="("+ConvertUserQueryToString(new UserQuery []{u})+")";
				}
				complexmorestr+=")";
				if(!complexmorestr.equals("+()"))
					str+=complexmorestr;
			}
			if(ur.getProhibitComplexKey()!=null&&ur.getProhibitComplexKey().length>0)
			{
				String complexmorestr="-(";
				UserQuery user[]=ur.getProhibitComplexKey();
				for(UserQuery u:user)
				{
					complexmorestr+="("+ConvertUserQueryToString(new UserQuery []{u})+")";
				}
				complexmorestr+=")";
				if(!complexmorestr.equals("-()"))
					str+=complexmorestr;
			}
			
			
		}
		return str;
	}
	 private Token[] getTokens(String field,String text) throws Exception {
		    if (text == null || text.trim().equals("")) {
		      return new Token[] {
		          new Token("", 0, 0)};
		    }
        	Set<IndexField> index=SearchContext.getIndexFieldSet();
        	for(IndexField f:index)//自定义字段按照自定义字段定义的分词算法分词。
        	{
        		if(f.getCode().equals(field))
        		{
        			TokenStream ts=SearchContext.getAnalyzer(f.getTokentype()).tokenStream("content", new StringReader(text));
        		    ArrayList result = new ArrayList();
        		    for (Token token = ts.next(); token != null; token = ts.next()) {
        		    	if(!token.termText().trim().equals(""))
        		      result.add(token);
        		    }
        		    return (Token[]) result.toArray(new Token[result.size()]);
        		}
        	}
        	if(field.equals("content"))//content字段按照一元分词
        	{
        		TokenStream ts=SearchContext.getAnalyzer(1).tokenStream("content", new StringReader(text));
    		    ArrayList result = new ArrayList();
    		    for (Token token = ts.next(); token != null; token = ts.next()) {
    		    	if(!token.termText().trim().equals(""))
    		      result.add(token);
    		    }
    		    return (Token[]) result.toArray(new Token[result.size()]);
        	}
        	else
        	{//其他系统字段不分词
        		TokenStream ts=SearchContext.getAnalyzer(0).tokenStream("content", new StringReader(text));
    		    ArrayList result = new ArrayList();
    		    for (Token token = ts.next(); token != null; token = ts.next()) {
    		    	if(!token.termText().trim().equals(""))
    		      result.add(token);
    		    }
    		    return (Token[]) result.toArray(new Token[result.size()]);
        	}
	}
	private  String strfilter(String str,String IgnoreSpace)
	{
		 if(str==null||(str!=null&&str.length()==0))
			 return "";
		 String res="";
		 for(int i=0;i<str.length();i++)
		 {
			 if((str.charAt(i)==' '||str.charAt(i)=='　')&&IgnoreSpace.equals("true"))
				 res+=str.charAt(i);
			 else
//	         res+="\\"+str.charAt(i);
			 res+=str.charAt(i);
		 }
		 return res;
	}
	private static boolean isRangeSearch(String str)
	{
		if(str==null||(str!=null&&str.length()<=2))
			return false;
		if(str.charAt(0)!='[')
			return false;
		if(str.charAt(0)=='[')
		{
			if(str.charAt(str.length()-1)!=']')
				return false;
		}
		String [] words=str.split("\\s+");
		if(words.length!=3)
			return false;
		if(!words[1].equals("TO"))
			return false;
		for(int i=1;i<str.length()-1;i++)
		{
            if(str.charAt(i)=='['||str.charAt(i)==']')
            	return false;
		}
		return true;
	}
	private  String strSpacefilter(String field,String word) throws Exception
	{
//		 if(word==null||(word!=null&&word.length()==0))
//			 return "";
//		 StringBuffer res=new StringBuffer();
//		    Token[] tokens = getTokens(field,word);
//		    for (Token token : tokens) {
//		    	if(!token.termText().trim().equals(""))
//					 res.append(token.termText()+" ");
//		    }
//		 return res.toString();
		return  word;
	}
	private String pyfilter(String field,String word) throws Exception
	{
		 if(word==null||(word!=null&&word.length()==0))
			 return "";
		 StringBuffer res=new StringBuffer();
		 res.append(strfilter(word,"true")).append(" ");
		 List<String> ls=PYContext.find(word);
		 if(ls!=null&&ls.size()>0)
	     {
	    	 for(String s:ls)
	    	 {
	    		 res.append(strfilter(s,"true")).append(" ");
	    	 }
	     }
		 return res.toString();
	}
	private  String strReqSpacefilter(String field,String word) throws Exception
	{
		 if(word==null||(word!=null&&word.length()==0))
			 return "";
		 StringBuffer res=new StringBuffer();
		    Token[] tokens = getTokens(field,word);
		    for (Token token : tokens) {
		    	if(!token.termText().trim().equals(""))
		    		res.append("+"+strfilter(token.termText(),"true")+" ");
		    }
		 return res.toString();
	}
	
	public static void main(String [] arg)
	{
//		String keyworld="下 载";
//        UserQuery u1=new UserQuery();
//        u1.setRequireKey(new String[][]{{"content",keyworld,"false"},{"docType","file","false"}});
//        UserQuery u2=new UserQuery();
//        u2.setRequireOneMoreKey(new String[][]{{"strtitle",keyworld,"true"}});
//        UserQuery u=new UserQuery();
//        u.setRequireComplexKey(new UserQuery[]{u1,u2});
//		UserQueryConvert uc=new UserQueryConvert();
//
//		System.out.println(uc.ConvertUserQueryToString(new UserQuery[]{u}));
		String str="[20061201 TO 20081201]";
		System.out.println(isRangeSearch(str));
	}

	
}
