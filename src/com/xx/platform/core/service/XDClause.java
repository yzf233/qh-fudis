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
public class XDClause implements java.io.Serializable{
    private boolean required;
    private boolean prohibited;
    private boolean bphrase ;
    private boolean range = false ;
    private boolean includeRange = true ;
    private String beginRange ;
    private String endRange ;
    private String field = "DEFAULT";
    private String keyword;
    private String[] phrase;
    public XDClause(){}
    public XDClause(String field,String keyword,
                    boolean isRequired, boolean isProhibited, boolean isPhrase,
                    String[] phrase) {
        this(keyword, isRequired, isProhibited, isPhrase, phrase);
        this.field = field;
    }

    public XDClause(String field,boolean isRange , String begin ,String end , boolean includeRange,
                    boolean isRequired, boolean isProhibited) {
        this("", isRequired, isProhibited, false, null);
        this.field = field;
        this.range = isRange ;
        this.beginRange = begin ;
        this.endRange = end ;
        this.includeRange = includeRange ;
    }
    public XDClause(String keyword, boolean isRequired, boolean isProhibited,
                    boolean isPhrase, String[] phrase) {
        this.keyword = keyword;
        this.required = isRequired;
        this.prohibited = isProhibited;
        this.bphrase = isPhrase ;
        this.phrase = phrase;
    }

    public String getField() {
        return field;
    }



    public String getKeyword() {
        return keyword;
    }

    public String[] getPhrase() {
        return phrase;
    }

    public String getBeginRange() {
        return beginRange;
    }

    public String getEndRange() {
        return endRange;
    }

  public boolean isBphrase() {
    return bphrase;
  }

  public boolean isIncludeRange() {
    return includeRange;
  }

  public boolean isProhibited() {
    return prohibited;
  }

  public boolean isRange() {
    return range;
  }

  public boolean isRequired() {
    return required;
  }

  public void setField(String field) {
        this.field = field;
    }


    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setPhrase(String[] phrase) {
        this.phrase = phrase;
    }

    public void setBeginRange(String beginRange) {
        this.beginRange = beginRange;
    }

    public void setEndRange(String endRange) {
        this.endRange = endRange;
    }

  public void setBphrase(boolean bphrase) {
    this.bphrase = bphrase;
  }

  public void setIncludeRange(boolean includeRange) {
    this.includeRange = includeRange;
  }

  public void setProhibited(boolean prohibited) {
    this.prohibited = prohibited;
  }

  public void setRange(boolean range) {
    this.range = range;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }
  public String toString()
  {
    StringBuffer strb = new StringBuffer() ;
    strb.append("required=").append(required);
    strb.append("\nprohibited=").append(prohibited);
    strb.append("\nbphrase =").append(bphrase);
    strb.append("\nrange =").append(range);
    strb.append("\nincludeRange=").append(includeRange);
    strb.append("\nbeginRange =").append(beginRange);
    strb.append("\nendRange =").append(endRange);
    strb.append("\nfield=").append(field);
    strb.append("\nkeyword=").append(keyword);
    strb.append("\nphrase=").append(phrase);

    return strb.toString();
  }
}
