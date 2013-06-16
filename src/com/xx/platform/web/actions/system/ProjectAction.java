package com.xx.platform.web.actions.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.nutch.ipc.RPC;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.dao.FunctionParameters;
import com.xx.platform.dao.IDaoManager;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.domain.model.system.ProjectUser;
import com.xx.platform.domain.model.system.Relation;
import com.xx.platform.domain.model.system.Sproject;
import com.xx.platform.util.dao.DCriteriaPageSupport;
import com.xx.platform.util.tools.ArraysObjectTool;
import com.xx.platform.web.actions.BaseAction;

public class ProjectAction extends BaseAction {
	private static Document document=null;
	private static Document defaultDocument=null;
	private int pageSize = 80;// 每页显示的结果数量
	private Sproject sproject;
	private List<Sproject> sprojectList = new ArrayList<Sproject>();
	private int page = -1;
	private List<SourceFile> files;
	private String fileName;// 文件夹名称
	private String docName;// 文档名称
	private String newName;// 新文件名称
	private File uploadFile;
	private List<ProjectUser> userList;//项目中已有的用户
	private List<ProjectUser> userListNotIn;//项目中没有的用户
	private String[] notInproject=null;//没有加入项目的用户ID
	private String[] Inproject=null;//入项目的用户ID
	private int perUsers=50;//每页显示的用户数量
	private String clickId;//被点击的对象的ID
	private String filePath;//文件路径
	private String imgPath;//图片资源文件夹位置
	private String sourceContent;//编辑内容
	private List<SourceFile> fileList;
	private ProjectFileManager fileManager=new ProjectFileManager();
	private String rootId;//导航跟节点ID
	private String projectId;//项目ID
	private String erroMessage="";
	private String isTest;
	public String createNewFile(){
		boolean success=fileManager.createNewFile(sproject.getCode(), fileName, docName,true);
		if (!success) {
			message = "文件创建失败！";
		}
		return null;
	}
/**
 * 发布项目
 * @return
 */
	public String publish(){
		String code=sproject.getCode();
		ProjectFileManager manager=new ProjectFileManager();
		List<Sproject> sprojectList=service.findAllByCriteria(DetachedCriteria.forClass(Sproject.class).add(Restrictions.eq("code",code)));
		try{
			if("true".equals(isTest)&&"default".equalsIgnoreCase(code)){
				//默认项目测试发布
				manager.publicTest("default");
			}else{
				if(!sprojectList.isEmpty()&&!"true".equals(isTest)){
					manager.publish(code);
				}else{
					manager.publicTest(code);
				}
			}
			sproject=sprojectList.get(0);
			if(!sprojectList.isEmpty()&&"true".equals(isTest)){
				sproject.setIstest(1l);
				service.saveOrUpdateIObject(sproject);
			}
			if(!sprojectList.isEmpty()&&!"true".equals(isTest)){
				sproject.setState(1l);
				service.saveOrUpdateIObject(sproject);
			}
		}catch(Exception e){
			erroMessage="发布失败！";
			sproject.setState(0l);
			service.saveOrUpdateIObject(sproject);
			e.printStackTrace();
		}
		SearchContext.initProject();
		return Action.SUCCESS;
	}
	/**
	 * 收回发布资源
	 * @return
	 */
	public String reback(){
		String code=sproject.getCode();
		ProjectFileManager manager=new ProjectFileManager();
		try{
			manager.reback(code);
			List<Sproject> sprojectList=service.findAllByCriteria(DetachedCriteria.forClass(Sproject.class).add(Restrictions.eq("code",code)));
			if(!sprojectList.isEmpty()){
				sproject=sprojectList.remove(0);
				sproject.setState(0l);
				service.saveOrUpdateIObject(sproject);
			}
		}catch(Exception e){
			erroMessage="收回失败！";
			sproject.setState(1l);
			service.saveOrUpdateIObject(sproject);
			e.printStackTrace();
		}
		SearchContext.initProject();
		return Action.SUCCESS;
	}
	@SuppressWarnings("unchecked")
	public String query() {
		if (page == -1) {
			page = 1;
		}
		sprojectList = service.findByIObjectCType(Sproject.class, page,
				pageSize);
		return Action.SUCCESS;
	}

	/**
	 * 添加一条数据
	 * 
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public String insert() throws IOException {
		if (sproject.getName() == null || sproject.getCode() == null) {
			message = "项目名称以及项目代码不能为空！";
		} else {

			int n = service.getCountByCriteria(DetachedCriteria.forClass(
					Sproject.class).add(
					Restrictions.eq("name", sproject.getName().trim())));
			if (n > 0) {
				message = "项目名称重复，添加失败！";
			} else {
				int count = service.getCountByCriteria(DetachedCriteria
						.forClass(Sproject.class).add(
								Restrictions.eq("code", sproject.getCode()
										.trim())));
				if (count > 0) {
					message = "项目代码重复，添加失败！";
				} else {
					sproject.setCode(sproject.getCode().trim());
					sproject.setName(sproject.getName().trim());
					service.saveIObject(sproject);
					fileManager.createFile(sproject.getCode(),true);
					sproject = null;
				}
			}
		}
		SearchContext.initProject();
		query();
		return Action.SUCCESS;
	}

	/**
	 * 根据主键删除一条记录
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String deleteByKey() throws Exception {
		Sproject temp = (Sproject) service.getIObjectByPK(Sproject.class,
				sproject.getId());
		if (temp != null) {
			if("default".equals(temp.getCode())){
				
			}else{
				service.execByHQL("delete from Sproject where id='" + sproject.getId()
						+ "'");
					String code=temp.getCode();
					fileManager.deleteFile(code,true);
					fileManager.deletePulishResource(code);
					fileManager.deleteTestResource(code);
					SearchContext.initProject();
			}
		}
		
		query();
		return Action.SUCCESS;
	}

	/**
	 * 获取一条项目 数据的详细信息
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String viewProject() {
		//基本信息
		sproject = (Sproject) service.getIObjectByPK(Sproject.class, sproject
				.getId());
		// 获取资源列表
		files = ProjectFileManager.getChildFiles(ProjectFileManager.rootPath
				.concat(ProjectFileManager.separator)
				.concat(sproject.getCode()));
		//获取用户信息
		IDaoManager dao=(IDaoManager)service;
		if(page<1){
			page=1;
		}
		//查找项目用户总数
		int inCount=service.getCountByCriteria(DetachedCriteria.forClass(Relation.class).add(Restrictions.eq("projectid",sproject.getId())));
		//查找用户总数
		int userCount=service.getCountByCriteria(DetachedCriteria.forClass(ProjectUser.class));
		
		String hql="select id,username,userpassword,mail,showname from ProjectUser where id in(select userid from Relation where projectid='"+sproject.getId()+"')";
		FunctionParameters[] foelds={new FunctionParameters("setId",String.class),new FunctionParameters("setUsername",String.class),new FunctionParameters("setUserpassword",String.class),new FunctionParameters("setMail",String.class),new FunctionParameters("setShowname",String.class)};
		userList=(List<ProjectUser>)dao.hqlListBox(ProjectUser.class, hql, foelds, (page-1)*10, perUsers);
		DCriteriaPageSupport tempUserList=new DCriteriaPageSupport(userList,inCount,perUsers,(page-1)*10);
		userList=tempUserList;
		
		String hql1="select id,username,userpassword,mail,showname from ProjectUser where id not in(select userid from Relation where projectid='"+sproject.getId()+"')";
		FunctionParameters[] foelds1={new FunctionParameters("setId",String.class),new FunctionParameters("setUsername",String.class),new FunctionParameters("setUserpassword",String.class),new FunctionParameters("setMail",String.class),new FunctionParameters("setShowname",String.class)};
		userListNotIn=(List<ProjectUser>)dao.hqlListBox(ProjectUser.class, hql1, foelds1, (page-1)*10, perUsers);
		DCriteriaPageSupport tempuserListNotIn=new DCriteriaPageSupport(userListNotIn,userCount-inCount,perUsers,(page-1)*10);
		userListNotIn=tempuserListNotIn;
		
		return Action.SUCCESS;
	}

	/**
	 * 更新基本配置
	 * 
	 * @return
	 */
	public String updateProject() {
		service.updateIObject(sproject);
		SearchContext.initProject();
		viewProject();
		message = "修改成功！";
		return Action.SUCCESS;
	}

	/**
	 * 获取一个资源文件下的所有文件
	 * 
	 * @return
	 */
	public String getSourceFile() {
		files = ProjectFileManager.getChildFiles(ProjectFileManager.rootPath
				.concat(sproject.getCode())
				.concat(ProjectFileManager.separator).concat(fileName));
		return Action.SUCCESS;
	}

	/**
	 * 删除一个文件
	 * 
	 * @return
	 */
	public String deleteSourceFile() {
		fileManager.deleteDoc(sproject.getCode(), fileName, docName,true);
		files = ProjectFileManager.getChildFiles(filePath);
		return null;
	}

	/**
	 * 下载一个文件
	 * 
	 * @return
	 * @throws IOException
	 */
	public String downloadSourceFile() throws IOException {
		String filePath = ProjectFileManager.rootPath
				.concat(sproject.getCode())
				.concat(ProjectFileManager.separator).concat(fileName);
		String docPath = filePath.concat(ProjectFileManager.separator).concat(
				docName);
		download(docPath, response);
		return null;
	}

	/**
	 * 判断一个文件是否存在
	 * 
	 * @return
	 * @throws IOException
	 */
	public String findFile() throws IOException {
		String filePath = ProjectFileManager.rootPath
				.concat(sproject.getCode())
				.concat(ProjectFileManager.separator).concat(fileName);
		String newPath = filePath.concat(ProjectFileManager.separator).concat(
				newName);
		File file = new File(newPath);
		PrintWriter out = response.getWriter();
		if (file.exists()) {
			out.write("1");
		} else {
			out.write("0");
		}
		if (out != null) {
			out.flush();
			out.close();
		}
		return null;
	}

	/**
	 * 重命名一个文件
	 * 
	 * @return
	 * @throws IOException
	 */
	public String renameFile() throws IOException {
		boolean success=fileManager.rename(sproject.getCode(), fileName, docName, newName,true);
		if (!success) {
			// 此位置已经包含同名文件！重命名失败！
			message = "\u6b64\u4f4d\u7f6e\u5df2\u7ecf\u5305\u542b\u540c\u540d\u6587\u4ef6\uff01\u91cd\u547d\u540d\u5931\u8d25\uff01";
		}
		//getSourceFile();
		return null;
	}
/**
 * 上传文件
 * @return
 */
	public String uploadSource(){
		String projectCode=sproject.getCode();
		fileManager.upload(projectCode, fileName,docName,uploadFile,true);
		getSourceFile();
		return Action.SUCCESS;
	}
	/**
	 * 把用户添加进项目
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String addUser(){
		IDaoManager dao=(IDaoManager)service;
		if(notInproject!=null){
			List<Relation> relationList=new ArrayList<Relation>(); 
			for(String userId:notInproject){
				Relation relation =new Relation();
				relation.setProjectid(sproject.getId());
				relation.setUserid(userId);
				relationList.add(relation);
			}
			if(relationList!=null&&!relationList.isEmpty()){
				dao.inserBat(relationList);
			}
		}
		if(page<1){
			page=1;
		}
		//查找项目用户总数
		int inCount=service.getCountByCriteria(DetachedCriteria.forClass(Relation.class).add(Restrictions.eq("projectid",sproject.getId())));
		//查找用户总数
		int userCount=service.getCountByCriteria(DetachedCriteria.forClass(ProjectUser.class));
		
		String hql="select id,username,mail,showname from ProjectUser where id in(select userid from Relation where projectid='"+sproject.getId()+"')";
		FunctionParameters[] foelds={new FunctionParameters("setId",String.class),new FunctionParameters("setUsername",String.class),new FunctionParameters("setMail",String.class),new FunctionParameters("setShowname",String.class)};
		userList=dao.hqlListBox(ProjectUser.class, hql, foelds, (page-1)*10, perUsers);
		DCriteriaPageSupport tempUserList=new DCriteriaPageSupport(userList,inCount,perUsers,(page-1)*10);
		userList=tempUserList;
		
		String hql1="select id,username,mail,showname from ProjectUser where id not in(select userid from Relation where projectid='"+sproject.getId()+"')";
		FunctionParameters[] foelds1={new FunctionParameters("setId",String.class),new FunctionParameters("setUsername",String.class),new FunctionParameters("setMail",String.class),new FunctionParameters("setShowname",String.class)};
		userListNotIn=dao.hqlListBox(ProjectUser.class, hql1, foelds1, (page-1)*10, perUsers);
		DCriteriaPageSupport tempuserListNotIn=new DCriteriaPageSupport(userListNotIn,userCount-inCount,perUsers,(page-1)*10);
		userListNotIn=tempuserListNotIn;
		
		return Action.SUCCESS;
	}
	/**
	 * 从一个项目中移除用户
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String removeUser() throws Exception{
		IDaoManager dao=(IDaoManager)service;
		if(Inproject!=null){
			String hql="delete from Relation where projectid='"+sproject.getId()+"' and userid in('";
			StringBuilder sbSQL=new StringBuilder();
			for(String userId:Inproject){
				if(sbSQL.length()==0){
					sbSQL.append(userId);
				}else{
					sbSQL.append("','").append(userId);
				}
			}
			sbSQL.append("')");
			hql=hql.concat(sbSQL.toString());
			service.execByHQL(hql);
		}
		if(page<1){
			page=1;
		}
		//查找项目用户总数
		int inCount=service.getCountByCriteria(DetachedCriteria.forClass(Relation.class).add(Restrictions.eq("projectid",sproject.getId())));
		//查找用户总数
		int userCount=service.getCountByCriteria(DetachedCriteria.forClass(ProjectUser.class));
		
		String hql="select id,username,mail,showname from ProjectUser where id in(select userid from Relation where projectid='"+sproject.getId()+"')";
		FunctionParameters[] foelds={new FunctionParameters("setId",String.class),new FunctionParameters("setUsername",String.class),new FunctionParameters("setMail",String.class),new FunctionParameters("setShowname",String.class)};
		userList=dao.hqlListBox(ProjectUser.class, hql, foelds, (page-1)*10, perUsers);
		DCriteriaPageSupport tempUserList=new DCriteriaPageSupport(userList,inCount,perUsers,(page-1)*10);
		userList=tempUserList;
		
		String hql1="select id,username,mail,showname from ProjectUser where id not in(select userid from Relation where projectid='"+sproject.getId()+"')";
		FunctionParameters[] foelds1={new FunctionParameters("setId",String.class),new FunctionParameters("setUsername",String.class),new FunctionParameters("setMail",String.class),new FunctionParameters("setShowname",String.class)};
		userListNotIn=dao.hqlListBox(ProjectUser.class, hql1, foelds1, (page-1)*10, perUsers);
		DCriteriaPageSupport tempuserListNotIn=new DCriteriaPageSupport(userListNotIn,userCount-inCount,perUsers,(page-1)*10);
		userListNotIn=tempuserListNotIn;
		return Action.SUCCESS;
	}
	/**
	 * 获项目中的用户（分页）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getInusers(){
		IDaoManager dao=(IDaoManager)service;
		if(page<1){
			page=1;
		}
		//查找项目用户总数
		int inCount=service.getCountByCriteria(DetachedCriteria.forClass(Relation.class).add(Restrictions.eq("projectid",sproject.getId())));
		String hql="select id,username,mail,showname from ProjectUser where id in(select userid from Relation where projectid='"+sproject.getId()+"')";
		FunctionParameters[] foelds={new FunctionParameters("setId",String.class),new FunctionParameters("setUsername",String.class),new FunctionParameters("setMail",String.class),new FunctionParameters("setShowname",String.class)};
		userList=dao.hqlListBox(ProjectUser.class, hql, foelds, (page-1)*10, perUsers);
		DCriteriaPageSupport tempUserList=new DCriteriaPageSupport(userList,inCount,perUsers,(page-1)*10);
		userList=tempUserList;
		return Action.SUCCESS;
	}
	/**
	 * 获得不在项目中的用户(分页)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getNotInusers(){
		IDaoManager dao=(IDaoManager)service;
		if(page<1){
			page=1;
		}
		//查找项目用户总数
		int inCount=service.getCountByCriteria(DetachedCriteria.forClass(Relation.class).add(Restrictions.eq("projectid",sproject.getId())));
		//查找用户总数
		int userCount=service.getCountByCriteria(DetachedCriteria.forClass(ProjectUser.class));
		String hql1="select id,username,mail,showname from ProjectUser where id not in(select userid from Relation where projectid='"+sproject.getId()+"')";
		FunctionParameters[] foelds1={new FunctionParameters("setId",String.class),new FunctionParameters("setUsername",String.class),new FunctionParameters("setMail",String.class),new FunctionParameters("setShowname",String.class)};
		userListNotIn=dao.hqlListBox(ProjectUser.class, hql1, foelds1, (page-1)*10, perUsers);
		DCriteriaPageSupport tempuserListNotIn=new DCriteriaPageSupport(userListNotIn,userCount-inCount,perUsers,(page-1)*10);
		userListNotIn=tempuserListNotIn;
		return Action.SUCCESS;
	}
	/**
	 * 获得文件夹下的所有文件，返回xml文件
	 * @return
	 * @throws IOException 
	 */
	public String getAllFilesUnderFileXML() throws IOException{
		String code=sproject.getCode();
		File rootFile=new File(ProjectFileManager.rootPath.concat(code));
		Document document=DocumentHelper.createDocument();
		Element root = document.addElement("tree");
		root.setAttributeValue("id","0");
		getStruct(rootFile,root,code+"-");
		String xml=document.asXML();
		response.setCharacterEncoding("utf-8");
		PrintWriter out=response.getWriter();
		out.write(xml);
		out.flush();
		out.close();
		return null;
	}
	private void getStruct(File rootFile,Element root,String path){
		if(rootFile!=null&&rootFile.exists()){
			File[] files=rootFile.listFiles();
			if(files!=null&&files.length>0){
				for(File file:files){
					SourceFile srcFile=new SourceFile(file);
					String id=path+srcFile.getName();
					Element element=root.addElement("item");
					String fileName=srcFile.getName();
					String text="";
					if("data".equalsIgnoreCase(fileName)||"script".equalsIgnoreCase(fileName)||"page".equalsIgnoreCase(fileName)){
						String showText=fileName;
						if("data".equalsIgnoreCase(fileName)){
							showText="资源文件夹";
						}else if("script".equalsIgnoreCase(fileName)){
							showText="脚本文件夹";
						}else if("page".equalsIgnoreCase(fileName)){
							showText="页面文件夹";
						}
						//showText="<a onclick=viewFolder('"+sproject.getCode()+"','"+file.getName()+"')><font color=black>".concat(showText).concat("</font></a>");
						element.setAttributeValue("im0","folderClosed.gif");
						text=showText.concat("<a onclick=projectOpenFileUploadPage('"+sproject.getCode()+"','"+file.getName()+"','guidConfig')>[上传]</a>");
					}else{
						//showEditeSorceDiv
						String tempFileName="<a onclick=showEditeSorceDiv('"+id+"');><u>"+fileName+"</u></a>";
						text=tempFileName;
						//text=text.concat("");
					}
					element.addAttribute("text",text);
					element.addAttribute("id",id);
					if(file.isDirectory()){
						element.addAttribute("open","0");
					}
					getStruct(file,element,id+"-");
				}
			}
		}
	}
	/**
	 * 删除一个文件
	 * @return
	 */
	public String deleteSourceByTree(){
//		File file=new File(filePath);
//		if(file.exists()){
//			file.delete();
//		}
		fileManager.deleteFileByPath(filePath, true);
		return null;
	}
	public String getSourceFileContent() throws IOException{
		String content=ProjectFileManager.readFileHTML(sproject.getCode(),fileName,docName);
		if(content!=null){
//			content=content.replaceAll("<","&#60;");
//			content=content.replaceAll(">","&#62;");
//			content=content.replaceAll("&","&#38;");
			//content=content.replace(" ","&#160;");
		}else{
			content="";
		}
		response.setCharacterEncoding("utf-8");
		PrintWriter out=response.getWriter();
		//String result=URLEncoder.encode(content);
		String result=content;
		out.write(result);
		out.flush();
		out.close();
		return null;
	}
	/**
	 * 编辑脚本
	 * @return
	 * @throws IOException 
	 */
	public String editSource() throws IOException{
		fileManager.editSource(sproject.getCode(), fileName, docName, sourceContent, true);
		return getSourceFileContent();
	}
	/**
	 * 生成树
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	public String treeMenu() throws DocumentException, IOException{
		if(document==null){
			String realPath=request.getSession().getServletContext().getRealPath("/");
			realPath=realPath.concat("projectmodule/menu.xml");
			File moduleFile=new File(realPath);
			SAXReader reader=new SAXReader();
			Document document=reader.read(moduleFile);
			this.document=document;
		}
		if(defaultDocument==null){
			String realPath=request.getSession().getServletContext().getRealPath("/");
			realPath=realPath.concat("projectmodule/default.xml");
			File moduleFile=new File(realPath);
			SAXReader reader=new SAXReader();
			Document document=reader.read(moduleFile);
			defaultDocument=document;
		}
		String code=sproject.getCode();
		Element root=null;
		if("default".equalsIgnoreCase(code)){
			root=defaultDocument.getRootElement();
		}else{
			root=document.getRootElement();
		}
		List<Element> elements=root.elements();
		Element src=null;
		for(Element e:elements){
			List<Element> menuElement=e.elements();
			for(Element menu:menuElement){
				String id=menu.attribute("id").getValue();
				if("menu_guidConfig".equals(id)){
					src=menu;
				}
			}
		}
		File rootFile=new File(ProjectFileManager.rootPath.concat(code));
		getStruct(rootFile,src,code+"-");
		response.setCharacterEncoding("utf-8");
		String xml="";
		if("default".equalsIgnoreCase(code)){
			xml=defaultDocument.asXML();
		}else{
			xml=document.asXML();
		}
		
		src.clearContent();
		PrintWriter out=response.getWriter();
		out.write(xml);
		out.flush();
		out.close();
		return null;
	}
	/**
	 * 获取一个文件夹下的所有文件
	 * @return
	 * @throws IOException 
	 */
	public String getFilesDhtml() throws IOException{
//		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
//		String filePath=ProjectFileManager.rootPath.concat(sproject.getCode()).concat(ProjectFileManager.separator).concat(fileName);
//		File root=new File(filePath);
//		Document document=DocumentHelper.createDocument();
//		document.setXMLEncoding("utf-8");
//		Element rootElement=document.addElement("data");
//		if(root.isDirectory()){
//			File[] files=root.listFiles();
//			if(files!=null&&files.length>0){
//				for(File file:files){
//					Element item=rootElement.addElement("item");
//					SourceFile sfile=new SourceFile(file);
//					item.addAttribute("name",file.getName().concat(".").concat(sfile.getSuffix()));
//					String type="file";
//					if(file.isDirectory()){
//						type="file";
//					}
//					item.addAttribute("type",type);
//					Element filesize=item.addElement("filesize");
//					if(file.isFile()){
//						filesize.addText(String.valueOf(file.length()));
//						long lModifyDate=file.lastModified();
//						Date dModifyDate=new Date(lModifyDate);
//						String modifyDate=sdf.format(dModifyDate);
//						Element modifdate=item.addElement("modifdate");
//						modifdate.setText(modifyDate);
//					}
//				}
//			}
//		}
//		String xml=document.asXML();
//		response.setCharacterEncoding("utf-8");
//		PrintWriter out=response.getWriter();
//		out.write(xml);
//		out.flush();
//		out.close();
//		return null;
		String filePath=ProjectFileManager.rootPath.concat(sproject.getCode()).concat(ProjectFileManager.separator).concat(fileName);
		File root=new File(filePath);
		
		if(root.isDirectory()){
			File[] files=root.listFiles();
			if(files!=null&&files.length>0){
				fileList=new ArrayList<SourceFile>();
				for(File file:files){
					SourceFile sfile=new SourceFile(file,imgPath);
					fileList.add(sfile);
				}
			}
		}
		return Action.SUCCESS;
	}
	/**
	 * 删除文件
	 * @return
	 * @throws IOException 
	 */
	public String deleteFileByPath() throws IOException{
		fileManager.deleteFileByPath(filePath,true);
		getFilesDhtml();
		return Action.SUCCESS;
	}
	
	public Sproject getSproject() {
		return sproject;
	}

	public void setSproject(Sproject sproject) {
		this.sproject = sproject;
	}

	public List<Sproject> getSprojectList() {
		return sprojectList;
	}

	public void setSprojectList(List<Sproject> sprojectList) {
		this.sprojectList = sprojectList;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<SourceFile> getFiles() {
		return files;
	}

	public void setFiles(List<SourceFile> files) {
		this.files = files;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public void download(String path, HttpServletResponse response) throws UnsupportedEncodingException {
		File file = new File(path);
		String filename = file.getName();
		filename=URLEncoder.encode(filename,"utf-8");
		response.setContentType("application/octet-stream");
		response.addHeader("Content-Disposition", "attachment;filename="
				+ filename);
		response.addHeader("Content-Length", "" + file.length());
		PrintWriter out=null;
		FileInputStream fis=null;
		try {
			out = response.getWriter();
			fis = new FileInputStream(file);
			int i;
			while ((i = fis.read()) != -1) {
				out.write(i);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out!=null){
				out.close();
			}
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public List<ProjectUser> getUserList() {
		return userList;
	}

	public void setUserList(List<ProjectUser> userList) {
		this.userList = userList;
	}

	public List<ProjectUser> getUserListNotIn() {
		return userListNotIn;
	}

	public void setUserListNotIn(List<ProjectUser> userListNotIn) {
		this.userListNotIn = userListNotIn;
	}
	public String[] getNotInproject() {
		return notInproject;
	}

	public void setNotInproject(String[] notInproject) {
		this.notInproject = notInproject;
	}

	public String[] getInproject() {
		return Inproject;
	}

	public void setInproject(String[] inproject) {
		Inproject = inproject;
	}

	public int getPerUsers() {
		return perUsers;
	}

	public void setPerUsers(int perUsers) {
		this.perUsers = perUsers;
	}

	public String getClickId() {
		return clickId;
	}

	public void setClickId(String clickId) {
		this.clickId = clickId;
	}
	public String getSourceContent() {
		return sourceContent;
	}

	public void setSourceContent(String sourceContent) {
		this.sourceContent = sourceContent;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
	
	public String getErroMessage() {
		return erroMessage;
	}

	public void setErroMessage(String erroMessage) {
		this.erroMessage = erroMessage;
	}

	public List<SourceFile> getFileList() {
		return fileList;
	}

	public void setFileList(List<SourceFile> fileList) {
		this.fileList = fileList;
	}
	public String getIsTest() {
		return isTest;
	}
	public void setIsTest(String isTest) {
		this.isTest = isTest;
	}
}
