package cn.jbolt.admin.mall.goods;

import java.math.BigDecimal;
import java.util.Date;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.admin.mall.goodscategory.back.GoodsBackCategoryService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Goods;
import cn.jbolt.common.model.GoodsBackCategory;
import cn.jbolt.common.model.GoodsHtmlContent;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.CACHE;
/**
 * 商品管理Service
 * @ClassName:  GoodsService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月23日 下午12:47:30   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class GoodsService extends BaseService<Goods> {
	private Goods dao = new Goods().dao();
	@Inject
	private GoodsHtmlContentService goodsHtmlContentService;
	@Inject
	private GoodsElementContentService goodsElementContentService;
	@Inject
	private GoodsBackCategoryService  goodsBackCategoryService;
	@Override
	protected Goods dao() {
		return dao;
	}
	/**
	 * 后台管理分页查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @param onSale
	 * @return
	 */
	public Page<Goods> paginateAdminData(int pageNumber, int pageSize, String keywords,Boolean onSale,Boolean isHot,Boolean isRecommend,Boolean isDelete,Integer bcategoryId,Integer fcategoryId) {
		Kv paras=Kv.by("table",table());
		paras.setIfNotBlank("keywords",keywords);
		paras.setIfNotNull("onSale",onSale);
		paras.setIfNotNull("isHot",isHot);
		paras.setIfNotNull("isRecommend",isRecommend);
		paras.setIfNotNull("isDelete",isDelete);
		paras.setIfNotNull("bcategoryId",bcategoryId);
		paras.setIfNotNull("fcategoryId",fcategoryId);
		return daoTemplate("mall.goods.paginateAdminList", paras).paginate(pageNumber, pageSize);
	}
	/**
	 * 获取一个商品 并带着content表数据 属性 规格等信息
	 * @param goodsId
	 * @return
	 */
	public Goods getGoodsWithDetail(Integer goodsId){
		Goods goods=findById(goodsId);
		if(goods!=null){
			processGoodsContent(goods);
		}
		return goods;
	}
	/**
	 * 处理Goods 的 content
	 * @param goods
	 */
	public void processGoodsContent(Goods goods) {
		switch (goods.getContentType().intValue()) {
		case Goods.CONTENT_TYPE_HTML:
			goods.put("htmlContent",goodsHtmlContentService.getWithAutoCreate(goods.getId()));
			break;
		case Goods.CONTENT_TYPE_ELEMENT:
			goods.put("elementContents", goodsElementContentService.getGoodsElementContents(goods.getId()));
			break;
		}
		
	}
	
	/**
	 * 检测商品类型在商品中已经使用
	 * @param typeId
	 * @return
	 */
	public boolean checkTypeInUse(Integer typeId) {
		return exists("type_id", typeId);
	}
	
	 
	/**
	 * 在进入添加页面时主动创建一个空商品
	 * @param userId
	 * @param categoryId 
	 * @return
	 */
	public Ret createTempGoods(Integer userId, Integer categoryId) {
		if(notOk(categoryId)){
			return fail(Msg.FAIL_NEED_CATEGORY);
		}
		GoodsBackCategory goodsCategory=goodsBackCategoryService.findById(categoryId);
		if(goodsCategory==null){
			return fail(Msg.FAIL_NEED_CATEGORY);
		}
		
			Goods goods=new Goods();
			Date now=new Date();
			goods.setBcategoryId(categoryId);
			goods.setBcategoryKey(goodsCategory.getCategoryKey());
			goods.setCreateUserId(userId);
			goods.setCreateTime(now);
			goods.setUpdateUserId(userId);
			goods.setUpdateTime(now);
			goods.setContentType(Goods.CONTENT_TYPE_HTML);
			goods.setIsGuarantee(true);
			goods.setIsHot(false);
			goods.setIsMultispec(false);
			goods.setIsProvideInvoices(false);
			goods.setIsRecommend(false);
			goods.setLimitCount(0);
			goods.setIsDelete(false);
			goods.setOnSale(false);
			goods.setOriginalPrice(new BigDecimal("0"));
			goods.setPrice(new BigDecimal("0"));
			goods.setRealSaleCount(0);
			goods.setShowSaleCount(0);
			goods.setTypeId(0);
			goods.setName("新商品");
			
			
			boolean success=goods.save();
			if(success){
				goods.setName("新商品_"+goods.getId());
				success=goods.update();
				if(success){
					return success(goods.getId(),Msg.SUCCESS);
				}
			}
			return FAIL;
	}
	
/*	*//**
	 * 保存一个商品
	 * @param userId
	 * @param goods
	 * @return
	 *//*
	public Ret save(Integer userId,Goods goods){
		if(goods==null||isOk(goods.getId())||notOk(goods.getName())){return fail(Msg.PARAM_ERROR);}
		
		boolean success=goods.save();
		return ret(success);
	}*/
	
	/**
	 * 切换上下架
	 * @param userId
	 * @param id 
	 * @return
	 */
	public Ret toggleOnSale(Integer userId, Integer id) {
		Ret ret=toggleBoolean(Kv.by("userId", userId),id, "on_sale");
		if(ret.isOk()){
			Goods goods=ret.getAs("data");
			//添加日志
			addUpdateSystemLog(id, userId, SystemLog.TARGETTYPE_MALL_GOODS, goods.getName(), "[上下架状态]:"+(goods.getOnSale()?"上架":"下架"));
		}
		return ret;
	}
	
	 
	
	/**
	 * 执行toggleBoolen后 额外可能需要操作的事情处理
	 */
	@Override
	public String toggleExtra(Kv kv,Goods goods, String column) {
		Date now=new Date();
		Integer userId=kv.getInt("userId");
		goods.setUpdateTime(now);
		goods.setUpdateUserId(userId);
		//处理切换上下架状态
		if(column.equals("on_sale")){
			goods.setIsHot(false);
			goods.setIsRecommend(false);
			if(goods.getOnSale()){
				goods.setOnSaleTime(now);
				goods.setOnSaleUserId(userId);
			}else{
				goods.setUnderTime(now);
				goods.setUnderUserId(userId);
			}
		}
		return null;
	 
	}
	/**
	 * 切换热销
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret toggleIsHot(Integer userId, Integer id) {
		return toggleBoolean(Kv.by("userId", userId),id, "is_hot");
	}
	/**
	 * 切换推荐
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret toggleIsRecommend(Integer userId, Integer id) {
		return toggleBoolean(Kv.by("userId", userId),id, "is_recommend");
	}
	@Override
	public String checkCanToggle(Kv kv,Goods goods, String column) {
		if(goods.getIsDelete()!=null&&goods.getIsDelete()){
			return "已经进入回收站的商品，不能切换此属性";
		}
		if(column.equals("is_recommend")||column.equals("is_hot")){
			if(goods.getOnSale()!=null&&goods.getOnSale()==false){
				return "商品未上架，不能切换此属性";
			}
		}
		return null;
	}
	/**
	 * 商品
	 * @param id
	 * @return
	 */
	public Ret deleteGoods(Integer userId, Integer id) {
		if(MainConfig.DEMO_MODE) {return fail(Msg.DEMO_MODE_CAN_NOT_DELETE);}
		if(notOk(id)){return fail(Msg.PARAM_ERROR);}
		Goods goods=findById(id);
		if(goods==null){return fail(Msg.DATA_NOT_EXIST);}
		if(goods.getOnSale()){return fail("商品已上架，不能删除;下架此商品后可以删除。");}
		goods.setIsDelete(true);
		goods.setOnSale(false);
		goods.setIsHot(false);
		goods.setIsRecommend(false);
		boolean success=goods.update();
		if(success){
			//添加日志
			addUpdateSystemLog(id, userId, SystemLog.TARGETTYPE_MALL_GOODS, goods.getName(),",已将其[移到回收站]");
		}
		return ret(success);
	}
	
	/**
	 * 更新商品 基本信息
	 * @param userId
	 * @param goods
	 * @return
	 */
	public Ret updateBaseInfo(Integer userId, Goods goods) {
		if(goods==null||notOk(goods.getId())||notOk(goods.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		return updateGoodsInfo(userId,goods,"[基本信息]");
	}
	
	

	/**
	 * 得到回收站内商品数量
	 * @return
	 */
	public int getDeleteCount() {
		return getCount(Kv.by("is_delete", TRUE()));
	}
	
	
	/**
	 * 恢复回收站内的商品
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret restoreGoods(Integer userId, Integer id) {
		if(notOk(id)){return fail(Msg.PARAM_ERROR);}
		Goods goods=findById(id);
		if(goods==null){return fail(Msg.DATA_NOT_EXIST);}
		goods.setIsDelete(false);
		boolean success=goods.update();
		if(success){
			//添加日志
			addUpdateSystemLog(id, userId, SystemLog.TARGETTYPE_MALL_GOODS, goods.getName(),",已将其[从回收站内恢复]");
		}
		return ret(success);
	}
	
	/**
	 * 更新商品图片信息
	 * @param userId
	 * @param goods
	 * @return
	 */
	public Ret updateImages(Integer userId, Goods goods) {
		if(goods!=null&&notOk(goods.getMainImage())){
			return fail("请上传商品主图 ");
		}
		return updateGoodsInfo(userId,goods,"[图片信息]");
	}
	
	
	
	/**
	 * 检测后端分类类目已经被商品使用了
	 * @param id
	 * @return
	 */
	public boolean checkGoodsBackCategoryInUse(Integer id) {
		Integer goodsId=dbTemplate(
						"mall.goods.checkGoodsBackCategoryInUse", 
						Kv.by("bcategoryId",id)
						).queryInt();
		return isOk(goodsId);
	}
	/**
	 * 更新营销信息
	 * @param userId
	 * @param goods
	 * @return
	 */
	public Ret updateMarketingInfo(Integer userId, Goods goods) {
		return updateGoodsInfo(userId,goods,"[营销信息]");
	}
	
	/**
	 * 更新售后信息
	 * @param userId
	 * @param goods
	 * @return
	 */
	public Ret updateAfterSalesInfo(Integer userId, Goods goods) {
		return updateGoodsInfo(userId,goods,"[售后信息]");
	}
	
	/**
	 * 更新物流信息
	 * @param userId
	 * @param goods
	 * @return
	 */
	public Ret updateShippingInfo(Integer userId, Goods goods) {
		return updateGoodsInfo(userId,goods,"[物流信息]");
	}
	/**
	 * 更新商品其他额外信息
	 * @param userId
	 * @param goods
	 * @param logAppend
	 * @return
	 */
	private Ret updateGoodsInfo(Integer userId, Goods goods, String logAppend) {
		if(goods==null||notOk(goods.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		Ret ret=checkCanUpdate(goods);
		if(ret.isFail()){
			return ret;
		}
		boolean success=goods.update();
		if(success){
			//添加日志
			addUpdateSystemLog(goods.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS, goods.getName(),logAppend);
		}
		return ret(success);
	}
	
	/**
	 * 检测是否可以修改
	 * @param goods
	 * @return
	 */
	private Ret checkCanUpdate(Goods goods) {
		Goods dbGoods=findById(goods.getId());
		if(dbGoods==null){
			return fail(Msg.DATA_NOT_EXIST);
		}
		if(dbGoods.getOnSale()&&dbGoods.getIsDelete()==false){
			return fail("已上架商品不能修改");
		}
		//除了基本信息 其他的都不会传递name参数  这里要设置一下 方便后面日志调用
		if(notOk(goods.getName())){
			goods.setName(dbGoods.getName());
		}
		//防止恶意修改分类
		removeGoodsSpecAttr(goods);
		return SUCCESS;
	}
	/**
	 * 提交数据删掉这几个属性 防止恶意修改
	 * @param goods
	 */
	private void removeGoodsSpecAttr(Goods goods) {
		goods.remove("bcategory_id","fcategory_id","bcategory_key","fcategory_key","type_id");
	}
	/**
	 * 检测是否可以修改category
	 * @param goods
	 * @return
	 */
	public Ret checkCanEditGoodsBackCategory(Goods goods) {
		if(goods.getIsDelete()){return fail("已删除商品，不能修改商品后端分类");}
		if(goods.getOnSale()){return fail("已上架商品，不能修改商品后端分类");}
		//TODO  用于根据实际需求扩展 是否商品有销量了就不能修改分类了
//		if(goods.getRealSaleCount()>0){return fail("商品已经存在订单销量，不能修改分类");}
		return SUCCESS;
	}
	/**
	 * 更新商品的后台分类
	 * @param userId
	 * @param goodsId
	 * @param categoryId
	 * @return
	 */
	public Ret updateBackCategory(Integer userId, Integer goodsId, Integer categoryId) {
		if(notOk(goodsId)||notOk(categoryId)){return fail(Msg.PARAM_ERROR);}
		GoodsBackCategory goodsCategory=goodsBackCategoryService.findById(categoryId);
		if(goodsCategory==null){
			return fail("所选分类的"+Msg.DATA_NOT_EXIST);
		}
		Goods goods=new Goods().setId(goodsId);
		Ret ret=checkCanUpdate(goods);
		if(ret.isFail()){
			return ret;
		}
		goods.setBcategoryId(categoryId);
		goods.setBcategoryKey(goodsCategory.getCategoryKey());
		goods.setTypeId(goodsCategory.getTypeId());
		boolean success=goods.update();
		if(success){
			//添加日志
			addUpdateSystemLog(goods.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS, goods.getName(),"[后台分类]变为["+CACHE.me.getGoodsBackCategoryFullName(categoryId)+"]");
		}
		return ret(success);
	}
	/**
	 * 更新商品图文html详情
	 * @param userId
	 * @param goodsHtmlContent
	 * @return
	 */
	public Ret updateHtmlContent(Integer userId, GoodsHtmlContent goodsHtmlContent) {
		if(goodsHtmlContent==null||notOk(goodsHtmlContent.getGoodsId())){
			return fail(Msg.PARAM_ERROR);
		}
		Goods goods=findById(goodsHtmlContent.getGoodsId());
		if(goods==null){return fail("商品信息不存在");}
		Ret ret=null;
		if(isOk(goodsHtmlContent.getId())){
			ret=goodsHtmlContentService.update(userId, goodsHtmlContent);
		}else{
			ret=goodsHtmlContentService.save(userId, goodsHtmlContent);
		}
		if(ret.isOk()){
			goods.setUpdateTime(new Date());
			goods.setUpdateUserId(userId);
			boolean success=goods.update();
			if(success){
				//添加日志
				addUpdateSystemLog(goods.getId(), userId, SystemLog.TARGETTYPE_MALL_GOODS, goods.getName(),"[图文详情描述]");
				return SUCCESS;
			}
		}
		return FAIL;
	}
	/**
	 * 检测品牌是否在商品中被使用
	 * @param brandId
	 * @return
	 */
	public boolean checkBrandInUse(Integer brandId) {
		return exists("brand_id", brandId);
	}
	 

}
