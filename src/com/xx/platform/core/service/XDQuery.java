package com.xx.platform.core.service;

import java.util.*;

/**
 * <p>Title: 查询接口</p>
 * <p>Description: Web Services 调用方式条件构造接口，可提供6中查询条件
 *                 1、逻辑与 字段名---关键词
 *                 2、逻辑与 关键词
 *                 3、逻辑或 字段名---关键词片段
 *                 4、逻辑非 字段名---关键词
 *                 5、逻辑非 关键词
 *                 6、逻辑非 字段名---关键词片段
 *
 * </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: 北京线点科技优先公司</p>
 * @author not jaddy0302
 */
public class XDQuery {
    /**
     * 查询条件存储数据结构
     */
    private Map<String ,XDClause> clauses = new HashMap() ;

    public XDQuery(){}
    /**
     * 功能说明：添加一个 逻辑与 条件
     * 使用说明：指定条件检索，传入检索字段和要求在检索字段上筛选的值
     * 注意：传入的keyword是未经分词处理的词条，如果创建索引字段的时候设置了该字段为分词（Token）
     *      则在检索的时候会有意想不到的搜索结果出现，并会导致搜索结果极不准确，所以，指定的检索字段
     *      最好是为经分词处理的字段。例如，数码产品的检索系统：在创建索引的时候对产品厂商（com）设置为不分词，
     *      则在检索的时候，可以指定的查询条件 以诺基亚为例：
     *      addRequired("com" , "诺基亚")
     *      表示查询所有厂商为 诺基亚 的 产品 ，多个条件查询 ，需要增加多次；例如 查询所有诺基亚亚洲版手机：
     *      addRequired("com" , "诺基亚");
     *      addRequired("area" , "亚洲")
     *
     * @param field String  字段名称
     * @param keyword String  字段值
     */
    public void addRequired(String field , String keyword)
    {
        XDClause xdClause = new XDClause(field,keyword , true , false , false , null);
        clauses.put(field , xdClause) ;
    }
    /**
     * 功能说明：添加一个 逻辑非 条件
     * 使用说明：指定条件检索，传入检索字段和要求在检索字段上排除的值
     * 注意：传入的keyword是未经分词处理的词条，如果创建索引字段的时候设置了该字段为分词（Token）
     *      则在检索的时候会有意想不到的搜索结果出现，并会导致搜索结果极不准确，所以，指定的检索字段
     *      最好是为经分词处理的字段。例如，数码产品的检索系统：在创建索引的时候对产品厂商（com）设置为不分词，
     *      则在检索的时候，可以指定的查询条件 以诺基亚为例：
     *      addProhibited("com" , "诺基亚")
     *      表示排除所有厂商为 诺基亚 的 产品 ，多个条件查询 ，需要增加多次；例如 查询所有非诺基亚和摩托罗拉手机：
     *      addRequired("com" , "诺基亚");
     *      addRequired("com" , "摩托罗拉")
     *
     * @param field String  字段名称
     * @param keyword String  字段值
     */
    public void addProhibited(String field , String keyword)
    {
        XDClause xdClause = new XDClause(field,keyword , false , true , false , null);
        clauses.put(field , xdClause) ;
    }
    /**
     * 功能说明：添加一个 默认 条件
     * 使用说明：不指定条件字段检索
     * 注意：传入的keyword是未经分词处理的词条，如果创建索引字段的时候设置了该字段为分词（Token）
     *      则在检索的时候会有意想不到的搜索结果出现，并会导致搜索结果极不准确，所以，指定的检索字段
     *      最好是为经分词处理的字段。例如，数码产品的检索系统：在创建索引的时候对产品厂商（com）设置为不分词，
     *      则在检索的时候，可以指定的查询条件 以诺基亚为例：
     *      addRequired("诺基亚")
     *      表示查找所有和 诺基亚 相关的数据 ，多个条件查询 ，需要增加多次；例如 查询所有和诺基亚、摩托罗拉相关的 信息：
     *      addRequired("诺基亚");
     *      addRequired("摩托罗拉")
     *
     * @param field String  字段名称
     * @param keyword String  字段值
     */
    public void addRequired(String keyword)
    {
        XDClause xdClause = new XDClause(keyword , true , false , false , null);
        clauses.put("DEFAULT",xdClause) ;
    }
    /**
     * 功能说明：添加一个 默认 的排除条件
     * 使用说明：不指定条件字段排除
     * 注意：传入的keyword是未经分词处理的词条，如果创建索引字段的时候设置了该字段为分词（Token）
     *      则在检索的时候会有意想不到的搜索结果出现，并会导致搜索结果极不准确，所以，指定的检索字段
     *      最好是为经分词处理的字段。例如，数码产品的检索系统：在创建索引的时候对产品厂商（com）设置为不分词，
     *      则在检索的时候，可以指定的查询条件 以诺基亚为例：
     *      addProhibited("诺基亚")
     *      表示排除所有和 诺基亚 相关的数据 ，多个条件查询 ，需要增加多次；例如 排除所有和诺基亚、摩托罗拉相关的 信息：
     *      addProhibited("诺基亚");
     *      addProhibited("摩托罗拉")
     *
     * @param field String  字段名称
     * @param keyword String  字段值
     */
    public void addProhibited(String keyword)
    {
        XDClause xdClause = new XDClause(keyword , false , true , false , null);
        clauses.put("DEFAULT",xdClause) ;
    }
    /**
     * 功能说明：添加一个 逻辑 条件
     * 使用说明：指定字段检索
     * 注意：传入的keyword的每一个片段是未经分词处理的独立的词条，如果创建索引字段的时候设置了该字段为分词（Token）
     *      则在检索的时候会有意想不到的搜索结果出现，并会导致搜索结果极不准确，所以，指定的检索字段
     *      最好是为经分词处理的字段。例如，数码产品的检索系统：在创建索引的时候对产品厂商（com）设置为不分词，
     *      则在检索的时候，可以指定的查询条件 以诺基亚为例：
     *      addRequiredPhrase("com",{"诺基亚","摩托罗拉"})
     *      表示查询所有com 字段为 诺基亚或摩托罗拉 数据 ，多个条件查询 ，需要增加多次；例如 查找所有和诺基亚、摩托罗拉在亚洲或欧洲销售的手机产品相关的 信息：
     *      addRequiredPhrase("com",{"诺基亚","摩托罗拉"})
     *      addRequiredPhrase("area",{"亚洲","欧洲"})
     *
     * @param field String  字段名称
     * @param keyword String  字段值
     */
    public void addRequiredPhrase(String field,String[] keyword)
    {
        XDClause xdClause = new XDClause(field,null , true , false , true , keyword);
        clauses.put(field,xdClause) ;
    }
    /**
     * 功能说明：添加一个 逻辑 条件
     * 使用说明：指定字段排除
     * 注意：传入的keyword的每一个片段是未经分词处理的独立的词条，如果创建索引字段的时候设置了该字段为分词（Token）
     *      则在检索的时候会有意想不到的搜索结果出现，并会导致搜索结果极不准确，所以，指定的检索字段
     *      最好是为经分词处理的字段。例如，数码产品的检索系统：在创建索引的时候对产品厂商（com）设置为不分词，
     *      则在检索的时候，可以指定的查询条件 以诺基亚为例：
     *      addProhibitedPhrase("com",{"诺基亚","摩托罗拉"})
     *      表示排除所有com 字段为 诺基亚或摩托罗拉 的数据 ，多个条件查询 ，需要增加多次；例如 排除所有和诺基亚、摩托罗拉在亚洲或欧洲销售的手机产品相关的 信息：
     *      addProhibitedPhrase("com",{"诺基亚","摩托罗拉"})
     *      addProhibitedPhrase("area",{"亚洲","欧洲"})
     *
     * @param field String  字段名称
     * @param keyword String  字段值
     */
    public void addProhibitedPhrase(String field , String[] keyword)
    {
        XDClause xdClause = new XDClause(field,null , false , true , true , keyword);
        clauses.put(field,xdClause) ;
    }

    public Map<String ,XDClause> getClauses() {
        return clauses;
    }

    public void setClauses(Map<String ,XDClause> clauses) {
        this.clauses = clauses;
    }

}
