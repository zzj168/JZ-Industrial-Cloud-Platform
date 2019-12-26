package cn.jbolt.common.gen;

import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.generator.MappingKitGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;


/**
 * MappingKit 文件生成器
 */
public class JBoltMappingKitGenerator extends MappingKitGenerator {
	public JBoltMappingKitGenerator(String mappingKitPackageName, String mappingKitOutputDir) {
		super(mappingKitPackageName, mappingKitOutputDir);
	}
	@Override
	protected void initEngine() {
		super.initEngine();
		engine.setDevMode(true);
		setTemplate("/gentpl/mapping_kit_template.jf");
	}

	@Override
	public void generate(List<TableMeta> tableMetas) {
		ConsoleUtil.printMessage("-------------------------MappingKit-------------------------");
		ConsoleUtil.printMessageWithDate(" Generate MappingKit file :Starting ...");
		ConsoleUtil.printMessageWithDate(" MappingKit Output Dir: " + mappingKitOutputDir);
		ConsoleUtil.printMessageWithDate(" JBolt MappingKit Generator is Working...");
		
		Kv data = Kv.by("mappingKitPackageName", mappingKitPackageName);
		data.set("mappingKitClassName", mappingKitClassName);
		data.set("tableMetas", tableMetas);
		
		String ret = engine.getTemplate(template).renderToString(data);
		writeToFile(ret);
		
		ConsoleUtil.printMessageWithDate(" Generate MappingKit file :Done ...");
	}
	
}




