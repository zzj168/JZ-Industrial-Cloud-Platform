package cn.jbolt.admin.wechat.menu;

import com.jfinal.aop.Inject;

import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.model.WechatMenu;
import cn.jbolt.common.model.WechatMpinfo;

/**   
 * 微信菜单管理
 * @ClassName:  WechatMenuAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月12日 下午11:37:20   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@CheckPermission(PermissionKey.WECHAT_MENU)
public class WechatMenuAdminController extends BaseController {
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatMenuService service;
	/**
	 * 进入一个公众号的菜单管理界面
	 */
	public void mgr(){
		Integer mpId=getInt(0);
		if(notOk(mpId)){
			renderDialogError("参数异常");
			return;
		}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null){
			renderDialogError("微信公众平台信息不存在");
			return;
		}
		set("mpId", mpId);
		set("mpName", wechatMpinfo.getName());
		render("mgr.html");
	}
	/**
	 * 读取一级菜单数据
	 */
	public void level1List(){
		renderJsonData(service.getLevel1Menus(getInt(0)));
	}
	/**
	 * 读取二级菜单数据
	 */
	public void level2List(){
		renderJsonData(service.getLevel2Menus(getInt(0),getInt(1)));
	}
	/**
	 * 进入单个菜单的编辑界面
	 * @return
	 */
	public void edit(){
		Integer mpId=getInt(0);
		Integer id=getInt(1);
		if(isOk(id)){
			WechatMenu menu = service.findById(id);
			if(menu.getMpId().intValue() != mpId.intValue()){
				renderErrorPortal("参数异常");
				return;
			}
			setAttr("menu", menu);
			set("action","weixin/menu/modify");
		}else{
			set("action","weixin/menu/add");
		}
		set("mpId", mpId);
		render("form.html");
	}

	
	/**
	 * 新增数据
	 * @return
	 */
	public void save(){
		renderJson(service.save(getSessionAdminUserId(),getInt("mpId"),getModel(WechatMenu.class,"menu")));
	}
	/**
	 * 修改数据
	 * @return
	 */
	public void update(){
		renderJson(service.update(getSessionAdminUserId(),getInt("mpId"),getModel(WechatMenu.class,"menu")));
	}
	
	/**
	 * 删除数据
	 * @return
	 */
	public void delete(){
		renderJson(service.delete(getSessionAdminUserId(),getInt(0),getInt(1)));
	}
	/**
	 * 发布到公众号
	 * @return
	 */
	public void publish(){
		renderJson(service.publish(getSessionAdminUserId(),getInt(0)));
	}
}
