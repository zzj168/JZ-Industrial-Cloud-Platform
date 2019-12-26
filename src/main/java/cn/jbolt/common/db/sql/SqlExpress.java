package cn.jbolt.common.db.sql;

import java.io.Serializable;
/**
 * Sql处理表达式
 * @ClassName:  SqlExpress   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年9月5日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@SuppressWarnings("serial")
public class SqlExpress implements Serializable{
	private Serializable value;
	public SqlExpress(Serializable value){
		this.value=value;
	}
	public Serializable getValue() {
		return value;
	}
	public void setValue(Serializable value) {
		this.value = value;
	}
	public String toString(){
		return value.toString();
	}
}
