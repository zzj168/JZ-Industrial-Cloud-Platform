package cn.jbolt.common.directive;

import java.io.IOException;

import com.jfinal.kit.StrKit;
import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.ParseException;
import com.jfinal.template.stat.Scope;

import cn.jbolt.common.db.sql.SqlUtil;

public class SqlValueDirective extends Directive {
	
	private Expr valueExpr;
	private int paraNum;
	
	public void setExprList(ExprList exprList) {
		this.paraNum = exprList.length();
		if (paraNum ==0) {
			throw new ParseException("Wrong number parameter of #sqlValue directive, one parameters allowed at most", location);
		}else
		
			if (paraNum == 1) {
				this.valueExpr  = exprList.getExpr(0);
			} 
	}
	
	public void exec(Env env, Scope scope, Writer writer) {
		if (paraNum == 0) {
			outputNothing(env, writer);
		} else if (paraNum == 1) {
			outputSqlValue(env, scope, writer);
		}
	}
	/**
	 * 输出空字符
	 * @param env
	 * @param writer
	 */
	private void outputNothing(Env env, Writer writer) {
		try {
			writer.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 输出处理后的value值
	 * @param env
	 * @param scope
	 * @param writer
	 */
	private void outputSqlValue(Env env,Scope scope, Writer writer) {
		Object value=this.valueExpr.eval(scope);
		try {
			String res=SqlUtil.sqlValue(value);
			if(StrKit.notBlank(res)) {
				writer.write(res);
			}
			/*
			 * if(value==null){
			 * 
			 * return; } String sqlValue=value.toString().trim();
			 * if(StrKit.isBlank(sqlValue)) { writer.write("NULL"); return; } if(value
			 * instanceof String) { if(sqlValue.indexOf("'")!=-1) {
			 * sqlValue=sqlValue.replace("'", "''"); } if(sqlValue.indexOf("like")!=-1) {
			 * if(sqlValue.indexOf(" or ")==-1&&sqlValue.indexOf("%")==-1) {
			 * writer.write(sqlValue); }else { writer.write(" like ''"); } return; }
			 * 
			 * 
			 * 
			 * //如果是特殊is not判断 直接输出
			 * if(sqlValue.indexOf("is ")!=-1||sqlValue.indexOf(" not ")!=-1||sqlValue.
			 * indexOf(" in(")!=-1||sqlValue.indexOf(" in (")!=-1){ writer.write(sqlValue);
			 * }else { writer.write("'"); writer.write(sqlValue); writer.write("'"); }
			 * 
			 * }else if(value instanceof Boolean) { if(sqlValue.equalsIgnoreCase("true")){
			 * writer.write("'1'"); }else if(sqlValue.equalsIgnoreCase("false")){
			 * writer.write("'0'"); } }else if(value instanceof Number) {
			 * writer.write(sqlValue); }else { writer.write("'"); writer.write(sqlValue);
			 * writer.write("'"); }
			 */
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
		
}
