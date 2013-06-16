package com.xx.platform.web.actions.search;

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
import java.io.File;
import java.util.List;

import org.apache.lucene.search.Hits;
import org.apache.nutch.searcher.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SearchResult {
    private Hit hit;
    private HitDetails details;
    private String summaries;
    private String title ;
    private String url ;
    private long length ;
    private String time ;
    private long docNo;
    private org.apache.lucene.search.Hits lunHits;
    private String sourcef;
    private float score;
    private String percentage;
    private String content;
    private List ustr;
    
    public static String getFileName(String str)
    {
        try
        {
    	int i=0;
    	i=str.lastIndexOf("/");
    	if(i==-1)
    	i=str.lastIndexOf(File.separator);
    	
    	str=str.substring(i+1,str.length());
        }
        catch(Exception e)
        {
        	
        }

    	return str;
    }

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public HitDetails getDetails() {
        return details;
    }

    public Hit getHit() {
        return hit;
    }

    public Hits getLunHits() {
        return lunHits;
    }

    public String getSummaries() {
        return summaries;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getTime() {
        return time;
    }

    public long getLength() {
        return length;
    }

    public String getSourcef() {
        return sourcef;
    }

    public void setSummaries(String summaries) {
        this.summaries = summaries;
    }

    public void setLunHits(Hits lunHits) {
        this.lunHits = lunHits;
    }

    public void setHit(Hit hit) {
        this.hit = hit;
    }

    public void setDetails(HitDetails details) {
        this.details = details;
    }

    public void setTitle(String title) {
            this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setSourcef(String sourcef) {
        this.sourcef = sourcef;
    }

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public List getUstr() {
		return ustr;
	}

	public void setUstr(List ustr) {
		this.ustr = ustr;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getDocNo() {
		return docNo;
	}

	public void setDocNo(long docNo) {
		this.docNo = docNo;
	}


}
