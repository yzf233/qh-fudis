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
public class WebServiceSearchResult {
    private String summaries;
    private String title ;
    private String url ;
    private long length ;
    private XDIndexValue[] indexMap ;
    private String time ;
    private long docNo ;
    private int inx ;
    private long total ;
    private String createDate;
    private String updataDate;
    private String site;
    private String type;
    private String subType;
    private String docType;
    private String dataSource;
    private String docSource;

    
    public String getValue(String field)
    {
    	for(XDIndexValue v:indexMap)
    	{
    		if(v!=null&&v.getField()!=null&&v.getField().equals(field))
    		return v.getValue();
    	}
    	return null;
    }
    public long getLength() {
        return length;
    }

    public String getSummaries() {
        return summaries;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public XDIndexValue[] getIndexMap() {
        return indexMap;
    }

    public long getTotal() {
        return total;
    }


    public int getInx() {
        return inx;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSummaries(String summaries) {
        this.summaries = summaries;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setIndexMap(XDIndexValue[] indexMap) {
        this.indexMap = indexMap;
    }

    public void setTotal(long total) {
        this.total = total;
    }


	public long getDocNo() {
		return docNo;
	}
	public void setDocNo(long docNo) {
		this.docNo = docNo;
	}
	public void setInx(int inx) {
        this.inx = inx;
    }
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getDocSource() {
		return docSource;
	}
	public void setDocSource(String docSource) {
		this.docSource = docSource;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUpdataDate() {
		return updataDate;
	}
	public void setUpdataDate(String updataDate) {
		this.updataDate = updataDate;
	}


}
