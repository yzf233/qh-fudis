package com.xx.platform.core.service;

import javax.jws.WebService;
import javax.jws.WebResult;

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
@WebService(targetNamespace = "http://www.xd-tech.com.cn")
public interface IndexPushInterface {
   /**
   * 推送数据函数
   * @param docType String   分类 默认为 page
   * @param field String[]   推送数据 字段 ， 和 value对应
   * @param value String[]   推送数据 值   ， 和 field对应
   * @return int             返回值 0:数据校验错误 , 日期格式错误或 docType/subDocType/title/context/sourceHost 中的某些为NULL或者空字符 ；1:推送数据成功 ;-1:推送失败，系统目前正在执行并入数据操作，不接受新数据，请稍候在推送数据
   * @throws Exception
   */
  @WebResult(name = "push")
  public int push(String docType,  String[] field, String[][] value) throws Exception;

  /**
   * 修改数据
   * @param delField String 待修该数据的field（唯一且不变的字段）
   * @param delValue String 待修该数据的field的值（唯一且不变的字段）
   * @param docType String  新数据所属的docType
   * @param title String    新数据所属的title
   * @param field String[]
   * @param value String[]
   * @param url String
   * @return int
   * @throws Exception
   */
  @WebResult(name = "updateByField")
  public int updateByField(String delField,String delValue,String docType,  String[] field, String[] value) throws Exception;

  /**
   * 修改数据
   * @param docId int 待修该数据的id
   * @param docType String  新数据所属的docType
   * @param title String    新数据所属的title
   * @param field String[]
   * @param value String[]
   * @param url String
   * @return int
   * @throws Exception
   */
  @WebResult(name = "updateById")
  public int updateById(int docId,String docType,  String[] field, String[] value) throws Exception;

  /**
   * 并入索引
   * @param docId int
   * @return boolean
   * @throws Exception
   */
  @WebResult(name = "merger")
  public void merger() throws Exception ;
}
