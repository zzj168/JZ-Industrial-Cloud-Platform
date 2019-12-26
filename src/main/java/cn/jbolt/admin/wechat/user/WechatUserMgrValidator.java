package cn.jbolt.admin.wechat.user;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;

import cn.jbolt.base.JBoltValidator;
import cn.jbolt.common.config.Msg;

public class WechatUserMgrValidator extends JBoltValidator {
	@Inject
	private WechatUserService wechatUserService;
	@Override
	protected void validate(Controller c) {
		validateJBoltInteger(0, Msg.PARAM_ERROR);
		Integer mpId=c.getInt(0);
		if(mpId!=null&&mpId>0) {
			boolean exist=wechatUserService.tableExist(mpId);
			if(exist==false) {
				setErrorMsg(Msg.TABLE_NOT_EXIST);
			}
		}
	}
}
