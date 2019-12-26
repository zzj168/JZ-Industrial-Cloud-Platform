package cn.jbolt.common.gen;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;

import cn.hutool.core.util.StrUtil;

/**
 * JBoltMetaBuilder
 */
public class JBoltMetaBuilder extends MetaBuilder{
	private String schemaPattern;
	
	public JBoltMetaBuilder(DataSource dataSource) {
		super(dataSource);
	}
	@Override
	public List<TableMeta> build() {
		ConsoleUtil.printMessageWithDate(" Build TableMeta ...");
		try {
			conn = dataSource.getConnection();
			dbMeta = conn.getMetaData();
			
			List<TableMeta> ret = new ArrayList<TableMeta>();
			buildTableNames(ret);
			for (TableMeta tableMeta : ret) {
				buildPrimaryKey(tableMeta);
				buildColumnMetas(tableMeta);
			}
			removeNoPrimaryKeyTable(ret);
			return ret;
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (conn != null) {
				try {conn.close();} catch (SQLException e) {throw new RuntimeException(e);}
			}
		}
	}
	/**
	 * 构造 modelName，mysql 的 tableName 建议使用小写字母，多单词表名使用下划线分隔，不建议使用驼峰命名
	 * oracle 之下的 tableName 建议使用下划线分隔多单词名，无论 mysql还是 oralce，tableName 都不建议使用驼峰命名
	 */
	@Override
	protected String buildModelName(String tableName) {
		// 移除表名前缀仅用于生成 modelName、baseModelName，而 tableMeta.name 表名自身不能受影响
		tableName = tableName.toLowerCase();
		if (removedTableNamePrefixes != null) {
			for (String prefix : removedTableNamePrefixes) {
				if (tableName.startsWith(prefix.toLowerCase())) {
					tableName = tableName.replaceFirst(prefix.toLowerCase(), "");
					break;
				}
			}
		}
		
		return StrKit.firstCharToUpperCase(StrKit.toCamelCase(tableName));
	}
	@Override
	protected String handleJavaType(String typeStr, ResultSetMetaData rsmd, int column) throws SQLException {
		if("java.lang.String".equals(typeStr)) {
			int scale = rsmd.getScale(column);			// 小数点右边的位数，值为 0 表示整数
			int precision = rsmd.getPrecision(column);	// 最大精度
			if (scale == 0 && precision == 1&&JBoltProjectGenConfig.charToBoolean) {
				typeStr = "java.lang.Boolean";
			}
		}
		// 当前实现只处理 Oracle
		if ( ! dialect.isOracle() ) {
			return typeStr;
		}
		
		// 默认实现只处理 BigDecimal 类型
		if ("java.math.BigDecimal".equals(typeStr)) {
			int scale = rsmd.getScale(column);			// 小数点右边的位数，值为 0 表示整数
			int precision = rsmd.getPrecision(column);	// 最大精度
			if (scale == 0) {
				if (precision <= 9) {
					typeStr = "java.lang.Integer";
				} else if (precision <= 18) {
					typeStr = "java.lang.Long";
				} else {
					typeStr = "java.math.BigDecimal";
				}
			} else {
				// 非整数都采用 BigDecimal 类型，需要转成 double 的可以覆盖并改写下面的代码
				typeStr = "java.math.BigDecimal";
			}
		}
		
		return typeStr;
	}
	// 移除没有主键的 table
    @Override
	protected void removeNoPrimaryKeyTable(List<TableMeta> ret) {
		for (java.util.Iterator<TableMeta> it = ret.iterator(); it.hasNext();) {
			TableMeta tm = it.next();
			if (StrUtil.isBlank(tm.primaryKey)) {
				it.remove();
				ConsoleUtil.printErrorMessageWithDate(" Skip table " + tm.name + " because there is no primary key");
			}
		}
	}
    @Override
    protected void buildPrimaryKey(TableMeta tableMeta) throws SQLException {
    		ResultSet rs = dbMeta.getPrimaryKeys(conn.getCatalog(), null, tableMeta.name);
    		
    		String primaryKey = "";
    		int index = 0;
    		while (rs.next()) {
    			String cn = rs.getString("COLUMN_NAME");
    			
    			// 避免 oracle 驱动的 bug 生成重复主键，如：ID,ID
    			if (primaryKey.equals(cn)) {
    				continue ;
    			}
    			
    			if (index++ > 0) {
    				primaryKey += ",";
    			}
    			primaryKey += cn;
    		}
    		
    		// 无主键的 table 将在后续的 removeNoPrimaryKeyTable() 中被移除，不再抛出异常
    		// if (StrKit.isBlank(primaryKey)) {
    			// throw new RuntimeException("primaryKey of table \"" + tableMeta.name + "\" required by active record pattern");
    		// }
    		
    		tableMeta.primaryKey = primaryKey;
    		rs.close();
    }
	@Override
	protected ResultSet getTablesResultSet() throws SQLException {
		if(dialect instanceof PostgreSqlDialect&&schemaPattern!=null) {
			return dbMeta.getTables(conn.getCatalog(), schemaPattern, null, new String[]{"TABLE"});	// 不支持 view 生成
		}
		return super.getTablesResultSet();
	}
	@Override
	protected void buildTableNames(List<TableMeta> ret) throws SQLException {
		ResultSet rs = getTablesResultSet();
		while (rs.next()) {
			String tableName = rs.getString("TABLE_NAME");
			
			if (excludedTables.contains(tableName)) {
				ConsoleUtil.printMessageWithDate(" Skip table :" + tableName);
				continue ;
			}
			if (isSkipTable(tableName)) {
				ConsoleUtil.printMessageWithDate(" Skip table :" + tableName);
				continue ;
			}
			// jfinal 4.3 新增过滤 table 机制
			if (filterPredicate != null && filterPredicate.test(tableName)) {
				ConsoleUtil.printMessageWithDate(" Skip table :" + tableName);
				continue ;
			}
			
			TableMeta tableMeta = new TableMeta();
			tableMeta.name = tableName;
			tableMeta.remarks = rs.getString("REMARKS");
			
			tableMeta.modelName = buildModelName(tableName);
			tableMeta.baseModelName = buildBaseModelName(tableMeta.modelName);
			ret.add(tableMeta);
		}
		rs.close();
	}
	public String getSchemaPattern() {
		return schemaPattern;
	}
	public void setSchemaPattern(String schemaPattern) {
		this.schemaPattern = schemaPattern;
	}

}







