package com.xx.platform.core.task;

import java.util.Map;
import java.sql.DriverManager;
import java.sql.Connection;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.*;
import com.xx.platform.domain.model.database.Dbconfig;
import com.xx.platform.domain.model.database.Dbtable;
import com.xx.platform.domain.model.database.Tableproperty;
import com.xx.platform.web.actions.crawl.*;

import java.sql.SQLException;
import java.sql.PreparedStatement;

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
public class ClearDbParseTask implements Task {
    private static boolean run_ing = false ;
    private static int num = 0 ;
    public void doTask() {
        com.xdtech.platform.util.tools.a.a(false) ;
        if (SearchContext.dbValueMap.size()>0 && !run_ing && SearchContext.getDao()!=null) {
            run_ing = true ;
            //存入数据库
            num++ ;
            try {
                clear();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            run_ing = false ;
        }
    }
    private void clear() throws Exception
    {

        Map valueMap ;
        Dbconfig dbconfig ;
        Dbtable dbtable ;
        PreparedStatement statement = null ;
        for(int i=0 ; i<SearchContext.dbValueMap.size() ; i++)
        {
            dbtable = SearchContext.dbValueMap.remove(i) ;
            dbconfig = (Dbconfig)dbtable.getDbid() ;
            if(dbconfig.getConnection()==null)
            {//获得数据库连接
                createConnection(dbconfig) ;
            }
            //开始存入数据库
            crateSQL(dbtable , statement , dbconfig) ;
        }
        for(Dbtable dbTable:SearchContext.dbtableList)
        {
            if(dbTable.getDbid().getConnection()!=null)
            {
                dbTable.getDbid().getConnection().close();
                dbTable.getDbid().setConnection(null) ;
            }
        }
    }
    private void createConnection(Dbconfig dbconfig)
    {
        try {
            Class.forName(dbconfig.getDriverclazz());
            dbconfig.setConnection(DriverManager.getConnection(dbconfig.getDburl(),
                                               dbconfig.getDbuser(),
                                               dbconfig.getDbpass()));
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    private void crateSQL(Dbtable dbtable , PreparedStatement statement , Dbconfig dbconfig) throws Exception {
        StringBuffer sql = new StringBuffer("INSERT INTO ") ;
        sql.append(dbtable.getCode()).append("(");
        String encode = null ;
        int propertyNum = 0 ;

        for(Tableproperty tableProperty:dbtable.getTableproperty())
        {
            if(propertyNum>0)
            {
                sql.append(",");
            }
            sql.append(tableProperty.getCode()) ;
            propertyNum++ ;
        }
        sql.append(") VALUES(") ;
        propertyNum = 0 ;
        for(Tableproperty tableProperty:dbtable.getTableproperty())
        {
            if (propertyNum > 0) {
                sql.append(",");
            }
            if(tableProperty.getDatatype()!=null && tableProperty.getDatatype().equals("String"))
            {
                sql.append("?");
                propertyNum++;
            }else
            {
                if(tableProperty.getParseValue()==null || tableProperty.getParseValue().trim().equals(""))
                {
                    sql.append(tableProperty.getDefaultvalue());
                }else
                {
                    sql.append(tableProperty.getParseValue());
                }
                propertyNum++;
            }
        }
        sql.append(")");
        if(statement==null && dbconfig.getConnection()!=null && !dbconfig.getConnection().isClosed())
        {


            statement = dbconfig.getConnection().prepareStatement(sql.toString()) ;
        }
        if(statement!=null)
        {
            statement.clearParameters();
        }else
        {
            return ;
        }
        propertyNum = 1 ;
        for(Tableproperty tableProperty:dbtable.getTableproperty())
        {
            if(tableProperty.getDatatype()!=null && !tableProperty.getDatatype().equals("String"))
                continue ;
            if(tableProperty.getParseValue()==null || tableProperty.getParseValue().trim().equals(""))
            {
                if((encode=parseEncoding(dbconfig.getDburl()))!=null)
                {
                    statement.setString(propertyNum,tableProperty.getDefaultvalue()!=null?
                                        new String((dbconfig.getCode()!=null&&!dbconfig.getCode().trim().equals("")?tableProperty.getDefaultvalue().getBytes(dbconfig.getCode()):tableProperty.getDefaultvalue().getBytes()),encode):tableProperty.getDefaultvalue());
                }else
                {
                    statement.setString(propertyNum,
                                        tableProperty.getDefaultvalue());
                }
            }else
            {
                if((encode=parseEncoding(dbconfig.getDburl()))!=null)
                {
                    statement.setString(propertyNum,
                                        new String((dbconfig.getCode()!=null&&!dbconfig.getCode().trim().equals("")?tableProperty.getParseValue().getBytes(dbconfig.getCode()):tableProperty.getParseValue().getBytes()),encode));
                }else
                {
                    statement.setString(propertyNum,
                                        tableProperty.getParseValue());
                }
            }
            statement.addBatch();
            propertyNum++ ;
        }

        statement.execute() ;
    }
    private String parseEncoding(String url)
    {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("characterEncoding[ ]*=[ ]*([\\w-_]*)") ;
       java.util.regex.Matcher matcher = pattern.matcher(url) ;
       String encode = null ;
       if(matcher.find())
       {
           encode = matcher.group(1) ;
       }
       return encode ;

    }
}
