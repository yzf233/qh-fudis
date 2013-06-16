package com.xx.platform.core.nutch;

import java.io.*;
import java.util.logging.*;

import org.apache.lucene.index.*;
import org.apache.nutch.fetcher.*;
import org.apache.nutch.fs.*;
import org.apache.nutch.indexer.*;
import org.apache.nutch.io.*;
import org.apache.nutch.parse.*;
import org.apache.nutch.protocol.*;
import org.apache.nutch.segment.*;
import org.apache.nutch.segment.SegmentReader;
import org.apache.nutch.util.*;

import com.xx.platform.core.*;

import org.apache.nutch.searcher.NutchBean;

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
public class FixSegmentTool {
  /**
   *  Step 1 :read data from segment dic ;
   *  Step 2 :write data to segment dic ;
   *  Step 3 :delete bad index
   * @param segDir String
   * @return int
   * @throws Exception
   */
  private Content _co = new Content();
  private ParseText _pt = new ParseText();
  private ParseData _pd = new ParseData();
  private FetcherOutput fo = new FetcherOutput();
  private SegmentReader segmentReader;
  private SegmentWriter segmentWriter;
  public ArrayFile.Reader fetcherReader;
  public static final Logger LOG =
            LogFormatter.getLogger("com.xx.platform.core.nutch.FixSegmentTool");
  private void test()
  {
    FileOperate delTool = new FileOperate();
    File oldSegFile = new File("C:\\program\\search\\zhyg\\classes\\org") ;
    File newSegFile = new File("C:\\program\\search\\zhyg\\classes\\com") ;
    delTool.delFolder(oldSegFile);
    newSegFile.renameTo(oldSegFile);

  }
  public static void main(String[] args)
  {
    FixSegmentTool fx = new FixSegmentTool();
    fx.test();
  }
  /**
   *
   * @param segDir String
   * @return int
   */
  public long fix(String segDir) throws Exception{
    RuntimeDataCollect.crawl_status_restore_data = true ;
    LOG.info("�����޸���ʼ......");
    long docNum = 0 ;
    File newSegFile = FetchListTool.createNewSegment() ;
    File oldSegFile = new File(segDir) ;
    File[] dateList = oldSegFile.getParentFile().listFiles() ;
    String fileName = "" ;
    File lastFile = null ;
    for(File file:dateList)
    {
      if(file!=null && file.getName().compareTo(fileName)>0 && !file.getName().equals(newSegFile.getName())  && !file.getName().equals(oldSegFile.getName()))
      {
        fileName = file.getName();
        lastFile = file ;
      }
    }
    NutchFileSystem nfs = NutchFileSystem.get() ;
    if(lastFile!=null)
    {
      File indexFile = new File(lastFile,"index.data");
      if (indexFile.exists()) {
        NFSDataInputStream indexReader = new NFSDataInputStream(nfs.open(indexFile), 1024);
        if (indexReader != null) {
          IndexFetcher.docNum = indexReader.readLong();
          indexReader.close();
          LOG.info("���ݼ�¼��"+IndexFetcher.docNum+"��ʼ�޸�......");
        }
      }
    }

    LongWritable w = new LongWritable(-1);
    try {

      segmentReader = new SegmentReader(oldSegFile, false);
      segmentWriter = new SegmentWriter(newSegFile, false);
      fetcherReader = new ArrayFile.Reader(nfs, new File(oldSegFile.getPath(), FetcherOutput.DIR_NAME).toString());

      try {
        fetcherReader.finalKey(w);
      }
      catch (Exception ex) {
        LOG.info("��Ч���ݹ�"+w.get()+"��");
        RuntimeDataCollect.crawl_restore_num = w.get() ;
      }
      while (segmentReader.next(fo, _co, _pt, _pd)) {
        segmentWriter.append(fo, _co, _pt, _pd);
        docNum++ ;
        RuntimeDataCollect.crawl_restored_num = docNum ;
      }
    }catch (Exception ex) {
      LOG.info("���ݸ��Ƴ��ִ��󣬴���ԭ��:"+ex.getMessage());
      ex.printStackTrace();
    }finally{
      segmentWriter.close();
      segmentReader.close();
      fetcherReader.close();
      NFSDataOutputStream index_dataWriter = new NFSDataOutputStream(nfs.open(new
                    File(newSegFile,"index.data"), false));
      index_dataWriter.writeLong(docNum);
      index_dataWriter.close();
      LOG.info("���ݸ�����ɣ���������"+docNum+"��");
    }
    try {
      LOG.info("��ʼ��������......");
      IndexMerger.main(new String[] {newSegFile.getPath()+File.separator+"index" , segDir});
      long cIndexNum = clearIndex(newSegFile , docNum) ;
      //����
      {
        LOG.info("����������ɣ���������Ч������¼"+cIndexNum+"��");
        {//�ر� Segment
          NutchBean.getBean().closeSegment(oldSegFile.getName());
        }
        FileOperate delTool = new FileOperate();
        delTool.delFolder(oldSegFile);
        File doneFile = new File(newSegFile,
                                 "index.done");
        if (!doneFile.exists()) {
            doneFile.createNewFile();
        }

        Thread.sleep(3000);
        boolean suc = newSegFile.renameTo(oldSegFile) ;
        if(!suc)
        {
          LOG.info("�޸�ʧ�ܣ�ʧ��ԭ���ļ���ȫ������");
          throw new IOException("�ļ���ȫ������");
        }
      }

      IndexMerger.main(new String[]{SearchContext.search_dir+File.separator+"index",oldSegFile.getPath()}) ;
      RuntimeDataCollect.has_new_index = true ;
      LOG.info("�����޸��ɹ����");
    }
    catch (Exception ex) {
      LOG.info("�����޸�ʧ�ܣ�ʧ��ԭ��"+ex.getMessage());

      throw new RuntimeException("�����޸�ʧ�ܣ�ʧ��ԭ��"+ex.getMessage()) ;
    }


    RuntimeDataCollect.crawl_status_restore_data = false ;
    RuntimeDataCollect.crawl_restore_num = 0 ;
    RuntimeDataCollect.crawl_restored_num = 0 ;
    return IndexFetcher.docNum+docNum;
  }
  private int clearIndex(File newSegFile , long docNum) throws Exception
  {
    IndexReader reader = IndexReader.open(newSegFile.getPath() +
                        File.separator + "index");
    int indexDocNum = 1 ;
    int cIndexNum = 0 ;
    while(indexDocNum>0)
    {
      indexDocNum = reader.deleteDocuments(new Term("docNo",
          String.valueOf(docNum++)));
      cIndexNum+=indexDocNum;
    }
    reader.close();
    return cIndexNum ;
  }
  public class FileOperate {

    /**
     * ɾ���ļ�
     * @param filePathAndName String �ļ�·�������� ��c:/fqf.txt
     * @param fileContent String
     * @return boolean
     */
    public void delFile(String filePathAndName) {
      try {
        String filePath = filePathAndName;
        filePath = filePath.toString();
        java.io.File myDelFile = new java.io.File(filePath);
        myDelFile.delete();

      }
      catch (Exception e) {
        System.out.println("ɾ���ļ���������");
        e.printStackTrace();

      }

    }

    /**
     * ɾ���ļ���
     * @param filePathAndName String �ļ���·�������� ��c:/fqf
     * @param fileContent String
     * @return boolean
     */
    public void delFolder(File folderPath) {
      try {
        File[] fileList = folderPath.listFiles() ;
        for(File file:fileList)
        {
          if(file.isDirectory())
            delFolder(file) ;
          else
            file.delete() ;
        }
        folderPath.delete() ;
      }catch (Exception e) {
        e.printStackTrace();
      }

    }
  }
}
