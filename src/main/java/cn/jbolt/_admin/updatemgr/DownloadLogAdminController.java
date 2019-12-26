package cn.jbolt._admin.updatemgr;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.base.BaseController;

/**
* @author 小木 qq:909854136
* @version 创建时间：2019年1月15日 上午8:38:00
*/
@CheckPermission(PermissionKey.DOWNLOADLOG)
public class DownloadLogAdminController extends BaseController {
	@Inject
	private DownloadLogService downloadLogService;
	public void index(){
		set("pageData", downloadLogService.paginate("id", "desc", getPageNumber(), getPageSize()));
		render("index.html");
	}
}
