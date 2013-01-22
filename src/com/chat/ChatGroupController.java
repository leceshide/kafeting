package com.chat;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.users.UserState;
import com.users.Users;
import com.util.DateKit;

public class ChatGroupController extends Controller {
	public void index(){
		renderJsp("common/index.html");
	}

	/**
	 * 用户第一次登入时自动发送请求的处理，实现初始化对方id
	 */
	public void initChatGroup() {
		String sessionId = getSession().getId();
		checkUserRegister(getRequest(), sessionId);
		int selfChatState = UserState.dao.getUserState(sessionId);
		if (selfChatState == Users.onlineWithoutChat || selfChatState == Users.offLine || selfChatState == Users.unExist) {
			UserState.dao.updateUserState(sessionId, Users.requestChat);
			String newAcceptancerId = UserState.dao.getLongWriteUser(sessionId);// 返回此用户ID
			if (StringKit.isBlank(newAcceptancerId)) {
				renderText("-1");
			} else {
				renderText("0," + newAcceptancerId);
			}
		} else if (selfChatState == Users.onChatting || selfChatState == Users.onWritting) {
			String resultTypeAndId = ChatGroup.dao.getChatTypeAndId(sessionId,
					ChatGroup.onChatting);
			if (StringKit.isBlank(resultTypeAndId)) {
				UserState.dao.updateUserState(sessionId, Users.requestChat);
				renderText("-2");
			} else {
				String[] strTempArr = resultTypeAndId.split(",");
				renderText("1," + strTempArr[1] + ","
						+ UserState.dao.getUserState(strTempArr[1]));
			}
		} else if (selfChatState == Users.requestChat) {
			String acceptance_session_id = UserState.dao
					.getLongWriteUser(sessionId);
			if (StringKit.isBlank(acceptance_session_id)) {
				renderText("-1");
			} else {
				renderText("0," + acceptance_session_id);
			}
		}
	}

	/**
	 *  创建或返回与之对应的会话者id
	 */
	public void getOrRequestChaterID() {
		HttpSession session = getSession(true);
		session.setMaxInactiveInterval(1800000);
		String sessionId = session.getId();
		checkUserRegister(getRequest(), sessionId);
		String acceptancerId = getPara("acceptancerId");
		if (StringKit.isBlank(acceptancerId)) {
			String idResult = getOnChattingID(sessionId);
			if (StringKit.isBlank(idResult)) {
				if (UserState.dao.getUserState(sessionId) <= Users.onlineWithoutChat) {
					UserState.dao.updateUserState(sessionId, Users.requestChat);
				}
				String newAcceptancerId = UserState.dao
						.getLongWriteUser(sessionId);
				if (StringKit.isBlank(newAcceptancerId)) {
					renderText("-1");
				} else {
					renderText("0," + newAcceptancerId);
				}
			} else {
				renderText("2," + idResult + ","
						+ UserState.dao.getUserState(idResult));
			}
		} else {
			int acceptancerStateTemp = UserState.dao
					.getUserState(acceptancerId);
			if (acceptancerStateTemp <= Users.onlineWithoutChat) {
				UserState.dao.updateUserState(sessionId,
						Users.onlineWithoutChat);
			}
			renderText("1," + acceptancerId + "," + acceptancerStateTemp);
		}
	}

	
	/**
	 * 查询词用户是否注册过
	 * @param sessionId
	 */
	private void checkUserRegister(HttpServletRequest request, String session_id) {
		if (UserState.dao.queryExistUserState(session_id)) {
			if (!Users.dao.queryExistOrUsing(session_id)) {
				registUsersHandlerHelper(request, session_id);
			}
		} else {
			registUsersHandlerHelper(request, session_id);
			UserState.dao.registUserState(session_id);
		}
	}

	/**
	 * 用户登录信息 的辅助方法
	 * @param request
	 * @param session_id
	 */
	private void registUsersHandlerHelper(HttpServletRequest request,
			String session_id) {
		String ip = getIpAddr(request);
		String login_in_date = DateKit.getDate();
		String login_in_time = DateKit.getTime();
		Users.dao.registUsers(session_id, ip, login_in_date, login_in_time);
	}

	/**
	 * 获取用户iP，可穿透代理而获取真实IP
	 * @param request
	 * @return
	 */
	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 获取与之聊天的对方 session_id,此时要考虑自己是会话的请求方还是接受方
	 * @param sessionId
	 *            自己的session_id
	 * @return 对方 session_id
	 */
	public String getOnChattingID(String sessionId) {
		String sql = "SELECT * FROM chatgroup WHERE  state =? AND sponsor_session_id=?  OR  acceptance_session_id=?";
		List<ChatGroup> chatGroupList = ChatGroup.dao.find(sql,
				Users.onChatting, sessionId, sessionId);
		int count = chatGroupList.size();
		if (count > 0) {
			ChatGroup chatGroupItem = chatGroupList.get(0);
			String sponsorSessionId = chatGroupItem.get("sponsor_session_id");
			String acceptance_session_id = chatGroupItem
					.get("acceptance_session_id");
			if (sponsorSessionId.equals(sessionId)) {
				return acceptance_session_id;
			} else {
				return sponsorSessionId;
			}
		} else {
			return null;
		}
	}



	public void disconnectionChatManager() {
		String sessionId = getSession().getId();
		String acceptancerId = getPara("acceptancerId");
		if (StringKit.isBlank(acceptancerId)) {
			String idResult = getOnChattingID(sessionId);
			if (StringKit.isBlank(idResult)) {
				renderText("-3");
			} else {
				ChatGroup.dao.disconnectionAChat(sessionId, idResult);
				renderText("4");
			}
		} else {
			ChatGroup.dao.disconnectionAChat(sessionId, acceptancerId);
			renderText("4");
		}
	}
	
	
	public void disAndExit(){
		String sessionId = getSession().getId();
		String acceptancerId = getPara("acceptancerId");
		if (StringKit.isBlank(acceptancerId)) {
			String idResult = getOnChattingID(sessionId);
			if (!StringKit.isBlank(idResult)) {
				ChatGroup.dao.disconnectionAChat(sessionId, idResult);
			}
		} else {
			ChatGroup.dao.disconnectionAChat(sessionId, acceptancerId);
		}
		UserState.dao.updateUserState(sessionId, Users.offLine);
		renderNull();
	}
	
	
}
