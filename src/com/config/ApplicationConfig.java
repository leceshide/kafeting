package com.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.chat.ChatGroup;
import com.chat.ChatGroupController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.render.ViewType;
import com.messages.MessageController;
import com.messages.Messages;
import com.users.UserState;
import com.users.Users;

public class ApplicationConfig extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {
		loadPropertyFile("application_config.properties");
		me.setDevMode(getPropertyToBoolean("devMode",false));
		me.setDevMode(false);
		me.setViewType(ViewType.JSP); 
		me.setError404View("404.html");
		me.setError500View("500.html");
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/",ChatGroupController.class);
		me.add("/message", MessageController.class);
		me.add("/chat",ChatGroupController.class);
	}

	@Override
	public void configPlugin(Plugins me) {
		DruidPlugin dp =null;
		String VCAP_SERVICES = java.lang.System.getenv("VCAP_SERVICES");
		if(StringKit.notNull(VCAP_SERVICES)){
			try {
				JSONObject credentials = JSONObject.parseObject(VCAP_SERVICES)
					.getJSONArray("mysql-5.1").getJSONObject(0).getJSONObject("credentials");

				StringBuffer jdbcUrl = new StringBuffer("jdbc:mysql://");
				jdbcUrl.append(credentials.getString("host")).append(":")
					.append(credentials.getString("port")).append("/")
					.append(credentials.getString("name")).append("?")
					.append("characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");

				String userName = credentials.getString("username");
				String password = credentials.getString("password");

				dp = new DruidPlugin(jdbcUrl.toString(), userName, password);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			dp= new DruidPlugin(getProperty("jdbcUrl"),getProperty("user"), getProperty("password"));
		} 
		dp.addFilter(new StatFilter());
		WallFilter wall = new WallFilter();
		wall.setDbType("mysql");
		dp.addFilter(wall);
		me.add(dp);
		
		ActiveRecordPlugin arp =new  ActiveRecordPlugin(dp);
		me.add(arp);
		
		arp.addMapping("messages", Messages.class);
		arp.addMapping("users", Users.class);
		arp.addMapping("chatgroup", ChatGroup.class);
		arp.addMapping("userstate", UserState.class);
	}

	@Override
	public void configInterceptor(Interceptors me) {
		// TODO Auto-generated method stub
	}

	@Override
	public void configHandler(Handlers me) {
		DruidStatViewHandler dvh = new DruidStatViewHandler("/druid");
		me.add(dvh);
	}
	
//	public static void main(String[] args) {
//		JFinal.start("WebRoot", 80, "/", 5);
//	}

}
