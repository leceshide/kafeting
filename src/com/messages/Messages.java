package com.messages;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.util.DateKit;
@SuppressWarnings("serial")
public class Messages extends Model<Messages> {
	public static final Messages  dao= new Messages();
	
	public static final int msgFail=-2;
	public static final int msgWatched=-1;
	public static final int msgUnWatched=0;
	
	/**
	 * 添加新的消息，默认问未状态，等待对方读取,返回插入的时间
	 * @param sessionId
	 * @param contextText
	 * @param create_date
	 * @param create_time
	 * 
	 */
	public String insertNewMessage(String sessionId,String acceptance_session_id,String contentText){
		String curTime=DateKit.getTime();
		new Messages()
		.set("sponsor_session_id", sessionId)
		.set("acceptance_session_id", acceptance_session_id)
		.set("content", contentText)
		.set("create_date",DateKit.getDate())
		.set("create_time",curTime)
		.set("state", msgUnWatched)
		.save();
		return curTime;
	}
	/**插入失效的信息*/
	public String insertFailMessage(String sessionId,String acceptance_session_id,String contentText){
		String curTime=DateKit.getTime();
		new Messages()
		.set("sponsor_session_id", sessionId)
		.set("acceptance_session_id", acceptance_session_id)
		.set("content", contentText)
		.set("create_date",DateKit.getDate())
		.set("create_time",curTime)
		.set("state", msgFail)
		.save();
		return curTime;
	}
	
	/**
	 * 设置
	 * @param strArr
	 */
	public void setMessageWatched(String strArr){
		String sql="UPDATE messages SET state=? WHERE id IN("+strArr+")";
		Db.update(sql,msgWatched);
	}
	
}
