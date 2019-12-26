package cn.jbolt.common.db.sql;

import com.alibaba.druid.util.JdbcConstants;
import com.jfinal.kit.StrKit;

import cn.jbolt.common.config.MainConfig;

public class SqlUtil {
	private static final String TRUE="'1'";
	private static final String FALSE="'0'";
	/**
	 * 根据数据类型设置特殊值转换 
	 * @param value
	 * @return
	 */
	public static String processBooleanValueToChar(boolean value) {
		return value?TRUE:FALSE;
	}
	public static Object processBoolean(Object value) {
		if(value instanceof Boolean) {
			return boolToInt(value.toString().trim());
		}
		return value;
	}
	
	public static Integer boolToInt(boolean value) {
		return value?Sql.TRUE:Sql.FALSE;
	}
	public static Integer boolToInt(String value) {
		return Boolean.parseBoolean(value.toString())?Sql.TRUE:Sql.FALSE;
	}
	
	public static String sqlValue(Object value) {
		if(value==null){
			return "NULL";
		}
		String sqlValue=value.toString().trim();
		if(StrKit.isBlank(sqlValue)) {
			return "NULL";
		}
			if(value instanceof String) {
				if(sqlValue.indexOf("like")!=-1) {
//					if(sqlValue.replace(" ", "").replace("like'", "").indexOf("''"))
					return sqlValue;
				}
					
				if(sqlValue.indexOf("'")!=-1) {
					sqlValue=sqlValue.replace("'", "''");
				}
				
				//如果是特殊is not判断 直接输出
				if(sqlValue.indexOf("is ")!=-1||sqlValue.indexOf(" not ")!=-1||sqlValue.indexOf(" in(")!=-1||sqlValue.indexOf(" in (")!=-1){
					return  sqlValue;
				}
				
			}else if(value instanceof Boolean) {
				if(sqlValue.equalsIgnoreCase("true")){
					return "'1'";
				}else if(sqlValue.equalsIgnoreCase("false")){
					return "'0'";
				}
			}else if(value instanceof Number) {
				return sqlValue;
			}
			return 	"'"+sqlValue+"'";
	}

	public static String likeValue(Object value) {
		if(value==null){
			return "";
		}
		String sqlValue=value.toString().trim();
		if(StrKit.isBlank(sqlValue)) {
			return "";
		}
		if(sqlValue.indexOf("'")!=-1) {
			sqlValue=sqlValue.replace("'", "''");
		}
		return sqlValue;
	}
	
	
	public static Object[] findInSet(String dbType,Object values,Object value,boolean valuesIsTableColumn){
		String key="";
		Object v="";
		switch (dbType) {
		case JdbcConstants.MYSQL:
			key=" find_in_set('"+value+"',"+values+") >";
			v=0;
			break;
		case JdbcConstants.ORACLE:
			key= " (',' || "+(valuesIsTableColumn?values:"'"+values+"'")+" ||',')";
			v="like '%,"+value+",%'";
			break;
		case JdbcConstants.POSTGRESQL:
			key= " (',' || "+(valuesIsTableColumn?values:"'"+values+"'")+" ||',')";
			v="like '%,"+value+",%'";
			break;
		case JdbcConstants.SQL_SERVER:
			key= " (',' + "+values+" + ',')";
			v="like '%,"+value+",%'";
			break;
		}
		return new Object[] {key,v};
	}
	public static Object TRUE() {
		if(MainConfig.DB_TYPE.equals(JdbcConstants.POSTGRESQL)) {
			return Sql.TRUE.toString();
		}
		return Sql.TRUE;
	}
	
	public static Object FALSE() {
		if(MainConfig.DB_TYPE.equals(JdbcConstants.POSTGRESQL)) {
			return Sql.FALSE.toString();
		}
		return Sql.FALSE;
	}
	
	public static Object toSqlBool(Boolean value) {
		if(MainConfig.DB_TYPE.equals(JdbcConstants.POSTGRESQL)) {
			return processBooleanValueToChar(value);
		}
		return value?Sql.TRUE:Sql.FALSE;
	}
}
