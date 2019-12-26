package cn.jbolt.common.bean;
/**
 * jstree封装
 * @author mmm
 *
 */
public class JsTreeBean {
	private String id;
	private String parent;
	private String text;
	private JsTreeStateBean state;
	private String type;
	public JsTreeBean(Integer id,Integer pid,String text) {
		init(id, pid, text,null);
		this.state=new JsTreeStateBean();
	}
	public JsTreeBean(Integer id,Integer pid,String text,String type) {
		init(id, pid, text,type);
		this.state=new JsTreeStateBean();
	}
	private void init(Integer id, Integer pid, String text,String type) {
		this.id=id+"";
		if((id==null||id==0)&&(pid==null||pid==0)){
			this.parent="#";
		}else {
			this.parent=pid+"";
		}
		this.text=text;
		if(type!=null){
			this.type=type;
		}else{
			this.type="default";
		}
	}
	public JsTreeBean(Integer id,Integer pid,String text,boolean opened,String type) {
		init(id, pid, text,type);
		this.state=new JsTreeStateBean(opened);
	}
	public JsTreeBean(Integer id,Integer pid,String text,boolean opened,boolean selected,String type) {
		init(id, pid, text,type);
		this.state=new JsTreeStateBean(opened,selected);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public JsTreeStateBean getState() {
		return state;
	}
	public void setState(JsTreeStateBean state) {
		this.state = state;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
