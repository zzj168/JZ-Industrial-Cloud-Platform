package cn.jbolt._admin.demo;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt._admin.updatemgr.ChangeLogService;
import cn.jbolt._admin.updatemgr.JBoltVersionService;
import cn.jbolt._admin.updatemgr.JboltVersionFileService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.PageSize;
/**
 * 主从表管理案例
 * @ClassName:  MasterSlaveDemoController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月10日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.DEMO_MASTERSLAVE)
@UnCheckIfSystemAdmin
public class MasterSlaveDemoController extends BaseController {
	@Inject
	private JBoltVersionService service;
	@Inject
	private ChangeLogService changeLogService;
	@Inject
	private JboltVersionFileService jboltVersionFileService;
	/**
	 * demo主页
	 */
	public void index() {
		set("pageData", service.paginate(getPageNumber(), getPageSize(PageSize.PAGESIZE_API_LIST_5)));
		render("index.html");
	}
	/**
	 * 主更新文件列表
	 */
	public void mainFiles() {
		Integer jboltVersionId=getInt(0);
		if(notOk(jboltVersionId)) {
			renderErrorPortal("暂无数据");
			return;
		}
		set("jboltVersionId", jboltVersionId);
		set("files", jboltVersionFileService.getFilesByJboltVersionId(jboltVersionId));
		render("files.html");
	}
	/**
	 * 读取changelog
	 */
	public void changelog() {
		Integer jboltVersionId=getInt(0);
		if(notOk(jboltVersionId)) {
			renderErrorPortal("暂无数据");
			return;
		}
		set("jboltVersionId", jboltVersionId);
		set("changelog", changeLogService.findByJboltVersionId(jboltVersionId));
		render("changelog.html");
	}
}
