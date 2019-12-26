package cn.jbolt.admin.wechat.media;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt.admin.wechat.config.WechatConfigService;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.Option;
import cn.jbolt.common.bean.OptionBean;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.config.PageSize;
import cn.jbolt.common.model.WechatMedia;
import cn.jbolt.common.model.WechatMpinfo;
/**
 * 微信公众平台素材库管理
 * @ClassName:  WechatMediaAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年6月25日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.WECHAT_MEDIA)
public class WechatMediaAdminController extends BaseController {
	@Inject
	private WechatMediaService service;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatConfigService wechatConfigService;
	@Before(WechatMediaMgrValidator.class)
	public void index() {
		Integer mpId=getInt(0);
		WechatMpinfo mpinfo=wechatMpinfoService.findById(mpId);
		if(mpinfo==null) {renderDialogError("微信公众平台信息不存在");return;}
		String type=get("type", WechatMedia.TYPE_NEWS);
		set("pageData", service.paginateAdminList(mpId,type,getKeywords(),getPageNumber(),getPageSize(PageSize.PAGESIZE_ADMIN_LIST_20)));
		keepPara("keywords");
		set("mpId", mpId);
		set("type", type);
		render("index.html");
	}
	@Before(WechatMediaMgrValidator.class)
	public void download() {
		renderJson(service.downloadWechatMedia(getSessionAdminUserId(), getInt(0)));
	}
	@CheckPermission({PermissionKey.WECHAT_AUTOREPLY_DEFAULT,PermissionKey.WECHAT_AUTOREPLY_KEYWORDS,PermissionKey.WECHAT_AUTOREPLY_SUBSCRIBE})
	public void choose() {
		Integer mpId=getInt(0);
		String type=get(1);
		if(notOk(mpId)||notOk(type)) {renderDialogError(Msg.PARAM_ERROR);return;}
		WechatMpinfo mpinfo=wechatMpinfoService.findById(mpId);
		if(mpinfo==null) {renderDialogError("微信公众平台信息不存在");return;}
		set("pageData", service.paginateAdminList(mpId,type,getKeywords(),getPageNumber(),getPageSize(PageSize.PAGESIZE_ADMIN_LIST_20)));
		keepPara("keywords");
		set("mpId", mpId);
		set("type", type);
		render("choose.html");
	}
	@CheckPermission({PermissionKey.WECHAT_AUTOREPLY_DEFAULT,PermissionKey.WECHAT_AUTOREPLY_KEYWORDS,PermissionKey.WECHAT_AUTOREPLY_SUBSCRIBE})
	@Before(WechatMediaMgrValidator.class)
	public void chooseIt() {
		renderJson(service.getReplyChooseInfo(getInt(0),getInt(1)));
	}
	@UnCheck
	public void types() {
		List<Option> options=new ArrayList<Option>();
		options.add(new OptionBean("图文", WechatMedia.TYPE_NEWS));
		options.add(new OptionBean("图片", WechatMedia.TYPE_IMG));
		options.add(new OptionBean("语音", WechatMedia.TYPE_VOICE));
		options.add(new OptionBean("视频", WechatMedia.TYPE_VIDEO));
		renderJsonData(options);
	}
	
	@Before(WechatMediaMgrValidator.class)
	public void syncAll() {
		renderJson(service.syncAll(getInt(0)));
	}
	@Before(WechatMediaMgrValidator.class)
	public void syncNewDatas() {
		renderJson(service.syncNewDatas(getInt(0)));
	}
}
