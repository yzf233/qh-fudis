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
 * <p>Title: 没有处理segments</p>
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

    private static String D_SPLIT = "split";//索引分解后保存目录
    private String srcD; //待处理索引目录（完整目录）
    private int bnum = 1; //需要分解成的数量
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
        ///单线程
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
            System.out.println("第"+(i+1)+"份索引生成完毕");
        }
        reader.close();
    }


    public static void main(String[] args) {
        /**处理参数*/
        if (args.length != 2) {
            System.out.println("Usage: splitindex dir（待分解的索引的主目录） splitnum（需要分解成的数量）");
            return;
        }
        String srcD = args[0];
        ///检查各个目录是否正确
        File localF = new File(srcD);
        File indexF = new File(localF,"index");
        if(!localF.isDirectory()|| !indexF.isDirectory() ){
            System.out.println("输入的目录不存在或者不是索引目录");
            return;
        }
        int num = 1;
        try{
            num = Integer.parseInt(args[1]);
        }catch(java.lang.NumberFormatException e){
            System.out.println("splitnum 必须是数字类型");
            return;
        }
        if(num<2){
            System.out.println("切分数量小于2！");
            return;
        }
        ///没有检查split目录是否已经存在

        SplitIndexTool tool = new SplitIndexTool(srcD,num);
        System.out.println("开始切分索引，索引将被分为"+num+"份，每份约有"+tool.num+"条数据！");
        try {
            tool.split();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("切分索引完成！");
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
