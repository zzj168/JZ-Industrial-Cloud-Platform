package cn.jbolt.admin.mall.goodstype;

import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.admin.mall.goods.GoodsService;
import cn.jbolt.admin.mall.goodscategory.back.GoodsBackCategoryService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.GoodsType;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.PinYinUtil;

/**   
 * 商品分类管理Service
 * @ClassName:  GoodsTypeService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午1:01:46   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class GoodsTypeService extends BaseService<GoodsType> {
	private GoodsType dao = new GoodsType().dao();
	@Inject
	private GoodsService goodsService;
	@Inject
	private GoodsBackCategoryService goodsBackCategoryService;
	@Inject
	private GoodsTypeBrandService goodsTypeBrandService;
	@Override
	protected GoodsType dao() {
		return dao;
	}
	/**
	 * 保存
	 * @param userId
	 * @param goodsType
	 * @return
	 */
	public Ret save(Integer userId, GoodsType goodsType) {
		if(goodsType==null||isOk(goodsType.getId())||notOk(goodsType.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=goodsType.getName();
		if(name.indexOf(" ")!=-1){
			return saveAll(userId,ArrayUtil.from3(name, " "));
		}
		
		if(existsName(name)){
			return fail(Msg.DATA_SAME_NAME_EXIST+":["+name+"]");
		}
		goodsType.setName(name);
		goodsType.setEnable(true);
		goodsType.setSortRank(getNextSortRank());
		goodsType.setAttrCount(0);
		goodsType.setBrandCount(0);
		goodsType.setSpecCount(0);
		goodsType.setPinyin(PinYinUtil.getFirstSpell(name));
		boolean success=goodsType.save();
		if(success){
			//添加日志
			addSaveSystemLog(goodsType.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS_TYPE, goodsType.getName());
		}
		return ret(success);
	}
	/**
	 * 添加多个
	 * @param userId
	 * @param names
	 * @return
	 */
	private Ret saveAll(Integer userId, String[] names) {
		Ret ret=null; 
		for(String name:names){
			ret=save(userId, new GoodsType().setName(name));
			if(ret.isFail()){
				return ret;
			}
		 }
		return SUCCESS;
	}
	/**
	 * 修改
	 * @param userId
	 * @param goodsType
	 * @return
	 */
	public Ret update(Integer userId, GoodsType goodsType) {
		if(goodsType==null||notOk(goodsType.getId())||notOk(goodsType.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=goodsType.getName().trim();
		if(existsName(name,goodsType.getId())){
			return fail(Msg.DATA_SAME_NAME_EXIST+":["+name+"]");
		}
		goodsType.setName(name);
		goodsType.setPinyin(PinYinUtil.getFirstSpell(name));
		boolean success=goodsType.update();
		if(success){
			//添加日志
			Boolean sort=goodsType.getBoolean("sort");
			if(sort!=null){
				addUpdateSystemLog(goodsType.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS_TYPE, goodsType.getName(),"的顺序");
			}else{
				addUpdateSystemLog(goodsType.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS_TYPE, goodsType.getName());
			}
			
		}
		return ret(success);
	}
	/**
	 * 修改
	 * @param userId
	 * @param goodsType
	 * @return
	 */
	public Ret delete(Integer userId, Integer id) {
		Ret ret=deleteById(id, true);
		if(ret.isOk()){
			GoodsType goodsType=ret.getAs("data");
			//删除后更新后面数据的顺序
			updateSortRankAfterDelete(goodsType.getSortRank());
			//添加日志
			addDeleteSystemLog(goodsType.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS_TYPE, goodsType.getName());
		}
		return ret;
	}
 
	/**
	 * 切换禁用启用状态
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret toggleEnable(Integer userId, Integer id) {
		Ret ret=toggleBoolean(id, "enable");
		if(ret.isOk()){
			//添加日志
			GoodsType goodsType=ret.getAs("data");
			addUpdateSystemLog(goodsType.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS_TYPE, goodsType.getName(),"的启用状态:"+goodsType.getEnable());
		}
		return ret;
	}
	/**
	 * 检测是否可以切换状态
	 */
	@Override
	public String checkCanToggle(Kv kv,GoodsType goodsType, String column) {
		return checkInUse(goodsType);
	}
	/**
	 * 上移
	 * @param id
	 * @return
	 */
	public Ret doUp(Integer userId,Integer id) {
		GoodsType goodsType=findById(id);
		if(goodsType==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=goodsType.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		if(rank==1){
			return fail("已经是第一个");
		}
		GoodsType upGoodsType=findFirst(Kv.by("sort_rank", rank-1));
		if(upGoodsType==null){
			return fail("顺序需要初始化");
		}
		upGoodsType.setSortRank(rank);
		goodsType.setSortRank(rank-1);
		
		upGoodsType.put("sort", true);
		goodsType.put("sort", true);
		
		update(userId,upGoodsType);
		update(userId,goodsType);
		return SUCCESS;
	}
 
	
	
	/**
	 * 下移
	 * @param id
	 * @return
	 */
	public Ret doDown(Integer userId,Integer id) {
		GoodsType goodsType=findById(id);
		if(goodsType==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=goodsType.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		int max=getCount();
		if(rank==max){
			return fail("已经是最后已一个");
		}
		GoodsType upGoodsType=findFirst(Kv.by("sort_rank", rank+1));
		if(upGoodsType==null){
			return fail("顺序需要初始化");
		}
		upGoodsType.setSortRank(rank);
		goodsType.setSortRank(rank+1);
		upGoodsType.put("sort", true);
		goodsType.put("sort", true);
		
		update(userId,upGoodsType);
		update(userId,goodsType);
		return SUCCESS;
	}
	
	
	/**
	 * 初始化排序
	 */
	public Ret doInitRank(Integer userId){
		List<GoodsType> allList=findAll();
		if(allList.size()>0){
			for(int i=0;i<allList.size();i++){
				allList.get(i).setSortRank(i+1);
				allList.get(i).deleteIdCache();
			}
			Db.batchUpdate(allList, allList.size());
		}
		addUpdateSystemLog(null, userId, SystemLog.TARGETTYPE_MALL_GOODS_TYPE, "所有类型", "的顺序:初始化所有");
		return SUCCESS;
		
	}
	
	
	@Override
	public String checkInUse(GoodsType goodsType) {
		boolean categoryUse=goodsBackCategoryService.checkTypeInUse(goodsType.getId());
		if(categoryUse){
			return "此商品类型下已经存在商品类目数据";
		}
		
		boolean goodsUse=goodsService.checkTypeInUse(goodsType.getId());
		if(goodsUse){
			return "此商品类型下已经存在商品数据";
		}
		return null;
	}
	/**
	 * 更新BrandCount
	 * @param goodsTypeId
	 */
	public Ret updateBrandCount(Integer goodsTypeId) {
		return updateTheCount(goodsTypeId, "brand_count");
	}
	/**
	 * 更新AttrCount
	 * @param goodsTypeId
	 */
	public Ret updateAttrCount(Integer goodsTypeId) {
		return updateTheCount(goodsTypeId, "attr_count");
	}
	/**
	 * 更新ApecCount
	 * @param goodsTypeId
	 */
	public Ret updateSpecCount(Integer goodsTypeId) {
		return updateTheCount(goodsTypeId, "spec_count");
	}
	/**
	 * 更新指定count
	 * @param goodsTypeId
	 * @param field
	 * @param count
	 */
	public Ret updateTheCount(Integer goodsTypeId,String field) {
		if(notOk(goodsTypeId)) {return fail(Msg.PARAM_ERROR);}
		GoodsType goodsType=findById(goodsTypeId);
		if(goodsType==null) {return fail(Msg.DATA_NOT_EXIST);}
		int count=goodsTypeBrandService.getCountByGoodsType(goodsTypeId);
		goodsType.set(field, count);
		boolean success=goodsType.update();
		return ret(success);
	}
	/**
	  *   后台查询管理列表
	 * @param keywords
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<GoodsType> paginateAdminList(String keywords, Integer pageNumber, Integer pageSize) {
		if(notOk(keywords)) {
			return paginate("sort_rank",pageNumber, pageSize);
		}
		return paginateBySqlTemplate("mall.goodstype.paginateAdminList", Kv.by("keywords", keywords), pageNumber, pageSize);
	}
	
	/**
	 * 检测并初始化拼音
	 */
	public void checkAndInitPinyin() {
		List<GoodsType> goodsTypes=getCommonList(Kv.by("pinyin", "is null"), true);
		if(isOk(goodsTypes)) {
			for(GoodsType goodsType:goodsTypes) {
				goodsType.setPinyin(PinYinUtil.getFirstSpell(goodsType.getName()));
			}
			Db.batchUpdate(goodsTypes, goodsTypes.size());
		}
		
	}

}
