package cn.jbolt.base;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
/**
 * Force action no urlPara, otherwise render error 404.
 * @ClassName:  JBoltNoUrlPara   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月17日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JBoltNoUrlPara implements Interceptor {
	@Override
	public void intercept(Invocation inv) {
		Controller controller =inv.getController();
		if (controller.getPara() == null) {
			inv.invoke();
		} else {
			ControllerKit.render404Error(controller);
		}
	}
}