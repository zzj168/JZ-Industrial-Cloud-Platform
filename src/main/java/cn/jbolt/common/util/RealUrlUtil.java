package cn.jbolt.common.util;

import com.jfinal.kit.StrKit;

import cn.jbolt.common.config.GlobalConfigKey;
import cn.jbolt.common.config.MainConfig;

/** 
 * 获取真实访问地址  
 * @ClassName:  RealUrlUtil   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月14日 下午9:59:54   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class RealUrlUtil {
	public static String getImage(Object url){
		return getImage(url, null);
	}
	public static String getImage(Object url,Object defaultValue){
		if(defaultValue==null||StrKit.isBlank(defaultValue.toString())){
			defaultValue="/assets/img/uploadimg.png";
		}
		return get(url, defaultValue);
	}
	
	public static String getWechatImage(String url){
		return getWechatImage(url, null);
	}
	public static String getWechatImage(String url,String defaultValue){
		if(defaultValue==null||StrKit.isBlank(defaultValue)){
			defaultValue="/assets/img/uploadimg.png";
		}
		String serverUrl=get(url, defaultValue);
		if(StrKit.isBlank(serverUrl)) {
			return null;
		}
		String domain=CACHE.me.getGlobalConfigValue(GlobalConfigKey.WECHAT_ASSETS_SERVER_DOMAIN);
		if(StrKit.isBlank(domain)) {
			System.out.println("全局配置表里 没有配置微信的资源根URL");
			return null;
		}
			
		return domain+((serverUrl.charAt(0)=='/')?serverUrl:("/"+serverUrl));
	}
	public static String get(Object url){
		return get(url, null);
	}
	public static String get(Object url,Object defaultValue){
		if((url==null||StrKit.isBlank(url.toString()))){
			if(defaultValue!=null&&StrKit.notBlank(defaultValue.toString())){
				return  defaultValue.toString().trim();
			}else{
				return null;
			}
		}
		
		String urlValue=url.toString().trim();
		if(StrKit.notBlank(MainConfig.BASE_UPLOAD_PATH_PRE)){
			if(urlValue.indexOf(MainConfig.BASE_UPLOAD_PATH_PRE)!=-1){
				urlValue=urlValue.replace(MainConfig.BASE_UPLOAD_PATH_PRE, "");
			}
		}
		char firstLe=Character.toLowerCase(urlValue.charAt(0));
		if(firstLe!='/'&&firstLe!='h') {
			urlValue="/"+urlValue;
		}
		
		return urlValue;
	}
	
	
 
}
