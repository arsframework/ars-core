package ars.file.query.condition;

import ars.file.Describe.Property;
import ars.file.query.condition.Condition;

/**
 * 排序条件
 * 
 * @author wuyq
 *
 */
public class Order implements Condition {
	private Property property; // 排序属性

	public Order(Property property) {
		this.property = property;
	}

	public Property getProperty() {
		return property;
	}

}
