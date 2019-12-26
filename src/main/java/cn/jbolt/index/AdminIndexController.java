package cn.jbolt.index;

import javax.servlet.http.HttpSession;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;

import cn.jbolt._admin.globalconfig.GlobalConfigService;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt._admin.updatemgr.DownloadLogService;
import cn.jbolt._admin.user.UserService;
import cn.jbolt.base.BaseController;
import cn.jbolt.base.ControllerKit;
import cn.jbolt.base.JBoltNoUrlPara;
import cn.jbolt.common.config.GlobalConfigKey;
import cn.jbolt.common.config.SessionKey;
import cn.jbolt.common.model.User;
import cn.jbolt.common.util.CACHE;

public class AdminIndexController extends BaseController {
	@Inject
	private UserService userService;
	@Inject
	private DownloadLogService downloadLogService;
	@Inject
	private GlobalConfigService globalConfigService;
	@UnCheck
	@Before(JBoltNoUrlPara.class)
	public void index(){
		render("index.html");
	}
	@UnCheck
	public void menu(){
		set("leftMenus", CACHE.me.getRoleMenus(getSessionAdminRoleIds()));
		render("menu.html");
	}
	@UnCheck
	public void lockSystem(){
		ControllerKit.lockSystem(this);
	}
	@UnCheck
	public void unLockSystem(){
		String password=get("password");
		if(notOk(password)) {
			renderJsonFail("请输入登录密码");
		}else {
			boolean success=userService.checkPwd(getSessionAdminUserId(),password);
			if(success) {
				ControllerKit.unLockSystem(this);
			}else {
				renderJsonFail("密码不正确");
			}
		}
		
	}
	
	@CheckPermission("dashboard")
	@UnCheckIfSystemAdmin
	public void dashboard(){
		render("dashboard.html");
	}
	@Clear
	public void tologin(){
		render("login.html");
	}
	@Clear
	public void logout(){
		HttpSession session=getSession();
		session.removeAttribute(SessionKey.ADMIN_USER_ID);
		session.invalidate();
		render("login.html");
	}
	/**
	 * 登录
	 */
	@Clear
	public void login(){
		//根据全局配置判断是否需要验证码 默认需要
		boolean checkCaptcha=CACHE.me.isJBoltLoginUseCapture();
		if(checkCaptcha){
			boolean checkSuccess=validateCaptcha("captcha");
			if(!checkSuccess) {
				renderJsonFail("验证码输入错误");
				return;
			}
		}
		
		User user=userService.getUser(get("username"),get("password"));
		if(user==null){
			renderJsonFail("用户名或密码不正确");
		}else if(user.getEnable()==null||user.getEnable()==false){
			renderJsonFail("用户已被禁用");
		}else if(notOk(user.getRoles())&&user.getIsSystemAdmin()==false){
			renderJsonFail("用户未设置可登录权限");
		}else{
			setSessionAdminUserId(user.getId());
			resetUserConfigCookie(user.getId());
			renderJsonSuccess();
		}
	}
	/**
	 * 登录后重置登录页面用户设置cookie
	 * @param userId
	 */
	private void resetUserConfigCookie(Integer userId) {
		boolean glass=CACHE.me.getUserJBoltLoginFormStyleGlass(userId);
		setCookie("jbolt_login_glassStyle",glass+"",60*60*24*7);
		boolean blur=CACHE.me.getUserJBoltLoginBgimgBlur(userId);
		setCookie("jbolt_login_bgimgBlur",blur+"" ,60*60*24*7);
	}
	
	/**
	 * 验证码
	 */
	@Clear
	public void captcha(){
		renderJBoltCaptcha(CACHE.me.getGlobalConfigValue(GlobalConfigKey.JBOLT_LOGIN_CAPTURE_TYPE));
	}
}
