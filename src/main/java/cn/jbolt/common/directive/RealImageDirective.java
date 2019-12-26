package cn.jbolt.common.directive;

import cn.jbolt.common.util.RealUrlUtil;

/**
 * 正确输出图片地址的指令
 * @ClassName:  RealImageDirective   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月14日 下午10:08:45   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class RealImageDirective extends RealUrlDirective {
	@Override
	protected String getRealUrl(Object value,Object defaultValue) {
		return RealUrlUtil.getImage(value, defaultValue);
	}
}
