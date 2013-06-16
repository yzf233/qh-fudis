package com.xx.platform.core.nutch;

import java.io.*;
import java.util.logging.Level;

import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;

public class TestSpeed {
	
	public static void main(String[] args) {
		StringBuffer strb = new StringBuffer();
		org.apache.lucene.index.IndexWriter writer = null;
		long start = System.currentTimeMillis();
		InputStream input = null;
		try {
			writer = new IndexWriter("D:/index",
					new org.apache.lucene.analysis.standard.StandardAnalyzer(),true , IndexWriter.MaxFieldLength.UNLIMITED);
			writer.setMergeFactor(10000);
			writer.setRAMBufferSizeMB(10000);
			writer.setMaxMergeDocs(1000000000);
			writer.setRAMBufferSizeMB(128);
			writer.setTermIndexInterval(128);
			writer.setMaxFieldLength(100000000);
			writer.setUseCompoundFile(true);
			
			int i = 0;
			input = new FileInputStream("e:/test.txt");
			
			byte[] data = new byte[1024];
			int length = 0;
			while ((length = input.read(data)) > 0) {
				strb.append(new String(data, 0, length));
			}

			ThreadGroup group = new ThreadGroup("index"); // our group
			System.out.println("次数:"+i+" 耗费时间一："
					+ (System.currentTimeMillis() - start));
			while (i < 200) 
			{
				TestThread tt = new TestThread(writer , strb , "index"+i,group) ;
				tt.start() ;			
				i++ ;
				while(group.activeCount()>=2)
					Thread.sleep(1000) ;
			}
			
			 while (true) {
		            Thread.sleep(1000);
		            int n = group.activeCount();
		            Thread[] list = new Thread[n];
		            group.enumerate(list);

//		            System.out.println("还有"+list.length+"个线程在执行中") ;
		            boolean noMoreFetcherThread = true; // assumption
		            for (int j = 0; j < n; j++) {
		                // this thread may have gone away in the meantime
		                if (list[j] == null)
		                    continue;

		                String tname = list[j].getName();
		                if (tname.startsWith("index")) // prove it
		                    noMoreFetcherThread = false;
		               
		            }
		            if (noMoreFetcherThread) {
		               break;
		            }
		        }
			 
		} catch (Exception e) {
			e.printStackTrace() ;
		} finally {
			try {
				if (input != null)
					input.close();
				if (writer != null)		
					writer.close();
//					while(true)
//						 Thread.sleep(1000) ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("times ：" + (System.currentTimeMillis() - start));
	}
	
	
}

class TestThread extends Thread
{
	private org.apache.lucene.index.IndexWriter writer = null;
	private StringBuffer strb = null ;
	TestThread(org.apache.lucene.index.IndexWriter writer , StringBuffer strb, String name , ThreadGroup group)
	{
		super(group,name);
		this.writer = writer ; 
		this.strb = strb ;
	}
	
	public void run() {
		System.out.println("start thread："+this.currentThread().getName()) ;
		
//		for(int j = 0 ; j<10 ; j++)
		{
			long start = System.currentTimeMillis() ;
			Document doc = new Document();
			start = System.currentTimeMillis();
			doc.add(new Field("path", strb.toString(), Field.Store.NO,
					Field.Index.ANALYZED));
			try {
				writer.addDocument(doc);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			System.out.println("thread name："+this.currentThread().getName()+ " times2："
					+ (System.currentTimeMillis() - start));
			start = System.currentTimeMillis();
		}
	}
	
	
}

