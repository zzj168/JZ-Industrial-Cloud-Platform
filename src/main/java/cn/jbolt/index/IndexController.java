package cn.jbolt.index;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;

import cn.jbolt._admin.updatemgr.DownloadLogService;
import cn.jbolt._admin.updatemgr.JBoltVersionService;
import cn.jbolt._admin.updatemgr.UpdateLibsService;
import cn.jbolt.base.BaseController;
import cn.jbolt.base.JBoltNoUrlPara;
import cn.jbolt.common.model.DownloadLog;
import cn.jbolt.common.util.IpUtil;

/**
* @author 小木 qq:909854136
* @version 创建时间：2019年1月11日 上午2:39:20
*/
public class IndexController extends BaseController {
	@Inject
	private JBoltVersionService jboltVersionService;
	@Inject
	private DownloadLogService downloadLogService;
	@Inject
	private UpdateLibsService updateLibsService;
	@Before(JBoltNoUrlPara.class)
	public void index(){
		redirect("/admin");
	}
	/**
	 * 主版本更新
	 */
	public void mainupdate(){
		String datas=jboltVersionService.getMainUpdateDatas();
		String ip=IpUtil.getIpAddr(getRequest());
		downloadLogService.addLog(ip,DownloadLog.DOWNLOADTYPE_MAINUPDATE);
		if(datas==null){
			renderText("");
		}else{
			renderJson(datas);
		}
	}
	/**
	 * libs更新
	 */
	public void libs(){
		String datas=updateLibsService.getUpdateLibs();
		String ip=IpUtil.getIpAddr(getRequest());
		downloadLogService.addLog(ip,DownloadLog.DOWNLOADTYPE_LIBSUPDATE);
		if(datas==null){
			renderText("");
		}else{
			renderJson(datas);
		}
	}
}
