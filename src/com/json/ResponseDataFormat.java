package com.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.messages.Messages;

/**
 * 此类为系统自动返回信息的处理类
 * @author Max
 *
 */
public class ResponseDataFormat {
	public static final ResponseDataFormat responseDataFormat = new ResponseDataFormat();
	private SystemResponse systemResponse;
	private SelfResponse selfResponse;
	private List<AcceptancerResponse> acceptancerResponseList;
	
	/**
	 * 设置完整返回信息
	 * @param systemResponse
	 * @param selfResponse
	 * @param acceptancerResponseList
	 * @return
	 */
	public static String setResponstJson(SystemResponse systemResponse,SelfResponse selfResponse, List<AcceptancerResponse> acceptancerResponseList){
		Map<String,Object> objectMap = new HashMap<String,Object>();
		objectMap.put("sys", systemResponse);
		if(null != selfResponse  ){
			objectMap.put("self", selfResponse);
		}
		if(null != acceptancerResponseList){
			objectMap.put("acceptancer", acceptancerResponseList);
		}
		String listJson = JSON.toJSONString(objectMap, true);  
		//System.out.println(listJson);
		return listJson;
	}
	
	/**
	 * 仅返回系统搜集到的错误信息--适用于 系统异常、对方不存在等
	 * @param systemResponse
	 * @return
	 */
	public static String setResponstJsonOnlySystem(SystemResponse systemResponse){
		Map<String,Object> objectMap = new HashMap<String,Object>();
		objectMap.put("sys", systemResponse);
		String listJson = JSON.toJSONString(objectMap, true);  
		System.out.println(listJson);
		return listJson;
	}
	

	public List<AcceptancerResponse> getAcceptancerResponseList(String sessionId,String acceptancerId){
		String sql="SELECT id,content,create_time  FROM messages WHERE sponsor_session_id='"+sessionId+"' AND acceptance_session_id='"+acceptancerId+"' AND state="+Messages.msgUnWatched+" ORDER BY create_time ASC";
		//System.out.println("AcceptancerResponse.getAcceptancerResponseList:"+sql);
		List<Messages> acceptancerResponseList = Messages.dao.find(sql);
		int count =  acceptancerResponseList.size();
		if(count<=0){
			return null;
		}else{
			ArrayList<AcceptancerResponse> responseList = new ArrayList<AcceptancerResponse>();
			for (Messages msg : acceptancerResponseList) {
				AcceptancerResponse acceptancerResponse =new AcceptancerResponse();
				acceptancerResponse.setId(msg.getInt("id"));
				acceptancerResponse.setMsg(msg.getStr("content"));
				acceptancerResponse.setTime(msg.getStr("create_time"));
				responseList.add(acceptancerResponse);
			}
			return responseList;
		}
	}
	
	public SystemResponse getSystemResponse() {
		return systemResponse;
	}
	public void setSystemResponse(SystemResponse systemResponse) {
		this.systemResponse = systemResponse;
	}
	public SelfResponse getSelfResponse() {
		return selfResponse;
	}
	public void setSelfResponse(SelfResponse selfResponse) {
		this.selfResponse = selfResponse;
	}
	public List<AcceptancerResponse> getAcceptancerResponseList() {
		return acceptancerResponseList;
	}
	public void setAcceptancerResponseList(
			List<AcceptancerResponse> acceptancerResponseList) {
		this.acceptancerResponseList = acceptancerResponseList;
	}

}
