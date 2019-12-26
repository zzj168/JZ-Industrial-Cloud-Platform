package cn.jbolt._admin.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 如果是SuperAdmin就不检测
 * @ClassName:  UnCheckIfSuperAdmin   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年10月28日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface UnCheckIfSystemAdmin {

}
