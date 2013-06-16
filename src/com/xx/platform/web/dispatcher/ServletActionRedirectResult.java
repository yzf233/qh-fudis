package com.xx.platform.web.dispatcher;

import com.xx.platform.domain.model.database.Dbtable;

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
public class ServletActionRedirectResult extends org.apache.struts2.dispatcher.ServletActionRedirectResult{
    private String page ;
    private String id;
    private String sname ;
    private Dbtable dbtable = new Dbtable();
    private String message;
    private String indexmessage;
    public String getPage() {
        return page;
    }

    public Dbtable getDbtable() {
        return dbtable;
    }

    public String getId() {
        return id;
    }

  public String getSname() {
    return sname;
  }

    public String getMessage() {
        return message;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setDbtable(Dbtable dbtable) {
        this.dbtable = dbtable;
    }

    public void setId(String id) {
        this.id = id;
    }

  public void setSname(String sname) {
    this.sname = sname;
  }

    public void setMessage(String message) {
        this.message = message;
    }

	public String getIndexmessage() {
		return indexmessage;
	}

	public void setIndexmessage(String indexmessage) {
		this.indexmessage = indexmessage;
	}
}
