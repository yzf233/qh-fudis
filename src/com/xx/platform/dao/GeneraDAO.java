package com.xx.platform.dao;

import java.io.*;
import java.util.*;
import org.hibernate.criterion.*;

public interface GeneraDAO<T, P, PK extends Serializable> extends IBaseDAO<T , P , PK>
{
    /**
     * 执行自定义HQL
     * @param hSQL String
     * @return int
     * @throws Exception
     */
    public int execByHQL(final String hSQL) throws Exception ;

    /**
     * 执行自定义HQL
     * @param hSQL String
     * @return int
     * @throws Exception
     */
    public int execByHQL(final String hSQL , T[] t) throws Exception ;
    /**
     * 执行自定义HQL
     * @param hSQL String
     * @return int
     * @throws Exception
     */
    public List<T> hqlList(String keyword ,Class clazz, int page_size) ;
    /**
     * Hibernate 配置 查询 HQL
     * @param iNameQuery String
     * @return List
     */
    public List<T> findByNamedQuery(final String iNameQuery);

    /**
     * HQL 查询 ， 带参数
     * @param iQuery String
     * @param param P
     * @return List
     */
    public List<T> findByNamedQuery(final String iQuery, final P param);

    /**
     * HQL查询， 带多个参数
     * @param iQuery String
     * @param params P[]
     * @return List
     */
    public List<T> findByNamedQuery(final String iQuery,
                                    final P[] params);

    /**
     * HQL 查询 ，构造查询 语句
     * @param iQuery String
     * @return List
     */
    public List<T> findByQuery(final String iQuery);

    /**
     * 构造HQL 查询语句， 带参数
     * @param iQuery String
     * @param param P
     * @return List
     */
    public List<T> findByQuery(final String iQuery, final P param);

    /**
     * 构造DetachedCriteria ， 带分页信息
     * @param detachedCriteria DetachedCriteria
     * @return DCriteriaPageSupport
     */
    public List<T> findPageByCriteria(
            final DetachedCriteria detachedCriteria);

    /**
     * DetachedCriteria 带分页信息 ， 指定其实位置
     * @param detachedCriteria DetachedCriteria
     * @param startIndex int
     * @return DCriteriaPageSupport
     */
    public List<T> findPageByCriteria(
            final DetachedCriteria detachedCriteria, final int startIndex);

    /**
     * DetachedCriteria 带分页信息 ，指定开始位置
     * @param detachedCriteria DetachedCriteria
     * @param pageSize int
     * @param startIndex int
     * @return DCriteriaPageSupport
     */
    public List<T> findPageByCriteria(
            final DetachedCriteria detachedCriteria, final int pageSize,
            final int startIndex);

    /**
     *
     *  detachedCriteria 查询
     * @param detachedCriteria DetachedCriteria
     * @return List
     */
    public List<T> findAllByCriteria(
            final DetachedCriteria detachedCriteria);

    /**
     * detachedCriteria 查询所有记录数
     * @param detachedCriteria DetachedCriteria
     * @return int
     */
    public int getCountByCriteria(
            final DetachedCriteria detachedCriteria);

}
