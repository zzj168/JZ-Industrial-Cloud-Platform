package cn.jbolt.admin.wechat.autoreply;

import com.jfinal.core.Controller;

import cn.hutool.core.util.ArrayUtil;
import cn.jbolt.base.JBoltValidator;
import cn.jbolt.common.config.Msg;

public class WechatAutoReplyMgrValidator extends JBoltValidator {
	private static final String[] actionNames= new String[]{"toggleEnable"};
	@Override
	protected void validate(Controller c) {
		validateJBoltInteger(0, Msg.PARAM_ERROR);
		String actionName=getActionMethodName();
		if(ArrayUtil.contains(actionNames, actionName)) {
			validateJBoltInteger(1,  Msg.PARAM_ERROR+":数据ID");
		}
	}
}
