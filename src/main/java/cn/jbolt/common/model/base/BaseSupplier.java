package cn.jbolt.common.model.base;
import cn.jbolt.base.JBoltBaseModel;

/**
 * Generated by JBolt, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseSupplier<M extends BaseSupplier<M>> extends JBoltBaseModel<M>{

	/**
	 * 主键
	 */
	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 主键
	 */
	public java.lang.Integer getId() {
		return getInt("id");
	}

	/**
	 * 供货商编号
	 */
	public M setSupplierNo(java.lang.String supplierNo) {
		set("supplierNo", supplierNo);
		return (M)this;
	}
	
	/**
	 * 供货商编号
	 */
	public java.lang.String getSupplierNo() {
		return getStr("supplierNo");
	}

	/**
	 * 供货商名称
	 */
	public M setSupplierName(java.lang.String supplierName) {
		set("supplierName", supplierName);
		return (M)this;
	}
	
	/**
	 * 供货商名称
	 */
	public java.lang.String getSupplierName() {
		return getStr("supplierName");
	}

	/**
	 * 供货商地区
	 */
	public M setSupplierArea(java.lang.Integer supplierArea) {
		set("supplierArea", supplierArea);
		return (M)this;
	}
	
	/**
	 * 供货商地区
	 */
	public java.lang.Integer getSupplierArea() {
		return getInt("supplierArea");
	}

	/**
	 * 供货商级别
	 */
	public M setSupplierLevel(java.lang.Integer supplierLevel) {
		set("supplierLevel", supplierLevel);
		return (M)this;
	}
	
	/**
	 * 供货商级别
	 */
	public java.lang.Integer getSupplierLevel() {
		return getInt("supplierLevel");
	}

	/**
	 * 创建时间
	 */
	public M setCreateTime(java.util.Date createTime) {
		set("createTime", createTime);
		return (M)this;
	}
	
	/**
	 * 创建时间
	 */
	public java.util.Date getCreateTime() {
		return get("createTime");
	}

}
