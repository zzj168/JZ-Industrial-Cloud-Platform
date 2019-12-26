package cn.jbolt.admin.appdevcenter;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt._admin.permission.UnCheckIfSystemAdmin;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.bean.OptionBean;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.Application;
/**
 * 应用开发者中心
 * 主要管理调用接口的Application
 * 不管是APP还是小程序或者其它端应用 都可以集成进来
 * 为调用开发API类应用准备
 * @ClassName:  AppDevCenterAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月12日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.APPLICATION)
@UnCheckIfSystemAdmin
public class ApplicationAdminController extends BaseController {
	@Inject
	private ApplicationService service;
	/**
	 * 应用中心首页
	 */
	public void index() {
		set("pageData", service.paginateAdminList(getPageNumber(),getPageSize(),getKeywords(),getType(),getEnable()));
		keepPara();
		render("index.html");
	}
	/**
	 * 查看app的appId和appSecret信息
	 */
	@Before(ApplicationValidator.class)
	public void appinfo() {
		Integer id=getInt(0);
		Application application=service.findById(id);
		if(application==null) {
			renderDialogError(Msg.DATA_NOT_EXIST);
			return;
		}
		setAttr("application", application);
		render("appinfo.html");
	}
	/**
	 * Api应用中心应用类型列表数据源
	 */
	@Before(CacheInterceptor.class)
	public void types() {
		List<OptionBean> options=new ArrayList<OptionBean>();
		options.add(new OptionBean("PC网页",Application.TYPE_WEB_PC));
		options.add(new OptionBean("普通移动端H5",Application.TYPE_WEB_MOBILE_H5));
		options.add(new OptionBean("公众平台H5",Application.TYPE_WEB_MP_H5));
		options.add(new OptionBean("微信小程序",Application.TYPE_WECHAT_APP));
		options.add(new OptionBean("支付宝小程序",Application.TYPE_ALIPAY_APP));
		options.add(new OptionBean("抖音小程序",Application.TYPE_DOUYIN_APP));
		options.add(new OptionBean("百度小程序",Application.TYPE_BAIDU_APP));
		options.add(new OptionBean("头条小程序",Application.TYPE_TOUTIAO_APP));
		options.add(new OptionBean("QQ小程序",Application.TYPE_QQ_APP));
		options.add(new OptionBean("苹果IOS-APP",Application.TYPE_APPLE_IOS_APP));
		options.add(new OptionBean("安卓-APP",Application.TYPE_ANDROID_APP));
		options.add(new OptionBean("混合Hybird-APP",Application.TYPE_HYBIRD_APP));
		options.add(new OptionBean("UNI-APP",Application.TYPE_UNI_APP));
		renderJsonData(options);
	}
	
	public void add() {
		render("add.html");
	}
	@Before(ApplicationValidator.class)
	public void edit() {
		Integer id=getInt(0);
		Application application=service.findById(id);
		if(application==null) {
			renderDialogError(Msg.DATA_NOT_EXIST);
			return;
		}
		setAttr("application", application);
		render("edit.html");
	}
	/**
	 * 保存
	 */
	public void save() {
		renderJson(service.save(getSessionAdminUserId(),getModel(Application.class, "application")));
	}
	/**
	 * 更新
	 */
	public void update() {
		renderJson(service.update(getSessionAdminUserId(),getModel(Application.class, "application")));
	}
	/**
	 * 删除
	 */
	@Before({Tx.class,ApplicationValidator.class})
	public void delete() {
		renderJson(service.deleteApplication(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 切换状态
	 */
	@Before(ApplicationValidator.class)
	public void toggleEnable() {
		renderJson(service.toggleEnable(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 切换是否需要接口needchecksign
	 */
	@Before(ApplicationValidator.class)
	public void toggleNeedCheckSign() {
		renderJson(service.toggleNeedCheckSign(getSessionAdminUserId(),getInt(0)));
	}
	/**
	 * 变更AppSecret
	 */
	@Before(ApplicationValidator.class)
	public void changeAppSecret() {
		renderJson(service.changeAppSecret(getSessionAdminUserId(),getInt(0)));
	}
}
