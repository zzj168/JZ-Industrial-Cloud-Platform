package cn.jbolt.admin.wechat.article;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.model.WechatArticle;

/**
 * 微信图文信息管理
 * 包括本地和公众号内的数据   
 * @ClassName:  WechatArticleService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月9日 上午12:06:52   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WechatArticleService extends BaseService<WechatArticle> {
	private WechatArticle dao = new WechatArticle().dao();
	@Override
	protected WechatArticle dao() {
		return dao;
	}

}
