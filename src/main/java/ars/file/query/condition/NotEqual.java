package ars.file.query.condition;

import ars.file.Describe.Property;
import ars.file.query.condition.Condition;

/**
 * 不等于
 * 
 * @author yongqiangwu
 * 
 */
public class NotEqual implements Condition {
	private Property property; // 比较属性
	private Object value; // 比较值

	public NotEqual(Property property, Object value) {
		this.property = property;
		this.value = value;
	}

	public Property getProperty() {
		return property;
	}

	public Object getValue() {
		return value;
	}

}
