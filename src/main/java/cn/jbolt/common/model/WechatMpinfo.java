package cn.jbolt.common.model;

import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.common.model.base.BaseWechatMpinfo;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class WechatMpinfo extends BaseWechatMpinfo<WechatMpinfo> {
	/**
	 * 类型-订阅号
	 */
	public static final int TYPE_DYH=1;
	/**
	 * 类型-服务号
	 */
	public static final int TYPE_FWH=2;
	/**
	 * 类型-企业微信
	 */
	public static final int TYPE_QYWX=3;
	/**
	 * 类型-小程序
	 */
	public static final int TYPE_XCX=4;
	
	
	/**
	 * 主体类型-个人
	 */
	public static final int SUBJECT_TYPE_PERSONAL=1;
	/**
	 * 主体类型-个体工商户
	 */
	public static final int SUBJECT_TYPE_INDIVIDUAL_BUSINESS=2;
	/**
	 * 主体类型-企业
	 */
	public static final int SUBJECT_TYPE_COMPANY=3;
	/**
	 * 主体类型-媒体
	 */
	public static final int SUBJECT_TYPE_MEDIA=4;
	/**
	 * 主体类型-组织
	 */
	public static final int SUBJECT_TYPE_ORG=5;
	/**
	 * 主体类型-政府机关
	 */
	public static final int SUBJECT_TYPE_GOV=6;
	/**
	 * 主体类型-事业单位
	 */
	public static final int SUBJECT_TYPE_GOV_SPONSORED_INSTITUTION=7;
	
	public String getTypeName(){
		return WechatMpinfoService.typeName(getType());
	}
	public String getSubjectTypeName(){
		return WechatMpinfoService.subjectTypeName(getSubjectType());
	}
}
