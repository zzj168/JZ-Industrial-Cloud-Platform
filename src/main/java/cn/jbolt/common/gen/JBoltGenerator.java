package cn.jbolt.common.gen;

import java.util.List;

import javax.sql.DataSource;

import com.alibaba.druid.util.JdbcConstants;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.activerecord.generator.TableMeta;

import cn.hutool.core.util.StrUtil;
/**
 * 生成器主体
 * @ClassName:  JBoltGenerator   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年12月11日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JBoltGenerator extends Generator {
	private JBoltMetaBuilder metaBuilder;
	public JBoltGenerator(DataSource dataSource) {
		super(dataSource, new JBoltBaseModelGenerator(JBoltProjectGenConfig.baseModelPackageName, JBoltProjectGenConfig.baseModelOutputDir),
				new JBoltModelGenerator(JBoltProjectGenConfig.modelPackageName, JBoltProjectGenConfig.baseModelPackageName, JBoltProjectGenConfig.modelOutputDir));
		this.setMappingKitGenerator(
				new JBoltMappingKitGenerator(modelGenerator.getModelPackageName(), modelGenerator.getModelOutputDir()));
		this.setDataDictionaryGenerator(
				new JBoltDataDictionaryGenerator(dataSource, modelGenerator.getModelOutputDir()));
		switch (JBoltProjectGenConfig.dbType) {
			case JdbcConstants.MYSQL:
				setDialect(new MysqlDialect());
				metaBuilder = new JBoltMetaBuilder(dataSource);
				break;
			case JdbcConstants.ORACLE:
				setDialect(new JBoltOracleDialect());
				metaBuilder = new _OracleMetaBuilder(dataSource);
				break;
			case JdbcConstants.SQL_SERVER:
				setDialect(new SqlServerDialect());
				metaBuilder = new _SqlServerMetaBuilder(dataSource);
				break;
			case JdbcConstants.POSTGRESQL:
				setDialect(new PostgreSqlDialect());
				metaBuilder = new JBoltMetaBuilder(dataSource);
				String schemaPattern=getPostGresqlSchema(JBoltProjectGenConfig.jdbcUrl);
				if(schemaPattern!=null) {
					metaBuilder.setSchemaPattern(schemaPattern);
				}
				break;
				
		}
		if(metaBuilder==null) {
			throw new RuntimeException("目前只支持Mysql、Oracle、SqlServer、Postgresql四个数据库");
		}
		metaBuilder.filter(JBoltProjectGenConfig.filterPredicate);
		setMetaBuilder(metaBuilder);
		
	}

	private String getPostGresqlSchema(String jdbcUrl) {
		if(jdbcUrl.indexOf("currentSchema")!=-1) {
			String schemaname=jdbcUrl.substring(jdbcUrl.lastIndexOf("currentSchema")+13).replace("=", "").trim();
			if(StrUtil.isNotBlank(schemaname)) {
				return schemaname;
			}
		}
		return null;
	}


	@Override
	public void generate() {
		ConsoleUtil.printJboltcn();
		ConsoleUtil.printMessage(
				"=========================JBolt Generator:JFinal Model Generator:Start=========================");
		ConsoleUtil.printMessageWithDate(" JBolt Generate Start");
		if (dialect != null) {
			metaBuilder.setDialect(dialect);
		}

		long start = System.currentTimeMillis();
		List<TableMeta> allMatas = metaBuilder.build();
		if (allMatas.size() == 0) {
			ConsoleUtil.printMessageWithDate(" TableMeta 数量为 0，不生成任何文件");
			return;
		}
		baseModelGenerator.generate(allMatas);
		if (modelGenerator != null) {
			modelGenerator.generate(allMatas);
		}
		if (mappingKitGenerator != null) {
			mappingKitGenerator.generate(allMatas);
		}
		if (dataDictionaryGenerator != null && generateDataDictionary) {
			dataDictionaryGenerator.generate(allMatas);
		}
		long usedTime = (System.currentTimeMillis() - start) / 1000;
		ConsoleUtil.printMessageWithDate(" JBolt Generate complete in " + usedTime + " seconds.");
		ConsoleUtil.printMessage(
				"=========================JBolt Generator:JFinal Model Generator:Done=========================");
	}

	 

}
