package com.xx.platform.core.service;

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
import java.io.UnsupportedEncodingException;
import java.util.*;

import org.codehaus.xfire.client.*;
import org.codehaus.xfire.service.*;
import org.codehaus.xfire.service.binding.*;
import org.mortbay.util.UrlEncoded;

/**
 * ��ѯ�����WebService JSR181��ͻ���.
 *
 * @author calvin
 */
public class PushDataJSR181Client {
    private String serviceURL;
    private IndexPushInterface service;
    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public IndexPushInterface getClient() throws Exception {
        if (service == null) {
            Service serviceModel = new ObjectServiceFactory().
                                   create(IndexPushInterface.class);
            service = (IndexPushInterface)new XFireProxyFactory().create(serviceModel, serviceURL);
        }
        return service;
    }
    public int updateByField(String delField,String delValue,String docType,String title, String[] field, String[] value,String url) throws Exception
    {	
    	return getClient().updateByField(delField, delValue, docType,  field, value);
    }
    public int pushData() throws Exception {
        return getClient().push("test2", new String[]{"timestamp"}, new String[][]{{"����3313"}} );
    }

    public void merger() throws Exception {
      getClient().merger(); //���������÷�ʽ
    }

    public static void main(String[] args)
    {
        PushDataJSR181Client client = new PushDataJSR181Client();
        client.setServiceURL("http://192.168.1.11:901/service/pushData");
        try {
        System.out.println(client.getClient().push("test", new String[]{"id","thecontent","dis"}, new String[][]{{"998877","ȫ�����������1","����1"},{"445566","java��Դ��Ʒmysql","û������1"}}));
        client.getClient().merger();
        // client.updateByField();
         //client.pushData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
}


