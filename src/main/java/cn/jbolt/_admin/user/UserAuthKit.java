package cn.jbolt._admin.user;
/**
 * 用户权限工具类
* @author 小木
*/

import com.jfinal.aop.Aop;

import cn.jbolt._admin.rolepermission.RolePermissionService;

public class UserAuthKit{
	private static UserService userService=Aop.get(UserService.class);
	private static RolePermissionService rolePermissionService=Aop.get(RolePermissionService.class);
	
	/**
	 * 判断用户是否有指定所有角色
	 * @param userId
	 * @param checkAll
	 * @param roleIds
	 * @return
	 */
	public static boolean hasRole(Integer userId,boolean checkAll,Integer... roleIds){
		return userService.checkUserHasRole(userId,checkAll,roleIds);
	}
	/**
	 * 判断用户是否有指定权限
	 * @param userId
	 * @param checkAll
	 * @param permissionKeys
	 * @return
	 */
	public static boolean hasPermission(Integer userId,boolean checkAll,String... permissionKeys){
		return rolePermissionService.checkUserHasPermission(userId,checkAll, permissionKeys);
		
	}
	
}
