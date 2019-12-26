package cn.jbolt.base.api;

public class JBoltApiKit {
	/**
	  * 针对JwtParseResut的ThreadLocal
	 */
    private static final ThreadLocal<JwtParseRet> jwtTL = new ThreadLocal<JwtParseRet>();
   
    public static void setJwtParseRet(JwtParseRet jwtParseRet) {
        jwtTL.set(jwtParseRet);
    }

    public static void removeJwtParseRet() {
        jwtTL.remove();
    }

    public static JwtParseRet getJwtParseRet() {
        return jwtTL.get();
    }
    
    
    /**
          * 针对AppId的ThreadLocal
     */
    private static final ThreadLocal<String> appIdTL = new ThreadLocal<String>();
    
    public static void setAppId(String appId) {
    	appIdTL.set(appId);
    }

    public static void removeAppId() {
    	appIdTL.remove();
    }

    public static String getAppId() {
        return appIdTL.get();
    }

	public static void clear() {
		removeJwtParseRet();
		removeAppId();
	}

}
