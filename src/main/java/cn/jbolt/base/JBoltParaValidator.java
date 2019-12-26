package cn.jbolt.base;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

import cn.hutool.core.io.FileTypeUtil;
import cn.jbolt.common.util.ArrayUtil;
import cn.jbolt.common.util.ImageType;

public class JBoltParaValidator{
	/**
	 * 判断Integer参数有效性
	 * @param param
	 */
	public static boolean isOk(Integer param){
		return param!=null&&param>0;
	}
	/**
	 * 判断Integer参数是否无效
	 */
	public static boolean notOk(Integer param){
		return param==null||param<=0;
	}
	/**
	 * 判断List参数有效性
	 * @param param
	 */
	public static boolean isOk(List param){
		return param!=null&&param.size()>0;
	}
	/**
	 * 判断List参数是否无效
	 * @param param
	 */
	public static boolean notOk(List param){
		return param==null||param.size()==0;
	}
	/**
	 * 判断上传文件类型不是图片
	 * @param file
	 */
	public static boolean notImage(UploadFile file){
		if(file==null) {return true;}
		return notImage(file.getContentType())||ImageType.notImage(FileTypeUtil.getType(file.getFile()));
	}
	/**
	 * 判断上传文件类型是否为图片
	 * @param file
	 */
	public static boolean isImage(UploadFile file){
		if(file==null) {return false;}
		return isImage(file.getContentType())&&ImageType.isImage(FileTypeUtil.getType(file.getFile()));
	}
	/**
	 * 判断String参数是否有效
	 * @param param
	 */
	public static boolean isOk(String param){
		return StrKit.notBlank(param);
	}
	/**
	 * 判断String参数无效
	 * @param param
	 */
	public static boolean notOk(String param){
		return StrKit.isBlank(param);
	}
	/**
	 * 判断Double参数是否有效
	 * @param param
	 */
	public static boolean isOk(Double param){
		return param!=null&&param>0;
	}
	/**
	 * 判断Double参数无效
	 * @param param
	 */
	public static boolean notOk(Double param){
		return param==null||param<=0;
	}
	/**
	 * 判断Float参数是否有效
	 * @param param
	 */
	public static boolean isOk(Float param){
		return param!=null&&param>0;
	}
	/**
	 * 判断Float参数无效
	 * @param param
	 */
	public static boolean notOk(Float param){
		return param==null||param<=0;
	}
	
	/**
	 * 判断Long参数是否有效
	 * @param param
	 */
	public static boolean isOk(Long param){
		return param!=null&&param>0;
	}
	/**
	 * 判断Long参数无效
	 * @param param
	 */
	public static boolean notOk(Long param){
		return param==null||param<=0;
	}
	
	
	/**
	 * 判断BigDecimal参数是否有效
	 * @param param
	 */
	public static boolean isOk(BigDecimal param){
		return param!=null&&param.doubleValue()>0;
	}
	/**
	 * 判断BigDecimal参数无效
	 * @param param
	 */
	public static boolean notOk(BigDecimal param){
		return param==null||param.doubleValue()<=0;
	}
	/**
	 * 判断上传文件是图片
	 * @param isImage
	 */
	public static boolean isImage(String contentType){
		return contentType!=null&&contentType.indexOf("image/")!=-1;
	}
	/**
	 * 判断上传文件不是图片
	 * @param notImage
	 */
	public static boolean notImage(String contentType){
		return contentType==null||contentType.indexOf("image/")==-1;
	}
	
	/**
	 * 判断Serializable[]数组类型数据是否正确
	 * @param param
	 * @return
	 */
	public static boolean isOk(Object[] param){
		return ArrayUtil.notEmpty(param);
	}
	/**
	 * 判断Serializable[]数组类型数据不正确
	 * @param param
	 * @return
	 */
	public static boolean notOk(Object[] param){
		return ArrayUtil.isEmpty(param);
	}
	/**
	 * 判断Date类型数据是否正确
	 * @param param
	 * @return
	 */
	public static boolean isOk(Date param){
		return param!=null;
	}

	
	
	/**
	 * 判断Date类型数据不正确
	 * @param param
	 * @return
	 */
	public static boolean notOk(Date param){
		return param==null;
	}
	/**
	 * 判断Boolean类型数据不正确
	 * @param param
	 * @return
	 */
	public static boolean notOk(Boolean param){
		return param==null;
	}
	/**
	 * 判断Boolean类型数据是否正确
	 * @param param
	 * @return
	 */
	public static boolean isOk(Boolean param){
		return param!=null;
	}
	/**
	 * 判断Map类型数据不正确
	 * @param param
	 * @return
	 */
	public static boolean notOk(Map param){
		return param==null||param.isEmpty();
	}
	/**
	 * 判断Map类型数据是否正确
	 * @param param
	 * @return
	 */
	public static boolean isOk(Map param){
		return param!=null&&!param.isEmpty();
	}
	/**
	 * 判断Set类型数据不正确
	 * @param param
	 * @return
	 */
	public static boolean notOk(Set param){
		return param==null||param.isEmpty();
	}
	/**
	 * 判断Set类型数据是否正确
	 * @param param
	 * @return
	 */
	public static boolean isOk(Set param){
		return param!=null&&!param.isEmpty();
	}
	
	/**
	 * 判断参数数据不正确
	 * @param param
	 * @return
	 */
	public static boolean notOk(Object param){
		if(param==null) {return true;}
		String name=param.getClass().getSimpleName();
		boolean success=false;
		switch (name) {
			case "Integer":
				success=notOk((Integer)param);
				break;
			case "String":
				success=notOk(param.toString());
				break;
			case "Long":
				success=notOk((Long)param);
				break;
			case "Double":
				success=notOk((Double)param);
				break;
			case "Float":
				success=notOk((Float)param);
				break;
			case "BigDecimal":
				success=notOk((BigDecimal)param);
				break;
			case "List":
				success=notOk((List)param);
				break;
			case "ArrayList":
				success=notOk((ArrayList)param);
				break;
			case "Boolean":
				success=notOk((Boolean)param);
				break;
			case "Date":
				success=notOk((Date)param);
				break;
			case "Map":
				success=notOk((Map)param);
				break;
			case "HashMap":
				success=notOk((HashMap)param);
				break;
			case "Set":
				success=notOk((Set)param);
				break;
			case "HashSet":
				success=notOk((HashSet)param);
				break;
		}
		return success;
	}
	/**
	 * 判断参数数据是否正确
	 * @param param
	 * @return
	 */
	public static boolean isOk(Object param){
		if(param==null) {return false;}
		String name=param.getClass().getSimpleName();
		boolean success=false;
		switch (name) {
			case "Integer":
				success=isOk((Integer)param);
				break;
			case "String":
				success=isOk(param.toString());
				break;
			case "Long":
				success=isOk((Long)param);
				break;
			case "Double":
				success=isOk((Double)param);
				break;
			case "Float":
				success=isOk((Float)param);
				break;
			case "BigDecimal":
				success=isOk((BigDecimal)param);
				break;
			case "List":
				success=isOk((List)param);
				break;
			case "ArrayList":
				success=isOk((ArrayList)param);
				break;
			case "Boolean":
				success=isOk((Boolean)param);
				break;
			case "Date":
				success=isOk((Date)param);
				break;
			case "Map":
				success=isOk((Map)param);
				break;
			case "HashMap":
				success=isOk((HashMap)param);
				break;
			case "Set":
				success=isOk((Set)param);
				break;
			case "HashSet":
				success=isOk((HashSet)param);
				break;
		}
		return success;
	}
	
	
	
}
