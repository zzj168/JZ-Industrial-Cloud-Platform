package cn.jbolt._admin.userconfig;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;

import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.GlobalConfigKey;
import cn.jbolt.common.util.CACHE;
/**
 * 用户自己使用的配置
 * @ClassName:  UserConfigAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月25日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class UserConfigAdminController extends BaseController {
	@Inject
	private UserConfigService service;
	@UnCheck
	public void index() {
		set("userConfigs", service.getAdminList(getSessionAdminUserId()));
		render("index.html");
	}
	@UnCheck
	public void toggleBooleanConfig() {
		Integer userId=getSessionAdminUserId();
		Ret ret=service.toggleBooleanConfig(userId,getInt(0));
		if(ret.isOk()) {
			processUserConfigCookie(userId,ret.getAs("data"));
		}
		renderJson(ret);
	}
	/**
	 * 根据
	 * @param configKey
	 */
	private void processUserConfigCookie(Integer userId,String configKey) {
		switch (configKey) {
		case GlobalConfigKey.JBOLT_LOGIN_FORM_STYLE_GLASS:
			boolean glass=CACHE.me.getUserJBoltLoginFormStyleGlass(userId);
			setCookie("jbolt_login_glassStyle",glass+"" ,60*60*24*7*4);
			break;
		case GlobalConfigKey.JBOLT_LOGIN_BGIMG_BLUR:
			boolean blur=CACHE.me.getUserJBoltLoginBgimgBlur(userId);
			setCookie("jbolt_login_bgimgBlur",blur+"" ,60*60*24*7*4);
			break;
		}
	}
	/**
	 * 更新Value
	 */
	@UnCheck
	public void changeStringValue(){
		renderJson(service.changeStringValue(getSessionAdminUserId(),getInt("id"),get("value")));
	}
}
