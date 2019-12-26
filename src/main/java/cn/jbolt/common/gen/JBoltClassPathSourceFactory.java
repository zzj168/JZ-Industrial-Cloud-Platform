package cn.jbolt.common.gen;

import com.jfinal.template.source.ISource;
import com.jfinal.template.source.ISourceFactory;

public class JBoltClassPathSourceFactory implements ISourceFactory {

	@Override
	public ISource getSource(String baseTemplatePath, String fileName, String encoding) {
		return new JboltClassPathSource(baseTemplatePath, fileName, encoding);
	}


}
