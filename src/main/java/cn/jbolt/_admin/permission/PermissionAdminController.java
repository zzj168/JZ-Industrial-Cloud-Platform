package cn.jbolt._admin.permission;

import java.util.List;

import com.jfinal.aop.Inject;

import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.Permission;
@CheckPermission(PermissionKey.PERMISSION)
@UnCheckIfSystemAdmin
public class PermissionAdminController extends BaseController {
	@Inject
	private PermissionService service;
	public void index(){
		List<Permission> permissions=service.getAllPermissionsWithLevel();
		set("dataList", permissions);
		render("index.html");
	}
	@UnCheck
	public void parentSelect(){
		renderJsonData(service.getTwoLevelPermissions());
	}
	public void add(){
		set("pid", getInt(0,0));
		set("level", getInt(1,1));
		render("add.html");
	}
	 
	public void edit(){
		Permission permission=service.findById(getInt(0));
		if(permission==null){
			renderFormError("数据不存在或已被删除");
			return;
		}
		set("pid",permission.getPid());
		set("level", permission.getPermissionLevel());
		set("permission", permission);
		render("edit.html");
	}
	
	public void save(){
		Permission permission=getModel(Permission.class,"permission");
		renderJson(service.save(getSessionAdminUserId(),permission));
	}
	
	public void update(){
		Permission permission=getModel(Permission.class,"permission");
		renderJson(service.update(getSessionAdminUserId(),permission));
	}
	
	public void delete(){
		renderJson(service.delPermissionById(getSessionAdminUserId(),getInt(0),true));
	}
	
	public void up(){
		renderJson(service.doUp(getSessionAdminUserId(),getInt(0)));
	}
	
	public void down(){
		renderJson(service.doDown(getSessionAdminUserId(),getInt(0)));
	}
	
	public void initRank(){
		renderJson(service.doInitRank(getSessionAdminUserId()));
	}
	/**
	 * 切换是否是超管默认
	 */
	public void toggleSystemAdminDefault(){
		renderJson(service.toggleSystemAdminDefault(getSessionAdminUserId(),getInt(0),getInt(1),getInt(2)));
	}
	
}
