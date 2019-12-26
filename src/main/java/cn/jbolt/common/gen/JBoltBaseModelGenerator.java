package cn.jbolt.common.gen;

import java.util.List;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;

/**
 * Base model 生成器
 */
public class JBoltBaseModelGenerator extends BaseModelGenerator{
	public JBoltBaseModelGenerator(String baseModelPackageName, String baseModelOutputDir) {
		super(baseModelPackageName, baseModelOutputDir);
		getterTypeMap.put("java.lang.Boolean", "getBoolean");
	}
	
	@Override
	protected void initEngine() {
		super.initEngine();
		engine.setDevMode(true);
		setTemplate("/gentpl/base_model_template.jf");
	}
	@Override
	public void generate(List<TableMeta> tableMetas) {
		ConsoleUtil.printMessage("-------------------------Base Model-------------------------");
		ConsoleUtil.printMessageWithDate(" Generate Base Model :Starting ...");
		ConsoleUtil.printMessageWithDate(" Base Model Output Dir: " + baseModelOutputDir);
		ConsoleUtil.printMessageWithDate(" JBolt Base Model Generator is Working...");
		for (TableMeta tableMeta : tableMetas) {
			genBaseModelContent(tableMeta);
		}
		writeToFile(tableMetas);
		ConsoleUtil.printMessageWithDate(" Generate Base Model :Done ...");
	}
	
	@Override
	protected void genBaseModelContent(TableMeta tableMeta) {
		ConsoleUtil.printMessageWithDate(" Generate Base Model:"+baseModelPackageName+"."+tableMeta.baseModelName);
		Kv data = Kv.by("baseModelPackageName", baseModelPackageName);
		data.set("generateChainSetter", generateChainSetter);
		data.set("tableMeta", tableMeta);
		data.set("charToBoolean", JBoltProjectGenConfig.charToBoolean);
		data.set("extendsJBoltBaseModel",JBoltProjectGenConfig.baseModelExtendsJBoltBaseModel);
		
		tableMeta.baseModelContent = engine.getTemplate(template).renderToString(data);
	}
	
}






