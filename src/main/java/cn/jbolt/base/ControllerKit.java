package cn.jbolt.base;

import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;

import cn.jbolt.base.api.ApiBaseController;
import cn.jbolt.base.api.ApiTokenManger;
import cn.jbolt.base.api.ApiUser;
import cn.jbolt.common.config.SessionKey;
import cn.jbolt.common.model.Application;
/**
 * controller层公用工具类
 * @ClassName:  ControllerKit   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月17日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class ControllerKit {
	/**
	 * 成功返回值 默认
	 */
	private static final Ret JSON_SUCCESS=Ret.ok();
	/**
	 * AjaxPortal请求返回错误信息
	 * @param controller
	 * @param msg
	 */
	public static void renderErrorPortal(Controller controller,String msg) {
		setMsg(controller,msg);
		controller.render("/_view/_admin/common/msg/errorportal.html");
	}
	/**
	 * 判断是否是Ajax请求
	 * @return
	 */
	public static boolean isAjax(Controller controller){
		String xrequestedwith= controller.getRequest().getHeader("X-Requested-With");
		boolean isAjax = false;
		if(xrequestedwith==null||xrequestedwith.equalsIgnoreCase("XMLHttpRequest")==false){
			isAjax=false;
		}else if(xrequestedwith.equalsIgnoreCase("XMLHttpRequest")){
			isAjax=true;
		}
		return isAjax;
	}
	/**
	 * 判断请求是否是Pjax请求
	 * @return
	 */
	public static boolean isPjax(Controller controller){
		return controller.getAttr("isPjax",false);
	}
	 
	/**
	 * 判断请求是否是ajaxPortal请求
	 * @return
	 */
	public static boolean isAjaxPortal(Controller controller){
		return controller.getAttr("isAjaxPortal",false);
	}
	/**
	 * pjax请求返回错误信息片段
	 * @param controller
	 * @param msg
	 */
	public static void renderErrorPjax(Controller controller, String msg) {
		setMsg(controller,msg);
		controller.render("/_view/_admin/common/msg/errorpjax.html");
		
	}
	/**
	 * form提交跳转 表单跳转到错误信息显示
	 * @param controller
	 * @param msg
	 */
	public static void renderFormError(Controller controller, String msg) {
		setMsg(controller,msg);
		controller.render("/_view/_admin/common/msg/formerror.html");
	}
	/**
	  * 返回失败信息 json格式
	 * @param controller
	 * @param msg
	 */
	public static void renderJsonFail(Controller controller, String msg) {
		controller.renderJson(Ret.fail("msg", msg));
	}
	/**
	 * 拦截器层 根据request类型响应不同错误信息
	 * @param controller
	 * @param msg
	 */
	public static void renderInterceptorErrorInfo(Controller controller,String msg) {
		renderError(controller, msg);
	}
	/**
	 * 拦截器返回未登录错误信息
	 * @param controller
	 */
	public static void renderInterceptorNotLoginInfo(Controller controller) {
		if (isPjax(controller)) {
			renderErrorPjax(controller,"尚未登录");
		} else if (isAjaxPortal(controller)) {
			renderErrorPortal(controller,"尚未登录");
		} else if (isAjax(controller)) {
			renderJsonFail(controller,"尚未登录");
		} else {
			// 判断如果没有登录 需要跳转到登录页面
			toAdminLogin(controller);
		}
		
	}
	

	/**
	 * 跳转到登录页面
	 */
	public static void toAdminLogin(Controller controller) {
		controller.redirect("/admin/tologin");
	}
	/**
	 * 	返回Validator错误信息
	 * @param controller
	 */
	public static void renderValidatorError(Controller controller) {
		if(isPjax(controller)) {
			renderErrorPjax(controller,controller.getAttr("msg"));
		}else if(isAjaxPortal(controller)) {
			renderErrorPortal(controller,controller.getAttr("msg"));
		}else if(isAjax(controller)) {
			renderJsonFail(controller,controller.getAttr("msg"));
		}else {
			renderDialogError(controller,controller.getAttr("msg"));
		}
	}
	/**
	 * 设置msg信息
	 * @param controller
	 * @param msg
	 */
	public static void setMsg(Controller controller,String msg) {
		controller.set("msg", msg);
	}
	/**
	 * 
	 * @param controller
	 * @param msg
	 */
	public static void renderDialogError(Controller controller, String msg) {
		setMsg(controller,msg);
		controller.render("/_view/_admin/common/msg/formerror.html");
	}
	/**
	 * 返回404错误信息
	 * @param controller
	 */
	public static void render404Error(Controller controller) {
		String msg="404,您访问的资源不存在!";
		renderError(controller, msg);
	}
	/**
	 * render 锁屏消息
	 * @param controller
	 */
	public static void renderSystemLockedInfo(Controller controller) {
		String msg="jbolt_system_locked";
		renderError(controller, msg);
	}
	/**
	 * render 锁屏消息
	 * @param controller
	 */
	public static void renderSystemLockedPage(Controller controller) {
		controller.render("/_view/_admin/common/msg/systemlocked.html");
	}
	
	/**
	 * 设置锁屏
	 */
	public static void lockSystem(Controller controller) {
		controller.setSessionAttr(SessionKey.JBOLT_LOCK_SYSTEM, true);
		renderJsonSuccess(controller);
	}
	/**
	 * 设置屏幕解锁
	 */
	public static void unLockSystem(Controller controller) {
		controller.removeSessionAttr(SessionKey.JBOLT_LOCK_SYSTEM);
		renderJsonSuccess(controller);
	}
	
	/**
	 * render错误信息
	 * @param controller
	 * @param msg
	 */
	private static void renderError(Controller controller,String msg) {
		if (isPjax(controller)) {
			renderErrorPjax(controller,msg);
		} else if (isAjaxPortal(controller)) {
			renderErrorPortal(controller,msg);
		} else if (isAjax(controller)) {
			renderJsonFail(controller,msg);
		} else {
			renderFormError(controller,msg);
		}
	}
	/**
	 * 返回正确json result
	 * @param controller
	 */
	public static void renderJsonSuccess(Controller controller) {
		controller.renderJson(JSON_SUCCESS);
	}
	/**
	 * 返回正确json result 以及Msg
	 * @param controller
	 * @param msg
	 */
	public static void renderJsonSuccess(Controller controller,String msg) {
		controller.renderJson(Ret.ok().set("msg",msg));
	}
	
	/**
	 * 返回正确json result 并带着特殊数据
	 * @param controller
	 * @param data
	 */
	public static void renderJsonData(Controller controller, Object data) {
		controller.renderJson(Ret.ok("data",data));
	}
	/**
	 * 判断是直接访问action
	 * @param controller
	 * @return
	 */
	public static boolean isPageAction(BaseController controller) {
		return isAjax(controller)==false&&isAjaxPortal(controller)==false&&isPjax(controller)==false;
	}
	
}
