package cn.jbolt.common.directive;

import java.io.IOException;
import java.util.Date;

import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.ParseException;
import com.jfinal.template.stat.Scope;

import cn.jbolt.common.util.DateUtil;

/**
 * 专门处理时间转换 HTML5原生时间格式的指令
 * 
 * @ClassName: DateTimeDirective
 * @author: JFinal学院-小木 QQ：909854136
 * @date: 2019年10月8日
 * 
 *        注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class DateTimeDirective extends Directive {

	private Expr valueExpr;
	private Expr datePatternExpr;
	private boolean defaultToday;
	private boolean withT;
	private int paraNum;

	public void setExprList(ExprList exprList) {
		this.paraNum = exprList.length();
		if (paraNum > 3) {
			throw new ParseException("Wrong number parameter of #datetime directive, three parameters allowed at most",
					location);
		}

		if (paraNum == 0) {
			this.valueExpr = null;
			this.datePatternExpr = null;
		} else if (paraNum == 1) {
			this.valueExpr = exprList.getExpr(0);
			this.datePatternExpr = null;
		} else if (paraNum == 2) {
			this.valueExpr = exprList.getExpr(0);
			this.datePatternExpr = exprList.getExpr(1);
		} else if (paraNum == 3) {
			this.valueExpr = exprList.getExpr(0);
			this.datePatternExpr = exprList.getExpr(1);
			this.defaultToday = true;
		}
		this.withT=false;
	}

	public void exec(Env env, Scope scope, Writer writer) {
		if (paraNum == 1) {
			outputWithoutDatePattern(env, scope, writer);
		} else if (paraNum == 2) {
			outputWithDatePattern(env, scope, writer);
		} else if (paraNum == 3) {
			outputWithDatePattern(env, scope, writer);
		} else {
			outputToday(env, writer, scope);
		}
	}

	private void outputToday(Env env, Writer writer, Scope scope) {
		Date date = new Date();
		if (this.paraNum == 1) {
			write(writer, date, env.getEngineConfig().getDatePattern());
		} else if (this.paraNum == 2 || this.paraNum == 3) {
			String datePatternString = processDatePattern(scope);
			write(writer, date, datePatternString);
		}
	}

	private String processDatePattern(Scope scope) {
		Object datePattern = this.datePatternExpr.eval(scope);
		String datePatternString=null;
		if (datePattern instanceof String) {
			datePatternString = datePattern.toString();
			if(datePatternString.indexOf("T")!=-1) {
				this.withT=true;
				datePatternString=datePatternString.replaceAll("T", " ");
			}
		} else {
			throw new TemplateException("The sencond parameter datePattern of #datetime directive must be String",
					location);
		}
		return datePatternString;
	}

	/**
	 * 输出空字符
	 * 
	 * @param env
	 * @param writer
	 */
	private void outputNothing(Env env, Writer writer) {
		try {
			writer.write("''");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void outputWithoutDatePattern(Env env, Scope scope, Writer writer) {
		Object value = valueExpr.eval(scope);
		if (value == null || value.toString().trim().length() == 0) {
			outputNothing(env, writer);
		} else if (value instanceof Date) {
			write(writer, (Date) value, env.getEngineConfig().getDatePattern());
		} else {
			Date date = DateUtil.toDate(value.toString(), env.getEngineConfig().getDatePattern());
			if(date==null) {
				outputNothing(env, writer);
			}
			write(writer, date, env.getEngineConfig().getDatePattern());
			throw new TemplateException("The first parameter date of #datetime directive must be Date type", location);
		}
	}

	private void outputWithDatePattern(Env env, Scope scope, Writer writer) {
		Object value = valueExpr.eval(scope);
		if (value == null || value.toString().trim().length() == 0) {
			if (defaultToday) {
				outputToday(env, writer, scope);
			} else {
				outputNothing(env, writer);
			}
			return;
		}
		String datePatternString=processDatePattern(scope);
		
		if (value instanceof Date) {
			write(writer, (Date) value, datePatternString);			
		} else {
			Date date = DateUtil.toDate(value.toString(), datePatternString);
			if(date==null) {
				outputNothing(env, writer);
			}
			write(writer, date, datePatternString);
			throw new TemplateException("The first parameter date of #datetime directive must be Date type", location);
		}
	}

	 
	private void write(Writer writer, Date date, String datePattern) {
		try {
			if(withT) {
				writer.write(DateUtil.formatWithT(date, datePattern));
			}else {
				writer.write(DateUtil.format(date, datePattern));
			}
		} catch (IOException e) {
			throw new TemplateException(e.getMessage(), location, e);
		}
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * private Expr valueExpr; private int paraNum; private Expr withTExpr;
	 * 
	 * public void setExprList(ExprList exprList) { this.paraNum =
	 * exprList.length(); if (paraNum > 2) { throw new
	 * ParseException("Wrong number parameter of #datetime directive, two parameters allowed at most"
	 * , location); }
	 * 
	 * if (paraNum == 0) { this.valueExpr = null; } else if (paraNum == 1) {
	 * this.valueExpr = exprList.getExpr(0); } else if (paraNum == 2) {
	 * this.valueExpr = exprList.getExpr(0); this.withTExpr = exprList.getExpr(1); }
	 * }
	 * 
	 * public void exec(Env env, Scope scope, Writer writer) { if (paraNum == 1) {
	 * outputDatetime(env, scope, writer,false); }else if (paraNum == 2) {
	 * outputDatetime(env, scope, writer,true); } else { outputToday(env, writer); }
	 * }
	 * 
	 * private void outputToday(Env env, Writer writer) { try { writer.write( new
	 * Date(), env.getEngineConfig().getDatePattern()); } catch (IOException e) {
	 * throw new TemplateException(e.getMessage(), location, e); } }
	 * 
	 * private void write(Writer writer, Date date, String datePattern) { try {
	 * writer.write(date, datePattern); } catch (IOException e) { throw new
	 * TemplateException(e.getMessage(), location, e); } }
	 * 
	 * private void outputDatetime(Env env, Scope scope, Writer writer,boolean
	 * withT) { Object value = valueExpr.eval(scope); if(value!=null){ if(value
	 * instanceof Date) { write(writer, (Date)value, "yyyy-MM-dd"); return; } String
	 * datetime=null; if(withT){ Object withTValue = withTExpr.eval(scope);
	 * if(withTValue!=null&&withTValue.toString().trim().equals("true")){
	 * datetime=JBoltTimestampConverter.doConvertInputString(value.toString());
	 * }else{
	 * datetime=JBoltTimestampConverter.doConvertShowString(value.toString()); }
	 * }else{
	 * datetime=JBoltTimestampConverter.doConvertShowString(value.toString()); } try
	 * { writer.write(datetime==null?"":datetime); } catch (IOException e) { throw
	 * new TemplateException(e.getMessage(), location, e); } }
	 * 
	 * }
	 */
	

}
