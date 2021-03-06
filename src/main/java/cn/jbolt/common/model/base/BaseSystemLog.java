package cn.jbolt.common.model.base;
import cn.jbolt.base.JBoltBaseModel;

/**
 * Generated by JBolt, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseSystemLog<M extends BaseSystemLog<M>> extends JBoltBaseModel<M>{

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
	 * 标题
	 */
	public M setTitle(java.lang.String title) {
		set("title", title);
		return (M)this;
	}
	
	/**
	 * 标题
	 */
	public java.lang.String getTitle() {
		return getStr("title");
	}

	/**
	 * 操作类型 删除 更新 新增
	 */
	public M setType(java.lang.Integer type) {
		set("type", type);
		return (M)this;
	}
	
	/**
	 * 操作类型 删除 更新 新增
	 */
	public java.lang.Integer getType() {
		return getInt("type");
	}

	/**
	 * 连接对象详情地址
	 */
	public M setUrl(java.lang.String url) {
		set("url", url);
		return (M)this;
	}
	
	/**
	 * 连接对象详情地址
	 */
	public java.lang.String getUrl() {
		return getStr("url");
	}

	/**
	 * 操作人ID
	 */
	public M setUserId(java.lang.Integer userId) {
		set("user_id", userId);
		return (M)this;
	}
	
	/**
	 * 操作人ID
	 */
	public java.lang.Integer getUserId() {
		return getInt("user_id");
	}

	/**
	 * 操作人姓名
	 */
	public M setUserName(java.lang.String userName) {
		set("user_name", userName);
		return (M)this;
	}
	
	/**
	 * 操作人姓名
	 */
	public java.lang.String getUserName() {
		return getStr("user_name");
	}

	/**
	 * 操作对象类型
	 */
	public M setTargetType(java.lang.Integer targetType) {
		set("target_type", targetType);
		return (M)this;
	}
	
	/**
	 * 操作对象类型
	 */
	public java.lang.Integer getTargetType() {
		return getInt("target_type");
	}

	/**
	 * 记录创建时间
	 */
	public M setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
		return (M)this;
	}
	
	/**
	 * 记录创建时间
	 */
	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	/**
	 * 操作对象ID
	 */
	public M setTargetId(java.lang.Integer targetId) {
		set("target_id", targetId);
		return (M)this;
	}
	
	/**
	 * 操作对象ID
	 */
	public java.lang.Integer getTargetId() {
		return getInt("target_id");
	}

	/**
	 * 打开类型URL还是Dialog
	 */
	public M setOpenType(java.lang.Integer openType) {
		set("open_type", openType);
		return (M)this;
	}
	
	/**
	 * 打开类型URL还是Dialog
	 */
	public java.lang.Integer getOpenType() {
		return getInt("open_type");
	}

}
