package cn.jbolt.index;

import com.jfinal.config.Routes;

import cn.jbolt._admin.demo.AjaxbtnDemoController;
import cn.jbolt._admin.demo.AutoSelectDemoController;
import cn.jbolt._admin.demo.AutocompleteDemoController;
import cn.jbolt._admin.demo.CheckboxDemoController;
import cn.jbolt._admin.demo.CurdWithPageDemoController;
import cn.jbolt._admin.demo.CurdWithoutPageDemoController;
import cn.jbolt._admin.demo.DemoController;
import cn.jbolt._admin.demo.DialogbtnDemoController;
import cn.jbolt._admin.demo.FileUploaderDemoController;
import cn.jbolt._admin.demo.FormCheckDemoController;
import cn.jbolt._admin.demo.Html5dateDemoController;
import cn.jbolt._admin.demo.HtmlEditorDemoController;
import cn.jbolt._admin.demo.ImgUploaderDemoController;
import cn.jbolt._admin.demo.ImgviewerDemoController;
import cn.jbolt._admin.demo.JboltLayerDemoController;
import cn.jbolt._admin.demo.LaydateDemoController;
import cn.jbolt._admin.demo.MasterSlaveDemoController;
import cn.jbolt._admin.demo.MultipleFileSyncUploaderDemoController;
import cn.jbolt._admin.demo.MultipleFileUploaderDemoController;
import cn.jbolt._admin.demo.PhotoBtnDemoController;
import cn.jbolt._admin.demo.RadioDemoController;
import cn.jbolt._admin.demo.SwitchbtnDemoController;
import cn.jbolt._admin.demo.TabTriggerDemoController;
import cn.jbolt._admin.demo.WebcamDemoController;
import cn.jbolt._admin.interceptor.AdminAuthInterceptor;
/**
 * demo测试路由配置
 * @ClassName:  DemoRoutes   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年3月26日 下午12:25:32   
 *     
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class DemoRoutes extends Routes {

	@Override
	public void config() {
		this.setBaseViewPath("/_view/_admin/demo");
		this.addInterceptor(new PjaxInterceptor());
		this.addInterceptor(new AjaxPortalInterceptor());
		this.addInterceptor(new AdminAuthInterceptor());
		this.add("/demo", DemoController.class,"/");
		this.add("/demo/autoselect", AutoSelectDemoController.class,"/");
		this.add("/demo/autocomplete", AutocompleteDemoController.class,"/");
		this.add("/demo/photobtn", PhotoBtnDemoController.class,"/");
		this.add("/demo/imgviewer", ImgviewerDemoController.class,"/");
		this.add("/demo/radio", RadioDemoController.class,"/");
		this.add("/demo/checkbox", CheckboxDemoController.class,"/");
		this.add("/demo/switchbtn", SwitchbtnDemoController.class,"/");
		this.add("/demo/ajaxbtn", AjaxbtnDemoController.class,"/");
		this.add("/demo/tabtrigger", TabTriggerDemoController.class,"/");
		this.add("/demo/jboltlayer", JboltLayerDemoController.class,"/");
		this.add("/demo/dialogbtn", DialogbtnDemoController.class,"/");
		this.add("/demo/htmleditor", HtmlEditorDemoController.class,"/");
		this.add("/demo/laydate", LaydateDemoController.class,"/");
		this.add("/demo/html5date", Html5dateDemoController.class,"/");
		this.add("/demo/masterslave", MasterSlaveDemoController.class,"/masterslave");
		this.add("/demo/webcam", WebcamDemoController.class,"/");
		this.add("/demo/imguploader", ImgUploaderDemoController.class,"/");
		this.add("/demo/fileuploader", FileUploaderDemoController.class,"/");
		this.add("/demo/multiplefileuploader", MultipleFileUploaderDemoController.class,"/");
		this.add("/demo/multiplefilesyncuploader", MultipleFileSyncUploaderDemoController.class,"/");
		this.add("/demo/formcheck", FormCheckDemoController.class,"/");
		this.add("/demo/curdwithpage", CurdWithPageDemoController.class,"/curdwithpage");
		this.add("/demo/curdwithoutpage", CurdWithoutPageDemoController.class,"/curdwithoutpage");
	}

}
