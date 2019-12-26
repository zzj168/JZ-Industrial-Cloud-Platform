package cn.jbolt.admin.wechat.autoreply;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.Option;
import cn.jbolt.common.bean.OptionBean;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.WechatKeywords;
/**
 * 微信公众平台自动回复规则中的触发关键词管理
 * @ClassName:  WechatKeywordsAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年7月1日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.WECHAT_AUTOREPLY_KEYWORDS)
public class WechatKeywordsAdminController extends BaseController {
	@Inject
	private WechatAutoReplyService wechatAutoReplyService;
	@Inject
	private WechatKeywordsService service;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	
	@Before(WechatKeywordsMgrValidator.class)
	public void index() {
		Integer autoReplyId=getInt(0);
		set("datas", service.getListByAutoReplyId(autoReplyId));
		set("autoReplyId", autoReplyId);
		render("index.html");
	}
	
	
	@Before(WechatKeywordsMgrValidator.class)
	public void add() {
		Integer autoReplyId=getInt(0);
		Ret checkRet=service.checkCanOpt(autoReplyId);
		if(checkRet.isFail()) {
			renderDialogErrorRet(checkRet);
			return;
		}
		Kv checkResultKv=checkRet.getAs("data");
		Integer mpId=checkResultKv.getInt("mpId");
		set("autoReplyId",autoReplyId);
		set("mpId", mpId);
		render("add.html");
	}
	
	
	@Before(WechatKeywordsMgrValidator.class)
	public void edit() {
		Integer autoReplyId=getInt(0);
		Integer id=getInt(1);
		Ret checkRet=service.checkCanOpt(autoReplyId);
		if(checkRet.isFail()) {
			renderDialogErrorRet(checkRet);
			return;
		}
		WechatKeywords wechatKeywords=service.findById(id);
		if(wechatKeywords==null) {
			renderDialogError(Msg.DATA_NOT_EXIST);
			return;
		}
		Kv checkResultKv=checkRet.getAs("data");;
		Integer mpId=checkResultKv.getInt("mpId");
		if(wechatKeywords.getMpId().intValue()!=mpId.intValue()) {renderDialogError("参数异常:公众平台mpId");return;}
		if(wechatKeywords.getAutoReplyId().intValue()!=autoReplyId.intValue()) {renderDialogError("参数异常:所属规则 autoReplyId");return;}
		set("wechatKeywords", wechatKeywords);
		set("autoReplyId",autoReplyId);
		set("mpId",mpId);
		render("edit.html");
	}
	
	public void types() {
		List<Option> options=new ArrayList<Option>();
		options.add(new OptionBean("模糊匹配",WechatKeywords.TYPE_LIKE));
		options.add(new OptionBean("全等匹配",WechatKeywords.TYPE_EQUALS));
		renderJsonData(options);
	}
	
	@Before(WechatKeywordsMgrValidator.class)
	public void save() {
		renderJson(service.save(getSessionAdminUserId(),getInt(0),getModel(WechatKeywords.class,"wechatKeywords")));
	}
	@Before(WechatKeywordsMgrValidator.class)
	public void update() {
		renderJson(service.update(getSessionAdminUserId(),getInt(0),getModel(WechatKeywords.class,"wechatKeywords")));
	}
	@Before(WechatKeywordsMgrValidator.class)
	public void delete() {
		renderJson(service.delete(getSessionAdminUserId(),getInt(0),getInt(1)));
	}
}
