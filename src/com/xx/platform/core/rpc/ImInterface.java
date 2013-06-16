package com.xx.platform.core.rpc;

import java.io.IOException;
import java.util.Map;

import com.xx.platform.domain.model.distributed.SynState;
import com.xx.platform.util.tools.IndexMessage;

/**
 * 分布式接口
 * @author Administrator
 *
 * @param <T>
 */
public interface ImInterface{
	/**
	 * 返回任务名字
	 */
	public void getStatus();
	/**
	 * 同步数据库插入操作
	 * @param object 表对象
	 * @throws IOException
	 */
	public void save(byte[] object) throws IOException ;
	/**
	 * 同步数据库更新操作
	 * @param object 表对象
	 * @throws IOException
	 */
	public void saveOrUpdate(byte[]  object) throws IOException ;
	/**
	 * 同步数据库删除操作
	 * @param object 表对象
	 * @throws IOException
	 */
	public void delete(byte[]  object) throws IOException ;
	/**
	 * 同步数据库更新操作
	 * @param object 表对象
	 * @throws IOException
	 */
	public void update(byte[] object) throws IOException ;
	/**
	 * 向节点发送数据List<String,map<String,String>>,在节点建立索引
	 * @param in 对象List<String,map<String,String>>的byte[]类型
	 * @throws Exception
	 */
	public void push(byte [] in) throws Exception;
	/**
	 * 执行hql语句
	 * @param hSQL
	 * @return
	 * @throws Exception
	 */
	public int execByHQL(byte[] stringHql) throws Exception;
	/**
	 * 删除用户操作
	 * @param projectUser
	 * @throws IOException
	 */
	public void deleteUser(byte[] projectUser)throws IOException ;
	/**
	 * 执行批量添加
	 * @param list
	 * @throws IOException
	 */
	public void inserBat(byte[] list)throws IOException ;
	
	
	/****************************************文件操作与数据库操作的分割线*******************************************/
	/**
	 * 发布资源
	 */
	public void publish(byte[] projectCode)throws IOException ;
/**
 * 收回资源
 * @param code
 * @throws IOException
 */
	public void reback(byte[] projectCode) throws IOException;
	/**
	 * 项目发布测试
	 * @param code
	 * @throws IOException
	 */
	public void publicTest(String code) throws IOException;
	/**
	 * 删除测试资源
	 * @param code
	 */
	public void deleteTestResource(String code);
	/**
	 * 创建文件
	 */
	public void createFile(byte[] projectCode)throws IOException ;
	/**
	 * 上传文件
	 */
	public void upload(byte[] projectCode,byte[] fileName,byte[] docName,byte[] uploadFile)throws IOException;
	/**
	 * 删除一个项目中的所有资源
	 */
	public void deleteFile(byte[] projectCode)throws IOException;
	/**
	 * 重命名一个文件
	 * @param projectCode
	 * @param fileName
	 * @param docName
	 * @param newName
	 * @return
	 * @throws IOException
	 */
	public boolean rename(byte[] projectCode,byte[] fileName,byte[] docName,byte[] newName)throws IOException;
	/**
	 * 编辑资源
	 * @param projectCode
	 * @param fileName
	 * @param docName
	 * @param content
	 * @throws IOException
	 */
	public void editSource(byte[] projectCode,byte[] fileName,byte[] docName,byte[] content) throws IOException;
	/**
	 * 删除一个资源
	 * @param path
	 * @param command
	 * @throws IOException
	 */
	public  void deleteFileByPath(byte[] path) throws IOException;
	/**
	 * 删除文档
	 * @param projectCode
	 * @param fileName
	 * @param docName
	 * @param command
	 * @throws IOException
	 */
	public void deleteDoc(byte[] projectCode,byte[] fileName,byte[] docName) throws IOException;
	/**
	 * 创建文件
	 * @param projectCode
	 * @param fileName
	 * @param docName
	 * @throws IOException
	 */
	public void createNewFile(byte[] projectCode,byte[] fileName,byte[] docName) throws IOException;
	/**
	 * 获取服务器状态信息
	 * @return
	 */
	public SynState getServerState();
	/**
	 * 开始采集
	 * @param bflag
	 * @throws Exception
	 */
	public void startCrawl(byte[] bflag) throws Exception ;
	/**
	 * 停止采集
	 * @throws Exception
	 */
	public void stopCrawl(byte[] bflag) throws Exception;
	/**
	 * 修改爬虫状态
	 * @param crawl_status
	 * @throws IOException
	 */
	public void setCrawl_status(byte[] crawl_status) throws IOException;
	/**
	 * 写索引
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
	 * 爬行完后提交
	 * @param auto
	 * @throws Exception
	 */
	public void commit() throws Exception;
	/**
	 * 数据推送的索引合并
	 * @throws Exception
	 */
	public void pushDataMerger() throws Exception;
	/**
	 * webservice删除索引
	 * @param field
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public long deleteOneIndexByField(byte[] field, byte[] value)throws IOException;
	/**
	 * 添加去重信息
	 * @throws IOException
	 */
	public void addContent(byte[] md5Content) throws Exception;
	/**
	 * 节点断掉时没有索引的信息加入索引
	 * @param bDocumetnList
	 * @throws Exception
	 */
	public void addDocument(byte[] bDocumetnList) throws Exception;
	public void dataPro(IndexMessage im) throws Exception;
//	/**
//	 * 停止文件采集
//	 * @return
//	 * @throws Exception
//	 */
//	public void stopFileCrawler() throws Exception;
//	/**
//	 * 停止数据库采集
//	 * @return
//	 * @throws Exception
//	 */
//	public void stop() throws Exception;
}
