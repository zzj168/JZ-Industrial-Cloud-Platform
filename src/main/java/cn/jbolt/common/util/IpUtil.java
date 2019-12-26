package cn.jbolt.common.util;
 
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.http.HttpUtil;
 
/**
 * IP工具类
 */
public class IpUtil {
 
    /**
     * 获取登录用户的IP地址
     * 
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "localhost";
        }
        if (ip.split(",").length > 1) {
            ip = ip.split(",")[0];
        }
        return ip;
    }
 
    /**
     * 通过IP获取地址(需要联网，调用淘宝的IP库)
     * 
     * @param ip
     * @return
     */
    public static String getIpInfo(String ip) {
        if (ip.equals("localhost")) {
            ip = "127.0.0.1";
        }
        String url="http://ip.taobao.com/service/getIpInfo.php?ip=" + ip;
        String result=HttpUtil.get(url);
         JSONObject obj = JSON.parseObject(result);
         String info = "";
         if (obj.getIntValue("code") == 0) {
             JSONObject data = obj.getJSONObject("data");
             info += data.getString("country") + " ";
             info += data.getString("region") + " ";
             info += data.getString("city");
             //+ " ";
             //info += data.getString("isp");
         }
        return info;
    }
 public static void main(String[] args) {
}
}