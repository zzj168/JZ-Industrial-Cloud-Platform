package cn.jbolt.common.util;
/**
* @author zunhui qq:1442182361
* @version 2019年12月30日
* 返回正确和错误的数据
*/
import java.util.List;

public class ExcelBean<T>{

	private List<T> correctList;
	private List<Integer> errorList;
	
	public List<T> getCorrectList() {
		return correctList;
	}
	public void setCorrectList(List<T> correctList) {
		this.correctList = correctList;
	}
	public List<Integer> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<Integer> errorList) {
		this.errorList = errorList;
	}
	public ExcelBean() {
	}
}
