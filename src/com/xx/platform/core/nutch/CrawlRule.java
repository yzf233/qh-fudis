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
     * 获得 解析字段 名称
     * @return String
     */
    public String getName() ;
    /**
     * 获得解析字段代码
     * @return String
     */
    public String getCode() ;
    /**
     * 数据类型从 DataType 中获得
     * @return int
     */
    public int getDataType() ;

    /**
     * 获得数据值
     * @return String
     */
    public String getValue() ;
    /**
     * 字段是否在 Fetch文件中存储 ，如果不存储 ，则该字段也不能被索引
     * @return boolean
     * @throws Exception
     */
    public boolean isStorge();
    /**
     * 字段编码
     * @param code String
     */
    public void setCode(String code) ;
    /**
     * 字段值
     * @param value String
     */
    public void setValue(String value) ;
}
