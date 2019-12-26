package cn.jbolt.admin.mall.brand;

import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.admin.mall.goods.GoodsService;
import cn.jbolt.admin.mall.goodstype.GoodsTypeBrandService;
import cn.jbolt.admin.mall.goodstype.GoodsTypeService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Brand;
import cn.jbolt.common.model.SystemLog;

/**   
 * 品牌库管理
 * @ClassName:  BrandService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月12日 下午6:03:49   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class BrandService extends BaseService<Brand> {
	private Brand dao = new Brand().dao();
	@Inject
	private GoodsTypeService goodsTypeService;
	@Inject
	private GoodsTypeBrandService goodsTypeBrandService;
	@Inject
	private GoodsService goodsService;

	@Override
	protected Brand dao() {
		return dao;
	}
	
	/**
	 * 保存
	 * @param user
	 * @return
	 */
	public Ret save(Integer userId,Brand brand) {
		if(brand==null||isOk(brand.getId())||notOk(brand.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=brand.getName().trim();
		if (existsName(name)) {
			return fail(Msg.DATA_SAME_NAME_EXIST+"中文名:["+name+"]");
		}
		if (isOk(brand.getEnglishName())&&exists("english_name",name)) {
			return fail(Msg.DATA_SAME_NAME_EXIST+"英文名:["+name+"]");
		}
		brand.setName(name);
		if(isOk(brand.getEnglishName())){
			brand.setEnglishName(brand.getEnglishName().trim());
		}
		if(notOk(brand.getEnable())){
			brand.setEnable(false);
		}
		brand.setSortRank(getNextSortRank());
		boolean success=brand.save();
		if(success){
			//添加日志
			addSaveSystemLog(brand.getId(), userId, SystemLog.TARGETTYPE_MALL_BRAND, brand.getName());
		}
		return ret(success);
	}

	/**
	 * 更新
	 * @param user
	 * @return
	 */
	public Ret update(Integer userId,Brand brand) {
		if(brand==null||notOk(brand.getId())||notOk(brand.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=brand.getName().trim();
		if (existsName(name,brand.getId())) {
			return fail(Msg.DATA_SAME_NAME_EXIST+"中文名:["+name+"]");
		}
		if (isOk(brand.getEnglishName())&&exists("english_name",name,brand.getId())) {
			return fail(Msg.DATA_SAME_NAME_EXIST+"英文名:["+name+"]");
		}
		brand.setName(name);
		if(isOk(brand.getEnglishName())){
			brand.setEnglishName(brand.getEnglishName());
		}
		if(notOk(brand.getEnable())){
			brand.setEnable(false);
		}
		boolean success=brand.update();
		if(success){
			//添加日志
			addUpdateSystemLog(brand.getId(), userId, SystemLog.TARGETTYPE_MALL_BRAND, brand.getName());
		}
		return ret(success);
	}
	/**
	 * 删除
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret delete(Integer userId,Integer id) {
		Ret ret=deleteById(id, true);
		if(ret.isOk()){
			//添加日志
			Brand brand=ret.getAs("data");
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_MALL_BRAND, brand.getName());
		}
		return ret;
	}

	@Override
	public String checkInUse(Brand brand) {
		//被类型使用
		boolean useByGoodsType=goodsTypeBrandService.checkBrandInUse(brand.getId());
		if(useByGoodsType){return "此品牌已经在商品类型中被关联使用,不能删除";}
		boolean useByGoods=goodsService.checkBrandInUse(brand.getId());
		if(useByGoods){return "此品牌已经在商品中被关联使用,不能删除";}
		return null;
	}
	/**
	 * 切换启用禁用状态
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret toggleEnable(Integer userId, Integer id) {
		Ret ret=toggleBoolean(id, "enable");
		if(ret.isOk()){
			//添加日志
			Brand brand=ret.getAs("data");
			addUpdateSystemLog(id, userId, SystemLog.TARGETTYPE_MALL_BRAND, brand.getName(),"的启用状态:"+brand.getEnable());
		}
		return ret;
	}
	/**
	 * 后台管理list查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<Brand> paginateAdminList(int pageNumber, int pageSize, String keywords) {
		if(notOk(keywords)) {
			return paginate(pageNumber, pageSize);
		}
		return paginateBySqlTemplate("mall.brand.paginateAdminList", Kv.by("keywords", keywords), pageNumber, pageSize);
	}

	/**
	 * 根据商品类型ID获取关联的品牌列表
	 * @param goodsTypeId
	 * @return
	 */
	public List<Brand> getBrandsByGoodsType(Integer goodsTypeId) {
		return daoTemplate("mall.brand.getBrandsByGoodsType", Kv.by("goodsTypeId", goodsTypeId)).find();
	}
	/**
	 * 获取尚未归属指定分类的品牌
	 * @param goodsTypeId
	 * @return
	 */
	public List<Brand> getBrandsWithoutGoodsType(Integer goodsTypeId) {
		return daoTemplate("mall.brand.getBrandsWithoutGoodsType", Kv.by("goodsTypeId", goodsTypeId)).find();
	}
	

}
