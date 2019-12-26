package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt._admin.interceptor.AdminAuthInterceptor;
import cn.jbolt.admin.wechat.autoreply.WechatAutoReplyAdminController;
import cn.jbolt.admin.wechat.autoreply.WechatKeywordsAdminController;
import cn.jbolt.admin.wechat.autoreply.WechatReplyContentAdminController;
import cn.jbolt.admin.wechat.config.WechatConfigAdminController;
import cn.jbolt.admin.wechat.media.WechatMediaAdminController;
import cn.jbolt.admin.wechat.menu.WechatMenuAdminController;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoAdminController;
import cn.jbolt.admin.wechat.user.WechatUserAdminController;
/**
 * admin后台 微信管理模块相关 的路由配置
 * @ClassName:  WechatAdminRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月8日15:03:24   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatAdminRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_admin/_wechat");
		this.addInterceptor(new PjaxInterceptor());
		this.addInterceptor(new AjaxPortalInterceptor());
		this.addInterceptor(new AdminAuthInterceptor());
		this.add("/admin/wechat/mpinfo", WechatMpinfoAdminController.class,"/mpinfo");
		this.add("/admin/wechat/config", WechatConfigAdminController.class,"/config");
		this.add("/admin/wechat/menu", WechatMenuAdminController.class,"/menu");
		this.add("/admin/wechat/autoreply", WechatAutoReplyAdminController.class,"/autoreply");
		this.add("/admin/wechat/keywords", WechatKeywordsAdminController.class,"/autoreply/keywords");
		this.add("/admin/wechat/replycontent", WechatReplyContentAdminController.class,"/autoreply/replycontent");
		this.add("/admin/wechat/media", WechatMediaAdminController.class,"/media");
		this.add("/admin/wechat/user", WechatUserAdminController.class,"/user");
	}

}
