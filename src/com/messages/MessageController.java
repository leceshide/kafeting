package com.messages;

import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
public class MessageController extends Controller {
	/**
	 * 此方法我写了近200行，感觉逻辑很复杂也很混乱，在此不便贴出，以免有误导之嫌。
	 * 这也给亲爱的读者一个自由发挥的机会，您可以设计自己的处理方法。我感觉此项目最应该学习的地方
	 * 不在服务器端，而是在前端的设计，前端设计是100%完整的！
	 * 如果您有简洁的方法，敬请和我交流，不吝赐教 ^_^
	 */
	public void addMessage() {
		//Todo...
	}
	
	/**
	 * 原因同上
	 */
	public void updateMsgAndSelfState() {
		//Todo...
	}
	
	/**
	 * 设置消息为已读
	 */
	public void setMessageWatched() {
		String msgIdArray = getPara("msgIdArray");
		String acceptancer_id = getPara("acceptancer_id");
		if (StringKit.isBlank(msgIdArray) || StringKit.isBlank(acceptancer_id)) 
			renderText("error");
		int strLength = msgIdArray.length();
		String newMsgIdArray = msgIdArray.substring(0, strLength - 1);
		Messages.dao.setMessageWatched(newMsgIdArray);
		renderNull();
	}
}
