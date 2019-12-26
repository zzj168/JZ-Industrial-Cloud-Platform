package cn.jbolt.base;

import com.jfinal.core.NotAction;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;

import cn.jbolt.common.config.SessionKey;
import cn.jbolt.common.model.User;
import cn.jbolt.common.util.CACHE;
/**
 * Controller层基础封装 基于CommonController
 * @ClassName:  BaseController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年8月8日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class BaseController extends CommonController {
	
	/**
	 * dialog里请求页面 返回错误信息
	 * @param msg
	 */
	@NotAction
	public void renderDialogError(String msg) {
		ControllerKit.renderDialogError(this,msg);
	}
	/**
	 * dialog里请求页面返回错误信息
	 * @param ret
	 */
	@NotAction
	public void renderDialogErrorRet(Ret ret) {
		renderDialogError(ret.getStr("msg"));
	}
	/**
	 * 加载或者提交form表单 返回错误信息 与Dialog错误信息目前一致
	 * @param msg
	 */
	@NotAction
	public void renderFormError(String msg) {
		ControllerKit.renderFormError(this,msg);
	}
	@NotAction
	public void renderFormErrorRet(Ret ret) {
		renderFormError(ret.getStr("msg"));
	}
	@NotAction
	public void renderErrorPjax(String msg) {
		ControllerKit.renderErrorPjax(this, msg);
	}
	@NotAction
	public void renderErrorPjax(String msg,String backUrl) {
		setMsg(msg);
		set("backUrl", backUrl);
		render("/_view/_admin/common/msg/errorpjax.html");
	}
	protected void renderErrorPortal(String msg) {
		ControllerKit.renderErrorPortal(this,msg);
	}
	protected void renderSuccessPjax(String msg) {
		setMsg(msg);
		render("/_view/_admin/common/msg/successpjax.html");
	}
	protected void renderPjaxRet(Ret ret) {
		if(ret.isOk()){
			renderSuccessPjax(ret.getStr("msg"));
		}else{
			renderErrorPjax(ret.getStr("msg"));
		}
	}
	/**
	 *返回上传文件的成功消息
	 * 使用Bootstrap-fileinput组件
	 * @param msg
	 */
	protected void renderBootFileUploadSuccess(String msg) {
		renderJson(Kv.by("success", msg));
	}
	/**
	 *返回上传文件的失败消息
	 * 使用Bootstrap-fileinput组件
	 * @param msg
	 */
	protected void renderBootFileUploadError(String msg) {
		 renderJson(Kv.by("error", msg));
	}
  
	
	/**
	 * 得到后台管理用户登录session UserId
	 * @return
	 */
	@NotAction
	public Integer getSessionAdminUserId(){
		return getSessionAttr(SessionKey.ADMIN_USER_ID);
	}
	/**
	 * 得到后台管理用户登录session roleId
	 * @return
	 */
	@NotAction
	public String getSessionAdminRoleIds(){
		Integer userId=getSessionAdminUserId();
		if(notOk(userId)){return null;}
		User user=CACHE.me.getUser(userId);
		if(user==null){return null;}
		return user.getRoles();
	}
	/**
	 * 设置后台管理用户登录session userId
	 * @return
	 */
	protected void setSessionAdminUserId(Integer userId){
		setSessionAttr(SessionKey.ADMIN_USER_ID,userId);
	}
	
	/**
	 * 得到后台管理用户是否登录
	 * @return
	 */
	@NotAction
	public boolean isAdminLogin(){
		return isOk(getSessionAdminUserId())&&isOk(getSessionAdminRoleIds());
	}
	/**
	 * 得到后台管理用户是否是超级管理员
	 * @return
	 */
	@NotAction
	public boolean isSystemAdmin(){
		User user=CACHE.me.getUser(getSessionAdminUserId());
		return user==null?false:user.getIsSystemAdmin();
	}
	
	/**
	 * 判断是否用户锁屏
	 * @return
	 */
	@NotAction
	public boolean systemIsLocked() {
		Object obj=getSessionAttr(SessionKey.JBOLT_LOCK_SYSTEM);
		if(obj==null){
			return false;
		}
		return obj.toString().equals("true");
	}
	
}
