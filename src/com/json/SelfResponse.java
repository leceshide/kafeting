package com.json;

public class SelfResponse {
	private String msg;
	private String time;
	private int postType;
	public SelfResponse(){}
	
	public SelfResponse(String msg,String time,int postType){
		this.msg=msg;
		this.time=time;
		this.postType=postType;
	}
	
	public int getPostType() {
		return postType;
	}

	public void setPostType(int postType) {
		this.postType = postType;
	}

	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
