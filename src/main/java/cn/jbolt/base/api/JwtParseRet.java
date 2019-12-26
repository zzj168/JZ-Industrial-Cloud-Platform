package cn.jbolt.base.api;

import cn.jbolt.common.model.Application;
/**
 * JWT Parse result
 * @ClassName:  JwtParseRet   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月13日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JwtParseRet {
	private ApiUser apiUser;
	private Application application;
	private String msg;
	private boolean expired;
	private boolean signCheckFailed;
	
	
	public boolean isSignCheckFailed() {
		return signCheckFailed;
	}

	public JwtParseRet setSignCheckFailed(boolean signCheckFailed) {
		this.signCheckFailed = signCheckFailed;
		return this;
	}

	public boolean isExpired() {
		return expired;
	}

	public JwtParseRet setExpired(boolean expired) {
		this.expired = expired;
		return this;
	}
	public Integer getUserId() {
		if(isOk()) {
			return apiUser.getUserId();
		}
		return null;
	}
	public boolean isOk() {
		return apiUser!=null&&application!=null&&msg==null&&signCheckFailed==false;
	}
	 
	public String getMsg() {
		return msg;
	}

	public JwtParseRet setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public ApiUser getApiUser() {
		return apiUser;
	}
	public JwtParseRet setApiUser(ApiUser apiUser) {
		this.apiUser = apiUser;
		return this;
	}
	public Application getApplication() {
		return application;
	}
	public JwtParseRet setApplication(Application application) {
		this.application = application;
		return this;
	}
}
