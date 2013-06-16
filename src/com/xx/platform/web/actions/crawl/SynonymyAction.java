package com.xx.platform.web.actions.crawl;

import java.util.*;

import com.opensymphony.xwork2.*;
import com.xx.platform.core.*;
import com.xx.platform.domain.model.crawl.*;
import com.xx.platform.web.actions.*;

import org.hibernate.criterion.*;



/**
 * <p>Title: 同义词管理</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author qh 2007-07-25
 * @version 1.0
 */
public class SynonymyAction extends BaseAction{
    private List<Synonymy> synonymyList;
    private Synonymy synonymy;
    private Integer sum = 0;
    private int page = 1;

    public String list() throws Exception {
        synonymyList = service.findByIObjectCType(Synonymy.class,page, 100);
        return Action.SUCCESS;
    }

    public String add() throws Exception {
        if(synonymy!=null)
        {
            if (service.findPageByCriteria(DetachedCriteria.forClass(
                    Synonymy.class).add(Restrictions.eq(
                            "groups", synonymy.getGroups()))).size() > 0) {
                message = "该组名已经存在!";
                return Action.INPUT;
            } else {
                synonymy.setWords(synonymy.getWords()==null?"":synonymy.getWords().replaceAll("[ ，]",","));
                service.saveIObject(synonymy);
                if(synonymy.getWords()!=null && synonymy.getWords().length()>0)
                    SearchContext.addNewSynonymy(synonymy);
            }
        }
        return Action.SUCCESS;
    }

    public String edit() throws Exception {

            if (synonymy != null && synonymy.getId()!=null) {
                synonymy = (Synonymy)service.getIObjectByPK(Synonymy.class,synonymy.getId());
            }
            return Action.SUCCESS;
    }


    public String editDo() throws Exception {
            if(request.getParameter("type")!=null && request.getParameter("type").equals("1"))
            {
                if (synonymy != null && synonymy.getId() != null) {
                    service.deleteIObject(synonymy);
                    SearchContext.reloadSynonymyMap();
                }
            }else{
                if (synonymy != null && synonymy.getId() != null) {
                    if (service.findPageByCriteria(DetachedCriteria.forClass(
                            Synonymy.class).add(Restrictions.and(Restrictions.
                        not(Restrictions.eq("id",synonymy.getId())),Restrictions.eq(
                                    "groups", synonymy.getGroups())))).size() >0) {
                        message = "该组名已经存在!";
                        return Action.INPUT;

                    } else {
                        synonymy.setWords(synonymy.getWords()==null?"":synonymy.getWords().replaceAll("[ ，]",","));
                        service.updateIObject(synonymy);
                        SearchContext.reloadSynonymyMap();
                    }
                }
            }
            return Action.SUCCESS;
    }

    public Integer getSum() {
        if (synonymyList != null) {
            sum = synonymyList.size();
        }
        return sum;
    }

    public Synonymy getSynonymy() {
        return synonymy;
    }

    public List getSynonymyList() {
        return synonymyList;
    }

    public int getPage() {
        return page;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public void setSynonymy(Synonymy synonymy) {
        this.synonymy = synonymy;
    }

    public void setSynonymyList(List synonymyList) {
        this.synonymyList = synonymyList;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
