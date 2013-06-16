package com.xx.platform.web.actions.database;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.*;
import com.xx.platform.core.*;
import com.xx.platform.core.nutch.*;
import com.xx.platform.domain.model.database.*;
import com.xx.platform.util.tools.*;
import com.xx.platform.web.actions.*;

import java.text.*;

import org.apache.nutch.util.NutchConf;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

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
public class DbpropertyAction extends BaseAction {
    private Dbtable dbtable;
    private Tableproperty tableproperty; 
    private List<IndexFieldImpl> indexList ;
    private List<IndexFieldImpl> indexasoList ;//联合采集索引
    private static String [] errorcolumn_oracle;
    private static String [] errorcolumn_mysql;
    private static String [] errorcolumn_mssql;
    static{
    	errorcolumn_oracle=NutchConf.get().get("oracle.errorcolumn")==null?null:NutchConf.get().get("oracle.errorcolumn").split(",");
    	errorcolumn_mysql=NutchConf.get().get("mysql.errorcolumn")==null?null:NutchConf.get().get("mysql.errorcolumn").split(",");
    	errorcolumn_mssql=NutchConf.get().get("mssql.errorcolumn")==null?null:NutchConf.get().get("mssql.errorcolumn").split(",");
    }

    private boolean canaddproperty(String code,StringBuffer typename)throws Exception//获得表字段在数据库中的类型，并且判断是否能够作为更新标志。
    {
    	Statement statement = null;
    	ResultSet rs = null;
        dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class, tableproperty.getDbtableid());
       // System.out.println("dbtable.getDbid().getConnection():"+dbtable.getDbid().getConnection());
        if (dbtable.getDbid().getConnection() == null ||
        		dbtable.getDbid().getConnection().isClosed()) {
                try {
                    Class.forName(dbtable.getDbid().getDriverclazz());
                    dbtable.getDbid().setConnection(DriverManager.getConnection(dbtable.getDbid().
                            getDburl(),
                            dbtable.getDbid().getDbuser(),
                            dbtable.getDbid().getDbpass()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        if (dbtable.getDbid().getConnection() == null ||
        		dbtable.getDbid().getConnection().isClosed()) {
        return false;
        }
        statement = dbtable.getDbid().getConnection().createStatement();
        String dbtype=dbtable.getDbid().getDbtype();
        if(dbtype.equals("mssqlserver"))
        rs = statement.executeQuery("select top 1 * from ["+dbtable.getCode()+"]");
        else if(dbtype.equals("oracle"))
        rs = statement.executeQuery("select * from "+dbtable.getCode()+" WHERE  rownum<=1");
        else if(dbtype.equals("mysql"))
        rs = statement.executeQuery("select * from `"+dbtable.getCode()+"` LIMIT 0,1");
        

        
        java.sql.ResultSetMetaData meta = rs.getMetaData();
    	for(int i=1;i<=meta.getColumnCount();i++)
    	{
    		String typeName = meta.getColumnTypeName(i);
    		if((meta.getColumnName(i)).equalsIgnoreCase(code))
    		{
    			if(dbtable.getDbid().getDbtype().equalsIgnoreCase("mssqlserver"))
    			{
    				for(int j=0;errorcolumn_mssql!=null&&j<errorcolumn_mssql.length;j++)
    				{
    					if(typeName.equalsIgnoreCase(errorcolumn_mssql[j]))
    					{
    						typename.append(typeName);
    						rs.close();
    						return false;
    					}
    				}
    			}
    			else if(dbtable.getDbid().getDbtype().equalsIgnoreCase("oracle"))
    			{
    				for(int j=0;errorcolumn_oracle!=null&&j<errorcolumn_oracle.length;j++)
    				{
    					if(typeName.equalsIgnoreCase(errorcolumn_oracle[j]))
    					{
    						typename.append(typeName);
    						rs.close();
    						return false;
    					}
    				}
    			}
    			else
    			{
    				for(int j=0;errorcolumn_mysql!=null&&j<errorcolumn_mysql.length;j++)
    				{
    					if(typeName.equalsIgnoreCase(errorcolumn_mysql[j]))
    					{
    						typename.append(typeName);
    						rs.close();
    						return false;
    					}
    				}
    			}
    			    
    		}
    	}
    	rs.close();
    	return true;
    }
    public boolean hasSetFileDataName(String tid,String dbid)//表是否已经设置了文件名
    {
        List<Tableproperty> tp = service.findAllByIObjectCType(Tableproperty.class);
        for(Tableproperty t:tp)
        {
        	if(t.getDbtableid().equals(dbid)&&t.getIsfiledata()!=null&&t.getIsfiledata().equals("1")&&!tid.equals(t.getId()))
        	{
        		return true;
        	}
        }
    	return false;
    }
    public boolean hasSetFileDataPath(String tid,String dbid)//表是否已经设置了文件目录
    {
        List<Tableproperty> tp = service.findAllByIObjectCType(Tableproperty.class);
        for(Tableproperty t:tp)
        {
        	if(t.getDbtableid().equals(dbid)&&t.getIsfiledata()!=null&&t.getIsfiledata().equals("2")&&!tid.equals(t.getId()))
        	{
        		return true;
        	}
        }
    	return false;
    }
    public String addPropertyDo() throws Exception {
    	
    	
    	if(tableproperty.getIsorderby()==null)
    	{
    		tableproperty.setIsorderby("0");
    		tableproperty.setCode(tableproperty.getName());
    		tableproperty.setMultfuction("");
            List<Tableproperty> tp = service.findAllByIObjectCType(Tableproperty.class);
            for(Tableproperty t:tp)
            {
            	if(t.getDbtableid().equals(tableproperty.getDbtableid()))
            	{
            		tableproperty.setAsoindexfield(t.getAsoindexfield());//获得联合采集索引id
                    break;
            	}
            }
            tableproperty.setIsfiledata("0");
       	   service.saveIObject(tableproperty);
           SearchContext.reloadRules();
           return Action.SUCCESS;
    	}
        if (tableproperty != null) {
            tableproperty.setName(tableproperty.getCode());
            try{

              if (tableproperty.getDatatype()!=null && !"".equals(tableproperty.getDatatype().trim())&&"java.util.Date".equalsIgnoreCase(tableproperty.getDatatype())&& tableproperty.getDefaultvalue()!=null && !"".equals(tableproperty.getDefaultvalue())) {
                  java.text.SimpleDateFormat  sdf= new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                  if(!sdf.format(sdf.parse(tableproperty.getDefaultvalue())).equalsIgnoreCase(tableproperty.getDefaultvalue())){
                  message = "请写入正确数据！";
                  return Action.INPUT;
                  }
              }
              if(tableproperty.getMultfuction()!=null){
                  SimpleDateFormat sdf = new SimpleDateFormat(tableproperty.
                          getMultfuction());
              }
          }catch(Exception e){
               message = "请写入正确的数据！";
               return Action.INPUT;
          }
          /**
           * 控制更新字段只能添加一个时间类型的字段和一个非时间类型的字段 ypp
           */
          dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class, tableproperty.getDbtableid());
          
          if(tableproperty.getId()==null)
          {
        	  if(tableproperty.getIsorderby().equals("1"))
        	  {
        		  for (Tableproperty prop : dbtable.getTableproperty()) {
        			  if(prop.getIsorderby().equals("1"))
        			  {
        				  message = "已经设置了一个主要更新标志！";
        	               return Action.INPUT;
        			  }
        		  }
        	  }
        	  else if(tableproperty.getIsorderby().equals("2"))
        	  {
        		  for (Tableproperty prop : dbtable.getTableproperty()) {
        			  if(prop.getIsorderby().equals("2"))
        			  {
        				  message = "已经设置了一个次要更新标志！";
        	               return Action.INPUT;
        			  }
        		  }
        	  }
              StringBuffer typename=new StringBuffer("");
              if(!tableproperty.getIsorderby().equals("0")&&!canaddproperty(tableproperty.getCode(),typename))
              {
            	     String str="";
            	     if(dbtable.getDbid().getDbtype().equalsIgnoreCase("mssqlserver"))
            	     {
            	    	 for(int i=0;i<errorcolumn_mssql.length;i++)
            	    	 {
            	    		 str+=errorcolumn_mssql[i];
            	    		 if(i<errorcolumn_mssql.length-1)
            	    			 str+=",";
            	    		 if(i%4==0&&i!=0)
            	    			 str+="</br>";
            	    	 }
            	    	 message = "您的数据库字段类型:"+typename+" </br>sqlserver如下数据库字段类型不能做为更新标志:</br>"+str+"。";
            	     }
            	     else if(dbtable.getDbid().getDbtype().equalsIgnoreCase("oracle"))
            	     {
            	    	 for(int i=0;i<errorcolumn_oracle.length;i++)
            	    	 {
            	    		 str+=errorcolumn_oracle[i];
            	    		 if(i<errorcolumn_oracle.length-1)
            	    			 str+=",";
            	    		 if(i%4==0&&i!=0)
            	    			 str+="</br>";
            	    	 }
            	    	 message = "您的数据库字段类型:"+typename+" </br>oracle如下数据库字段类型不能做为更新标志:</br>"+str+"。";
            	     }
            	     else
            	     {
            	    	 for(int i=0;i<errorcolumn_mysql.length;i++)
            	    	 {
            	    		 str+=errorcolumn_mysql[i];
            	    		 if(i<errorcolumn_mysql.length-1)
            	    			 str+=",";
            	    		 if(i%4==0&&i!=0)
            	    			 str+="</br>";
            	    	 }
            	    	 message = "您的数据库字段类型:"+typename+" </br>mysql如下数据库字段类型不能做为更新标志:</br>"+str+"。";
            	     }
            	     
                     return Action.INPUT;
              }
              if(!tableproperty.getIsorderby().equals("0"))
              {
            	  if (tableproperty.getDatatype().equals("java.util.Date")) {
            		  for (Tableproperty prop : dbtable.getTableproperty()) {
            			  if(prop.getDatatype().equals("java.util.Date")&&!prop.getIsorderby().equals("0"))
            			  {
            				  message = "已经设置了时间类型的更新标志！";
            	               return Action.INPUT;
            			  }
            		  }
            	  } else  
            	  {
            		  for (Tableproperty prop : dbtable.getTableproperty()) {
            			  if(!prop.getDatatype().equals("java.util.Date")&&!prop.getIsorderby().equals("0"))
            			  {
            				  message = "已经设置了非时间类型的更新标志！";
            	               return Action.INPUT;
            			  }
            		  }
            	  }
            }
              List<Tableproperty> tp = service.findAllByIObjectCType(Tableproperty.class);
              for(Tableproperty t:tp)
              {
              	if(t.getDbtableid().equals(tableproperty.getDbtableid()))
              	{
              		tableproperty.setAsoindexfield(t.getAsoindexfield());//获得联合采集索引id
                      break;
              	}
              }
              tableproperty.setIsfiledata("0");
          	   service.saveIObject(tableproperty);
               SearchContext.reloadRules();
               return Action.SUCCESS;
          }
          dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class, tableproperty.getDbtableid());

          if(!tableproperty.getIsorderby().equals("0")){            
              String is=tableproperty.getIsorderby();
              if(is.equals("1"))
              {
            	  for (Tableproperty prop : dbtable.getTableproperty()) {
            		  if(prop.getIsorderby().equals("1")&&!tableproperty.getId().equals(prop.getId()))
            		  {
            			  message="已经设置了一个主要更新标志";
            			  return  Action.INPUT;
            		  }
            	  }
                  StringBuffer typename=new StringBuffer("");
                  if(!canaddproperty(tableproperty.getCode(),typename))
                  {
                	     String str="";
                	     if(dbtable.getDbid().getDbtype().equalsIgnoreCase("mssqlserver"))
                	     {
                	    	 for(int i=0;i<errorcolumn_mssql.length;i++)
                	    	 {
                	    		 str+=errorcolumn_mssql[i];
                	    		 if(i<errorcolumn_mssql.length-1)
                	    			 str+=",";
                	    		 if(i%4==0&&i!=0)
                	    			 str+="</br>";
                	    	 }
                	    	 message = "您的数据库字段类型:"+typename+" </br>sqlserver如下数据库字段类型不能做为更新标志:</br>"+str+"。";
                	     }
                	     else if(dbtable.getDbid().getDbtype().equalsIgnoreCase("oracle"))
                	     {
                	    	 for(int i=0;i<errorcolumn_oracle.length;i++)
                	    	 {
                	    		 str+=errorcolumn_oracle[i];
                	    		 if(i<errorcolumn_oracle.length-1)
                	    			 str+=",";
                	    		 if(i%4==0&&i!=0)
                	    			 str+="</br>";
                	    	 }
                	    	 message = "您的数据库字段类型:"+typename+" </br>oracle如下数据库字段类型不能做为更新标志:</br>"+str+"。";
                	     }
                	     else
                	     {
                	    	 for(int i=0;i<errorcolumn_mysql.length;i++)
                	    	 {
                	    		 str+=errorcolumn_mysql[i];
                	    		 if(i<errorcolumn_mysql.length-1)
                	    			 str+=",";
                	    		 if(i%4==0&&i!=0)
                	    			 str+="</br>";
                	    	 }
                	    	 message = "您的数据库字段类型:"+typename+" </br>mysql如下数据库字段类型不能做为更新标志:</br>"+str+"。";
                	     }
                         return Action.INPUT;
                  }
                  for (Tableproperty prop : dbtable.getTableproperty()) {
            		  if(prop.getIsorderby().equals("1")&&tableproperty.getId().equals(prop.getId()))
            		  {
                          if(!prop.getIsorderby().equals(tableproperty.getIsorderby()))//如果用户修改了自身的更新标志属性
                          {
                        	dbtable.setLasttime(null);
                        	dbtable.setIdnum(null);
                        	dbtable.setPagenum(0);
                          }
            		  }
            		  else if(prop.getIsorderby().equals("0")&&tableproperty.getId().equals(prop.getId()))
                	  {
                		dbtable.setLasttime(null);
                      	dbtable.setIdnum(null);
                      	dbtable.setPagenum(0);
                	  }
            	  }

              }
              else if(is.equals("2"))
              {
            	  for (Tableproperty prop : dbtable.getTableproperty()) {
            		  if(prop.getIsorderby().equals("2")&&!tableproperty.getId().equals(prop.getId()))
            		  {
            			  message="已经设置了一个次要更新标志";
            			  return  Action.INPUT;
            		  }
            	  }
                  if (tableproperty.getDatatype().equals("blob")) {
                	  message = "blob不能作为更新标志！";
                      return Action.INPUT;
                  }
                  if (tableproperty.getDatatype().equals("clob")) {
                	  message = "clob不能作为更新标志！";
                      return Action.INPUT;
                  }
                  StringBuffer typename=new StringBuffer("");
                  if(!canaddproperty(tableproperty.getCode(),typename))
                  {
                	     String str="";
                	     if(dbtable.getDbid().getDbtype().equalsIgnoreCase("mssqlserver"))
                	     {
                	    	 for(int i=0;i<errorcolumn_mssql.length;i++)
                	    	 {
                	    		 str+=errorcolumn_mssql[i];
                	    		 if(i<errorcolumn_mssql.length-1)
                	    			 str+=",";
                	    		 if(i%4==0&&i!=0)
                	    			 str+="</br>";
                	    	 }
                	    	 message = "您的数据库字段类型:"+typename+" </br>sqlserver如下数据库字段类型不能做为更新标志:</br>"+str+"。";
                	     }
                	     else if(dbtable.getDbid().getDbtype().equalsIgnoreCase("oracle"))
                	     {
                	    	 for(int i=0;i<errorcolumn_oracle.length;i++)
                	    	 {
                	    		 str+=errorcolumn_oracle[i];
                	    		 if(i<errorcolumn_oracle.length-1)
                	    			 str+=",";
                	    		 if(i%4==0&&i!=0)
                	    			 str+="</br>";
                	    	 }
                	    	 message = "您的数据库字段类型:"+typename+" </br>oracle如下数据库字段类型不能做为更新标志:</br>"+str+"。";
                	     }
                	     else
                	     {
                	    	 for(int i=0;i<errorcolumn_mysql.length;i++)
                	    	 {
                	    		 str+=errorcolumn_mysql[i];
                	    		 if(i<errorcolumn_mysql.length-1)
                	    			 str+=",";
                	    		 if(i%4==0&&i!=0)
                	    			 str+="</br>";
                	    	 }
                	    	 message = "您的数据库字段类型:"+typename+" </br>mysql如下数据库字段类型不能做为更新标志:</br>"+str+"。";
                	     }
                         return Action.INPUT;
                  }
                  for (Tableproperty prop : dbtable.getTableproperty()) {
                	  if(prop.getIsorderby().equals("2")&&tableproperty.getId().equals(prop.getId()))
            		  {
                          if(!prop.getIsorderby().equals(tableproperty.getIsorderby()))//如果用户修改了自身的更新标志属性
                          {
                        	dbtable.setLasttime(null);
                        	dbtable.setIdnum(null);
                        	dbtable.setPagenum(0);
                          }
            		  }
                	  else if(prop.getIsorderby().equals("0")&&tableproperty.getId().equals(prop.getId()))
                	  {
                		dbtable.setLasttime(null);
                      	dbtable.setIdnum(null);
                      	dbtable.setPagenum(0);
                	  }
            	  }

              }
              else
              {
           	    for (Tableproperty prop : dbtable.getTableproperty()) {
                 	  if(!prop.getIsorderby().equals("0")&&tableproperty.getId().equals(prop.getId()))
             		  {
                           if(!prop.getIsorderby().equals(tableproperty.getIsorderby()))//如果用户修改了自身的更新标志属性
                           {
                         	dbtable.setLasttime(null);
                         	dbtable.setIdnum(null);
                         	dbtable.setPagenum(0);
                           }
             		  }
             	  }
           	   
              }

              
        	  if (tableproperty.getDatatype().equals("java.util.Date")) {
        		  for (Tableproperty prop : dbtable.getTableproperty()) {
        			  if(prop.getDatatype().equals("java.util.Date")&&!prop.getIsorderby().equals("0"))
        			  {
        				  message = "已经设置了时间类型的更新标志！";
        	               return Action.INPUT;
        			  }
        		  }
        	  } else  
        	  {
        		  for (Tableproperty prop : dbtable.getTableproperty()) {
        			  if(!prop.getDatatype().equals("java.util.Date")&&!prop.getIsorderby().equals("0"))
        			  {
        				  message = "已经设置了非时间类型的更新标志！";
        	               return Action.INPUT;
        			  }
        		  }
        	  }
        	  
          }
            //tableproperty.s


                List<Tableproperty> tp = service.findAllByIObjectCType(Tableproperty.class);
                for(Tableproperty t:tp)
                {
                	if(t.getDbtableid().equals(tableproperty.getDbtableid()))
                	{
                		tableproperty.setAsoindexfield(t.getAsoindexfield());//获得联合采集索引id
                        break;
                	}
                }
                tableproperty.setIsfiledata("0");
                service.saveIObject(tableproperty);
                service.updateIObject(dbtable);
                SearchContext.reloadRules();
        }
        return Action.SUCCESS;
    }

    /**
 * 复制
 * @return String
 * @throws Exception
 */
public String copy() throws Exception {
  if (dbtable != null && dbtable.getId() != null) {
      sdbtable = (Dbtable) service.getIObjectByPK(Dbtable.class,
                      dbtable.getId());

  }
  //System.out.println("copy()"+gcrawler.getName());
  response.setCharacterEncoding("UTF-8");
  if (sdbtable == null || sdbtable.getName() == null)
    response.getWriter().write("<font color=green>复制失败</font>");
  else
    response.getWriter().write("<font color=green>复制 " + sdbtable.getName() +
                               " 成功</font>");
  return null;
  //return Action.SUCCESS;
}

/**
 * 粘贴
 * @return String
 * @throws Exception
 */
public String paste() throws Exception {
  if (sdbtable != null && sdbtable.getId() != null &&dbtable!=null&&dbtable.getId() != null) {
      Iterator<Tableproperty> it = sdbtable.getTableproperty().iterator();
      service.execByHQL("delete from Tableproperty where dbtableid='" +
                        dbtable.getId() + "'");
//      dbtable.setTableproperty(new java.util.HashSet<Tableproperty>());
      Tableproperty property = null;
      while (it.hasNext()) {
          property = it.next();
          property.setDbtableid(dbtable.getId());
//          dbtable.getTableproperty().add(property);
          service.saveIObject(property);
      }
  }
  //System.out.println("paste()");
  // return null;
  SearchContext.reloadRules();
  return Action.SUCCESS;
}


    public String editProperty() {
        if (dbtable != null && dbtable.getId() != null && tableproperty != null &&
            tableproperty.getId() != null)
            dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class,
                    dbtable.getId());

          Iterator<Tableproperty> it = dbtable.getTableproperty().iterator();
          Tableproperty property = null;
          indexList = service.findAllByIObjectCType(IndexFieldImpl.class) ;
          List<IndexFieldImpl> ls = service.findAllByIObjectCType(IndexFieldImpl.class) ;
          indexasoList=new ArrayList();
          for(IndexFieldImpl s:ls)
          {
        	  if(s.isIsfilecontent())
        	  indexasoList.add(s);
          }
          
          while (it.hasNext()) {
              property = it.next();
              if (tableproperty.getId().equals(property.getId())) {
                  tableproperty = property;
                  break;
              }

      }

        return Action.SUCCESS;
    }

    public String editPropertyDo() throws ClassNotFoundException, SQLException {
    	if(tableproperty != null && tableproperty.getId() != null&&tableproperty.isDbfield()==false)
    	{
    	    if(tableproperty.getIsfiledata()!=null&&!tableproperty.getIsfiledata().equals("0"))
    	    {
     	    	if(tableproperty.getIsfiledata().equals("1")&&hasSetFileDataName(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		message="当前表联合采集已经设置了一个<span style='color=red'>文件名</span>字段。</br>";
     	    		return Action.INPUT;
     	    	} 	
     	    	if(tableproperty.getIsfiledata().equals("1")&&!hasSetFileDataName(tableproperty.getId(),tableproperty.getDbtableid())&&!hasSetFileDataPath(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		message="<span style='color=red'>文件名</span>设置成功，联合采集您还需要设置了一</br>个<span style='color=red'>文件目录</span>字段。</br>";
     	    		if(tableproperty.getAsoindexfield()==null||(tableproperty.getAsoindexfield()!=null&&tableproperty.getAsoindexfield().equals("")))
     	    			message+="<span style='color=red'>请您指定联合采集索引字段</span>";
     	    	} 
     	    	if(tableproperty.getIsfiledata().equals("1")&&!hasSetFileDataName(tableproperty.getId(),tableproperty.getDbtableid())&&hasSetFileDataPath(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		if(tableproperty.getAsoindexfield()==null||(tableproperty.getAsoindexfield()!=null&&tableproperty.getAsoindexfield().equals("")))
     	    			message="<span style='color=red'>请您指定联合采集索引字段</span>";
     	    		else
     	    			message="联合采集设置成功。</br>";
     	    	} 
     	    	if(tableproperty.getIsfiledata().equals("2")&&hasSetFileDataPath(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		message="当前表联合采集已经设置了一个<span style='color=red'>文件目录</span>字段。</br>";
     	    		return Action.INPUT;
     	    	} 	
     	    	if(tableproperty.getIsfiledata().equals("2")&&!hasSetFileDataPath(tableproperty.getId(),tableproperty.getDbtableid())&&!hasSetFileDataName(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		message="<span style='color=red'>文件目录</span>设置成功，联合采集您还需要设置了一</br>个<span style='color=red'>文件名</span>字段。</br>";
     	    		if(tableproperty.getAsoindexfield()==null||(tableproperty.getAsoindexfield()!=null&&tableproperty.getAsoindexfield().equals("")))
         	    		message+="<span style='color=red'>请您指定联合采集索引字段</span>";
     	    	} 
     	    	if(tableproperty.getIsfiledata().equals("2")&&!hasSetFileDataPath(tableproperty.getId(),tableproperty.getDbtableid())&&hasSetFileDataName(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		if(tableproperty.getAsoindexfield()==null||(tableproperty.getAsoindexfield()!=null&&tableproperty.getAsoindexfield().equals("")))
     	    			message="<span style='color=red'>请您指定联合采集索引字段</span>";
     	    		else
     	    			message="联合采集设置成功。</br>";
     	    	} 
    	    }
      	   tableproperty.setName(tableproperty.getCode());
    	   service.updateIObject(tableproperty);
    	   boolean change=false;
    	   String newIndexName="";
           List<Tableproperty> tp = service.findAllByIObjectCType(Tableproperty.class);
           for(Tableproperty t:tp)
           {
           	if(t.getDbtableid().equals(tableproperty.getDbtableid()))//更新这个表的所有的对应联合索引的id
           	{
           		if(tableproperty.getAsoindexfield()!=null&&!tableproperty.getAsoindexfield().equals(""))
           		{
           		  if(t.getAsoindexfield()==null||t.getAsoindexfield().equals("")||(t.getAsoindexfield()!=null&&!t.getAsoindexfield().equals(tableproperty.getAsoindexfield())))
           		  {
           			change=true;
           			newIndexName=((IndexFieldImpl)service.getIObjectByPK(IndexFieldImpl.class, tableproperty.getAsoindexfield())).getName();
           		    t.setAsoindexfield(tableproperty.getAsoindexfield());
           		    service.updateIObject(t);
           		  }
           		}
           		else
           		{
           		    t.setAsoindexfield("");
           		    service.updateIObject(t);
           		}
           	}
           }
           if(!newIndexName.equals(""))
           {
           message=((message==null)?"":message)+"当前表联合采集索引字段设置为：<span style='color=red'>"+newIndexName+"</span>";
           SearchContext.reloadRules();
           return Action.INPUT;
           }
           else
           {
        	    SearchContext.reloadRules();
               	if(message!=null&&!message.equals(""))
                return Action.INPUT;
            	else
            	return Action.SUCCESS;	
           }
    	}
    	
    	if (tableproperty != null && tableproperty.getId() != null) {
            tableproperty.setName(tableproperty.getCode());
            try{
               if (tableproperty.getDatatype()!=null && !"".equals(tableproperty.getDatatype().trim())&&"java.util.Date".equalsIgnoreCase(tableproperty.getDatatype()) && tableproperty.getDefaultvalue()!=null && !"".equals(tableproperty.getDefaultvalue())) {
                   if(tableproperty.getDefaultvalue()!=null&&!"".equals(tableproperty.getDefaultvalue())){
                       SimpleDateFormat  sdf= new  SimpleDateFormat("yyyy-MM-dd");
                    if(!sdf.format(sdf.parse(tableproperty.getDefaultvalue())).equalsIgnoreCase(tableproperty.getDefaultvalue())){
                    message = "请写入正确的数据";
                    SearchContext.reloadRules();
                    return Action.INPUT;
                   }
                  }
              }
              if(tableproperty.getMultfuction()!=null){
                 SimpleDateFormat sdf = new SimpleDateFormat(tableproperty.
                         getMultfuction());
             }
           }catch(Exception e){
               // e.printStackTrace();
                message = "请写入正确的数据";
                SearchContext.reloadRules();
                return Action.INPUT;
           }

           /**
          * 控制更新标志只能添加一个时间类型的字段和一个非时间类型的字段 ypp
          */

           
           dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class, tableproperty.getDbtableid());
           String is=tableproperty.getIsorderby();
           if(is.equals("1"))
           {
         	  for (Tableproperty prop : dbtable.getTableproperty()) {
         		  if(prop.getIsorderby().equals("1")&&!tableproperty.getId().equals(prop.getId()))
         		  {
         			  message="已经设置了一个主要更新标志";
         			  return  Action.INPUT;
         		  }
         	  }
              try
              {
            	  StringBuffer typename=new StringBuffer("");
            	  if(!canaddproperty(tableproperty.getCode(),typename))
            	  {
            		  String str="";
             	     if(dbtable.getDbid().getDbtype().equalsIgnoreCase("mssqlserver"))
            	     {
            	    	 for(int i=0;i<errorcolumn_mssql.length;i++)
            	    	 {
            	    		 str+=errorcolumn_mssql[i];
            	    		 if(i<errorcolumn_mssql.length-1)
            	    			 str+=",";
            	    		 if(i%4==0&&i!=0)
            	    			 str+="</br>";
            	    	 }
            	    	 message = "您的数据库字段类型:"+typename+" </br>sqlserver如下数据库字段类型不能做为更新标志:</br>"+str+"。";
            	     }
            	     else if(dbtable.getDbid().getDbtype().equalsIgnoreCase("oracle"))
            	     {
            	    	 for(int i=0;i<errorcolumn_oracle.length;i++)
            	    	 {
            	    		 str+=errorcolumn_oracle[i];
            	    		 if(i<errorcolumn_oracle.length-1)
            	    			 str+=",";
            	    		 if(i%4==0&&i!=0)
            	    			 str+="</br>";
            	    	 }
            	    	 message = "您的数据库字段类型:"+typename+" </br>oracle如下数据库字段类型不能做为更新标志:</br>"+str+"。";
            	     }
            	     else
            	     {
            	    	 for(int i=0;i<errorcolumn_mysql.length;i++)
            	    	 {
            	    		 str+=errorcolumn_mysql[i];
            	    		 if(i<errorcolumn_mysql.length-1)
            	    			 str+=",";
            	    		 if(i%4==0&&i!=0)
            	    			 str+="</br>";
            	    	 }
            	    	 message = "您的数据库字段类型:"+typename+" </br>mysql如下数据库字段类型不能做为更新标志:</br>"+str+"。";
            	     }
            		  return Action.INPUT;
            	  }
              }
              catch(Exception e)
              {e.printStackTrace();
              }
              for (Tableproperty prop : dbtable.getTableproperty()) {
            	  if(prop.getIsorderby().equals("1")&&tableproperty.getId().equals(prop.getId()))
        		  {
                      if(!prop.getIsorderby().equals(tableproperty.getIsorderby()))//如果用户修改了自身的更新标志属性
                      {
                    	dbtable.setLasttime(null);
                    	dbtable.setIdnum(null);
                    	dbtable.setPagenum(0);
                      }
        		  }
            	  else if(prop.getIsorderby().equals("0")&&tableproperty.getId().equals(prop.getId()))
            	  {
            		dbtable.setLasttime(null);
                  	dbtable.setIdnum(null);
                  	dbtable.setPagenum(0);
            	  }
        	  }

           } 
           else if(is.equals("2"))
           {
         	  for (Tableproperty prop : dbtable.getTableproperty()) {
         		
         		  if(prop.getIsorderby().equals("2")&&!tableproperty.getId().equals(prop.getId()))
         		  {
         			  message="已经设置了一个次要更新标志";
         			  return  Action.INPUT;
         		  }
         	  }
              try
              {
            	  StringBuffer typename=new StringBuffer("");
            	  if(!canaddproperty(tableproperty.getCode(),typename))
            	  {
            		  String str="";
             	     if(dbtable.getDbid().getDbtype().equalsIgnoreCase("mssqlserver"))
            	     {
            	    	 for(int i=0;i<errorcolumn_mssql.length;i++)
            	    	 {
            	    		 str+=errorcolumn_mssql[i];
            	    		 if(i<errorcolumn_mssql.length-1)
            	    			 str+=",";
            	    		 if(i%4==0&&i!=0)
            	    			 str+="</br>";
            	    	 }
            	    	 message = "您的数据库字段类型:"+typename+" </br>sqlserver如下数据库字段类型不能做为更新标志:</br>"+str+"。";
            	     }
            	     else if(dbtable.getDbid().getDbtype().equalsIgnoreCase("oracle"))
            	     {
            	    	 for(int i=0;i<errorcolumn_oracle.length;i++)
            	    	 {
            	    		 str+=errorcolumn_oracle[i];
            	    		 if(i<errorcolumn_oracle.length-1)
            	    			 str+=",";
            	    		 if(i%4==0&&i!=0)
            	    			 str+="</br>";
            	    	 }
            	    	 message = "您的数据库字段类型:"+typename+" </br>oracle如下数据库字段类型不能做为更新标志:</br>"+str+"。";
            	     }
            	     else
            	     {
            	    	 for(int i=0;i<errorcolumn_mysql.length;i++)
            	    	 {
            	    		 str+=errorcolumn_mysql[i];
            	    		 if(i<errorcolumn_mysql.length-1)
            	    			 str+=",";
            	    		 if(i%4==0&&i!=0)
            	    			 str+="</br>";
            	    	 }
            	    	 message = "您的数据库字段类型:"+typename+" </br>mysql如下数据库字段类型不能做为更新标志:</br>"+str+"。";
            	     }
            		  return Action.INPUT;
            	  }
              }
              catch(Exception e)
              {e.printStackTrace();}
              for (Tableproperty prop : dbtable.getTableproperty()) {
            	  if(prop.getIsorderby().equals("2")&&tableproperty.getId().equals(prop.getId()))
        		  {
                      if(!prop.getIsorderby().equals(tableproperty.getIsorderby()))//如果用户修改了自身的更新标志属性
                      {
                    	dbtable.setLasttime(null);
                    	dbtable.setIdnum(null);
                    	dbtable.setPagenum(0);
                      }
        		  }
            	  else if(prop.getIsorderby().equals("0")&&tableproperty.getId().equals(prop.getId()))
            	  {
            		dbtable.setLasttime(null);
                  	dbtable.setIdnum(null);
                  	dbtable.setPagenum(0);
            	  }
        	  }
           }
           else
           {
        	    for (Tableproperty prop : dbtable.getTableproperty()) {
              	  if(!prop.getIsorderby().equals("0")&&tableproperty.getId().equals(prop.getId()))
          		  {
                        if(!prop.getIsorderby().equals(tableproperty.getIsorderby()))//如果用户修改了自身的更新标志属性
                        {
                      	dbtable.setLasttime(null);
                      	dbtable.setIdnum(null);
                      	dbtable.setPagenum(0);
                        }
          		  }
          	  }
        	   
           }

           

           if(!tableproperty.getIsorderby().equals("0")){ 
         	  if (tableproperty.getDatatype().equals("java.util.Date")) {
        		  for (Tableproperty prop : dbtable.getTableproperty()) {
        			  if(prop.getDatatype().equals("java.util.Date")&&!prop.getIsorderby().equals("0")&&!tableproperty.getId().equals(prop.getId()))
        			  {
        				  message = "已经设置了时间类型的更新标志！";
        	               return Action.INPUT;
        			  }
        		  }
        	  } else  
        	  {
        		  for (Tableproperty prop : dbtable.getTableproperty()) {
        			  if(!prop.getDatatype().equals("java.util.Date")&&!prop.getIsorderby().equals("0")&&!tableproperty.getId().equals(prop.getId()))
        			  {
        				  message = "已经设置了非时间类型的更新标志！";
        	               return Action.INPUT;
        			  }
        		  }
        	  }
           }
            
           
           
    	    if(tableproperty.getIsfiledata()!=null&&!tableproperty.getIsfiledata().equals("0"))
    	    {
     	    	if(tableproperty.getIsfiledata().equals("1")&&hasSetFileDataName(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		message="当前表联合采集已经设置了一个<span style='color=red'>文件名</span>字段。</br>";
     	    		return Action.INPUT;
     	    	} 	
     	    	if(tableproperty.getIsfiledata().equals("1")&&!hasSetFileDataName(tableproperty.getId(),tableproperty.getDbtableid())&&!hasSetFileDataPath(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		message="<span style='color=red'>文件名</span>设置成功，联合采集您还需要设置了一</br>个<span style='color=red'>文件目录</span>字段。</br>";
     	    		if(tableproperty.getAsoindexfield()==null||(tableproperty.getAsoindexfield()!=null&&tableproperty.getAsoindexfield().equals("")))
     	    			message+="<span style='color=red'>请您指定联合采集索引字段</span>";
     	    	} 
     	    	if(tableproperty.getIsfiledata().equals("1")&&!hasSetFileDataName(tableproperty.getId(),tableproperty.getDbtableid())&&hasSetFileDataPath(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		if(tableproperty.getAsoindexfield()==null||(tableproperty.getAsoindexfield()!=null&&tableproperty.getAsoindexfield().equals("")))
     	    			message="<span style='color=red'>请您指定联合采集索引字段</span>";
     	    		else
     	    			message="联合采集设置成功。</br>";
     	    	} 
     	    	if(tableproperty.getIsfiledata().equals("2")&&hasSetFileDataPath(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		message="当前表联合采集已经设置了一个<span style='color=red'>文件目录</span>字段。</br>";
     	    		return Action.INPUT;
     	    	} 	
     	    	if(tableproperty.getIsfiledata().equals("2")&&!hasSetFileDataPath(tableproperty.getId(),tableproperty.getDbtableid())&&!hasSetFileDataName(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		message="<span style='color=red'>文件目录</span>设置成功，联合采集您还需要设置了一</br>个<span style='color=red'>文件名</span>字段。</br>";
     	    		if(tableproperty.getAsoindexfield()==null||(tableproperty.getAsoindexfield()!=null&&tableproperty.getAsoindexfield().equals("")))
         	    		message+="<span style='color=red'>请您指定联合采集索引字段</span>";
     	    	} 
     	    	if(tableproperty.getIsfiledata().equals("2")&&!hasSetFileDataPath(tableproperty.getId(),tableproperty.getDbtableid())&&hasSetFileDataName(tableproperty.getId(),tableproperty.getDbtableid()))
     	    	{
     	    		if(tableproperty.getAsoindexfield()==null||(tableproperty.getAsoindexfield()!=null&&tableproperty.getAsoindexfield().equals("")))
     	    			message="<span style='color=red'>请您指定联合采集索引字段</span>";
     	    		else
     	    			message="联合采集设置成功。</br>";
     	    	} 
    	    }
           
           
            service.updateIObject(tableproperty);
     	   boolean change=false;
    	   String newIndexName="";
           List<Tableproperty> tp = service.findAllByIObjectCType(Tableproperty.class);
           for(Tableproperty t:tp)
           {
           	if(t.getDbtableid().equals(tableproperty.getDbtableid()))//更新这个表的所有的对应联合索引的id
           	{
           		if(tableproperty.getAsoindexfield()!=null&&!tableproperty.getAsoindexfield().equals(""))
           		{
           		  if(t.getAsoindexfield()==null||t.getAsoindexfield().equals("")||(t.getAsoindexfield()!=null&&!t.getAsoindexfield().equals(tableproperty.getAsoindexfield())))
           		  {
           			change=true;
           			newIndexName=((IndexFieldImpl)service.getIObjectByPK(IndexFieldImpl.class, tableproperty.getAsoindexfield())).getName();
           		    t.setAsoindexfield(tableproperty.getAsoindexfield());
           		    service.updateIObject(t);
           		  }
           		}
           		else
           		{
           		    t.setAsoindexfield("");
           		    service.updateIObject(t);
           		}
           	}
           }
           if(!newIndexName.equals(""))
           message=((message==null)?"":message)+"当前表联合采集索引字段设置为：<span style='color=red'>"+newIndexName+"</span>";
           dbtable.getDbid().setConnection(null);
           service.updateIObject(dbtable);
            SearchContext.reloadRules();
        }
    	

    	if(message!=null&&!message.equals(""))
        return Action.INPUT;
    	else
    	return Action.SUCCESS;	
    }

    public Dbtable getDbtable() {
        return dbtable;
    }

    public Tableproperty getTableproperty() {
        return tableproperty;
    }

  public List getIndexList() {
    return indexList;
  }

  public void setDbtable(Dbtable dbtable) {
        this.dbtable = dbtable;
    }

    public void setTableproperty(Tableproperty tableproperty) {
        this.tableproperty = tableproperty;
    }

  public void setIndexList(List indexList) {
    this.indexList = indexList;
  }

  private final Set getDatabasetableproperty(String dbid,
                                               String tableName) throws
            Exception {

        Dbconfig databaseconinfo = (Dbconfig) service.
                                   getIObjectByPK(
                                           Dbconfig.class, dbid);
        ResultSet rs = null;
        Connection conn = null;
        Set propertySet = null;
        try {
            Class.forName(databaseconinfo.getDriverclazz());

            conn = DriverManager.getConnection(databaseconinfo.getDburl(),
                                               databaseconinfo.getDbuser(),
                                               databaseconinfo.getDbpass());

            DatabaseMetaData dbMetData = conn.getMetaData();
            {
                rs = dbMetData.getColumns(null,
                                          convertDatabaseCharsetType(
                                                  databaseconinfo.
                                                  getDbuser().toUpperCase(),
                                                  databaseconinfo.getDbtype()),
                                          tableName, null);
            }
            propertySet = new HashSet();
            Tableproperty property = null;
            while (rs.next()) {
                property = new Tableproperty();
                property.setCode(rs.getString(4).toLowerCase());
                property.setName(rs.getString(4).toLowerCase());
                property.setDatatype(DataMapping.convertDataType(rs.getInt(5)));
                property.setAllownull(false);
                propertySet.add(property);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return propertySet;

    }

  
  
    public static String convertDatabaseCharsetType(String in, String type) {
        String dbUser;
        if (in != null) {
            if (type.equals("oracle")) {
                dbUser = in.toUpperCase();
            } else if (type.equals("postgresql")) {
                dbUser = in.toLowerCase();
            } else if (type.equals("mysql")) {
                dbUser = null;
            } else if (type.equals("mssqlserver")) {
                dbUser = null;
            } else {
                dbUser = in;
            }
        } else {
            dbUser = "public";
        }
        return dbUser;
    }
	public List<IndexFieldImpl> getIndexasoList() {
		return indexasoList;
	}
	public void setIndexasoList(List<IndexFieldImpl> indexasoList) {
		this.indexasoList = indexasoList;
	}
}
