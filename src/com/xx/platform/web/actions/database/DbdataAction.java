package com.xx.platform.web.actions.database;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.Action;
import com.xx.platform.domain.model.database.*;
import com.xx.platform.web.actions.BaseAction;


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
public class DbdataAction extends BaseAction {
    private int num;
    private Dbtable dbtable;
    private Tableproperty tableproperty;
    private List dbtableList;
    private Set tablepropertyList;
    private List dbdataList;

    public String list() {
        dbtableList = service.findAllByIObjectCType(Dbtable.class);
        num = dbtableList != null ? dbtableList.size() : 0;
        return Action.SUCCESS;
    }

    public String listDbdata() throws SQLException, ClassNotFoundException {
        dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class, dbtable.getId());
        tablepropertyList = dbtable.getTableproperty();
        int sum = tablepropertyList != null ? tablepropertyList.size() : 0;
        if (sum > 0) {
           dbdataList=getResultSet(dbtable.getDbid(), dbtable, 10, sum);
        }
        return Action.SUCCESS;
    }

    public Dbtable getDbtable() {
        return dbtable;
    }

    public List getDbtableList() {
        return dbtableList;
    }

    public int getNum() {
        return num;
    }

    public Tableproperty getTableproperty() {
        return tableproperty;
    }

    public Set getTablepropertyList() {
        return tablepropertyList;
    }

    public List getDbdataList() {
        return dbdataList;
    }

    public void setDbtable(Dbtable dbtable) {
        this.dbtable = dbtable;
    }

    public void setDbtableList(List dbtableList) {
        this.dbtableList = dbtableList;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setTableproperty(Tableproperty tableproperty) {
        this.tableproperty = tableproperty;
    }

    public void setTablepropertyList(Set tablepropertyList) {
        this.tablepropertyList = tablepropertyList;
    }

    public void setDbdataList(List dbdataList) {
        this.dbdataList = dbdataList;
    }

    private List getResultSet(Dbconfig dbconfig, Dbtable table, int page,int propertysum) {
        List list = new ArrayList();
        ResultSet rs = null;
        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName(dbconfig.getDriverclazz());

            conn = DriverManager.getConnection(dbconfig.getDburl(),
                                               dbconfig.getDbuser(),
                                               dbconfig.getDbpass());

            stmt = conn.createStatement();
            StringBuffer strb = new StringBuffer() ;
            for(Tableproperty tableproperty : table.getTableproperty())
            {
                if(strb.length()>0)
                    strb.append(",");
                strb.append(tableproperty.getCode()) ;
            }
            rs = stmt.executeQuery("select "+strb.toString()+" from "+dbtable.getCode());

            if(rs!=null){
                int nowpage=0;
               while (rs.next()&&nowpage<page) {
                   List sublist = new ArrayList();
                   for(int i=1;i<=propertysum;i++){
                       sublist.add(rs.getObject(i));
                   }
                   list.add(sublist);
                   nowpage++;
                  }
            }


        } catch (SQLException ex1) {
              ex1.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex2) {

            }
        }

        return list;
    }
}
