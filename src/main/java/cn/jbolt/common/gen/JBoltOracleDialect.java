package cn.jbolt.common.gen;

import com.jfinal.plugin.activerecord.dialect.OracleDialect;

public class JBoltOracleDialect extends OracleDialect {

	@Override
	public String forTableBuilderDoBuild(String tableName) {
		return "select * from \"" + tableName + "\" where rownum < 1";
	}

}
