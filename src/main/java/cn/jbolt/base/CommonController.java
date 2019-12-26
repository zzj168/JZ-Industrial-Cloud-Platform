package cn.jbolt.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.upload.UploadFile;

import cn.jbolt.common.config.JBoltTimestampConverter;
import cn.jbolt.common.config.PageSize;
import cn.jbolt.common.model.GlobalConfig;
import cn.jbolt.common.render.ByteFileType;
import cn.jbolt.common.render.ByteRender;
import cn.jbolt.common.render.ByteRenderType;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.captcha.JBoltCaptchaRender;
/**
 * 封装controller层简化用法 和常用方法
 * @ClassName:  CommonController   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年8月8日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class CommonController extends Controller implements IJBoltParaValidator{
	/**
	 * 判断Object参数有效性
	 * @param param
	 */
	@NotAction
	public boolean isOk(Object param){
		return JBoltParaValidator.isOk(param);
	}
	/**
	 * 判断Object参数是否无效
	 */
	@NotAction
	public boolean notOk(Object param){
		return JBoltParaValidator.notOk(param);
	}
	
	/**
	 * 判断上传文件是图片
	 * @param isImage
	 */
	@NotAction
	public boolean isImage(String contentType){
		return JBoltParaValidator.isImage(contentType);
	}
	/**
	 * 判断上传文件不是图片
	 * @param notImage
	 */
	@NotAction
	public boolean notImage(String contentType){
		return JBoltParaValidator.notImage(contentType);
	}
	/**
	 * 判断上传文件类型不是图片
	 * @param file
	 */
	@NotAction
	public boolean notImage(UploadFile file){
		return JBoltParaValidator.notImage(file);
	}
	/**
	 * 判断上传文件类型是否为图片
	 * @param file
	 */
	@NotAction
	public boolean isImage(UploadFile file){
		return JBoltParaValidator.isImage(file);
	}
	
	/**
	 * 判断Object[]数组类型数据是否正确
	 * @param param
	 * @return
	 */
	@NotAction
	public boolean isOk(Object[] param){
		return JBoltParaValidator.isOk(param);
	}
	/**
	 * 判断Object[]数组类型数据不正确
	 * @param param
	 * @return
	 */
	@NotAction
	public boolean notOk(Object[] param){
		return JBoltParaValidator.notOk(param);
	}
	
	 
	/**
	 * 获取Html5中type="datetime-local"的数据 返回Date
	 * @param name
	 * @return
	 */
	protected Date getDateTime(String name){
		return JBoltTimestampConverter.doConvertDateTime(get(name));
	}
	/**
	 * 判断是否是Ajax请求
	 * @return
	 */
	@NotAction
	public boolean isAjax(){
		return ControllerKit.isAjax(this);
	}
	
	/**
	 * 返回经过格式包装的JSON数据
	 * @param data
	 */
	@NotAction
	public void renderJsonData(Object data){
		ControllerKit.renderJsonData(this,data);
	}
	/**
	 * 返回经过包装的JSON成功信息
	 */
	@NotAction
	public void renderJsonSuccess(){
		ControllerKit.renderJsonSuccess(this);
	}
	/**
	  *  返回经过包装的JSON成功信息
	 * @param msg
	 */
	@NotAction
	public void renderJsonSuccess(String msg){
		ControllerKit.renderJsonSuccess(this, msg);
	}
	/**
	 * 设置返回的错误信息内容
	 */
	protected Ret setFailMsg(String msg){
		return Ret.fail("msg", msg);
	}
	/**
	 * 返回经过格式包装的错误信息
	 * @param msg
	 */
	@NotAction
	public void renderJsonFail(String msg){
		ControllerKit.renderJsonFail(this,msg);
	}
	/**
	 * 设置返回信息内容
	 * @param msg
	 */
	protected void setMsg(String msg){
		set("msg", msg);
	}
	
	/**
	 * 获取type参数
	 * @return
	 */
	protected Integer getType() {
		return getInt("type");
	}
	/**
	 * 设置type属性
	 * @return
	 */
	protected void setType(Integer type) {
		set("type", type);
	}
	/**
	 * 常用关键词搜索 获得关键词参数
	 * @return
	 */
	protected String getKeywords() {
		return get("keywords");
	}
	/**
	 * 常用关键词搜索 获得关键词参数
	 * @return
	 */
	protected void setKeywords(String keywords) {
		set("keywords", keywords);
	}
	/**
	 * 常用关键词搜索 获得关键词参数
	 * @return
	 */
	protected String getKeywords(String key) {
		return get(key);
	}
	/**
	 * 获得enable
	 * @return
	 */
	protected Boolean getEnable() {
		return getBoolean("enable");
	}
	/**
	 * 常用获得state状态参数
	 * @return
	 */
	protected Integer getState() {
		return getInt("state");
	}
	/**
	 * 常用获得state状态参数
	 * @return
	 */
	protected Integer getState(Integer defaultValue) {
		return getInt("state",defaultValue);
	}
	/**
	 * 从参数中获得分页 当前要查询第几页的pageNumber
	 * @return
	 */
	protected Integer getPageNumber() {
		return getInt("page", 1);
	}
	/**
	 * 从参数中获得分页 的每页数据量pageSize 默认10
	 * @return
	 */
	protected Integer getPageSize() {
		return getInt("pageSize", PageSize.PAGESIZE_ADMIN_LIST);
	}
	/**
	 * 从参数中获得分页 的每页数据量pageSize 自定义默认值
	 * @param defaultPageSize
	 * @return
	 */
	protected Integer getPageSize(int defaultPageSize) {
		return getInt("pageSize", defaultPageSize);
	}
	
	/**
	 * 判断请求是否是Pjax请求
	 * @return
	 */
	@NotAction
	public boolean isPjax(){
		return ControllerKit.isPjax(this);
	}
	 
	/**
	 * 判断请求是否是ajaxPortal请求
	 * @return
	 */
	@NotAction
	public boolean isAjaxPortal(){
		return ControllerKit.isAjaxPortal(this);
	}

	
	
	/**
	 * 获取Json数据
	 * @return
	 */
	@NotAction
	public JSONObject getJSONObject() {
		String json=getRawData();
		if(notOk(json)) {return null;}
		JSONObject jsonObject=null;
		try {
			jsonObject=JSON.parseObject(json);
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		return jsonObject;
	}
	/**
	 * 获取Json数据转为JSonArray
	 * @return
	 */
	@NotAction
	public JSONArray getJSONArray() {
		String json=getRawData();
		if(notOk(json)) {return null;}
		JSONArray jsonArray=null;
		try {
			jsonArray=JSON.parseArray(json);
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		return jsonArray;
	}
	
	/**
	 * 获取Json数据转为JSonArray
	 * @return
	 */
	@NotAction
	public List<JSONObject> getJSONObjectList() {
		String json=getRawData();
		if(notOk(json)) {return null;}
		JSONArray jsonArray=null;
		try {
			jsonArray=JSON.parseArray(json);
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		if(jsonArray==null) {return null;}
	
		return jsonArrayToObjectList(jsonArray);
	}
	/**
	 * 将jsonarray转为List<JSONObject>
	 * @param jsonArray
	 * @return
	 */
	@NotAction
	public List<JSONObject> jsonArrayToObjectList(JSONArray jsonArray) {
		int size=jsonArray.size();
		List<JSONObject> jsonObjects=new ArrayList<JSONObject>();
		for (int i = 0; i < size; i++) {
			jsonObjects.add(jsonArray.getJSONObject(i));
		}
		return jsonObjects;
	}
	/**
	 * 获取Json数据 转为Java List
	 * @return
	 */
	@NotAction
	public <T> List<T> getJSONList(Class<T> clazz) {
		String json=getRawData();
		if(notOk(json)) {return null;}
		List<T> list=null;
		try {
			list=JSON.parseArray(json, clazz);
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		return list;
	}
	/**
	 * 根据key获取json数据 转为JSONObject
	 * @param key
	 * @return
	 */
	@NotAction
	public JSONObject getJSONObject(String key) {
		if(notOk(key)) {return null;}
		JSONObject jsonObject=getJSONObject();
		if(jsonObject==null) {return null;}
		return jsonObject.getJSONObject(key);
	}
	
	/**
	 * 根据key获取json数据 转为JSONArray
	 * @param key
	 * @return
	 */
	@NotAction
	public JSONArray getJSONArray(String key) {
		if(notOk(key)) {return null;}
		JSONObject jsonObject=getJSONObject();
		if(jsonObject==null) {return null;}
		return jsonObject.getJSONArray(key);
	}
	/**
	 * 根据key获取json数据 转为List<JSONObject>
	 * @param key
	 * @return
	 */
	@NotAction
	public List<JSONObject> getJSONObjectList(String key) {
		if(notOk(key)) {return null;}
		JSONArray jsonArray=getJSONArray(key);
		if(jsonArray==null) {return null;}
		return jsonArrayToObjectList(jsonArray);
	}
	/**
	 * 根据key获取json数据 转为java List
	 * @param key
	 * @return
	 */
	@NotAction
	public <T> List<T> getJSONList(String key,Class<T> clazz) {
		if(notOk(key)) {return null;}
		JSONArray jsonArray=getJSONArray(key);
		if(jsonArray==null) {return null;}
		return jsonArray.toJavaList(clazz);
	}
	
	
	/**
	 * JBolt定制getModel实现
	 */
	public <T> T getModel(Class<T> modelClass) {
		return (T)JBoltInjector.injectModel(modelClass, getRequest(), false);
	}
	
	public <T> T getModel(Class<T> modelClass, boolean skipConvertError) {
		return (T)JBoltInjector.injectModel(modelClass, getRequest(), skipConvertError);
	}
	
	/**
	 * Get model from http request.
	 */
	public <T> T getModel(Class<T> modelClass, String modelName) {
		return (T)JBoltInjector.injectModel(modelClass, modelName, getRequest(), false);
	}
	
	public <T> T getModel(Class<T> modelClass, String modelName, boolean skipConvertError) {
		return (T)JBoltInjector.injectModel(modelClass, modelName, getRequest(), skipConvertError);
	}
	
	public <T> T getBean(Class<T> beanClass) {
		return (T)JBoltInjector.injectBean(beanClass, getRequest(), false);
	}
	
	public <T> T getBean(Class<T> beanClass, boolean skipConvertError) {
		return (T)JBoltInjector.injectBean(beanClass, getRequest(), skipConvertError);
	}
	
	public <T> T getBean(Class<T> beanClass, String beanName) {
		return (T)JBoltInjector.injectBean(beanClass, beanName, getRequest(), false);
	}
	
	public <T> T getBean(Class<T> beanClass, String beanName, boolean skipConvertError) {
		return (T)JBoltInjector.injectBean(beanClass, beanName, getRequest(), skipConvertError);
	}
	/**
	 * 根据类型生成验证码
	 * @param type
	 */
	@NotAction
	public void renderJBoltCaptcha(String type) {
		render(new JBoltCaptchaRender(type));
	}
	/**
	 * 返回气泡PNG 验证码
	 */
	@NotAction
	public void renderBubblePngCaptcha() {
		render(new JBoltCaptchaRender(GlobalConfig.CAPTCHA_TYPE_BUBBLE_PNG));
	}
	/**
	 * 返回gif格式的验证码
	 */
	@NotAction
	public void renderGifCaptcha() {
		render(new JBoltCaptchaRender(GlobalConfig.CAPTCHA_TYPE_GIF));
	}
	/**
	 * 获取Checkbox选中值
	 * @param name
	 * @return
	 */
	@NotAction
	public String[] getCheckBoxValues(String name) {
		return getParaValues(name);
	}
	
	/**
	 * 获取Checkbox选中值
	 * @param name
	 * @return
	 */
	@NotAction
	public Integer[] getCheckBoxValuesToInt(String name) {
		return getParaValuesToInt(name);
	}
	

	/**
	 * 获取Checkbox选中值
	 * @param name
	 * @return
	 */
	@NotAction
	public Long[] getCheckBoxValuesToLong(String name) {
		return getParaValuesToLong(name);
	}
	/**
	 * 保持住表单checkbox的值
	 * @param name
	 */
	@NotAction
	public void keepCheckbox(String name) {
		String[] checkValues=getCheckBoxValues(name);
		if(checkValues!=null&&checkValues.length>0){
			set(name, ArrayUtil.join(checkValues, ","));
		}
	}
	
	
	@Override
	public Controller keepModel(Class<? extends Model> modelClass, String modelName) {
		if (StrKit.notBlank(modelName)) {
			set(modelName, getModel(modelClass,modelName, true));
		} else {
			keepPara();
		}
		return this;
	}
	
	@Override
	public Controller keepBean(Class<?> beanClass, String beanName) {
		if (StrKit.notBlank(beanName)) {
			set(beanName, getBean(beanClass,beanName, true));
		} else {
			keepPara();
		}
		return this;
	}
	
	/**
	 * 批量获取数据转为Model
	  * 必须符合modelName[0...n].attrName格式的
	 * @param <T>
	 * @param modelClass
	 * @param modelName
	 * @return
	 */
	public <T> List<T> getModels(Class<T> modelClass,String modelName){
        Pattern p = Pattern.compile(modelName + "\\[\\d+\\].[a-zA-z0-9]+");       
        Map<String, String[]> paraMap = getParaMap();      
        String paraKey;        
        Set<String> modelNameSet = new HashSet<String>();        
        for (Entry<String, String[]> e : paraMap.entrySet()) {
	        paraKey = e.getKey();           
	        if(p.matcher(paraKey).find()){
	          modelNameSet.add(paraKey.split("\\.")[0]);
	        }
        }
        List<T> list = new ArrayList<T>();        
        for (String mdName : modelNameSet) {
        	list.add(getModel(modelClass,mdName));
        } 
        return list;
    }

	/**
	 * 批量获取数据转为javaBean
	  * 必须符合beanName[0...n].attrName格式的
	 * @param <T>
	 * @param beanClass
	 * @param beanName
	 * @return
	 */
	public <T> List<T> getBeans(Class<T> beanClass,String beanName){
        Pattern p = Pattern.compile(beanName + "\\[\\d+\\].[a-zA-z0-9]+");       
        Map<String, String[]> paraMap = getParaMap();      
        String paraKey;        
        Set<String> beanNameSet = new HashSet<String>();        
        for (Entry<String, String[]> e : paraMap.entrySet()) {
	        paraKey = e.getKey();           
	        if(p.matcher(paraKey).find()){
	        	beanNameSet.add(paraKey.split("\\.")[0]);
	        }
        }
        List<T> list = new ArrayList<T>();        
        for (String bName : beanNameSet) {
        	list.add(getBean(beanClass,bName));
        } 
        return list;
    }
	/**
	 * 渲染字符串byte 默认PDF 在线查看
	 * @param data
	 */
	public void renderByteToPDF(String data) {
		render(ByteRender.create(data.toString().getBytes()));
	}
	/**
	 * 渲染bytes 默认PDF 在线查看
	 * @param bytes
	 */
	public void renderByteToPDF(byte[] bytes) {
		render(ByteRender.create(bytes));
	}
	/**
	 * 渲染bytes 为图片数据流 在线查看
	 * @param bytes
	 */
	public void renderByteToImage(byte[] bytes) {
		render(ByteRender.create(bytes).setFileType(ByteFileType.JPG));
	}
	/**
	 * 渲染bytes 为图片数据流 下载文件
	 * @param bytes
	 * @param fileName
	 */
	public void renderByteToImageFile(byte[] bytes,String fileName) {
		render(ByteRender.create(bytes).setFileType(ByteFileType.JPG).setFileName(fileName).setRenderType(ByteRenderType.DOWNLOAD));
	}
	/**
	 * 渲染bytes 为PDF数据流 下载文件
	 * @param bytes
	 * @param fileName
	 */
	public void renderByteToPDFFile(byte[] bytes,String fileName) {
		render(ByteRender.create(bytes).setFileName(fileName).setRenderType(ByteRenderType.DOWNLOAD));
	}
	
	
}
