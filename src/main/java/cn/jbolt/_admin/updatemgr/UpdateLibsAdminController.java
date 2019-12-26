package cn.jbolt._admin.updatemgr;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.UpdateLibs;

/**
* @author 小木 qq:909854136
* @version 创建时间：2019年1月11日 下午1:35:49
* 类说明
*/
@CheckPermission(PermissionKey.UPDATELIBS)
public class UpdateLibsAdminController extends BaseController {
	@Inject
	private UpdateLibsService service;
	public void index(){
		Page<UpdateLibs> page = service.paginateAdminList(getKeywords(),getPageNumber(), getPageSize());
		keepPara();
		set("pageData", page);
		render("index.html");
	}
	public void init(){
		renderJson(service.doInitDatas(get("url")));
	}
	
	public void add() {
		render("add.html");
	}

	public void edit() {
		set("updateLib", service.findById(getInt(0)));
		render("edit.html");
	}

	public void save() {
		renderJson(service.save(getSessionAdminUserId(), getModel(UpdateLibs.class, "updateLib")));
	}

	public void update() {
		renderJson(service.update(getSessionAdminUserId(), getModel(UpdateLibs.class, "updateLib")));
	}

	public void delete() {
		renderJson(service.delete(getSessionAdminUserId(), getInt(0)));
	}

	public void toggleMust() {
		renderJson(service.toggleMust(getSessionAdminUserId(), getInt(0)));
	}
	public void toggleDeleteAll() {
		renderJson(service.toggleDeleteAll(getSessionAdminUserId(), getInt(0)));
	}
}
