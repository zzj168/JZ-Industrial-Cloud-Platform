package cn.jbolt.common.gen;

import java.util.List;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.generator.DataDictionaryGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;

/**
 * DataDictionary 数据字典生成器
 */
public class JBoltDataDictionaryGenerator extends DataDictionaryGenerator{
	public JBoltDataDictionaryGenerator(DataSource dataSource, String dataDictionaryOutputDir) {
		super(dataSource, dataDictionaryOutputDir);
	}

	@Override
	public void generate(List<TableMeta> tableMetas) {
		ConsoleUtil.printMessage("-------------------------DataDictionary-------------------------");
		ConsoleUtil.printMessageWithDate(" Generate DataDictionary file :Starting ...");
		ConsoleUtil.printMessageWithDate(" DataDictionary Output Dir: " + dataDictionaryOutputDir);
		ConsoleUtil.printMessageWithDate(" JBolt DataDictionary Generator is Working...");
		
		rebuildColumnMetas(tableMetas);
		StringBuilder ret = new StringBuilder();
		for (TableMeta tableMeta : tableMetas) {
			generateTable(tableMeta, ret);
		}
		writeToFile(ret.toString());
		
		ConsoleUtil.printMessageWithDate(" Generate DataDictionary file :Done ...");
	}
	
}
