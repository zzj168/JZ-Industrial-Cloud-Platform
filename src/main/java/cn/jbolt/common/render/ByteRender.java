package cn.jbolt.common.render;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
/**
 * 通用的文件字节渲染器
 * 来自于 https://www.jfinal.com/share/1877
 * @ClassName:  ByteRender   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年11月9日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class ByteRender extends Render {
 
	private String fileName;// 下载名称
	private byte[] fileByte;
	//前端试图展示的文件类型，例如想通过浏览器直接打开为pdf类型
	private ByteFileType fileType = ByteFileType.PDF;
	//浏览器直接试图查看和下载两种方式
	private ByteRenderType renderType = ByteRenderType.VIEW;// 默认类型
 
	private ServletOutputStream outputStream;
 
	@Override
	public void render() {
 
		if (validate() == false) {
			throw new RenderException("请检查ByteRender属性值是否为空");
		}
 
		if (renderType == ByteRenderType.DOWNLOAD) {
			response.setHeader("Content-Disposition", getFilename(request));
		}
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Accept-Ranges", "bytes");
		response.setDateHeader("Expires", 0);
		response.setContentType(fileType.contentType);
 
		try {
			this.outputStream = response.getOutputStream();
			switch (fileType) {
			case JPG:
				renderImage();
			case PDF:
				// 暂时只支持pdf
				renderPdf();
			}
 
		} catch (IOException e) {
			LogKit.error(e.getMessage(), e);
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
 
	void renderPdf() throws IOException {
		outputStream.write(fileByte);
		outputStream.flush();
		outputStream.close();
	}
 
	void renderImage() {
		//这里自定义扩展
	}
 
	/**
	 * 文件下载名称
	 */
	private String getFilename(HttpServletRequest request) {
		try {
			String agent = request.getHeader("USER-AGENT");
			if (agent.toLowerCase().indexOf("firefox") > 0) {
				fileName = new String(fileName.getBytes("utf-8"), "ISO8859-1");
			} else {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "attachment; filename=" + fileName + "." + fileType.suffix;
	}
 
	private boolean validate() {
		if (renderType == null || fileType == null) {
			return false;
		}
		// 如果是下载，需要传入下载的名称
		if (renderType == ByteRenderType.DOWNLOAD) {
			if (StrKit.isBlank(fileName)) {
				return false;
			}
		}
		if (fileByte == null || fileByte.length == 0) {
			return false;
		}
		return true;
	}
 
	public static ByteRender create() {
		return new ByteRender();
	}
	public static ByteRender create(byte[] fileByte) {
		return create().setFileByte(fileByte);
	}
 
	public ByteRender setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}
 
	public ByteRender setFileByte(byte[] fileByte) {
		this.fileByte = fileByte;
		return this;
	}
 
	public ByteRender setFileType(ByteFileType fileType) {
		this.fileType = fileType;
		return this;
	}
 
	public ByteRender setRenderType(ByteRenderType renderType) {
		this.renderType = renderType;
		return this;
	}
 
	
 
	@Override
	public String toString() {
		return "ByteRender [fileName=" + fileName + ", fileByte[length]="
				+ (fileByte == null ? "null" : fileByte.length) + ", fileType=" + fileType + ", renderType="
				+ renderType + "]";
	}
}