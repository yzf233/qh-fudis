package com.xx.platform.web.actions.system;

import java.util.List;

import com.opensymphony.xwork2.Action;
import com.xx.platform.domain.model.user.AllowList;
import com.xx.platform.domain.model.user.ForbidList;
import com.xx.platform.domain.model.user.IPCheckInteface;
import com.xx.platform.util.tools.ipcheck.CheckIPUtil;
import com.xx.platform.web.actions.BaseAction;

public class IPCheckAction  extends BaseAction{
	private List<IPCheckInteface> ipList;
	private String dotype;
	private IPCheckInteface ipBean;
	private String[] deleteIps;
	public String ipCheckIndex(){
		if(dotype.equals("BLACK")){
			ipList=service.findAllByIObjectCType(ForbidList.class);
		}else{
			ipList=service.findAllByIObjectCType(AllowList.class);
		}
		ipBean=null;
		return Action.SUCCESS;
	}
	/**
	 * 添加一条数据
	 * @return
	 */
	public String ipInsert(){
		if(dotype.equals("BLACK")){
			service.saveIObject(getForbidList(ipBean));
		}else{
			service.saveIObject(getAllowList(ipBean));
		}
		CheckIPUtil.init();
		ipCheckIndex();
		return Action.SUCCESS;
	}
	/**
	 * 删除一条数据
	 * @return
	 * @throws Exception
	 */
	public String ipDeleteByP() throws Exception{
		String hSQL=null;
		if(dotype.equals("BLACK")){
			hSQL="delete from ForbidList where id='"+ipBean.getId()+"'";
		}else{
			hSQL="delete from AllowList where id='"+ipBean.getId()+"'";
		}
		service.execByHQL(hSQL);
		CheckIPUtil.init();
		ipCheckIndex();
		return Action.SUCCESS;
	}
	/**
	 * 删除多个IP地址
	 * @return
	 * @throws Exception
	 */
	public String ipDeleteByCondition() throws Exception{
		if(deleteIps!=null){
			StringBuilder sbConditions=new StringBuilder();
			for(String ip:deleteIps){
				if(sbConditions.length()==0){
					sbConditions.append("'").append(ip).append("'");
				}else{
					sbConditions.append(",'").append(ip).append("'");
				}
			}
			if(sbConditions.length()!=0){
				String hSQL=null;
				if(dotype.equals("BLACK")){
					hSQL="delete from ForbidList where id in(".concat(sbConditions.toString()).concat(")");
				}else{
					hSQL="delete from AllowList where id in(".concat(sbConditions.toString()).concat(")");
				}
				service.execByHQL(hSQL);
			}else{
				return Action.SUCCESS;
			}
		}
		CheckIPUtil.init();
		ipCheckIndex();
		return Action.SUCCESS;
	}
	/**
	 * 查看一条数据
	 * @param ipBean
	 * @return
	 */
	public String ipView(){
		if(dotype.equals("BLACK")){
			ipBean=(IPCheckInteface) service.getIObjectByPK(ForbidList.class, ipBean.getId());
		}else{
			ipBean=(IPCheckInteface) service.getIObjectByPK(AllowList.class, ipBean.getId());
		}
		return Action.SUCCESS;
	}
	/**
	 * 更新
	 * @return
	 */
	public String ipUpdate(){
		if(dotype.equals("BLACK")){
			service.updateIObject(getForbidList(ipBean));
		}else{
			service.updateIObject(getAllowList(ipBean));
		}
		CheckIPUtil.init();
		ipCheckIndex();
		return Action.SUCCESS;
	}
	private ForbidList getForbidList(IPCheckInteface ipBean){
		ForbidList bean=new ForbidList();
		bean.setId(ipBean.getId());
		bean.setStartip(ipBean.getStartip());
		bean.setEndip(ipBean.getEndip());
		bean.setName(ipBean.getName());
		return bean;
	}
	private AllowList getAllowList(IPCheckInteface ipBean){
		AllowList bean=new AllowList();
		bean.setId(ipBean.getId());
		bean.setStartip(ipBean.getStartip());
		bean.setEndip(ipBean.getEndip());
		bean.setName(ipBean.getName());
		return bean;
	}
	public List<IPCheckInteface> getIpList() {
		return ipList;
	}
	public void setIpList(List<IPCheckInteface> ipList) {
		this.ipList = ipList;
	}
	public IPCheckInteface getIpBean() {
		return ipBean;
	}
	public void setIpBean(IPCheckInteface ipBean) {
		this.ipBean = ipBean;
	}
	public String[] getDeleteIps() {
		return deleteIps;
	}
	public void setDeleteIps(String[] deleteIps) {
		this.deleteIps = deleteIps;
	}
	public String getDotype() {
		return dotype;
	}
	public void setDotype(String dotype) {
		this.dotype = dotype;
	}
}
