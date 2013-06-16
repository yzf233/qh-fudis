package com.xx.platform.core.db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.io.ArrayFile;
import com.xx.platform.core.io.MapFile;
import com.xx.platform.core.nutch.WebDB;

import org.apache.nutch.fs.NutchFileSystem;
import org.apache.nutch.io.MD5Hash;
import org.apache.nutch.io.Writable;
import org.apache.nutch.util.LogFormatter;

/**
 * <p>Title: URL�����߹���</p>
 *
 * <p>Description: �洢�ṹ��ÿ��URL�洢��������������¼��һ������������
 * ����һ�����ڿ��ٲ�ѯ������������������¼�洢������MD5ֵ��URL��MD5ֵ�ͼ�¼
 * �ĵ�ַ�����ڿ��ٲ�ѯ������ֻ�洢URL��MD5�ͼ�¼�ĵ�ַ
 * </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class WebDbAdminTool
     {
  private final static String SERVICE_NAME = "��ַ������";
  private static ArrayFile arrayFile;
//  private static ArrayFile.Writer webDbWriter;
//  private static ArrayFile.Writer webDbUpdateWriter;
//  private static ArrayFile.Reader webDbReader;
//  private static ArrayFile.Reader webDbReaderConsole;
  private static boolean isOk = false;
  private static boolean isReloading = false;
  private static File dbFile = new File(SearchContext.getXdtechsite().
                                        getSearchdir(), "db"); //SearchContext.getSegFilePath()
  public static final Logger LOG =
      LogFormatter.getLogger("com.xx.platform.core.db.WebDbAdminTool");
  static {
    init();
//    Thread th = new Thread(new Runnable() {
//      public void run() {
//        Test.startServer();
//      }
//    });
//    th.start();
  }

  static {
    {
      {
        Thread hookThread = new Thread(new Runnable() {

          public void run() {
        	  LOG.info("��д��Ϣ��ʼ");
          // LOG.info("��ֹ�������ϵͳ������Դ����д��ַ��Ϣ");
            try {
//              webDbWriter.close();
//              webDbReader.close();
//              webDbUpdateWriter.close();
//              webDbReaderConsole.close();
              arrayFile.writeMap();
//              LOG.info("--��д��ַ��Ϣ����");
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        });
        Runtime.getRuntime().addShutdownHook(hookThread);
      }
    }
  }

  private static void init() {
    try {
      NutchFileSystem nfs = NutchFileSystem.getXDFS();

      if (!dbFile.exists())
        dbFile.mkdir();
      arrayFile = new ArrayFile(nfs,
                                dbFile.toString());
//      webDbWriter = new ArrayFile.Writer(nfs,
//                                         dbFile.toString(), WebDB.class);
//      webDbUpdateWriter = new ArrayFile.Writer(nfs,
//                                               dbFile.toString(), WebDB.class);
//      webDbReader = new ArrayFile.Reader(nfs,
//                                         dbFile.toString());
//      webDbReaderConsole = new ArrayFile.Reader(nfs,
//                                                dbFile.toString());
      Thread loadThread = new Thread(new Runnable() {
        //���ص�ַ�߳�
        public void run() {
         // LOG.info("�ӳټ��ص�ַ���ַ��Ϣ......");
          arrayFile.init();
          try {
           // LOG.info("��ַ���ַ��Ϣ������ϣ����ص�ַ����" + arrayFile.size() + "......");
            if(arrayFile.size()==0)
            {//δȡ����Ч���� �� �� ���ݿ���ȡ��
              //
            }
          }catch (Exception ex) {}
        }
      });
      loadThread.start();

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    isOk = true;
  }

  
  
  private synchronized static void initFile() {
    if (!isOk && isReloading) {
      try {
        NutchFileSystem nfs = NutchFileSystem.getXDFS();

//        webDbWriter = new ArrayFile.Writer(nfs,
//                                           dbFile.toString(), WebDB.class);
//        webDbUpdateWriter = new ArrayFile.Writer(nfs,
//                                                 dbFile.toString(), WebDB.class);
//        webDbReader = new ArrayFile.Reader(nfs,
//                                           dbFile.toString());
//        webDbReaderConsole = new ArrayFile.Reader(nfs,
//                                                  dbFile.toString());
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }

      isOk = true;
      isReloading = false;
      {
        //���л�Map ���� ������ÿ����������Ҫ���ļ�����������
        try {
          arrayFile.writeMap();
        }
        catch (Exception ex1) {
          ex1.printStackTrace();
        }
      }

    }
  }

  public Object get(byte[] key) throws Exception {
    return arrayFile.get(key);
  }

  public long getKey(byte[] key) throws Exception {
    Long keyVal = (Long) arrayFile.get(key);
    return keyVal != null ? keyVal.longValue() : 0;
  }

  public void rmKey(byte[] key) throws Exception {
    arrayFile.rmKey(key);
  }

//  public void flush() throws IOException {
//    try {
//      webDbWriter.flush();
//      webDbUpdateWriter.flush();
//    }
//    catch (Exception ex) {
//      ex.printStackTrace();
//    }
//  }

  /**
   * ������ַ
   * @param webDb WebDB
   * @return boolean
   * @throws Exception
   */
  public synchronized boolean addContents(String content) throws Exception {
	  byte[] bContent=MD5Hash.digest(content).getDigest();
	  if (get(bContent) == null) {
//      synchronized (arrayFile) {
//        webDbWriter.append(webDb);
        arrayFile.putValue(bContent, 0);
//      }
      return true;
    }
    else
      return false;
  }

  /**
   * ɾ����ַ
   * @param webDb WebDB
   * @return boolean
   * @throws Exception
   */
  public synchronized boolean removeContents(String content) throws Exception {
    if (get(MD5Hash.digest(content).getDigest()) != null) {
//      synchronized (arrayFile) {
//        webDbWriter.append(webDb);
        arrayFile.rmKey(MD5Hash.digest(content).getDigest());
//      }
      return true;
    }
    else
      return false;
  }

  /**
   * ������ַ
   * @param webDb WebDB
   * @return boolean
   * @throws Exception
   */
  public boolean addWebDB(WebDB webDb) throws Exception {
//    if (get(webDb.getMd5CHash().getDigest()) == null) {
//      synchronized (arrayFile) {
//        webDbWriter.append(webDb);
//        arrayFile.putValue(webDb.getMd5CHash().getDigest(), webDb.getKey());
//      }
//      return true;
//    }
//    else
      return false;
  }

  /**
   * ɾ����ַ
   * @param webDb WebDB
   * @throws Exception
   */
  public WebDB getWebDB(WebDB webDb) throws Exception {
//    while (!isOk) {
//      reload(isOk);
//      Thread.sleep(3000);
//    }
//
//    webDb = (WebDB) webDbReader.get(webDb.getKey(), webDb);
//    return webDb;
      return null;
  }

  /**
   * ɾ����ַ
   * @param webDb WebDB
   * @throws Exception
   */
  public synchronized void rmWebDB(WebDB webDb) throws Exception {
    webDb.setIsDelete(true);
    synchronized (arrayFile) {
//      webDbUpdateWriter.update(webDb.getKey(), webDb);
      arrayFile.rmKey(webDb.getMd5CHash().getDigest());
    }
  }

  public long rmWebDB(String domainMD5) {
    return 0;
  }
  private static Long aDocNum = new Long(0);
  /**
   * ȡ��ַ
   * @param recNum long
   * @return List
   * @throws Exception
   */
  public List<Writable> getWebDB(long recNum) throws Exception {
    Writable webDbData = new WebDB();
    List<Writable> list = new ArrayList();
//    long num = 1;
//    while (!isOk) {
//      if (!isReloading)
//        reload(isOk);
//      Thread.sleep(3000);
//    }
//
//    if (webDbReader != null) {
//      while (num < recNum && (webDbData = webDbReader.next(webDbData)) != null) {
//        if ( ( (WebDB) webDbData).getRetriessincefetch() == null ||
//            ( (WebDB) webDbData).getRetriessincefetch().intValue() == 0 &&
//            ( (WebDB) webDbData).getErrornum() < 5) {
//          if (! ( (WebDB) webDbData).getIsDelete() &&
//              arrayFile.get( ( (WebDB) webDbData).getMd5CHash().getDigest()) != null &&
//              ( (WebDB) webDbData).getRetriessincefetch().intValue() == 0) {
//            list.add(webDbData);
//            webDbData = new WebDB();
//          }
//        }
//      }
//    }
    return list;
  }

  /**
   * ��ַ�������
   * @param recNum long
   * @return List
   * @throws Exception
   */
  public List<WebDB> getWebDB(long start, long page_size, long time) throws
      Exception {
    long docNum = 0;
    Writable webDbData = new WebDB();
    List<WebDB> list = new ArrayList();
//    long num = 0;
//    while (!isOk) {
//      if (!isReloading)
//        reload(isOk);
//      Thread.sleep(3000);
//    }
//
//    if (webDbReader != null) {
//      synchronized (aDocNum) {
//        while (num < page_size &&
//               (webDbData = webDbReader.get(aDocNum, webDbData)) != null) {
//          {
//            aDocNum++ ;
//            if (! ( (WebDB) webDbData).getIsDelete() &&
//                ( (WebDB) webDbData).getNextfetch() <= time &&
//                ( (WebDB) webDbData).getMd5CHash() != null &&
//                ( (WebDB) webDbData).getErrornum() < 5) {
//              docNum++;
//              if (docNum > start &&
//                  arrayFile.get( ( (WebDB) webDbData).getMd5CHash().getDigest()) != null &&
//                  ( (WebDB) webDbData).getRetriessincefetch().intValue() == 0) {
//                list.add( (WebDB) webDbData);
//                webDbData = new WebDB();
//                num++;
//              }
//            }
//          }
//        }
//      }
//    }
    return list;

  }

  /**
   * ȡ��ַ
   * @param recNum long
   * @return List
   * @throws Exception
   */
  public List<WebDB> getWebDBList(final long start, long page_size) throws
      Exception {
    long docNum = 0, aDocNumT = 0;
    Writable webDbData = new WebDB();
    List<WebDB> list = new ArrayList();
//    long num = 0;
//    while (!isOk) {
//      if (!isReloading)
//        reload(isOk);
//      Thread.sleep(3000);
//    }
//
//    if (webDbReaderConsole != null) {
//      while (num < page_size &&
//             (webDbData = webDbReaderConsole.get(aDocNumT++, webDbData)) != null) {
//        {
//          if (! ( (WebDB) webDbData).getIsDelete() &&
//              ( (WebDB) webDbData).getMd5CHash() != null &&
//              ( (WebDB) webDbData).getErrornum() < 5) {
//            docNum++;
//            if (docNum > start &&
//                arrayFile.get( ( (WebDB) webDbData).getMd5CHash().getDigest()) != null) {
//              list.add( (WebDB) webDbData);
//              webDbData = new WebDB();
//              num++;
//            }
//          }
//        }
//      }
//    }
    return list;
  }

//  private static Long aDocNum = new Long(0);
  /**
   * ȡ��ַ
   * @param recNum long
   * @return List
   * @throws Exception
   */
  public synchronized List<WebDB> getWebDB(long start, long page_size) throws
      Exception {

    long docNum = 0;
    Writable webDbData = new WebDB();
    List<WebDB> list = new ArrayList();
//    long num = 0;
//    while (!isOk) {
//      if (!isReloading)
//        reload(isOk);
//      Thread.sleep(3000);
//    }
//
//    if (webDbReader != null) {
//      synchronized (aDocNum) {
//        while (num < page_size &&
//               (webDbData = webDbReader.get(aDocNum, webDbData)) != null) {
//          {
//            aDocNum++ ;
//            if (! ( (WebDB) webDbData).getIsDelete() &&
//                ( (WebDB) webDbData).getMd5CHash() != null &&
//                ( (WebDB) webDbData).getErrornum() < 5) {
//              docNum++;
//              if (docNum > start &&
//                  arrayFile.get( ( (WebDB) webDbData).getMd5CHash().getDigest()) != null &&
//                  ( (WebDB) webDbData).getRetriessincefetch().intValue() == 0) {
//                list.add( (WebDB) webDbData);
//                webDbData = new WebDB();
//                num++;
//              }
//            }
//          }
//        }
//      }
//    }
    return list;

  }

  /**
   * ��������ȡ��ַ
   * @param domain String
   * @param recNum long
   * @return List
   * @throws Exception
   */
  public List<WebDB> getWebDB(String domain, long start, long page_size) throws
      Exception {
    Writable webDbData = new WebDB();
    List<WebDB> list = new ArrayList();
//    long num = 1;
//    while (!isOk) {
//      if (!isReloading)
//        reload(isOk);
//      Thread.sleep(3000);
//    }
//
//    if (webDbReader != null) {
//      while (num < page_size &&
//             (webDbData = webDbReader.get(start++, webDbData)) != null) {
//        if ( ( (WebDB) webDbData).getDomain() != null &&
//            ( (WebDB) webDbData).getDomain().equals(domain)) {
//          if (! ( (WebDB) webDbData).getIsDelete()) {
//            list.add( (WebDB) webDbData);
//            webDbData = new WebDB();
//          }
//        }
//      }
//    }
    return list;
  }

  /**
   * ���µ�ַ����
   * @param webDb WebDB
   * @throws Exception
   */
  public synchronized void updateWebDB(WebDB webDb) throws Exception {
//    synchronized (webDbUpdateWriter) {
//      webDbUpdateWriter.update(webDb.getKey(), webDb);
//    }
  }

  /**
   * �ر��ļ�
   * @throws Exception
   */
  public synchronized void close() throws Exception {

    {
//      if (webDbWriter != null)
//        webDbWriter.close();
//      if (webDbReaderConsole != null)
//        webDbReaderConsole.close();
//      if (webDbReader != null)
//        webDbReader.close();
//      if (webDbUpdateWriter != null)
//        webDbUpdateWriter.close();
    }
  }

  /**
   * �ر��ļ�
   * @throws Exception
   */
  public void reload() throws Exception {
    isOk = false;
    isReloading = true;
//    if (webDbWriter != null)
//      webDbWriter.close();
//    if (webDbUpdateWriter != null)
//      webDbUpdateWriter.close();
//    if (webDbUpdateWriter != null)
//      webDbReader.close();
//    if (webDbReaderConsole != null)
//      webDbReaderConsole.close();
    if (!isOk)
      initFile();
  }

  /**
   * �ر��ļ�
   * @throws Exception
   */
  public void reload(boolean isOk) throws Exception {
    if (!isOk)
      reload();
  }

  /**
   * �ر��ļ�
   * @throws Exception
   */
  public static void reloadReader() throws Exception {
    isOk = false;
    isReloading = true;
//    if (webDbWriter != null)
//      webDbWriter.close();
//    if (webDbUpdateWriter != null)
//      webDbUpdateWriter.close();
//    if (webDbUpdateWriter != null)
//      webDbReader.close();
//    if (webDbReaderConsole != null)
//      webDbReaderConsole.close();
    if (!isOk)
      initFile();

  }

  /**
   * ɾ�����е�ַ��Ϣ
   * @throws Exception
   */
  public void rmAllWebDB() throws Exception {
    MapFile.Writer.docNum = 0;
//    aDocNum = new Long(0);
    close();
    File[] fileList = dbFile.listFiles();
    for (File file : fileList)
      file.delete();
    arrayFile.clear();
    arrayFile = null;
    init();
  }

  /**
   * ��õ�ַ������
   * @return long
   * @throws Exception
   */
  public long size() throws Exception {
    return arrayFile.size();
  }

  public WebDB[] getWebDB_A(long start, long page_size) throws Exception {
    List webDbList = this.getWebDB(start, page_size);
    return (WebDB[]) webDbList.toArray(new WebDB[webDbList.size()]);
  }
  public WebDB[] getWebDB_B(long start, long page_size , long time) throws Exception {
    List webDbList = this.getWebDB(start, page_size , time);
    return (WebDB[]) webDbList.toArray(new WebDB[webDbList.size()]);
  }

  public String getName() {
    return SERVICE_NAME;
  }


}
