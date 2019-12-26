package cn.jbolt.common.directive;

import java.util.Map;

import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;

import cn.jbolt._admin.user.UserAuthKit;
import cn.jbolt.common.config.SessionKey;

/**
 * 界面上的权限控制功能
 * 用来控制界面上的菜单、按钮等等元素的显示
 * #permission(cn.jbolt._admin.permission.PermissionKey::DASHBOARD)
 *    <a href="/admin/jboltversion/edit/1">审核</a>
 * #end
 */
public class PermissionDirective extends Directive {

	public void exec(Env env, Scope scope, Writer writer) {
		Map session =(Map) scope.getRootData().get("session");
		Object adminuserId=session.get(SessionKey.ADMIN_USER_ID);
		if(adminuserId!=null){
			Integer userId=Integer.parseInt(adminuserId.toString());
			boolean success=UserAuthKit.hasPermission(userId,true, getPermission(scope));
			if(success){
				stat.exec(env, scope, writer);
			}
		}
	}

	/**
	 * 从 #permission 指令参数中获取 permission
	 */
	private String getPermission(Scope scope) {
		Object value = exprList.eval(scope);
		if (value instanceof String) {
			return (String)value;
		} else {
			throw new IllegalArgumentException("权限参数只能为 String 类型");
		}
	}

	public boolean hasEnd() {
		return true;
	}
}