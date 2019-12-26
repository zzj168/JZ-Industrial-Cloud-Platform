package cn.jbolt.admin.mall.goodstype;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.GoodsType;
import cn.jbolt.common.model.GoodsTypeBrand;
import cn.jbolt.common.util.ArrayUtil;

/**  
 * 类型和品牌关联中间表管理Service 
 * @ClassName:  GoodsTypeBrandService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月13日 上午12:56:35   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class GoodsTypeBrandService extends BaseService<GoodsTypeBrand> {
	private GoodsTypeBrand dao = new GoodsTypeBrand().dao();
	@Inject
	private GoodsTypeService goodsTypeService;
	@Override
	protected GoodsTypeBrand dao() {
		return dao;
	}
	/**
	 * 检测品牌被使用情况
	 * @param brandId
	 * @return
	 */
	public boolean checkBrandInUse(Integer brandId) {
		return exists("brand_id", brandId);
	}
	/**
	 * 清空关联品牌
	 * @param goodsTypeId
	 * @return
	 */
	public Ret clearByType(Integer goodsTypeId) {
		if(MainConfig.DEMO_MODE) {return fail(Msg.DEMO_MODE_CAN_NOT_DELETE);}
		if(notOk(goodsTypeId)) {return fail(Msg.PARAM_ERROR);}
		GoodsType goodsType=goodsTypeService.findById(goodsTypeId);
		if(goodsType==null) {return fail("所属的商品类型不存在");}
		boolean success=Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				Ret ret=deleteBy(Kv.by("goods_type_id", goodsTypeId));
				if(ret.isFail()) {return false;}
				Ret upret=goodsTypeService.updateBrandCount(goodsTypeId);
				return upret.isOk();
			}
		});
		return ret(success);
	}
	/**
	 * 提交关联数据
	 * @param goodsTypeId
	 * @param brandIds
	 * @return
	 */
	public Ret submit(Integer goodsTypeId, String brandIds) {
		if(notOk(goodsTypeId)||notOk(brandIds)) {return fail(Msg.PARAM_ERROR);}
		Integer[] ids=ArrayUtil.toInt(brandIds, ",");
		if(ids==null||ids.length==0) {return fail(Msg.PARAM_ERROR);}
		//清空之前的
		Ret ret = clearByType(goodsTypeId);
		if(ret.isFail()) {return ret;}
		//添加新的
		List<GoodsTypeBrand> goodsTypeBrands=new ArrayList<GoodsTypeBrand>();
		for(Integer id:ids) {
			if(notOk(id)) {continue;}
			goodsTypeBrands.add(new GoodsTypeBrand().setGoodsTypeId(goodsTypeId).setBrandId(id));
		}
		//批量保存
		Db.batchSave(goodsTypeBrands, goodsTypeBrands.size());
		//更新brandCount统计
		ret=goodsTypeService.updateBrandCount(goodsTypeId);
		return ret;
	}
	/**
	  *   获取
	 * @param goodsTypeId
	 * @return
	 */
	public int getCountByGoodsType(Integer goodsTypeId) {
		return getCount(Kv.by("goods_type_id", goodsTypeId));
	}
	
	

}
