package ars.util;

import java.util.Map;
import java.util.HashMap;

import ars.util.AbstractTree;

/**
 * 树简单实现
 * 
 * @author yongqiangwu
 * 
 */
public class SimpleTree extends AbstractTree<SimpleTree> {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private Map<String, Object> attributes;

	public SimpleTree(String id) {
		this(id, id);
	}

	public SimpleTree(String id, String name) {
		this(id, name, new HashMap<String, Object>(0));
	}

	public SimpleTree(String id, String name, Map<String, Object> attributes) {
		if (id == null) {
			throw new IllegalArgumentException("Illegal id:" + id);
		}
		if (name == null) {
			throw new IllegalArgumentException("Illegal name:" + name);
		}
		this.id = id;
		this.name = name;
		this.attributes = attributes;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Map<String, Object> format() {
		Map<String, Object> values = super.format();
		values.put("id", this.id);
		values.put("name", this.name);
		values.put("attributes", this.attributes);
		return values;
	}

	@Override
	public int hashCode() {
		return 31 + this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj != null && obj instanceof SimpleTree && this.id.equals(((SimpleTree) obj).getId()));
	}

	@Override
	public String toString() {
		return this.name;
	}

}
