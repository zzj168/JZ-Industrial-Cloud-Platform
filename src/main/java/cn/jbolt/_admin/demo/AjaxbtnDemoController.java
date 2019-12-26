package cn.jbolt._admin.demo;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
import sun.print.resources.serviceui;
/**
 * Demo演示-AjaxBtn组件页面
 * @ClassName:  AjaxbtnDemoController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年10月1日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.DEMO)
@UnCheckIfSystemAdmin
public class AjaxbtnDemoController extends BaseController {
	public void index() {
		render("ajaxbtn.html");
	}
	/**
	 * 最简单的调用
	 */
	public void ajaxSuccess() {
		try {
			//模拟执行时长
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		renderJsonSuccess();
	}
	/**
	 * 最简单的调用 测试fail
	 */
	public void ajaxFail() {
		renderJsonFail("Ajax执行失败");
	}
	
	/**
	 * 模拟删除操作
	 */
	public void delete() {
//		renderJson(serviceui.delById(getInt(0)));
		renderJsonSuccess();
	}
}
