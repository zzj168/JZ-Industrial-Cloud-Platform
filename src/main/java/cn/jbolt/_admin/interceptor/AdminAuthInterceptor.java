package cn.jbolt._admin.interceptor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt.base.BaseController;
import cn.jbolt.base.ControllerKit;
import cn.jbolt.common.model.Permission;
import cn.jbolt.common.util.CACHE;


/**
 * JBolt管理后台权限校验拦截器
 * @ClassName:  AdminAuthInterceptor   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月12日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class AdminAuthInterceptor implements Interceptor {
	@Override
	public void intercept(Invocation inv) {
		Controller ctl =inv.getController();
		if (!(ctl instanceof BaseController)) {
			throw new RuntimeException("控制器需要继承 BaseController");
		}
		BaseController controller=(BaseController) ctl;
		boolean isAdminLogin = controller.isAdminLogin();
		boolean isSystemAdmin = controller.isSystemAdmin();
		if (!isAdminLogin&&isSystemAdmin==false) {
			ControllerKit.renderInterceptorNotLoginInfo(controller);
			return;
		}
		//判断锁屏
		if(controller.systemIsLocked()) {
			if(inv.getActionKey().equals("/admin/unLockSystem")) {
				inv.invoke();
				return;
			}else {
				//直接访问action
				if(ControllerKit.isPageAction(controller)) {
					ControllerKit.renderSystemLockedPage(controller);
				}else {
					//ajax pjax ajaxPortal等访问
					ControllerKit.renderSystemLockedInfo(controller);
				}
				return;
			}
		}
		//uncheck是只校验上面的登录 不校验其它
		if(SecurityCheck.isUncheck(inv.getMethod())){
			inv.invoke();
			return;
		}
		//如果是超级管理员可以直接访问的权限 不校验其它
		if(SecurityCheck.isUncheckIfSystemAdmin(controller,inv.getMethod())) {
			inv.invoke();
			return;
		}
		//拿到登录用户所分配的角色
		String roleIds = controller.getSessionAdminRoleIds();
		//从cache中找到这些角色对应的权限绑定集合
		Set<String> permissionKeySet = CACHE.me.getRolePermissionKeySet(roleIds);
		if (permissionKeySet == null || permissionKeySet.isEmpty()) {
			// 如果没有权限 返回错误信息
			ControllerKit.renderInterceptorErrorInfo(controller,"尚未分配任何权限");
			return;
		}
		// 获取controllerKey 然后拿到本人的role对应的permissions
		String[] permissionKeys = getPermissionKeys(controller, inv.getMethod());
		if(permissionKeys==null||permissionKeys.length==0){
			// 如果没有权限 返回错误信息
			ControllerKit.renderInterceptorErrorInfo(controller,"开发未设置校验权限");
			return;
		}
		
		//如果是超管，判断是不是超管默认的，是的话就直接过
		if(isSystemAdmin) {
			boolean isSystemAdminDefault=SecurityCheck.checkIsSystemAdminDefaultPermission(false,permissionKeys);
			if(isSystemAdminDefault) {
				inv.invoke();
				return;
			}
			
		}
		
		//检测拦截到正在访问的controller+action上需要校验的权限资源 拿到后去跟缓存里当前用户所在的角色下的所有资源区对比
		boolean exist = SecurityCheck.checkHasPermission(false,permissionKeySet, permissionKeys);
		if (!exist) {
			// 如果没有权限 返回错误信息
			ControllerKit.renderInterceptorErrorInfo(controller,"无权访问");
			return;
		}
		// 最后执行action
		inv.invoke();

	}

	


	/**
	 * 得到需要校验的permissionKey
	 * @param controller
	 * @param method
	 * @return
	 */
	private String[] getPermissionKeys(BaseController controller, Method method) {
		boolean mc=SecurityCheck.isPermissionCheck(method);
		boolean cc=SecurityCheck.isPermissionCheck(controller);
		if(!mc&&!cc){
			return null;
		}
		String[] temps=null;
		if(mc){
			CheckPermission per = method.getAnnotation(CheckPermission.class);
			String[] values = per.value();
			if (values == null || values.length == 0) {
				return null;
			}
			temps=values;
		}
		if(cc&&temps==null){
			CheckPermission per = controller.getClass().getAnnotation(CheckPermission.class);
			String[] values = per.value();
			if (values == null || values.length == 0) {
				return null;
			}
			temps=values;
		}
		
		return temps;
	}
	/**
	 * 检测keys是否在用户携带权限里存在
	 * @param permissions
	 * @param keys
	 * @return
	 */
	private boolean checkAuth(List<Permission> permissions, String[] keys) {
		boolean success = false;
		for (String key : keys) {
			/*	if (key.equals(Permission.ONLYLOGIN)) {
				success = true;
				break;
			}*/
			for (Permission f : permissions) {
				if (StrKit.notBlank(f.getPermissionKey())&&f.getPermissionKey().equals(key)) {
					success = true;
					break;
				}
			}
		}
		return success;
	}
	

}
