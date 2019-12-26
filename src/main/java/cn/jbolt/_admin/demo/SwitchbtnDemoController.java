package cn.jbolt._admin.demo;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
import sun.print.resources.serviceui;
/**
 * Demo演示-SwitchBtn组件页面
 * @ClassName:  SwitchbtnDemoController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年10月1日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.DEMO)
@UnCheckIfSystemAdmin
public class SwitchbtnDemoController extends BaseController {
	public void index() {
		set("enable", true);
		set("dataId", 1);
		render("switchbtn.html");
	}
	/**
	 * 最简单的调用
	 */
	public void toggleNormal() {
//		这里一般就是调用service方法 里 执行具体切换任务
//		renderJson(serviceui.toggleEnable(getInt(0)));
		//这里模拟执行成功
		renderJsonSuccess();
	}
	/**
	 * 最简单的调用 测试fail
	 */
	public void toggleNormalFail() {
//		这里一般就是调用service方法 里 执行具体切换任务
//		renderJson(serviceui.toggleEnable(getInt(0)));
		//这里模拟执行成功
		renderJsonFail("切换状态失败");
	}
}
