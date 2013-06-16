package com.xx.platform.domain.service;

import com.xx.platform.dao.GeneraDAO;

import javax.persistence.Transient;

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
public class DomainLogic implements AbstractDomain{
    @Transient
    protected GeneraDAO dao ;
}
