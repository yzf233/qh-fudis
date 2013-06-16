package com.xx.platform.core.nutch;

import com.xx.platform.core.nutch.CrawlRule;

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
public interface IndexField {
    /**
     * ��� �ֶ�����
     * @return String
     * @throws Exception
     */
    public String getName() throws Exception;

    /**
     * ����ֶδ���
     * @return String
     * @throws Exception
     */
    public String getCode() throws Exception;

    /**
     * ������������ �������� , �������ʹ� DataType �л��
     * @return CrawlRule
     * @throws Exception
     */
    public int getDataType() throws Exception;
    /**
     * ���������ʽ �� ���֡����ڡ��ַ����������� �������ʹ� DataType �л��
     * @return Object
     * @throws Exception
     */
    public int getIndexType() throws Exception;
    /**
     * ������������ ��Ӧ�� ��ȡ�ֶ�
     * @return CrawlRule
     * @throws Exception
     */
    public CrawlRule getCrawlRule() throws Exception;

    /**
     * �Ƿ�����
     * @return boolean
     * @throws Exception
     */
    public boolean isIndex() throws Exception;

    /**
     * �Ƿ�洢
     * @return boolean
     * @throws Exception
     */
    public boolean isStorge();

    /**
     * �Ƿ� �зִ�
     * @return boolean
     * @throws Exception
     */
    public boolean isToken();

    /**
     * �ִ��㷨���
     * @return boolean
     * @throws Exception
     */
    public int getTokentype();
    /**
     * ��ַ�ֵ
     * @return boolean
     * @throws Exception
     */
    public int getBoost();
}
