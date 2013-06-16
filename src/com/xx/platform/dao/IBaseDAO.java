package com.xx.platform.dao;

/**
 * <p>Title: ���ݷ��� ���� ���� ֧��</p>
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
import java.io.*;
import java.util.*;

public interface IBaseDAO<T, P, PK extends Serializable> extends IBase<T, P, PK> {



    /**
     * ����һ���¶������ ����һ������
     * @param object T
     * @return T
     */
    public void saveOrUpdateIObject(final T object);

    /**
     * ��� PK ֵ�� primaryKey�� ���ݼ�¼
     * @param primaryKey PK
     * @return T
     */
    public T getIObjectByPK(final Class iClass, PK primaryKey);

    /**
     *
     * @param example T
     * @return List
     */
    public List<T> findAllByIObjectCType(final Class iClass);

    /**
     *
     * @param example T
     * @param first int
     * @param max int
     * @return List
     */
    public List<T> findByIObjectCType(final Class iClass, final int page,final int pageSize);


    /**
     *
     * @param object T
     */
    public void updateIObject(final T object);

    /**
     *
     * @param object T
     */
    public void deleteIObject(final T object);
}
