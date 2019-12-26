package cn.jbolt.apitest;

import cn.jbolt.base.api.ApiBaseController;
import cn.jbolt.base.api.ApplyApiToken;
/**
 * 测试API
 * @ClassName:  ApiTestController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月12日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class ApiTestController extends ApiBaseController {
	/**
	 * API测试
	 */
	public void go() {
		//进入这个方法 是需要拦截器校验request Header中的JWT信息的
		renderJsonSuccess("卧槽！API 测试通过");
	}
	
	/**
	 * API首次访问 签发JWT
	 */
	@ApplyApiToken
	public void login() {
		//模拟用户信息 这里可能是微信授权登录
		Integer userId=1;
		String userName="李四";
		
		//此方法有注解@ApplyApiToken 说明方法需要执行后设置当前APIUser
		setApplyTokenApiUser(userId,userName);
		
		renderJsonSuccess("登录成功："+getApplication().getName());
	}
	
}
