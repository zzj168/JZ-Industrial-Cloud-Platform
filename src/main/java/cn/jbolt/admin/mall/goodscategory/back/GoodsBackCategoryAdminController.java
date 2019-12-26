package cn.jbolt.admin.mall.goodscategory.back;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.GoodsBackCategory;

/** 
 * 商品类目管理-后台类目管理 主要用作后台  
 * @ClassName:  GoodsCategoryBackAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午3:18:27   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */

@CheckPermission({PermissionKey.MALL_GOODS_BACK_CATEGORY})
public class GoodsBackCategoryAdminController extends BaseController {
	@Inject
	private GoodsBackCategoryService service;
		public void index(){
			render("index.html");
		}
		
		public void add(){
			set("pid", getInt(0,0));
			render("add.html");
		}
		
		public void edit(){
			Integer id=getInt(0);
			if(isOk(id)){
				GoodsBackCategory category=service.findById(id);
				if(category==null){
					renderErrorPortal("分类信息不存在");
					return;
				}
				set("goodsBackCategory",category);
				set("pid",category.getPid());
			}
		
			render("edit.html");
		}
		
		@Before(Tx.class)
		public void save(){
			renderJson(service.save(getSessionAdminUserId(),getModel(GoodsBackCategory.class,"category")));
		}
		
		@Before(Tx.class)
		public void update(){
			renderJson(service.update(getSessionAdminUserId(),getModel(GoodsBackCategory.class,"category")));
		}
		
		@Before(Tx.class)
		public void delete(){
			renderJson(service.delete(getSessionAdminUserId(),getInt(0)));
		}
		@Before(Tx.class)
		public void move(){
			renderJson(service.move(getSessionAdminUserId(),getInt("id"),getInt("pid"),getInt("rank")));
		}
		
	/*	@Before(Tx.class)
		public void toggleEnable(){
			renderJson(service.toggleEnable(getSessionAdminUserId(),getInt(0)));
		}*/
		/**
		 * 后台类目管理页面的属性结构数据源
		 */
		public void mgrTree(){
			renderJsonData(service.getMgrTree(getInt(0)));
		}
		
		/**
		 * 商品页面需要使用的 筛选商品的tree
		 */
		@CheckPermission({PermissionKey.MALL_GOODS})
		public void goodsFilterTree() {
			renderJsonData(service.getEnableTree(getInt(0,0)));
		}
		/**
		 * 添加商品第一步需要选择的商品分类数据源
		 */
		@CheckPermission({PermissionKey.MALL_GOODS})
		public void selectbox() {
			renderJsonData(service.getEnableListByPid(getInt(0,0)));
		}
		
}
