package cn.jbolt.admin.mall.goods;

import java.util.Date;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;

import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.model.GoodsHtmlContent;

/**
 * 商品的富文本内容描述管理Service
 * @ClassName:  GoodsHtmlContentService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月23日 下午12:47:59   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class GoodsHtmlContentService extends BaseService<GoodsHtmlContent> {
	private GoodsHtmlContent dao = new GoodsHtmlContent().dao();
	@Override
	protected GoodsHtmlContent dao() {
		return dao;
	}
	/**
	 * 获取一个商品的htmlContent图文详情
	 * @param goodsId
	 * @return
	 */
	public GoodsHtmlContent getGoodsContent(Integer goodsId) {
		return findFirst(Kv.by("goods_id", goodsId));
	}
	/**
	 * 如果数据库里没有 就自动创建出来
	 * @param goodsId
	 * @return
	 */
	public GoodsHtmlContent getWithAutoCreate(Integer goodsId) {
		GoodsHtmlContent htmlContent=getGoodsContent(goodsId);
		if(htmlContent==null){
			htmlContent=new GoodsHtmlContent();
			htmlContent.setGoodsId(goodsId);
			htmlContent.save();
		}
		return htmlContent;
	}
	
	/**
	 * 保存商品htmlcontent
	 * @param userId
	 * @param goodsHtmlContent
	 * @return
	 */
	public Ret save(Integer userId, GoodsHtmlContent goodsHtmlContent) {
		if(goodsHtmlContent==null||isOk(goodsHtmlContent.getId())||notOk(goodsHtmlContent.getGoodsId())){
			return fail(Msg.PARAM_ERROR);
		}
		if(notOk(goodsHtmlContent.getContent())){
			return fail("请填写图文详情");
		}
		goodsHtmlContent.setUpdateTime(new Date());
		goodsHtmlContent.setUpdateUserId(userId);
		boolean success=goodsHtmlContent.save();
		return success?success("操作成功"):fail("操作失败");
	}
	/**
	 * 更新商品htmlcontent
	 * @param userId
	 * @param goodsHtmlContent
	 * @return
	 */
	public Ret update(Integer userId, GoodsHtmlContent goodsHtmlContent) {
		if(goodsHtmlContent==null||notOk(goodsHtmlContent.getId())||notOk(goodsHtmlContent.getGoodsId())){
			return fail(Msg.PARAM_ERROR);
		}
		if(notOk(goodsHtmlContent.getContent())){
			return fail("请填写图文详情");
		}
		goodsHtmlContent.setUpdateTime(new Date());
		goodsHtmlContent.setUpdateUserId(userId);
		boolean success=goodsHtmlContent.update();
		return success?success("操作成功"):fail("操作失败");
	}
	

}
