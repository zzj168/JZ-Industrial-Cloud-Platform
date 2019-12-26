package cn.jbolt._admin.rolepermission;

import java.util.List;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.PermissionService;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Permission;
import cn.jbolt.common.model.Role;
@CheckPermission(PermissionKey.ROLE)
@UnCheckIfSystemAdmin
public class RolePermissionAdminController extends BaseController {
	@Inject
	private RolePermissionService service;
	@Inject
	private PermissionService permissionService;
	@Inject
	private RoleService roleService;
	/**
	 * 进入角色分配资源的界面
	 */
	public void setting(){
		Integer roleId=getInt(0);
		if(notOk(roleId)) {
			renderDialogError(Msg.PARAM_ERROR);
			return;
		}
		Role role=roleService.findById(roleId);
		if(role==null) {
			renderDialogError(Msg.PARAM_ERROR);
			return;
		}
		List<Permission> permissions=null;
		if(isOk(role.getPid())) {
			permissions=permissionService.getParentPermissionsWithLevel(role.getPid());
		}else {
			permissions=permissionService.getAllPermissionsWithLevel();
		}
		
		set("dataList", permissions);
		set("roleId", roleId);
		render("setting.html");
	}
	/**
	 * 提交角色分配资源变更
	 */
	public void submit(){
		Integer roleId=getInt("roleId");
		String permissionStr=get("permissions");
		renderJson(service.doSubmit(getSessionAdminUserId(),roleId,permissionStr));
	}
	
	/**
	 *  获取角色已经设置的资源
	 */
	public void getCheckeds(){
		renderJsonData(service.getListByRole(getInt(0)));
	}
	/**
	 * 根据角色ID去清空此角色绑定的所有权限
	 */
	public void clear() {
		renderJson(service.deleteByRole(getSessionAdminUserId(),getInt(0)));
	}
}
