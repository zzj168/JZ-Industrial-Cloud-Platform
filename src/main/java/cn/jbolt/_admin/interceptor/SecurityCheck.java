package cn.jbolt._admin.interceptor;

import java.lang.reflect.Method;
import java.util.Set;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.Permission;
import cn.jbolt.common.util.CACHE;

/**
 * 权限检查
 * 
 * @author 小木
 * 
 */
public class SecurityCheck {
    
    public static boolean isNoAnnotation(Method method){
        if (method.getAnnotations()==null||method.getAnnotations().length==0) {
            return true;
        }
        return false;
    }
    public static boolean isUncheck(Method method){
        if (method.isAnnotationPresent(UnCheck.class)) {
            return true;
        }
        return false;
    }
    
 
    public static boolean isPermissionCheck(Method method){
    	if (method.isAnnotationPresent(CheckPermission.class)) {
    		return true;
    	}
    	return false;
    }
    public static boolean isPermissionCheck(Controller controller){
        if (controller.getClass().isAnnotationPresent(CheckPermission.class)) {
            return true;
        }
        return false;
    }
    
	public static boolean isUncheckIfSystemAdmin(BaseController controller, Method method) {
		if(controller.isSystemAdmin()==false) {
			return false;
		}
		if(controller.getClass().isAnnotationPresent(UnCheckIfSystemAdmin.class)) {
			return true;
		}
		if (method.isAnnotationPresent(UnCheckIfSystemAdmin.class)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 检测keys是否在用户携带权限里存在
	 * @param checkAll
	 * @param roleIds
	 * @param keys
	 * @return
	 */
	public static boolean checkHasPermission(boolean checkAll,String roleIds,String... permissionKeys) {
		if(StrKit.isBlank(roleIds)||permissionKeys==null||permissionKeys.length==0) {return false;}
		return checkHasPermission(checkAll,CACHE.me.getRolePermissionKeySet(roleIds), permissionKeys);
	}
	/**
	 * 检测keys是否在用户携带权限里存在
	 * @param checkAll
	 * @param permissionKeyset
	 * @param keys
	 * @return
	 */
	public static boolean checkHasPermission(boolean checkAll,Set<String> permissionKeyset,String... permissionKeys) {
		if(permissionKeyset==null||permissionKeyset.isEmpty()||permissionKeys==null||permissionKeys.length==0) {return false;}
		if(checkAll) {
			//检测所有 就是只要有一个不行就都不行
			for (String permissionKey : permissionKeys) {
				if (permissionKeyset.contains(permissionKey)==false) {
					return false;
				}
			}
			return true;
		}
		
		//只要有一个存在就可以
		for (String permissionKey : permissionKeys) {
			if (permissionKeyset.contains(permissionKey)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 检测是否是超管默认权限
	 * @param checkAll 
	 * @param permissionKeys
	 * @return
	 */
	public static boolean checkIsSystemAdminDefaultPermission(boolean checkAll,String... permissionKeys) {
		if(permissionKeys==null||permissionKeys.length==0) {return false;}
		
		int count=permissionKeys.length;
		if(count==1) {
			//当只传了一个 就判断这个是不是超管默认 是就返回true
			Permission permission=CACHE.me.getPermission(permissionKeys[0]);
			return (permission!=null&&permission.getIsSystemAdminDefault());
		}
		//多个的时候就得判断checkAll
		Permission permission;
		if(checkAll) {
			for(String permissionKey:permissionKeys) {
				permission=CACHE.me.getPermission(permissionKey);
				//检测all 但是只要有一个不是的 就返回false
				if(permission==null||permission.getIsSystemAdminDefault()==false) {
					return false;
				}
			}
			return true;
		}
		
		//检测只要一个符合条件就是true
		for(String permissionKey:permissionKeys) {
			permission=CACHE.me.getPermission(permissionKey);
			if(permission!=null&&permission.getIsSystemAdminDefault()) {
				return true;
			}
		}
		
		return false;
	}
   
    

}
