package cn.jbolt.admin.mall.goods;

import java.util.List;

import com.jfinal.kit.Kv;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.model.GoodsGroup;

/**   
 * @ClassName:  GoodsGroupService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月23日 下午1:52:51   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class GoodsGroupService extends BaseService<GoodsGroup> {
	private GoodsGroup dao = new GoodsGroup().dao();
	@Override
	protected GoodsGroup dao() {
		return dao;
	}
	
	public List<GoodsGroup> getList(Boolean enable) {
		if(isOk(enable)){
			return getCommonList(Kv.by("enable", enable));
		}
		return findAll();
	}


}
