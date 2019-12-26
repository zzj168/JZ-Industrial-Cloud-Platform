package cn.jbolt.base.api;

import com.jfinal.core.NotAction;

import cn.jbolt.base.CommonController;
import cn.jbolt.common.model.Application;
import cn.jbolt.common.util.CACHE;
/**
 * 所有写接口的Controller 统一继承APIBaseController
 * @ClassName:  ApiBaseController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月13日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class ApiBaseController extends CommonController {
	
	/**
	  * 获得当前访问的Application
	 * @return
	 */
	@NotAction
	public Application getApplication() {
		JwtParseRet jwtParseRet=JBoltApiKit.getJwtParseRet();
		if(jwtParseRet==null) {
			String appId=JBoltApiKit.getAppId();
			if(notOk(appId)) {return null;}
			return CACHE.me.getApplicationByAppId(appId);
		}
		return jwtParseRet.getApplication();
	}
	/**
	  * 获得当前访问的appId
	 * @return
	 */
	protected String getAppId() {
		return JBoltApiKit.getAppId();
	}
	/**
	  * 判断接口请求是否已经准备好
	 * @return
	 */
	protected boolean requestIsOk() {
		String requestIsOk=getAttrForStr("requestIsOk");
		return isOk(requestIsOk)&&requestIsOk.equals("true");
	}
	/**
	 * 获得当前访问的userId
	 * @return
	 */
	protected Integer getApiUserId() {
		JwtParseRet jwtParseRet=JBoltApiKit.getJwtParseRet();
		return jwtParseRet==null?null:jwtParseRet.getUserId();
	}
	/**
	 * 获得当前访问的ApiUser
	 * @return
	 */
	protected ApiUser getApiUser() {
		JwtParseRet jwtParseRet=JBoltApiKit.getJwtParseRet();
		return jwtParseRet==null?null:jwtParseRet.getApiUser();
	}
	/**
	 * 设置当前请求中的APIUser
	 * @param userId
	 * @param userName
	 */
	protected void setApplyTokenApiUser(Integer userId,String userName) {
		set("jboltApplyTokenApiUser", new ApiUserBean(userId,userName));
	}
	
	/**
	 * 获得当前请求中的APIUser
	 */
	@NotAction
	public ApiUserBean getApplyTokenApiUser() {
		return getAttr("jboltApplyTokenApiUser");
	}
	
}
