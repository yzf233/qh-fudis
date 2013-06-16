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
	private int pageSize = 80;// ÿҳ��ʾ�Ľ������
	private Sproject sproject;
	private List<Sproject> sprojectList = new ArrayList<Sproject>();
	private int page = -1;
	private List<SourceFile> files;
	private String fileName;// �ļ�������
	private String docName;// �ĵ�����
	private String newName;// ���ļ�����
	private File uploadFile;
	private List<ProjectUser> userList;//��Ŀ�����е��û�
	private List<ProjectUser> userListNotIn;//��Ŀ��û�е��û�
	private String[] notInproject=null;//û�м�����Ŀ���û�ID
	private String[] Inproject=null;//����Ŀ���û�ID
	private int perUsers=50;//ÿҳ��ʾ���û�����
	private String clickId;//������Ķ����ID
	private String filePath;//�ļ�·��
	private String imgPath;//ͼƬ��Դ�ļ���λ��
	private String sourceContent;//�༭����
	private List<SourceFile> fileList;
	private ProjectFileManager fileManager=new ProjectFileManager();
	private String rootId;//�������ڵ�ID
	private String projectId;//��ĿID
	private String erroMessage="";
	private String isTest;
	public String createNewFile(){
		boolean success=fileManager.createNewFile(sproject.getCode(), fileName, docName,true);
		if (!success) {
			message = "�ļ�����ʧ�ܣ�";
		}
		return null;
	}
/**
 * ������Ŀ
 * @return
 */
	public String publish(){
		String code=sproject.getCode();
		ProjectFileManager manager=new ProjectFileManager();
		List<Sproject> sprojectList=service.findAllByCriteria(DetachedCriteria.forClass(Sproject.class).add(Restrictions.eq("code",code)));
		try{
			if("true".equals(isTest)&&"default".equalsIgnoreCase(code)){
				//Ĭ����Ŀ���Է���
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
			erroMessage="����ʧ�ܣ�";
			sproject.setState(0l);
			service.saveOrUpdateIObject(sproject);
			e.printStackTrace();
		}
		SearchContext.initProject();
		return Action.SUCCESS;
	}
	/**
	 * �ջط�����Դ
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
			erroMessage="�ջ�ʧ�ܣ�";
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
	 * ���һ������
	 * 
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public String insert() throws IOException {
		if (sproject.getName() == null || sproject.getCode() == null) {
			message = "��Ŀ�����Լ���Ŀ���벻��Ϊ�գ�";
		} else {

			int n = service.getCountByCriteria(DetachedCriteria.forClass(
					Sproject.class).add(
					Restrictions.eq("name", sproject.getName().trim())));
			if (n > 0) {
				message = "��Ŀ�����ظ������ʧ�ܣ�";
			} else {
				int count = service.getCountByCriteria(DetachedCriteria
						.forClass(Sproject.class).add(
								Restrictions.eq("code", sproject.getCode()
										.trim())));
				if (count > 0) {
					message = "��Ŀ�����ظ������ʧ�ܣ�";
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
	 * ��������ɾ��һ����¼
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
	 * ��ȡһ����Ŀ ���ݵ���ϸ��Ϣ
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String viewProject() {
		//������Ϣ
		sproject = (Sproject) service.getIObjectByPK(Sproject.class, sproject
				.getId());
		// ��ȡ��Դ�б�
		files = ProjectFileManager.getChildFiles(ProjectFileManager.rootPath
				.concat(ProjectFileManager.separator)
				.concat(sproject.getCode()));
		//��ȡ�û���Ϣ
		IDaoManager dao=(IDaoManager)service;
		if(page<1){
			page=1;
		}
		//������Ŀ�û�����
		int inCount=service.getCountByCriteria(DetachedCriteria.forClass(Relation.class).add(Restrictions.eq("projectid",sproject.getId())));
		//�����û�����
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
	 * ���»�������
	 * 
	 * @return
	 */
	public String updateProject() {
		service.updateIObject(sproject);
		SearchContext.initProject();
		viewProject();
		message = "�޸ĳɹ���";
		return Action.SUCCESS;
	}

	/**
	 * ��ȡһ����Դ�ļ��µ������ļ�
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
	 * ɾ��һ���ļ�
	 * 
	 * @return
	 */
	public String deleteSourceFile() {
		fileManager.deleteDoc(sproject.getCode(), fileName, docName,true);
		files = ProjectFileManager.getChildFiles(filePath);
		return null;
	}

	/**
	 * ����һ���ļ�
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
	 * �ж�һ���ļ��Ƿ����
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
	 * ������һ���ļ�
	 * 
	 * @return
	 * @throws IOException
	 */
	public String renameFile() throws IOException {
		boolean success=fileManager.rename(sproject.getCode(), fileName, docName, newName,true);
		if (!success) {
			// ��λ���Ѿ�����ͬ���ļ���������ʧ�ܣ�
			message = "\u6b64\u4f4d\u7f6e\u5df2\u7ecf\u5305\u542b\u540c\u540d\u6587\u4ef6\uff01\u91cd\u547d\u540d\u5931\u8d25\uff01";
		}
		//getSourceFile();
		return null;
	}
/**
 * �ϴ��ļ�
 * @return
 */
	public String uploadSource(){
		String projectCode=sproject.getCode();
		fileManager.upload(projectCode, fileName,docName,uploadFile,true);
		getSourceFile();
		return Action.SUCCESS;
	}
	/**
	 * ���û���ӽ���Ŀ
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
		//������Ŀ�û�����
		int inCount=service.getCountByCriteria(DetachedCriteria.forClass(Relation.class).add(Restrictions.eq("projectid",sproject.getId())));
		//�����û�����
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
	 * ��һ����Ŀ���Ƴ��û�
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
		//������Ŀ�û�����
		int inCount=service.getCountByCriteria(DetachedCriteria.forClass(Relation.class).add(Restrictions.eq("projectid",sproject.getId())));
		//�����û�����
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
	 * ����Ŀ�е��û�����ҳ��
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getInusers(){
		IDaoManager dao=(IDaoManager)service;
		if(page<1){
			page=1;
		}
		//������Ŀ�û�����
		int inCount=service.getCountByCriteria(DetachedCriteria.forClass(Relation.class).add(Restrictions.eq("projectid",sproject.getId())));
		String hql="select id,username,mail,showname from ProjectUser where id in(select userid from Relation where projectid='"+sproject.getId()+"')";
		FunctionParameters[] foelds={new FunctionParameters("setId",String.class),new FunctionParameters("setUsername",String.class),new FunctionParameters("setMail",String.class),new FunctionParameters("setShowname",String.class)};
		userList=dao.hqlListBox(ProjectUser.class, hql, foelds, (page-1)*10, perUsers);
		DCriteriaPageSupport tempUserList=new DCriteriaPageSupport(userList,inCount,perUsers,(page-1)*10);
		userList=tempUserList;
		return Action.SUCCESS;
	}
	/**
	 * ��ò�����Ŀ�е��û�(��ҳ)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getNotInusers(){
		IDaoManager dao=(IDaoManager)service;
		if(page<1){
			page=1;
		}
		//������Ŀ�û�����
		int inCount=service.getCountByCriteria(DetachedCriteria.forClass(Relation.class).add(Restrictions.eq("projectid",sproject.getId())));
		//�����û�����
		int userCount=service.getCountByCriteria(DetachedCriteria.forClass(ProjectUser.class));
		String hql1="select id,username,mail,showname from ProjectUser where id not in(select userid from Relation where projectid='"+sproject.getId()+"')";
		FunctionParameters[] foelds1={new FunctionParameters("setId",String.class),new FunctionParameters("setUsername",String.class),new FunctionParameters("setMail",String.class),new FunctionParameters("setShowname",String.class)};
		userListNotIn=dao.hqlListBox(ProjectUser.class, hql1, foelds1, (page-1)*10, perUsers);
		DCriteriaPageSupport tempuserListNotIn=new DCriteriaPageSupport(userListNotIn,userCount-inCount,perUsers,(page-1)*10);
		userListNotIn=tempuserListNotIn;
		return Action.SUCCESS;
	}
	/**
	 * ����ļ����µ������ļ�������xml�ļ�
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
							showText="��Դ�ļ���";
						}else if("script".equalsIgnoreCase(fileName)){
							showText="�ű��ļ���";
						}else if("page".equalsIgnoreCase(fileName)){
							showText="ҳ���ļ���";
						}
						//showText="<a onclick=viewFolder('"+sproject.getCode()+"','"+file.getName()+"')><font color=black>".concat(showText).concat("</font></a>");
						element.setAttributeValue("im0","folderClosed.gif");
						text=showText.concat("<a onclick=projectOpenFileUploadPage('"+sproject.getCode()+"','"+file.getName()+"','guidConfig')>[�ϴ�]</a>");
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
	 * ɾ��һ���ļ�
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
	 * �༭�ű�
	 * @return
	 * @throws IOException 
	 */
	public String editSource() throws IOException{
		fileManager.editSource(sproject.getCode(), fileName, docName, sourceContent, true);
		return getSourceFileContent();
	}
	/**
	 * ������
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
	 * ��ȡһ���ļ����µ������ļ�
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
	 * ɾ���ļ�
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
