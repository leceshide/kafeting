package com.chat;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.users.UserState;
import com.users.Users;
import com.util.DateKit;
/**
 * ChatGroup is the model about two users relationship
 * @author hanzhankang@126.com
 */
@SuppressWarnings("serial")
public class ChatGroup extends Model<ChatGroup> {
	public static final ChatGroup  dao= new ChatGroup();
	
	/*Chat state */
	public static final int onChatting=Users.onChatting;
	public static final int chatComplete=-1;
	
	/**
	 * 为2个用户发起一个会话,并设置自己的状态为正在会话
	 * Begin a chat and set their state is onChatting
	 * @param sponsor_session_id
	 * @param acceptance_session_id
	 */
	public void setAChat(String sponsor_session_id ,String acceptance_session_id ){
		new ChatGroup().set("sponsor_session_id",sponsor_session_id)
		.set("acceptance_session_id",acceptance_session_id)
		.set("begin_date",DateKit.getDate())
		.set("begin_time",DateKit.getTime())
		.set("state",ChatGroup.onChatting)
		.save();
		UserState.dao.updateTwoUserState(sponsor_session_id, acceptance_session_id, Users.onChatting);
	}
	
	
	/**
	 * 断开2个用户的会话--已请求者的sponsor_session_id，这样的原因是不管请求断开的人是发起者还是请求者，都能断开
	 * Disconnection a chat 
	 * @param sponsor_session_id
	 * @param acceptance_session_id
	 */

	@Before(Tx.class)
	public void disconnectionAChat(String sponsor_session_id ,String acceptance_session_id ){
		String sql="UPDATE chatgroup SET state=?,end_datetime=? WHERE sponsor_session_id=? or acceptance_session_id=?";
		Db.update(sql,ChatGroup.chatComplete,DateKit.getDateTime(),sponsor_session_id,sponsor_session_id);
		UserState.dao.updateTwoUserState(sponsor_session_id, acceptance_session_id,Users.onlineWithoutChat);
	}
	
	
	
	/**
	 * 找到已存在的会话（包括正在进行和已结束），获取对方id,并确定本人是主叫还是被叫
	 * 已结束回话只返回最近被结束的一条
	 * @param sessionId
	 * @param chatState为已断开--此处设置为灵活
	 * @return 数字+对方id，其中1代表自己为主叫，0代表自己为被叫
	 */
	public String getChatTypeAndId(String sessionId,int chatState){
		String sql="SELECT * FROM chatgroup WHERE state="+chatState+" AND sponsor_session_id='"+sessionId+"' OR acceptance_session_id='"+sessionId+"' ORDER BY end_datetime DESC";
		List<ChatGroup> chatGroupList = dao.find(sql);
		int count = chatGroupList.size();
		if (count > 0) {
			ChatGroup chatGroupItem = (ChatGroup)chatGroupList.get(0);
			String sponsorSessionId = chatGroupItem.get("sponsor_session_id");
			String acceptance_session_id = chatGroupItem.get("acceptance_session_id");
			if (sponsorSessionId.equals(sessionId)) {
				return "1,"+acceptance_session_id;
			} else {
				return  "0,"+sponsorSessionId;
			}
		} else {
			return null;
		}
	}
}
