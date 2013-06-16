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
 * �����鿴action
 * @author ����
 *
 */
public class IndexViewAction extends BaseAction {
	private List fieldlist;
	public static long NUMTERMS=0;
	private static int MAXWORDS = NutchConf.get().getInt("default.indexview.max",10000);//����ܹ���ʾ��������Ĭ��10000��
	private static boolean ismerger=false;
	/***
	 * �����û������������ţ����õ������ŵ������ֶεĻ�����Ϣ-����
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
			request.setAttribute("message","�ĵ���ŷǷ���");
			return Action.INPUT;
		}
	    File file = new File(SearchContext.search_dir + File.separator +
        "index");
	    if(!file.exists())
	    {
	    	request.setAttribute("message", "�����ļ������ڣ�");
	    	return Action.INPUT;
	    }
	    Directory directory = FSDirectory.getDirectory(file, false);
        IndexReader reader = IndexReader.open(directory);
        Collection fn=reader.getFieldNames(FieldOption.ALL);
        request.setAttribute("termsnum", String.valueOf(NUMTERMS));//term����-����
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        if (fn.size() == 0) 
        	request.setAttribute("fieldsnum", 0);//�����ֶ�����-����
        else
        	request.setAttribute("fieldsnum", fn.size());
        request.setAttribute("lastModified", format.format(IndexReader.lastModified(directory)));//����������ʱ��-����
        request.setAttribute("indexfilepath",SearchContext.search_dir + File.separator + "index");//����·��-����
        request.setAttribute("indexfilesize",getfilesize(SearchContext.search_dir + File.separator + "index"));//�����ռ��С-����
        request.setAttribute("indexfilenum",getfilenum(SearchContext.search_dir + File.separator + "index"));//�����ļ�����-����
        request.setAttribute("indexfilemax",getFileMaxSize(SearchContext.search_dir + File.separator + "index"));//�����ļ�����-����
	    
        List fls=new ArrayList();
        request.setAttribute("maxIndex", String.valueOf(reader.maxDoc()));//�����ĵ��������
        request.setAttribute("maxnum",String.valueOf(reader.maxDoc()));//�����ĵ�����-����
        try
        {
        fls=reader.document(docNum).getFields();
        }
        catch(Exception e)
        {
        	request.setAttribute("message", "<font color=red>�����ڴ˱��������</font>");
	    	return Action.SUCCESS;
        }
        

        TreeSet<String> tfields = new TreeSet<String>(fn);
        String[] idxFields = (String[])tfields.toArray(new String[tfields.size()]);
        if(idxFields==null||idxFields.length==0)
        {
        	reader.close();
	    	request.setAttribute("message", "�����ֶ�Ϊ�գ�");
	    	return Action.SUCCESS;
        }
        String docNo=reader.document(docNum).get("docNo");
        if(BerkeleyDB.getDelDoc(String.valueOf(docNum))!=null)
        {
        	request.setAttribute("message", "<font color=red>�����ڴ˱��������</font>");
	    	return Action.SUCCESS;
        }
        for(int i=0;i<idxFields.length;i++)//�������ΪdocNum�����������ֶ�-����
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
        
        if(NUMTERMS==0)//�����һ�μ��أ���ʼ��NUMTERMS
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
			request.setAttribute("message","�ĵ���ŷǷ���");
			return Action.INPUT;
		}
	    File file = new File(SearchContext.search_dir + File.separator +
        "index");
	    if(!file.exists())
	    {
	    	request.setAttribute("message", "�����ļ������ڣ�");
	    	return Action.INPUT;
	    }
	    
	    Directory directory = FSDirectory.getDirectory(file, false);
        IndexReader reader = IndexReader.open(directory);
        List fls=new ArrayList();
        Collection fn=reader.getFieldNames(FieldOption.ALL);
        request.setAttribute("termsnum", String.valueOf(NUMTERMS));//term����-����
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        if (fn.size() == 0) 
        	request.setAttribute("fieldsnum", 0);//�����ֶ�����-����
        else
        	request.setAttribute("fieldsnum", fn.size());
        request.setAttribute("lastModified", format.format(IndexReader.lastModified(directory)));//����������ʱ��-����
        request.setAttribute("indexfilepath",SearchContext.search_dir + File.separator + "index");//����·��-����
        request.setAttribute("indexfilesize",getfilesize(SearchContext.search_dir + File.separator + "index"));//�����ռ��С-����
        request.setAttribute("indexfilenum",getfilenum(SearchContext.search_dir + File.separator + "index"));//�����ļ�����-����
        request.setAttribute("indexfilemax",getFileMaxSize(SearchContext.search_dir + File.separator + "index"));//�����ļ�����-����
        request.setAttribute("maxIndex", String.valueOf(reader.maxDoc()));//�����ĵ��������
        request.setAttribute("maxnum",String.valueOf(reader.maxDoc()));//�����ĵ�����-����
        try
        {
        fls=reader.document(docNum).getFields();
        }
        catch(Exception e)
        {
        	reader.close();
        	request.setAttribute("message", "<font color=red>�����ڴ˱��������</font>");
	    	return Action.SUCCESS;
        }
        
        TreeSet<String> tfields = new TreeSet<String>(fn);
        String[] idxFields = (String[])tfields.toArray(new String[tfields.size()]);
        if(idxFields==null||idxFields.length==0)
        {
	    	request.setAttribute("message", "�����ֶ�Ϊ�գ�");
	    	return Action.SUCCESS;
        }
        String docNo=reader.document(docNum).get("docNo");
        if(BerkeleyDB.getDelDoc(String.valueOf(docNum))!=null)
        {
        	request.setAttribute("message", "<font color=red>�����ڴ˱��������</font>");
	    	return Action.SUCCESS;
        }
        for(int i=0;i<idxFields.length;i++)//�������ΪdocNum�����������ֶ�-����
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
	 * �õ�ĳ����������ϸ����
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
			request.setAttribute("message","�ĵ���ŷǷ���");
			return Action.INPUT;
		}
	    File file = new File(SearchContext.search_dir + File.separator +
        "index");
	    if(!file.exists())
	    {
	    	request.setAttribute("message", "�����ļ������ڣ�");
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
        	request.setAttribute("message", "�����ڴ˱��������");
	    	return Action.INPUT;
        }
        Collection fn=reader.getFieldNames(FieldOption.ALL);
        TreeSet<String> tfields = new TreeSet<String>(fn);
        String[] idxFields = (String[])tfields.toArray(new String[tfields.size()]);
        if(idxFields==null||idxFields.length==0)
        {
	    	request.setAttribute("message", "�����ֶ�Ϊ�գ�");
	    	return Action.INPUT;
        }
        String docNo=reader.document(docNum).get("docNo");
        if(BerkeleyDB.getDelDoc(String.valueOf(docNum))!=null)
        {
        	request.setAttribute("message", "<font color=red>�����ڴ˱��������</font>");
	    	return Action.INPUT;
        }
        for(int i=0;i<idxFields.length;i++)//�������ΪdocNum�����������ֶ�-����
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
        	fields[1]=tkvalue.length()>MAXWORDS?tkvalue.substring(0,MAXWORDS-1)+"......���ڳ���̫�����޷�ȫ����ʾ(ȫ���ַ�"+tkvalue.length()+"��)��":tkvalue;
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
              fields[2]=tokenstring.length()>MAXWORDS?tokenstring.substring(0, MAXWORDS-1)+"......���ڳ���̫�����޷�ȫ����ʾ(ȫ������"+tokenstring.length()+"��)��":tokenstring;
        	fieldlist.add(fields);
        }
        reader.close();
		return Action.SUCCESS;
	}
	/**
	 * �õ���ǰ�����ļ�������������-����
	 * @return
	 * @throws Exception
	 */
	public String getIndexMaxNum() throws Exception {
		request.setAttribute("maxnum", RuntimeDataCollect.getIndex_rec_num());
		return Action.SUCCESS;
	}
	/**
	 * ����������ţ�ȡ�������ŵ���ϸ���ݣ������ִ��Ժ������-����
	 * @return
	 * @throws Exception
	 */
	public String getIndexData() throws Exception {
		int docno=Integer.valueOf(request.getParameter("id"));
		return Action.SUCCESS;
	}
	/**
	 * ����ļ����µ����ļ����Ŀռ�����
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
	 * ����ļ���ռ�ռ��С
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
     * �����ϲ�
     * @throws Exception
     */
    public String merger() throws Exception {
	    if(ismerger)//����Ѿ����úϲ��ӿڣ���ôֱ�ӷ��ء�
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
