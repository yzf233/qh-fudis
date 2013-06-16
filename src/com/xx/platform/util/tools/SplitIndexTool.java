package com.xx.platform.util.tools;

import java.io.File;

import org.apache.lucene.index.IndexReader;
import org.apache.nutch.fs.LocalFileSystem;
import org.apache.nutch.fs.NutchFileSystem;
import org.apache.nutch.searcher.FetchedSegments;
import org.apache.nutch.io.ArrayFile;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import java.io.IOException;
import org.apache.lucene.index.IndexWriter;
import org.apache.nutch.analysis.NutchDocumentAnalyzer;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.RuntimeDataCollect;

import java.util.Date;
import java.util.logging.Level;
import org.apache.nutch.util.LogFormatter;
import org.apache.nutch.indexer.NutchSimilarity;
import org.apache.nutch.util.NutchConf;
import org.apache.lucene.document.Field;

/**
 * <p>Title: û�д���segments</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SplitIndexTool {

    private static String D_SPLIT = "split";//�����ֽ�󱣴�Ŀ¼
    private String srcD; //����������Ŀ¼������Ŀ¼��
    private int bnum = 1; //��Ҫ�ֽ�ɵ�����
    private long num = 0;

    ///index
    private IndexReader reader;
    private static IndexWriter indexWriter;
    ///segments
//    private FetchedSegments segments;

    private static final int TERM_INDEX_INTERVAL = 128;
//      NutchConf.get().getInt("indexer.termIndexInterval",
//                             IndexWriter.DEFAULT_TERM_INDEX_INTERVAL);
    private static final int MAX_MERGE_DOCS = IndexWriter.DEFAULT_MAX_MERGE_DOCS;
//            NutchConf.get().getInt( "indexer.maxMergeDocs",IndexWriter.DEFAULT_MAX_MERGE_DOCS);
    private static final int maxFieldLength = 10000;
//            NutchConf.get().getInt("indexer.max.tokens",10000);

    public SplitIndexTool(String srcD,int num) {
        this.srcD = srcD;
        this.bnum = num;
        init();
    }

    private void init() {
        try {
//            NutchFileSystem nfs = NutchFileSystem.getXDFS();
            reader = IndexReader.open(srcD+File.separator+"index");
            num = reader.numDocs()/bnum ;
//            segments = new FetchedSegments(new LocalFileSystem(),srcD+File.separator+"segments");
        } catch (Exception ex) {
            System.out.println("");
        }
    }


    private void split() throws CorruptIndexException, IOException {
//        // the output
//        ArrayFile.Writer fetcherWriter;
//        ArrayFile.Writer contentWriter;
//        ArrayFile.Writer parseTextWriter;
//        ArrayFile.Writer parseDataWriter;

        Document doc;
        ///���߳�
        int now = 0 ;
        for(int i=0;i<bnum;i++){
            initIndexWriter(srcD+File.separator+D_SPLIT+File.separator+i);
            for(long j=0;(j<num || (i==(bnum-1)&& now < (reader.numDocs()-1)));j++){
                doc = reader.document(now);
                doc.removeField("docNo");
                doc.add(Field.UnIndexed("docNo", Long.toString(j, 16)));
                indexWriter.addDocument(doc);
                now++;
            }
            indexWriter.optimize();
            indexWriter.close();
            System.out.println("��"+(i+1)+"�������������");
        }
        reader.close();
    }


    public static void main(String[] args) {
        /**�������*/
        if (args.length != 2) {
            System.out.println("Usage: splitindex dir�����ֽ����������Ŀ¼�� splitnum����Ҫ�ֽ�ɵ�������");
            return;
        }
        String srcD = args[0];
        ///������Ŀ¼�Ƿ���ȷ
        File localF = new File(srcD);
        File indexF = new File(localF,"index");
        if(!localF.isDirectory()|| !indexF.isDirectory() ){
            System.out.println("�����Ŀ¼�����ڻ��߲�������Ŀ¼");
            return;
        }
        int num = 1;
        try{
            num = Integer.parseInt(args[1]);
        }catch(java.lang.NumberFormatException e){
            System.out.println("splitnum ��������������");
            return;
        }
        if(num<2){
            System.out.println("�з�����С��2��");
            return;
        }
        ///û�м��splitĿ¼�Ƿ��Ѿ�����

        SplitIndexTool tool = new SplitIndexTool(srcD,num);
        System.out.println("��ʼ�з�����������������Ϊ"+num+"�ݣ�ÿ��Լ��"+tool.num+"�����ݣ�");
        try {
            tool.split();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("�з�������ɣ�");
    }

    public static File srcDir;
    public static File localWorkingDir;
    private void initIndexWriter(String directory) throws
            IOException {
        srcDir = new File(directory);
        localWorkingDir = new File(srcDir, "indexsegment-workingdir");
        File outputIndex = new File(srcDir, "index");
//        File tmpOutputIndex = new File(localWorkingDir, "index");

        File localOutput = outputIndex ;
        indexWriter
                = new IndexWriter(localOutput,new NutchDocumentAnalyzer(), true);
        indexWriter.setMergeFactor(3000);
        indexWriter.setRAMBufferSizeMB(64);
        indexWriter.setMaxMergeDocs(MAX_MERGE_DOCS);
        indexWriter.setTermIndexInterval(TERM_INDEX_INTERVAL);
        indexWriter.setMaxFieldLength(maxFieldLength);
//        indexWriter.setInfoStream(LogFormatter.getLogStream(LOG, Level.FINE));
        indexWriter.setUseCompoundFile(false);
        indexWriter.setSimilarity(new NutchSimilarity());
    }


}
