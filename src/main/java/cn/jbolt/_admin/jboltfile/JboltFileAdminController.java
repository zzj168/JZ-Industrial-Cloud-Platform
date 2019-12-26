package cn.jbolt._admin.jboltfile;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;

import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.PageSize;
import cn.jbolt.common.model.JboltFile;

/**   
 * 文件库
 * @ClassName:  JBoltFileAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月24日 上午12:38:43   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JboltFileAdminController extends BaseController {
	@Inject
	private JboltFileService service;
	public void index(){
		Page<JboltFile> pageData=service.paginateAdminData(getPageNumber(),getPageSize(PageSize.PAGESIZE_ADMIN_LIST_20));
		set("pageData", pageData);
		render("index.html");
	}
	
}
