package cn.jbolt.admin.wechat.menu;

import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MenuApi;

import cn.jbolt.admin.wechat.config.WechatConfigService;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.WechatMenu;
import cn.jbolt.common.model.WechatMpinfo;
import cn.jbolt.common.util.CACHE;

/**   
 * 微信公众号的菜单管理Service
 * @ClassName:  WechatMenuService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月8日 下午11:56:35   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatMenuService extends BaseService<WechatMenu> {
	private WechatMenu dao = new WechatMenu().dao();
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatConfigService wechatConfigService;

	@Override
	protected WechatMenu dao() {
		return dao;
	}
	/**
	 * 检测指定公众平台是否已经有配置了
	 * @param mpId
	 * @return
	 */
	public boolean checkWechatMpinfoInUse(Integer mpId) {
		return exists("mp_id", mpId);
	}
	/**
	 * 删除一个公众平台的菜单配置
	 * @param mpId
	 * @return
	 */
	public Ret deleteByMpId(Integer mpId) {
		return deleteBy(Kv.by("mp_id", mpId));
	}
	/**
	 * 得到一级菜单
	 * @param mpId
	 * @return
	 */
	public List<WechatMenu> getLevel1Menus(Integer mpId) {
		return getCommonList(Kv.by("mp_id", mpId).set("pid",0),"sort_rank");
	}
	/**
	 * 得到二级菜单
	 * @param mpId
	 * @param pid
	 * @return
	 */
	public List<WechatMenu> getLevel2Menus(Integer mpId,Integer pid) {
		return getCommonList(Kv.by("mp_id", mpId).set("pid",pid),"sort_rank");
	}
	/**
	 * 得到子菜单
	 * @param mpId
	 * @param pid
	 * @return
	 */
	public List<WechatMenu> getListByPid(Integer mpId,Integer pid) {
		return getCommonList(Kv.by("mp_id", mpId).set("pid",pid==null?0:pid),"sort_rank");
	}
	/**
	 * 保存
	 * @param userId
	 * @param mpId
	 * @param menu
	 * @return
	 */
	public Ret save(Integer userId, Integer mpId, WechatMenu menu) {
		if(notOk(mpId)||isOk(menu.getId())){return fail(Msg.PARAM_ERROR);}
		if(notOk(menu.getPid())){
			menu.setPid(0);
		}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null){
			return fail("微信公众平台信息不存在");
		}
		int count=getCountByPid(mpId, menu.getPid());
		if(menu.getPid()==0){
			if(count>=3){
				return fail("一级菜单最多3个");
			}
			menu.setType(WechatMenu.TYPE_NONE);
		}else{
			if(count>=5){
				return fail("二级菜单最多5个");
			}
			menu.setType(WechatMenu.TYPE_VIEW);
		}
		menu.setMpId(mpId);
		int rank=getNextSortRank(Kv.by("mp_id",mpId).set("pid", menu.getPid()));
		menu.setSortRank(rank);
		menu.setName("菜单");
	
		boolean success=menu.save();
		if(success){
			menu.setName("菜单_"+menu.getId());
			success=menu.update();
			if(success){
				//TODO 添加日志
			}
		}
		return success?success(menu,Msg.SUCCESS):FAIL;
	}
	/**
	 * 更新
	 * @param userId
	 * @param mpId
	 * @param menu
	 * @return
	 */
	public Ret update(Integer userId, Integer mpId, WechatMenu menu) {
		if(notOk(mpId)||notOk(menu.getId())||notOk(menu.getName())){return fail(Msg.PARAM_ERROR);}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null){
			return fail("微信公众平台信息不存在");
		}
		WechatMenu db  = findById(menu.getId());
		int oldRank=db.getSortRank();
		int newRank=menu.getSortRank();
		if(db.getMpId().intValue()!=mpId.intValue()){
			return fail("操作的数据不是此公众平台下的，请谨慎操作");
		}
		menu.setMpId(mpId);
		if(oldRank!=newRank){
			WechatMenu otherMenu=findFirst(Kv.by("mp_id =",mpId).set("pid =", db.getPid()).set("sort_rank =", newRank).set("id !=", db.getId()),true);
			if(otherMenu!=null){
				otherMenu.setSortRank(oldRank);
				otherMenu.update();
			}
		}
		boolean success=menu.update();
		return success?success(db,Msg.SUCCESS):FAIL;
	}
	/**
	 * 删除
	 * @param userId
	 * @param mpId
	 * @param id
	 * @return
	 */
	public Ret delete(Integer userId,Integer mpId, Integer id) {
		if(MainConfig.DEMO_MODE) {return fail(Msg.DEMO_MODE_CAN_NOT_DELETE);}
		if(notOk(mpId)||notOk(id)){return fail(Msg.PARAM_ERROR);}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null){
			return fail("微信公众平台信息不存在");
		}
		WechatMenu db  = findById(id);
		if(mpId.intValue()!=db.getMpId().intValue()){
			return fail("操作的数据不是此公众平台下的，请谨慎操作");
		}
		if(notOk(db.getPid())){
			int count=getCountByPid(mpId, db.getId());
			if(count>0){
				return fail("存在子菜单，不能删除");
			}
		}
		Ret result=deleteById(id);
		if(result.isOk()){
			doInitRankByPid(mpId,db.getPid());
		}
		return result;
	}
	
	private void doInitRankByPid(Integer mpId, Integer pid) {
		List<WechatMenu> weixinMenus=getListByPid(mpId, pid);
		if(weixinMenus.size()>0){
			for(int i=1;i<=weixinMenus.size();i++){
				weixinMenus.get(i-1).setSortRank(i);
			}
			Db.batchUpdate(weixinMenus,weixinMenus.size());
		}
	}
	
	private int getCountByPid(Integer mpId, Integer pid) {
		return getCount(Kv.by("mp_id", mpId).set("pid",pid));
	}
	/**
	 * 发布自定义菜单
	 * @param userId
	 * @param mpId
	 * @return
	 */
	public Ret publish(Integer userId, Integer mpId) {
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null){
			return fail("微信公众平台信息不存在");
		}
		List<WechatMenu> level1Menus=getLevel1Menus(mpId);
		if(level1Menus==null||level1Menus.size()==0){
			return fail("请添加菜单后再生成");
		}
		List<WechatMenu> level2Menus=null;
		StringBuilder msg=new StringBuilder();
		msg.append("{\"button\":[");
		for(WechatMenu menu:level1Menus){
			level2Menus=getLevel2Menus(mpId,menu.getId());
			if(level2Menus.size()==0){
				switch (menu.getType()) {
				case WechatMenu.TYPE_EVENT:
					msg.append("{\"type\":\"click\",\"name\":\""+menu.getName()+"\",");
					msg.append("\"key\":\""+menu.getValue()+"\"},");
					break;
				case WechatMenu.TYPE_KEYWORDS:
					msg.append("{\"type\":\"click\",\"name\":\""+menu.getName()+"\",");
					msg.append("\"key\":\""+"keywords_"+menu.getValue()+"\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置关键词");	
					}
					break;
				case WechatMenu.TYPE_VIEW:
					msg.append("{\"type\":\"view\",\"name\":\""+menu.getName()+"\",");
					msg.append("\"url\":\""+menu.getValue()+"\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置URL");	
					}
					break;
				case WechatMenu.TYPE_PIC_SYSPHOTO:
					msg.append("{\"type\":\"pic_sysphoto\",\"name\":\""+menu.getName()+"\",");
					msg.append("\"key\":\""+menu.getValue()+"\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置事件KEY");	
					}
					break;
				case WechatMenu.TYPE_PIC_PHOTO_OR_ALBUM:
					msg.append("{\"type\":\"pic_photo_or_album\",\"name\":\""+menu.getName()+"\",");
					msg.append("\"key\":\""+menu.getValue()+"\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置事件KEY");	
					}
					break;
				case WechatMenu.TYPE_PIC_WEIXIN:
					msg.append("{\"type\":\"pic_weixin\",\"name\":\""+menu.getName()+"\",");
					msg.append("\"key\":\""+menu.getValue()+"\"},");
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置事件KEY");	
					}
					break;
				case WechatMenu.TYPE_MINIPROGRAM:
					msg.append("{\"type\":\"miniprogram\",\"name\":\""+menu.getName()+"\",");
					msg.append("\"appid\":\""+menu.getAppId()+"\",");
					msg.append("\"pagepath\":\""+menu.getPagePath()+"\",");
					msg.append("\"url\":\""+menu.getValue()+"\"},");
					if(notOk(menu.getAppId())){
						return fail(menu.getName()+"未设置跳转小程序的APPID");	
					}
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置跳转小程序的页面路径pagePath");	
					}
					if(notOk(menu.getValue())){
						return fail(menu.getName()+"未设置默认URL");	
					}
					break;
				case WechatMenu.TYPE_NONE:
					return fail(menu.getName()+"请添加二级微信菜单");
				}
			}else{
				msg.append("{\"name\":\""+menu.getName()+"\",\"sub_button\":[");
				for(WechatMenu menu2:level2Menus){
					
					switch (menu2.getType()) {
					case WechatMenu.TYPE_EVENT:
						msg.append("{\"type\":\"click\",\"name\":\""+menu2.getName()+"\",");
						msg.append("\"key\":\""+menu2.getValue()+"\"},");
						break;
					case WechatMenu.TYPE_KEYWORDS:
						msg.append("{\"type\":\"click\",\"name\":\""+menu2.getName()+"\",");
						msg.append("\"key\":\""+"keywords_"+menu2.getValue()+"\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置关键词");	
						}
						break;
					case WechatMenu.TYPE_VIEW:
						msg.append("{\"type\":\"view\",\"name\":\""+menu2.getName()+"\",");
						msg.append("\"url\":\""+menu2.getValue()+"\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置URL");	
						}
						break;
					case WechatMenu.TYPE_PIC_SYSPHOTO:
						msg.append("{\"type\":\"pic_sysphoto\",\"name\":\""+menu2.getName()+"\",");
						msg.append("\"key\":\""+menu2.getValue()+"\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置事件KEY");	
						}
						break;
					case WechatMenu.TYPE_PIC_PHOTO_OR_ALBUM:
						msg.append("{\"type\":\"pic_photo_or_album\",\"name\":\""+menu2.getName()+"\",");
						msg.append("\"key\":\""+menu2.getValue()+"\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置事件KEY");	
						}
						break;
					case WechatMenu.TYPE_PIC_WEIXIN:
						msg.append("{\"type\":\"pic_weixin\",\"name\":\""+menu2.getName()+"\",");
						msg.append("\"key\":\""+menu2.getValue()+"\"},");
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置事件KEY");	
						}
						break;
					case WechatMenu.TYPE_MINIPROGRAM:
						msg.append("{\"type\":\"miniprogram\",\"name\":\""+menu2.getName()+"\",");
						msg.append("\"appid\":\""+menu2.getAppId()+"\",");
						msg.append("\"pagepath\":\""+menu2.getPagePath()+"\",");
						msg.append("\"url\":\""+menu2.getValue()+"\"},");
						if(notOk(menu2.getAppId())){
							return fail(menu2.getName()+"未设置跳转小程序的APPID");	
						}
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置跳转小程序的页面路径pagePath");	
						}
						if(notOk(menu2.getValue())){
							return fail(menu2.getName()+"未设置默认URL");	
						}
						break;
					}
				}
				msg.setLength(msg.length()-1);
				msg.append("]},");
			}
		}
		msg.setLength(msg.length()-1);
		msg.append("]}");
		String appId=CACHE.me.getWechatConfigAppId(mpId);
		if(StrKit.notBlank(appId)){
			ApiConfigKit.setThreadLocalAppId(appId);
			try {
				ApiResult apiResult=MenuApi.createMenu(msg.toString());
				if(apiResult.isSucceed()==false){
					return fail(apiResult.getErrorMsg());
				}
			} catch (RuntimeException e) {
				return fail(e.getMessage());
			}finally {
				ApiConfigKit.removeThreadLocalAppId();
			}
		}else{
			return fail(wechatMpinfo.getName()+"基础配置不正确!");
		}
		
		return SUCCESS;
	}
	


}
