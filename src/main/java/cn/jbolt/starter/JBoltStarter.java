package cn.jbolt.starter;

import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.server.undertow.WebBuilder;

import cn.jbolt.common.config.MainConfig;

/**
 * JBolt项目启动器
 * @ClassName:  JBoltStarter   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年11月9日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JBoltStarter {
	public static final JBoltStarter me=new JBoltStarter();
	/**
	 * 配置Filter
	 * @param builder
	 */
	public void configFilter(WebBuilder builder) {
		// 配置 Filter
		//builder.addFilter("myFilter", "com.abc.MyFilter");
		//builder.addFilterUrlMapping("myFilter", "/*");
		//builder.addFilterInitParam("myFilter", "key", "value");
	}
	/**
	 * 配置Servlet
	 * @param builder
	 */
	public void configServlet(WebBuilder builder) {
		// 配置 Servlet
		//builder.addServlet("myServlet", "com.abc.MyServlet");
		//builder.addServletMapping("myServlet", "*.do");
		//builder.addServletInitParam("myServlet", "key", "value");
	}
	/**
	 * 配置监听Listener
	 * @param builder
	 */
	public void configListener(WebBuilder builder) {
		// 配置 Listener
		//builder.addListener("com.abc.MyListener");
	}
	/**
	 * 配置webSocket
	 * @param builder
	 */
	public void configWebSocket(WebBuilder builder) {
		// 配置 WebSocket，MyWebSocket 需使用 ServerEndpoint 注解
		//builder.addWebSocketEndpoint("com.abc.MyWebSocket");
	}
	/**
	 * 创建并启动
	 */
	public void run() {
		UndertowServer.create(MainConfig.class,"undertow.properties")
		  .configWeb(builder -> {
			  	//配置Filter
			  	configFilter(builder);
			  	//配置Servlet
			  	configServlet(builder);
			  	//配置监听Listener
			  	configListener(builder);
			  	//配置webSocket
			  	configWebSocket(builder);
	       })
		  .start();
	}
	/**
	 * 启动器入口
	 * @param args
	 */
	public static void main(String[] args) {
		JBoltStarter.me.run();
	}
}
