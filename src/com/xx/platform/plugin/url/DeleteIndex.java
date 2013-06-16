package com.xx.platform.plugin.url;
//
//import java.util.List;
//import com.xx.platform.dao.IBase;
//import com.xx.platform.core.SearchContext;
//import com.xx.platform.domain.model.database.Dbconfig;
//import com.xx.platform.domain.model.database.Dbtable;
//import java.sql.ResultSet;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.*;
//import java.util.*;
//import java.text.SimpleDateFormat;
//import org.apache.nutch.tools.PruneIndexTool;
//import org.apache.nutch.tools.PruneIndexTool.*;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.HitCollector;
//import java.io.*;
//import org.apache.lucene.search.TermQuery;
//import org.apache.nutch.searcher.Query.Term;
//import org.apache.lucene.search.BooleanQuery;
//import com.xx.platform.core.nutch.RuntimeDataCollect;
//import com.xx.platform.core.nutch.NutchCommand;
//
///**
// * <p>Title: </p>
// *
// * <p>Description: </p>
// *
// * <p>Copyright: Copyright (c) 2008</p>
// *
// * <p>Company: </p>
// *
// * @author not attributable
// * @version 1.0
// */
//public class DeleteIndex implements UrlGenerator {
//    private final static int pernum = 1000;
//    private final SimpleDateFormat dateFormat = new SimpleDateFormat(
//            "yyyy-MM-dd HH:mm:ss");
//    private IndexReader reader = null;
//    private IndexSearcher searcher = null;
//    BooleanQuery booleanQuery = new BooleanQuery();
//
//    private static int num = 0;
//
//    public DeleteIndex() {
//    }
//
//    public List generator() throws Exception {
//
//        while (!RuntimeDataCollect.getCrawl_status().equals(NutchCommand.CRAWL_STATUS_NOT_RUNNING)) {
//            NutchCommand.setCrawl(false);
//            Thread.sleep(60 * 1000);
//        }
//
//        List<String> list = new ArrayList<String>();
//        Dbtable dbtable_ = null;
//        List<Dbtable> dbtableList = SearchContext.getDbtableList();
//        for (Dbtable dbtable : dbtableList) {
//            dbtable_ = dbtable;
//            boolean isnext = true;
//            while (isnext) {
//                list.addAll(getData(dbtable));
//                if(list!=null && list.size()>0 && list.size()<pernum){
//                    num=list.size();
//                }else{
//                    num=0;
//                }
//                SearchContext.getDao().updateIObject(dbtable);
//                if (list == null || list.size() < pernum) {
//                    isnext = false;
//                    // continue;
//                } else {
//                    deleteIndex();
//                    booleanQuery = new BooleanQuery();
//                    isnext = true;
//                    list = new ArrayList<String>();
//                }
//            }
//
//        }
//        if (list != null && list.size() > 0 && dbtable_ != null) {
//            deleteIndex();
//        }
//
//        return new ArrayList();
//    }
//
//    private void deleteIndex() {
//        Directory dir = null;
//        try {
//            dir = FSDirectory.getDirectory(new File(SearchContext.search_dir,
//                    "index"));
//            reader = IndexReader.open(dir);
//            BitSet bits = new BitSet(reader.maxDoc());
//            AllHitsCollector ahc = new AllHitsCollector(bits);
//            searcher = new IndexSearcher(reader);
//            searcher.search(booleanQuery, ahc);
//            int docNum = -1, start = 0;
//            while ((docNum = bits.nextSetBit(start)) != -1) {
//                if (reader.isDeleted(docNum)) {
//                    continue;
//                }
//                reader.delete(docNum);
//                start = docNum + 1;
//            }
//            RuntimeDataCollect.has_new_index = true;
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                reader.close();
//            } catch (IOException ex1) {
//            }
//        }
//
//    }
//
//    public void generator(IBase service, InnerURL innerUrl) throws Exception {
//    }
//
//    private void createConnection(Dbconfig dbconfig) throws Exception {
//        if (dbconfig.getConnection() == null ||
//            dbconfig.getConnection().isClosed()) {
//            try {
//                Class.forName(dbconfig.getDriverclazz());
//                dbconfig.setConnection(DriverManager.getConnection(dbconfig.
//                        getDburl(),
//                        dbconfig.getDbuser(),
//                        dbconfig.getDbpass()));
////               dbconfigMap.put(dbconfig.getId(),dbconfig);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//
//    private static class AllHitsCollector extends HitCollector {
//        private BitSet bits;
//
//        public AllHitsCollector(BitSet bits) {
//            this.bits = bits;
//        }
//
//        public void collect(int doc, float score) {
//            bits.set(doc);
//        }
//    }
//
//    private StringBuffer buffer=new StringBuffer();
//    private   List<String> getData(Dbtable dbtable) {
//        Statement statement = null;
//        ResultSet rs = null;
//        String sql = getMYSQL(dbtable);
//
//        List dataList = new ArrayList();
//        try {
//            createConnection(dbtable.getDbid());
//            statement = dbtable.getDbid().getConnection().createStatement();
//            rs = statement.executeQuery(sql);
//            while (rs.next()) {
//                String id = rs.getString(1);
//                buffer.setLength(0);
//                booleanQuery.add(new TermQuery(luceneTerm("id",
//                        new Term(buffer.append(id).append(" ").append(dbtable.getDbid().getName()).append(" ").append(dbtable.getCode()).toString()))), false, false);
//                buffer.setLength(0);
//                java.util.Date date = new java.util.Date(rs.getTimestamp(2).
//                        getTime());
//                if (date != null && date.after(dbtable.getDeltime())) {
//                    dbtable.setDeltime(date);
//
//                }
//                dataList.add(id);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                if (rs != null) {
//                    rs.close();
//                }
//                if (statement != null) {
//                    statement.close();
//                }
//                if (dbtable.getDbid().getConnection() != null) {
//                    dbtable.getDbid().getConnection().close();
//                    dbtable.getDbid().setConnection(null);
//                }
//            } catch (Exception ex1) {
//                ex1.printStackTrace();
//            }
//        }
//        System.out.println("DELETE SQL:" + sql+"  dataList:"+dataList.size());
//        return dataList;
//    }
//
//    private String getMYSQL(Dbtable dbtable) {
//        StringBuffer sql = new StringBuffer("SELECT post_id ,deltime ");
//        sql.append(" FROM ").append(dbtable.getCode()).append("_del");
//        sql.append(" WHERE deltime>'").append(dateFormat.format(dbtable.
//                getDeltime())).append("'");
//        sql.append(" LIMIT 0,").append(pernum-num);
//        return sql.toString();
//    }
//
//    /** Utility to construct a Lucene Term given a Nutch query term and field. */
//    private static org.apache.lucene.index.Term luceneTerm(String field,
//            org.apache.nutch.searcher.Query.Term term) {
//        return new org.apache.lucene.index.Term(field, term.toString());
//    }
//
//
//}
