package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt._admin.interceptor.AdminAuthInterceptor;
import cn.jbolt.admin.mall.brand.BrandAdminController;
import cn.jbolt.admin.mall.goods.GoodsAdminController;
import cn.jbolt.admin.mall.goodscategory.back.GoodsBackCategoryAdminController;
import cn.jbolt.admin.mall.goodstype.GoodsTypeAdminController;
import cn.jbolt.admin.mall.goodstype.GoodsTypeBrandAdminController;
/**
 * admin后台 电商管理模块相关 的路由配置
 * @ClassName:  MallAdminRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午12:25:11   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class MallAdminRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_admin/_mall");
		this.addInterceptor(new PjaxInterceptor());
		this.addInterceptor(new AjaxPortalInterceptor());
		this.addInterceptor(new AdminAuthInterceptor());
		this.add("/admin/mall/goods", GoodsAdminController.class,"/goods");
		this.add("/admin/mall/goodstype", GoodsTypeAdminController.class,"/goodstype");
		this.add("/admin/mall/goodsbackcategory", GoodsBackCategoryAdminController.class,"/goodsbackcategory");
		this.add("/admin/mall/brand", BrandAdminController.class,"/brand");
		this.add("/admin/mall/goodstypebrand", GoodsTypeBrandAdminController.class,"/goodstypebrand");
	}

}
