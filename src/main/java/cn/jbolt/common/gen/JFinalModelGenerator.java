package cn.jbolt.common.gen;

import javax.sql.DataSource;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.druid.DruidPlugin;

import cn.hutool.core.util.StrUtil;
import cn.jbolt.common.config.MainConfig;
import cn.jbolt.common.util.ArrayUtil;

/**
 * jfinal model Generator 主要用来生成Model和BaseModel
 * @ClassName:  JFinalModelGenerator   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年12月10日   
 */
public class JFinalModelGenerator {
	private static DruidPlugin druidPlugin;
	private static DataSource getDataSource() {
		druidPlugin = MainConfig.createDruidPlugin();
		JBoltProjectGenConfig.initDbConfig(PropKit.get("db_type"),PropKit.get("jdbc_url"));
		boolean success=druidPlugin.start();
		return success?druidPlugin.getDataSource():null;
	}

	public static void run(){
		final DataSource dataSource=getDataSource();
		if(dataSource==null) {
			ConsoleUtil.printMessage("=====JFinalModelGenerator.run dataSource Error====");
			return;
		}
		// 创建生成器
		final JBoltGenerator generator = new JBoltGenerator(dataSource);
		generator.setGenerateRemarks(true);
		// 设置是否在 Model 中生成 dao 对象
		generator.setGenerateDaoInModel(JBoltProjectGenConfig.generateDaoInModel);
		// 设置是否生成字典文件
		generator.setGenerateDataDictionary(JBoltProjectGenConfig.generateDataDictionary);
		// 设置是否生成setter链
		generator.setGenerateChainSetter(JBoltProjectGenConfig.generateChainSetterInBaseModel);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		String remove=JBoltProjectGenConfig.removedTableNamePrefixes.trim();
		if(StrUtil.isNotBlank(remove)) {
			String[] arr=ArrayUtil.from(remove, ",");
			if(arr!=null&&arr.length>0) {
				generator.setRemovedTableNamePrefixes(arr);
			}
		}
	
		System.out.println("Model Generator is Running...");
		generator.generate();
	}
	
	public static void main(String[] args) {
		JFinalModelGenerator.run();
	}
}
