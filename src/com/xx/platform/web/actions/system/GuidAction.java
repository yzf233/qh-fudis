package com.xx.platform.web.actions.system;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.SearchContext;
import com.xx.platform.domain.model.search.Guide;
import com.xx.platform.domain.model.system.Sproject;
import com.xx.platform.web.actions.BaseAction;

public class GuidAction extends BaseAction{
	private Guide guide;
	private Sproject sproject;
	private String guideId;
	private String pGuideId;
	/**
	 * ������еĵ���
	 * @return
	 * @throws IOException 
	 */
	public String getAllGuid(){
		List<Guide> guidList=service.findAllByCriteria(DetachedCriteria.forClass(Guide.class).add(Restrictions.eq("pid",guide.getPid())));
		Document document=DocumentHelper.createDocument();
		Element root=document.addElement("tree");
		root.addAttribute("id","0");
		Element item=root.addElement("item");
		item.addAttribute("text","��������");
		item.addAttribute("id","1");
		item.addAttribute("im0","folderOpen.gif");
		item.addAttribute("open","1");
		guidToXml(item,guidList);
		String xml=document.asXML();
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=null;
		try {
			out = response.getWriter();
			out.write(xml);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out!=null){
				out.flush();
				out.close();
			}
		}
		return Action.SUCCESS;
	}
	public String getAllGuidByProjectCode(){
		String code=sproject.getCode();
		List<Guide> guidList=SearchContext.allGuideMap.get(code);
		Document document=DocumentHelper.createDocument();
		Element root=document.addElement("tree");
		root.addAttribute("id","0");
		Element item=root.addElement("item");
		item.addAttribute("text","��ѯ����");
		item.addAttribute("id","1");
		item.addAttribute("im0","folderOpen.gif");
		item.addAttribute("open","1");
		if(guidList!=null&&!guidList.isEmpty()){
			guidToXml(item,guidList);
		}
		String xml=document.asXML();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml;charset=utf-8");
		PrintWriter out=null;
		try {
			out = response.getWriter();
			out.write(xml);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out!=null){
				out.flush();
				out.close();
			}
		}
		return Action.SUCCESS;
	}
	/**
	 * ��һ��������ӽ�xml
	 * @param root
	 * @param guid
	 */
	private Element guidToXml(Element root,Guide guid){
		Element item=root.addElement("item");
		item.addAttribute("text",guid.getName());
		item.addAttribute("id",guid.getId());
		item.addAttribute("open","1");
		return item;
	}
	/**
	 * �����е�������װ���xml�ļ�
	 * @param root
	 * @param guidList
	 */
	private void guidToXml(Element root,List<Guide> guidList){
		String id=root.attributeValue("id");
		for(Guide guid:guidList){
			if(guid.getParentid().equals(id)){
				Element item=guidToXml(root,guid);
				guidToXml(item,guidList);
			}
		}
	}
	public String editGuide(){
		if("1".equals(guide.getId())){
			return Action.INPUT;
		}else{
			guide=(Guide)service.getIObjectByPK(Guide.class,guide.getId());
		}
		return Action.SUCCESS;
	}
	/**
	 * ���һ��Ԫ��
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String insertGuide(){
//		String uri=guide.getUripath();
//		if(uri==null||uri.equals("")){
//			uri="1";
//		}else{
//			uri+="-"+guide.getParentid();
//		}
//		guide.setUripath(uri);
		service.saveIObject(guide);
		SearchContext.reloadGuide();//��ʼ������
		return null;
	}
	/**
	 * �������ƴ��뼶��
	 * @return
	 * @throws IOException
	 */
	public String findBynameOrCode() throws IOException{
		int n=service.getCountByCriteria(DetachedCriteria.forClass(Guide.class).add(Restrictions.and(Restrictions.eq("parentid",guide.getParentid()), Restrictions.and(Restrictions.eq("pid",sproject.getId()), Restrictions.or(Restrictions.eq("name", guide.getName()), Restrictions.eq("code",guide.getCode()))))));
		PrintWriter out=response.getWriter();
		out.write(""+n);
		out.flush();
		out.close();
		return null;
	}
	/**
	 * �������ƴ��뼶��ID
	 * @return
	 * @throws IOException
	 */
	public String findBynameOrCodeAndID() throws IOException{
		int n=service.getCountByCriteria(DetachedCriteria.forClass(Guide.class).add(Restrictions.and(Restrictions.not(Restrictions.eq("id",guide.getId())),Restrictions.and(Restrictions.eq("parentid",guide.getParentid()), Restrictions.and(Restrictions.eq("pid",sproject.getId()), Restrictions.or(Restrictions.eq("name", guide.getName()), Restrictions.eq("code",guide.getCode())))))));
		PrintWriter out=response.getWriter();
		out.write(""+n);
		out.flush();
		out.close();
		return null;
	}
	/**
	 * ɾ��һ��guide
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public String deleteGuide() throws Exception{
		List<Guide> guidList=service.findAllByCriteria(DetachedCriteria.forClass(Guide.class).add(Restrictions.eq("pid",guide.getPid())));
		List<String> temp=new ArrayList<String>();
		temp.add(guide.getId());
		getAllSonId(temp,guide.getId(),guidList);
		StringBuilder sbCondition=new StringBuilder();
		for(String s:temp){
			if(sbCondition.length()==0){
				sbCondition.append("'").append(s).append("'");
			}else{
				sbCondition.append(",'").append(s).append("'");
			}
		}
		String hql="delete from Guide where id in(".concat(sbCondition.toString()).concat(")");
		service.execByHQL(hql);
		SearchContext.reloadGuide();//��ʼ������
		return Action.SUCCESS;
	}
	private void getAllSonId(List<String> ids,String id,List<Guide> guidList){
		for(Guide guide:guidList){
			String tempParentid=guide.getParentid();
			if(tempParentid.equals(id)){
				ids.add(guide.getId());
				getAllSonId(ids,guide.getId(),guidList);
			}
		}
	}
	/**
	 * ���һ���ڵ�������ӽڵ��xml�ļ����
	 * @return
	 * @throws IOException
	 */
	public String getAllSunXml() throws IOException{
		List<Guide> guidList=service.findAllByCriteria(DetachedCriteria.forClass(Guide.class).add(Restrictions.eq("pid",sproject.getId())));
		for(Guide temp:guidList){
			if(temp.getId().equals(guide.getId())){
				guide=temp;
				break;
			}
		}
		List<Guide> result=new ArrayList<Guide>();
		getAllSon(result,guide,guidList);
		Document document=DocumentHelper.createDocument();
		Element root=document.addElement("tree");
		Element item=root.addElement("item");
		StringBuilder sbRoot=new StringBuilder();
		sbRoot.append(guide.getName());
		sbRoot.append("������");
		item.addAttribute("text",sbRoot.toString());
		item.addAttribute("id","1");
		item.addAttribute("im0","folderOpen.gif");
		item.addAttribute("open","1");
		guidToXml(item,result);
		response.setCharacterEncoding("utf-8");
		PrintWriter out=response.getWriter();
		out.write(document.asXML());
		out.flush();
		out.close();
		return Action.SUCCESS;
	}
	/**
	 * ���һ�������������ӽڵ�
	 * @return
	 */
	private void getAllSon(List<Guide> result,Guide root,List<Guide> data){
		if(data!=null&&!data.isEmpty()){
			result.add(root);
			String rootId=root.getId();
			for(Guide guide:data){
				if(guide.getParentid().equals(rootId)){
					getAllSon(result,guide,data);
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	public String updateGuide(){
		service.saveOrUpdateIObject(guide);
		SearchContext.reloadGuide();//��ʼ������
		return Action.SUCCESS;
	}
	/**
	 * �޸ĵ����ڵ����ʾλ��
	 * @return
	 * @throws Exception
	 */
	public String changePosition() throws Exception{
		if((guideId==null||pGuideId==null)||(guideId.trim().length()<32&&pGuideId.trim().length()<32)){
			return null;
		}
		/*
		 * ���ͬ�������Ƿ������ƻ��ߴ����ظ������
		 */
		int n=0;
		String projectCode=sproject.getCode();
		String key=null;
		if(SearchContext.projectList!=null){
			for(Sproject project:SearchContext.projectList){
				if(project.getId().equals(projectCode)){
					key=project.getCode();
					break;
				}
			}
		}
		if(key!=null&&SearchContext.allGuideMap!=null){
			List<Guide> guideList=SearchContext.allGuideMap.get(key);
			if(guideList!=null){
				Guide sonGuide=null;
				for(Guide guide:guideList){
					if(guide.getId().equals(guideId)){
						sonGuide=guide;
						break;
					}
				}
				List<Guide> friends=new ArrayList<Guide>();
				for(Guide guide:guideList){
					if(guide.getParentid().equals(pGuideId)){
						friends.add(guide);
					}
				}
				if(!friends.isEmpty()&&sonGuide!=null){
					for(Guide guide:friends){
						if((guide.getCode().equals(sonGuide.getCode())||guide.getName().equals(sonGuide.getName()))&&!guide.getId().equals(guideId)){
							n=1;
							break;
						}
					}
				}
			}
		}
		if(n>0){
			PrintWriter out=response.getWriter();
			out.write("1");//ʧ��
			out.flush();
			out.close();
		}else{
			StringBuilder sbHql=new StringBuilder();
			sbHql.append("update Guide set parentid='").append(pGuideId).append("' where id='").append(guideId).append("'");
			service.execByHQL(sbHql.toString());
			SearchContext.reloadGuide();//��ʼ������
			PrintWriter out=response.getWriter();
			out.write("0");//�ɹ�
			out.flush();
			out.close();
		}
		SearchContext.reloadGuide();//��ʼ������
		return null;
	}
	public Guide getGuide() {
		return guide;
	}
	public void setGuide(Guide guide) {
		this.guide = guide;
	}
	public Sproject getSproject() {
		return sproject;
	}
	public void setSproject(Sproject sproject) {
		this.sproject = sproject;
	}
	public String getGuideId() {
		return guideId;
	}
	public void setGuideId(String guideId) {
		this.guideId = guideId;
	}
	public String getPGuideId() {
		return pGuideId;
	}
	public void setPGuideId(String guideId) {
		pGuideId = guideId;
	}
}
