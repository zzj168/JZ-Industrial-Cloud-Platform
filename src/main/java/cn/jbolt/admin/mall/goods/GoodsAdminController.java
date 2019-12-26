package cn.jbolt.admin.mall.goods;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.jboltfile.JboltFileService;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.mall.goodscategory.back.GoodsBackCategoryService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.OptionBean;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.config.UploadFolder;
import cn.jbolt.common.model.Goods;
import cn.jbolt.common.model.GoodsHtmlContent;
import cn.jbolt.common.util.CACHE;

/**
* @author 小木 QQ:909854136
* @version 创建时间：2019年3月22日 下午10:25:29
*/
@CheckPermission(PermissionKey.MALL_GOODS)
public class GoodsAdminController extends BaseController {
	@Inject
	private GoodsService service;
	@Inject
	private GoodsHtmlContentService goodsHtmlContentService;
	@Inject
	private GoodsGroupService goodsGroupService;
	@Inject
	private GoodsBackCategoryService goodsBackCategoryService;
	@Inject
	private JboltFileService jboltFileService;
	public void index(){
		Page<Goods> pageData=service.paginateAdminData(getPageNumber(),getPageSize(),getKeywords(),getBoolean("onSale"),getBoolean("isHot"),getBoolean("isRecommend"),getBoolean("isDelete",false),getInt("bcategoryId"),getInt("fcategoryId"));
		keepPara();
		set("deleteCount", service.getDeleteCount());
		set("pageData", pageData);
		render("index.html");
	}
	/**
	 * 进入添加新商品
	 */
	public void add(){
		Ret ret=service.createTempGoods(getSessionAdminUserId(),getInt("categoryId"));
		if(ret.isFail()){
			renderErrorPjax(ret.getStr("msg"), "javascript:goback()");
			return;
		}
		//处理添加完临时goods之后 直接redirect到edit中 这样就可以在页面刷新时不重复生成新的tempGoods了
		redirect("/admin/mall/goods/edit/"+ret.getInt("data"));
	}
	/**
	 * 更新商品基本信息
	 */
	public void updateBaseInfo(){
		renderJson(service.updateBaseInfo(getSessionAdminUserId(),getModel(Goods.class,"goods")));
	}
	/**
	 * 更新商品图片信息
	 */
	public void updateImages(){
		renderJson(service.updateImages(getSessionAdminUserId(),getModel(Goods.class,"goods")));
	}
	/**
	 * 更新商品营销信息
	 */
	public void updateMarketingInfo(){
		renderJson(service.updateMarketingInfo(getSessionAdminUserId(),getModel(Goods.class,"goods")));
	}
	/**
	 * 更新商品售后信息
	 */
	public void updateAfterSalesInfo(){
		renderJson(service.updateAfterSalesInfo(getSessionAdminUserId(),getModel(Goods.class,"goods")));
	}
	/**
	 * 更新商品物流信息
	 */
	public void updateShippingInfo(){
		renderJson(service.updateShippingInfo(getSessionAdminUserId(),getModel(Goods.class,"goods")));
	}
	/**
	 * 更新商品图文详情htmlcontent信息
	 */
	@Before(Tx.class)
	public void updateHtmlContent(){
		renderJson(service.updateHtmlContent(getSessionAdminUserId(),getModel(GoodsHtmlContent.class,"goodsHtmlContent")));
	}
	/**
	 *  预备添加 界面
	 */
	public void preadd(){
		render("choosebackcategory.html");
	}
	/**
	 * 读取后端分类
	 */
	public void backCategorys(){
		renderJsonData(goodsBackCategoryService.getEnableListByPid(getInt(0,0)));
	}
	/**
	 * 更新后端分类
	 */
	public void updateBackCategory(){
		Integer goodsId=getInt("goodsId");
		Integer categoryId=getInt("categoryId");
		Ret ret=service.updateBackCategory(getSessionAdminUserId(),goodsId,categoryId);
		if(ret.isFail()){
			renderErrorPjax(ret.getStr("msg"), "javascript:goback()");
			return;
		}
		//直接redirect到edit中 
		redirect("/admin/mall/goods/edit/"+goodsId);
	}
	/**
	 * 进入修改分类界面
	 */
	public void editBackCategory(){
		Integer goodsId=getInt(0);
		if(notOk(goodsId)){
			renderErrorPjax(Msg.PARAM_ERROR, "javascript:goback()");
			return;
		}
		Goods goods=service.findById(goodsId);
		if(goods==null){
			renderErrorPjax("商品信息不存在", "javascript:goback()");
			return;
		}
		Ret ret=service.checkCanEditGoodsBackCategory(goods);
		if(ret.isOk()){
			set("goodsName", goods.getName());
			set("goodsBackCategoryFullName", CACHE.me.getGoodsBackCategoryFullName(goods.getBcategoryId()));
			set("goodsId", goods.getId());
			set("bcategoryId", goods.getBcategoryId());
			set("bcategoryKey", goods.getBcategoryKey());
			render("choosebackcategory.html");
		}else{
			renderErrorPjax(ret.getStr("msg"), "javascript:goback()");
		}
	}
	
	/**
	 * 进入商品编辑
	 */
	public void edit(){
		Integer goodsId=getInt(0);
		if(notOk(goodsId)){
			renderErrorPjax(Msg.PARAM_ERROR, "javascript:goback()");
			return;
		}
		Goods goods=service.getGoodsWithDetail(goodsId);
		if(goods==null){
			renderErrorPjax("商品信息不存在", "javascript:goback()");
			return;
		}
		Ret ret=service.checkCanEditGoodsBackCategory(goods);
		set("canEditCategory", ret.isOk());
		if(ret.isFail()){
			set("failTips", ret.get("msg"));
		}
		set("imghost", PropKit.get("editor_imghost"));
		set("goods", goods);
		processFormDatas();
		render("form.html");
	}
	/**
	 * 上传商品相关图片
	 */
	public void uploadImage(){
		Integer goodsId=getInt(0,0);
		//上传到今天的文件夹下
		String todayFolder=UploadFolder.todayFolder();
		String uploadPath=UploadFolder.MALL_GOODS_IMAGE+"/"+todayFolder+"/"+goodsId;
		UploadFile file=getFile("file",uploadPath);
		String contentType=file.getContentType();
		if(contentType==null||contentType.indexOf("image/")==-1){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		renderJson(jboltFileService.saveImageFile(getSessionAdminUserId(),file,uploadPath));
	}
	/**
	 * 处理表单需要的公共数据
	 */
	private void processFormDatas() {
		set("groups", goodsGroupService.getList(true));
		
	}
	/**
	 * select onSale下拉数据源
	 */
	@Before(CacheInterceptor.class)
	public void onSaleOptions(){
		List<OptionBean> options=new ArrayList<OptionBean>();
		options.add(new OptionBean("未上架",false));
		options.add(new OptionBean("已上架",true));
		renderJsonData(options);
	}
	/**
	 * select isHot下拉数据源
	 */
	@Before(CacheInterceptor.class)
	public void isHotOptions(){
		List<OptionBean> options=new ArrayList<OptionBean>();
		options.add(new OptionBean("普通商品",false));
		options.add(new OptionBean("热销商品",true));
		renderJsonData(options);
	}
	/**
	 * select isRecommend下拉数据源
	 */
	@Before(CacheInterceptor.class)
	public void isRecommendOptions(){
		List<OptionBean> options=new ArrayList<OptionBean>();
		options.add(new OptionBean("未推荐",false));
		options.add(new OptionBean("已推荐",true));
		renderJsonData(options);
	}
	/**
	 * select isDelete下拉数据源
	 */
	@Before(CacheInterceptor.class)
	public void isDeleteOptions(){
		List<OptionBean> options=new ArrayList<OptionBean>();
		options.add(new OptionBean("商品库",false));
		options.add(new OptionBean("回收站",true));
		renderJsonData(options);
	}
	/**
	 * 切换上下架
	 */
	public void toggleOnSale(){
		renderJson(service.toggleOnSale(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 切换热销
	 */
	public void toggleIsHot(){
		renderJson(service.toggleIsHot(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 切换推荐
	 */
	public void toggleIsRecommend(){
		renderJson(service.toggleIsRecommend(getSessionAdminUserId(),getInt(0)));
	}
	
	/**
	 * 删除商品
	 */
	public void delete(){
		renderJson(service.deleteGoods(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 恢复回收站里的一个商品
	 */
	public void restore(){
		renderJson(service.restoreGoods(getSessionAdminUserId(),getInt(0)));
	}

}
