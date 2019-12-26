package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt.admin.wechat.autoreply.WechatMsgController;
import cn.jbolt.admin.wechat.autoreply.WechatWxaMsgController;
/**
 * 微信公众平台前端配置
 * @ClassName:  WechatRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月8日15:03:24   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatRoutes extends Routes {

	@Override
	public void config() {
		this.setMappingSuperClass(true);
		this.add("/wx/msg", WechatMsgController.class);
		this.add("/wxa/msg", WechatWxaMsgController.class);
	}

}
