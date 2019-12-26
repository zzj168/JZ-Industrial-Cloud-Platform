package cn.jbolt.common.config;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.core.converter.IConverter;
import com.jfinal.kit.StrKit;

import cn.jbolt.common.util.DateUtil;
/**
 * JBolt中处理timestamp的converter
 * 特殊处理前端原生Html5的控件 input type="datetime"
 * @ClassName:  JBoltTimestampConverter   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月25日 下午9:28:38   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class JBoltTimestampConverter implements IConverter<Timestamp> {
	private static final String datePattern = "yyyy-MM-dd";
	private static final int dateLen = datePattern.length();
	private static final int timeStampWithoutSecPatternLen = "yyyy-MM-dd HH:mm".length();
	// mysql type: timestamp, datetime
	@Override
	public java.sql.Timestamp convert(String s) throws ParseException {
		return doConvert(s);
	}
	
	public static java.sql.Timestamp doConvert(String s) throws ParseException{
		if(StrKit.isBlank(s)){return  null;}
		s=doConvertString(s);
		if (s.length() > dateLen) {
			return java.sql.Timestamp.valueOf(s);
		}
		else {
			return new java.sql.Timestamp(new SimpleDateFormat(datePattern).parse(s).getTime());
		}
	}

	public static String doConvertString(String s) {
		if(StrKit.isBlank(s)){return  null;}
		if(s.indexOf(" ")==-1&&s.indexOf("-")!=-1&&s.indexOf(":")!=-1&&s.indexOf("T")!=-1){
			s=s.replace("T", " ");
		}
		if (timeStampWithoutSecPatternLen == s.length()) {
			s = s + ":00";
		}
		return s;
	}
	public static String doConvertShowString(String s) {
		if(StrKit.isBlank(s)){return  null;}
		if(s.indexOf(" ")==-1&&s.indexOf("-")!=-1&&s.indexOf(":")!=-1&&s.indexOf("T")!=-1){
			s=s.replace("T", " ");
		}
		return s;
	}
	public static String doConvertInputString(String s) {
		if(StrKit.isBlank(s)){return  null;}
		if(s.indexOf(" ")!=-1&&s.indexOf("-")!=-1&&s.indexOf(":")!=-1&&s.indexOf("T")==-1){
			s=s.replace(" ", "T");
		}
		return s;
	}
	public static String doConvertWithT(String s) {
		if(StrKit.isBlank(s)){return  null;}
		if(s.indexOf("-")!=-1&&s.indexOf(":")!=-1&&s.indexOf(" ")!=-1){
			s=s.replace(" ", "T");
		}
		return s;
	}
	public static Date doConvertDate(String s) {
		if(StrKit.isBlank(s)){return  null;}
		s=doConvertString(s);
		return DateUtil.getDate(s, DateUtil.YMD);
	}
	public static Date doConvertDateTime(String s) {
		if(StrKit.isBlank(s)){return  null;}
		s=doConvertString(s);
		return DateUtil.getDate(s, DateUtil.YMDHMS);
	}
}