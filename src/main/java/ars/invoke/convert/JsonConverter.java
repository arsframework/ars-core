package ars.invoke.convert;

import ars.util.Jsons;
import ars.invoke.convert.Converter;

/**
 * 基于json格式的对象数据转换实现
 * 
 * @author wuyq
 *
 */
public class JsonConverter implements Converter {

	@Override
	public String serialize(Object object) {
		return Jsons.format(object);
	}

	@Override
	public Object deserialize(String string) {
		return Jsons.parse(string);
	}

}
