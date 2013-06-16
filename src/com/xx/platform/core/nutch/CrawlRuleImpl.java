package com.xx.platform.core.nutch;

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
public class CrawlRuleImpl implements CrawlRule{
  private String name ;
  private String code ;
  private String value ;
  private int dataType ;
  private boolean isStorge ;
    public String getCode() {
        return code;
    }

    public int getDataType() {
        return dataType;
    }


    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsStorge(boolean isStorge) {
        this.isStorge = isStorge;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isStorge() {
        return false;
    }
}
