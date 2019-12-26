package cn.jbolt.base;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
/**
 * JBolt封装的Validator层
 * @ClassName:  JBoltValidator   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月17日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public abstract class JBoltValidator extends Validator {
	protected static final String ERRORKEY="msg";
	protected void validateJBoltInteger(String value,String errorMessage) {
		if (value != null && (value.startsWith("N") || value.startsWith("n"))) {
			value = "-" + value.substring(1);
		}
		if (StrKit.isBlank(value)) {
			setErrorMsg(errorMessage);
			return ;
		}
		try {
			int temp = Integer.parseInt(value.trim());
			if (temp <= 0) {
				setErrorMsg(errorMessage);
			}
		}
		catch (Exception e) {
			setErrorMsg(errorMessage);
		}
	}
	protected void setErrorMsg(String msg) {
		addError(ERRORKEY, msg);
	}
	/**
	 * Jbolt里专门处理校验Int类型数据
	 */
	protected void validateJBoltInteger(int index,String errorMessage) {
		validateJBoltInteger(controller.getPara(index), errorMessage);
	}
	

	@Override
	protected void handleError(Controller c) {
		ControllerKit.renderValidatorError(c);
	}
}
