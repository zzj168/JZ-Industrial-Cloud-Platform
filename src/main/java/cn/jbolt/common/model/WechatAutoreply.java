package cn.jbolt.common.model;

import cn.jbolt.common.model.base.BaseWechatAutoreply;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class WechatAutoreply extends BaseWechatAutoreply<WechatAutoreply> {
	/**
	 * 回复类型-关注后回复
	 */
	public static final int TYPE_SUBSCRIBE=1;
	/**
	 * 回复类型-关键词回复
	 */
	public static final int TYPE_KEYWORDS=2;
	/**
	 * 回复类型-默认回复
	 */
	public static final int TYPE_DEFAULT=3;
	/**
	 * 回复方式-随机一条
	 */
	public static final int REPLYTYPE_RANDOMONE=1;
	/**
	 * 回复方式-全部
	 */
	public static final int REPLYTYPE_ALL=2;
}