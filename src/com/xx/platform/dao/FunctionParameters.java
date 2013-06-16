package com.xx.platform.dao;

import java.lang.reflect.Method;

/**
 * ���ڼ�¼�������Ƹ�������������Ķ���
 * ��ʱֻ֧��һ�������ķ���
 * @author �ߵ�Ƽ�
 *
 */
public class FunctionParameters {
	private String functionName;
	private Class<?> parameter;
	public Class<?> getClazz(){
		return parameter;
	}
	public FunctionParameters(String functionName,Class<?> clazz){
		if(functionName==null||clazz==null){
			try {
				throw new Exception("��������Ϊ�գ�");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.functionName=functionName;
		this.parameter=clazz;
	}
	public String toString(){
		return functionName+"("+parameter+")";
	}
	public Method getMethod(Class<?> clazz) throws SecurityException, NoSuchMethodException{
		return clazz.getMethod(functionName, parameter);
	}
}
