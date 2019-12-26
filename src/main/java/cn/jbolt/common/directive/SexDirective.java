package cn.jbolt.common.directive;

import java.io.IOException;

import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.expr.ast.Expr;
import com.jfinal.template.expr.ast.ExprList;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.ParseException;
import com.jfinal.template.stat.Scope;
/**
  * 用户性别转换指令
 * @ClassName:  WechatUserSexDirective   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年10月8日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class SexDirective extends Directive {
	
	private Expr valueExpr;
	private int paraNum;
	
	public void setExprList(ExprList exprList) {
		this.paraNum = exprList.length();
		if (paraNum ==0) {
			throw new ParseException("Wrong number parameter of #sex directive, one parameters allowed at most", location);
		}else
		
			if (paraNum == 1) {
				this.valueExpr  = exprList.getExpr(0);
			} 
	}
	
	public void exec(Env env, Scope scope, Writer writer) {
		if (paraNum == 0) {
			outputNothing(env, writer);
		} else if (paraNum == 1) {
			outputValue(env, scope, writer);
		}
	}
	/**
	 * 输出空字符
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
	/**
	 * 输出处理后的value值
	 * @param env
	 * @param scope
	 * @param writer
	 */
	private void outputValue(Env env,Scope scope, Writer writer) {
		Object value=this.valueExpr.eval(scope);
		if(value!=null&&value.toString().length()>0){
				String sexValue=value.toString();
				String name="参数异常";
				switch (sexValue) {
				case "1":
					name= "男";
					break;
				case "2":
					name= "女";
					break;
				case "0":
					name= "未知";
					break;
				}
				try {
					writer.write(name);
				} catch (IOException e) {
					e.printStackTrace();
				}
			 
		}
		
	}
	
	
	
	
}
