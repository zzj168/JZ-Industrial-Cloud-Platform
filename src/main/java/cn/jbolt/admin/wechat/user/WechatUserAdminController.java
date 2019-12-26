package cn.jbolt.admin.wechat.user;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheck;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.Option;
import cn.jbolt.common.bean.OptionBean;
import cn.jbolt.common.config.PageSize;
import cn.jbolt.common.model.User;
/**
 *   微信用户信息管理 公众号和小程序
 * @ClassName:  WechatUserAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年7月20日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.WECHAT_USER)
public class WechatUserAdminController extends BaseController {
	@Inject
	private WechatUserService service;
	@Before(WechatUserMgrValidator.class)
	public void index() {
		Integer mpId=getInt(0);
		set("pageData", service.paginateAdminList(mpId,getPageNumber(), getPageSize(PageSize.PAGESIZE_ADMIN_LIST_20),getKeywords(),getInt("sex")));
		keepPara();
		set("mpId", mpId);
		render("index.html");
	}
	@UnCheck
	public void sexOptions() {
		List<Option> options=new ArrayList<Option>();
		options.add(new OptionBean("未知",User.SEX_NONE));
		options.add(new OptionBean("男",User.SEX_MALE));
		options.add(new OptionBean("女",User.SEX_FEMALE));
		renderJsonData(options);
	}
	/**
	 * 执行微信用户数据同步
	 */
	@Before(WechatUserMgrValidator.class)
	public void sync() {
		Integer mpId=getInt(0);
		renderJson(service.sync(mpId));
	}
	/**
	 * 切换Enable状态
	 */
	@Before(WechatUserMgrValidator.class)
	public void toggleEnable() {
		Integer mpId=getInt(0);
		Integer id=getInt(1);
		renderJson(service.toggleEnable(getSessionAdminUserId(),mpId,id));
	}
	/**
	 * 同步个人信息
	 */
	@Before(WechatUserMgrValidator.class)
	public void syncUser() {
		Integer mpId=getInt(0);
		Integer id=getInt(1);
		renderJson(service.syncOneUserInfo(getSessionAdminUserId(),mpId,id));
	}
}
