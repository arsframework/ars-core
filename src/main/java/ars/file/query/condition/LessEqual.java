package ars.file.query.condition;

import ars.file.Describe.Property;
import ars.file.query.condition.Condition;

/**
 * 小于或等于
 * 
 * @author yongqiangwu
 *
 */
public class LessEqual implements Condition {
	private Property property; // 比较属性
	private Object value; // 比较值

	public LessEqual(Property property, Object value) {
		if (property == null) {
			throw new IllegalArgumentException("Illegal property:" + property);
		}
		if (value == null) {
			throw new IllegalArgumentException("Illegal value:" + value);
		}
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
