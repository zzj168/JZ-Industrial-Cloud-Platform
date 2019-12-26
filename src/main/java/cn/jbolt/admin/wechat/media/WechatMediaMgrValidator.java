package cn.jbolt.admin.wechat.media;

import com.jfinal.core.Controller;

import cn.hutool.core.util.ArrayUtil;
import cn.jbolt.base.JBoltValidator;
import cn.jbolt.common.config.Msg;
/**
 * 微信公众平台资源库
 * @ClassName:  WechatReplyContentMgrValidator   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年6月22日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatMediaMgrValidator extends JBoltValidator {
	private static final String[] actionNames= new String[]{"chooseIt"};
	@Override
	protected void validate(Controller c) {
		validateJBoltInteger(0, Msg.PARAM_ERROR+":微信公众平台mpId");
		String actionName=getActionMethodName();
		if(ArrayUtil.contains(actionNames, actionName)) {
			validateJBoltInteger(1,  Msg.PARAM_ERROR+":数据ID");
		}
	}


}
