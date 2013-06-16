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
   * �������ݺ���
   * @param docType String   ���� Ĭ��Ϊ page
   * @param field String[]   �������� �ֶ� �� �� value��Ӧ
   * @param value String[]   �������� ֵ   �� �� field��Ӧ
   * @return int             ����ֵ 0:����У����� , ���ڸ�ʽ����� docType/subDocType/title/context/sourceHost �е�ĳЩΪNULL���߿��ַ� ��1:�������ݳɹ� ;-1:����ʧ�ܣ�ϵͳĿǰ����ִ�в������ݲ����������������ݣ����Ժ�����������
   * @throws Exception
   */
  @WebResult(name = "push")
  public int push(String docType,  String[] field, String[][] value) throws Exception;

  /**
   * �޸�����
   * @param delField String ���޸����ݵ�field��Ψһ�Ҳ�����ֶΣ�
   * @param delValue String ���޸����ݵ�field��ֵ��Ψһ�Ҳ�����ֶΣ�
   * @param docType String  ������������docType
   * @param title String    ������������title
   * @param field String[]
   * @param value String[]
   * @param url String
   * @return int
   * @throws Exception
   */
  @WebResult(name = "updateByField")
  public int updateByField(String delField,String delValue,String docType,  String[] field, String[] value) throws Exception;

  /**
   * �޸�����
   * @param docId int ���޸����ݵ�id
   * @param docType String  ������������docType
   * @param title String    ������������title
   * @param field String[]
   * @param value String[]
   * @param url String
   * @return int
   * @throws Exception
   */
  @WebResult(name = "updateById")
  public int updateById(int docId,String docType,  String[] field, String[] value) throws Exception;

  /**
   * ��������
   * @param docId int
   * @return boolean
   * @throws Exception
   */
  @WebResult(name = "merger")
  public void merger() throws Exception ;
}
