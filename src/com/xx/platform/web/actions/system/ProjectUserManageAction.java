package com.xx.platform.web.actions.system;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.Action;
import com.xx.platform.dao.IDaoManager;
import com.xx.platform.domain.model.system.ProjectUser;
import com.xx.platform.util.tools.MD5;
import com.xx.platform.web.actions.BaseAction;

public class ProjectUserManageAction extends BaseAction{
	private int curPage=1;
	private int pageSize=10;
	private ProjectUser projectUser;
	private List<ProjectUser> projectUserList=new ArrayList<ProjectUser>();;
	@SuppressWarnings("unchecked")
	public String search(){
		projectUserList=service.findByIObjectCType(ProjectUser.class, curPage, pageSize);
		return Action.SUCCESS;
	}
	public String insert(){
		if(projectUser==null||projectUser.getUsername()==null||projectUser.getUsername().trim()==null||projectUser.getUserpassword()==null||projectUser.getUserpassword().trim()==""||projectUser.getShowname()==null||projectUser.getShowname().trim()==""){
			message="����Ϊ�յ��ֶ������˿�ֵ��";
			return Action.SUCCESS;
		}else{
			int n=service.getCountByCriteria(DetachedCriteria.forClass(ProjectUser.class).add(Restrictions.eq("username",projectUser.getUsername())));
			if(n>0){
				message="���û����Ѿ���ռ�ã����ʧ�ܣ�";
			}else{
				projectUser.setUserpassword(MD5.encoding(projectUser.getUserpassword()));
				service.saveIObject(projectUser);
			}
		}
		search();
		projectUser=null;
		return Action.SUCCESS;
	}
	/**
	 * ɾ��һ���û�
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String delete(){
		IDaoManager dao=(IDaoManager)service;
		dao.deleteUser(projectUser);
		search();
		return Action.SUCCESS;
	}
	/**
	 * �鿴һ���û�
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String viewUser(){
		projectUser=(ProjectUser) service.getIObjectByPK(ProjectUser.class, projectUser.getId());
		return Action.SUCCESS;
	}
	/**
	 * �޸�һ���û�����Ϣ
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String updateUser(){
		ProjectUser temp=(ProjectUser)service.getIObjectByPK(ProjectUser.class, projectUser.getId());
		if(projectUser.getUserpassword()==null||projectUser.getUserpassword().trim().length()==0){
			projectUser.setUserpassword(temp.getUserpassword());
		}else{
			projectUser.setUserpassword(MD5.encoding(projectUser.getUserpassword()));
		}
		if(temp!=null){
			service.updateIObject(projectUser);
		}
		search();
		projectUser=null;
		return Action.SUCCESS;
	}
	public ProjectUser getProjectUser() {
		return projectUser;
	}
	public void setProjectUser(ProjectUser projectUser) {
		this.projectUser = projectUser;
	}
	public List<ProjectUser> getProjectUserList() {
		return projectUserList;
	}
	public void setProjectUserList(List<ProjectUser> projectUserList) {
		this.projectUserList = projectUserList;
	}
	public int getCurPage() {
		return curPage;
	}
	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}
}
