
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
 * #role(1)或者#role(1,2,3)
 * 	...
 * #end
 */
public class RoleDirective extends Directive {

	
	public void exec(Env env, Scope scope, Writer writer) {
		Map session =(Map) scope.getRootData().get("session");
		Object adminuserId=session.get(SessionKey.ADMIN_USER_ID);
		if(adminuserId!=null){
			Integer userId=Integer.parseInt(adminuserId.toString());
			boolean success=UserAuthKit.hasRole(userId,false, getRoleArray(scope));
			if(success){
				stat.exec(env, scope, writer);
			}
		}
	}

	/**
	 * 从 #role 指令参数中获取角色数组
	 */
	private Integer[] getRoleArray(Scope scope) {
		Object[] values = exprList.evalExprList(scope);
		Integer[] ret = new Integer[values.length];
		for (int i=0; i<values.length; i++) {
			if (values[i] instanceof Integer) {
				ret[i] =Integer.parseInt( values[i].toString());
			} else {
				throw new IllegalArgumentException("角色只能为 int 类型");
			}
		}
		return ret;
	}

	public boolean hasEnd() {
		return true;
	}
}
