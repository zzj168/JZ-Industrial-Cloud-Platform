
package cn.jbolt._admin.globalconfig;

import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.GlobalConfigKey;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.db.sql.Sql;
import cn.jbolt.common.model.GlobalConfig;
import cn.jbolt.common.model.SystemLog;

/**
 * 全局配置 service
 * 
 * @author 小木 qq:909854136
 * @version 创建时间：2018年12月25日 下午11:18:26
 */
public class GlobalConfigService extends BaseService<GlobalConfig> {
	private GlobalConfig dao = new GlobalConfig().dao();
	@Override
	protected GlobalConfig dao() {
		return dao;
	}
	
	/**
	 * 更新
	 * @param userId
	 * @param globalConfig
	 * @return
	 */
	public Ret update(Object userId, GlobalConfig globalConfig) {
		if (globalConfig == null || notOk(globalConfig.getId()) || notOk(globalConfig.getConfigKey())
				|| notOk(globalConfig.getConfigValue())) {
			return fail(Msg.PARAM_ERROR);
		}
		GlobalConfig db=findById(globalConfig.getId());
		if(db==null){return fail(Msg.DATA_NOT_EXIST);}
		db.setObjectUpdateUserId(userId);
		db.setConfigValue(globalConfig.getConfigValue());
		boolean success = db.update();
		if (success) {
			refreshMainConfig(globalConfig.getConfigKey());
			//增加日志
			addUpdateSystemLog(db.getId(), userId, SystemLog.TARGETTYPE_GLOBAL_CONFIG, db.getName());
		}
		return ret(success);
	}

	/**
	 * 判断与MainConfig有关的配置 更新调用
	 * @param configKey
	 */
	private void refreshMainConfig(String configKey) {
		switch (configKey) {
			case GlobalConfigKey.JBOLT_ACTION_REPORT_WRITER:
				MainConfig.configActionReportWriter();
				break;
			case GlobalConfigKey.JBOLT_AUTO_CACHE_LOG:
				MainConfig.configJBoltAutoCacheLog();
				break;
		}
	}

	/**
	 * 处理valueType=null的组件
	 */
	private void processDatasIfValueTypeIsNull() {
		Sql sql=selectSql().isNull("value_type");
		List<GlobalConfig> configs=find(sql.toSql());
		if(isOk(configs)) {
			for(GlobalConfig config:configs) {
				switch (config.getConfigKey()) {
				case GlobalConfigKey.WECHAT_MP_SERVER_DOMAIN:
					config.setValueType(GlobalConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.WECHAT_WXA_SERVER_DOMAIN:
					config.setValueType(GlobalConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.WECHAT_ASSETS_SERVER_DOMAIN:
					config.setValueType(GlobalConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.SYSTEM_NAME:
					config.setValueType(GlobalConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.SYSTEM_ADMIN_LOGO:
					config.setValueType(GlobalConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.SYSTEM_COPYRIGHT_COMPANY:
					config.setValueType(GlobalConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.SYSTEM_COPYRIGHT_LINK:
					config.setValueType(GlobalConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.JBOLT_ADMIN_STYLE:
					config.setValueType(GlobalConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.JBOLT_ADMIN_WITH_TABS:
					config.setValueType(GlobalConfig.TYPE_BOOLEAN);
					break;
				case GlobalConfigKey.JBOLT_LOGIN_FORM_STYLE_GLASS:
					config.setValueType(GlobalConfig.TYPE_BOOLEAN);
					break;
				case GlobalConfigKey.JBOLT_LOGIN_USE_CAPTURE:
					config.setValueType(GlobalConfig.TYPE_BOOLEAN);
					break;
				case GlobalConfigKey.JBOLT_LOGIN_BGIMG_BLUR:
					config.setValueType(GlobalConfig.TYPE_BOOLEAN);
					break;
				case GlobalConfigKey.JBOLT_LOGIN_CAPTURE_TYPE:
					config.setValueType(GlobalConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.JBOLT_LOGIN_BGIMG:
					config.setValueType(GlobalConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.JBOLT_ACTION_REPORT_WRITER:
					config.setValueType(GlobalConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.JBOLT_AUTO_CACHE_LOG:
					config.setValueType(GlobalConfig.TYPE_BOOLEAN);
					break;
				}
				config.deleteKeyCache();
			}
			Db.batchUpdate(configs, configs.size());
		}
		
	}
	/**
	 * 检测和初始化配置
	 */
	public void checkAndInit(Object userId) {
		processDatasIfValueTypeIsNull();
		checkAndInitConfig(userId, GlobalConfigKey.WECHAT_MP_SERVER_DOMAIN);
		checkAndInitConfig(userId, GlobalConfigKey.WECHAT_WXA_SERVER_DOMAIN);
		checkAndInitConfig(userId, GlobalConfigKey.WECHAT_ASSETS_SERVER_DOMAIN);
		checkAndInitConfig(userId, GlobalConfigKey.SYSTEM_NAME);
		checkAndInitConfig(userId, GlobalConfigKey.SYSTEM_ADMIN_LOGO);
		checkAndInitConfig(userId, GlobalConfigKey.SYSTEM_COPYRIGHT_COMPANY);
		checkAndInitConfig(userId, GlobalConfigKey.SYSTEM_COPYRIGHT_LINK);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_ADMIN_STYLE);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_ADMIN_WITH_TABS);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_LOGIN_FORM_STYLE_GLASS);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_LOGIN_USE_CAPTURE);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_LOGIN_BGIMG_BLUR);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_LOGIN_CAPTURE_TYPE);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_LOGIN_BGIMG);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_ACTION_REPORT_WRITER);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_AUTO_CACHE_LOG);
	}
	/**
	 * 检查并初始化全局配置表数据
	 * @param userId
	 * @param configKey
	 */
	public void checkAndInitConfig(Object userId, String configKey) {
		boolean checkExist = exists("config_key", configKey);
		if (checkExist == false) {
			GlobalConfig config = new GlobalConfig();
			config.setConfigKey(configKey);
			switch (configKey) {
			case GlobalConfigKey.WECHAT_MP_SERVER_DOMAIN:
				config.setName("微信公众号_服务器配置_根URL");
				config.setConfigValue(PropKit.get("domain")+"/wx/msg");
				config.setValueType(GlobalConfig.TYPE_STRING);
				break;
			case GlobalConfigKey.WECHAT_WXA_SERVER_DOMAIN:
				config.setName("微信小程序_客服消息推送配置_根URL");
				config.setConfigValue(PropKit.get("domain")+"/wxa/msg");
				config.setValueType(GlobalConfig.TYPE_STRING);
				break;
			case GlobalConfigKey.WECHAT_ASSETS_SERVER_DOMAIN:
				config.setName("微信_静态资源_根URL");
				config.setConfigValue(PropKit.get("domain"));
				config.setValueType(GlobalConfig.TYPE_STRING);
				break;
			case GlobalConfigKey.SYSTEM_NAME:
				config.setName("系统名称");
				config.setConfigValue("JBolt极速开发平台");
				config.setValueType(GlobalConfig.TYPE_STRING);
				break;
			case GlobalConfigKey.SYSTEM_ADMIN_LOGO:
				config.setName("系统后台主页LOGO");
				config.setConfigValue("/assets/img/logo.png");
				config.setValueType(GlobalConfig.TYPE_STRING);
				break;
			case GlobalConfigKey.SYSTEM_COPYRIGHT_COMPANY:
				config.setName("系统版权所有人");
				config.setConfigValue("©JBolt(JBOLT.CN)");
				config.setValueType(GlobalConfig.TYPE_STRING);
				break;
			case GlobalConfigKey.SYSTEM_COPYRIGHT_LINK:
				config.setName("系统版权所有人的网址链接");
				config.setConfigValue("http://jbolt.cn");
				config.setValueType(GlobalConfig.TYPE_STRING);
				break;
			case GlobalConfigKey.JBOLT_ADMIN_STYLE:
				config.setName("系统Admin后台样式");
				config.setConfigValue("default");
				config.setValueType(GlobalConfig.TYPE_STRING);
				break;
			case GlobalConfigKey.JBOLT_ADMIN_WITH_TABS:
				config.setName("系统Admin后台是否启用多选项卡");
				config.setConfigValue("false");
				config.setValueType(GlobalConfig.TYPE_BOOLEAN);
				break;
			case GlobalConfigKey.JBOLT_LOGIN_FORM_STYLE_GLASS:
				config.setName("系统登录页面是否启用透明玻璃风格");
				config.setConfigValue("false");
				config.setValueType(GlobalConfig.TYPE_BOOLEAN);
				break;
			case GlobalConfigKey.JBOLT_LOGIN_USE_CAPTURE:
				config.setName("系统登录页面是否启用验证码");
				config.setConfigValue("true");
				config.setValueType(GlobalConfig.TYPE_BOOLEAN);
				break;
			case GlobalConfigKey.JBOLT_LOGIN_BGIMG_BLUR:
				config.setName("系统登录页面背景图是否启用模糊风格");
				config.setConfigValue("false");
				config.setValueType(GlobalConfig.TYPE_BOOLEAN);
				break;
			case GlobalConfigKey.JBOLT_LOGIN_CAPTURE_TYPE:
				config.setName("系统登录页验证码类型");
				config.setConfigValue(GlobalConfig.CAPTCHA_TYPE_DEFAULT);
				config.setValueType(GlobalConfig.TYPE_STRING);
				break;
			case GlobalConfigKey.JBOLT_LOGIN_BGIMG:
				config.setName("系统登录页背景图");
				config.setConfigValue("/assets/css/img/login_bg.jpg");
				config.setValueType(GlobalConfig.TYPE_STRING);
				break;
			case GlobalConfigKey.JBOLT_ACTION_REPORT_WRITER:
				config.setName("JFinal Action Report输出方式");
				config.setConfigValue("sysout");
				config.setValueType(GlobalConfig.TYPE_STRING);
				MainConfig.ACTION_REPORT_WRITER="sysout";
				break;
			case GlobalConfigKey.JBOLT_AUTO_CACHE_LOG:
				config.setName("JBolt自动缓存Debug日志");
				config.setConfigValue("false");
				config.setValueType(GlobalConfig.TYPE_BOOLEAN);
				MainConfig.JBOLT_AUTO_CACHE_LOG=false;
				break;
			}
			config.setObjectUserId(userId);
			config.setObjectUpdateUserId(userId);
			config.save();
		}

	}
	/**
	 * 根据configKey获取全局配置
	 * @param configKey
	 * @return
	 */
	public GlobalConfig getByConfigKey(String configKey) {
		return findFirst(Kv.by("config_key", configKey));
	}
	/**
	 * 更新jbolt Style
	 * @param userId
	 * @param style
	 * @return
	 */
	public Ret updateJboltStyle(Object userId, String style) {
		GlobalConfig globalConfig=getByConfigKey(GlobalConfigKey.JBOLT_ADMIN_STYLE);
		if(globalConfig==null) {return fail(Msg.DATA_NOT_EXIST);}
		globalConfig.setObjectUpdateUserId(userId);
		globalConfig.setConfigValue(style);
		boolean success = globalConfig.update();
		if (success) {
			//增加日志
			addUpdateSystemLog(globalConfig.getId(), userId, SystemLog.TARGETTYPE_GLOBAL_CONFIG, globalConfig.getName());
		}
		return ret(success);
	}


	
	
}
