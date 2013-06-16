package com.xx.platform.domain.model.search;

import javax.persistence.*;

import com.xx.platform.domain.service.*;

import org.hibernate.annotations.GenericGenerator;

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
@Entity
@Table(name = "searcherlog")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
    public class InnerSearchLog
    extends DomainLogic {
  private String id;
  private String vkeyword;
  public void setVkeyword(String keyword) {
    this.vkeyword = keyword;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getVkeyword() {
    return this.vkeyword;
  }

  @Id
  @Column(length = 32)
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")

  public String getId() {
    return id;
  }
}
