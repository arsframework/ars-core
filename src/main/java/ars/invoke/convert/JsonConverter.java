package ars.invoke.convert;

import ars.util.Jsons;
import ars.invoke.convert.Converter;

/**
 * 基于json格式的对象数据转换实现
 * 
 * @author yongqiangwu
 * 
 */
public class JsonConverter implements Converter {
	protected final int depth; // json转换对象属性下钻深度

	public JsonConverter() {
		this(2);
	}

	public JsonConverter(int depth) {
		this.depth = depth;
	}

	@Override
	public String serialize(Object object) {
		return Jsons.format(object, this.depth);
	}

	@Override
	public Object deserialize(String string) {
		return Jsons.parse(string);
	}

}
