package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt._admin.dictionary.DictionaryAdminController;
import cn.jbolt._admin.dictionary.DictionaryTypeAdminController;
import cn.jbolt._admin.globalconfig.GlobalConfigAdminController;
import cn.jbolt._admin.interceptor.AdminAuthInterceptor;
import cn.jbolt._admin.permission.PermissionAdminController;
import cn.jbolt._admin.role.RoleAdminCotroller;
import cn.jbolt._admin.rolepermission.RolePermissionAdminController;
import cn.jbolt._admin.salemanage.SaleManageAdminController;
import cn.jbolt._admin.supplier.SupplierAdminController;
import cn.jbolt._admin.supplymanage.SupplyManageAdminController;
import cn.jbolt._admin.systemlog.SystemLogAdminController;
import cn.jbolt._admin.updatemgr.DownloadLogAdminController;
import cn.jbolt._admin.updatemgr.JBoltVersionAdminController;
import cn.jbolt._admin.updatemgr.UpdateLibsAdminController;
import cn.jbolt._admin.user.UserAdminCotroller;
import cn.jbolt._admin.userconfig.UserConfigAdminController;
import cn.jbolt._admin.warehousemanage.WarehouseManageAdminController;
import cn.jbolt.admin.bom.BomController;
import cn.jbolt.admin.customer.CustomerController;
import cn.jbolt.admin.warehouse.WarehouseController;
import cn.jbolt.common.controller.NeditorUploadAdminController;
import cn.jbolt.common.controller.SummernoteUploadAdminController;
import cn.jbolt.common.model.WarehouseManage;
import cn.jbolt.common.style.JBoltStyleAdminController;
/**
 * admin后台的路由配置
 * @ClassName:  AdminRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午12:25:20   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class AdminRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_admin");
		this.addInterceptor(new PjaxInterceptor());
		this.addInterceptor(new AjaxPortalInterceptor());
		this.addInterceptor(new AdminAuthInterceptor());
		this.add("/admin", AdminIndexController.class,"/index");
		this.add("/admin/jboltstyle", JBoltStyleAdminController.class);
		this.add("/admin/user", UserAdminCotroller.class,"/user");
		this.add("/admin/role", RoleAdminCotroller.class,"/role");
		this.add("/admin/dictionarytype", DictionaryTypeAdminController.class,"/dictionary/type");
		this.add("/admin/dictionary", DictionaryAdminController.class,"/dictionary");
		this.add("/admin/permission", PermissionAdminController.class,"/permission");
		this.add("/admin/rolepermission", RolePermissionAdminController.class,"/rolepermission");
		this.add("/admin/systemlog", SystemLogAdminController.class,"/systemlog");
		this.add("/admin/globalconfig", GlobalConfigAdminController.class,"/globalconfig");
		this.add("/admin/jboltversion", JBoltVersionAdminController.class,"/jboltversion");
		this.add("/admin/updatelibs", UpdateLibsAdminController.class,"/updatelibs");
		this.add("/admin/downloadlog", DownloadLogAdminController.class,"/downloadlog");
		this.add("/admin/neditor/upload", NeditorUploadAdminController.class);
		this.add("/admin/summernote/upload", SummernoteUploadAdminController.class);
		this.add("/admin/userconfig", UserConfigAdminController.class,"/userconfig");
		//业务管理
		this.add("/admin/supplymanage",SupplyManageAdminController.class,"/supplymanage");
		//销售管理
		this.add("/admin/salemanage", SaleManageAdminController.class,"/salemanage");
		//仓位管理
		this.add("/admin/warehousemanage", WarehouseManageAdminController.class,"/warehousemanage");
		//基础设置 物料管理
		this.add("/admin/bom", BomController.class,"/bom");
		//客户管理
		this.add("/admin/customer", CustomerController.class,"/customer");
		//供应商管理
		this.add("/admin/supplier", SupplierAdminController.class,"/supplier");
		//仓库管理
		this.add("/admin/warehouse", WarehouseController.class,"/warehouse");
	}

}
