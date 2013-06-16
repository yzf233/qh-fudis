package com.xx.platform.util.tools.ipcheck;

public class IpRange implements Comparable<IpRange>{
	private String start;
	private String end;
	public IpRange(String start,String end){
		setStart(start);
		setEnd(end);
	}
	public String getStart() {
		return start;
	}
	private void setStart(String start) {
		String[] ips=start.split("\\.");
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
		this.start = sbIp.toString();
	}
	public String getEnd() {
		return end;
	}
	private void setEnd(String end) {
		String[] ips=end.split("\\.");
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
		this.end = sbIp.toString();
	}
	public int compareTo(IpRange ipRange) {
		if(!this.start.equals(ipRange.getStart())||!this.end.equals(ipRange.getEnd())){
			if(!this.start.equals(ipRange)){
				return this.start.compareTo(ipRange.getStart());
			}else{
				return this.end.compareTo(ipRange.getEnd());
			}
		}else{
			return 0;
		}
	}
	public String toString(){
		StringBuilder sbToString=new StringBuilder();
		sbToString.append("startIp:"+this.start).append("    endIp:").append(this.end);
		return sbToString.toString();
	}
}
