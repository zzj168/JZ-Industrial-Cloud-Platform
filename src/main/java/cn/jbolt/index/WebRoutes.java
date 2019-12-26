package cn.jbolt.index;

import com.jfinal.config.Routes;

public class WebRoutes extends Routes {

	@Override
	public void config() {
		this.addInterceptor(new PjaxInterceptor());
		this.addInterceptor(new AjaxPortalInterceptor());
		this.setBaseViewPath("/_view/_web");
		this.add("/", IndexController.class);
	}

}
