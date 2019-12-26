package cn.jbolt._admin.permission;
/**
 * 由PermissionKeyGen生成的 权限定义KEY 
 * 用于在注解里使用
 */
public class PermissionKey {
	#for(data:permissions)
	/**
	 * #(data.title)
	 */
	public static final String #(cn.jbolt.common.util.StringUtil::toUpperCase(data.permissionKey??)) = "#(data.permissionKey)";
	#else
	//数据库里未定义权限
	#end
}