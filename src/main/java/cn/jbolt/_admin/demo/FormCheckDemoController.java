package cn.jbolt._admin.demo;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
/**
 * Demo演示-表单校验 页面
 * @ClassName:  FormCheckDemoController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年10月1日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.DEMO)
@UnCheckIfSystemAdmin
public class FormCheckDemoController extends BaseController {
	public void index() {
		render("formcheck.html");
	}
	
	public void submit() {
		keepPara();
		render("formcheck.html");
	}
	public void ajaxCheckSuccess() {
		renderJsonSuccess();
	}
	public void ajaxCheckFail() {
		renderJsonFail("已经存在重名数据");
	}
	
}
