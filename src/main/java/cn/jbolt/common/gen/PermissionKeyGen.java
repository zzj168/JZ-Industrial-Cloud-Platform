package cn.jbolt.common.gen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import com.jfinal.aop.Aop;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;

import cn.hutool.core.io.FileUtil;
import cn.jbolt._admin.permission.PermissionService;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.model.Permission;

/**
 * 本系统中资源权限表里定义的资源 
 * 快捷生成静态常量到PermissionKey.java文件中，
 * 方便其他地方统一调用
 */
public class PermissionKeyGen {
	/**
	 * JBolt项目绝对路径 修改这个就行
	 */
	private static final String PROJECT_PATH=System.getProperty("user.dir");
	/**
	 * PermissionKey.java的绝对路径
	 */
	private static final String TARGET=PROJECT_PATH+"//src//main//java//cn//jbolt//_admin//permission//PermissionKey.java";
	/**
	 * 模板绝对路径
	 */
	private static final String TPL=PROJECT_PATH+"//src//main//java//cn//jbolt//common//gen//permissionkey.tpl";
	protected static DruidPlugin druidPlugin;
	public static DataSource getDataSource() {
		druidPlugin = MainConfig.createDruidPlugin();
		boolean success=druidPlugin.start();
		return success?druidPlugin.getDataSource():null;
	}

	public static void main(String[] args) {
		gen();
	}

	private static void gen() {
		DataSource dataSource=getDataSource();
		if(dataSource==null) {
			System.out.println("数据库连接失败");
			return;
		}
		ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(dataSource);
		activeRecordPlugin.addMapping("jb_permission", Permission.class);
		activeRecordPlugin.start();
		PermissionService service = Aop.get(PermissionService.class);
		List<Permission> permissions = service.findAll();
		Template template=Engine.use().getTemplate(TPL);
		BufferedWriter writer=FileUtil.getWriter(TARGET, "utf-8", false);
		try {
			writer.write(template.renderToString(Kv.by("permissions", permissions)));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
			
			if(activeRecordPlugin!=null) {
				activeRecordPlugin.stop();
			}
			if(druidPlugin!=null) {
				druidPlugin.stop();
			}
		}
	
		
	}
}
