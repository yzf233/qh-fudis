/*
 * $Id: IDaoManager.java 421119 2007-03-07 00:49:11Z jaddy $
 *
 * Copyright 2006-2007 Beijing Xdtech Inc.
 *
 * All rights reserved. �����ߵ�Ƽ����޹�˾
 */

package com.xx.platform.dao;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.nutch.ipc.RPC;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.InstantiationException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleStateException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.domain.model.system.ProjectUser;
import com.xx.platform.domain.model.system.Xdtechsite;
import com.xx.platform.util.dao.DCriteriaPageSupport;

/**
 * 
 * <p>
 * Title: �����ߵ�Ƽ����޹�˾ ���ݷ�����
 * </p>
 * 
 * <p>
 * Description: �����ߵ�Ƽ����޹�˾ ���ݷ�����, ͨ�� Spring ���� ��ǿ�� HibernateDaoSupport �� *
 * ��װHibernate �� ͳһ�����ݷ��ʽӿ� , ����ҳ
 * </p>
 * 
 * <p>
 * Copyright: xdtech ,ltd Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company: �����ߵ�Ƽ����޹�˾
 * </p>
 * 
 * @author jaddy0302 date 2007-03-07 00:10:39
 * @version 1.0
 */
public class IDaoManager<T, P, PK extends Serializable> extends
		HibernateDaoSupport implements GeneraDAO<T, P, PK> {
	/**
	 * <p>
	 * �־û�ҵ���߼����ݶ��� ���޷���ֵ
	 * </p>
	 */
	public void saveIObject(final T object) {
		saveIObject(object, "1");
	}

	@SuppressWarnings("unchecked")
	public void saveIObject(final T object, String command) {
		getHibernateTemplate().save(object);
		if (!(object instanceof Synchro) && command.equals("1")) {
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.save(ObjectToArrays(object));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
	}

	/**
	 * <p>
	 * �־û�ҵ���߼����ݶ����������߸��� ���޷���ֵ
	 * </p>
	 * 
	 * @param object
	 *            T ҵ���߼����ݶ���
	 */
	public void saveOrUpdateIObject(final T object) {
		saveOrUpdateIObject(object, "1");
	}

	public void saveOrUpdateIObject(final T object, String command) {
		String id=null;
		try {
			Method idMethod = object.getClass().getMethod("getId");
			id=(String)idMethod.invoke(object);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		try{
			getHibernateTemplate().saveOrUpdate(object);
		}catch(StaleStateException e){
			
		}
		if (!(object instanceof Synchro) && command.equals("1")) {
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0){
				if(id==null&&id.trim().length()>0){
					//���
					for (Synchro s : synchro) {// ����ÿ���ڵ�
						try {
							((ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut)).save(ObjectToArrays(object));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else{
					//�޸�
					for (Synchro s : synchro) {// ����ÿ���ڵ�
						try {
							((ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut)).update(ObjectToArrays(object));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * �־û������� ɾ�����ݿ��е����ݼ�¼
	 * </p>
	 * 
	 * @param object
	 *            T
	 */
	public void deleteIObject(final T object) {
		deleteIObject(object, "1");
	}

	public void deleteIObject(final T object, String command) {
		if (!(object instanceof Synchro) && command.equals("1")) {
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.delete(ObjectToArrays(object));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		try{
			getHibernateTemplate().delete(object);
		}catch(StaleStateException e){
			
		}
	}

	/**
	 * <p>
	 * ���PK
	 * </p>
	 * 
	 * @param iClass
	 *            Class
	 * @param id
	 *            PK
	 * @return T
	 */
	public T getIObjectByPK(final Class iClass, final PK iPK) {
		return (T) getHibernateTemplate().load(iClass, iPK);
	}

	/**
	 * ���� ����
	 * 
	 * @param object
	 *            T
	 */
	public void updateIObject(T object) {
		updateIObject(object, "1");
	}

	public void updateIObject(T object, String command) {
		if(object instanceof Xdtechsite&&!command.equals("1")){
			List<Xdtechsite> sites=(List<Xdtechsite>)findAllByIObjectCType(Xdtechsite.class);
			Xdtechsite site=sites.remove(0);
			Xdtechsite saveObject=(Xdtechsite)object;
			saveObject.setLocalip(site.getLocalip());
			saveObject.setSearchdir(site.getSearchdir());
			getHibernateTemplate().update(saveObject);
		}else{
			if (!(object instanceof Synchro) && command.equals("1")) {
				List<Synchro> synchro = SearchContext.getSynchroList();
				if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
						&& synchro.size() > 0)
					for (Synchro s : synchro) {// ����ÿ���ڵ�
						try {
							((ImInterface) RPC.getProxy(ImInterface.class,
									ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
									.update(ObjectToArrays(object));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			}
			try{
				getHibernateTemplate().update(object);
			}catch(StaleStateException e){
				
			}
		}
	}

	/**
	 * ��������ݶ������Ͳ�������
	 * 
	 * @param iClass
	 *            Class
	 * @return List
	 */
	public List<T> findAllByIObjectCType(final Class iClass) {
		return getHibernateTemplate().find("from " + iClass.getName());
	}

	public List<T> findByIObjectCType(final Class iClass, final int page,
			final int pageSize) {
		return findPageByCriteria(DetachedCriteria.forClass(iClass), pageSize,
				page);
	}

	/**
	 * Hibernate ���� ��ѯ HQL
	 * 
	 * @param iNameQuery
	 *            String
	 * @return List
	 */
	public List<T> findByNamedQuery(final String iNameQuery) {
		return getHibernateTemplate().findByNamedQuery(iNameQuery);
	}

	/**
	 * Hibernate ���� ��ѯ HQL
	 * 
	 * @param iNameQuery
	 *            String
	 * @return List
	 */
	public int execByHQL(final String hSQL) throws Exception {
		return execByHQL(hSQL,"1");
	}
	public int execByHQL(final String hSQL,String command) throws Exception {
		int n=0;
		if (command.equals("1")) {
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.execByHQL(ObjectToArrays(hSQL));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		n=getHibernateTemplate().bulkUpdate(hSQL);
		return n; 
	}

	/**
	 * �÷���δʵ��ͬ��
	 * Hibernate ���� ��ѯ HQL
	 * 
	 * @param iNameQuery
	 *            String
	 * @return List
	 */
	public int execByHQL(final String hSQL, T[] t) {
		return getHibernateTemplate().bulkUpdate(hSQL, t);
	}

	/**
	 * HQL ��ѯ �� ������
	 * 
	 * @param iQuery
	 *            String
	 * @param param
	 *            P
	 * @return List
	 */
	public List<T> findByNamedQuery(final String iQuery, final P param) {
		return getHibernateTemplate().findByNamedQuery(iQuery, param);
	}

	/**
	 * HQL��ѯ�� ���������
	 * 
	 * @param iQuery
	 *            String
	 * @param params
	 *            P[]
	 * @return List
	 */
	public List<T> findByNamedQuery(final String iQuery, final P[] params) {
		return getHibernateTemplate().findByNamedQuery(iQuery, params);
	}

	/**
	 * HQL ��ѯ �������ѯ ���
	 * 
	 * @param iQuery
	 *            String
	 * @return List
	 */
	public List<T> findByQuery(final String iQuery) {
		return getHibernateTemplate().find(iQuery);
	}

	/**
	 * ����HQL ��ѯ��䣬 ������
	 * 
	 * @param iQuery
	 *            String
	 * @param param
	 *            P
	 * @return List
	 */
	public List<T> findByQuery(final String iQuery, final P param) {
		return getHibernateTemplate().find(iQuery, param);
	}

	/**
	 * ����DetachedCriteria �� ����ҳ��Ϣ
	 * 
	 * @param detachedCriteria
	 *            DetachedCriteria
	 * @return DCriteriaPageSupport
	 */
	public List<T> findPageByCriteria(final DetachedCriteria detachedCriteria) {
		return findPageByCriteria(detachedCriteria,
				DCriteriaPageSupport.I_PAGE_SIZE, 0);
	}

	/**
	 * DetachedCriteria ����ҳ��Ϣ �� ָ����ʵλ��
	 * 
	 * @param detachedCriteria
	 *            DetachedCriteria
	 * @param startIndex
	 *            int
	 * @return DCriteriaPageSupport
	 */
	public List<T> findPageByCriteria(final DetachedCriteria detachedCriteria,
			final int page) {
		return findPageByCriteria(detachedCriteria,
				DCriteriaPageSupport.I_PAGE_SIZE, page);
	}

	/**
	 * DetachedCriteria ����ҳ��Ϣ ��ָ����ʼλ��
	 * 
	 * @param detachedCriteria
	 *            DetachedCriteria
	 * @param pageSize
	 *            int
	 * @param startIndex
	 *            int
	 * @return DCriteriaPageSupport
	 */
	public List<T> findPageByCriteria(final DetachedCriteria detachedCriteria,
			final int pageSize, final int page) {
		return findPageByCriteria(detachedCriteria, pageSize,
				(page > 0) ? (page - 1) * pageSize : page * pageSize, true);
	}

	/**
	 * DetachedCriteria ����ҳ��Ϣ ��ָ����ʼλ��
	 * 
	 * @param detachedCriteria
	 *            DetachedCriteria
	 * @param pageSize
	 *            int
	 * @param startIndex
	 *            int
	 * @return DCriteriaPageSupport
	 */
	@SuppressWarnings("unchecked")
	private List<T> findPageByCriteria(final DetachedCriteria detachedCriteria,
			final int pageSize, final int startIndex, boolean isPage) {
		return (DCriteriaPageSupport) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {

						Criteria criteria = detachedCriteria
								.getExecutableCriteria(session);
						List orderList = ((CriteriaImpl) criteria)
								.getOrderList();
						((CriteriaImpl) criteria).setOrderList(new ArrayList());
						int totalCount = (Integer) criteria.setProjection(
								Projections.rowCount()).uniqueResult();
						criteria.setProjection(null);

						((CriteriaImpl) criteria).setOrderList(orderList);
						DCriteriaPageSupport ps = new DCriteriaPageSupport(
								criteria.setFirstResult(startIndex)
										.setMaxResults(pageSize).list(),
								totalCount, pageSize, startIndex);
						return ps;
					}
				}, true);
	}

	/**
	 * 
	 * detachedCriteria ��ѯ
	 * 
	 * @param detachedCriteria
	 *            DetachedCriteria
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAllByCriteria(final DetachedCriteria detachedCriteria) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Criteria criteria = detachedCriteria
						.getExecutableCriteria(session);
				return criteria.list();
			}
		}, true);
	}

	/**
	 * detachedCriteria ��ѯ���м�¼��
	 * 
	 * @param detachedCriteria
	 *            DetachedCriteria
	 * @return int
	 */
	public int getCountByCriteria(final DetachedCriteria detachedCriteria) {
		Integer count = (Integer) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Criteria criteria = detachedCriteria
								.getExecutableCriteria(session);
						return criteria.setProjection(Projections.rowCount())
								.uniqueResult();
					}
				}, true);
		return count;
	}

	/**
	 * DetachedCriteria ����ҳ��Ϣ ��ָ����ʼλ��
	 * 
	 * @param detachedCriteria
	 *            DetachedCriteria
	 * @param pageSize
	 *            int
	 * @param startIndex
	 *            int
	 * @return DCriteriaPageSupport
	 */
	@SuppressWarnings("unchecked")
	public List<T> hqlList(final String sql, final Class clazz,
			final int pageSize) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Query query = session.createQuery(sql);
				query.setMaxResults(pageSize);
				Iterator iterator = query.list().iterator();
				List clazzList = new ArrayList();
				while (iterator.hasNext())
					clazzList.add(iterator.next());
				return clazzList;
			}
		}, true);
	}

	/**
	 * hql ��ѯ ����ҳ��Ϣ quhuan 2007/10/11.
	 * 
	 * @param sql
	 *            String
	 * @param endIndex
	 *            int -1 ������
	 * @param startIndex
	 *            int -1 ������
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	public List<T> hqlList(final String sql, final int startIndex,
			final int endIndex) {
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				Query query = session.createQuery(sql);
				if (startIndex >= 0)
					query.setFirstResult(startIndex);
				if (endIndex >= 0)
					query.setMaxResults(endIndex);
				Iterator iterator = query.list().iterator();
				List clazzList = new ArrayList();
				while (iterator.hasNext())
					clazzList.add(iterator.next());
				return clazzList;
			}
		}, true);
	}

	private byte[] ObjectToArrays(Object dataObject) throws IOException {
		java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
		java.io.ObjectOutputStream objOut = new java.io.ObjectOutputStream(
				baout);
		objOut.writeObject(dataObject);
		byte[] data = baout.toByteArray();
		objOut.close();
		baout.close();
		return data;
	}

	/**
	 * �Զ���װ
	 * 
	 * @param hql:��ѯ���
	 * @param field����Ҫ��ֵ������
	 * @param startIndex����ʼΪֹ
	 * @param endIndex������Ϊֹ
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> hqlListBox(final Class clazz, final String hql,
			final FunctionParameters[] fields, final int startIndex,
			final int endIndex) {
		return (List<T>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						if (startIndex >= 0)
							query.setFirstResult(startIndex);
						if (endIndex >= 0)
							query.setMaxResults(endIndex);
						Iterator iterator = query.list().iterator();
						List<T> clazzList = new ArrayList<T>();
						while (iterator.hasNext()) {
							Object instance = null;
							try {
								instance = clazz.newInstance();
								Object[] objs = (Object[]) iterator.next();
								int i = 0;
								for (Object o : objs) {
									box(instance, clazz, fields[i], o);
									i++;
								}
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (java.lang.InstantiationException e) {
								e.printStackTrace();
							}
							clazzList.add((T) instance);
						}
						return clazzList;
					}
				}, true);
	}
	private void box(Object instance, final Class<?> clazz,
			FunctionParameters field, Object value)
			throws java.lang.InstantiationException {
		try {
			Method method = field.getMethod(clazz);
			method.invoke(instance, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> sqlListBox(final Class clazz, final String sql,
			final FunctionParameters[] fields, final int startIndex,
			final int endIndex) {
		return (List<T>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createSQLQuery(sql);
						if (startIndex >= 0)
							query.setFirstResult(startIndex);
						if (endIndex >= 0)
							query.setMaxResults(endIndex);
						Iterator iterator = query.list().iterator();
						List<T> clazzList = new ArrayList<T>();
						while (iterator.hasNext()) {
							Object instance = null;
							Object o = iterator.next();
							Object[] values = (Object[]) o;
							int i = -1;
							try {
								instance = clazz.newInstance();
							} catch (java.lang.InstantiationException e1) {
								e1.printStackTrace();
							} catch (IllegalAccessException e1) {
								e1.printStackTrace();
							}
							for (Object value : values) {
								i++;
								if (value instanceof java.lang.Number) {
									Long tempValue = 0l;
									try {
										tempValue = (Long) value.getClass()
												.getMethod("longValue").invoke(
														value);
									} catch (IllegalArgumentException e) {
										e.printStackTrace();
									} catch (SecurityException e) {
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									} catch (InvocationTargetException e) {
										e.printStackTrace();
									} catch (NoSuchMethodException e) {
										e.printStackTrace();
									}
									try {
										box(instance, clazz, fields[i],
												tempValue);
									} catch (java.lang.InstantiationException e) {
										e.printStackTrace();
									}
								} else {
									try {
										box(instance, clazz, fields[i], value);
									} catch (java.lang.InstantiationException e) {
										e.printStackTrace();
									}
								}
							}
							clazzList.add((T) instance);
						}
						return clazzList;
					}
				}, true);
	}

	@SuppressWarnings("unchecked")
	public List<T> hqlListBoxBetween(final Class clazz, final String hql,
			final FunctionParameters[] fields, final int startIndex,
			final int endIndex, final Date startDate, final Date endDate) {
		return (List<T>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						Query query = session.createQuery(hql);
						query.setDate(0, startDate);
						query.setDate(1, endDate);
						if (startIndex >= 0)
							query.setFirstResult(startIndex);
						if (endIndex >= 0)
							query.setMaxResults(endIndex);
						Iterator iterator = query.list().iterator();
						List<T> clazzList = new ArrayList<T>();
						while (iterator.hasNext()) {
							Object instance = null;
							try {
								instance = clazz.newInstance();
								Object[] objs = (Object[]) iterator.next();
								int i = 0;
								for (Object o : objs) {
									box(instance, clazz, fields[i], o);
									i++;
								}
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (java.lang.InstantiationException e) {
								e.printStackTrace();
							}
							clazzList.add((T) instance);
						}
						return clazzList;
					}
				}, true);
	}

	/**
	 * ���session
	 * 
	 * @return
	 */
	public Session getIDaoSession() {
		return getSession();
	}

	/**
	 * 
	 * @param auto
	 * @throws Exception
	 */
	public void setAutoSubmit(Session session, boolean auto) throws Exception {
		session.connection().setAutoCommit(auto);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void commitTran(Session session) throws Exception {
		session.connection().commit();
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void rollbackTran(Session session) throws Exception {
		session.connection().rollback();
	}

	/**
	 * ɾ��һ���û�
	 * 
	 * @param projectUser
	 * @param relation
	 */
	public void deleteUser(ProjectUser projectUser) {
		deleteUser(projectUser,"1");
	}
	public void deleteUser(ProjectUser projectUser,String commond) {
		if(commond=="1"){
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.deleteUser(ObjectToArrays(projectUser));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		Session session = getIDaoSession();
		try {
			setAutoSubmit(session, false);
			session.beginTransaction();
			String userId = projectUser.getId();
			String hql1 = "delete from ProjectUser where id='" + userId + "'";
			String hql2 = "delete from Relation where userid='" + userId + "'";
			Query query = session.createQuery(hql1);
			query.executeUpdate();
			Query query1 = session.createQuery(hql2);
			query1.executeUpdate();
			commitTran(session);
		} catch (Exception e) {
			try {
				rollbackTran(session);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			session.close();
		}
	}
	/**
	 * ���һ���û�ɾ��
	 */
	public void inserBat(List<T> list){
		inserBat(list,"1");
	}
	public void inserBat(List<T> list,String commond){
		if(commond=="1"){
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.inserBat(ObjectToArrays(list));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		Session session=getIDaoSession();
		int count=list.size();
		for(int i=0;i<count;i++){
			session.save(list.get(i));
			if(i>0&&i%100==0){
				session.flush();	
			}
		}
		session.flush();
		session.close();
	}
}
