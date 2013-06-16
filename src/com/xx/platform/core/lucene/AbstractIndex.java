package com.xx.platform.core.lucene;

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
public interface AbstractIndex<T> {
    /**
     * »ñµÃ Ë÷Òý×Ö¶Î Óò
     * @return List
     * @throws Exception
     */
    public List<String> getIndexField() throws Exception;
    /**
     * É¾³ý Ë÷Òý×Ö¶Î
     * @param field String
     * @throws Exception
     */
    public void removeIndexField(String field) throws Exception;
    /**
     * ÐÂÔöË÷Òý×Ö¶Î
     * @param t T
     * @throws Exception
     */
    public void putIndexField(T t) throws Exception ;
    /**
     * ÐÞ¸ÄË÷Òý×Ö¶Î
     * @param t T
     * @throws Exception
     */
    public void updateIndexFiled(T t) throws Exception ;
}
