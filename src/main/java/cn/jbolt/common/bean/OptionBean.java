package cn.jbolt.common.bean;
/**
 * 下拉 checkbox radio的数据源Bean
 * @ClassName:  OptionBean   
 * @author: JFinal学院-小木 QQ：909854136 
 * @date:   2019年10月22日   
 *    
 * 注意：本内容仅限于JFinal学院 JBolt平台VIP成员内部传阅，请尊重开发者劳动成果，不要外泄出去用于其它商业目的
 */
public class OptionBean implements Option {
	private String text;
	private Object value;
	private Boolean selected;
	public OptionBean(){}
	public OptionBean(String text,Object value){
		this.text=text;
		this.value=value;
	}
	public OptionBean(String text,Object value,Boolean selected){
		this.text=text;
		this.value=value;
		this.selected=selected;
	}
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

}
