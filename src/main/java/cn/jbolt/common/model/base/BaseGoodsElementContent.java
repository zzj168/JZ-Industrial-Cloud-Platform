package cn.jbolt.common.model.base;
import cn.jbolt.base.JBoltBaseModel;

/**
 * Generated by JBolt, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseGoodsElementContent<M extends BaseGoodsElementContent<M>> extends JBoltBaseModel<M>{

	/**
	 * 关联商品ID
	 */
	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 关联商品ID
	 */
	public java.lang.Integer getId() {
		return getInt("id");
	}

	/**
	 * 商品ID
	 */
	public M setGoodsId(java.lang.Integer goodsId) {
		set("goods_id", goodsId);
		return (M)this;
	}
	
	/**
	 * 商品ID
	 */
	public java.lang.Integer getGoodsId() {
		return getInt("goods_id");
	}

	public M setType(java.lang.Integer type) {
		set("type", type);
		return (M)this;
	}
	
	public java.lang.Integer getType() {
		return getInt("type");
	}

	/**
	 * 内容
	 */
	public M setContent(java.lang.String content) {
		set("content", content);
		return (M)this;
	}
	
	/**
	 * 内容
	 */
	public java.lang.String getContent() {
		return getStr("content");
	}

	/**
	 * 排序
	 */
	public M setSortRank(java.lang.Integer sortRank) {
		set("sort_rank", sortRank);
		return (M)this;
	}
	
	/**
	 * 排序
	 */
	public java.lang.Integer getSortRank() {
		return getInt("sort_rank");
	}

	/**
	 * SKUID
	 */
	public M setSkuId(java.lang.Integer skuId) {
		set("sku_id", skuId);
		return (M)this;
	}
	
	/**
	 * SKUID
	 */
	public java.lang.Integer getSkuId() {
		return getInt("sku_id");
	}

}