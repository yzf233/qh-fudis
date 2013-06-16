package com.xx.platform.dao;

import java.io.Serializable;

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
public interface IBase<T, P, PK extends Serializable> {
    /**
     * 持久化一个新对象到数据到 数据库
     * @param object T
     * @return T
     */
    public void saveIObject(final T object);

}
