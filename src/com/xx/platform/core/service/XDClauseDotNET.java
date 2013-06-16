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
public class XDClauseDotNET implements java.io.Serializable{
    private String required;
    private String prohibited;
    private String iphrase ;
    private String range = "0" ;
    private String includeRange = "1" ;
    private String beginRange ;
    private String endRange ;
    private String field = "DEFAULT";
    private String keyword;
    private String[] phrase;
    public XDClauseDotNET(){}
    public XDClauseDotNET(String field,String keyword,
                    String isRequired, String isProhibited, String isPhrase,
                    String[] phrase) {
        this(keyword, isRequired, isProhibited, isPhrase, phrase);
        this.field = field;
    }

    public XDClauseDotNET(String field,String isRange , String begin ,String end , String includeRange,
                    String isRequired, String isProhibited) {
        this("", isRequired, isProhibited, "0", null);
        this.field = field;
        this.range = isRange ;
        this.beginRange = begin ;
        this.endRange = end ;
        this.includeRange = includeRange ;
    }
    public XDClauseDotNET(String keyword, String isRequired, String isProhibited,
                    String isPhrase, String[] phrase) {
        this.keyword = keyword;
        this.required = isRequired;
        this.prohibited = isProhibited;
        this.iphrase = isPhrase ;
        this.phrase = phrase;
    }

    public XDClause toXDClause()
    {
      XDClause xdClause = new XDClause() ;
      xdClause.setField(this.field);
      xdClause.setKeyword(this.keyword);
      xdClause.setBeginRange(this.getBeginRange());
      xdClause.setEndRange(this.getEndRange());
      xdClause.setIncludeRange(this.includeRange!=null&&this.includeRange.equals("1"));
      xdClause.setBphrase(this.iphrase!=null&&this.iphrase.equals("1"));
      xdClause.setRequired(this.required!=null&&this.required.equals("1"));
      xdClause.setProhibited(this.prohibited!=null&&this.prohibited.equals("1"));
      xdClause.setRange(this.range!=null&&this.range.equals("1"));
      xdClause.setPhrase(this.phrase);
//      {
//        System.out.println("this.field:" + this.field);
//        System.out.println("this.keyword:"+this.keyword);
//        System.out.println("this.getBeginRange():"+this.getBeginRange());
//        System.out.println("this.getEndRange():"+this.getEndRange());
//        System.out.println("this.isIncludeRange:"+this.includeRange);
//        System.out.println("this.isPhrase:"+this.iphrase);
//        System.out.println("this.isRequired:"+this.required);
//        System.out.println("this.isProhibited:"+this.prohibited);
//        System.out.println("this.isRange:"+this.range);
//        System.out.println("this.phrase:"+this.phrase);
//      }
      return xdClause ;
    }

  public String getBeginRange() {
    return beginRange;
  }

  public String getEndRange() {
    return endRange;
  }

  public String getField() {
    return field;
  }

  public String getIncludeRange() {
    return includeRange;
  }

  public String getIphrase() {
    return iphrase;
  }

  public String getKeyword() {
    return keyword;
  }

  public String[] getPhrase() {
    return phrase;
  }

  public String getProhibited() {
    return prohibited;
  }

  public String getRange() {
    return range;
  }

  public String getRequired() {
    return required;
  }

  public void setEndRange(String endRange) {
    this.endRange = endRange;
  }

  public void setBeginRange(String beginRange) {
    this.beginRange = beginRange;
  }

  public void setRequired(String required) {
    this.required = required;
  }

  public void setRange(String range) {
    this.range = range;
  }

  public void setProhibited(String prohibited) {
    this.prohibited = prohibited;
  }

  public void setPhrase(String[] phrase) {
    this.phrase = phrase;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public void setIphrase(String iphrase) {
    this.iphrase = iphrase;
  }

  public void setIncludeRange(String includeRange) {
    this.includeRange = includeRange;
  }

  public void setField(String field) {
    this.field = field;
  }
}
