package cn.jbolt.base;

import com.jfinal.log.ILogFactory;
import com.jfinal.log.Log;

public class JBoltSlf4jLogFactory implements ILogFactory {
	
	public Log getLog(Class<?> clazz) {
		return new JBoltSlf4jLog(clazz);
	}
	
	public Log getLog(String name) {
		return new JBoltSlf4jLog(name);
	}
}