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
     * 返回词库文件 列表
     * @return Map
     * @throws Exception
     */
    public Map<String,Object> getWordStackFileList() throws Exception ;
    /**
     * 返回所有词库 词条数量
     * @return int
     * @throws Exception
     */
    public int getWordStackCount() throws Exception ;
    /**
     * 返回词库文件的词条内容
     * @param wordFile String
     * @return Map
     * @throws Exception
     */
    public Map<String,String> getWordMap(String wordFile) throws Exception ;
    /**
     * 搜索词库中是否包含key 的 词条
     * @param key String
     * @return List
     * @throws Exception
     */
    public List<String> searchWord(String key) throws Exception ;
    /**
     * 插入新词到内存词库
     * @param word String
     * @throws Exception
     */
    public void putNewWord(String word) throws Exception ;
    /**
     * 插入新词到词库文件
     * @param word String
     * @throws Exception
     */
    public void putNewWordFile(String word) throws Exception ;

}
