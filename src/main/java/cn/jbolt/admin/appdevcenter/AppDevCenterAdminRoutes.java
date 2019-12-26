package cn.jbolt.admin.appdevcenter;

import com.jfinal.config.Routes;

import cn.jbolt._admin.interceptor.AdminAuthInterceptor;
import cn.jbolt.index.AjaxPortalInterceptor;
import cn.jbolt.index.PjaxInterceptor;
/**
 * admin后台app dev center 应用开发中心 的路由配置
 * @ClassName:  AdminRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午12:25:20   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class AppDevCenterAdminRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_admin/_app_dev_center");
		this.addInterceptor(new PjaxInterceptor());
		this.addInterceptor(new AjaxPortalInterceptor());
		this.addInterceptor(new AdminAuthInterceptor());
		this.add("/admin/app", ApplicationAdminController.class,"/app");
	}

}
