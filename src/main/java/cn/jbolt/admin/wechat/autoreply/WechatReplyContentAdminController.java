package cn.jbolt.admin.wechat.autoreply;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;

import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.Option;
import cn.jbolt.common.bean.OptionBean;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.config.UploadFolder;
import cn.jbolt.common.model.WechatAutoreply;
import cn.jbolt.common.model.WechatMpinfo;
import cn.jbolt.common.model.WechatReplyContent;

/**
 * 微信公众平台自动回复内容设置管理
 * 
 * @ClassName: WechatAutoReplyAdminController
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2019年6月20日
 * 
 *        注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatReplyContentAdminController extends BaseController {
	@Inject
	private WechatAutoReplyService wechatAutoReplyService;
	@Inject
	private WechatReplyContentService service;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void index() {
		Integer autoReplyId=getInt(0);
		Ret ret=service.checkPermission(getSessionAdminUserId(),autoReplyId);
		if(ret.isFail()) {renderDialogErrorRet(ret);return;}
		set("datas", service.getListByAutoReplyId(autoReplyId));
		set("autoReplyId", autoReplyId);
		render("index.html");
	}
	


	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void add() {
		Integer autoReplyId=getInt(0);
		Ret ret=service.checkPermission(getSessionAdminUserId(),autoReplyId);
		if(ret.isFail()) {renderDialogError(ret.getStr("msg"));return;}
		
		WechatAutoreply wechatAutoreply=ret.getAs("data");
		Integer mpId=wechatAutoreply.getMpId();
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {renderDialogError("关联微信公众平台不存在");return;}
		
		set("mpId", mpId);
		set("autoReplyId", autoReplyId);
		render("add.html");
	}
	
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void edit() {
		Integer autoReplyId=getInt(0);
		Integer id=getInt(1);
		//根据TYPE 判断是否有权限
		Ret ret=service.checkPermission(getSessionAdminUserId(),autoReplyId);
		if(ret.isFail()) {renderDialogError(ret.getStr("msg"));return;}
		WechatAutoreply wechatAutoreply=ret.getAs("data");
		
		Integer mpId=wechatAutoreply.getMpId();
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {renderDialogError("关联微信公众平台不存在");return;}
		
		set("mpId", mpId);
		set("autoReplyId", autoReplyId);
		WechatReplyContent wechatReplyContent=service.findById(id);
		if(wechatReplyContent==null) {
			renderDialogError(Msg.DATA_NOT_EXIST);
			return;
		}
		if(wechatReplyContent.getMpId().intValue()!=wechatAutoreply.getMpId().intValue()) {renderDialogError("参数异常:公众平台mpId");return;}
		if(wechatReplyContent.getAutoReplyId().intValue()!=autoReplyId.intValue()) {renderDialogError("参数异常:所属规则 autoReplyId");return;}
		set("wechatReplyContent", wechatReplyContent);
		render("edit.html");
	}
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void up(){
		renderJson(service.doUp(getSessionAdminUserId(),getInt(0),getInt(1)));
	}
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void down(){
		renderJson(service.doDown(getSessionAdminUserId(),getInt(0),getInt(1)));
	}
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void initRank(){
		renderJson(service.doInitRank(getSessionAdminUserId(),getInt(0)));
	}
	
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void delete() {
		renderJson(service.delete(getSessionAdminUserId(),getInt(0),getInt(1)));
	}
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void toggleEnable() {
		renderJson(service.toggleEnable(getSessionAdminUserId(),getInt(0),getInt(1)));
	}
	@UnCheck
	public void types() {
		List<Option> options=new ArrayList<Option>();
		options.add(new OptionBean("图文",WechatReplyContent.TYPE_NEWS));
		options.add(new OptionBean("文本",WechatReplyContent.TYPE_TEXT));
		options.add(new OptionBean("视频",WechatReplyContent.TYPE_VIDEO));
		options.add(new OptionBean("图片",WechatReplyContent.TYPE_IMG));
		options.add(new OptionBean("语音",WechatReplyContent.TYPE_VOICE));
		options.add(new OptionBean("音乐",WechatReplyContent.TYPE_MUSIC));
		renderJsonData(options);
	}
	
	/**
	 * 上传公众平台相关图片
	 */
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void uploadImage(){
		Integer autoReplyId=getInt(0);
		Ret ret=service.checkPermission(getSessionAdminUserId(),autoReplyId);
		if(ret.isFail()) {renderDialogError(ret.getStr("msg"));return;}
		//上传到今天的文件夹下
		String todayFolder=UploadFolder.todayFolder();
		String uploadPath=UploadFolder.WECHAT_AUTOREPLY_REPLYCONTENT+"/"+todayFolder;
		UploadFile file=getFile("img",uploadPath);
		if(notImage(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		renderJsonData(JFinal.me().getConstants().getBaseUploadPath()+"/"+uploadPath+"/"+file.getFileName());
	}
	
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void save() {
		renderJson(service.save(getSessionAdminUserId(),getInt(0),getModel(WechatReplyContent.class,"wechatReplyContent")));
	}
	@UnCheck
	@Before(WechatReplyContentMgrValidator.class)
	public void update() {
		renderJson(service.update(getSessionAdminUserId(),getInt(0),getModel(WechatReplyContent.class,"wechatReplyContent")));
	}

}
