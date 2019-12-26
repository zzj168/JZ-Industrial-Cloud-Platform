package cn.jbolt.common.config;

import java.util.Date;
import java.util.List;

import com.jfinal.aop.Aop;

import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.jbolt._admin.globalconfig.GlobalConfigService;
import cn.jbolt._admin.userconfig.UserConfigService;
import cn.jbolt.common.model.GlobalConfig;
import cn.jbolt.common.model.UserConfig;

/**
 * 一切版本过渡之间的自动处理升级业务
 * 在这里实现，文件由JBolt官方操作，他人请勿修改
 * @ClassName:  JBoltAutoUpgrade   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年12月7日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JBoltAutoUpgrade {
	public static final JBoltAutoUpgrade me=new JBoltAutoUpgrade();
	private static final Log LOG=LogFactory.get();
	private JBoltAutoUpgrade() {
	}
	/**
	 * 执行
	 */
	public void exe() {
		LOG.info("JBolt自动升级：开始执行");
		up20191207_globalconfig();
		up20191207_userconfig();
		LOG.info("JBolt自动升级：执行完成");
	}
	public boolean canUpgrade(String upgradeDate) {
		Date date_upgradeDate=DateUtil.parse(upgradeDate);
		Date now=new Date();
		return now.after(date_upgradeDate);
	}
	/**
	 * 临时方法 升级global Config
	 */
	public void up20191207_globalconfig() {
		if(canUpgrade("2019-12-07 23:59:59")) {
			LOG.info("JBolt自动升级：正在执行-up20191207_globalconfig");
			    GlobalConfigService globalConfigService=Aop.get(GlobalConfigService.class);
			    List<GlobalConfig> rewardConfigs=globalConfigService.getCommonListByKeywords("REWARD", "id", "config_key");
				if(rewardConfigs!=null&&rewardConfigs.size()>0) {
					for(GlobalConfig reConfig:rewardConfigs) {
						reConfig.delete();
						LOG.info("删除废弃的全局配置项："+reConfig.getName()+":"+reConfig.getConfigKey());
					}
				}
				List<GlobalConfig> globalConfigs=globalConfigService.getCommonListByKeywords("CONFIG_KEY_", "id", "config_key");
				if(globalConfigs!=null&&globalConfigs.size()>0) {
					for(GlobalConfig config:globalConfigs) {
						config.setConfigKey(config.getConfigKey().replace("CONFIG_KEY_", ""));
						config.update();
					}
				}
				//增加两个新的全局配置项
				globalConfigService.checkAndInitConfig(1, GlobalConfigKey.JBOLT_ACTION_REPORT_WRITER);
				globalConfigService.checkAndInitConfig(1, GlobalConfigKey.JBOLT_AUTO_CACHE_LOG);
				
			LOG.info("JBolt自动升级：执行完毕-up20191207_globalconfig");
		}
	}
	/**
	 * 临时方法 升级user Config
	 */
	public void up20191207_userconfig() {
		if(canUpgrade("2019-12-07 23:59:59")) {
			LOG.info("JBolt自动升级：正在执行-up20191207_userconfig");
			    UserConfigService userConfigService=Aop.get(UserConfigService.class);
				List<UserConfig> userConfigs=userConfigService.getCommonListByKeywords("CONFIG_KEY_", "id", "config_key");
				if(userConfigs!=null&&userConfigs.size()>0) {
					for(UserConfig config:userConfigs) {
						config.setConfigKey(config.getConfigKey().replace("CONFIG_KEY_", ""));
						config.update();
					}
				}
				
			LOG.info("JBolt自动升级：执行完毕-up20191207_userconfig");
		}
	}
}
