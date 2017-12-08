package ars.file.query.condition;

import ars.file.Describe.Property;
import ars.file.query.condition.Condition;

/**
 * 大于
 * 
 * @author wuyq
 *
 */
public class Large implements Condition {
	private Property property; // 比较属性
	private Object value; // 比较值

	public Large(Property property, Object value) {
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
