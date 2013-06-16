package com.xx.platform.core;

public class Control {
    private boolean disLicense = false ;
    private boolean cluster=false;
    private int disNumer=0;
    private int clusterNumer=0;
    private boolean together=false;
    private int maxKnot=1;
    private int kind=1;
    /**
     * 是否获得分布式部署授权许可
     * @return boolean
     */
	public boolean isDisLicense() {
		return disLicense;
	}
	public void setDisLicense(boolean disLicense) {
		this.disLicense = disLicense;
	}
	/**
	 * 是否支持集群
	 * @return boolean
	 */
	public boolean isCluster() {
		return cluster;
	}
	public void setCluster(boolean cluster) {
		this.cluster = cluster;
	}
	/**
	 * 授权分布式节点数目
	 * @return int
	 */
	public int getDisNumer() {
		return disNumer;
	}
	public void setDisNumer(int disNumer) {
		this.disNumer = disNumer;
	}
	/**
	 * 授权集群数目
	 * @return int
	 */
	public int getClusterNumer() {
		return clusterNumer;
	}
	public void setClusterNumer(int clusterNumer) {
		this.clusterNumer = clusterNumer;
	}
	/**
	 * 调度能否和节点放在同一台机器上
	 * @return boolean
	 */
	public boolean isTogether() {
		return together;
	}
	public void setTogether(boolean together) {
		this.together = together;
	}
	/**
	 * 同一台机器上最多能够运行多少个结点
	 * @return int
	 */
	public int getMaxKnot() {
		return maxKnot;
	}
	public void setMaxKnot(int maxKnot) {
		if(maxKnot<1){
			maxKnot=1;
		}
		this.maxKnot = maxKnot;
	}
	/**
	 * 调度：1  节点：2
	 * @return int
	 */
	public int getKind() {
		return kind;
	}
	public void setKind(int kind) {
		this.kind = kind;
	}
	public String toString(){
		StringBuilder sbToString=new StringBuilder();
		String sDisLicense="否";//分布式
		if(this.disLicense){
			sDisLicense="是";
		}
		String sTogether="否";//单服务器模式
		if(this.together){
			sTogether="是";
		}
		String sCluster="否";
		if(this.cluster){
			sCluster="是";
		}
		String sKind="";
		if(1==this.kind){
			sKind="调度";
		}else if(2==this.kind){
			sKind="节点";
		}		
		String huanhuang=System.getProperty("line.separator");
		sbToString.append(huanhuang);
		sbToString.append("*************************************************************************").append(huanhuang);
		sbToString.append("*	是否获得分布式授权许可			： ").append(sDisLicense).append("			*").append(huanhuang);
		if(this.disLicense){
			sbToString.append("*						 ").append("			*").append(huanhuang);
			sbToString.append("*	授权分布式节点数目			： ").append(this.disNumer).append("			*").append(huanhuang);
			sbToString.append("*						 ").append("			*").append(huanhuang);
			sbToString.append("*	单服务器模式				： ").append(sTogether).append("			*").append(huanhuang);
			sbToString.append("*						 ").append("			*").append(huanhuang);
			sbToString.append("*	单台服务器可运行节点数目		： ").append(this.maxKnot).append("			*").append(huanhuang);
			sbToString.append("*						 ").append("			*").append(huanhuang);
//			sbToString.append("*	是否支持集群				： ").append(sCluster).append("			*").append(huanhuang);
//			sbToString.append("*						 ").append("			*").append(huanhuang);
//			sbToString.append("*	授权集群数目				： ").append(this.clusterNumer).append("			*").append(huanhuang);
//			sbToString.append("*						 ").append("			*").append(huanhuang);
			sbToString.append("*	服务器类型				： ").append(sKind).append("			*").append(huanhuang);
		}
		sbToString.append("*************************************************************************").append(huanhuang);
		return sbToString.toString();
	}
}
