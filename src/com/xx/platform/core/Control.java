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
     * �Ƿ��÷ֲ�ʽ������Ȩ���
     * @return boolean
     */
	public boolean isDisLicense() {
		return disLicense;
	}
	public void setDisLicense(boolean disLicense) {
		this.disLicense = disLicense;
	}
	/**
	 * �Ƿ�֧�ּ�Ⱥ
	 * @return boolean
	 */
	public boolean isCluster() {
		return cluster;
	}
	public void setCluster(boolean cluster) {
		this.cluster = cluster;
	}
	/**
	 * ��Ȩ�ֲ�ʽ�ڵ���Ŀ
	 * @return int
	 */
	public int getDisNumer() {
		return disNumer;
	}
	public void setDisNumer(int disNumer) {
		this.disNumer = disNumer;
	}
	/**
	 * ��Ȩ��Ⱥ��Ŀ
	 * @return int
	 */
	public int getClusterNumer() {
		return clusterNumer;
	}
	public void setClusterNumer(int clusterNumer) {
		this.clusterNumer = clusterNumer;
	}
	/**
	 * �����ܷ�ͽڵ����ͬһ̨������
	 * @return boolean
	 */
	public boolean isTogether() {
		return together;
	}
	public void setTogether(boolean together) {
		this.together = together;
	}
	/**
	 * ͬһ̨����������ܹ����ж��ٸ����
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
	 * ���ȣ�1  �ڵ㣺2
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
		String sDisLicense="��";//�ֲ�ʽ
		if(this.disLicense){
			sDisLicense="��";
		}
		String sTogether="��";//��������ģʽ
		if(this.together){
			sTogether="��";
		}
		String sCluster="��";
		if(this.cluster){
			sCluster="��";
		}
		String sKind="";
		if(1==this.kind){
			sKind="����";
		}else if(2==this.kind){
			sKind="�ڵ�";
		}		
		String huanhuang=System.getProperty("line.separator");
		sbToString.append(huanhuang);
		sbToString.append("*************************************************************************").append(huanhuang);
		sbToString.append("*	�Ƿ��÷ֲ�ʽ��Ȩ���			�� ").append(sDisLicense).append("			*").append(huanhuang);
		if(this.disLicense){
			sbToString.append("*						 ").append("			*").append(huanhuang);
			sbToString.append("*	��Ȩ�ֲ�ʽ�ڵ���Ŀ			�� ").append(this.disNumer).append("			*").append(huanhuang);
			sbToString.append("*						 ").append("			*").append(huanhuang);
			sbToString.append("*	��������ģʽ				�� ").append(sTogether).append("			*").append(huanhuang);
			sbToString.append("*						 ").append("			*").append(huanhuang);
			sbToString.append("*	��̨�����������нڵ���Ŀ		�� ").append(this.maxKnot).append("			*").append(huanhuang);
			sbToString.append("*						 ").append("			*").append(huanhuang);
//			sbToString.append("*	�Ƿ�֧�ּ�Ⱥ				�� ").append(sCluster).append("			*").append(huanhuang);
//			sbToString.append("*						 ").append("			*").append(huanhuang);
//			sbToString.append("*	��Ȩ��Ⱥ��Ŀ				�� ").append(this.clusterNumer).append("			*").append(huanhuang);
//			sbToString.append("*						 ").append("			*").append(huanhuang);
			sbToString.append("*	����������				�� ").append(sKind).append("			*").append(huanhuang);
		}
		sbToString.append("*************************************************************************").append(huanhuang);
		return sbToString.toString();
	}
}
