package com.xx.platform.web.actions.crawl;

import java.util.*;

import com.opensymphony.xwork2.*;
import org.hibernate.criterion.*;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.*;
import com.xx.platform.domain.model.crawl.*;
import com.xx.platform.web.actions.*;

/**
 * <p>Title:分类管理 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: 北京线点科技有限公司</p>
 *
 * @author 杨庆
 * @version 1.0
 */
public class CategoryAction extends BaseAction {
    private List<Category> categoryList;
    private Category category;
    private Integer sum = 0;
    private String message ;
    public String list() throws Exception {
        categoryList = service.findAllByIObjectCType(Category.class);
        // System.out.println("*************"+categoryList.size());
        return Action.SUCCESS;

    }

    public String addCategory() throws Exception {
        if(category!=null)
        {
            if (service.findPageByCriteria(DetachedCriteria.forClass(
                    Category.class).add(Restrictions.or(Restrictions.eq(
                            "name", category.getName()),
                                              Restrictions.eq("code",
                    category.getCode())))).size() > 0) {
                message = "分类名称或者代码重复";
                return Action.INPUT;
            } else {
                service.saveIObject(category);
                SearchContext.reloadRules();
            }
        }

        return Action.SUCCESS;
    }

    public String editCategory() throws Exception {

        if (category != null && category.getId()!=null) {
            category = (Category)service.getIObjectByPK(Category.class,category.getId());
        }
        return Action.SUCCESS;
    }

    public String editCategoryDo() throws Exception {
        if(request.getParameter("type")!=null && request.getParameter("type").equals("1"))
        {
            if (category != null && category.getId() != null) {
                service.deleteIObject(category);
                SearchContext.reloadRules();
            }
        }else
        {
            if (category != null && category.getId() != null) {
                if (service.findPageByCriteria(DetachedCriteria.forClass(
                        Category.class).add(Restrictions.and(Restrictions.
                        not(Restrictions.eq("id",
                                            category.getId())),
                                                  Restrictions.or(Restrictions.
                        eq(
                                "name", category.getName()),
                        Restrictions.eq("code",
                                        category.getCode()))))).size() > 0) {
                    message = "分类名称或者代码重复";
                    return Action.INPUT;
                } else {
                    service.updateIObject(category);
                    SearchContext.reloadRules();
                }
            }
        }

        return Action.SUCCESS;
    }

    public Category getCategory() {
        return category;
    }

    public List getCategoryList() {
        return categoryList;
    }

    public Integer getSum() {
        if (categoryList != null) {
            sum = categoryList.size();
        }
        return sum;
    }

    public String getMessage() {
        return message;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCategoryList(List categoryList) {
        this.categoryList = categoryList;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
