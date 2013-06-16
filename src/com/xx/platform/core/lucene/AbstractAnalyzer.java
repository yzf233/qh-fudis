package com.xx.platform.core.lucene;

import java.util.Map;
import java.util.List;

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
public interface AbstractAnalyzer {
    /**
     * ���شʿ��ļ� �б�
     * @return Map
     * @throws Exception
     */
    public Map<String,Object> getWordStackFileList() throws Exception ;
    /**
     * �������дʿ� ��������
     * @return int
     * @throws Exception
     */
    public int getWordStackCount() throws Exception ;
    /**
     * ���شʿ��ļ��Ĵ�������
     * @param wordFile String
     * @return Map
     * @throws Exception
     */
    public Map<String,String> getWordMap(String wordFile) throws Exception ;
    /**
     * �����ʿ����Ƿ����key �� ����
     * @param key String
     * @return List
     * @throws Exception
     */
    public List<String> searchWord(String key) throws Exception ;
    /**
     * �����´ʵ��ڴ�ʿ�
     * @param word String
     * @throws Exception
     */
    public void putNewWord(String word) throws Exception ;
    /**
     * �����´ʵ��ʿ��ļ�
     * @param word String
     * @throws Exception
     */
    public void putNewWordFile(String word) throws Exception ;

}
