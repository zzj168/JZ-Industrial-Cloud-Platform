package cn.jbolt._admin.role;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt._admin.user.UserService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Role;
import cn.jbolt.common.model.User;
import cn.jbolt.common.util.CACHE;
@CheckPermission(PermissionKey.ROLE)
@UnCheckIfSystemAdmin
public class RoleAdminCotroller extends BaseController {
	@Inject
	private RoleService service;
	@Inject
	private UserService userService;
	/**
	 * 管理首页
	 */
	public void index(){
		set("dataList", service.getAllRoleTreeDatas());
		render("index.html");
	}
	@UnCheck
	public void select(){
		renderJsonData(service.getAllRoleTreeDatas());
	}
	/**
	 * 查询role上所有用户列表
	 */
	public void users() {
		Integer roleId=getInt(0);
		set("users", userService.getUsersByRoleId(roleId));
		set("roleId", roleId);
		User user= CACHE.me.getUser(getSessionAdminUserId());
		set("isSystemAdmin",user.getIsSystemAdmin());
		render("users.html");
	}
	
	/**
	 * 新增
	 */
	public void add(){
		render("add.html");
	}
	/**
	 * 新增Item
	 */
	public void addItem(){
		set("pid", getInt(0,0));
		render("add.html");
	}
	/**
	 * 编辑
	 */
	public void edit(){
		Role role=service.findById(getInt(0));
		if(role==null) {
			renderDialogError(Msg.DATA_NOT_EXIST);
			return;
		}
		set("role", role);
		set("pid", role.getPid());
		render("edit.html");
	}
	/**
	 * 保存
	 */
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getModel(Role.class, "role")));
	}
	/**
	 * 更新
	 */
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getModel(Role.class, "role")));
	}
	/**
	 * 删除
	 */
	public void delete(){
		renderJson(service.delete(getSessionAdminUserId(),getInt()));
	}
	
	/**
	 * 清空角色上的用户列表
	 */
	public void clearUsers() {
		renderJson(userService.clearUsersByRole(getSessionAdminUserId(),getInt()));
	}
}
