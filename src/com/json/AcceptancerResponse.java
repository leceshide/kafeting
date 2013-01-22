package com.json;

import java.util.ArrayList;
import java.util.List;

import com.messages.Messages;

public class AcceptancerResponse extends SelfResponse{
	private int id;//信息的ID编号

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * 生成2人未读聊天信息（此方法没有问题，也不用区分对方的身份，因为站在自己的立场上，自己就是消息的发起人）
	 * @param sessionId 发起人id
	 * @param acceptancerId 受邀人id
	 * @return
	 */
	public static List<AcceptancerResponse> getAcceptancerResponseList(String sessionId,String acceptancerId){
		String sql="SELECT id,content,create_time  FROM messages WHERE sponsor_session_id='"+sessionId+"' AND acceptance_session_id='"+acceptancerId+"' AND state="+Messages.msgUnWatched+" ORDER BY create_time ASC";
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
				acceptancerResponse.setTime(msg.getTime("create_time").toString());
				responseList.add(acceptancerResponse);
			}
			return responseList;
		}
	}
	
}
