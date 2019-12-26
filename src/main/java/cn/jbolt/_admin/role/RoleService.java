package cn.jbolt._admin.role;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;

import cn.jbolt._admin.rolepermission.RolePermissionService;
import cn.jbolt._admin.systemlog.SystemLogService;
import cn.jbolt._admin.user.UserService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Dictionary;
import cn.jbolt.common.model.Role;
import cn.jbolt.common.model.Role;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.ListMap;
/**
 * 角色管理Service
 * @ClassName:  RoleService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月27日 上午11:54:25   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class RoleService extends BaseService<Role> {
	private Role dao = new Role().dao();
	@Inject
	private UserService userService;
	@Inject
	private SystemLogService systeLogService;
	@Inject
	private RolePermissionService rolePermissionService;
	@Override
	protected Role dao() {
		return dao;
	}
	
	/**
	 * 保存role数据
	 * @param user
	 * @return
	 */
	public Ret save(Integer userId,Role role) {
		if(role==null||isOk(role.getId())||notOk(role.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=role.getName().trim();
		if(name.indexOf(" ")!=-1){
			return saveAll(userId,role.getPid(),ArrayUtil.from3(name, " "));
		}
		if (existsName(name)) {
			return fail(Msg.DATA_SAME_NAME_EXIST+":["+name+"]");
		}
		role.setName(name);
		if(role.getPid()==null) {
			role.setPid(0);
		}
		boolean success=role.save();
		if(success){
			addSaveSystemLog(role.getId(), userId, SystemLog.TARGETTYPE_ROLE,role.getName());
		}
		return ret(success);
	}
	/**
	 * 添加多个
	 * @param userId
	 * @param pid
	 * @param names
	 * @return
	 */
	private Ret saveAll(Integer userId,Integer pid, String[] names) {
		Ret ret=null; 
		for(String name:names){
			ret=save(userId, new Role().setName(name).setPid(isOk(pid)?pid:0));
			if(ret.isFail()){
				return ret;
			}
		 }
		return SUCCESS;
	}

	
	
	/**
	 * 修改role数据
	 * @param user
	 * @return
	 */
	public Ret update(Integer userId,Role role) {
		if(role==null||notOk(role.getId())||notOk(role.getName())){
			return fail(Msg.PARAM_ERROR);
		}
		String name=role.getName().trim();
		if (existsName(name,role.getId())) {
			return fail(Msg.DATA_SAME_NAME_EXIST+":["+name+"]");
		}
		role.setName(name);
		boolean success=role.update();
		if(success){
			addUpdateSystemLog(role.getId(), userId, SystemLog.TARGETTYPE_ROLE,role.getName());
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
		//调用底层删除
		Ret ret=deleteById(id, true);
		if(ret.isOk()){
			// 删除rolePermissions绑定
			Ret delRet=rolePermissionService.deleteRolePermission(id);
			if(delRet.isFail()) {return delRet;}
			//添加日志
			Role role=ret.getAs("data");
			addDeleteSystemLog(id, userId, SystemLog.TARGETTYPE_ROLE,role.getName());
		}
		return ret;
	}

	@Override
	public String checkInUse(Role role) {
		boolean hasChild =checkHasChild(role.getId());
		if(hasChild) {
			return "此角色存在下级角色，不能直接删除";
		}
		boolean inUse=userService.checkRoleInUse(role.getId());
		return inUse?"此角色已被用户分配使用，不能直接删除":null;
	}
	/**
	 * 判断存在下级节点
	 * @param pid
	 * @return
	 */
	private boolean checkHasChild(Integer pid) {
		return exists("pid", pid);
	}

	/**
	 * 得到所有角色是树形数据结构
	 * @return
	 */
	public List<Role> getAllRoleTreeDatas() {
		return processSubItems(findAll());
	}
	private List<Role> processSubItems(ListMap<String, Role> map,List<Role> submitItems){
		for(Role role:submitItems){
			List<Role> items=map.get("role_"+role.getId());
			if(items!=null&&items.size()>0){
				role.put("items",processSubItems(map,items));
			}
		}
		return submitItems;
	}
	private List<Role> processSubItems(List<Role> roles) {
		if(isOk(roles)) {

			List<Role> submitItems=new ArrayList<Role>();
			for(Role role:roles){
				if(notOk(role.getPid())){
					submitItems.add(role);
				}
			}
			if(submitItems.size()>0){
				ListMap<String, Role> map=new ListMap<String, Role>();
				for(Role role:roles){
					if(isOk(role.getPid())){
						map.addItem("role_"+role.getPid(), role);
					}
				}
				for(Role role:submitItems){
					List<Role> items=map.get("role_"+role.getId());
					if(items!=null&&items.size()>0){
						role.put("items",processSubItems(map,items));
					}
				}
			}
			return submitItems;
		
		}
		return null;
		
	}
	

}
