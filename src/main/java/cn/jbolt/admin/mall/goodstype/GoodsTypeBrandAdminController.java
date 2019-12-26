package cn.jbolt.admin.mall.goodstype;

import java.util.List;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.mall.brand.BrandService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.Brand;
/**
 * 商品类型关联的品牌表
 * @ClassName:  GoodsTypeBrandAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年7月11日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.MALL_GOODSTYPE)
public class GoodsTypeBrandAdminController extends BaseController {
	@Inject
	private BrandService brandService;
	@Inject
	private GoodsTypeBrandService service;
	public void index() {
		Integer goodsTypeId=getInt(0);
		List<Brand> choosedList=brandService.getBrandsByGoodsType(getInt(0));
		set("choosedList", choosedList);
		if(isOk(choosedList)) {
			set("brands", brandService.getBrandsWithoutGoodsType(getInt(0)));
		}else {
			set("brands", brandService.findAll());
		}
		set("goodsTypeId", goodsTypeId);
		render("index.html");
	}
	/**
	 * 清空关联品牌
	 */
	public void clearByType() {
		renderJson(service.clearByType(getInt(0)));
	}
	/**
	 * 提交关联数据
	 */
	public void submit() {
		renderJson(service.submit(getInt("goodsTypeId"),get("brandIds")));
	}
}
