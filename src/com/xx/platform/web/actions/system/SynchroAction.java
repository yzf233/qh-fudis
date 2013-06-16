package com.xx.platform.web.actions.system;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.SearchContext;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.web.actions.BaseAction;

public class SynchroAction 
extends BaseAction {
	 private List<Synchro> synchroList;
	 private Synchro synchro;
	 public List<Synchro> getSynchroList() {
		return synchroList;
	}

	public void setSynchroList(List<Synchro> synchroList) {
		this.synchroList = synchroList;
	}

	public Synchro getSynchro() {
		return synchro;
	}

	public void setSynchro(Synchro synchro) {
		this.synchro = synchro;
	}
	/**
	 *	获得集群列表 
	 * @return
	 */
	public String list() {
		 //synchroList = SearchContext.getSynchroList();
		synchroList=service.findAllByIObjectCType(Synchro.class);
	 	    return Action.SUCCESS;
	 }

	 /**
	  * 添加同步节点
	  * @return
	  * @throws Exception
	  */
	 public String addDo() throws Exception {
		 int n=service.getCountByCriteria(DetachedCriteria.forClass(Synchro.class).add(Restrictions.or(Restrictions.eq("ipaddress",synchro.getIpaddress()),Restrictions.eq("dname",synchro.getDname()))));
		 if(n>0){
			message="节点名称或者节点IP重复！";
			return Action.SUCCESS;
		 }
	 	service.saveIObject(synchro);
	    SearchContext.reloadSynchroList();
	    return Action.SUCCESS;
	}
	/**
	 * 显示某个节点信息
	 * @return
	 * @throws Exception
	 */
    public String edit() throws Exception {
		    synchro = (Synchro) service.getIObjectByPK(Synchro.class,
		    			synchro.getId());
		    return Action.SUCCESS;
	}

    /**
     * 编辑节点
     * @return
     * @throws Exception
     */
	public String editDo() throws Exception {
		    if (synchro != null && synchro.getId() != null) {
		    	int count=service.getCountByCriteria(DetachedCriteria.forClass(Synchro.class).add(Restrictions.and(Restrictions.not(Restrictions.eq("id",synchro.getId())), Restrictions.or(Restrictions.eq("ipaddress",synchro.getIpaddress()),Restrictions.eq("dname",synchro.getDname())))));
		    	if(count>0){
		    		message="节点名称或者节点IP重复！";
		    		return Action.SUCCESS;
		    	}
		        service.updateIObject(synchro);
		    }
		    SearchContext.reloadSynchroList();
		    return Action.SUCCESS;
	}
    /**
     * 删除节点
     * @return
     * @throws Exception
     */
	public String delDo() throws Exception {
		    service.deleteIObject(synchro);
		    SearchContext.reloadSynchroList();
		    return Action.SUCCESS;
	}
}
