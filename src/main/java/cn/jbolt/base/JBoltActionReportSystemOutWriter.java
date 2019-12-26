package cn.jbolt.base;

import java.io.IOException;
import java.io.Writer;

/**
 * JFinal action Report 使用System Out 输出
 * @ClassName:  JBoltActionReportSystemOutWriter   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年12月8日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JBoltActionReportSystemOutWriter extends Writer {
	public void write(String str) throws IOException {
		System.out.print(str);
	}
	public void write(char[] cbuf, int off, int len) throws IOException {}
	public void flush() throws IOException {}
	public void close() throws IOException {}
}