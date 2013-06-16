package com.xx.platform.dao;

import java.lang.reflect.Method;

/**
 * 用于记录方法名称跟方法所需参数的对象
 * 暂时只支持一个参数的方法
 * @author 线点科技
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
				throw new Exception("参数不能为空！");
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
