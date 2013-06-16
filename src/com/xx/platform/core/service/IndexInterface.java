package com.xx.platform.core.service;

import javax.jws.WebService;
import javax.jws.WebResult;

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
@WebService(targetNamespace = "http://www.xd-tech.com.cn")
public interface IndexInterface {

    /**
     * 删除索引一条内容
     * @param docId int
     * @return boolean
     * @throws Exception
     */
    @WebResult(name = "index")
    public boolean deleteOneIndexByID(int ino,int docId) throws Exception;
    /**
     *
     * @param field String
     * @param value String
     * @return boolean
     * @throws Exception
     */
    @WebResult(name = "index_field")
    public long deleteOneIndexByField(String field , String value) throws Exception;
}
