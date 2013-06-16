package com.xx.platform.core.service;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author qh
 * @version 1.0
 */
public class PushObject {
    private String docType;
    private String title;
    private String[] field;
    private String[] value;
    private String url;

    public PushObject(String docType, String title, String[] field,String[] value,String url){
        this.docType = docType;
        this.title = title;
        this.field = field;
        this.value = value;
        this.url = url;
    }


    public String getDocType() {
        return docType;
    }

    public String[] getField() {
        return field;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String[] getValue() {
        return value;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public void setField(String[] field) {
        this.field = field;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setValue(String[] value) {
        this.value = value;
    }
}
