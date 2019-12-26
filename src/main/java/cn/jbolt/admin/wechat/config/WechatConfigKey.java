package cn.jbolt.admin.wechat.config;

import cn.jbolt.common.model.WechatConfig;

/**  
 * 微信公众平台配置 
 * @ClassName:  WechatConfigKey   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月13日 下午12:18:59   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */

public class WechatConfigKey {
	/**
	 * 公众号开发信息-AppId
	 */
	public static final String APP_ID="app_id";
	/**
	 * 公众号开发信息-AppSecret
	 */
	public static final String APP_SECRET="app_secret";
	/**
	 * 公众号开发信息-原始ID
	 */
	public static final String APP_GHID="app_ghid";
	/**
	 * 服务器配置-URL
	 */
	public static final String SERVER_DOMAIN_URL="server_domain_url";
	/**
	 * 服务器配置-令牌 Token
	 */
	public static final String SERVER_TOKEN="server_token";
	/**
	 * 服务器配置-消息加解密密钥 EncodingAESKey
	 */
	public static final String SERVER_ENCODINGAESKEY="server_encodingaeskey";
	/**
	 * 服务器配置-消息加解密方式 msg_encrypt_type
	 */
	public static final String SERVER_MSG_ENCRYPT_TYPE="server_msg_encrypt_type";
	/**
	 * 服务器配置-消息数据格式 小程序客服消息
	 */
	public static final String SERVER_DATA_FORMAT="server_data_format";
	/**
	 * 音乐消息默认MediaId
	 */
	public static final String MUSIC_POST_MEDIAID="music_post_mediaid";
	/**
	 * 基础配置keys_公众号
	 */
	public static final String[] baseConfigKeys=new String[]{
			APP_ID,	APP_SECRET,APP_GHID,SERVER_DOMAIN_URL,SERVER_TOKEN,SERVER_ENCODINGAESKEY,SERVER_MSG_ENCRYPT_TYPE
	};
	/**
	 * 额外配置keys_公众号
	 */
	public static final String[] extraConfigKeys=new String[]{
			MUSIC_POST_MEDIAID
	};
	/**
	 * 基础配置keys_小程序
	 */
	public static final String[] baseWxaConfigKeys=new String[]{
			APP_ID,	APP_SECRET,APP_GHID,SERVER_DOMAIN_URL,SERVER_TOKEN,SERVER_ENCODINGAESKEY,SERVER_MSG_ENCRYPT_TYPE,SERVER_DATA_FORMAT
	};
	/**
	 * 基础配置Names
	 */
	public static final String[] baseConfigNames=new String[]{
			"开发者ID(AppID)","开发者密码(AppSecret)","原始ID","服务器地址(URL)","令牌(Token)","消息加解密密钥(EncodingAESKey)","消息加解密方式"
	};
	/**
	 * 额外配置Names
	 */
	public static final String[] extraConfigNames=new String[]{
			"音乐消息封面图MediaId"
	};
	/**
	 * 基础配置Names_小程序
	 */
	public static final String[] baseWxaConfigNames=new String[]{
			"开发者ID(AppID)","开发者密码(AppSecret)","原始ID","服务器地址(URL)","令牌(Token)","消息加解密密钥(EncodingAESKey)","消息加解密方式","数据格式"
	};
	
	
	
	/**
	 * 微信支付配置-商户Id-mchId
	 */
	public static final String MCH_ID="mch_id";
	/**
	 * 微信支付配置-支付密钥
	 */
	public static final String PATERNERKEY="paternerKey";
	/**
	 * 微信支付配置-支付密钥 APIV3
	 */
	public static final String PATERNERKEY_V3="paternerKey_v3";
	/**
	 * 微信支付配置-限制使用信用卡 limit_pay
	 */
	public static final String LIMIT_PAY="limit_pay";
	/**
	 * 微信支付配置-开放电子发票 电子发票入口开放标识 receipt
	 */
	public static final String RECEIPT="receipt";
	
	/**
	 * 支付配置keys
	 */
	public static final String[] payConfigKeys=new String[]{
			MCH_ID,	PATERNERKEY,PATERNERKEY_V3,LIMIT_PAY,RECEIPT
	};
	
	/**
	 * 支付配置Names
	 */
	public static final String[] payConfigNames=new String[]{
			"商户号(mch_id)","API密钥","APIv3密钥","指定支付方式(limit_pay)","电子发票入口(receipt)"
	};
	/**
	 * 根据配置类型获得配置keys
	 * @param type
	 * @return
	 */
	public static String[] getConfigKeys(int configType,boolean isWxa) {
		switch (configType) {
			case WechatConfig.TYPE_BASE:
				return isWxa?baseWxaConfigKeys:baseConfigKeys;
			case WechatConfig.TYPE_PAY:
				return payConfigKeys;
			case WechatConfig.TYPE_EXTRA:
				return extraConfigKeys;
		}
		return null;
	}
	/**
	 * 根据配置类型获取配置项目名字names
	 * @param type
	 * @return
	 */
	public static String[] getConfigNames(int type,boolean isWxa) {
		switch (type) {
			case WechatConfig.TYPE_BASE:
				return isWxa?baseWxaConfigNames:baseConfigNames;
			case WechatConfig.TYPE_PAY:
				return payConfigNames;
			case WechatConfig.TYPE_EXTRA:
				return extraConfigNames;
		}
		return null;
	}
	
}
