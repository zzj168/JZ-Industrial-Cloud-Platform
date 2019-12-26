package cn.jbolt.common.directive;

import java.io.IOException;

import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.ParseException;
import com.jfinal.template.stat.Scope;

public class AjaxPortalDirective extends Directive {
	
	private Expr portalIdExpr;
	private Expr urlExpr;
	private Expr autoLoadExpr;
	private int paraNum;
	
	public void setExprList(ExprList exprList) {
		this.paraNum = exprList.length();
		if (paraNum > 3) {
			throw new ParseException("Wrong number parameter of #ajaxPortal directive, three parameters allowed at most", location);
		}
		
		if (paraNum == 1) {
			this.urlExpr  = exprList.getExpr(0);
		} else if (paraNum == 2) {
			this.urlExpr  = exprList.getExpr(0);
			this.portalIdExpr = exprList.getExpr(1);
		} else if (paraNum == 3) {
			this.urlExpr  = exprList.getExpr(0);
			this.portalIdExpr = exprList.getExpr(1);
			this.autoLoadExpr = exprList.getExpr(2);
		}
	}
	
	public void exec(Env env, Scope scope, Writer writer) {
		if (paraNum == 0) {
			outputNothing(env, writer);
		} else if (paraNum == 1) {
			outputNormalAjaxPortal(env, scope, writer);
		} else if (paraNum == 2) {
			outputNormalAjaxPortalWithPortalId(env, scope, writer);
		} else if (paraNum == 3) {
			outputFullAjaxPortal(env, scope, writer);
		}
	}
	/**
	 * 输出空字符
	 * @param env
	 * @param writer
	 */
	private void outputNothing(Env env, Writer writer) {
		
	}
	/**
	 * 输出自动加载的仅指定Url的ajaxPortal代码
	 * @param env
	 * @param scope
	 * @param writer
	 */
	private void outputNormalAjaxPortal(Env env,Scope scope, Writer writer) {
		Object value=this.urlExpr.eval(scope);
		if(value!=null){
			try {
				writer.write("<div data-ajaxportal data-url='");
				writer.write(value.toString());
				writer.write("'></div>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * 输出自动加载的带有portalId和url的ajaxPortal代码
	 * @param env
	 * @param scope
	 * @param writer
	 */
	private void outputNormalAjaxPortalWithPortalId(Env env,Scope scope, Writer writer) {
		Object url=this.urlExpr.eval(scope);
		Object portalId=this.portalIdExpr.eval(scope);
		if(url!=null&&portalId!=null){
			try {
				writer.write("<div data-ajaxportal data-url='");
				writer.write(url.toString());
				writer.write("' id='");
				writer.write(portalId.toString());
				writer.write("'></div>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * 输出带有portalId和url的ajaxPortal代码
	 * 可以设置是否自动加载
	 * @param env
	 * @param scope
	 * @param writer
	 */
	private void outputFullAjaxPortal(Env env,Scope scope, Writer writer) {
		Object url=this.urlExpr.eval(scope);
		Object portalId=this.portalIdExpr.eval(scope);
		Object autoload=this.autoLoadExpr.eval(scope);
		if(url!=null&&portalId!=null&&autoload!=null){
			try {
				writer.write("<div data-ajaxportal data-autoload='");
				writer.write(autoload.toString());
				writer.write("' data-url='");
				writer.write(url.toString());
				writer.write("' id='");
				writer.write(portalId.toString());
				writer.write("'></div>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
}
