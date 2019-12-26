package cn.jbolt.index;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * 设置 pjax 标志
 */
public class PjaxInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		c.set("isPjax","true".equalsIgnoreCase(c.getHeader("X-PJAX")));
		inv.invoke();
	}
}