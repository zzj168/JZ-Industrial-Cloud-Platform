package cn.jbolt.common.render;

/**
 * Byte流的文件类型
 * 
 * @ClassName: ByteFileType
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2019年11月9日
 * 
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public enum ByteFileType {
	PDF("application/pdf", "pdf"), 
	JPG("image/jpeg", "jpg");
	public String contentType;
	public String suffix;

	ByteFileType(String contentType, String suffix) {
		this.contentType = contentType;
		this.suffix = suffix;
	}
}
