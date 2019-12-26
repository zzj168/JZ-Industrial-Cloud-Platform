package cn.jbolt.admin.wechat.media;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MediaApi;
import com.jfinal.weixin.sdk.utils.IOUtils;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.jbolt.admin.wechat.config.WechatConfigService;
import cn.jbolt.admin.wechat.mpinfo.WechatMpinfoService;
import cn.jbolt.base.BaseService;
import cn.jbolt.common.config.Msg;
import cn.jbolt.common.config.UploadFolder;
import cn.jbolt.common.db.sql.Sql;
import cn.jbolt.common.model.WechatMedia;
import cn.jbolt.common.model.WechatMpinfo;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.RealUrlUtil;

public class WechatMediaService extends BaseService<WechatMedia> {
	private WechatMedia dao = new WechatMedia().dao();
	@Inject
	private WechatConfigService wechatConfigService;
	@Inject
	private WechatMpinfoService wechatMpinfoService;
	@Override
	protected WechatMedia dao() {
		return dao;
	}
	/**
	 * 后台分页管理查询
	 * @param mpId
	 * @param type
	 * @param keywords
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<WechatMedia> paginateAdminList(Integer mpId, String type, String keywords, Integer pageNumber,
			int pageSize) {
		if(notOk(mpId)||notOk(type)) {return EMPTY_PAGE;}
		return daoTemplate("wechat.media.paginateAdminList", Kv.by("mpId", mpId).set("type",type).setIfNotBlank("keywords", keywords)).paginate(pageNumber, pageSize);
	}
	
	/**
	 * 同步素材库
	 * @param mpId
	 * @return
	 */
	public Ret syncAll(Integer mpId) {
		return syncDatas(mpId, false);
	}
	/**
	 * 同步素材库
	 * @param mpId
	 * @return
	 */
	public Ret syncNewDatas(Integer mpId) {
		return syncDatas(mpId, true);
	}
	/**
	 * 同步素材库
	 * @param mpId
	 * @return
	 */
	public Ret syncDatas(Integer mpId,boolean newDatas) {
		if(notOk(mpId)) {return fail(Msg.PARAM_ERROR);}
		WechatMpinfo mpinfo=wechatMpinfoService.findById(mpId);
		if(mpinfo==null) {return fail("微信公众平台信息不存在");}
		String appId=CACHE.me.getWechatConfigAppId(mpId);
		if(StrKit.isBlank(appId)){
			return fail(mpinfo.getName()+"基础配置异常:APPID");
		}
		
		ApiConfigKit.setThreadLocalAppId(appId);
		try {
		
		ApiResult apiResult = MediaApi.getMaterialCount();
		if (apiResult.isSucceed() == false) {
			return fail(apiResult.getErrorMsg());
		}
		Ret ret = syncNews(mpId, mpinfo.getName(), apiResult.getInt("news_count"),newDatas);
		if (ret.isFail()) {
			return ret;
		}
		
		ret = syncByType(mpId, mpinfo.getName(),WechatMedia.TYPE_IMG, apiResult.getInt("image_count"),newDatas);
		if (ret.isFail()) {
			return ret;
		}
		ret = syncByType(mpId, mpinfo.getName(),WechatMedia.TYPE_VOICE, apiResult.getInt("voice_count"),newDatas);
		if (ret.isFail()) {
			return ret;
		}
		ret = syncByType(mpId, mpinfo.getName(),WechatMedia.TYPE_VIDEO,apiResult.getInt("video_count"),newDatas);
		if (ret.isFail()) {
			return ret;
		}
		}catch (RuntimeException e) {
			return fail(e.getMessage());
		} finally {
			ApiConfigKit.removeThreadLocalAppId();
		}
		return SUCCESS;
	}
	
	/**
	 * 同步指定类型素材
	 * @param mpId
	 * @param mpname
	 * @param type
	 * @param totalCount
	 * @param newDatas 
	 * @return
	 */
	private Ret syncByType(Integer mpId, String mpname,String type, Integer totalCount, boolean newDatas) {
		if(totalCount==null||totalCount<0) {return fail("同步["+type+"]素材异常,总数参数异常");}
		if(totalCount==0) {return SUCCESS;}
		if(!newDatas) {
			//清空数据
			deleteByType(mpId,type);
		}
		//分页同步
		return syncMediaByPages(mpId,mpname,totalCount,type,newDatas);
	}
	 
	/**
	 * 同步除了news之外的 其他指定类型
	 * @param mpId
	 * @param mpname
	 * @param totalCount
	 * @param type
	 * @param newDatas 
	 * @return
	 */
	private Ret syncMediaByPages(Integer mpId, String mpname, Integer totalCount,String type, boolean newDatas) {
		int pageSize=20;
		int pageTotal=totalCount/pageSize+(totalCount%pageSize>0?1:0);
		Ret ret=null;
		List<WechatMedia> wechatMedias=new ArrayList<WechatMedia>();
		for(int pageNumber=1;pageNumber<=pageTotal;pageNumber++) {
			ret=syncMediaOnePage(mpId,type,pageNumber,pageSize,wechatMedias,newDatas);
			if(ret.isFail()) {
				if(newDatas&&"exist".equals(ret.getStr("msg"))) {
					break;
				}
				return ret;
			}
			if(wechatMedias.size()>=100) {
				Db.batchSave(wechatMedias, wechatMedias.size());
				wechatMedias.clear();
			}
		}
		if(wechatMedias.size()>0) {
			//最后一步保存释放
			Db.batchSave(wechatMedias, wechatMedias.size());
			wechatMedias.clear();
			wechatMedias=null;
		}
		return SUCCESS;
	
	}
	/**
	 * 同步图文
	 * @param mpId
	 * @param mpname
	 * @param totalCount
	 * @param newDatas 
	 * @return
	 */
	private Ret syncNews(Integer mpId, String mpname, Integer totalCount, boolean newDatas) {
		if(totalCount==null||totalCount<0) {return fail("同步图文素材异常,总数参数异常");}
		if(totalCount==0) {return SUCCESS;}
		if(!newDatas) {
			//清空数据
			deleteByType(mpId,WechatMedia.TYPE_NEWS);
		}
		//分页同步
		return syncNewsByPages(mpId,totalCount,newDatas);
	}
	/**
	 * 按照公众号ID和类型删除素材
	 * @param mpId
	 * @param type
	 * @return
	 */
	private Ret deleteByType(Integer mpId,String type) {
		return deleteBy(Kv.by("mp_id", mpId).set("type",type));
	}
	/**
	 * 分页同步图文素材
	 * @param mpId
	 * @param totalCount
	 * @param newDatas 
	 * @return
	 */
	private Ret syncNewsByPages(Integer mpId,Integer totalCount, boolean newDatas) {
		int pageSize=20;
		int pageTotal=totalCount/pageSize+(totalCount%pageSize>0?1:0);
		Ret ret=null;
		List<WechatMedia> wechatMedias=new ArrayList<WechatMedia>();
		for(int pageNumber=1;pageNumber<=pageTotal;pageNumber++) {
			ret=syncNewOnePage(mpId,pageNumber,pageSize,wechatMedias,newDatas);
			if(ret.isFail()) {
				if(newDatas&&"exist".equals(ret.getStr("msg"))) {
					break;
				}
				return ret;
			}
			if(wechatMedias.size()>=100) {
				Db.batchSave(wechatMedias, wechatMedias.size());
				wechatMedias.clear();
			}
		}
		if(wechatMedias.size()>0) {
			//最后一步保存释放
			Db.batchSave(wechatMedias, wechatMedias.size());
			wechatMedias.clear();
			wechatMedias=null;
		}
		return SUCCESS;
	}

	/**
	 * 同步一页的图文素材
	 * @param mpId
	 * @param pageNumber
	 * @param pageSize
	 * @param newDatas 
	 * @return
	 */
	private Ret syncNewOnePage(Integer mpId,int pageNumber,int pageSize,List<WechatMedia> wechatMedias, boolean newDatas) {
		int offset=(pageNumber-1)*pageSize;
		ApiResult apiResult=MediaApi.batchGetMaterialNews(offset, pageSize);
		if(apiResult.isSucceed()==false) {
			return fail(apiResult.getErrorMsg());
		}
		Integer itemCount=apiResult.getInt("item_count");
		if(itemCount==null) {
			return fail("同步图文素材异常，当前同步到第["+pageNumber+"]页");
		}
		if(itemCount>0) {
			String json=apiResult.getJson();
			JSONObject result=JSON.parseObject(json);
			JSONArray item=result.getJSONArray("item");
			int size=item.size();
			Ret ret=null;
			for(int i=0;i<size;i++) {
				ret=processOneItemNews(mpId,item.getJSONObject(i),wechatMedias,newDatas);
				if(newDatas&&ret.isFail()&&"exist".equals(ret.getStr("msg"))) {
					return ret;
				}
			}
		}
		return SUCCESS;
	}
	/**
	 * 同步一页的指定类型的素材
	 * @param mpId
	 * @param type
	 * @param pageNumber
	 * @param pageSize
	 * @param wechatMedias
	 * @param newDatas 
	 * @return
	 */
	private Ret syncMediaOnePage(Integer mpId,String type,int pageNumber,int pageSize,List<WechatMedia> wechatMedias, boolean newDatas) {
		int offset=(pageNumber-1)*pageSize;
		ApiResult apiResult=MediaApi.batchGetMaterial(type,offset, pageSize);
		if(apiResult.isSucceed()==false) {
			return fail(apiResult.getErrorMsg());
		}
		Integer itemCount=apiResult.getInt("item_count");
		if(itemCount==null) {
			return fail("同步["+type+"]素材异常，当前同步到第["+pageNumber+"]页");
		}
		if(itemCount>0) {
			String json=apiResult.getJson();
			JSONObject result=JSON.parseObject(json);
			JSONArray item=result.getJSONArray("item");
			int size=item.size();
			JSONObject jsonObject=null;
			for(int i=0;i<size;i++) {
				jsonObject=item.getJSONObject(i);
				if(newDatas) {
					boolean exist=checkExistMedia(mpId,type,jsonObject.getString("media_id"));
					if(exist) {
						return fail("exist");
					}
				}
				wechatMedias.add(convertToMedia(mpId,type,jsonObject));
			}
		}
		return SUCCESS;
	}
	private boolean checkExistMedia(Integer mpId,String type, String mediaId) {
		Sql sql=selectSql().selectId().eqQM("mp_id","type","media_id").first();
		Integer existId=queryInt(sql,mpId,type,mediaId);
		return isOk(existId);
	}
	/**
	 * 处理一个Item
	 * @param mpId
	 * @param media
	 * @param wechatMedias
	 */
	private WechatMedia convertToMedia(Integer mpId, String type,JSONObject media) {
		String mediaId=media.getString("media_id");
		WechatMedia wechatMedia=new WechatMedia();
		String name=media.getString("name");
		if(StrKit.isBlank(name)) {
			name=media.getString("title");
		}
		wechatMedia.setTitle(name);
		wechatMedia.setDigest(media.getString("digest"));
		wechatMedia.setType(type);
		wechatMedia.setMediaId(mediaId);
		wechatMedia.setMpId(mpId);
		Long updateTime=media.getLong("update_time");
		if(updateTime!=null) {
			wechatMedia.setUpdateTime(new Date(updateTime*1000L));
		}else {
			wechatMedia.setUpdateTime(new Date());
		}
		return wechatMedia;
	}
	/**
	 * 解析处理图文的一个Item 下的单图文和多图文
	 * @param mpId
	 * @param item
	 * @param wechatMedias 
	 * @param newDatas 
	 * @return 
	 */
	private Ret processOneItemNews(Integer mpId, JSONObject item, List<WechatMedia> wechatMedias, boolean newDatas) {
		String mediaId=item.getString("media_id");
		JSONObject content=item.getJSONObject("content");
		JSONArray newsItem=content.getJSONArray("news_item");
		int size=newsItem.size();
		JSONObject jsonObject=null;
		for(int i=0;i<size;i++) {
			jsonObject=newsItem.getJSONObject(i);
			if(newDatas) {
				if(checkExistNews(mpId,jsonObject.getString("url"))) {
					return fail("exist");
				}
			}
			if("分享一篇文章。".equals(jsonObject.getString("digest"))){
				continue;
			}
			wechatMedias.add(covertToNews(jsonObject,mpId,mediaId));
		}
		return SUCCESS;
	}
	/**
	 * 检测图文信息是否存在
	 * @param mpId
	 * @param url
	 * @return
	 */
	private boolean checkExistNews(Integer mpId,String url) {
		Sql sql=selectSql().selectId().eqQM("mp_id","type","url").first();
		Integer existId=queryInt(sql,mpId,WechatMedia.TYPE_NEWS,url);
		return isOk(existId);
	}
	/**
	 * 将JSON转WechatMedia中的news
	 * @param media
	 * @param mpId
	 * @param mediaId
	 * @return
	 */
	private WechatMedia covertToNews(JSONObject media,Integer mpId,String mediaId) {
		WechatMedia wechatMedia=new WechatMedia();
		wechatMedia.setAuthor(media.getString("author"));
		wechatMedia.setTitle(media.getString("title"));
		wechatMedia.setDigest(media.getString("digest"));
		wechatMedia.setUrl(media.getString("url"));
		wechatMedia.setType(WechatMedia.TYPE_NEWS);
		wechatMedia.setThumbMediaId(media.getString("thumb_media_id"));
		wechatMedia.setMediaId(mediaId);
		wechatMedia.setContentSourceUrl(media.getString("content_source_url"));
		wechatMedia.setMpId(mpId);
		Long updateTime=media.getLong("update_time");
		if(updateTime!=null) {
			wechatMedia.setUpdateTime(new Date(updateTime*1000L));
		}else {
			wechatMedia.setUpdateTime(new Date());
		}
		return wechatMedia;
	}
	//判断是否正在执行每隔一分钟的下载任务
	public static boolean downloading=false;
	/**
	 *  执行同步下载素材元数据
	 */
	public Ret downloadWechatMedia(Integer userId,Integer mpId) {
		if(notOk(mpId)) {return fail(Msg.PARAM_ERROR);}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {
			return fail("当前操作的微信公众平台信息不存在");
		}
		System.out.println("调用微信公众号素材下载任务 start");
		if(downloading) {System.out.println("任务执行中，等待一分钟后重新调用...");return fail("任务执行中，等待一分钟后重新调用...");}
		downloading=true;
		//开始执行下载 首先获取需要同步现在的media
		List<WechatMedia> needDownloadMedias=getNeedDownloadMedias(mpId,500);
		Ret ret=null;
		if(needDownloadMedias!=null&&needDownloadMedias.size()>0) {
			System.out.println("找到["+needDownloadMedias.size()+"]个需要下载的任务");
			ret=downloadMedias(needDownloadMedias);
		}else {
			ret=success("没有需要下载的任务了");
			System.out.println("没有需要下载的任务了");
		}
		downloading=false;
		System.out.println("调用微信公众号素材下载任务 end");
		return ret;
	}
	/**
	 * 下载素材
	 * @param needDownloadMedias
	 */
	private Ret downloadMedias(List<WechatMedia> needDownloadMedias) {
		String todayFolder=UploadFolder.todayFolder();
		Ret ret=null;
		for(WechatMedia media:needDownloadMedias) {
			ret=downloadMedia(media, todayFolder);
			if(ret.isFail()) {
				return ret;
			}
		}
		return success("素材下载成功："+needDownloadMedias.size()+"个");
	}

	/**
	 * 下载 meida
	 * @param media
	 * @param todayFolder
	 */
	private Ret downloadMedia(WechatMedia media,String todayFolder) {
		String appId=CACHE.me.getWechatConfigAppId(media.getMpId());
		if(notOk(appId)) {
			return fail("当前操作微信公众平台，配置异常：APPID");
		}
		ApiConfigKit.setThreadLocalAppId(appId);
		try {
			String mediaId=media.getType().equals(WechatMedia.TYPE_NEWS)?media.getThumbMediaId():media.getMediaId();
			InputStream stream=MediaApi.getMaterial(mediaId);
			
			if(stream!=null) {
				
				String fileFolderPath=JFinal.me().getConstants().getBaseUploadPath()+"/"+UploadFolder.WECHAT_MEDIA+"/"+media.getMpId()+"/"+media.getType()+"/"+todayFolder;
				if(media.getType().equals(WechatMedia.TYPE_VIDEO)) {
					String json=IOUtils.toString(stream);
					if(StrKit.notBlank(json)&&json.indexOf("errcode")!=-1&&json.indexOf("45009")!=-1) {
						System.out.println("永久素材下载限额已超，请登录后清零");
						return fail("永久素材下载限额已超，请登录后清零");
					}
					
						JSONObject jsonObject=JSON.parseObject(json);
						if(jsonObject.containsKey("down_url")) {
							String videoUrl=jsonObject.getString("down_url");
							String description=jsonObject.getString("description");
							media.setServerUrl(videoUrl);
							if(StrKit.notBlank(description)) {
								media.setDigest(description);
							}
							media.update();
						}
				}else {
					String filename=null;
					switch (media.getType()) {
					case WechatMedia.TYPE_NEWS:
						filename=media.getMpId()+"_"+media.getId()+"_"+mediaId+".jpg";
						break;
					case WechatMedia.TYPE_IMG:
						filename=media.getMpId()+"_"+media.getId()+"_"+mediaId+".jpg";
						break;
					case WechatMedia.TYPE_VOICE:
						filename=media.getMpId()+"_"+media.getId()+"_"+mediaId+".mp3";
						break;
					}
					String filePath=fileFolderPath+"/"+filename;
					boolean isAbs=FileUtil.isAbsolutePath(filePath);
					String absPath=isAbs?filePath:(PathKit.getWebRootPath()+"/"+filePath);
					
					if(FileUtil.exist(absPath)) {
						media.setServerUrl(filePath);
						media.update();
						return SUCCESS;
					}else {
						if(!isAbs) {
							fileFolderPath=PathKit.getWebRootPath()+"/"+fileFolderPath;
						}
						FileUtil.mkdir(fileFolderPath);
					}
					File targetFile=new File(absPath);
					IOUtils.toFile(stream,targetFile);
					if(targetFile.exists()) {
						if(targetFile.length()==0) {
							targetFile.delete();
							media.setServerUrl("/assets/img/uploadimg.png");
							media.update();
						}else {
							String type=FileTypeUtil.getType(targetFile);
							if(StrKit.isBlank(type)) {
								String json=FileUtil.readUtf8String(absPath);
								if(StrKit.notBlank(json)&&json.indexOf("errcode")!=-1&&json.indexOf("45009")!=-1) {
									System.out.println("永久素材下载限额已超，请登录后清零");
								}else {
									media.setServerUrl(filePath);
									media.update();
								}
							}else {
								media.setServerUrl(filePath);
								media.update();
							}
							
						}
						
						
					}
				}
				
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ApiConfigKit.removeThreadLocalAppId();
		}
		return SUCCESS;
		
	}
	  
	/**
	 * 获取指定个数的需要下载素材资源的media
	 * @param count
	 * @return
	 */
	private List<WechatMedia> getNeedDownloadMedias(Integer mpId,int count) {
		Sql sql=selectSql().eqQM("mp_id").isNull("server_url").firstPage(count);
		return find(sql.toSql(),mpId);
	}
	/**
	 * 删除指定公众平台的
	 * @param mpId
	 * @return
	 */
	public Ret deleteByMpId(Integer mpId) {
		return deleteBy(Kv.by("mp_id", mpId));
	}
	/**
	 * 在回复内容选择处选择一条数据使用
	 * @param mpId
	 * @param id
	 * @return
	 */
	public Ret getReplyChooseInfo(Integer mpId, Integer id) {
		if(notOk(mpId)||notOk(id)) {return fail(Msg.PARAM_ERROR);}
		WechatMpinfo wechatMpinfo=wechatMpinfoService.findById(mpId);
		if(wechatMpinfo==null) {
			return fail("关联微信公众平台信息不存在");
		}
		WechatMedia wechatMedia=findById(id);
		if(wechatMedia==null) {
			return fail(Msg.DATA_NOT_EXIST);
		}
		if(wechatMedia.getMpId().intValue()!=mpId.intValue()) {
			return fail(Msg.PARAM_ERROR+":mpId");
		}
		String serverUrl=wechatMedia.getServerUrl();
		if(isOk(serverUrl)) {
			if(wechatMedia.getType().equals(WechatMedia.TYPE_IMG)||wechatMedia.getType().equals(WechatMedia.TYPE_NEWS)) {
				wechatMedia.put("realImgUrl",RealUrlUtil.getImage(serverUrl));
			}else if(wechatMedia.getType().equals(WechatMedia.TYPE_VOICE)) {
				wechatMedia.put("realVoiceUrl",RealUrlUtil.get(serverUrl,null));
			}
		}
		return success(wechatMedia,Msg.SUCCESS);
	}
 

}
