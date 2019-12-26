package cn.jbolt.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.jbolt.common.util.CACHE;
/**
 * 声明需要自动处理CACHE规则
 * 默认ID-Object规则
 * 可以开启Key-Object规则
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JBoltAutoCache {
	/**
	 * 缓存的CACHE NAME
	 * @return
	 */
	String name() default CACHE.JBOLT_CACHE_NAME;
	/**
	 * 缓存KEY的前缀
	 * @return
	 */
	String prefix() default CACHE.JBOLT_CACHE_DEFAULT_PREFIX;
	/**
	 * ID-Object缓存规则 默认开启
	 * @return
	 */
	boolean idCache() default true;
	/**
	 * KEY-Object缓存规则 默认不启用
	 * @return
	 */
	boolean keyCache() default false;
	/**
	 * KEY-Object规则下使用哪个字段作为KEY
	 * @return
	 */
	String column() default "";
	/**
	 * 绑定其它字段 只有key-object开启后才有用，特殊情况比如UserConfig除了configKey还有userId
	 * @return
	 */
	String bindColumn() default "";
}