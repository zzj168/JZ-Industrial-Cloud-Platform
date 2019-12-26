package cn.jbolt._admin.user;

import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt._admin.role.RoleService;
import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.db.sql.Sql;
import cn.jbolt.common.db.sql.SqlUtil;
import cn.jbolt.common.model.Role;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.model.User;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.PinYinUtil;

public class UserService extends BaseService<User> {
	private User dao=new User().dao();
	@Inject
	private SystemLogService systeLogService;
	@Inject
	private UserService userService;
	@Inject
	private RoleService roleService;
	@Override
	protected User dao() {
		return dao;
	}
	
	/**
	 * 保存
	 * @param user
	 * @return
	 */
	public Ret save(Integer userId,User user) {
		if(user==null||notOk(userId)||isOk(user.getId())||notOk(user.getName())
				||notOk(user.getUsername())){
			return fail(Msg.PARAM_ERROR);
		}
		return saveOrUpdate(userId,user);
	}
	
	
	/**
	 * 更新
	 * @param user
	 * @return
	 */
	public Ret update(Integer userId,User user) {
		if(user==null||notOk(userId)||notOk(user.getId())||notOk(user.getName())
				||notOk(user.getUsername())){
			return fail(Msg.PARAM_ERROR);
		}
		
		return saveOrUpdate(userId,user);
	}
	/**
	 * saveOrUpdate
	 * @param userId
	 * @param user
	 * @return
	 */
	private Ret saveOrUpdate(Integer userId, User user) {
		if(user==null||notOk(userId)||notOk(user.getName())
				||notOk(user.getUsername())){
			return fail(Msg.PARAM_ERROR);
		}
		
		boolean update=isOk(user.getId());
		if(notOk(user.getRoles())){return fail("必须设置用户角色");}
		if(update) {
			if (existsName(user.getName(),user.getId())) {
				return fail("用户【"+user.getName()+"】已经存在，请输入其它名称");
			}
			if (exists("username",user.getUsername(),user.getId())) {
				return fail("用户名【"+user.getUsername()+"】已经存在，请输入其它名称");
			}
		}else {
			if(notOk(user.getPassword())) {
				return fail("请输入用户密码");
			}
			if (existsName(user.getName())) {
				return fail("用户【"+user.getName()+"】已经存在，请输入其它名称");
			}
			if (exists("username",user.getUsername())) {
				return fail("用户名【"+user.getUsername()+"】已经存在，请输入其它名称");
			}
		}
		
		if(notOk(user.getAvatar())){
			if(isOk(user.getSex())) {
				user.setAvatar("/assets/img/"+(user.getSex()==User.SEX_MALE?"nan.png":"nv.png"));
			}else {
				user.setAvatar("/assets/img/nan.png");
			}
		}
		user.setName(user.getName().trim());
		user.setUsername(user.getUsername().trim());
		user.setPinyin(PinYinUtil.getSpells(user.getName()));
		if(!update) {
			user.setPassword(HashKit.md5(user.getPassword()));
			user.setCreateUserId(userId);
		}
		boolean success=update?user.update():user.save();
		if(success){
			if(update) {
				addUpdateSystemLog(user.getId(), userId, SystemLog.TARGETTYPE_USER, user.getName());
			}else {
				addSaveSystemLog(user.getId(), userId, SystemLog.TARGETTYPE_USER, user.getName());
			}
		}
		return ret(success);
	}

	/**
	 * 删除
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret delete(Integer userId,Integer id) {
		if(isOk(userId)&&isOk(id)&&userId.intValue()==id.intValue()){
			return fail("自己不能删除自己");
		}
		User user=findById(id);
		if(user==null) {
			return fail(Msg.DATA_NOT_EXIST);
		}
		if(user.getIsSystemAdmin()) {
			return fail("不能删除超级管理员");
		}
		boolean success=user.delete();
		if(success) {
			addDeleteSystemLog(user.getId(), userId, SystemLog.TARGETTYPE_USER, user.getName());
		}
		return ret(success);
	}
	/**
	 * 检测一个角色是否被用户使用了
	 * @param id
	 * @return
	 */
	public boolean checkRoleInUse(Integer id) {
		Sql sql=selectSql().selectId().findInSet(id, "roles",true);
		Integer existId=queryInt(sql);
		return isOk(existId);
	}
	/**
	 * 切换Enable状态
	 * @param id
	 * @return
	 */
	public Ret toggleEnable(Integer userId,Integer id) {
		if(userId.intValue()==id.intValue()){
			return fail("自己不能操作自己的用户状态");
		}
		Ret ret=toggleBoolean(Kv.by("userId", userId), id, "enable");
		if(ret.isOk()){
			User user=ret.getAs("data");
			addUpdateSystemLog(id, userId, SystemLog.TARGETTYPE_USER, user.getName(), "的状态为["+(user.getEnable()?"启用]":"禁用]"));
		}
		
		return ret;
	}
	@Override
	public String checkCanToggle(Kv kv, User model, String column) {
		if(column.equals("enable")) {
			if(model.getIsSystemAdmin()&&model.getEnable()) {
				return "不能禁用超级管理员";
			}
		}
		return null;
	}
	/**
	 * 通过用户名密码获取用户信息
	 * @param userName
	 * @param password
	 * @return
	 */
	public User getUser(String userName, String password) {
		if(notOk(userName)||notOk(password)){
			return null;
		}
		return findFirst(Kv.by("username", userName).set("password",HashKit.md5(password)));
	}
	/**
	 * 更新密码
	 * @param userId
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
	public Ret doUpdatePwd(Integer userId, String oldPwd, String newPwd) {
		User me=findById(userId);
		if(me==null){return fail("用户信息异常");}
		User user=getUser(me.getUsername(), oldPwd);
		if(user==null){
			return fail("原密码输入不正确");
		}
		if(user.getId().intValue()!=userId.intValue()){
			return fail("原密码输入不正确");
		}
		user.setPassword(HashKit.md5(newPwd));
		boolean success=user.update();
		return ret(success);
	}
	/**
	 * 修改密码
	 * @param sessionUserId
	 * @param userId
	 * @param newPwd
	 * @return
	 */
	public Ret submitPwd(Integer sessionUserId,Integer userId, String newPwd) {
		User user=findById(userId);
		if(user==null){return fail("用户不存在");}
		if(user.getIsSystemAdmin()&&userId.equals(sessionUserId)==false) {return fail("您无权初始化系统超管员密码");}
		user.setPassword(HashKit.md5(newPwd));
		boolean success=user.update();
		if(success){
			addUpdateSystemLog(userId, sessionUserId, SystemLog.TARGETTYPE_USER, user.getName(), "的[密码]");
		}
		return ret(success);
	}
	/**
	 * 获取所有的角色组
	 * @return
	 */
	public List<String> getAllRoleGroups() {
		return query(selectSql().distinct("roles"));
	}
	/**
	 * 检测用户分配了指定的所有角色
	 * @param userId
	 * @param checkAll
	 * @param roleIds
	 * @return
	 */
	public boolean checkUserHasRole(Integer userId,boolean checkAll, Integer... roleIds) {
		if(roleIds==null||roleIds.length==0){return false;}
		User user=CACHE.me.getUser(userId);
		if(user==null){return false;}
		String roles=user.getRoles();
		if(StrKit.isBlank(roles)){return false;}
		String[] roleArray=ArrayUtil.from(roles, ",");
		if(roleArray==null||roleArray.length==0){return false;}
		boolean success=false;
		int count=roleIds.length;
		for(Integer roleId:roleIds){
			success=ArrayUtil.contains(roleArray, roleId);
			if(count==1&&success) {
				return true;
			}
			//如果是只要任何一个存在就返回True
			if(checkAll==false&&success) {
				return true;
			}
			if(checkAll&&success==false) {
				return false;
			}
			 
		}
		return true;
	}
	/**
	 * 检测密码是否准确
	 * @param userId
	 * @param password
	 * @return
	 */
	public boolean checkPwd(Integer userId, String password) {
		User user=findFirst(Kv.by("id", userId).set("password",HashKit.md5(password)));
		return user!=null;
	}
	/**
	 * 查询指定角色下的用户列表
	 * @param roleId
	 * @return
	 */
	public List<User> getUsersByRoleId(Integer roleId) {
//		return getCommonListWithoutColumns(Kv.by("find_in_set('"+roleId+"',roles) >", 0),true,"password");
		Object ret[]=SqlUtil.findInSet(MainConfig.DB_TYPE, "roles", roleId, true);
		return getCommonListWithoutColumns(Kv.by(ret[0], ret[1]),true,"password");
	}
	/**
	  * 删除用户角色
	 * @param sessionUserId
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public Ret deleteUserRole(Integer sessionUserId, Integer userId, Integer roleId) {
		if(notOk(sessionUserId)||notOk(userId)||notOk(roleId)) {return fail(Msg.PARAM_ERROR);}
		User sessionUser=findById(sessionUserId);
		if(sessionUser==null) {return fail(Msg.PARAM_ERROR);}
		//判断操作人和操作对象不是同一个人
		if(sessionUserId.intValue()==userId.intValue()&&!sessionUser.getIsSystemAdmin()) {
			return fail("系统禁止操作设置自身角色");
		}
		//得到被操作User
		User user=userService.findById(userId);
		if(user==null) {return fail("用户 "+Msg.DATA_NOT_EXIST);}
		Role role=roleService.findById(roleId);
		if(role==null) {return fail("角色 "+Msg.DATA_NOT_EXIST);}
		//拿到user的角色 删掉指定roleId
		String roles=user.getRoles();
		if(notOk(roles)) {return fail("此用户未设置任何角色");}
		String newRoles=(","+roles+",");
		if(newRoles.indexOf(","+roleId+",")==-1) {return fail("用户未绑定此角色");}
		newRoles=newRoles.replace(","+roleId+",", ",");
		if(newRoles.equals(",")) {
			newRoles=null;
		}else {
			newRoles=newRoles.substring(1, newRoles.length()-1);
		}
		user.setRoles(newRoles);
		boolean success=user.update();
		if(success) {
			addSystemLog(userId, sessionUserId, SystemLog.TYPE_UPDATE, SystemLog.TARGETTYPE_USER, user.getName(),"，将其从角色【"+role.getName()+"】中删除");
		}
		return ret(success);
	}
	/**
	 * 根据roleId，清空角色下的用户列表
	 * 只是删除绑定，并不是删掉用户信息
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public Ret clearUsersByRole(Integer sessionUserId, Integer roleId) {
		if(notOk(sessionUserId)||notOk(roleId)) {return fail(Msg.PARAM_ERROR);}
		User sessionUser=findById(sessionUserId);
		if(sessionUser==null) {return fail(Msg.PARAM_ERROR);}
		if(!sessionUser.getIsSystemAdmin()) {return fail("只有内置系统超级管理员才有权执行此操作");}
		Role role=roleService.findById(roleId);
		if(role==null) {return fail("角色 "+Msg.DATA_NOT_EXIST);}
		List<User> users=getUsersByRoleId(roleId);
		if(notOk(users)) {return fail("此角色下已无用户");}
		for(User user:users) {
			processDeleteUserRole(user,roleId);
		}
		Db.batchUpdate(users, users.size());
		addSystemLog(roleId, sessionUserId, SystemLog.TYPE_UPDATE, SystemLog.TARGETTYPE_ROLE, role.getName(),"，将其用户列表清空");
		return SUCCESS;
	}
	/**
	 * 处理用户的角色
	 * @param user
	 * @param roleId
	 */
	private void processDeleteUserRole(User user, Integer roleId) {
		//拿到user的角色 删掉指定roleId
		String roles=user.getRoles();
		if(notOk(roles)) {return;}
		String newRoles=(","+roles+",");
		if(newRoles.indexOf(","+roleId+",")==-1) {return;}
		newRoles=newRoles.replace(","+roleId+",", ",");
		if(newRoles.equals(",")) {
			newRoles=null;
		}else {
			newRoles=newRoles.substring(1, newRoles.length()-1);
		}
		user.setRoles(newRoles);
		//清除缓存
		deleteCacheById(user.getId());
	}
	/**
	 * 后台管理查询
	 * @param pageNumber
	 * @param pageSize
	 * @param keywords
	 * @return
	 */
	public Page<User> paginateAdminList(Integer pageNumber, Integer pageSize, String keywords) {
		String columns=getTableSelectColumnsWithout("password");
		if(notOk(keywords)) {
			return paginate(columns,"id", "desc", pageNumber, pageSize);
		}
		return paginateBySqlTemplate("user.paginateAdminList", Kv.by("keywords", keywords).setIfNotBlank("columns",columns), pageNumber, pageSize);
	}
	
	/**
	 * 更新avatar
	 * @param userId
	 * @param url
	 * @return
	 */
	public Ret updateUserAvatar(Integer userId, String url) {
		User user=findById(userId);
		if(user==null){return fail("用户信息异常");}
		user.setAvatar(url);
		boolean success=user.update();
		return ret(success);
	}

	public User getOneSystemAdmin() {
		return getRandomOne(Kv.by("is_system_admin", TRUE()));
	}
}
