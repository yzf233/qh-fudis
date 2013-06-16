package com.xx.platform.dao;

import java.io.*;

import com.xx.platform.core.nutch.*;

import org.hibernate.criterion.*;
import org.apache.nutch.db.Page;
import java.net.*;

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
public class URLDao<T, P, PK extends Serializable> implements IBase<T, P, PK>{
    private GeneraDAO ibase ;
    private static WebDBWriter webDbWriter = new WebDBWriter() ;
    public URLDao(GeneraDAO ibase){
        this.ibase = ibase ;
    }
    public void saveIObject(T t) {
        /**
         *  «∑Ò÷ÿ∏¥
         */
        try {
          webDbWriter.addWebDB((WebDB)t) ;
        } catch (Exception ex) {
          ex.printStackTrace();
        }
    }
}
