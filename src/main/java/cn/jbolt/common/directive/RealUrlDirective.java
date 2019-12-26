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

import cn.jbolt.common.util.RealUrlUtil;
/**
 * 正确输出资源地址的指令
 * @ClassName:  RealUrlDirective   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年4月14日 下午10:08:45   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class RealUrlDirective extends Directive {
	
	private Expr defaultValueExpr;
	private Expr valueExpr;
	private int paraNum;
	
	public void setExprList(ExprList exprList) {
		this.paraNum = exprList.length();
		if (paraNum ==0) {
			throw new ParseException("Wrong number parameter of #realImage directive, one parameters allowed at most", location);
		}else if (paraNum == 1) {
				this.valueExpr  = exprList.getExpr(0);
			} else if(paraNum == 2){
				this.valueExpr  = exprList.getExpr(0);
				this.defaultValueExpr  = exprList.getExpr(1);
			}
	}
	
	public void exec(Env env, Scope scope, Writer writer) {
		if (paraNum == 0) {
			outputNothing(env, writer);
		} else{
			outputRealUrl(env, scope, writer);
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
	private void outputRealUrl(Env env,Scope scope, Writer writer) {
		if(this.valueExpr==null){
			if(this.defaultValueExpr==null){
				outputNothing(env, writer);
			}else{
				Object defaultValue=this.defaultValueExpr.eval(scope);
				if(defaultValue==null){
					outputNothing(env, writer);
				}else{
					try {
						writer.write(defaultValue.toString().trim());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
			return;
		}
		
		Object value=this.valueExpr.eval(scope);
		Object defaultValue=null;
		if(defaultValueExpr!=null){
			defaultValue=this.defaultValueExpr.eval(scope);
		}
			try {
				String real=getRealUrl(value, defaultValue);
				if(StrKit.isBlank(real)) {
					outputNothing(env, writer);
				}else {
					writer.write(real);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	
	protected String getRealUrl(Object value,Object defaultValue) {
		return RealUrlUtil.get(value, defaultValue);
	}
	
}
