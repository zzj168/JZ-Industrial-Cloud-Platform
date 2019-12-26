package cn.jbolt._admin.rolepermission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt._admin.interceptor.SecurityCheck;
import cn.jbolt._admin.permission.PermissionService;
import cn.jbolt._admin.role.RoleService;
import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Permission;
import cn.jbolt.common.model.Role;
import cn.jbolt.common.model.RolePermission;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.model.User;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.CACHE;
/**
 * 角色权限中间表Service
 * @ClassName:  RolePermissionService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年11月9日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class RolePermissionService extends BaseService<RolePermission> {
	private RolePermission dao = new RolePermission().dao();
	@Inject
	private PermissionService permissionService;
	@Inject
	private RoleService roleService;
	@Inject
	private SystemLogService systeLogService;

	@Override
	protected RolePermission dao() {
		return dao;
	}

	/**
	 * 删除一个角色下的权限资源
	 * 
	 * @param roleId
	 * @return
	 */
	public Ret deleteRolePermission(Integer roleId) {
		if (notOk(roleId)) {
			return fail(Msg.PARAM_ERROR);
		}
		Ret ret = deleteBy(Kv.by("role_id", roleId));
		if (ret.isOk()) {
			// 删除缓存
			CACHE.me.removeMenusAndPermissionsByRoleGroups();
		}
		return ret;
	}

	/**
	 * 处理角色变动资源
	 * 
	 * @param userId
	 * @param roleId
	 * @param permissionStr
	 * @return
	 */
	public Ret doSubmit(Integer userId, Integer roleId, String permissionStr) {
		if (notOk(roleId) || notOk(permissionStr)) {
			return fail("参数有误");
		}
		Role role = roleService.findById(roleId);
		if (role == null) {
			return fail("角色信息不存在");
		}
		String ids[] = ArrayUtil.from(permissionStr, ",");
		if (ids == null || ids.length == 0) {
			return fail("请选择分配的权限");
		}
		// 先删除以前的
		deleteRolePermission(roleId);
		// 添加现在的
		saveRolePermissions(roleId, ids);
		// 添加日志
		addUpdateSystemLog(roleId, userId, SystemLog.TARGETTYPE_ROLE, role.getName(), "的可用权限设置");
		return SUCCESS;
	}

	/**
	 * 保存一个角色分配的所有资源信息
	 * 
	 * @param roleId
	 * @param ids
	 */
	private void saveRolePermissions(Integer roleId, String[] ids) {
		List<RolePermission> permissions = new ArrayList<RolePermission>();
		RolePermission rolePermission = null;
		for (String id : ids) {
			rolePermission = new RolePermission();
			rolePermission.setRoleId(roleId);
			rolePermission.setPermissionId(Integer.parseInt(id));
			permissions.add(rolePermission);
		}
		Db.batchSave(permissions, permissions.size());
	}

	/**
	 * 获取指定多个角色的所有权限资源
	 * 
	 * @param roleIds
	 * @return
	 */
	public Set<String> getPermissionsKeySetByRoles(String roleIds) {
		List<RolePermission> rolePermissions = getListByRoles(roleIds);
		if (rolePermissions == null || rolePermissions.size() == 0) {
			return null;
		}
		Set<String> set=new HashSet<String>();
		for (RolePermission rf : rolePermissions) {
			Permission f = CACHE.me.getPermission(rf.getPermissionId());
			if (f != null) {
				set.add(f.getPermissionKey());
			}
		}
		return set;
	}
	
	/**
	 * 获取指定多个角色的所有权限资源
	 * 
	 * @param roleIds
	 * @return
	 */
	public List<Permission> getPermissionsByRoles(String roleIds) {
		List<RolePermission> rolePermissions = getListByRoles(roleIds);
		if (rolePermissions == null || rolePermissions.size() == 0) {
			return null;
		}
		List<Permission> permissions = new ArrayList<Permission>();
		for (RolePermission rf : rolePermissions) {
			Permission f = CACHE.me.getPermission(rf.getPermissionId());
			if (f != null) {
				permissions.add(f);
			}
		}
		return permissions;
	}

	/**
	 * 获取一个角色下的分配的权限资源数据
	 * 
	 * @param roleId
	 * @return
	 */
	public List<RolePermission> getListByRole(Integer roleId) {
		return getCommonList(Kv.by("role_id", roleId));
	}

	/**
	 * 获取多个角色下分配的权限资源数据
	 * 
	 * @param roleIds
	 * @return
	 */
	public List<RolePermission> getListByRoles(String roleIds) {
		return find(selectSql().distinct("permission_id").in("role_id", roleIds).orderBy("permission_id"));
	}

	/**
	 * 根据角色获取左侧菜单导航
	 * 
	 * @param roleId
	 * @return
	 */
	public List<Permission> getMenusByRole(Integer roleId) {
		List<RolePermission> rolePermissions = getListByRole(roleId);
		if (rolePermissions == null || rolePermissions.size() == 0) {
			return null;
		}

		return processMenusByPermissions(rolePermissions);

	}

	/**
	 * 根据指定的多个角色，返回合并后的所有菜单资源
	 * 
	 * @param roleIds
	 * @return
	 *//*
		 * public List<Permission> getMenusByRoles(String roleIds) {
		 * List<RolePermission> rolePermissions=getListByRoles(roleIds);
		 * if(rolePermissions==null||rolePermissions.size()==0){ return null; }
		 * 
		 * return processMenusByPermissions(rolePermissions);
		 * 
		 * }
		 */
	/**
	 * 处理抽取左侧导航菜单部分的权限
	 * 
	 * @param rolePermissions
	 * @return
	 */
	private List<Permission> processMenusByPermissions(List<RolePermission> rolePermissions) {
		List<Permission> level1permissions = new ArrayList<>();
		List<Permission> level2permissions = new ArrayList<>();
		List<Permission> level3permissions = new ArrayList<>();
		for (RolePermission rf : rolePermissions) {
			Permission f = CACHE.me.getPermission(rf.getPermissionId());
			if (f != null && f.getIsMenu()) {
				if (f.getPermissionLevel() == Permission.LEVEL_1) {
					level1permissions.add(f);
				} else if (f.getPermissionLevel() == Permission.LEVEL_2) {
					level2permissions.add(f);
				} else if (f.getPermissionLevel() == Permission.LEVEL_3) {
					level3permissions.add(f);
				}
			}
		}
		Collections.sort(level1permissions, new Comparator<Permission>() {

			@Override
			public int compare(Permission o1, Permission o2) {
				return o1.getSortRank() - o2.getSortRank();
			}
		});
		Collections.sort(level2permissions, new Comparator<Permission>() {

			@Override
			public int compare(Permission o1, Permission o2) {
				return o1.getSortRank() - o2.getSortRank();
			}
		});
		Collections.sort(level3permissions, new Comparator<Permission>() {

			@Override
			public int compare(Permission o1, Permission o2) {
				return o1.getSortRank() - o2.getSortRank();
			}
		});

		for (Permission l2 : level2permissions) {
			l2.putItems(processSonlist(l2.getId(), level3permissions));
		}
		for (Permission l1 : level1permissions) {
			l1.putItems(processSonlist(l1.getId(), level2permissions));
		}
		return level1permissions;
	}

	/**
	 * 处理sonlist
	 * 
	 * @param pid
	 * @param level2permissions
	 * @return
	 */
	private List<Permission> processSonlist(Integer pid, List<Permission> level2permissions) {
		List<Permission> son = new ArrayList<>();
		for (Permission l2 : level2permissions) {
			if (l2.getPid().intValue() == pid.intValue()) {
				son.add(l2);
			}
		}
		return son;
	}

	/**
	 * 检测一个角色是否包含指定的权限
	 * 
	 * @param roleId
	 * @param permissionKey
	 * @return
	 */
	public boolean checkRoleHasPermission(Integer roleId, String permissionKey) {
		Permission permission = permissionService.getByPermissionkey(permissionKey);
		if (permission == null) {
			return false;
		}
		RolePermission rolePermission = findFirst(Kv.by("role_id", roleId).set("permission_id", permission.getId()));
		return (rolePermission != null);
	}

	/**
	 * 检测一个用户是否包含指定的权限
	 * 
	 * @param userId
	 * @param checkAll
	 * @param permissionKeys
	 * @return
	 */
	public boolean checkUserHasPermission(Integer userId, boolean checkAll, String... permissionKeys) {
		if (permissionKeys == null || permissionKeys.length == 0) {
			return false;
		}
		User user = CACHE.me.getUser(userId);
		if (user == null) {
			return false;
		}
		String roles = user.getRoles();
		if (notOk(roles)) {
			return false;
		}
		//是超管并且包含超管权限
		if(user.getIsSystemAdmin()) {
			boolean isSystemAdminPermission=SecurityCheck.checkIsSystemAdminDefaultPermission(checkAll,permissionKeys);
			if(isSystemAdminPermission) {
				return true;
			}
		}
		//不是超管就直接check
		return SecurityCheck.checkHasPermission(checkAll,roles,permissionKeys);
	}



	/**
	 * 删除关联一个资源的所有role和资源的绑定数据
	 * 
	 * @param permissionId
	 */
	public Ret deleteByPermission(Integer permissionId) {
		return deleteBy(Kv.by("permission_id", permissionId));
	}
	/**
	 * 根据角色清空权限绑定
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public Ret deleteByRole(Integer userId, Integer roleId) {
		Ret ret=deleteRolePermission(roleId);
		if(ret.isOk()) {
			addUpdateSystemLog(roleId, userId, SystemLog.TARGETTYPE_ROLE, CACHE.me.getRoleName(roleId), "清空了角色绑定设置的所有权限资源");
		}
		return ret;
	}

}
