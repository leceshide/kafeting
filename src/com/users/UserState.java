package com.users;

import java.util.List;

import com.chat.ChatGroup;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.util.DateKit;

@SuppressWarnings("serial")
public class UserState extends Model<UserState> {
	public static final UserState dao = new UserState();

	/**
	 * 从用户状态中找到等待最长的用户,并建立连接，如果没有合适的对象，就返回空
	 */
	@ Before(Tx.class)
	public String getLongWriteUser(String sessionId){
		String sql="SELECT * FROM userstate WHERE session_id<> '"+sessionId+"'  AND state="+Users.requestChat+" ORDER BY sponsor_datetime ASC LIMIT 0,2";
		List<UserState> singleUsers= dao.find(sql);
		int count = singleUsers.size();
		if(count==0){
			return null;
		}else{
			UserState userState = singleUsers.get(0);
			String acceptance_session_id= userState.getStr("session_id").trim();
			ChatGroup.dao.setAChat(sessionId,acceptance_session_id);
			return acceptance_session_id;
		}
	}
	
	public void updateUserState(String session_id,int userState){
		String sql="UPDATE userstate SET state=? WHERE session_id=?";
		if(userState==Users.requestChat){
			sql="UPDATE userstate SET state=?,sponsor_datetime=? WHERE session_id=?";
			Db.update(sql,Users.requestChat,DateKit.getDateTime(),session_id);
		}else{
			Db.update(sql,userState,session_id);
		}
	}
	
	/**
	 * 设置2个人的状态--避免事务失效 
	 * @param sponsorID 用户id 不分先后
	 * @param accentID
	 * @param userState
	 */
	public void updateTwoUserState(String sponsorID,String accentID,int userState){
		String sql="UPDATE userstate SET state=? WHERE session_id IN (?,?)";
		Db.update(sql,userState,sponsorID,accentID);
	}
	
	
	/**
	 * 查询用户状态,不存在返回false
	 * @param session_id
	 * @return
	 */
	public boolean queryExistUserState(String session_id){
		//针对当前用户，如果用户状态表中不存在其的记录，就进行插入，如果存在就更新
		String sql="SELECT * FROM userstate WHERE session_id='"+session_id+"' AND state >="+Users.onlineWithoutChat;
		List<UserState> singleUsers= dao.find(sql);
		int count = singleUsers.size();
		return (count <= 0)? false :true;
	}
	
	
	public void registUserState(String session_id){
		new UserState().set("session_id", session_id).set("state", Users.requestChat).set("sponsor_datetime", DateKit.getDateTime()).save();
	}
	
	public int getUserState(String session_id){
		String sql="SELECT * FROM userstate WHERE session_id='"+session_id+"'";
		List<UserState> singleUsers= dao.find(sql);
		int count = singleUsers.size();
		return (count<=0) ? Users.unExist : singleUsers.get(0).getInt("state");
	}
	
	
	/*统计用户状态人数*/
	public long countUser(int userState){
		String sql="SELECT COUNT(*) FROM userstate WHERE state="+userState;
		long result = Db.queryLong(sql);
		return result;
	}
	
}
