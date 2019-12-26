package cn.jbolt.base.api;

import java.lang.reflect.Method;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

import cn.jbolt.common.model.Application;
import cn.jbolt.common.util.CACHE;

/**
 * API接口专用拦截器，用于API Token校验鉴权等
 * 
 * @ClassName: ApiInterceptor
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2019年9月12日
 * 
 *        注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class ApiInterceptor implements Interceptor {
	/**
	 * 第三方应用调用接口携带Header中的APPID的key
	 */
	private static final String APPID_KEY="jbolt_appId";

	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		if (!(controller instanceof ApiBaseController)) {
			throw new RuntimeException("控制器需要继承 ApiBaseController");
		}
		
		//设置AppId进入ThreadlLocal
		String appId=controller.getHeader(APPID_KEY);
		if(StrKit.isBlank(appId)) {
			controller.renderJson(Ret.fail("msg", "请在Request的Header中设置[jbolt_appId]"));
			return;
		}
		Application application=CACHE.me.getApplicationByAppId(appId);
		if(application==null) {
    		controller.renderJson(Ret.fail("msg", "请求的Application不存在:["+appId+"]"));
    		return;
    	}
    	if(application.getEnable()==false) {
    		controller.renderJson(Ret.fail("msg", "请求的Application未开放:["+application.getName()+":"+appId+"]"));
    		return;
    	}
    	Method actionMethod=inv.getMethod();
		JBoltApiKit.setAppId(appId);
		//判断如果action上带着ApiOpen 说明是公开接口
		if(actionMethod.isAnnotationPresent(OpenAPi.class)) {
			inv.invoke();
			JBoltApiKit.removeAppId();
			return;
	    }
		//判断如果action上带着ApplyApiToken
		if(actionMethod.isAnnotationPresent(ApplyApiToken.class)) {
			inv.invoke();
			ApiTokenManger.me().createJBoltApiTokenToResponse((ApiBaseController)controller);
			JBoltApiKit.removeAppId();
			return;
	    }
		
		//如果没有带着ApplyApiToken 说明这是一个需要接口鉴权JWT的请求
		try {
			JwtParseRet jwtParseRet = ApiTokenManger.me().getJwtParseRet(controller);
			if (jwtParseRet.isOk()) {
				JBoltApiKit.setJwtParseRet(jwtParseRet);
				inv.invoke();
			} else if (jwtParseRet.isSignCheckFailed()) {
				System.out.println("jbolt_jwt_signature校验失败");
				controller.renderJson(Ret.fail("msg","jbolt_jwt_signature_check_failed"));
			} else if (jwtParseRet.isExpired()) {
				System.out.println("检测JWT已过期 "+jwtParseRet.getMsg());
				// 过期的处理
				controller.renderJson(Ret.fail("msg","jbolt_jwt_is_expired"));
			} else {
				System.out.println(jwtParseRet.getMsg());
				controller.renderJson(Ret.fail("msg", jwtParseRet.getMsg()));
			}
			return;
		} finally {
			JBoltApiKit.clear();
		}

	}

}
