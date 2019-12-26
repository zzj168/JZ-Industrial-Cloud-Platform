package cn.jbolt.apitest;

import com.jfinal.config.Routes;

import cn.jbolt.base.api.ApiInterceptor;

public class ApiTestRoutes extends Routes {

	@Override
	public void config() {
		this.addInterceptor(new ApiInterceptor());
		this.add("/test/api", ApiTestController.class);
	}

}
