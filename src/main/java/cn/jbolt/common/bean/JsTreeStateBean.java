package cn.jbolt.common.bean;

public class JsTreeStateBean {
	private boolean opened;
	private boolean disabled;
	private boolean selected;
	public JsTreeStateBean() {
		this.opened=true;
		this.disabled=false;
		this.selected=false;
	}
	public JsTreeStateBean(boolean opened) {
		this.opened=opened;
		this.disabled=false;
		this.selected=false;
	}
	public JsTreeStateBean(boolean opened,boolean selected) {
		this.opened=opened;
		this.disabled=false;
		this.selected=selected;
	}
	public boolean isOpened() {
		return opened;
	}
	public void setOpened(boolean opened) {
		this.opened = opened;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
