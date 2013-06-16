package com.xx.platform.util.tools.ipcheck;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class GetLocalIp {
	/**
	 * ±¾»úIP
	 */
	private static String localIp = null;

	public static String getIp() {
		if (localIp != null) {
			return localIp;
		}
		String ip = "127.0.0.1";
		Enumeration<NetworkInterface> eNwif;
		try {
			eNwif = NetworkInterface.getNetworkInterfaces();
			while (eNwif.hasMoreElements()) {
				NetworkInterface nwif = eNwif.nextElement();
				Enumeration<InetAddress> eInetAddr = nwif.getInetAddresses();
				while (eInetAddr.hasMoreElements()) {
					InetAddress ia = eInetAddr.nextElement();
					if (ia.isSiteLocalAddress() && !ia.isLoopbackAddress()
							&& ia.getHostAddress().indexOf(":") == -1) {
						ip = ia.getHostAddress();
						break;
					}
				}
				if (!ip.endsWith("127.0.0.1")) {
					break;
				}
			}
		} catch (SocketException e) {
		}
		localIp = ip;
		return ip;
	}

	public static void main(String[] args) {
		System.out.println(getIp());

	}
}
