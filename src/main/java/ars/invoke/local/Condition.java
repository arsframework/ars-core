package ars.invoke.local;

import java.util.regex.Pattern;

import ars.invoke.local.ParamAdapter;

/**
 * 参数条件对象
 * 
 * @author yongqiangwu
 * 
 */
public class Condition {
	private Class<?> type; // 参数类型
	private String name; // 参数名称
	private String value; // 参数默认值
	private Pattern pattern; // 正则表达式匹配模式
	private boolean required; // 是否必须
	private ParamAdapter adapter; // 参数适配器

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public ParamAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(ParamAdapter adapter) {
		this.adapter = adapter;
	}

}
