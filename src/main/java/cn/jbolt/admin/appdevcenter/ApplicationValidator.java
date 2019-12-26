package cn.jbolt.admin.appdevcenter;

import com.jfinal.core.Controller;

import cn.jbolt.base.JBoltValidator;
import cn.jbolt.common.config.Msg;
/**
   * 应用开发中心 管理应用 参数校验
 * @ClassName:  ApplicationValidator   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年6月22日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class ApplicationValidator extends JBoltValidator {
	@Override
	protected void validate(Controller c) {
		validateJBoltInteger(0,  Msg.PARAM_ERROR+":数据ID");
	}


}
