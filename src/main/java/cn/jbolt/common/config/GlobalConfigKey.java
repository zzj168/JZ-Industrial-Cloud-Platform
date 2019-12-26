package cn.jbolt.common.config;
/**   
 * 全局配置KEY
 * @ClassName:  GlobalConfigKey   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月16日 下午2:58:05   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class GlobalConfigKey {
	public static final String WECHAT_MP_SERVER_DOMAIN ="WECHAT_MP_SERVER_DOMAIN";//微信公众号服务器配置中的根地址
	public static final String WECHAT_WXA_SERVER_DOMAIN ="WECHAT_WXA_SERVER_DOMAIN";//微信小程序服务器配置中的根地址
	public static final String WECHAT_ASSETS_SERVER_DOMAIN ="WECHAT_ASSETS_SERVER_DOMAIN";//微信里的资源根地址
	
	
	/******************UI层配置************************/
	
	/**
	 * 系统名称
	 */
	public static final String SYSTEM_NAME="SYSTEM_NAME";
	/**
	 * 系统后台主页LOGO
	 */
	public static final String SYSTEM_ADMIN_LOGO="SYSTEM_ADMIN_LOGO";
	/**
	 * 版权所有人
	 */
	public static final String SYSTEM_COPYRIGHT_COMPANY="SYSTEM_COPYRIGHT_COMPANY";
	/**
	 * 版权所有人 URL
	 */
	public static final String SYSTEM_COPYRIGHT_LINK="SYSTEM_COPYRIGHT_LINK";
	/**
	 * 后台默认样式
	 */
	public static final String JBOLT_ADMIN_STYLE="JBOLT_ADMIN_STYLE";
	/**
	 * 后台是否采用多选项卡模式
	 */
	public static final String JBOLT_ADMIN_WITH_TABS="JBOLT_ADMIN_WITH_TABS";
	/**
	 * 后台登录页表单启用透明玻璃风格
	 */
	public static final String JBOLT_LOGIN_FORM_STYLE_GLASS="JBOLT_LOGIN_FORM_STYLE_GLASS";
	/**
	 * 后台登录页页面背景图模糊风格
	 */
	public static final String JBOLT_LOGIN_BGIMG_BLUR="JBOLT_LOGIN_BGIMG_BLUR";
	/**
	 * 后台登录页是否需要验证码
	 */
	public static final String JBOLT_LOGIN_USE_CAPTURE="JBOLT_LOGIN_USE_CAPTURE";
	/**
	 * 后台登录页验证码类型
	 */
	public static final String JBOLT_LOGIN_CAPTURE_TYPE="JBOLT_LOGIN_CAPTURE_TYPE";
	/**
	 * 后台登录页页面背景图
	 */
	public static final String JBOLT_LOGIN_BGIMG="JBOLT_LOGIN_BGIMG";
	
	
	/**
	 * JFinal action Report 输出方式
	 */
	public static final String JBOLT_ACTION_REPORT_WRITER="JBOLT_ACTION_REPORT_WRITER";
	/**
	 * JBoltAutoCache log 是否开启输出到日志文件
	 */
	public static final String JBOLT_AUTO_CACHE_LOG="JBOLT_AUTO_CACHE_LOG";
}
