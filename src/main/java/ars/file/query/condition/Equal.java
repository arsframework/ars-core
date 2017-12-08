package ars.file.query.condition;

import ars.file.Describe.Property;
import ars.file.query.condition.Condition;

/**
 * 等于条件
 * 
 * @author wuyq
 * 
 */
public class Equal implements Condition {
	private Property property; // 比较属性
	private Object value; // 比较值

	public Equal(Property property, Object value) {
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
