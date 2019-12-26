package cn.jbolt._admin.demo;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.Demotable;
/**
 * Demo演示-Html5原生Date组件页面
 * @ClassName:  Html5dateDemoController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年10月1日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.DEMO)
@UnCheckIfSystemAdmin
public class Html5dateDemoController extends BaseController {
	public void index() {
		render("html5date.html");
	}
	public void submit(){
		keepPara();
		keepBean(Demotable.class, "dt");
		render("html5date.html");
	}
	
}
