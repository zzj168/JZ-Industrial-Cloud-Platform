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

import cn.jbolt.common.config.GlobalConfigKey;
import cn.jbolt.common.model.GlobalConfig;
import cn.jbolt.common.util.CACHE;
import cn.jbolt.common.util.RealUrlUtil;
/**
 * 全局配置 页面专用模板指令
 * @ClassName:  GlobalConfigDirective   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年5月16日14:07:46 
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class GlobalConfigDirective extends Directive {
	
	private Expr defaultValueExpr;
	private Expr configKey;
	private int paraNum;
	
	public void setExprList(ExprList exprList) {
		this.paraNum = exprList.length();
		if (paraNum ==0) {
			throw new ParseException("Wrong number parameter of #globalConfig directive, one parameters allowed at most", location);
		}else if (paraNum == 1) {
				this.configKey  = exprList.getExpr(0);
			} else if(paraNum == 2){
				this.configKey  = exprList.getExpr(0);
				this.defaultValueExpr  = exprList.getExpr(1);
			}
	}
	
	public void exec(Env env, Scope scope, Writer writer) {
		if (paraNum == 0) {
			outputNothing(env, writer);
		} else{
			outputConfigValue(env, scope, writer);
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
	private void outputConfigValue(Env env,Scope scope, Writer writer) {
		if(this.configKey==null){
			throw new ParseException("Wrong first parameter of #globalConfig directive", location);
		}
		Object configKeyValue=this.configKey.eval(scope);
		if(configKeyValue==null){
			throw new ParseException("Wrong first parameter of #globalConfig directive", location);
		}
		Object defaultValue=null;
		if(defaultValueExpr!=null){
			defaultValue=this.defaultValueExpr.eval(scope);
		}
			try {
				GlobalConfig config=CACHE.me.getGlobalConfig(configKeyValue.toString().trim());
				if(config==null){
					if(defaultValue==null){
						outputNothing(env, writer);
					}else{
						try {
							writer.write(defaultValue.toString().trim());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else{
					String configValue=config.getConfigValue();
					if(StrKit.isBlank(configValue)){
						
						if(defaultValue==null){
							outputNothing(env, writer);
						}else{
							try {
								writer.write(defaultValue.toString().trim());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}else{
						if(configKeyValue.equals(GlobalConfigKey.SYSTEM_ADMIN_LOGO)){
							writer.write(RealUrlUtil.getImage(configValue, defaultValue));
						}else{
							writer.write(configValue);
						}
					}
				}
						
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	
	
	
}
