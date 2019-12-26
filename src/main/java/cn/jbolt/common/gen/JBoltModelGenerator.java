package cn.jbolt.common.gen;

import java.util.List;

import com.jfinal.plugin.activerecord.generator.ModelGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;


/**
 * Model 生成器
 */
public class JBoltModelGenerator extends ModelGenerator {
	public JBoltModelGenerator(String modelPackageName, String baseModelPackageName, String modelOutputDir) {
		super(modelPackageName, baseModelPackageName, modelOutputDir);
	}
	@Override
	protected void initEngine() {
		super.initEngine();
		engine.setDevMode(true);
		setTemplate("/gentpl/model_template.jf");
	}

	@Override
	public void generate(List<TableMeta> tableMetas) {
		ConsoleUtil.printMessage("-------------------------Model-------------------------");
		ConsoleUtil.printMessageWithDate(" Generate Model :Starting ...");
		ConsoleUtil.printMessageWithDate(" Model Output Dir: " + modelOutputDir);
		ConsoleUtil.printMessageWithDate(" JBolt Model Generator is Working...");
		
		for (TableMeta tableMeta : tableMetas) {
			genModelContent(tableMeta);
		}
		writeToFile(tableMetas);
		
		ConsoleUtil.printMessageWithDate(" Generate Model :Done ...");
	}
	@Override
	protected void genModelContent(TableMeta tableMeta) {
		ConsoleUtil.printMessageWithDate(" Generate Model:"+modelPackageName+"."+tableMeta.modelName);
		super.genModelContent(tableMeta);
	}
}


