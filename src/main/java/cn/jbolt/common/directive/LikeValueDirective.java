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

public class LikeValueDirective extends Directive {
	
	private Expr valueExpr;
	private int paraNum;
	
	public void setExprList(ExprList exprList) {
		this.paraNum = exprList.length();
		if (paraNum ==0) {
			throw new ParseException("Wrong number parameter of #likeValue directive, one parameters allowed at most", location);
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
		String res=SqlUtil.likeValue(this.valueExpr.eval(scope));
		if(StrKit.notBlank(res)) {
			try {
				writer.write(res);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
		
}
