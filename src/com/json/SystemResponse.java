package com.json;

public class SystemResponse {
	
	public static final int sysError=-1;//系统出错
	public static final int sysOK=0;//系统正常，不提醒
	public static final int sysBusy=1;//用户操作太快，需要歇息
	public static final int sysRabbishAds=1;//用户发送的是垃圾消息
	
	private int sysCode;//系统状态：0：正常，1，发送太快，2，重复内容
	private String sysTips;//系统提示信息，显示在右上角
	private String sysAds;//系统推送的广告，显示在右侧
	private int acceptancerState;//对方的状态，注意捕获 断开，离线
	private String acceptancerSessionId;//对方的id--供新id使用
	
	
	public SystemResponse(int sysCode,String sysTips,String sysAds,int acceptancerState,String acceptancerSessionId){
		this.sysCode=sysCode;
		this.sysTips=sysTips;
		this.sysAds=sysAds;
		this.acceptancerState=acceptancerState;
		this.acceptancerSessionId=acceptancerSessionId;
	}
	public SystemResponse(){}
	
	public String getSysTips() {
		return sysTips;
	}
	public void setSysTips(String sysTips) {
		this.sysTips = sysTips;
	}
	public int getSysCode() {
		return sysCode;
	}
	public void setSysCode(int sysCode) {
		this.sysCode = sysCode;
	}
	public String getSysAds() {
		return sysAds;
	}
	public void setSysAds(String sysAds) {
		this.sysAds = sysAds;
	}
	public int getAcceptancerState() {
		return acceptancerState;
	}
	public void setAcceptancerState(int acceptancerState) {
		this.acceptancerState = acceptancerState;
	}
	public String getAcceptancerSessionId() {
		return acceptancerSessionId;
	}
	public void setAcceptancerSessionId(String acceptancerSessionId) {
		this.acceptancerSessionId = acceptancerSessionId;
	}
	

}
