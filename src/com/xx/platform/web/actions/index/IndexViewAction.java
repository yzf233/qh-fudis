package com.xx.platform.web.actions.index;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermPositions;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.nutch.fetcher.Fetcher;
import org.apache.nutch.fetcher.IndexFetcher;
import org.apache.nutch.fs.NutchFileSystem;
import org.apache.nutch.util.NutchConf;


import com.opensymphony.xwork2.Action;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.StringUtils;
import com.xx.platform.core.db.BerkeleyDB;
import com.xx.platform.core.nutch.IndexField;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.util.tools.GrowableStringArray;
import com.xx.platform.web.actions.BaseAction;
/**
 * 索引查看action
 * @author 胡俊
 *
 */
public class IndexViewAction extends BaseAction {
	private List fieldlist;
	public static long NUMTERMS=0;
	private static int MAXWORDS = NutchConf.get().getInt("default.indexview.max",10000);//最多能够显示的字数，默认10000。
	private static boolean ismerger=false;
	/***
	 * 跟据用户传入的索引编号，来得到这个编号的索引字段的基本信息-胡俊
	 * @return
	 * @throws Exception
	 */
	public String getIndex() throws Exception {
		fieldlist=new ArrayList();
		int docNum=0;
		try
		{
	    docNum=Integer.valueOf(request.getParameter("id"));
		}
		catch(Exception e)
		{
			request.setAttribute("message","文档编号非法！");
			return Action.INPUT;
		}
	    File file = new File(SearchContext.search_dir + File.separator +
        "index");
	    if(!file.exists())
	    {
	    	request.setAttribute("message", "索引文件不存在！");
	    	return Action.INPUT;
	    }
	    Directory directory = FSDirectory.getDirectory(file, false);
        IndexReader reader = IndexReader.open(directory);
        Collection fn=reader.getFieldNames(FieldOption.ALL);
        request.setAttribute("termsnum", String.valueOf(NUMTERMS));//term数量-胡俊
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        if (fn.size() == 0) 
        	request.setAttribute("fieldsnum", 0);//索引字段总数-胡俊
        else
        	request.setAttribute("fieldsnum", fn.size());
        request.setAttribute("lastModified", format.format(IndexReader.lastModified(directory)));//索引最后更新时间-胡俊
        request.setAttribute("indexfilepath",SearchContext.search_dir + File.separator + "index");//索引路径-胡俊
        request.setAttribute("indexfilesize",getfilesize(SearchContext.search_dir + File.separator + "index"));//索引空间大小-胡俊
        request.setAttribute("indexfilenum",getfilenum(SearchContext.search_dir + File.separator + "index"));//索引文件个数-胡俊
        request.setAttribute("indexfilemax",getFileMaxSize(SearchContext.search_dir + File.separator + "index"));//索引文件个数-胡俊
	    
        List fls=new ArrayList();
        request.setAttribute("maxIndex", String.valueOf(reader.maxDoc()));//索引文档编号总数
        request.setAttribute("maxnum",String.valueOf(reader.maxDoc()));//索引文档总数-胡俊
        try
        {
        fls=reader.document(docNum).getFields();
        }
        catch(Exception e)
        {
        	request.setAttribute("message", "<font color=red>不存在此编号索引！</font>");
	    	return Action.SUCCESS;
        }
        

        TreeSet<String> tfields = new TreeSet<String>(fn);
        String[] idxFields = (String[])tfields.toArray(new String[tfields.size()]);
        if(idxFields==null||idxFields.length==0)
        {
        	reader.close();
	    	request.setAttribute("message", "索引字段为空！");
	    	return Action.SUCCESS;
        }
        String docNo=reader.document(docNum).get("docNo");
        if(BerkeleyDB.getDelDoc(String.valueOf(docNum))!=null)
        {
        	request.setAttribute("message", "<font color=red>不存在此编号索引！</font>");
	    	return Action.SUCCESS;
        }
        for(int i=0;i<idxFields.length;i++)//遍历编号为docNum的索引所有字段-胡俊
        {
        	String [] fields=new String [3];
        	fields[0]=idxFields[i];
        	String fieldvalue="";
        	if(StringUtils.isEncryptField(idxFields[i]))
        		fieldvalue=StringUtils.decrypt(BerkeleyDB.getRecord(docNo)!=null?BerkeleyDB.getRecord(docNo).getProperty(idxFields[i]):"");
        	else
        		fieldvalue=BerkeleyDB.getRecord(docNo)!=null?BerkeleyDB.getRecord(docNo).getProperty(idxFields[i]):"";	
        	fieldvalue=fieldvalue==null?"":fieldvalue.trim();
        	fields[1]=fieldvalue.length()>25?fieldvalue.substring(0,25)+"...":fieldvalue;
        	String tokenize="false";
        	if(fields[0].equals("content"))
        		tokenize="true";
        	if(fields[0].equals("title"))
        		tokenize="true";
        	Set<IndexField> index=SearchContext.getIndexFieldSet();
        	for(IndexField f:index)
        	{
        		if(f.getCode().equals(idxFields[i]))
        			tokenize=f.isToken()?"true":"false";
        	}
        	fields[2]=tokenize;
        	fieldlist.add(fields);
        }
        
        if(NUMTERMS==0)//如果第一次加载，初始化NUMTERMS
        {
        TermEnum te = reader.terms();
        while (te.next())
        	NUMTERMS++;
        te.close();
        }
        

        reader.close();
		return Action.SUCCESS;
	}
	public String refreshTermsnum() throws Exception 
	{
		fieldlist=new ArrayList();
		int docNum=0;
		try
		{
	    docNum=Integer.valueOf(request.getParameter("id"));
		}
		catch(Exception e)
		{
			request.setAttribute("message","文档编号非法！");
			return Action.INPUT;
		}
	    File file = new File(SearchContext.search_dir + File.separator +
        "index");
	    if(!file.exists())
	    {
	    	request.setAttribute("message", "索引文件不存在！");
	    	return Action.INPUT;
	    }
	    
	    Directory directory = FSDirectory.getDirectory(file, false);
        IndexReader reader = IndexReader.open(directory);
        List fls=new ArrayList();
        Collection fn=reader.getFieldNames(FieldOption.ALL);
        request.setAttribute("termsnum", String.valueOf(NUMTERMS));//term数量-胡俊
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        if (fn.size() == 0) 
        	request.setAttribute("fieldsnum", 0);//索引字段总数-胡俊
        else
        	request.setAttribute("fieldsnum", fn.size());
        request.setAttribute("lastModified", format.format(IndexReader.lastModified(directory)));//索引最后更新时间-胡俊
        request.setAttribute("indexfilepath",SearchContext.search_dir + File.separator + "index");//索引路径-胡俊
        request.setAttribute("indexfilesize",getfilesize(SearchContext.search_dir + File.separator + "index"));//索引空间大小-胡俊
        request.setAttribute("indexfilenum",getfilenum(SearchContext.search_dir + File.separator + "index"));//索引文件个数-胡俊
        request.setAttribute("indexfilemax",getFileMaxSize(SearchContext.search_dir + File.separator + "index"));//索引文件个数-胡俊
        request.setAttribute("maxIndex", String.valueOf(reader.maxDoc()));//索引文档编号总数
        request.setAttribute("maxnum",String.valueOf(reader.maxDoc()));//索引文档总数-胡俊
        try
        {
        fls=reader.document(docNum).getFields();
        }
        catch(Exception e)
        {
        	reader.close();
        	request.setAttribute("message", "<font color=red>不存在此编号索引！</font>");
	    	return Action.SUCCESS;
        }
        
        TreeSet<String> tfields = new TreeSet<String>(fn);
        String[] idxFields = (String[])tfields.toArray(new String[tfields.size()]);
        if(idxFields==null||idxFields.length==0)
        {
	    	request.setAttribute("message", "索引字段为空！");
	    	return Action.SUCCESS;
        }
        String docNo=reader.document(docNum).get("docNo");
        if(BerkeleyDB.getDelDoc(String.valueOf(docNum))!=null)
        {
        	request.setAttribute("message", "<font color=red>不存在此编号索引！</font>");
	    	return Action.SUCCESS;
        }
        for(int i=0;i<idxFields.length;i++)//遍历编号为docNum的索引所有字段-胡俊
        {
        	String [] fields=new String [3];
        	fields[0]=idxFields[i];
        	
        	String fieldvalue="";
        	if(StringUtils.isEncryptField(idxFields[i]))
        		fieldvalue=StringUtils.decrypt(BerkeleyDB.getRecord(docNo)!=null?BerkeleyDB.getRecord(docNo).getProperty(idxFields[i]):"");
        	else
        		fieldvalue=BerkeleyDB.getRecord(docNo)!=null?BerkeleyDB.getRecord(docNo).getProperty(idxFields[i]):"";	
        	fieldvalue=fieldvalue==null?"":fieldvalue.trim();
        	fields[1]=fieldvalue.length()>25?fieldvalue.substring(0,25)+"...":fieldvalue;
        	String tokenize="false";
        	if(fields[0].equals("content"))
        		tokenize="true";
        	Set<IndexField> index=SearchContext.getIndexFieldSet();
        	for(IndexField f:index)
        	{
        		if(f.getCode().equals(idxFields[i]))
        			tokenize=f.isToken()?"true":"false";
        	}
        	fields[2]=tokenize;
        	fieldlist.add(fields);
        }
        NUMTERMS=0;
        TermEnum te = reader.terms();
        while (te.next())
        	NUMTERMS++;
        te.close();
        

        reader.close();
		return Action.SUCCESS;
	}
	/***
	 * 得到某个索引的详细内容
	 * @return
	 * @throws Exception
	 */
	public String getIndexToken() throws Exception
	{
		fieldlist=new ArrayList();
		int docNum=0;
		try
		{
	    docNum=Integer.valueOf(request.getParameter("id"));
		}
		catch(Exception e)
		{
			request.setAttribute("message","文档编号非法！");
			return Action.INPUT;
		}
	    File file = new File(SearchContext.search_dir + File.separator +
        "index");
	    if(!file.exists())
	    {
	    	request.setAttribute("message", "索引文件不存在！");
	    	return Action.INPUT;
	    }
	    Directory directory = FSDirectory.getDirectory(file, false);
        IndexReader reader = IndexReader.open(directory);
        List fls=new ArrayList();
        try
        {
        fls=reader.document(docNum).getFields();
        }
        catch(Exception e)
        {
        	reader.close();
        	request.setAttribute("message", "不存在此编号索引！");
	    	return Action.INPUT;
        }
        Collection fn=reader.getFieldNames(FieldOption.ALL);
        TreeSet<String> tfields = new TreeSet<String>(fn);
        String[] idxFields = (String[])tfields.toArray(new String[tfields.size()]);
        if(idxFields==null||idxFields.length==0)
        {
	    	request.setAttribute("message", "索引字段为空！");
	    	return Action.INPUT;
        }
        String docNo=reader.document(docNum).get("docNo");
        if(BerkeleyDB.getDelDoc(String.valueOf(docNum))!=null)
        {
        	request.setAttribute("message", "<font color=red>不存在此编号索引！</font>");
	    	return Action.INPUT;
        }
        for(int i=0;i<idxFields.length;i++)//遍历编号为docNum的索引所有字段-胡俊
        {
        	String term = null;
        	String [] fields=new String [3];
        	fields[0]=idxFields[i];
        	
        	String tkvalue="";
        	if(StringUtils.isEncryptField(idxFields[i]))
        		tkvalue=StringUtils.decrypt(BerkeleyDB.getRecord(docNo)!=null?BerkeleyDB.getRecord(docNo).getProperty(idxFields[i]):"");
        	else
        		tkvalue=BerkeleyDB.getRecord(docNo)!=null?BerkeleyDB.getRecord(docNo).getProperty(idxFields[i]):"";	
        	tkvalue=tkvalue==null?"":tkvalue.trim();
        	fields[1]=tkvalue.length()>MAXWORDS?tkvalue.substring(0,MAXWORDS-1)+"......由于长度太长，无法全部显示(全部字符"+tkvalue.length()+"个)。":tkvalue;
        	TermPositions tp = reader.termPositions();
        	TermEnum te = reader.terms(new Term(idxFields[i], ""));
        	String tokenString="";
        	if (te == null || te.term()==null||te.term().field()==null||!te.term().field().equals(idxFields[i])) {
        		fields[2]=tokenString;
        		fieldlist.add(fields);
                continue;
              }
        	GrowableStringArray gsa=new GrowableStringArray();
            do {
                if (!te.term().field().equals(idxFields[i])) {
                  // end of terms in this field
                  break;
                }
                tp.seek(te.term());
                if (!tp.skipTo(docNum) || tp.doc() != docNum) {
                  // this term is not found in the doc
                  continue;
                }
                term = te.term().text();
                if(StringUtils.isEncryptField(idxFields[i]))
                	term=StringUtils.decrypt(term);
                for (int k = 0; k < tp.freq(); k++) {
                    int pos = tp.nextPosition();
                    gsa.append(pos, "|", term);
                  }
              } while (te.next());
              te.close();
              String tokenstring=gsa.toString(",");
              fields[2]=tokenstring.length()>MAXWORDS?tokenstring.substring(0, MAXWORDS-1)+"......由于长度太长，无法全部显示(全部词数"+tokenstring.length()+"个)。":tokenstring;
        	fieldlist.add(fields);
        }
        reader.close();
		return Action.SUCCESS;
	}
	/**
	 * 得到当前索引文件中索引的总数-胡俊
	 * @return
	 * @throws Exception
	 */
	public String getIndexMaxNum() throws Exception {
		request.setAttribute("maxnum", RuntimeDataCollect.getIndex_rec_num());
		return Action.SUCCESS;
	}
	/**
	 * 根据索引编号，取得这个编号的详细内容，包括分词以后的内容-胡俊
	 * @return
	 * @throws Exception
	 */
	public String getIndexData() throws Exception {
		int docno=Integer.valueOf(request.getParameter("id"));
		return Action.SUCCESS;
	}
	/**
	 * 获得文件夹下单个文件最大的空间容量
	 * @param path
	 */
	public static String getFileMaxSize(String path)
	{
		long file_max_size=0;
		String String_file_max_size="0";
		File file = new File(path) ;
		if(file.exists())
        {
           for(File f:file.listFiles())
           {
        	   if(f.length()>file_max_size)
        		   file_max_size=f.length();
           }
        }
		if (file_max_size >= 1024 && file_max_size < (1024 * 1024)) {
			String_file_max_size = String.valueOf(file_max_size /
                    (1024)) + "K";
        } else if (file_max_size >= (1024 * 1024) &&
        		file_max_size < (1024 * 1024 * 1024)) {
        	String_file_max_size = String.valueOf(file_max_size /
                    (1024 * 1024)) + "M";
        } else if (file_max_size >= (1024 * 1024 * 1024)) {
        	String_file_max_size = String.valueOf(file_max_size /
                    (1024 * 1024 * 1024)) + "G";
        } else {
        	String_file_max_size = String.valueOf(file_max_size) +
                                     "B";
        }
		return String_file_max_size;
		
	}
	/**
	 * 获得文件夹占空间大小
	 * @param path
	 * @return
	 */
	public static String getfilesize(String path)
	{
		long index_file_size=0;
		String index_file_size_string="0";
		File file = new File(path) ;
		if(file.exists())
        {
            index_file_size = getSize(file);
            if (index_file_size >= 1024 && index_file_size < (1024 * 1024)) {
                index_file_size_string = String.valueOf(index_file_size /
                        (1024)) + "K";
            } else if (index_file_size >= (1024 * 1024) &&
                       index_file_size < (1024 * 1024 * 1024)) {
                index_file_size_string = String.valueOf(index_file_size /
                        (1024 * 1024)) + "M";
            } else if (index_file_size >= (1024 * 1024 * 1024)) {
                index_file_size_string = String.valueOf(index_file_size /
                        (1024 * 1024 * 1024)) + "G";
            } else {
                index_file_size_string = String.valueOf(index_file_size) +
                                         "B";
            }
        }
		return index_file_size_string;
	}
	public static long getfilenum(String path)
	{
		long num=0;
		File file = new File(path) ;
		if(file.exists())
        {
			num=file.listFiles().length;
        }
		return num;
	}
    public static long getSize(Object path) {
        if (path == null)
            return 0;
        File file = (path instanceof String) ? new File((String) path) :
                    (File) path;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            long sum = 0;
            for (int i = 0; i < files.length; ++i)
                sum += files[i].isDirectory() ? getSize(files[i]) :
                        files[i].length();
            return sum;
        } else
            return file.length();
    }
    /**
     * 索引合并
     * @throws Exception
     */
    public String merger() throws Exception {
	    if(ismerger)//如果已经调用合并接口，那么直接返回。
	    	return  Action.SUCCESS;
	    ismerger=true;
		Thread t =new Thread(new Runnable() {
			public void run(){
				try {
					IndexFetcher.commit(true);
					ismerger=false;
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					ismerger=false;
				}
			}
		    }
			);
			t.start();
        return Action.SUCCESS;
    }
	public List getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(List fieldlist) {
		this.fieldlist = fieldlist;
	}

}
