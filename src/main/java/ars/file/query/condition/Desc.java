package ars.file.query.condition;

import ars.file.Describe.Property;
import ars.file.query.condition.Order;

/**
 * 降序排序条件
 * 
 * @author wuyq
 *
 */
public class Desc extends Order {

	public Desc(Property property) {
		super(property);
	}

}
