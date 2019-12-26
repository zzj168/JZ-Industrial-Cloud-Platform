package com.jfinal.wxaapp;
 
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jfinal.kit.StrKit;
import com.jfinal.wxaapp.msg.IMsgParser;
import com.jfinal.wxaapp.msg.JsonMsgParser;
import com.jfinal.wxaapp.msg.XmlMsgParser;
 
/**
 * 小程序多账号配置KIT
 * @ClassName:  WxaConfigKit   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月16日 下午6:15:02   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class WxaConfigKit {
    private static final ThreadLocal<String> TL = new ThreadLocal<>();
    private static final Map<String, WxaConfig> CFG_MAP = new ConcurrentHashMap<>();
    private static final String DEFAULT_CFG_KEY = "_default_cfg_key_";
 
    /**
     * 小程序消息解析 XML
     */
    private static IMsgParser xmlMsgParser = new XmlMsgParser();
    /**
     * 小程序消息解析 JSON
     */
    private static IMsgParser jsonMsgParser = new JsonMsgParser();
 
    /**
     * 获取小程序消息解析器
     *
     * @return {IMsgParser}
     */
    public static IMsgParser getMsgParser() {
    	WxaConfig wxaConfig=getWxaConfig();
    	if(wxaConfig!=null&&wxaConfig.getFormat().equals(WxaConfig.FORMAT_JSON)){
    		return jsonMsgParser;
    	}
    	return xmlMsgParser;
    }
 
 
    /**
     * 设置小程序消息解析器
     */
    public static void useJsonMsgParser() {
       
    }
 
    // 开发模式将输出消息交互 xml、json 到控制台
    private static boolean devMode = false;
 
    public static void setDevMode(boolean devMode) {
        WxaConfigKit.devMode = devMode;
    }
 
    public static boolean isDevMode() {
        return devMode;
    }
 
    /**
     * 添加小程序号配置，每个appId只需添加一次，相同appId将被覆盖。
     * 第一个添加的将作为默认公众号配置
     *
     * @param wxaConfig 公众号配置
     * @return WxaConfig 公众号配置
     */
    public static WxaConfig putWxaConfig(WxaConfig wxaConfig) {
        if (CFG_MAP.size() == 0) {
            CFG_MAP.put(DEFAULT_CFG_KEY, wxaConfig);
        }
        return CFG_MAP.put(wxaConfig.getAppId(), wxaConfig);
    }
 
    public static String getAppId() {
        String appId = TL.get();
        if (StrKit.isBlank(appId)) {
            appId = CFG_MAP.get(DEFAULT_CFG_KEY).getAppId();
        }
        return appId;
    }
 
    public static WxaConfig getWxaConfig() {
        String appId = getAppId();
        return getWxaConfig(appId);
    }
 
    public static WxaConfig getWxaConfig(String appId) {
        WxaConfig cfg = CFG_MAP.get(appId);
        if (cfg == null)
            throw new IllegalStateException("需事先调用 WxaConfigKit.setWxaConfig(apiConfig) 将 appId对应的 WxaConfig 对象存入，" +
                    "如JFinalConfig.afterJFinalStart()中调用, 才可以使用 WxaConfigKit.getWxaConfig() 系列方法");
        return cfg;
    }
 
 
    public static WxaConfig removeApiConfig(WxaConfig apiConfig) {
        return removeApiConfig(apiConfig.getAppId());
    }
 
    public static WxaConfig removeApiConfig(String appId) {
        return CFG_MAP.remove(appId);
    }
 
    public static void setThreadLocalAppId(String appId) {
        if (StrKit.isBlank(appId)) {
            appId = CFG_MAP.get(DEFAULT_CFG_KEY).getAppId();
        }
        TL.set(appId);
    }
 
    public static void removeThreadLocalAppId() {
        TL.remove();
    }
}