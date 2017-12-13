package ars.util;

import java.util.Map;

/**
 * 对象可格式化接口
 * 
 * @author yongqiangwu
 * 
 */
public interface Formable {
	/**
	 * 对象键/值对格式化
	 * 
	 * @return 键/值对
	 */
	public Map<String, Object> format();

}
