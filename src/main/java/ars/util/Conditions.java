package ars.util;

import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;

import ars.util.Strings;

/**
 * 条件操作工具类
 * 
 * @author wuyq
 * 
 */
public final class Conditions {
	/**
	 * 查询逻辑接口
	 * 
	 * @author wuyq
	 * 
	 */
	public static interface Logic {

	}

	/**
	 * 或逻辑实现
	 * 
	 * @author wuyq
	 * 
	 */
	public static class Or implements Logic {
		private Logic[] logics;

		public Or(Logic... logics) {
			if (logics == null || logics.length == 0) {
				throw new IllegalArgumentException("Illegal logics:" + Strings.toString(logics));
			}
			this.logics = logics;
		}

		public Or(Map<String, Object> conditions) {
			if (conditions == null || conditions.isEmpty()) {
				throw new IllegalArgumentException("Illegal conditions:" + conditions);
			}
			List<Logic> logics = new ArrayList<Logic>(conditions.size());
			for (Entry<String, Object> entry : conditions.entrySet()) {
				logics.add(new Condition(entry.getKey(), entry.getValue()));
			}
			this.logics = logics.toArray(new Logic[0]);
		}

		public Logic[] getLogics() {
			return logics;
		}

		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			for (Logic logic : this.logics) {
				if (buffer.length() > 0) {
					buffer.append(" or ");
				}
				boolean isCondition = logic instanceof Condition;
				if (!isCondition) {
					buffer.append('(');
				}
				buffer.append(logic);
				if (!isCondition) {
					buffer.append(')');
				}
			}
			return buffer.toString();
		}

	}

	/**
	 * 与逻辑实现
	 * 
	 * @author wuyq
	 * 
	 */
	public static class And implements Logic {
		private Logic[] logics;

		public And(Logic... logics) {
			if (logics == null || logics.length == 0) {
				throw new IllegalArgumentException("Illegal logics:" + Strings.toString(logics));
			}
			this.logics = logics;
		}

		public And(Map<String, Object> conditions) {
			if (conditions == null || conditions.isEmpty()) {
				throw new IllegalArgumentException("Illegal conditions:" + conditions);
			}
			List<Logic> logics = new ArrayList<Logic>(conditions.size());
			for (Entry<String, Object> entry : conditions.entrySet()) {
				logics.add(new Condition(entry.getKey(), entry.getValue()));
			}
			this.logics = logics.toArray(new Logic[0]);
		}

		public Logic[] getLogics() {
			return logics;
		}

		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			for (Logic logic : this.logics) {
				if (buffer.length() > 0) {
					buffer.append(" and ");
				}
				boolean isCondition = logic instanceof Condition;
				if (!isCondition) {
					buffer.append('(');
				}
				buffer.append(logic);
				if (!isCondition) {
					buffer.append(')');
				}
			}
			return buffer.toString();
		}

	}

	/**
	 * 条件逻辑实现
	 * 
	 * @author wuyq
	 * 
	 */
	public static class Condition implements Logic {
		private String key;
		private Object value;

		public Condition(String key, Object value) {
			if (Strings.isEmpty(key)) {
				throw new IllegalArgumentException("Illegal key:" + key);
			}
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		@Override
		public String toString() {
			return new StringBuilder(this.key).append('=').append(this.value).toString();
		}

	}

	/**
	 * 条件表达式逻辑对象转换
	 * 
	 * @param source
	 *            条件表达式
	 * @return 条件逻辑对象
	 */
	public static Logic parse(String source) {
		if (Strings.isEmpty(source)) {
			return null;
		}
		boolean continued = false;
		int offset = 0, start = 0, end = 0;
		List<String> setions = new ArrayList<String>();
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (c == '(') {
				if (start == end) {
					offset = i;
				}
				start++;
			} else if (c == ')') {
				if (start == ++end) {
					setions.add(source.substring(offset, i + 1));
					start = 0;
					end = 0;
					offset = i + 1;
					continued = true;
				}
			} else if (start == end) {
				int index = 0;
				String handle = null;
				if ((i > 3 && (handle = source.substring(index = i - 3, i + 1)).equalsIgnoreCase(" or "))
						|| (i > 4 && (handle = source.substring(index = i - 4, i + 1)).equalsIgnoreCase(" and "))) {
					if (!continued) {
						setions.add(source.substring(offset, index));
					}
					offset = i + 1;
					continued = false;
					setions.add(handle.trim().toLowerCase());
				}
			}
		}
		if (start != end) {
			throw new IllegalArgumentException("Illegal source:" + source);
		}
		if (offset < source.length()) {
			setions.add(source.substring(offset));
		}
		Logic logic = null;
		for (int i = 0; i < setions.size(); i += 2) {
			Logic _logic = null;
			String setion = setions.get(i).trim();
			if (setion.isEmpty()) {
				continue;
			}
			if (setion.charAt(0) == '(' && setion.charAt(setion.length() - 1) == ')') {
				setion = setion.substring(1, setion.length() - 1).trim();
				if (setion.isEmpty()) {
					continue;
				}
				_logic = parse(setion);
			} else {
				int split = setion.indexOf("=");
				String key = split < 0 ? setion.trim() : setion.substring(0, split).trim();
				if (key.isEmpty()) {
					continue;
				}
				String value = split < 0 ? null : setion.substring(split + 1).trim();
				_logic = new Condition(key,
						Strings.isList(value) ? Strings.toList(value) : Strings.isEmpty(value) ? null : value);
			}
			if (logic == null) {
				logic = _logic;
			} else if (setions.get(i - 1).equals("or")) {
				if (logic instanceof Or) {
					Logic[] logics = ((Or) logic).getLogics();
					Logic[] merges = new Logic[logics.length + 1];
					System.arraycopy(logics, 0, merges, 0, logics.length);
					merges[merges.length - 1] = _logic;
					logic = new Or(merges);
				} else {
					logic = new Or(logic, _logic);
				}
			} else {
				if (logic instanceof And) {
					Logic[] logics = ((And) logic).getLogics();
					Logic[] merges = new Logic[logics.length + 1];
					System.arraycopy(logics, 0, merges, 0, logics.length);
					merges[merges.length - 1] = _logic;
					logic = new And(merges);
				} else {
					logic = new And(logic, _logic);
				}
			}
		}
		return logic;
	}

}
