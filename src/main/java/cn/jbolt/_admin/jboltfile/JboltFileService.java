package cn.jbolt._admin.jboltfile;

import java.io.File;
import java.util.Date;

import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.model.JboltFile;
import cn.jbolt.common.model.SystemLog;

/**  
 * 系统文件库管理 
 * @ClassName:  JBoltFileService   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月24日 上午12:40:27   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JboltFileService extends BaseService<JboltFile> {
	private JboltFile dao = new JboltFile().dao();
	@Override
	protected JboltFile dao() {
		return dao;
	}
	

	public Page<JboltFile> paginateAdminData(int pageNumber, int pageSize) {
		return paginate("id", "desc", pageNumber, pageSize);
	}

	/**
	 * 保存图片
	 * @param userId
	 * @param file
	 * @param uploadPath
	 * @return
	 */
	public Ret saveImageFile(Integer userId,UploadFile file, String uploadPath) {
		return saveFile(userId,file,uploadPath,JboltFile.FILE_TYPE_IMAGE);
	}
	/**
	 * 保存音频
	 * @param userId
	 * @param file
	 * @param uploadPath
	 * @return
	 */
	public Ret saveAudioFile(Integer userId,UploadFile file, String uploadPath) {
		return saveFile(userId,file,uploadPath,JboltFile.FILE_TYPE_AUDIO);
	}
	/**
	 * 保存视频
	 * @param userId
	 * @param file
	 * @param uploadPath
	 * @return
	 */
	public Ret saveVideoFile(Integer userId,UploadFile file, String uploadPath) {
		return saveFile(userId,file,uploadPath,JboltFile.FILE_TYPE_VEDIO);
	}
	/**
	 * 保存附件
	 * @param userId
	 * @param file
	 * @param uploadPath
	 * @return
	 */
	public Ret saveAttachmentFile(Integer userId,UploadFile file, String uploadPath) {
		return saveFile(userId,file,uploadPath,JboltFile.FILE_TYPE_ATTACHMENT);
	}
	/**
	 * 保存文件底层方法
	 * @param file
	 * @param uploadPath
	 * @param fileType
	 * @return
	 */
	public Ret saveFile(Integer userId,UploadFile file, String uploadPath,int fileType) {
		String localPath=file.getUploadPath()+"/"+file.getFileName();
		String localUrl=JFinal.me().getConstants().getBaseUploadPath()+"/"+uploadPath+"/"+file.getFileName();
		JboltFile jboltFile=new JboltFile();
		jboltFile.setUserId(userId);
		jboltFile.setCreateTime(new Date());
		jboltFile.setFileName(file.getOriginalFileName());
		jboltFile.setFileType(fileType);
		jboltFile.setLocalPath(localPath);
		jboltFile.setLocalUrl(localUrl);
		File realFile=file.getFile();
		long fileSize=FileUtil.size(realFile);
		jboltFile.setFileSize(fileSize);
		String fileSuffix=FileTypeUtil.getType(realFile);
		jboltFile.setFileSuffix(fileSuffix);
		boolean success=jboltFile.save();
		if(success){
			//添加日志
			addSaveSystemLog(jboltFile.getId(), userId, SystemLog.TARGETTYPE_JBOLT_FILE, jboltFile.getFileName());
		}
		return success?success(localUrl,"上传成功"):fail("上传失败");
	}




}
