package ars.file.query.condition;

import ars.file.Describe.Property;
import ars.file.query.condition.Condition;

/**
 * 大于和小于
 * 
 * @author wuyq
 *
 */
public class Between implements Condition {
	private Property property; // 比较属性
	private Object low; // 低值
	private Object high; // 高值

	public Between(Property property, Object low, Object high) {
		this.property = property;
		this.low = low;
		this.high = high;
	}

	public Property getProperty() {
		return property;
	}

	public Object getLow() {
		return low;
	}

	public Object getHigh() {
		return high;
	}

}
