package cn.jbolt.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.log.Log;
/**
 * JBolt slf4j封装
 * @ClassName:  JBoltSlf4jLog   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年12月3日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JBoltSlf4jLog extends Log {
	
	private Logger logger;
	
	JBoltSlf4jLog(Class<?> clazz) {
		logger = LoggerFactory.getLogger(clazz);
	}
	
	JBoltSlf4jLog(String name) {
		logger =  LoggerFactory.getLogger(name);
	}
	
	public static JBoltSlf4jLog getLog(Class<?> clazz) {
		return new JBoltSlf4jLog(clazz);
	}
	
	public static JBoltSlf4jLog getLog(String name) {
		return new JBoltSlf4jLog(name);
	}
	
	public void info(String message) {
		logger.info(message);
	}
	
	public void info(String message, Throwable t) {
		logger.info(message,t);
	}
	
	public void debug(String message) {
		logger.debug(message);
	}
	
	public void debug(String message, Throwable t) {
		logger.debug(message,t);
	}
	
	public void warn(String message) {
		logger.warn(message);
	}
	
	public void warn(String message, Throwable t) {
		logger.warn(message,t);
	}
	
	public void error(String message) {
		logger.error(message);
	}
	
	public void error(String message, Throwable t) {
		logger.error(message,t);
	}
	
	 
	
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}
	
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}
	
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}
	
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}
	
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void fatal(String message) {
		logger.error(message);
	}

	@Override
	public void fatal(String message, Throwable t) {
		logger.error(message,t);
	}

	@Override
	public boolean isFatalEnabled() {
		return false;
	}
}

