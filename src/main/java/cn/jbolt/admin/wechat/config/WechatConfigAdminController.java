package cn.jbolt.admin.wechat.config;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.upload.UploadFile;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MediaApi;
import com.jfinal.weixin.sdk.api.MediaApi.MediaType;

import cn.hutool.core.io.FileUtil;
import cn.jbolt._admin.permission.CheckPermission;
import cn.jbolt._admin.permission.PermissionKey;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.base.BaseController;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.WechatConfig;
import cn.jbolt.common.model.WechatMpinfo;
import cn.jbolt.common.util.RealUrlUtil;

/**   
 * 微信公众平台配置
 * @ClassName:  WechatConfigAdminController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月12日 下午8:35:23   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */

public class WechatConfigAdminController extends BaseController {
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Inject
	private WechatConfigService service;
	@CheckPermission(PermissionKey.WECHAT_CONFIG_BASEMGR)
	public void baseMgr(){
		Integer mpId=getInt(0);
		List<WechatConfig> configs=service.checkAndInitConfig(mpId,WechatConfig.TYPE_BASE);
		set("configList",configs);
		set("configCount",configs.size());
		set("mpId", mpId);
		render("basemgr.html");
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_EXTRAMGR)
	public void extraMgr(){
		Integer mpId=getInt(0);
		List<WechatConfig> configs=service.checkAndInitConfig(mpId,WechatConfig.TYPE_EXTRA);
		for(WechatConfig config:configs) {
			if(config.getConfigKey().equals(WechatConfigKey.MUSIC_POST_MEDIAID)&&isOk(config.getConfigValue())) {
				String value=JFinal.me().getConstants().getBaseUploadPath()+"/wechatconfig/musicpost/"+config.getConfigValue()+".jpg";
				config.put("imgUrl",RealUrlUtil.getWechatImage(value));
			}
		}
		set("configList",configs);
		set("configCount",configs.size());
		set("mpId", mpId);
		render("extramgr.html");
	}
	
	/**
	 * 上传图片到微信素材库
	 */
	@CheckPermission(PermissionKey.WECHAT_CONFIG_BASEMGR)
	public void uploadWechatMusicImgMedia(){
		UploadFile file=getFile("file","wechatconfig/musicpost");
		if(notImage(file)){
			renderJsonFail("请上传图片类型文件");
			return;
		}
		Integer mpId=getInt(0);
		String appId=service.getWechatConfigAppId(mpId);
		if(notOk(appId)) {
			renderJsonFail("当前操作微信公众平台，配置异常：APPID");
			return;
		}
		ApiConfigKit.setThreadLocalAppId(appId);
		ApiResult apiResult=null;
		try {
			apiResult=MediaApi.addMaterial(file.getFile(), MediaType.IMAGE);
		} finally {
			ApiConfigKit.removeThreadLocalAppId();
		}
		if(apiResult.isSucceed()) {
			String mediaId=apiResult.getStr("media_id");
			FileUtil.rename(file.getFile(), mediaId+".jpg", false, true);
			renderJsonData(mediaId);
		}else {
			renderJsonData(apiResult.getErrorMsg());
		}
		
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_BASEMGR)
	public void submitBaseConfig(){
		submitConfig(WechatConfig.TYPE_BASE);
		
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_EXTRAMGR)
	public void submitExtraConfig(){
		submitConfig(WechatConfig.TYPE_EXTRA);
		
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_PAYMGR)
	public void payMgr(){
		Integer mpId=getInt(0);
		List<WechatConfig> configs=service.checkAndInitConfig(mpId,WechatConfig.TYPE_PAY);
		set("configList",configs);
		set("configCount",configs.size());
		set("mpId", mpId);
		render("paymgr.html");
	}
	private void submitConfig(int type){
		Integer configCount=getInt("configCount");
		if(notOk(configCount)){
			renderJsonFail(Msg.PARAM_ERROR);
		}else{
			Integer mpId=getInt("mpId");
			WechatMpinfo mpinfo=wechatMpinfoService.findById(mpId);
			if(mpinfo==null){
				renderJsonFail("微信公众平台信息不存在");
				return;
			}
			if(type==WechatConfig.TYPE_EXTRA) {
				if(!mpinfo.getEnable()){
					renderJsonFail("请在启用状态下操作更新此配置");
					return;
				}
				
			}else {
				if(mpinfo.getEnable()!=null&&mpinfo.getEnable()) {
					renderJsonFail("微信公众平台启用时无法更新配置信息，请在非启用状态下操作");
					return;
				}
			}
			 
			
				
			boolean isWxa=mpinfo.getType()==WechatMpinfo.TYPE_XCX;
			List<WechatConfig> configs=new ArrayList<WechatConfig>();
			for(int i=0;i<configCount;i++){
				configs.add(getModel(WechatConfig.class,"config["+i+"]"));
			}
			renderJson(service.updateConfigs(getSessionAdminUserId(),mpId,type,isWxa,configs));
		}
	}
	@CheckPermission(PermissionKey.WECHAT_CONFIG_PAYMGR)
	public void submitPayConfig(){
		submitConfig(WechatConfig.TYPE_PAY);
	}
}
