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
     * 获得 字段名称
     * @return String
     * @throws Exception
     */
    public String getName() throws Exception;

    /**
     * 获得字段代码
     * @return String
     * @throws Exception
     */
    public String getCode() throws Exception;

    /**
     * 获得与该索引域 数据类型 , 数据类型从 DataType 中获得
     * @return CrawlRule
     * @throws Exception
     */
    public int getDataType() throws Exception;
    /**
     * 获得索引方式 ， 数字、日期、字符串、浮点数 数据类型从 DataType 中获得
     * @return Object
     * @throws Exception
     */
    public int getIndexType() throws Exception;
    /**
     * 获得与该索引域 对应的 抽取字段
     * @return CrawlRule
     * @throws Exception
     */
    public CrawlRule getCrawlRule() throws Exception;

    /**
     * 是否索引
     * @return boolean
     * @throws Exception
     */
    public boolean isIndex() throws Exception;

    /**
     * 是否存储
     * @return boolean
     * @throws Exception
     */
    public boolean isStorge();

    /**
     * 是否 切分词
     * @return boolean
     * @throws Exception
     */
    public boolean isToken();

    /**
     * 分词算法类别
     * @return boolean
     * @throws Exception
     */
    public int getTokentype();
    /**
     * 打分分值
     * @return boolean
     * @throws Exception
     */
    public int getBoost();
}
