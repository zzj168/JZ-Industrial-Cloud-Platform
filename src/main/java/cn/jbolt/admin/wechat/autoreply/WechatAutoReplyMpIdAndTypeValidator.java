package cn.jbolt.admin.wechat.autoreply;

import com.jfinal.core.Controller;

import cn.jbolt.base.JBoltValidator;
import cn.jbolt.common.config.Msg;

public class WechatAutoReplyMpIdAndTypeValidator extends JBoltValidator {

	@Override
	protected void validate(Controller c) {
		validateJBoltInteger(0, Msg.PARAM_ERROR+":微信公众平台mpId");
		validateJBoltInteger(1, Msg.PARAM_ERROR+":微信公众平台type");
		if(getActionMethodName().equals("edit")) {
			validateJBoltInteger(2, Msg.PARAM_ERROR+":数据ID");
		}
	}
 

}
