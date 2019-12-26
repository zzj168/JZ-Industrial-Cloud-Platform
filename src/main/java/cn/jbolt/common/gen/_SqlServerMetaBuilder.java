package cn.jbolt.common.gen;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.activerecord.generator.ColumnMeta;
import com.jfinal.plugin.activerecord.generator.TableMeta;

public class _SqlServerMetaBuilder extends JBoltMetaBuilder{
    public _SqlServerMetaBuilder(DataSource dataSource) {
		super(dataSource);
	}
	@Override
    protected void buildColumnMetas(TableMeta tableMeta) throws SQLException {
    	String sql = dialect.forTableBuilderDoBuild(tableMeta.name);
		Statement stm = conn.createStatement();
		ResultSet rs = stm.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		ColumnMeta cm;
		for (int i=1; i<=rsmd.getColumnCount(); i++) {
			cm = new ColumnMeta();
			cm.name = rsmd.getColumnName(i);
			
			String colClassName = rsmd.getColumnClassName(i);
			String typeStr = typeMapping.getType(colClassName);
			if (typeStr != null) {
				cm.javaType = typeStr;
			}
			else {
				int type = rsmd.getColumnType(i);
				if (type == Types.BINARY || type == Types.VARBINARY || type == Types.BLOB) {
					cm.javaType = "byte[]";
				}
				else if (type == Types.SMALLINT) {
					cm.javaType="java.lang.Short";
				}
				else if (type == Types.CLOB || type == Types.NCLOB) {
					cm.javaType = "java.lang.String";
				}
				else {
					cm.javaType = "java.lang.String";
				}
			}
			//特殊处理char(1) to Boolean
			if("java.lang.String".equals(cm.javaType)) {
					int scale = rsmd.getScale(i);			// 小数点右边的位数，值为 0 表示整数
					int precision = rsmd.getPrecision(i);	// 最大精度
					if (scale == 0 && precision == 1&&JBoltProjectGenConfig.charToBoolean) {
						cm.javaType = "java.lang.Boolean";
					}
				}
			
			
			// 构造字段对应的属性名 attrName
			cm.attrName = buildAttrName(cm.name);
			
			tableMeta.columnMetas.add(cm);
		}
		
		rs.close();
		stm.close();
    	
    }
    @Override
    protected ResultSet getTablesResultSet() throws SQLException {
        setDialect(new SqlServerDialect());
        ResultSet rs = dbMeta.getTables(conn.getCatalog(),  null,"%", new String[]{"TABLE"});
        return rs;
    }
    
	
    @Override
    protected void buildTableNames(List<TableMeta> ret) throws SQLException {
        ResultSet rs = getTablesResultSet();
        while (rs.next()) {
            String schem = rs.getString("TABLE_SCHEM");
            String tableName = rs.getString("TABLE_Name");
 
            if (schem.equals("sys")) {
                ConsoleUtil.printMessageWithDate(" Skip table :" + tableName + ",sys table");
                continue;
            }
            if (excludedTables.contains(tableName)) {
                ConsoleUtil.printMessageWithDate(" Skip table :" + tableName);
                continue;
            }
            if (isSkipTable(tableName)) {
                ConsoleUtil.printMessageWithDate(" Skip table :" + tableName);
                continue;
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
 
  
}
