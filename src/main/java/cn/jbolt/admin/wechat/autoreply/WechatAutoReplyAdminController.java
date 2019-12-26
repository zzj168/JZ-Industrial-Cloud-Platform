package cn.jbolt.admin.wechat.autoreply;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt._admin.user.UserAuthKit;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.Option;
import cn.jbolt.common.bean.OptionBean;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.WechatAutoreply;

/**
 * 微信公众平台自动回复设置管理
 * 
 * @ClassName: WechatAutoReplyAdminController
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2019年6月20日
 * 
 *        注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatAutoReplyAdminController extends BaseController {
	@Inject
	private WechatAutoReplyService service;
	@CheckPermission(PermissionKey.WECHAT_AUTOREPLY_SUBSCRIBE)
	@Before(WechatAutoReplyMgrValidator.class)
	public void subscribeReplyMgr() {
		set("title", "关注后回复");
		Integer mpId=getInt(0);
		set("action", "/admin/wechat/autoreply/subscribeReplyMgr/"+mpId);
		mgr(mpId, WechatAutoreply.TYPE_SUBSCRIBE, getKeywords());
	}
	@CheckPermission(PermissionKey.WECHAT_AUTOREPLY_KEYWORDS)
	@Before(WechatAutoReplyMgrValidator.class)
	public void keywordsReplyMgr() {
		set("title", "关键词回复");
		Integer mpId=getInt(0);
		set("action", "/admin/wechat/autoreply/keywordsReplyMgr/"+mpId);
		mgr(mpId, WechatAutoreply.TYPE_KEYWORDS, getKeywords());
	}
	@CheckPermission(PermissionKey.WECHAT_AUTOREPLY_SUBSCRIBE)
	@Before(WechatAutoReplyMgrValidator.class)
	public void defaultReplyMgr() {
		set("title", "默认回复");
		Integer mpId=getInt(0);
		set("action", "/admin/wechat/autoreply/defaultReplyMgr/"+mpId);
		mgr(mpId, WechatAutoreply.TYPE_DEFAULT, getKeywords());
	}

	private void mgr(Integer mpId, int type, String keywords) {
		set("pageData",service.paginateAdminMgrList(mpId, type, keywords, getPageNumber(), getPageSize()));
		set("mpId", mpId);
		set("type", type);
		set("keywords", keywords);
		render("index.html");
	}
	@UnCheck
	@Before(WechatAutoReplyMpIdAndTypeValidator.class)
	public void add() {
		Integer mpId=getInt(0);
		Integer type=getInt(1);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogError(Msg.NOPERMISSION);
			return;
		}
		set("mpId", mpId);
		set("type", type);
		render("add.html");
	}
	@UnCheck
	@Before(WechatAutoReplyMpIdAndTypeValidator.class)
	public void edit() {
		Integer mpId=getInt(0);
		Integer type=getInt(1);
		Integer id=getInt(2);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogError(Msg.NOPERMISSION);
			return;
		}
		WechatAutoreply wechatAutoreply=service.findById(id);
		if(wechatAutoreply==null) {
			renderDialogError(Msg.DATA_NOT_EXIST);
			return;
		}
		if(wechatAutoreply.getMpId().intValue()!=mpId.intValue()) {renderDialogError("参数异常:公众平台mpId");return;}
		if(wechatAutoreply.getType().intValue()!=type.intValue()) {renderDialogError("参数异常:公众平台type");return;}
		set("wechatAutoreply", wechatAutoreply);
		set("mpId", mpId);
		set("type", type);
		render("edit.html");
	}
	
	@UnCheck
	@Before({WechatAutoReplyMpIdAndTypeValidator.class,Tx.class})
	public void delete() {
		Integer mpId=getInt(0);
		Integer type=getInt(1);
		Integer id=getInt(2);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogError(Msg.NOPERMISSION);
			return;
		}
		renderJson(service.deleteAutoreply(getSessionAdminUserId(),mpId,type,id));
	}
	@UnCheck
	@Before(WechatAutoReplyMpIdAndTypeValidator.class)
	public void toggleEnable() {
		Integer mpId=getInt(0);
		Integer type=getInt(1);
		Integer id=getInt(2);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogError(Msg.NOPERMISSION);
			return;
		}
		renderJson(service.toggleEnable(getSessionAdminUserId(),mpId,type,id));
	}
	
	
	@UnCheck
	public void replyTypes() {
		List<Option> options=new ArrayList<Option>();
		options.add(new OptionBean("随机一条", WechatAutoreply.REPLYTYPE_RANDOMONE));
		options.add(new OptionBean("全部", WechatAutoreply.REPLYTYPE_ALL));
		renderJsonData(options);
	}
	private boolean checkPermission(Integer type) {
		boolean hasPermission=false;
		switch (type) {
		case WechatAutoreply.TYPE_SUBSCRIBE:
			hasPermission=UserAuthKit.hasPermission(getSessionAdminUserId(),true, PermissionKey.WECHAT_AUTOREPLY_SUBSCRIBE);
			break;
		case WechatAutoreply.TYPE_KEYWORDS:
			hasPermission=UserAuthKit.hasPermission(getSessionAdminUserId(),true, PermissionKey.WECHAT_AUTOREPLY_KEYWORDS);
			break;
		case WechatAutoreply.TYPE_DEFAULT:
			hasPermission=UserAuthKit.hasPermission(getSessionAdminUserId(),true, PermissionKey.WECHAT_AUTOREPLY_DEFAULT);
			break;
		}
		return hasPermission;
	}
	@UnCheck
	@Before(WechatAutoReplyMpIdAndTypeValidator.class)
	public void save() {
		Integer mpId=getInt(0);
		Integer type=getInt(1);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogError(Msg.NOPERMISSION);
			return;
		}
		renderJson(service.save(getSessionAdminUserId(),mpId,type,getModel(WechatAutoreply.class,"wechatAutoreply")));
	}
	
	@UnCheck
	@Before(WechatAutoReplyMpIdAndTypeValidator.class)
	public void update() {
		Integer mpId=getInt(0);
		Integer type=getInt(1);
		//根据TYPE 判断是否有权限
		boolean hasPermission=checkPermission(type);
		if(!hasPermission) {
			renderDialogError(Msg.NOPERMISSION);
			return;
		}
		renderJson(service.update(getSessionAdminUserId(),mpId,type,getModel(WechatAutoreply.class,"wechatAutoreply")));
	}

}
