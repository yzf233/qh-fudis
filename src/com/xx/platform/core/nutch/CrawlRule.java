package com.xx.platform.core.nutch;

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
public interface CrawlRule {
    /**
     * ��� �����ֶ� ����
     * @return String
     */
    public String getName() ;
    /**
     * ��ý����ֶδ���
     * @return String
     */
    public String getCode() ;
    /**
     * �������ʹ� DataType �л��
     * @return int
     */
    public int getDataType() ;

    /**
     * �������ֵ
     * @return String
     */
    public String getValue() ;
    /**
     * �ֶ��Ƿ��� Fetch�ļ��д洢 ��������洢 ������ֶ�Ҳ���ܱ�����
     * @return boolean
     * @throws Exception
     */
    public boolean isStorge();
    /**
     * �ֶα���
     * @param code String
     */
    public void setCode(String code) ;
    /**
     * �ֶ�ֵ
     * @param value String
     */
    public void setValue(String value) ;
}
