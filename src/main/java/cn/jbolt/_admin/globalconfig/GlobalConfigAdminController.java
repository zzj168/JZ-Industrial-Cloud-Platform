package cn.jbolt._admin.globalconfig;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.GlobalConfig;

/**
 * 全局配置项
* @author 小木 qq:909854136
* @version 创建时间：2018年12月25日 下午11:13:57
*/
@CheckPermission(PermissionKey.GLOBALCONFIG)
@UnCheckIfSystemAdmin
public class GlobalConfigAdminController extends BaseController {
	@Inject
	private GlobalConfigService service;
	public void index(){
		service.checkAndInit(getSessionAdminUserId());
		set("globalConfigs", service.findAll());
		render("index.html");
	}
	
	/**
	 * 编辑
	 */
	public void edit(){
		set("globalConfig", service.findById(getInt(0)));
		render("edit.html");
	}
	/**
	 * 更新
	 */
	public void update(){
		GlobalConfig globalConfig=getModel(GlobalConfig.class, "globalConfig");
		renderJson(service.update(getSessionAdminUserId(),globalConfig));
	}
 
/*	*//**
	 * 删除
	 *//*
	public void delete(){
		renderJson(service.deleteById(getSessionAdminUserId(),getInt()));
	}*/
	
}
