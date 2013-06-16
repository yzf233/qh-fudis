package com.xx.platform.web.actions;

import javax.servlet.http.*;
import org.springframework.web.context.WebApplicationContext;

import com.xx.platform.dao.GeneraDAO;

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
public interface ServletHandler {
    /**
     * Request
     * @param request HttpServletRequest
     * @throws Exception
     */
    public void setServletRequest(HttpServletRequest request) throws Exception;
    /**
     * Response
     * @param response HttpServletResponse
     * @throws Exception
     */
    public void setServletResponse(HttpServletResponse response) throws Exception;

    /**
     * WebApplicationContext
     * @param wac WebApplicationContext
     * @throws Exception
     */
    public void setWebApplicationContext(WebApplicationContext wac) throws Exception ;

    /**
     *
     * @param dao GeneraDAO
     * @throws Exception
     */
    public void setGeneraDAO(GeneraDAO dao) throws Exception ;
    /**
     * ∑√Œ ’ﬂIP
     * @param userIP
     */
    public void setUserIP(String userIP);
}
