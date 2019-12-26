package cn.jbolt.base;

import java.io.IOException;
import java.io.Writer;
/**
 * JBolt中的action report 日志输出处理
 * @ClassName:  JBoltActionReportLogWriter   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年12月4日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JBoltActionReportLogWriter extends Writer {
	private static final JBoltSlf4jLog LOG=JBoltSlf4jLog.getLog("JBoltActionReportLog");
	@Override
	public void write(String str) throws IOException {
		LOG.debug(str);
	}
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

}
