package com.xx.platform.util.tools.ipcheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.transport.http.XFireServletController;

import com.xx.platform.core.SearchContext;
import com.xx.platform.dao.GeneraDAO;
import com.xx.platform.domain.model.system.Xdtechsite;
import com.xx.platform.domain.model.user.AllowList;
import com.xx.platform.domain.model.user.ForbidList;
import com.xx.platform.domain.model.user.IPCheckInteface;

/**
 * 黑白名单操作IP的帮助类
 * 
 * @author 线点科技
 */
public class CheckIPUtil {
	public final static String CHECK_IP_ERROR = "checkiperror";
	private static boolean isCache = false;// 是否执行过缓存操作
	private static Set<IpRange> ipCollections=Collections.synchronizedSet(new TreeSet<IpRange>());//存储ipduan

	/**
	 * 根据HTTP请求获得访问者IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getIPAddressForRequest(HttpServletRequest request) {
		if(request==null){
			return "127.0.0.1";
		}
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 根据webservice请求获得访问者IP
	 * 
	 * @param client
	 * @return
	 */
	private static String getIPAddressForWebService() {
		String ip = null;
		HttpServletRequest request = XFireServletController.getRequest();
		ip = getIPAddressForRequest(request);
		return ip;
	}

	/**
	 * 初始化
	 */
	public static void init() {
		ipCollections.clear();
		isCache = false;
	}

	public static boolean checkIpFacadeWebService(){
		String ip=getIPAddressForWebService();
		boolean isOk=checkIP(ip);
		return isOk;
	}
	public static boolean checkIpFacadeHttp(HttpServletRequest request){
		String ip=getIPAddressForRequest(request);
		if(ip!=null&&"127.0.0.1".equals(ip)){
			return true;
		}
		boolean isOk=checkIP(ip);
		return isOk;
	}
	@SuppressWarnings("finally")
	private static boolean checkIP(String ip) {
		String[] ips = ip.split("\\.");
		if (ip != null) {
			StringBuilder sbIp = new StringBuilder();
			for (String ipseg : ips) {
				if (ipseg.length() == 1) {
					sbIp.append("00").append(ipseg);
				} else if (ipseg.length() == 2) {
					sbIp.append("0").append(ipseg);
				} else if (ipseg.length() == 3) {
					sbIp.append(ipseg);
				}
			}
			return checkIpSub(sbIp.toString());
		}else{
			try {
				throw new Exception("无法获取IP地址!");
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				return false;
			}
		}
	}

	private static boolean checkIpSub(String ip) {
		Xdtechsite site = SearchContext.getXdtechsite();
		// 判断是否开启IP检查
		if (site.getViewermanager() == 1) {
			// 判断黑名单还是白名单
			if ("BLACK".equals(site.getVieweroption())) {
				cacheIP(ForbidList.class);
				if(ipCollections!=null&&!ipCollections.isEmpty()){
					boolean isOk=true;
					for(IpRange ipRange:ipCollections){
						if(ip.compareTo(ipRange.getStart())>=0&&ip.compareToIgnoreCase(ipRange.getEnd())<=0){
							isOk=false;
						}
					}
					return isOk;
				}else{
					return true;
				}
			} else {
				cacheIP(AllowList.class);
				if(ipCollections!=null&&!ipCollections.isEmpty()){
					boolean isOk=false;
					for(IpRange ipRange:ipCollections){
						if(ip.compareTo(ipRange.getStart())>=0&&ip.compareTo(ipRange.getEnd())<=0){
							isOk=true;
						}
					}
					return isOk;
				}else{
					return true;
				}	
			}
		} else {
			return true;
		}
	}
	private static void cacheIP(Class clazz) {
	if (!isCache) {
		GeneraDAO dao = SearchContext.getDao();
		List<IPCheckInteface> IPList =dao.findAllByIObjectCType(clazz);
		List<IpRange> tempList=new ArrayList<IpRange>();
		for(IPCheckInteface ip:IPList){
			IpRange iprange=new IpRange(ip.getStartip(),ip.getEndip());
			tempList.add(iprange);
		}
		ipCollections.clear();
		ipCollections.addAll(tempList);
		isCache = true;
	}
}
}
