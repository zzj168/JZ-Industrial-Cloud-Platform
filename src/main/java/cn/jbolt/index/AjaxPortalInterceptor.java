package cn.jbolt.index;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * 设置 isAjaxPortal 标志
 */
public class AjaxPortalInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		c.set("isAjaxPortal","true".equalsIgnoreCase(c.getHeader("AJAX-PORTAL")));
		inv.invoke();
	}
}