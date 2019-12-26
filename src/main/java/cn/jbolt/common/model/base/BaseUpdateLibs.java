package cn.jbolt.common.model.base;
import cn.jbolt.base.JBoltBaseModel;

/**
 * Generated by JBolt, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseUpdateLibs<M extends BaseUpdateLibs<M>> extends JBoltBaseModel<M>{

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public M setUrl(java.lang.String url) {
		set("url", url);
		return (M)this;
	}
	
	public java.lang.String getUrl() {
		return getStr("url");
	}

	public M setTarget(java.lang.String target) {
		set("target", target);
		return (M)this;
	}
	
	public java.lang.String getTarget() {
		return getStr("target");
	}

	/**
	 * 清空文件夹
	 */
	public M setDeleteAll(java.lang.Boolean deleteAll) {
		set("delete_all", deleteAll);
		return (M)this;
	}
	
	/**
	 * 清空文件夹
	 */
	public java.lang.Boolean getDeleteAll() {
		return getBoolean("delete_all");
	}

	/**
	 * 强制
	 */
	public M setIsMust(java.lang.Boolean isMust) {
		set("is_must", isMust);
		return (M)this;
	}
	
	/**
	 * 强制
	 */
	public java.lang.Boolean getIsMust() {
		return getBoolean("is_must");
	}

}