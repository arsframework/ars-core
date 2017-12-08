package ars.invoke.channel.http.tags;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import ars.util.Strings;
import ars.invoke.Invokes;
import ars.invoke.channel.http.tags.AbstractTag;

/**
 * 资源标签
 * 
 * @author wuyq
 * 
 */
public class ResourceTag extends AbstractTag {
	private String api; // 接口地址
	private Object param; // 参数，支持键/值对形式或字符串形式。如果为字符串则多个条件使用“,”号隔开；如果参数值为多个值，则使用“[]”包围，并且每个值使用“,”号隔开。
	private Object condition; // 附件条件，参数格式同param；附加条件中的参数将覆盖param参数。
	private String remove; // 排除条件，多个条件之间使用“,”号隔开
	private Object entity; // 对象实体

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api.trim();
	}

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param instanceof String ? ((String) param).trim() : param;
	}

	public Object getCondition() {
		return condition;
	}

	public void setCondition(Object condition) {
		this.condition = condition instanceof String ? ((String) condition).trim() : condition;
	}

	public String getRemove() {
		return remove;
	}

	public void setRemove(String remove) {
		this.remove = remove.trim();
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity instanceof String ? ((String) entity).trim() : entity;
	}

	/**
	 * 获取请求参数
	 * 
	 * @return 参数键/值对
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> getParameters() {
		Map<String, Object> param = this.param instanceof Map ? (Map<String, Object>) this.param
				: this.param instanceof String ? Strings.toMap((String) this.param)
						: Collections.<String, Object>emptyMap();
		Map<String, Object> condition = this.condition instanceof Map ? (Map<String, Object>) this.condition
				: this.condition instanceof String ? Strings.toMap((String) this.condition)
						: Collections.<String, Object>emptyMap();
		if (param.isEmpty() && condition.isEmpty()) {
			return new HashMap<String, Object>(0);
		}
		Map<String, Object> parameters = new HashMap<String, Object>(param.size() + condition.size());
		if (!param.isEmpty()) {
			parameters.putAll(param);
		}
		if (!condition.isEmpty()) {
			parameters.putAll(condition);
		}
		if (this.remove != null && !parameters.isEmpty()) {
			for (String key : Strings.split(this.remove, ',')) {
				parameters.remove(key);
			}
		}
		return parameters;
	}

	@Override
	protected Object execute() throws Exception {
		if (this.entity != null) {
			return this.entity;
		} else if (!Strings.isEmpty(this.api)) {
			return Invokes.getCurrentRequester().execute(this.api, this.getParameters());
		}
		return null;
	}

}
