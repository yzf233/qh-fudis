package com.xx.platform.core.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.nutch.util.LogFormatter;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentStats;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.xx.platform.core.SearchContext;

public class BerkeleyDB {
	public static final Logger LOG = LogFormatter
			.getLogger("com.xx.platform.core.db.BerkeleyDB");
	private static Database myDb = null;//����ԭ��
	private static Database delDocDB = null; //ɾ������
	private static Database classCatalogDb;
//	private static Database delDocDB = null;
//	private static String delDocDBName = "delDB";
	private static Environment dbEnv = null;
	private static long docNum =0 ;
	private static long docCount=0;
	private static java.io.RandomAccessFile ramFile = null ;
	static {
		try {
			init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void init() throws IOException {
		LOG.info("��ʼ��DB......");
		try {
			File file = new File(SearchContext.search_dir + File.separator + "segments" + File.separator + "bdbdata");
			// File file = new File("D:/index/insiteseIndex/segments") ;
			if (!file.exists())
				file.mkdirs();
			EnvironmentConfig envConf = new EnvironmentConfig();
			envConf.setAllowCreate(true);
			envConf.setTransactional(true);
//			envConf.setCachePercent(30);
			envConf.setCacheSize(30*1024*1024);
//			envConf.setConfigParam("je.log.fileCacheSize","1000000");
			envConf.setConfigParam("je.log.fileMax", "1000000000");
			dbEnv = new Environment(file, envConf);

//			EnvironmentStats envStats = dbEnv.getStats(null);
			DatabaseConfig dbConf = new DatabaseConfig();
			dbConf.setAllowCreate(true);
			dbConf.setTransactional(false);
//			dbConf.setDeferredWrite(true);
			dbConf.setSortedDuplicates(false);
      
//			DatabaseConfig dbConfDel = new DatabaseConfig();
//			dbConfDel.setAllowCreate(true);
//			dbConfDel.setTransactional(true);
//			dbConfDel.setSortedDuplicates(false);
			
			String database = "data";
			myDb = dbEnv.openDatabase(null, database, dbConf);
			
			String delDocDBName = "delDB";
			delDocDB = dbEnv.openDatabase(null, delDocDBName, dbConf);//Ҳʹ��Properties
			
//			classCatalog = new StoredClassCatalog(myDb);
			classCatalogDb = dbEnv.openDatabase(null, 
		                               "ClassCatalogDB",
		                               dbConf);
			classCatalog = new StoredClassCatalog(classCatalogDb);
			docCount = initDocNum() ;
//			docNum = myDb.count() ;
			dataBinding = new SerialBinding(classCatalog, Properties.class);
		} catch (Exception de) {
			// LOG.info("BDB��ʼ��ʧ�ܣ���ȷ���ļ�Ŀ¼���ڣ����Ҿ��ж�дȨ��");
			throw new IOException(de.getMessage());
		}
	}
	/**
	 * ���ļ��г�ʼ����ȡ �ĵ�����
	 * @return
	 * @throws Exception
	 */
	private static long initDocNum(){
		File file = new File(SearchContext.search_dir + File.separator + "segments" + File.separator + "docNum");
		if(!file.exists())
			file.mkdirs() ;
		File docNumFile = new File(file,"docNum.dat") ;
		long count = 0 ;
		if(!docNumFile.exists() || docNumFile.length()==0){
			try {
				ramFile = new java.io.RandomAccessFile(docNumFile, "rw") ;
				ramFile.writeLong(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			try{
				ramFile = new java.io.RandomAccessFile(docNumFile, "rw") ;
				count = ramFile.readLong() ;
				docCount=count;
				ramFile.seek(0) ;
				count++;
				ramFile.writeLong(count) ;
				ramFile.getFD().sync();
			}catch(Exception ex){
				ex.printStackTrace() ;
			}
		}
		return count ;
	}
	/**
	 * 
	 * @param count
	 * @return
	 * @throws IOException
	 */
	private static long getDocNum(long count) throws IOException{
		if(ramFile==null)
			return (int)initDocNum() ;
		else{
//			synchronized(ramFile)
//			{
////				ramFile.seek(0) ;
////				ramFile.writeLong(count) ;
////				ramFile.getFD().sync() ;
//			}
			return count ;
		}
	}
	/**
	 * 
	 * @throws IOException
	 */
	public static void close() throws IOException {
		LOG.info("�ر�DB......");
		try {
			if(ramFile!=null){
				ramFile.close() ;
			}
			if (myDb != null) {
				myDb.close();
			}
			if(delDocDB!=null){
				delDocDB.close();
			}
			if(classCatalogDb!=null){
				 classCatalogDb.close();
			}
			if (dbEnv != null) {
				dbEnv.cleanLog();
				dbEnv.close();
			}
		} catch (Exception ex) {
//			ex.printStackTrace();
			// LOG.info(ex.getMessage()) ;
		}
	}

	public static void syncDb() {
		// try {
		// // if(myDb!=null)
		// // myDb.sync();
		// } catch (DatabaseException e) {
		// // TODO Auto-generated catch block
		// LOG.info("�����Ѿ�ͬ����ɣ�������Ϣ��"+e.getMessage()) ;
		//			
		// }
	}

	private static StoredClassCatalog classCatalog;
	private static EntryBinding dataBinding;

	public static synchronized String getRecordNum() throws Exception {
		return new StringBuffer(Long.toString(docCount,16)).append("z").append(Long.toString(getDocNum(docNum++),16)).toString();
	}

	public static void saveRecord(String docNo, Properties object)
			throws IOException, DatabaseException {
		if (docNo == null)
			return;
		if (object == null)
			object = localProperties;
		if (classCatalog == null || dataBinding == null) {
			classCatalog = new StoredClassCatalog(myDb);
			dataBinding = new SerialBinding(classCatalog, Properties.class);
		}
		DatabaseEntry theKey = new DatabaseEntry();
		DatabaseEntry theData = new DatabaseEntry();

		dataBinding.objectToEntry(object, theData);
		theKey.setData(docNo.getBytes());
		myDb.putNoOverwrite(null, theKey, theData);
	}
	
	public static synchronized void saveDelDoc(String docNo, Properties object)
			throws IOException, DatabaseException {
		if (docNo == null)
			return;
		if (object == null)
			object = localProperties;
		if (classCatalog == null || dataBinding == null) {
			classCatalog = new StoredClassCatalog(delDocDB);
			dataBinding = new SerialBinding(classCatalog, Properties.class);
		}
		DatabaseEntry theKey = new DatabaseEntry();
		DatabaseEntry theData = new DatabaseEntry();

		dataBinding.objectToEntry(object, theData);
		theKey.setData(docNo.getBytes());
		synchronized (delDocDB) {
			delDocDB.put(null, theKey, theData);
		}
	}
	
	/**
	 * �����ݿ�ȡɾ�������ݣ�����װ��query
	 * @throws IOException
	 * @throws DatabaseException
	 */
	public static List getDelDocQuery() throws IOException, DatabaseException {
		synchronized (delDocDB) {
			List delList = null;
			if(delDocDB.count()>0){
				delList = new ArrayList();
//				Transaction tra = dbEnv.beginTransaction(null, null);
				com.sleepycat.je.Cursor cursor = delDocDB.openCursor(null, null);
				DatabaseEntry foundKey = new DatabaseEntry();
				DatabaseEntry foundData = new DatabaseEntry();
				try {
					Properties properties = null;
					while (cursor.getNext(foundKey, foundData,
							com.sleepycat.je.LockMode.DEFAULT) == com.sleepycat.je.OperationStatus.SUCCESS) {
						properties = (Properties) dataBinding.entryToObject(foundData);
						if(properties!=null&&properties.get("docNo")!=null){
							delList.add((String)properties.get("docNo"));
						}
						cursor.delete();
					}
					cursor.close();
//					tra.commit();
				} catch (Exception e) {
					LOG.info("�����������ݷ�������");
//					tra.abort();
					e.printStackTrace();
				}
				return delList;
			}else{
				return null;
			}
		}
	}

	public static synchronized void update(String docNo, Properties object)
			throws IOException, DatabaseException {
		if (docNo == null)
			return;
		if (object == null)
			object = localProperties;
		if (classCatalog == null || dataBinding == null) {
			classCatalog = new StoredClassCatalog(myDb);
			dataBinding = new SerialBinding(classCatalog, Properties.class);
		}
		DatabaseEntry theKey = new DatabaseEntry();
		DatabaseEntry theData = new DatabaseEntry();

		dataBinding.objectToEntry(object, theData);
		theKey.setData(docNo.getBytes());
		synchronized (myDb) {
			myDb.put(null, theKey, theData);
		}
	}

	private final static Properties localProperties = new Properties();

	public static Properties getRecord(String docNo) throws IOException {
		if (docNo == null)
			return localProperties;
		DatabaseEntry theKey = new DatabaseEntry((docNo.getBytes()));
		DatabaseEntry theData = new DatabaseEntry();
		Properties properties = null;
		try {
			if (myDb.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				properties = (Properties) dataBinding.entryToObject(theData);
			}
		} catch (Exception ex) {
			throw new IOException(ex.getMessage());
		}
		return properties;
	}
	
	
	public static Properties getDelDoc(String docNo) throws IOException {
		if (docNo == null)
			return localProperties;
		DatabaseEntry theKey = new DatabaseEntry((docNo.getBytes()));
		DatabaseEntry theData = new DatabaseEntry();
		Properties properties = null;
		try {
			if (delDocDB.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				properties = (Properties) dataBinding.entryToObject(theData);
			}
		} catch (Exception ex) {
			throw new IOException(ex.getMessage());
		}
		return properties;
	}


	public static void main(String[] args) {
		long start = 0;
		try {
			java.util.Properties properties = new java.util.Properties();
			properties
					.put("a",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");
			properties.put("b",
					"����������Ϣ����������asdfasdfasdfasdfϢ����������Ϣ����������Ϣ����������Ϣ");
			properties.put("c",
					"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");
			properties
					.put("d",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");
			properties
					.put(
							"e",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");
			properties
					.put(
							"f",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");
			properties
					.put(
							"g",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");
			properties
					.put("h",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");
			properties
					.put(
							"i",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");
			properties
					.put(
							"j",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");

			properties
					.put(
							"k",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");
			properties
					.put(
							"l",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");
			properties
					.put(
							"m",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");
			properties
					.put(
							"n",
							"����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ����������Ϣ");

			start = System.currentTimeMillis();
			// for(int i=0 ; i<1000000 ; i++)
			// {
			//				
			// if(i%10000==0)
			// {
			// System.out.print(" "+i);
			// }
			// saveRecord(i , properties) ;
			// }
			System.out.println("�����¼ʱ�䣺" + (System.currentTimeMillis() - start)
					+ " �����ʣ�" + dbEnv.getStats(null).getNCacheMiss());
			//
			start = System.currentTimeMillis();
			for (int i = 0; i < 20; i++) {

				Properties loadP = getRecord(String.valueOf(0L + i));

				System.out.println(loadP);

			}
			System.out.println("��ȡ��ʱ��20����¼ʱ�䣺"
					+ (System.currentTimeMillis() - start) + " �����ʣ�"
					+ dbEnv.getStats(null).getNCacheMiss());
			for (int i = 0; i < 100; i++) {

				Properties loadP = getRecord(String.valueOf(0L + i));

				// System.out.println(loadP.get("j")) ;

			}
			System.out.println("��ȡ��ʱ��100����¼ʱ�䣺"
					+ (System.currentTimeMillis() - start) + " �����ʣ�"
					+ dbEnv.getStats(null).getNCacheMiss());
			for (int i = 0; i < 1000; i++) {

				Properties loadP = getRecord(String.valueOf(0L + i));

				// System.out.println(loadP.get("j")) ;

			}
			System.out.println("��ȡ��ʱ��1000����¼ʱ�䣺"
					+ (System.currentTimeMillis() - start) + " �����ʣ�"
					+ dbEnv.getStats(null).getNCacheMiss());
			for (int i = 0; i < 10000; i++) {

				Properties loadP = getRecord(String.valueOf(0L + i));

				// System.out.println(loadP.get("j")) ;

			}
			System.out.println("��ȡ��ʱ��10000����¼ʱ�䣺"
					+ (System.currentTimeMillis() - start));
			// for(int i=0 ; i<100000 ; i++)
			// {
			//				
			// Properties loadP = getRecord(0L+i) ;
			//				
			// // System.out.println(loadP.get("j")) ;
			//				
			// }
			// System.out.println("��ȡ��ʱ��100000����¼ʱ�䣺"+(System.currentTimeMillis()-start))
			// ;
			start = System.currentTimeMillis();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("д���ļ�ʱ�䣺" + (System.currentTimeMillis() - start));
	}

	public static Database getDelDocDB() {
		return delDocDB;
	}

	public static void setDelDocDB(Database delDocDB) {
		BerkeleyDB.delDocDB = delDocDB;
	}

	public static EntryBinding getDataBinding() {
		return dataBinding;
	}

	public static void setDataBinding(EntryBinding dataBinding) {
		BerkeleyDB.dataBinding = dataBinding;
	}
	
}
