package cn.jbolt.base.api;

import io.jsonwebtoken.Claims;

public class ApiUserBean implements ApiUser {
	private Integer userId;
	private String userName;
	public ApiUserBean() {}
	public ApiUserBean(Integer userId,String userName) {
		this.userId=userId;
		this.userName=userName;
	}
	public ApiUserBean(Claims claims) {
		this.userId=claims.get("userId", Integer.class);
		this.userName=claims.get("userName",String.class);
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public Integer getUserId() {
		return userId;
	}

	@Override
	public String getUserName() {
		return userName;
	}

}
