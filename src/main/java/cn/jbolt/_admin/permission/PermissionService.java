package cn.jbolt._admin.permission;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt._admin.role.RoleService;
import cn.jbolt._admin.rolepermission.RolePermissionService;
import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.db.sql.Sql;
import cn.jbolt.common.model.Permission;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.ListMap;
/**
 * 系统权限资源Service
 * @author 小木
 *
 */
public class PermissionService extends BaseService<Permission> {
	private Permission dao = new Permission().dao();
	@Inject
	private RoleService roleService;
	@Inject
	private SystemLogService systeLogService;
	@Inject
	private RolePermissionService rolePermissionService;
	@Override
	protected Permission dao() {
		return dao;
	}
	/**
	 * 得到所有的一级permission
	 * @return
	 */
	public List<Permission> getAllParentPermissions() {
		return getCommonList(Kv.by("pid", 0).set("permission_level",Permission.LEVEL_1),"sort_rank");
	}
	/**
	 * 得到指定父级的下级permissions
	 * @return
	 */
	public List<Permission> getSonPermissions(Integer parentId) {
		return getCommonList(Kv.by("pid", parentId),"sort_rank");
	}
	/**
	 * 得到所有的一级permission
	 * @return
	 */
	public List<Permission> getAllParentPermissionsOrderById() {
		return getCommonList(Kv.by("pid", 0),"id");
		
	}
	/**
	 * 得到指定父级的下移permissions
	 * @return
	 */
	public List<Permission> getSonPermissionsOrderById(Integer parentId) {
		return getCommonList(Kv.by("pid", parentId),"id");
	}
	/**
	 * 得到所有permission 通过级别处理
	 * @return
	 */
	public List<Permission> getAllPermissionsWithLevel() {
		List<Permission> permissions=getAllParentPermissions();
		for(Permission f:permissions){
			List<Permission> sons=getSonPermissions(f.getId());
			for(Permission son:sons){
				son.putItems(getSonPermissions(son.getId()));
			}
			f.putItems(sons);
		}
		return permissions;
	}
	/**
	 * 得到所有permission 通过级别处理
	 * @return
	 */
	public List<Permission> getTwoLevelPermissions() {
		List<Permission> permissions=getAllParentPermissions();
		for(Permission f:permissions){
			f.putItems(getSonPermissions(f.getId()));
		}
		return permissions;
	}
	/**
	 * 保存数据
	 * @param permission
	 * @return
	 */
	public Ret save(Integer userId,Permission permission) {
		if(permission==null||isOk(permission.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		if(notOk(permission.getPermissionKey())){
			return fail("请设置权限KEY");
		}
		
		if (existsTitleWithSameParent(-1,permission.getTitle(),permission.getPid())) {
			return fail("同一父节点下资源【"+permission.getTitle()+"】已经存在，请更换");
		}
		if (exists("permission_key",permission.getPermissionKey())) {
			return fail("资源权限KEY【"+permission.getPermissionKey()+"】已经存在，请更换");
		}
		if(notOk(permission.getPid())){
			permission.setPid(0);
		}
		
		permission.setTitle(permission.getTitle().trim());
		permission.setSortRank(getNextSortRankWithPid(permission.getPid()));
		boolean success=permission.save();
		if(success){
			if(permission.getIsMenu()&&permission.getIsSystemAdminDefault()) {
				CACHE.me.removeMenusAndPermissionsByRoleGroups();
			}
			//添加日志
			addSaveSystemLog(permission.getId(), userId, SystemLog.TARGETTYPE_PERMISSION, permission.getTitle());
		}
		return ret(success);
	}
	/**
	 * 检测同一个pid下的title是否存在重复数据
	 * @param title
	 * @param pid
	 * @return
	 */
	private boolean existsTitleWithSameParent(Integer id,String title,Integer pid) {
		Sql sql=selectSql().selectId().eqQM("title","pid").idNoteqQM().first();
		Integer existId = queryInt(sql, title.trim(),pid,id);
		return isOk(existId);
	}
	
	/*	*//**
	 * 添加操作日志
	 * @param permission
	 * @param userId
	 * @param type
	 * @param sort
	 *//*
	private void addSystemLog(Permission permission, Integer userId, int type,boolean sort) {
		String userName=CACHE.me.getUserName(userId);
		StringBuilder title=new StringBuilder();
		title.append("<span class='text-danger'>[").append(userName).append("]</span>");
		switch (type) {
		case SystemLog.TYPE_SAVE:
			title.append("新增了");
			break;
		case SystemLog.TYPE_UPDATE:
			title.append("更新了");
			break;
		case SystemLog.TYPE_DELETE:
			title.append("删除了");
			break;
		}
		title.append("权限数据")
		.append("<span class='text-danger'>[").append(permission.getTitle()).append("]</span>");
		if(sort){
			title.append("的顺序");
		}
		systeLogService.saveLog(type, SystemLog.TARGETTYPE_PERMISSION, permission.getId(), title.toString(), 0, userId,userName);
		
	}*/
	/**
	 * 得到最新的next rank
	 * @param pid
	 * @return
	 */
	private int getNextSortRankWithPid(Integer pid) {
		if(pid==null){
			pid=0;
		}
		return getNextSortRank(Kv.by("pid", pid));
	}
	
	/**
	 * 修改数据
	 * @param permission
	 * @return
	 */
	public Ret update(Integer userId,Permission permission) {
		if(permission==null||notOk(permission.getId())){
			return fail(Msg.PARAM_ERROR);
		}
		Permission dbPermission=findById(permission.getId());
		//如果数据库不存在
		if(dbPermission==null) {
			return fail(Msg.DATA_NOT_EXIST);
		}
		if (existsTitleWithSameParent(permission.getId(),permission.getTitle(),permission.getPid())) {
			return fail("同一父节点下资源【"+permission.getTitle()+"】已经存在，请输入其它名称");
		}
		if (exists("permission_key",permission.getPermissionKey(),permission.getId())) {
			return fail("资源权限KEY【"+permission.getPermissionKey()+"】已经存在，请更换");
		}
		if(notOk(permission.getPid())){
			permission.setPid(0);
		}
		permission.setTitle(permission.getTitle().trim());
		//其他地方调用传过来的一个sort请求 指明是排序后调用update
		Boolean sort=permission.getBoolean("sort");
		if(sort==null){
			processPermissionNewLevel(permission);
		}
		boolean success=permission.update();
		if(success){
			//如果是菜单 需要处理菜单缓存
			if(permission.getIsMenu()){
				CACHE.me.removeMenusAndPermissionsByRoleGroups();
			}
			//如果是排序后update 日志会多加一部分说明
			if(sort!=null){
				addUpdateSystemLog(permission.getId(), userId, SystemLog.TARGETTYPE_PERMISSION, permission.getTitle(),"的顺序");
			}else{
				addUpdateSystemLog(permission.getId(), userId, SystemLog.TARGETTYPE_PERMISSION, permission.getTitle());
			}
			
		}
		return ret(success);
	}
	
	private void processPermissionNewLevel(Permission permission) {
		if(notOk(permission.getPid())){
			permission.setPermissionLevel(Permission.LEVEL_1);
		}else{
			int level=getParentLevel(permission.getPid());
			permission.setPermissionLevel(level+1);
		}
	}
	private int getParentLevel(Integer pid) {
		Permission permission=findById(pid);
		return permission.getPermissionLevel();
	}
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	public Ret delPermissionById(Integer userId,Integer id,boolean processRoleCache) {
		Ret ret=deleteById(id);
		if(ret.isOk()) {
			Permission permission=ret.getAs("data");
			//删除后需要把此数据之后的数据更新顺序
			updateSortRankAfterDelete(Kv.by("pid",permission.getPid()),permission.getSortRank());
			//删除子节点
			if(permission.getPermissionLevel()!=Permission.LEVEL_3){
				deleteByPid(userId,id);
			}
			//根据被删除的permission去删掉给role上的数据
			Ret delret=rolePermissionService.deleteByPermission(permission.getId());
			if(delret.isFail()) {return delret;}
		
			if(processRoleCache){
				CACHE.me.removeMenusAndPermissionsByRoleGroups();
			}
			//添加日志
			addDeleteSystemLog(permission.getId(), userId, SystemLog.TARGETTYPE_PERMISSION, permission.getTitle());
		}	
		return ret;
	}
	 /**
	  * 删除子节点
	  * @param userId
	  * @param pid
	  */
	private void deleteByPid(Integer userId,Integer pid) {
		List<Permission> permissions=getSonPermissions(pid);
		for(Permission permission:permissions){
			delPermissionById(userId, permission.getId(),false);
		}
	}
	 
	/**
	 * 上移
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret doUp(Integer userId,Integer id) {
		Permission permission=findById(id);
		if(permission==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=permission.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		if(rank==1){
			return fail("已经是第一个");
		}
		Permission upPermission=findFirst(Kv.by("sort_rank", rank-1).set("pid", permission.getPid()));
		if(upPermission==null){
			return fail("顺序需要初始化");
		}
		upPermission.setSortRank(rank);
		permission.setSortRank(rank-1);
		upPermission.put("sort", true);
		permission.put("sort", true);
		update(userId,upPermission);
		update(userId,permission);
	/*	CACHE.me.removePermission(upPermission.getId());
		CACHE.me.removePermission(permission.getId());
		if(permission.getIsMenu()){
			CACHE.me.removeMenusAndPermissionsByRoleGroups();
		}*/
		return SUCCESS;
	}
 
	
	
	/**
	 * 下移
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret doDown(Integer userId,Integer id) {
		Permission permission=findById(id);
		if(permission==null){
			return fail("数据不存在或已被删除");
		}
		Integer rank=permission.getSortRank();
		if(rank==null||rank<=0){
			return fail("顺序需要初始化");
		}
		int max=getCount(Kv.by("pid",permission.getPid()));
		if(rank==max){
			return fail("已经是最后已一个");
		}
		Permission upPermissions=findFirst(Kv.by("sort_rank", rank+1).set("pid", permission.getPid()));
		if(upPermissions==null){
			return fail("顺序需要初始化");
		}
		upPermissions.setSortRank(rank);
		permission.setSortRank(rank+1);
		upPermissions.put("sort", true);
		permission.put("sort", true);
		update(userId,upPermissions);
		update(userId,permission);
		/*CACHE.me.removePermission(upPermissions.getId());
		CACHE.me.removePermission(permission.getId());
		if(permission.getIsMenu()){
			CACHE.me.removeMenusAndPermissionsByRoleGroups();
		}*/
		return SUCCESS;
	}
	
	
	/**
	 * 初始化排序
	 * @param userId 
	 */
	public Ret doInitRank(Integer userId){
		List<Permission> parents=getAllParentPermissionsOrderById();
		if(parents.size()>0){
			for(int i=0;i<parents.size();i++){
				parents.get(i).setSortRank(i+1);
			}
			Db.batchUpdate(parents, parents.size());
			for(Permission f:parents){
				deleteCacheById(f.getId());
				deleteCacheByKey(f.getPermissionKey());
				List<Permission> permissions=getSonPermissionsOrderById(f.getId());
				if(permissions.size()>0){
					for(int i=0;i<permissions.size();i++){
						permissions.get(i).setSortRank(i+1);
					}
					Db.batchUpdate(permissions, permissions.size());
					for(Permission s2:permissions){
						deleteCacheById(s2.getId());
						deleteCacheByKey(s2.getPermissionKey());
						List<Permission> gsons=getSonPermissionsOrderById(s2.getId());
						if(gsons.size()>0){
							for(int i=0;i<gsons.size();i++){
								gsons.get(i).setSortRank(i+1);
							}
							Db.batchUpdate(gsons, gsons.size());
							for(Permission s3:gsons){
								deleteCacheById(s3.getId());
								deleteCacheByKey(s3.getPermissionKey());
							}
						}
						
					}
					
					
				}
				
			}
		}
		CACHE.me.removeMenusAndPermissionsByRoleGroups();
		//添加日志
		addUpdateSystemLog(null, userId, SystemLog.TARGETTYPE_PERMISSION, "全部","初始化顺序");
		return SUCCESS;
		
	}
	/**
	 * 通过权限资源的KEY获取数据
	 * @param permissionKey
	 * @return
	 */
	public Permission getByPermissionkey(String permissionKey) {
		if(notOk(permissionKey)) {return null;}
		return findFirst(Kv.by("permission_key", permissionKey));
	}
	
	public List<Permission> getParentPermissionsWithLevel(Integer roleId) {
		List<Permission> permissions=getPermissionsByRole(roleId);
		//处理分级
		List<Permission> parents=new ArrayList<Permission>();
		processParentPermission(permissions,parents);
		if(parents.size()>0&&permissions.size()>0) {
			processPermissionItems(permissions,parents);
		}
		return parents;
	}
	private void processPermissionItems(List<Permission> permissions, List<Permission> parents) {
		ListMap<String, Permission> map=new ListMap<String, Permission>();
		for(Permission p:permissions) {
			map.addItem("p_"+p.getPid(), p);
		}
		for(Permission p:parents) {
			processSubItems(map,p);
		}
		
	}
	private void processSubItems(ListMap<String, Permission> map, Permission permission) {
		List<Permission> items=map.get("p_"+permission.getId());
		if(items!=null&&items.size()>0) {
			for(Permission item:items) {
				processSubItems(map, item);
			}
		}
		permission.putItems(items);
	}
	private void processParentPermission(List<Permission> permissions, List<Permission> parents) {
		Permission permission;
		for(int i=0;i<permissions.size();i++) {
			permission=permissions.get(i);
			if(permission.getPermissionLevel()==Permission.LEVEL_1&&notOk(permission.getPid())) {
				parents.add(permission);
				permissions.remove(i);
				i--;
			}
		}
	}
	/**
	 * 根据角色ID获取到绑定的permission
	 * @param roleId
	 * @return
	 */
	public List<Permission> getPermissionsByRole(Integer roleId) {
		Sql sql=Sql.me(MainConfig.DB_TYPE).select("p.*").from(rolePermissionService.table(),"rp").leftJoin(table(),"p","p.id=rp.permission_id").eqQM("rp.role_id").orderBy("p.sort_rank");
		return find(sql,roleId);
	}
	
	/**
	 * 根据角色IDs获取到绑定的permission isMenu
	 * @param roleId
	 * @return
	 */
	public List<Permission> getIsMenuPermissionsByRoles(String roleIds) {
		return find(Sql.me(MainConfig.DB_TYPE).distinct("p.*").from(rolePermissionService.table(),"rp").leftJoin(table(),"p","p.id=rp.permission_id").eq("p.is_menu",TRUE()).in("rp.role_id",roleIds).orderBy("p.sort_rank"));
	}
	/**
	 * 
	 * @param userId
	 * @param self
	 * @param father
	 * @param grandfather
	 * @return
	 */
	public Ret toggleSystemAdminDefault(Integer userId, Integer self, Integer father, Integer grandfather) {
		Ret ret=toggleBoolean(self, "is_system_admin_default");
		if(ret.isOk()) {
			Permission selfPermission=ret.getAs("data");
			if(selfPermission.getIsSystemAdminDefault()) {
				processParentPermissionTrue(userId,selfPermission.getPid());
				processSonPermissionTrue(userId,selfPermission);
			}else {
				processSonPermissionFalse(userId,selfPermission);
			}
			CACHE.me.removeMenusAndPermissionsByRoleGroups();
		}
		return ret;
	}
	/**
	 * 处理下级全部true
	 * @param userId
	 * @param permission
	 */
	private void processSonPermissionTrue(Integer userId, Permission permission) {
		if(permission.getPermissionLevel()==Permission.LEVEL_3) {return;}
		List<Permission> sons=getSonPermissions(permission.getId());
		if(sons==null||sons.size()==0) {return;}
		boolean success=false;
		for(Permission son:sons) {
			son.setIsSystemAdminDefault(true);
			success=son.update();
			if(success) {
				processSonPermissionTrue(userId, son);
			}
		}
	}
	/**
	 * 处理下级全部false
	 * @param userId
	 * @param permission
	 */
	private void processSonPermissionFalse(Integer userId, Permission permission) {
		if(permission.getPermissionLevel()==Permission.LEVEL_3) {return;}
		List<Permission> sons=getSonPermissions(permission.getId());
		if(sons==null||sons.size()==0) {return;}
		boolean success=false;
		for(Permission son:sons) {
			son.setIsSystemAdminDefault(false);
			success=son.update();
			if(success) {
				processSonPermissionFalse(userId, son);
			}
		}
	}
	/**
	 * 处理多级上级全部true
	 * @param userId
	 * @param pid
	 */
	private void processParentPermissionTrue(Integer userId, Integer pid) {
		Permission permission=findById(pid);
		if(permission==null||permission.getIsSystemAdminDefault()) {return;}
		permission.setIsSystemAdminDefault(true);
		boolean success=permission.update();
		if(success) {
			if(permission.getPermissionLevel()==Permission.LEVEL_1) {return;}
			//继续找关联
			processParentPermissionTrue(userId, permission.getPid());
		}
			
		
	}
	public List<Permission> getIsMenuSystemAdminDefaultPermissions() {
		return getCommonList(Kv.by("is_system_admin_default", TRUE()).set("is_menu", TRUE()),"sort_rank");
	}
	
	public List<Permission> getIsMenuSystemAdminDefaultListWithLevel() {
			List<Permission> permissions=getIsMenuSystemAdminDefaultPermissions();
			//处理分级
			List<Permission> parents=new ArrayList<Permission>();
			processParentPermission(permissions,parents);
			if(parents.size()>0&&permissions.size()>0) {
				processPermissionItems(permissions,parents);
			}
			return parents;
	}
	/**
	 * 得到后台登录后显示的菜单
	 * @param roleIds
	 * @return
	 */
	public List<Permission> getMenusByRoles(String roleIds) {
		List<Permission> permissions=getIsMenuPermissionsByRoles(roleIds);
		//处理分级
		List<Permission> parents=new ArrayList<Permission>();
		processParentPermission(permissions,parents);
		if(parents.size()>0&&permissions.size()>0) {
			processPermissionItems(permissions,parents);
		}
		return parents;
	}
	/**
	 * 得到后台登录后显示的菜单
	 * @param roleIds
	 * @return
	 */
	public List<Permission> getMenusByRolesWithSystemAdminDefault(String roleIds) {
		if(StrKit.isBlank(roleIds)) {
			return getIsMenuSystemAdminDefaultListWithLevel();
		}
		List<Permission> permissions=getIsMenuPermissionsByRoles(roleIds);
		if(notOk(permissions)) {
			return getIsMenuSystemAdminDefaultListWithLevel();
		}
		List<Permission> adminDefaultPermissions=getIsMenuSystemAdminDefaultPermissions();
		if(isOk(adminDefaultPermissions)) {
			processMergePermissions(permissions,adminDefaultPermissions);
		}
		//处理分级
		List<Permission> parents=new ArrayList<Permission>();
		processParentPermission(permissions,parents);
		if(parents.size()>0&&permissions.size()>0) {
			processPermissionItems(permissions,parents);
		}
		return parents;
	}
	/**
	 * 合并去重
	 * @param permissions
	 * @param adminDefaultPermissions
	 */
	private void processMergePermissions(List<Permission> permissions, List<Permission> adminDefaultPermissions) {
		Set<String> set=new HashSet<String>();
		for(Permission permission:permissions) {
			set.add("p_"+permission.getId());
		}
		for(Permission permission:adminDefaultPermissions) {
			if(set.add("p_"+permission.getId())) {
				permissions.add(permission);
			}
		}
		
		permissions.sort(new Comparator<Permission>() {
			@Override
			public int compare(Permission o1, Permission o2) {
				return o1.getSortRank()-o2.getSortRank();
			}
		});
		
	}
}