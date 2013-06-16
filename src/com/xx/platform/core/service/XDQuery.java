package com.xx.platform.core.service;

import java.util.*;

/**
 * <p>Title: ��ѯ�ӿ�</p>
 * <p>Description: Web Services ���÷�ʽ��������ӿڣ����ṩ6�в�ѯ����
 *                 1���߼��� �ֶ���---�ؼ���
 *                 2���߼��� �ؼ���
 *                 3���߼��� �ֶ���---�ؼ���Ƭ��
 *                 4���߼��� �ֶ���---�ؼ���
 *                 5���߼��� �ؼ���
 *                 6���߼��� �ֶ���---�ؼ���Ƭ��
 *
 * </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: �����ߵ�Ƽ����ȹ�˾</p>
 * @author not jaddy0302
 */
public class XDQuery {
    /**
     * ��ѯ�����洢���ݽṹ
     */
    private Map<String ,XDClause> clauses = new HashMap() ;

    public XDQuery(){}
    /**
     * ����˵�������һ�� �߼��� ����
     * ʹ��˵����ָ��������������������ֶκ�Ҫ���ڼ����ֶ���ɸѡ��ֵ
     * ע�⣺�����keyword��δ���ִʴ���Ĵ�����������������ֶε�ʱ�������˸��ֶ�Ϊ�ִʣ�Token��
     *      ���ڼ�����ʱ��������벻��������������֣����ᵼ�������������׼ȷ�����ԣ�ָ���ļ����ֶ�
     *      �����Ϊ���ִʴ�����ֶΡ����磬�����Ʒ�ļ���ϵͳ���ڴ���������ʱ��Բ�Ʒ���̣�com������Ϊ���ִʣ�
     *      ���ڼ�����ʱ�򣬿���ָ���Ĳ�ѯ���� ��ŵ����Ϊ����
     *      addRequired("com" , "ŵ����")
     *      ��ʾ��ѯ���г���Ϊ ŵ���� �� ��Ʒ �����������ѯ ����Ҫ���Ӷ�Σ����� ��ѯ����ŵ�������ް��ֻ���
     *      addRequired("com" , "ŵ����");
     *      addRequired("area" , "����")
     *
     * @param field String  �ֶ�����
     * @param keyword String  �ֶ�ֵ
     */
    public void addRequired(String field , String keyword)
    {
        XDClause xdClause = new XDClause(field,keyword , true , false , false , null);
        clauses.put(field , xdClause) ;
    }
    /**
     * ����˵�������һ�� �߼��� ����
     * ʹ��˵����ָ��������������������ֶκ�Ҫ���ڼ����ֶ����ų���ֵ
     * ע�⣺�����keyword��δ���ִʴ���Ĵ�����������������ֶε�ʱ�������˸��ֶ�Ϊ�ִʣ�Token��
     *      ���ڼ�����ʱ��������벻��������������֣����ᵼ�������������׼ȷ�����ԣ�ָ���ļ����ֶ�
     *      �����Ϊ���ִʴ�����ֶΡ����磬�����Ʒ�ļ���ϵͳ���ڴ���������ʱ��Բ�Ʒ���̣�com������Ϊ���ִʣ�
     *      ���ڼ�����ʱ�򣬿���ָ���Ĳ�ѯ���� ��ŵ����Ϊ����
     *      addProhibited("com" , "ŵ����")
     *      ��ʾ�ų����г���Ϊ ŵ���� �� ��Ʒ �����������ѯ ����Ҫ���Ӷ�Σ����� ��ѯ���з�ŵ���Ǻ�Ħ�������ֻ���
     *      addRequired("com" , "ŵ����");
     *      addRequired("com" , "Ħ������")
     *
     * @param field String  �ֶ�����
     * @param keyword String  �ֶ�ֵ
     */
    public void addProhibited(String field , String keyword)
    {
        XDClause xdClause = new XDClause(field,keyword , false , true , false , null);
        clauses.put(field , xdClause) ;
    }
    /**
     * ����˵�������һ�� Ĭ�� ����
     * ʹ��˵������ָ�������ֶμ���
     * ע�⣺�����keyword��δ���ִʴ���Ĵ�����������������ֶε�ʱ�������˸��ֶ�Ϊ�ִʣ�Token��
     *      ���ڼ�����ʱ��������벻��������������֣����ᵼ�������������׼ȷ�����ԣ�ָ���ļ����ֶ�
     *      �����Ϊ���ִʴ�����ֶΡ����磬�����Ʒ�ļ���ϵͳ���ڴ���������ʱ��Բ�Ʒ���̣�com������Ϊ���ִʣ�
     *      ���ڼ�����ʱ�򣬿���ָ���Ĳ�ѯ���� ��ŵ����Ϊ����
     *      addRequired("ŵ����")
     *      ��ʾ�������к� ŵ���� ��ص����� �����������ѯ ����Ҫ���Ӷ�Σ����� ��ѯ���к�ŵ���ǡ�Ħ��������ص� ��Ϣ��
     *      addRequired("ŵ����");
     *      addRequired("Ħ������")
     *
     * @param field String  �ֶ�����
     * @param keyword String  �ֶ�ֵ
     */
    public void addRequired(String keyword)
    {
        XDClause xdClause = new XDClause(keyword , true , false , false , null);
        clauses.put("DEFAULT",xdClause) ;
    }
    /**
     * ����˵�������һ�� Ĭ�� ���ų�����
     * ʹ��˵������ָ�������ֶ��ų�
     * ע�⣺�����keyword��δ���ִʴ���Ĵ�����������������ֶε�ʱ�������˸��ֶ�Ϊ�ִʣ�Token��
     *      ���ڼ�����ʱ��������벻��������������֣����ᵼ�������������׼ȷ�����ԣ�ָ���ļ����ֶ�
     *      �����Ϊ���ִʴ�����ֶΡ����磬�����Ʒ�ļ���ϵͳ���ڴ���������ʱ��Բ�Ʒ���̣�com������Ϊ���ִʣ�
     *      ���ڼ�����ʱ�򣬿���ָ���Ĳ�ѯ���� ��ŵ����Ϊ����
     *      addProhibited("ŵ����")
     *      ��ʾ�ų����к� ŵ���� ��ص����� �����������ѯ ����Ҫ���Ӷ�Σ����� �ų����к�ŵ���ǡ�Ħ��������ص� ��Ϣ��
     *      addProhibited("ŵ����");
     *      addProhibited("Ħ������")
     *
     * @param field String  �ֶ�����
     * @param keyword String  �ֶ�ֵ
     */
    public void addProhibited(String keyword)
    {
        XDClause xdClause = new XDClause(keyword , false , true , false , null);
        clauses.put("DEFAULT",xdClause) ;
    }
    /**
     * ����˵�������һ�� �߼� ����
     * ʹ��˵����ָ���ֶμ���
     * ע�⣺�����keyword��ÿһ��Ƭ����δ���ִʴ���Ķ����Ĵ�����������������ֶε�ʱ�������˸��ֶ�Ϊ�ִʣ�Token��
     *      ���ڼ�����ʱ��������벻��������������֣����ᵼ�������������׼ȷ�����ԣ�ָ���ļ����ֶ�
     *      �����Ϊ���ִʴ�����ֶΡ����磬�����Ʒ�ļ���ϵͳ���ڴ���������ʱ��Բ�Ʒ���̣�com������Ϊ���ִʣ�
     *      ���ڼ�����ʱ�򣬿���ָ���Ĳ�ѯ���� ��ŵ����Ϊ����
     *      addRequiredPhrase("com",{"ŵ����","Ħ������"})
     *      ��ʾ��ѯ����com �ֶ�Ϊ ŵ���ǻ�Ħ������ ���� �����������ѯ ����Ҫ���Ӷ�Σ����� �������к�ŵ���ǡ�Ħ�����������޻�ŷ�����۵��ֻ���Ʒ��ص� ��Ϣ��
     *      addRequiredPhrase("com",{"ŵ����","Ħ������"})
     *      addRequiredPhrase("area",{"����","ŷ��"})
     *
     * @param field String  �ֶ�����
     * @param keyword String  �ֶ�ֵ
     */
    public void addRequiredPhrase(String field,String[] keyword)
    {
        XDClause xdClause = new XDClause(field,null , true , false , true , keyword);
        clauses.put(field,xdClause) ;
    }
    /**
     * ����˵�������һ�� �߼� ����
     * ʹ��˵����ָ���ֶ��ų�
     * ע�⣺�����keyword��ÿһ��Ƭ����δ���ִʴ���Ķ����Ĵ�����������������ֶε�ʱ�������˸��ֶ�Ϊ�ִʣ�Token��
     *      ���ڼ�����ʱ��������벻��������������֣����ᵼ�������������׼ȷ�����ԣ�ָ���ļ����ֶ�
     *      �����Ϊ���ִʴ�����ֶΡ����磬�����Ʒ�ļ���ϵͳ���ڴ���������ʱ��Բ�Ʒ���̣�com������Ϊ���ִʣ�
     *      ���ڼ�����ʱ�򣬿���ָ���Ĳ�ѯ���� ��ŵ����Ϊ����
     *      addProhibitedPhrase("com",{"ŵ����","Ħ������"})
     *      ��ʾ�ų�����com �ֶ�Ϊ ŵ���ǻ�Ħ������ ������ �����������ѯ ����Ҫ���Ӷ�Σ����� �ų����к�ŵ���ǡ�Ħ�����������޻�ŷ�����۵��ֻ���Ʒ��ص� ��Ϣ��
     *      addProhibitedPhrase("com",{"ŵ����","Ħ������"})
     *      addProhibitedPhrase("area",{"����","ŷ��"})
     *
     * @param field String  �ֶ�����
     * @param keyword String  �ֶ�ֵ
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
