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
import java.util.*;

import org.codehaus.xfire.client.*;
import org.codehaus.xfire.service.*;
import org.codehaus.xfire.service.binding.*;

/**
 * ��ѯ�����WebService JSR181��ͻ���.
 *
 * @author calvin
 */
public class IndexInterfaceJSR181Client {
    private String serviceURL;
    private IndexInterface service;
    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public IndexInterface getClient() throws Exception {
        if (service == null) {
            Service serviceModel = new ObjectServiceFactory().
                                   create(IndexInterface.class);
            service = (IndexInterface)new XFireProxyFactory().create(serviceModel, serviceURL);
        }
        return service;
    }
    public boolean deleteOneIndex(int ino,int docId) throws Exception {
    	return getClient().deleteOneIndexByID(ino, docId); //����һ���÷�ʽ
    }
    public long deleteMoreIndex(String field,String value) throws Exception {
        return getClient().deleteOneIndexByField(field,value); //���������÷�ʽ
    }
    
    public static void main(String[] args){
    	IndexInterfaceJSR181Client client1 = new IndexInterfaceJSR181Client();
    	client1.setServiceURL("http://localhost:901/service/index");
    	try {
			System.out.println(client1.getClient().deleteOneIndexByField("docNo", "0z0"));
		} catch (Exception e) {
			e.printStackTrace();
		}
//    	SearchServiceJSR181Client client = new SearchServiceJSR181Client();
//		client.setServiceURL("http://127.0.0.1:901/service/search");
//		try {
//			List<WebServiceSearchResult> resultList = client.search_11("һ");
//			System.out.println("size():" + resultList.size());
//			for (WebServiceSearchResult searchResult : resultList) {
//				System.out.println(searchResult.getDocNo()+":"+searchResult.getInx()+":"+searchResult.getSummaries());
//				System.out.println(client1.deleteOneIndex(searchResult.getInx(), (int)searchResult.getDocNo())?"ɾ���ɹ�":"ɾ��ʧ��");
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}	
    }
}


