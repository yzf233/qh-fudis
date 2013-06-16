package com.xx.platform.domain.model.crawl;

import javax.persistence.*;

import com.xx.platform.domain.service.DomainLogic;

import org.hibernate.annotations.GenericGenerator;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.nutch.io.WritableUtils;

/**
 * <p>Title:��ȡ���� </p>
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
@Entity
@Table(name = "parserrule")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
    public class ParserRule
    extends DomainLogic implements org.apache.nutch.io.Writable {
  private String id;
  private String name; //������
  private String code; // �������
  private int number; //��������
  private String mode; //��ȡģʽ
  private String over; //�Ƿ����
  private String returntype; //�������
  private String parentid; //�������ID
  private String indexid; //�����ֶζ�Ӧ��ϵ
  private String value; //����
  private String tablepropertyid;

  @Transient
  public void write(DataOutput out) throws IOException {
    WritableUtils.writeCompressedString(out, id);
    WritableUtils.writeCompressedString(out, name);
    WritableUtils.writeCompressedString(out, code);
    out.writeInt(number);
    WritableUtils.writeCompressedString(out, mode);
    WritableUtils.writeCompressedString(out, over);
    WritableUtils.writeCompressedString(out, returntype);
    WritableUtils.writeCompressedString(out, parentid);
    WritableUtils.writeCompressedString(out, indexid);
    WritableUtils.writeCompressedString(out, value);
    WritableUtils.writeCompressedString(out, tablepropertyid);
  }

  @Transient
  public void readFields(DataInput in) throws IOException {
    id = WritableUtils.readCompressedString(in);
    name = WritableUtils.readCompressedString(in);
    code = WritableUtils.readCompressedString(in);
    number = in.readInt() ;
    mode = WritableUtils.readCompressedString(in);
    over = WritableUtils.readCompressedString(in);
    returntype = WritableUtils.readCompressedString(in);
    parentid = WritableUtils.readCompressedString(in);
    indexid = WritableUtils.readCompressedString(in);
    value = WritableUtils.readCompressedString(in);
    tablepropertyid = WritableUtils.readCompressedString(in);

  }

  public String getCode() {
    return code;
  }

  @Id
  @Column(length = 32)
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")

  public String getId() {
    return id;
  }

  @Column(name = "vvalue")
  public String getValue() {
    return value;
  }

  public String getReturntype() {
    return returntype;
  }

  public String getParentid() {
    return parentid;
  }

  @Column(name = "vover")
  public String getOver() {
    return over;
  }

  public int getNumber() {
    return number;
  }

  public String getName() {
    return name;
  }

  public String getMode() {
    return mode;
  }

  public String getIndexid() {
    return indexid;
  }

  public String getTablepropertyid() {
    return tablepropertyid;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public void setOver(String over) {
    this.over = over;
  }

  public void setParentid(String parentid) {
    this.parentid = parentid;
  }

  public void setReturntype(String returntype) {
    this.returntype = returntype;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setIndexid(String indexid) {
    this.indexid = indexid;
  }

  public void setTablepropertyid(String tablepropertyid) {
    this.tablepropertyid = tablepropertyid;
  }

}
