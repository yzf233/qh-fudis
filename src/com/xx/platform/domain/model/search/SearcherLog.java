package com.xx.platform.domain.model.search;

import java.util.*;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;

import com.xx.platform.domain.service.*;

/**
 * <p>Title:搜索日志</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: 北京线点科技有限公司</p>
 *
 * @author yq
 * @version 1.0
 */
@Entity
@Table(name = "searcherlog")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
public class SearcherLog extends DomainLogic {
    private String id;
    private String vkeyword;
    private String hostip ;
    private Date createdate;
    public Date getCreatedate() {
        return createdate;
    }
    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getId() {
        return id;
    }

    public String getVkeyword() {
        return vkeyword;
    }

  public String getHostip() {
    return hostip;
  }

  public void setVkeyword(String vkeyword) {
        this.vkeyword = vkeyword;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

  public void setHostip(String hostip) {
    this.hostip = hostip;
  }
}
