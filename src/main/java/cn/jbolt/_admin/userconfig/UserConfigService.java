package cn.jbolt._admin.userconfig;

import java.util.Date;
import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.GlobalConfigKey;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.db.sql.Sql;
import cn.jbolt.common.model.SystemLog;
import cn.jbolt.common.model.UserConfig;
import cn.jbolt.common.util.CACHE;
/**
 * 用户自身配置
 * @ClassName:  UserConfigService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月25日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class UserConfigService extends BaseService<UserConfig> {
	private UserConfig dao = new UserConfig().dao();
	@Override
	protected UserConfig dao() {
		return dao;
	}
	/**
	 * 获取用户配置
	 * @param userId
	 * @return
	 */
	public List<UserConfig> getAdminList(Integer userId) {
		checkAndInit(userId);
		return getCommonList(Kv.by("user_id", userId));
	}

	/**
	 * 更新
	 * @param userId
	 * @param globalConfig
	 * @return
	 */
	public Ret update(Integer userId, UserConfig userConfig) {
		if (userConfig == null || notOk(userConfig.getId()) || notOk(userConfig.getConfigKey())
				|| notOk(userConfig.getConfigValue())) {
			return fail(Msg.PARAM_ERROR);
		}
		UserConfig db=findById(userConfig.getId());
		if(db==null){return fail(Msg.DATA_NOT_EXIST);}
		db.setConfigValue(userConfig.getConfigValue());
		boolean success=processUpdateConfig(db);
		return ret(success);
	}


	/**
	 * 检测和初始化配置
	 */
	public void checkAndInit(Integer userId) {
		processDatasIfValueTypeIsNull(userId);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_ADMIN_STYLE);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_ADMIN_WITH_TABS);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_LOGIN_FORM_STYLE_GLASS);
		checkAndInitConfig(userId, GlobalConfigKey.JBOLT_LOGIN_BGIMG_BLUR);
	}
	/**
	 * 处理valueType=null的组件
	 */
	private void processDatasIfValueTypeIsNull(Integer userId) {
		Sql sql=selectSql().isNull("value_type").eq("user_id", userId);
		List<UserConfig> configs=find(sql.toSql());
		if(isOk(configs)) {
			for(UserConfig config:configs) {
				switch (config.getConfigKey()) {
				case GlobalConfigKey.JBOLT_ADMIN_STYLE:
					config.setValueType(UserConfig.TYPE_STRING);
					break;
				case GlobalConfigKey.JBOLT_ADMIN_WITH_TABS:
					config.setValueType(UserConfig.TYPE_BOOLEAN);
					break;
				case GlobalConfigKey.JBOLT_LOGIN_FORM_STYLE_GLASS:
					config.setValueType(UserConfig.TYPE_BOOLEAN);
					break;
				case GlobalConfigKey.JBOLT_LOGIN_BGIMG_BLUR:
					config.setValueType(UserConfig.TYPE_BOOLEAN);
					break;
				}
				config.deleteKeyCache();
			}
			Db.batchUpdate(configs, configs.size());
		}
		
	}
	/**
	 * 检查并初始化全局配置表数据
	 * @param userId
	 * @param configKey
	 */
	public void checkAndInitConfig(Integer userId, String configKey) {
		boolean checkExist = checkUserExistConfig(userId, configKey);
		if (checkExist == false) {
			UserConfig config = new UserConfig();
			config.setConfigKey(configKey);
			String value=null;
			switch (configKey) {
			case GlobalConfigKey.JBOLT_ADMIN_STYLE:
				config.setName("系统Admin后台样式");
				value=CACHE.me.getGlobalConfigValue(GlobalConfigKey.JBOLT_ADMIN_STYLE);
				config.setConfigValue(isOk(value)?value:"default");
				config.setValueType(UserConfig.TYPE_STRING);
				break;
			case GlobalConfigKey.JBOLT_ADMIN_WITH_TABS:
				config.setName("系统Admin后台是否启用多选项卡");
				value=CACHE.me.getGlobalConfigValue(GlobalConfigKey.JBOLT_ADMIN_WITH_TABS);
				config.setConfigValue(isOk(value)?value:"false");
				config.setValueType(UserConfig.TYPE_BOOLEAN);
				break;
			case GlobalConfigKey.JBOLT_LOGIN_FORM_STYLE_GLASS:
				config.setName("系统登录页面是否启用透明玻璃风格");
				value=CACHE.me.getGlobalConfigValue(GlobalConfigKey.JBOLT_LOGIN_FORM_STYLE_GLASS);
				config.setConfigValue(isOk(value)?value:"false");
				config.setValueType(UserConfig.TYPE_BOOLEAN);
				break;
			case GlobalConfigKey.JBOLT_LOGIN_BGIMG_BLUR:
				config.setName("系统登录页面背景图是否启用模糊风格");
				value=CACHE.me.getGlobalConfigValue(GlobalConfigKey.JBOLT_LOGIN_BGIMG_BLUR);
				config.setConfigValue(isOk(value)?value:"false");
				config.setValueType(UserConfig.TYPE_BOOLEAN);
				break;
			}
			config.setCreateTime(new Date());
			config.setUserId(userId);
			config.setUpdateTime(new Date());
			config.save();
		}

	}
	/**
	 * 检测用户相同配置是否存在
	 * @param userId
	 * @param configKey
	 * @return
	 */
	private boolean checkUserExistConfig(Integer userId, String configKey) {
		UserConfig config=findFirst(Kv.by("user_id", userId).set("config_key",configKey));
		return config!=null;
	}
	/**
	 * 根据configKey获取全局配置
	 * @param configKey
	 * @return
	 */
	public UserConfig getByConfigKey(Integer userId,String configKey) {
		return findFirst(Kv.by("config_key", configKey).set("user_id",userId));
	}
	/**
	 * 切换Boolean类型config
	 * @param userId
	 * @param id
	 * @return
	 */
	public Ret toggleBooleanConfig(Integer userId, Integer id) {
		UserConfig userConfig=findById(id);
		if(userConfig==null) {return fail(Msg.DATA_NOT_EXIST);}
		if(userConfig.getValueType().equals(UserConfig.TYPE_BOOLEAN)==false) {
			return fail("配置项的值必须为Boolean类型");
		}
		if(userConfig.getConfigValue().equals("true")) {
			userConfig.setConfigValue("false");
		}else {
			userConfig.setConfigValue("true");
		}
		boolean success=processUpdateConfig(userConfig);
		return success?successWithData(userConfig.getConfigKey()):FAIL;
	}
	/**
	 * 改变配置项值
	 * @param userId
	 * @param id
	 * @param value
	 * @return
	 */
	public Ret changeStringValue(Integer userId, Integer id, String value) {
		if(notOk(value)||notOk(id)) {return fail(Msg.PARAM_ERROR);}
		UserConfig userConfig=findById(id);
		if(userConfig==null) {return fail(Msg.DATA_NOT_EXIST);}
		if(userConfig.getValueType().equals(UserConfig.TYPE_STRING)==false) {
			return fail("配置项的值必须为String类型");
		}
		userConfig.setConfigValue(value);
		boolean success=processUpdateConfig(userConfig);
		return ret(success);
	}
	/**
	 * 更新操作 附带处理日志和缓存
	 * @param userConfig
	 * @return
	 */
	private boolean processUpdateConfig(UserConfig userConfig) {
		userConfig.setUpdateTime(new Date());
		boolean success=userConfig.update();
		if(success) {
			//增加日志
			addUpdateSystemLog(userConfig.getId(), userConfig.getUserId(), SystemLog.TARGETTYPE_USER_CONFIG, userConfig.getName()+"为:["+userConfig.getConfigValue()+"]");
		}
		return success;
	}

}
