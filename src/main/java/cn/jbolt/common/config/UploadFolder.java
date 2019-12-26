package cn.jbolt.common.config;

import cn.jbolt.common.util.DateUtil;

/**   
 * 上传控制 定义的目录
 * @ClassName:  UploadFolder   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月24日 上午12:00:48   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class UploadFolder {
	
	public static String todayFolder(){
		return DateUtil.getNowStr("yyyyMMdd");
	}
	public static String todayFolder(String path){
		return path+"/"+todayFolder();
	}
	public static final String DEMO_EDITOR_IMAGE="demo/editor";
	public static final String DEMO_IMAGE_UPLOADER="demo/imguploader";
	public static final String DEMO_FILE_UPLOADER="demo/fileuploader";
	public static final String EDITOR_SUMMERNOTE_IMAGE="summernote/image";
	public static final String EDITOR_NEDITOR_IMAGE="neditor/image";
	public static final String EDITOR_NEDITOR_WORD_IMAGE="neditor/wordimage";
	public static final String EDITOR_NEDITOR_VIDEO="neditor/video";
	public static final String MALL_GOODS_IMAGE="mall/goods";
	public static final String MALL_BRAND_IMAGE="mall/brand";
	public static final String WECHAT_MPINFO="wechat/mpinfo";
	public static final String WECHAT_AUTOREPLY_REPLYCONTENT="wechat/mp/autoreply/replycontent";
	public static final String WECHAT_MEDIA="wechat/mp/media";
	public static final String USER_AVATAR="user/avatar";

}
