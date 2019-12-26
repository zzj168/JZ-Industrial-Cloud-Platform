package cn.jbolt.common.util.captcha;

import java.awt.Color;
import java.awt.Font;
import java.io.OutputStream;

/**
 *  验证码抽象类,暂时不支持中文
 * @ClassName:  Captcha   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月27日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public abstract class JBoltCaptcha extends Randoms {
	protected Font font = new Font("Verdana", Font.ITALIC | Font.BOLD, 28); // 字体
	protected int len = 5; // 验证码随机字符长度
	protected int width = 150; // 验证码显示跨度
	protected int height = 40; // 验证码显示高度
	private String chars = null; // 随机字符串
	private String key;
	/**
	 * 验证码默认过期时长 180 秒
	 */
	public static final int DEFAULT_EXPIRE_TIME = 180;
	private long expireAt=DEFAULT_EXPIRE_TIME * 1000 + System.currentTimeMillis();

	/**
	 * 生成随机字符数组
	 * 
	 * @return 字符数组
	 */
	protected char[] alphas() {
		char[] cs = new char[len];
		for (int i = 0; i < len; i++) {
			cs[i] = alpha();
		}
		chars = new String(cs);
		return cs;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * 给定范围获得随机颜色
	 * 
	 * @return Color 随机颜色
	 */
	protected Color color(int fc, int bc) {
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + num(bc - fc);
		int g = fc + num(bc - fc);
		int b = fc + num(bc - fc);
		return new Color(r, g, b);
	}
	
	public void gen(String key) {
		this.key=key;
		alphas();
	}

	/**
	 * 验证码输出,抽象方法，由子类实现
	 * 
	 * @param os
	 *            输出流
	 */
	protected abstract void out(OutputStream os);

	/**
	 * 获取随机字符串
	 * 
	 * @return string
	 */
	public String text() {
		return chars;
	}
	
	@Override
	public String getKey() {
		return this.key;
	}
	@Override
	public String getValue() {
		return chars;
	}

	@Override
	public long getExpireAt() {
		return expireAt;
	}
	@Override
	public boolean isExpired() {
		return expireAt < System.currentTimeMillis();
	}
	@Override
	public boolean notExpired() {
		return !isExpired();
	}
	
 
	 
	@Override
	public String toString() {
		return this.key + " : " + this.chars; 
	}
}