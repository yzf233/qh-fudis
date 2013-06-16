package com.xx.platform.web.actions.auth;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.SearchContext;
import com.xx.platform.domain.model.system.ProjectUser;
import com.xx.platform.domain.model.system.Relation;
import com.xx.platform.domain.model.system.Sproject;
import com.xx.platform.domain.model.user.User;
import com.xx.platform.util.tools.MD5;
import com.xx.platform.web.actions.BaseAction;
import com.xx.platform.web.actions.SessionAware;
import com.xx.platform.web.actions.system.ProjectFileManager;

/**
 * <p>Title: </p>
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
public class Login extends BaseAction {
    private List<User> userList;
    private int page = 1;
    private User user;
    private String uname;
    private String pwd;
    private String code;
    private String message;
    
    private String name;
    private String password;
    private String p;
    private String defualtUrl="/index.jsp";
    private List<Sproject> publishProject;//已经发布的项目
    private String isTest;
	public String getIsTest() {
		return isTest;
	}

	public void setIsTest(String isTest) {
		this.isTest = isTest;
	}

	public String list() {
        return Action.SUCCESS;
    }

    public String login() {
    	 if (SearchContext.useValidateCode && (code == null ||
    	            !code.trim().equals(request.getSession().getAttribute(
    	                    "verifyCode")))) {
    	            request.getSession().removeAttribute("verifyCode");
    	            message = "验证码错误";
    	            return Action.LOGIN;
        } else {
            request.getSession().removeAttribute("verifyCode");
            user = new User();
            user.setUsername(uname);
            user.setPassword(MD5.encoding(pwd));
            userList = service.findAllByCriteria(DetachedCriteria.forClass(User.class).
                                                 add(Restrictions.eq(
                    "username", user.getUsername())).add(Restrictions.eq(
                            "password", user.getPassword())));
            if (userList != null && userList.size() > 0) {
                user = userList.remove(0);
                request.getSession(true).removeAttribute(SessionAware.SESSION_LOGIN_ID);
                request.getSession(true).setAttribute(SessionAware.SESSION_LOGIN_ID , user);
                return Action.SUCCESS;
            } else {
                message = "用户名或密码错误";
                return Action.LOGIN;
            }
        }
    }
    public String loginTest(){
    	isTest="true";
    	StringBuilder path=new StringBuilder(); 
    	path.append("/search/").append(p).append(ProjectFileManager.testString).append("/page/login.jsp");
    	defualtUrl=path.toString();
    	return Action.SUCCESS;
    }
    public String projectUserLogin(){
    	publishProject=SearchContext.projectList;
		if(publishProject==null){
			publishProject=new ArrayList<Sproject>();
		}
		
		StringBuilder path=new StringBuilder(); 
    	List<ProjectUser> projectUsers=service.findAllByCriteria(DetachedCriteria.forClass(ProjectUser.class).add(Restrictions.eq("username",name)));
    	Sproject sproject=null;
    	for(Sproject project:publishProject){
    		if(p.equals(project.getCode())){
    			sproject=project;
    			break;
    		}
    	}
    	String p=this.p;
    	if("true".equals(isTest)){
    		p=p+ProjectFileManager.testString;
    	}
    	if(sproject==null&&!"true".equals(isTest)){
    		path.append("/search/").append(p).append("/page/login.jsp");
			defualtUrl=path.toString();
    		message="项目不存在！";
    		return Action.SUCCESS;
    	}
    	if(projectUsers==null||projectUsers.size()==0){
    		path.append("/search/").append(p).append("/page/login.jsp");
    		defualtUrl=path.toString();
    	}else{
    		ProjectUser user=projectUsers.get(0);
    		String pwd=user.getUserpassword();
    		String tempPwd=MD5.encoding(password);
    		String userId=user.getId();//用户ID
    		List<Relation> relations=service.findAllByCriteria(DetachedCriteria.forClass(Relation.class).add(Restrictions.eq("userid",userId)));
    		if(relations==null||relations.isEmpty()){
    			path.append("/search/").append(p).append("/page/login.jsp");
    			defualtUrl=path.toString();
    			message = "您不属于该项目！";
    		}else{
    			Relation tempR=null;
    			for(Relation relation:relations){
    				if(relation.getProjectid().equals(sproject.getId())){
    					tempR=relation;
    					break;
    				}
    			}
    			if(tempR==null){
    				message="您不属于该项目！";
    			}else{
    				if(!pwd.equals(tempPwd)){
    	    			path.append("/search/").append(p).append("/page/login.jsp");
    	    			defualtUrl=path.toString();
    	    			message = "用户名或密码错误";
    	    		}else{
    	    			HttpSession session=request.getSession();
    	    			session.setAttribute(SessionAware.PROJECT_LOGIN_ID,user);
    	    			path.append("/search/").append(p).append("/page/index.jsp");
    	    			defualtUrl=path.toString();
    	    		}
    			}
    		}
    	}
    	return Action.SUCCESS;
    }
    
    public String logout()
    {
        request.getSession().removeAttribute(SessionAware.SESSION_LOGIN_ID);
        return Action.LOGIN ;
    }
    public String getCode() {
        return code;
    }

    public int getPage() {
        return page;
    }

    public String getPwd() {
        return pwd;
    }

    public String getUname() {
        return uname;
    }

    public User getUser() {
        return user;
    }

    public List getUserList() {
        return userList;
    }

    public String getMessage() {
        return message;
    }

    public void setUserList(List userList) {
        this.userList = userList;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public String getDefualtUrl() {
		return defualtUrl;
	}

	public void setDefualtUrl(String defualtUrl) {
		this.defualtUrl = defualtUrl;
	}

	public List<Sproject> getPublishProject() {
		return publishProject;
	}

	public void setPublishProject(List<Sproject> publishProject) {
		this.publishProject = publishProject;
	}

}
