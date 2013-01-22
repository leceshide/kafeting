package com.users;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Users extends Model<Users> {
	public static final Users dao = new Users();
	public static final int unExist=-2;//不存在--不用表现在数据库中
	public static final int offLine=-1;//离线
	public static final int onlineWithoutChat=0;//在线不聊天 或 断开聊天//public static final int disconnection=4;//断开聊天
	public static final int onChatting=1;//正在聊天
	public static final int requestChat=2;//请求聊天
	public static final int onWritting=3;//正在输入
	//记录此新用户基本信息
	public void registUsers(String session_id,String ip,String login_in_date,String login_in_time){
		new Users().set("session_id", session_id).set("ip",ip).set("login_in_date",login_in_date).set("login_in_time",login_in_time).save();
	}
	
	/**
	 * 查看用户是否存在并正在使用,不存在返回false
	 * @param session_id
	 * @return
	 */
	public boolean queryExistOrUsing(String session_id){
		//判断用户是否存在应更加多个条件进行判断，避免误判的发生
		String sql="SELECT * FROM users WHERE session_id='"+session_id+"' ";
		List<Users> userList = dao.find(sql);
		int count = userList.size();
		return (count <= 0)? false :true;
		
	}

}
