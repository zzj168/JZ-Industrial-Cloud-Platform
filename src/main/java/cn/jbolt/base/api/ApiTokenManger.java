package cn.jbolt.base.api;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import cn.jbolt.common.model.Application;
import cn.jbolt.common.util.CACHE;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

/**
 * JBolt平台关于APItoken的实用管理工具类
 * 
 * @ClassName: ApiTokenManger
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2019年9月12日
 * 
 *        注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class ApiTokenManger {
	private static final Log log = Log.getLog(ApiTokenManger.class);
	/**
	 * 有效期 默认两个小时 2 * 60 * 60 *1000 2小时
	 */
	private static final long JWT_TTL = 7200000L;
	/**
	 * 定义调用接口方传递TOKEN数据的KEY键
	 */
	public final static String JBOLT_API_TOKEN_KEY = "jbolt_jwt";
	public final static String JBOLT_JWT_SIGNATURE = "jbolt_signature";
	public final static String JBOLT_JWT_TIMESTAMP = "jbolt_timestamp";
	public final static String JBOLT_JWT_NONCE = "jbolt_nonce";
	private static ApiTokenManger me = new ApiTokenManger();

	public static ApiTokenManger me() {
		return me;
	}

	/**
	 * 创建JBolt Api Token (JWT)
	 * 
	 * @param jwtParseRet
	 * @return
	 */
	public String createJBoltApiToken(JwtParseRet jwtParseRet) {
		// 默认两个小时过期
		return createJBoltApiToken(jwtParseRet.getApplication(), jwtParseRet.getApiUser());
	}

	/**
	 * 创建JBolt Api Token (JWT)
	 * 
	 * @param application 所属应用APP
	 * @param apiUser     签发用户
	 * @return
	 * @throws Exception
	 */
	public String createJBoltApiToken(Application application, ApiUser apiUser) {
		// 默认两个小时过期
		return createJBoltApiToken(application, apiUser, null);
	}

	/**
	 * 创建JBolt Api Token (JWT)
	 * 
	 * @param application 所属应用APP
	 * @param apiUser     签发用户
	 * @param ttlMillis   过期时长
	 * @return
	 * @throws Exception
	 */
	public String createJBoltApiToken(Application application, ApiUser apiUser, Long ttlMillis) {
		// 生成JWT 签发 时间
		long nowMillis = System.currentTimeMillis();//
		Date now = new Date(nowMillis);
		// 计算过期时间 如果没传或者不合要求 使用默认两小时
		if (ttlMillis == null || ttlMillis <= 0) {
			ttlMillis = JWT_TTL;
		}
		Date ttlDate = new Date(nowMillis + ttlMillis);

		// 得到加密秘钥 配置 一个app
		SecretKey key = generalKey(application.getAppSecret());
		// 构建claims
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("userId", apiUser.getUserId());
		claims.put("userName", apiUser.getUserName());

		// 构建payload 加密 合并压缩 生成 token
		return Jwts.builder().setClaims(claims)
				.setId(application.getId() + "_" + apiUser.getUserId() + "_" + UUID.randomUUID().toString())
				.setIssuedAt(now).signWith(key).setExpiration(ttlDate).compact();
	}

	/**
	 * 将APP的appSecret 生成 SecretKey
	 * 
	 * @param appSecret
	 * @return
	 */
	private SecretKey generalKey(String appSecret) {
		byte[] encodedKey = Base64Kit.decode(appSecret);
		return Keys.hmacShaKeyFor(encodedKey);
	}

	/**
	 * 从请求header里获取JWT生成的JBOLT API TOKEN
	 * 
	 * @return
	 */
	private String getJBoltApiToken(Controller controller) {
		return controller.getHeader(JBOLT_API_TOKEN_KEY);
	}

	/**
	 * 从请求header里获取JWT生成的JBOLT API TOKEN
	 * 
	 * @return
	 */
	public JwtParseRet getJwtParseRet(Invocation invocation) {
		return getJwtParseRet(invocation.getController());
	}

	/**
	 * 得到jbolt_jwt_sign
	 * 
	 * @param controller
	 * @return
	 */
	public String getJBoltSignature(Controller controller) {
		return controller.getHeader(JBOLT_JWT_SIGNATURE);
	}

	/**
	 * 从请求header里获取JWT生成的JBOLT API TOKEN
	 * 
	 * @return
	 */
	public JwtParseRet getJwtParseRet(Controller controller) {
		String token = getJBoltApiToken(controller);
		//校验application
		String appId = JBoltApiKit.getAppId();
		if (StrKit.isBlank(appId)) {
			return new JwtParseRet().setMsg("appId校验参数异常");
		}
		Application application = CACHE.me.getApplicationByAppId(appId);
		if (application == null) {
			return new JwtParseRet().setMsg("请求的Application不存在:[" + appId + "]");
		}
		if (application.getEnable() == false) {
			return new JwtParseRet().setMsg("请求的Application未开放:[" + application.getName() + ":" + appId + "]");
		}
		//判断是否需要校验sign
		if(application.getNeedCheckSign()) {
			boolean checkSignSuccess = checkJBoltJwtSignature(controller, token);
			
			if(checkSignSuccess==false) {
				return new JwtParseRet().setSignCheckFailed(true);
			}
		}
		return parseJWT(application,token);
	}
	/**
	  * 验证Signature
	 * @param signature
	 * @param token
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	private boolean checkSignature(String signature, String token, String timestamp, String nonce) {
		String array[] = { token, timestamp, nonce };
		Arrays.sort(array);
		String tempStr = array[0] + array[1] + array[2];
		tempStr = HashKit.sha1(tempStr);
		return tempStr.equalsIgnoreCase(signature);
	}
	/**
	 * 验证Jbolt_jwt_sign
	 * @param controller
	 * @param token
	 * @return
	 */
	private boolean checkJBoltJwtSignature(Controller controller, String token) {
		String signature = getJBoltSignature(controller);
		String timestamp = controller.getHeader(JBOLT_JWT_TIMESTAMP);
		String nonce = controller.getHeader(JBOLT_JWT_NONCE);
		if (StrKit.isBlank(signature) || StrKit.isBlank(timestamp) || StrKit.isBlank(nonce)) {
			log.error("check signature failure: params are empty");
			return false;
		}

		if (checkSignature(signature, token, timestamp, nonce)) {
			return true;
		}
		log.error("check signature failure: " + " signature = " + controller.getPara("signature") + " timestamp = "
				+ controller.getPara("timestamp") + " nonce = " + controller.getPara("nonce"));

		return false;
	}

	/**
	 * 解析jwt
	 * 
	 * @param application
	 * @param token
	 * @return
	 */
	private JwtParseRet parseJWT(Application application,String token) {
		JwtParseRet jwtParseResut = new JwtParseRet();
		if (StrKit.isBlank(token)) {
			jwtParseResut.setMsg("token不能为空");
			return jwtParseResut;
		}

		String[] jwtArray = token.split("\\.");
		if (jwtArray == null || jwtArray.length != 3) {
			jwtParseResut.setMsg("token格式不正确");
			return jwtParseResut;
		}
		

		jwtParseResut.setApplication(application);

		// 签名秘钥和生成的签名的秘钥一模一样
		SecretKey secretKey = generalKey(application.getAppSecret());
		Claims claims = null;
		try {
			claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
			if (claims == null) {
				jwtParseResut.setMsg("解析Jwt失败 1:[" + application.getName() + ":" + application.getAppId() + "]");
				return jwtParseResut;
			}

		} catch (MalformedJwtException | SignatureException e) {
			// 签名错误或解析错误
			jwtParseResut.setMsg("解析Jwt失败 2:[" + application.getName() + ":" + application.getAppId() + "]");
			return jwtParseResut;
		} catch (ExpiredJwtException e) {
			jwtParseResut.setExpired(true);
			// 过期
			jwtParseResut.setMsg("解析Jwt失败 3:[" + application.getName() + ":" + application.getAppId() + "]TOKEN已过期");
			return jwtParseResut;
		} catch (Throwable ex) {
			// 其它
			String msg = ex.getMessage();
			jwtParseResut.setMsg("解析Jwt失败 4:[" + application.getName() + ":" + application.getAppId() + "]" + (msg != null ? msg : ""));
			return jwtParseResut;
		}

		jwtParseResut.setApiUser(new ApiUserBean(claims));
		return jwtParseResut;
	}

	/**
	 * 签发JWT
	 * 
	 * @param controller
	 */
	public void createJBoltApiTokenToResponse(ApiBaseController controller) {
		Application application = controller.getApplication();
		ApiUserBean apiUser = controller.getApplyTokenApiUser();
		// 创建
		String jwt = createJBoltApiToken(application, apiUser);
		// 设置到response里 jwt
		controller.getResponse().setHeader(ApiTokenManger.JBOLT_API_TOKEN_KEY, jwt);
	}
}
