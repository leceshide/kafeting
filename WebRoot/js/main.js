//获取服务器时间，存储在本地
var $lastPostTime;//初始化时间
var $acceptancer_id = "";
var $updateTimeOutStates = 0;/*供判断timeout事件是否被执行了*/
var t;/*代表定时任务，供下次清除*/
var timerArr;/*新消息提示*/
//js控制输入框长度
$(document).ready(
		function(e) {
			show_div = function() {
				var $msgWidth = $("#bottom").width() - $("#linkSwitch").width()
						- $("#post").width() - 56;
				$("#msg").width($msgWidth);
				//设置内容栏，右侧窗体宽度
				var $contentRightLength = $("#content").width()
						- $("#content_left").width() - 26;
				$("#content_right").width($contentRightLength);
			};
			$(window).resize(function(e) {
				show_div();
			});
			show_div();
		});

$(document).ready(//初始化时间
$lastPostTime = new Date());

$(document).ready(//初始化 $acceptancer_id
initChatGroup());

$(document).ready(//初始化 发送按钮功能
$("#post").live("click", postMsg));

$(document).ready(//初始化 sponAChatBtn 按钮功能
$("#sponAChatBtn").live("click", initChatGroup));


/*设置妙语连珠栏里点击添加到文本域里*/
$(document).ready(//初始化 $acceptancer_id
$("li").live("click", function() {
	//判断此时按钮的状态
	if (!$("#msg").attr("disabled")) {
		var temp = $.trim($("#msg").val());
		if ($(this).attr("title") != "") {
			temp += $(this).attr("title");
		} else {
			temp += $(this).text();
		}
		$("#msg").text(temp);
	}
}));

/* 按钮正在请求状态 */
function btnRequestState() {
	$("#linkSwitch").val("正在连接……");
	$("#linkSwitch").attr("disabled", "true");
	$("#linkSwitch").unbind("click");
	$("#msg").attr("disabled", "true");
	$("#post").attr("disabled", "true");
}

/* 按钮正常状态（会话正常，等待用户点击断开） */
function btnNormalState() {
	$("#linkSwitch").val("断开会话");
	$("#linkSwitch").removeAttr("disabled");
	$("#linkSwitch").unbind("click");
	$("#linkSwitch").bind("click", disconnectionChat);
	$(".sysTipsNone").remove();
	$("#msg").removeAttr("disabled");
	$("#post").removeAttr("disabled");

}

/* 按钮请求发起会话状态  */
function btnSponsorState() {
	$("#linkSwitch").val("发起会话");
	$("#linkSwitch").removeAttr("disabled");
	$("#linkSwitch").unbind("click");
	$("#linkSwitch").bind("click", initChatGroup);
	$("#msg").attr("disabled", "true");
	$("#post").attr("disabled", "true");
}

/*第一次初始化 回话用户信息*/
function initChatGroup() {
	/*重新初始化Update……*/
	$updateTimeOutStates = 0;
	/* 正在连接，对控件控制 */
	btnRequestState();
	$.ajax({
				type : "POST",
				url : "chat/initChatGroup",
				dataType : "html",
				success : function(data) {
					/*处理用户第一次请求*/
					processAcceptancerIDAndState(data);
				},
				error : function(textStatus) {
					if ($(".sysTipsError").length > 0) {
						$(".sysTipsError").remove();
						$("#sponsorBtnDiv").remove();
					}
					tipHtml = "<span class='sysTipsError'>很抱歉，服务器开小差了，请您稍后重试！--没人聊天的话，耐心点击几次按钮就可能有场美丽的邂逅哦~~<br/></span> <div id='sponsorBtnDiv'><input type='Button' id='sponAChatBtn' value='重试' class='btn'/><br/></div>";
					$("#content_left").append(tipHtml);
					btnSponsorState();
				}
			});
	return false;
}

/*对服务器返回的查询会话信息的处理（仅包含简单的用户状态信息）*/
function processAcceptancerIDAndState(str) {
	if (str == "")
		return;
	var strs = new Array(); //定义一数组
	strs = str.split(","); //字符分割      
	var tipCode = strs[0];
	var tipHtml = "";
	switch (tipCode) {
	case "-2":/*系统错误*/
		if ($("#sponAChatBtn").length > 0) {
			$("#content_left").empty();
		}
		tipHtml = "<span class='sysTipsStatic'>很抱歉，服务器开小差了，系统已自动帮您修复^_^<br/></span><div id='sponsorBtnDiv'><input type='Button' id='sponAChatBtn' value='申请会话' class='btn'/><br/></div>";
		$("#content_left").append(tipHtml);
		btnSponsorState();
		break;
	case "-1": /*没有找到会话用户，就提示用户暂无会话用户*/
		if ($("#sponAChatBtn").length > 0) {
			$("#content_left").empty();
		}
		tipHtml = "<span class='sysTipsNone'>暂未找到正在请求聊天的用户，请您喝杯咖啡稍等会吧^_^ --没人聊天的话，耐心点击几次按钮就可能有场美丽的邂逅哦~~<br/></span> <div id='sponsorBtnDiv'><input type='Button' id='sponAChatBtn' value='重试' class='btn'/><br/></div>";
		$("#content_left").append(tipHtml);
		btnSponsorState();
		break;
	case "0": /*找到了新用户,就恭喜用户*/
		if ($("#sponAChatBtn").length > 0) {
			$("#content_left").empty();
		}
		tipHtml = "<span class='sysTipsStatic'>恭喜您,您已与对方建立了会话，现在就开始说话吧！^_^<br/></span>";
		$("#content_left").append(tipHtml);
		//客户端开始没有对方的id,现在要初始化对方的id
		$acceptancer_id = strs[1];
		$("#msg").empty();
		btnNormalState();
		//定时更新自己的状态和对方的信息
		/*判断timeout轮询是否执行了*/
		if ($updateTimeOutStates == 0) {/*轮询没有被执行，应执行----通过点按钮重置完成*/
			updateMsgAndSelfState();
		}
		break;
	case "1":
		/*提示用户已经建立有效连接*/
		if ($("#sponAChatBtn").length > 0) {
			$("#content_left").empty();
		}
		tipHtml = "<span class='sysTipsStatic'><hr>提示：您已与对方建立了会话，请继续聊天吧！^_^<br/></span>";
		$("#content_left").append(tipHtml);

		//强制更新对方的id
		$acceptancer_id = strs[1];
		//此时声明对方的状态
		acceptancerStateFunction(strs[2]);
		btnNormalState();
		//|此时不需要使用了吧，因为用户在添加……|定时更新自己的状态和对方的信息
		/*判断timeout轮询是否执行了*/
		if ($updateTimeOutStates == 0) {/*轮询没有被执行，应执行*/
			updateMsgAndSelfState();
		}
		break;
	default:
		break;
	}
}

//处理来自服务器端的完整数据，保护系统、对方、个人信息
function processCompleteMessage(json) {
	/*清除新消息提醒*/
	if (typeof (timerArr) != "undefined") {
		$.blinkTitle.clear(timerArr);
	}

	if (typeof (json) == "undefined" || json == null)
		return;
	//使用js解析json
	var sysCodeTemp = json.sys.sysCode;
	/* 处理系统状态  */
	if (sysCodeTemp == -1) {
		/*系统错误*/
		$("#content_left").empty();
		var tipHtml = "<span class='sysTipsWraning'>很抱歉，服务器开小差了，正在帮您修复……您可以选择重新申请一个会话^_^--没人聊天的话，耐心点击几次按钮就可能有场美丽的邂逅哦~~<br/></span><div id='sponsorBtnDiv'><input type='Button' id='sponAChatBtn' value='申请会话' class='btn'/><br/></div>";
		$("#content_left").append(tipHtml);
		btnSponsorState();
		return;
	} else if (sysCodeTemp == 0) { /*正常*/
		$acceptancer_id = json.sys.acceptancerSessionId;/*重新初始化$acceptancer_id*/
		/*处理用户统计数据*/
		processHeaderCount(json.sys.sysTips, json.sys.sysAds);
	}

	/*处理对方消息*/
	var tempId = "";/*接收对方信息的编号，供回传*/
	var acceptancerMsgTemp = json.acceptancer;

	if (typeof (acceptancerMsgTemp) != "undefined") {
		for ( var i = 0, count = acceptancerMsgTemp.length; i < count; i++) {
			var acceptancerMsgObj = acceptancerMsgTemp[i];
			var msg = acceptancerMsgObj.msg;
			var time = acceptancerMsgObj.time;
			tempId += acceptancerMsgObj.id + ",";
			var $myContent2 = "<span class='oppositeFormat'><strong>对方:</strong>"
					+ msg
					+ " <span class='timeFormat'>("
					+ time
					+ ")</span></span><br/>";
			$("#content_left").append($myContent2);
		}
		timerArr = $.blinkTitle.show();/*提示用户有新消息*/
		setMsgWatched(tempId);/*把对方的消息设置为已读*/
	}

	/*处理自己的消息*/
	var selfMsgTemp = json.self;
	if (typeof (selfMsgTemp) != "undefined") {
		var $myContent3;
		if (selfMsgTemp.state == -2) {/*发送失败*/
			$myContent3 = "<span class='sysTipsSendFail'><strong>我:</strong>"
					+ selfMsgTemp.msg
					+ "[哎~~此消息对方将无法正常接收  ToT] <span class='timeFormat'>("
					+ selfMsgTemp.time + ")</span></span><br/>";
		} else {
			$myContent3 = "<span class='selfFormat'><strong>我:</strong>"
					+ selfMsgTemp.msg + " <span class='timeFormat'>("
					+ selfMsgTemp.time + ")</span></span><br/>";
		}
		$("#content_left").append($myContent3);
	}
	/*处理用户状态*/
	acceptancerStateFunction(json.sys.acceptancerState);
	//如果有滚动条，就滚动到最低端
	scrollToBottom();
}

/*处理系统发回的对方用户状态的方法*/
function acceptancerStateFunction(code) {
	//删除静态消息sysTipsStatic
	$(".sysTipsStatic").remove;
	var tipCode;
	if (isNaN(code)) {
		tipCode = parseInt(code);
	} else {
		tipCode = code;
	}
	switch (tipCode) {
	case -1, -2:
		/*删除申请会话按钮*/
		//先判断是否有申请会话的元素，如果有就不再显示了
		if ($("#sponAChatBtn").length == 0) {
			//用户离线或不存在，提示用户重新申请
			tipHtml = "<span class='sysTipsOffLine'>对方已离线，请您重新申请一个新的会话！--没人聊天的话，耐心点击几次按钮就可能有场美丽的邂逅哦~~<br/></span> <div id='sponsorBtnDiv'><input type='Button' id='sponAChatBtn' value='申请会话' class='btn'/><br/></div>";
			$("#content_left").append(tipHtml);
			btnSponsorState();
		}
		/*消除定时轮询*/
		if ($updateTimeOutStates > 0) {/*轮询被执行，应清除*/
			clearTimeout(t);
		}

		break;
	case 0:/*对方已断开会话-----------此处的问题需要修复，就是对方断开后会，因前面的元素存在，会被阻塞*/
		if ($("#sponAChatBtn").length > 0) {
			$(".sysTipsDisconnection").remove();
			$("#sponsorBtnDiv").remove();
		}
		tipHtml = "<span class='sysTipsDisconnection'><hr>会话已断开，请您重新申请一个新的会话吧！--没人聊天的话，耐心点击几次按钮就可能有场美丽的邂逅哦~~<br/></span><div id='sponsorBtnDiv'><input type='Button' id='sponAChatBtn' value='申请会话' class='btn'/><br/></div>";
		$("#content_left").append(tipHtml);
		btnSponsorState();

		/*消除定时轮询*/
		if ($updateTimeOutStates > 0) {/*轮询被执行，应清除*/
			clearTimeout(t);
		}
		break;
	case 3:/*对方正在输入……*/
		/*先清除上次的正在输入提醒，防止过多的提醒*/
		if ($(".sysTipsWrintting").length > 0) {
			$(".sysTipsWrintting").remove();
		}
		tipHtml = "<span class='sysTipsWrintting'>对方正在输入……<br/></span>";
		$("#content_left").append(tipHtml);
		btnNormalState();
		break;
	case 5:/*特别状态，供用户手动请求成功的使用*/
		$("#content_left").empty();
		tipHtml = "<span class='sysTipsStatic'>恭喜您,您已与对方建立了会话，现在就开始说话吧！^_^<br/></span>";
		$("#content_left").append(tipHtml);
		btnNormalState();
		break;
	case 6:/*特别状态，供用户手动请求失败时使用-----*/
		if ($("#sponAChatBtn").length > 0) {
			$("#content_left").empty();
		}
		tipHtml = "<span class='sysTipsNone'>暂未找到正在请求聊天的用户，您可以重新申请一个会话！^_^  --没人聊天的话，耐心点击几次按钮就可能有场美丽的邂逅哦~~<br/></span> <div id='sponsorBtnDiv'><input type='Button' id='sponAChatBtn' value='申请会话' class='btn'/><br/></div>";
		$("#content_left").append(tipHtml);
		btnSponsorState();
		/*消除定时轮询*/
		if ($updateTimeOutStates > 0) {/*轮询被执行，应清除*/
			clearTimeout(t);
		}
		break;
	case 4:/*----此处供其他程序调用，----*/
		/*清空聊天记录（更好的方法是提供用户下载聊天记录的功能）*/
		tipHtml = "<span class='sysTipsDisconnection'><hr>您已成功断开此会话！您可以选择开始一段新的会话^_^<br/></span><div id='sponsorBtnDiv'><input type='Button' id='sponAChatBtn' value='申请会话' class='btn'/><br/></div>";
		$("#content_left").append(tipHtml);
		btnSponsorState();
		/*消除定时轮询*/
		if ($updateTimeOutStates > 0) {/*轮询被执行，应清除*/
			clearTimeout(t);
		}
		break;
	case -3:/*----此处供其他程序调用，----*/
		tipHtml = "<span class='sysTipsNone'>您尚未与任何用户建立有效的会话！您可以选择开始一段新的会话^_^--没人聊天的话，耐心点击几次按钮就可能有场美丽的邂逅哦~~<br/></span> <div id='sponsorBtnDiv'><input type='Button' id='sponAChatBtn' value='申请会话' class='btn'/><br/></div>";
		$("#content_left").append(tipHtml);
		btnSponsorState();
		/*消除定时轮询*/
		if ($updateTimeOutStates > 0) {/*轮询被执行，应清除*/
			clearTimeout(t);
		}
		break;
	default:
		break;
	}
}

//每隔一段时间，读取一次信息
function updateMsgAndSelfState() {
	$updateTimeOutStates += 1;/*累加器，每次执行就加1*/
	/*删除未找到用户时的提示*/
	$("#sysTipsNone").remove();
	/*删除用户正在输入提醒  */
	$(".sysTipsWrintting").remove();
	/*删除对方离线信息*/
	$(".sysTipsOffLine").remove();

	/*通过用户文本框里的内容进行判断，不是很科学，但却是有效的方案 */
	var $myState = -2;//获取用户的状态，1代表用户不输入	//alert($text.length);
	var $text = $.trim($("#msg").val());
	if ($text.length < 2) {
		$myState = 1;
	} else {
		$myState = 3;
	}

	//向服务器发请求
	$.ajax({
		type : "POST",
		url : "message/updateMsgAndSelfState",
		data : {
			selfState : $myState,
			acceptancer_id : $acceptancer_id
		},
		dataType : "json",
		success : function(data) {
			processCompleteMessage(data);
		},
		error : function(textStatus) {
			/* 发生错误就不做处理 */
		}
	});
	t = setTimeout('updateMsgAndSelfState()', 3000);
}

/* 断开连接事件 */
function disconnectionChat() {
	/* 提示用户真的要断开连接吗 */
	if (confirm("您确定要断开与对方的会话吗？")) {
		$.ajax({
			type : "POST",
			url : "chat/disconnectionChatManager",
			data : {
				acceptancer_id : $acceptancer_id
			},
			dataType : "json",
			success : function(data) {
				acceptancerStateFunction(data);
				btnSponsorState();
			},
			error : function(textStatus) {
				alert("很抱歉，服务器开小差了，无法断开您与对方的会话，你可以选择关闭浏览器结束此会话。祝您生活愉快^_^");
			}
		});
		return false;
	}
}

//捕获键盘事件
$(document).keydown(function(e) {
	if (e.ctrlKey && e.which == 13) {
		e.preventDefault();//屏蔽enter对系统作用。按后增加\r\n等换行
		$("#msg").val($("#msg").val() + "\n");
		$("#msg").focus();
	} else if (e.keyCode == 13) {
		e.preventDefault();//屏蔽enter对系统作用。按后增加\r\n等换行
		var $text = $.trim($("#msg").val());
		if ($text == "") {
			$("#msg").val("").focus();
		} else {
			//发送到服务器端
			postMsg();
		}
	}
});

//控制滚动条一直滚动到底部
function scrollToBottom() {
	var scrollTop = $("#content_left")[0].scrollHeight;
	$("#content_left").scrollTop(scrollTop);
}

function postMsg() {
	var $text = $.trim($("#msg").val());
	var msg = $text;
	if (msg != "") {
		var $curDate = new Date();
		if ($curDate.getTime() - $lastPostTime.getTime() < 1200) {
			alert("你发送得太快了,喝杯咖啡歇会吧！^_^");
		} else {
			$lastPostTime = new Date();
			/* 把消息发送的服务器端处理  */
			$.ajax({
				type : "POST",
				url : "message/addMessage",
				data : {
					selfMsg : msg,
					acceptancer_id : $acceptancer_id
				},
				dataType : "json",
				success : function(data) {
					/* 处理完整的系统返回信息 */
					processCompleteMessage(data);
					//清除此信息
					$("#msg").text("").focus();
					$("#msg").val("").focus();
				},
				error : function(textStatus) {
					var tipHtml = "<span class='sysTipsSendFail'>我:" + msg
							+ "[ 哎~~这破网，发送失败了！555...请您再试一次吧^_^ ] <br/></span>";
					$("#content_left").append(tipHtml);
				}
			});
			return false;
		}
	}
}

//设置对方收到的消息已发送成功
function setMsgWatched(str) {
	if (str == "")
		return false;
	$.ajax({
		type : "POST",
		url : "message/setMessageWatched",
		data : {
			msgIdArray : str,
			acceptancer_id : $acceptancer_id
		},
		dataType : "json",
		success : function(data) {
		},
		error : function(textStatus) {
		}
	});
	return false;//阻止表单提交
}

/*处理用户统计数据*/
function processHeaderCount(sys_online, sys_chatting) {
	$("#sys_online").text(sys_online);
	$("#sys_chatting").text(sys_chatting);
}

/*退出按钮设计*/
function disAndExit(){
	//alert("被执行了");
	if(confirm("您真的要退出会话吗？")){
		$.ajax({
			type : "POST",
			url : "chat/disAndExit",
			data : {
				acceptancer_id : $acceptancer_id
			},
			dataType : "json",
			success : function(data) {
				window.opener = null;
				window.close();
			},
			error : function(textStatus) {
				alert("很抱歉，服务器开小差了，操作失败，您可以选择关闭浏览器结束此会话。祝您生活愉快^_^");
			}
		});
	}
	return false;
}
