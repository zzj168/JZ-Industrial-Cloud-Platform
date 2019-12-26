package cn.jbolt.common.gen;

import com.jfinal.template.source.ClassPathSource;

public class JboltClassPathSource extends ClassPathSource {

	public JboltClassPathSource(String baseTemplatePath, String fileName, String encoding) {
		super(baseTemplatePath, fileName, encoding);
	}
	@Override
	protected ClassLoader getClassLoader() {
		return this.getClass().getClassLoader();
	}

}
