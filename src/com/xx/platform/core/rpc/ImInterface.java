package com.xx.platform.core.rpc;

import java.io.IOException;
import java.util.Map;

import com.xx.platform.domain.model.distributed.SynState;
import com.xx.platform.util.tools.IndexMessage;

/**
 * �ֲ�ʽ�ӿ�
 * @author Administrator
 *
 * @param <T>
 */
public interface ImInterface{
	/**
	 * ������������
	 */
	public void getStatus();
	/**
	 * ͬ�����ݿ�������
	 * @param object �����
	 * @throws IOException
	 */
	public void save(byte[] object) throws IOException ;
	/**
	 * ͬ�����ݿ���²���
	 * @param object �����
	 * @throws IOException
	 */
	public void saveOrUpdate(byte[]  object) throws IOException ;
	/**
	 * ͬ�����ݿ�ɾ������
	 * @param object �����
	 * @throws IOException
	 */
	public void delete(byte[]  object) throws IOException ;
	/**
	 * ͬ�����ݿ���²���
	 * @param object �����
	 * @throws IOException
	 */
	public void update(byte[] object) throws IOException ;
	/**
	 * ��ڵ㷢������List<String,map<String,String>>,�ڽڵ㽨������
	 * @param in ����List<String,map<String,String>>��byte[]����
	 * @throws Exception
	 */
	public void push(byte [] in) throws Exception;
	/**
	 * ִ��hql���
	 * @param hSQL
	 * @return
	 * @throws Exception
	 */
	public int execByHQL(byte[] stringHql) throws Exception;
	/**
	 * ɾ���û�����
	 * @param projectUser
	 * @throws IOException
	 */
	public void deleteUser(byte[] projectUser)throws IOException ;
	/**
	 * ִ���������
	 * @param list
	 * @throws IOException
	 */
	public void inserBat(byte[] list)throws IOException ;
	
	
	/****************************************�ļ����������ݿ�����ķָ���*******************************************/
	/**
	 * ������Դ
	 */
	public void publish(byte[] projectCode)throws IOException ;
/**
 * �ջ���Դ
 * @param code
 * @throws IOException
 */
	public void reback(byte[] projectCode) throws IOException;
	/**
	 * ��Ŀ��������
	 * @param code
	 * @throws IOException
	 */
	public void publicTest(String code) throws IOException;
	/**
	 * ɾ��������Դ
	 * @param code
	 */
	public void deleteTestResource(String code);
	/**
	 * �����ļ�
	 */
	public void createFile(byte[] projectCode)throws IOException ;
	/**
	 * �ϴ��ļ�
	 */
	public void upload(byte[] projectCode,byte[] fileName,byte[] docName,byte[] uploadFile)throws IOException;
	/**
	 * ɾ��һ����Ŀ�е�������Դ
	 */
	public void deleteFile(byte[] projectCode)throws IOException;
	/**
	 * ������һ���ļ�
	 * @param projectCode
	 * @param fileName
	 * @param docName
	 * @param newName
	 * @return
	 * @throws IOException
	 */
	public boolean rename(byte[] projectCode,byte[] fileName,byte[] docName,byte[] newName)throws IOException;
	/**
	 * �༭��Դ
	 * @param projectCode
	 * @param fileName
	 * @param docName
	 * @param content
	 * @throws IOException
	 */
	public void editSource(byte[] projectCode,byte[] fileName,byte[] docName,byte[] content) throws IOException;
	/**
	 * ɾ��һ����Դ
	 * @param path
	 * @param command
	 * @throws IOException
	 */
	public  void deleteFileByPath(byte[] path) throws IOException;
	/**
	 * ɾ���ĵ�
	 * @param projectCode
	 * @param fileName
	 * @param docName
	 * @param command
	 * @throws IOException
	 */
	public void deleteDoc(byte[] projectCode,byte[] fileName,byte[] docName) throws IOException;
	/**
	 * �����ļ�
	 * @param projectCode
	 * @param fileName
	 * @param docName
	 * @throws IOException
	 */
	public void createNewFile(byte[] projectCode,byte[] fileName,byte[] docName) throws IOException;
	/**
	 * ��ȡ������״̬��Ϣ
	 * @return
	 */
	public SynState getServerState();
	/**
	 * ��ʼ�ɼ�
	 * @param bflag
	 * @throws Exception
	 */
	public void startCrawl(byte[] bflag) throws Exception ;
	/**
	 * ֹͣ�ɼ�
	 * @throws Exception
	 */
	public void stopCrawl(byte[] bflag) throws Exception;
	/**
	 * �޸�����״̬
	 * @param crawl_status
	 * @throws IOException
	 */
	public void setCrawl_status(byte[] crawl_status) throws IOException;
	/**
	 * д����
	 * @param indexWriter
	 * @param segmentName
	 * @param fo
	 * @param pt
	 * @param pd
	 * @param content
	 * @param command
	 * @return
	 * @throws IOException
	 */
	//public void indexpage(byte[] bpd,byte[] bdoc) throws Exception;
	public void outputPage(byte[] bfo,byte[] bcontent,byte[] text,byte[] parseData) throws Exception;
	public void outputPage(byte[] bindexMessage) throws Exception;
	public void outputPage(IndexMessage im) throws Exception;
	public void filePro(IndexMessage im) throws Exception;
	/**
	 * ��������ύ
	 * @param auto
	 * @throws Exception
	 */
	public void commit() throws Exception;
	/**
	 * �������͵������ϲ�
	 * @throws Exception
	 */
	public void pushDataMerger() throws Exception;
	/**
	 * webserviceɾ������
	 * @param field
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public long deleteOneIndexByField(byte[] field, byte[] value)throws IOException;
	/**
	 * ���ȥ����Ϣ
	 * @throws IOException
	 */
	public void addContent(byte[] md5Content) throws Exception;
	/**
	 * �ڵ�ϵ�ʱû����������Ϣ��������
	 * @param bDocumetnList
	 * @throws Exception
	 */
	public void addDocument(byte[] bDocumetnList) throws Exception;
	public void dataPro(IndexMessage im) throws Exception;
//	/**
//	 * ֹͣ�ļ��ɼ�
//	 * @return
//	 * @throws Exception
//	 */
//	public void stopFileCrawler() throws Exception;
//	/**
//	 * ֹͣ���ݿ�ɼ�
//	 * @return
//	 * @throws Exception
//	 */
//	public void stop() throws Exception;
}
