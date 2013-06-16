package com.xx.platform.dao;

import java.io.*;
import java.util.*;
import org.hibernate.criterion.*;

public interface GeneraDAO<T, P, PK extends Serializable> extends IBaseDAO<T , P , PK>
{
    /**
     * ִ���Զ���HQL
     * @param hSQL String
     * @return int
     * @throws Exception
     */
    public int execByHQL(final String hSQL) throws Exception ;

    /**
     * ִ���Զ���HQL
     * @param hSQL String
     * @return int
     * @throws Exception
     */
    public int execByHQL(final String hSQL , T[] t) throws Exception ;
    /**
     * ִ���Զ���HQL
     * @param hSQL String
     * @return int
     * @throws Exception
     */
    public List<T> hqlList(String keyword ,Class clazz, int page_size) ;
    /**
     * Hibernate ���� ��ѯ HQL
     * @param iNameQuery String
     * @return List
     */
    public List<T> findByNamedQuery(final String iNameQuery);

    /**
     * HQL ��ѯ �� ������
     * @param iQuery String
     * @param param P
     * @return List
     */
    public List<T> findByNamedQuery(final String iQuery, final P param);

    /**
     * HQL��ѯ�� ���������
     * @param iQuery String
     * @param params P[]
     * @return List
     */
    public List<T> findByNamedQuery(final String iQuery,
                                    final P[] params);

    /**
     * HQL ��ѯ �������ѯ ���
     * @param iQuery String
     * @return List
     */
    public List<T> findByQuery(final String iQuery);

    /**
     * ����HQL ��ѯ��䣬 ������
     * @param iQuery String
     * @param param P
     * @return List
     */
    public List<T> findByQuery(final String iQuery, final P param);

    /**
     * ����DetachedCriteria �� ����ҳ��Ϣ
     * @param detachedCriteria DetachedCriteria
     * @return DCriteriaPageSupport
     */
    public List<T> findPageByCriteria(
            final DetachedCriteria detachedCriteria);

    /**
     * DetachedCriteria ����ҳ��Ϣ �� ָ����ʵλ��
     * @param detachedCriteria DetachedCriteria
     * @param startIndex int
     * @return DCriteriaPageSupport
     */
    public List<T> findPageByCriteria(
            final DetachedCriteria detachedCriteria, final int startIndex);

    /**
     * DetachedCriteria ����ҳ��Ϣ ��ָ����ʼλ��
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
     *  detachedCriteria ��ѯ
     * @param detachedCriteria DetachedCriteria
     * @return List
     */
    public List<T> findAllByCriteria(
            final DetachedCriteria detachedCriteria);

    /**
     * detachedCriteria ��ѯ���м�¼��
     * @param detachedCriteria DetachedCriteria
     * @return int
     */
    public int getCountByCriteria(
            final DetachedCriteria detachedCriteria);

}
