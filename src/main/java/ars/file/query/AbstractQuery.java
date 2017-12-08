package ars.file.query;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;

import ars.util.Beans;
import ars.util.Strings;
import ars.file.Describe;
import ars.file.Describe.Property;
import ars.file.query.Query;
import ars.file.query.condition.Order;
import ars.file.query.condition.Condition;
import ars.file.query.condition.Conditions;
import ars.file.query.condition.Like.Position;

/**
 * 文件查询集合抽象实现
 * 
 * @author wuyq
 * 
 */
public abstract class AbstractQuery implements Query {
	private String path; // 查询操作相对路径
	private int page, size; // 分页参数
	private boolean loaded; // 集合是否已加载
	private boolean spread; // 是否展开
	private String workingDirectory; // 工作目录
	private List<Order> orders = new LinkedList<Order>(); // 排序条件
	private List<Describe> cache = Collections.emptyList(); // 缓存数据
	private List<Condition> conditions = new LinkedList<Condition>(); // 查询条件

	public AbstractQuery() {
		this.workingDirectory = Strings.TEMP_PATH;
	}

	public AbstractQuery(String workingDirectory) {
		this.workingDirectory = Strings.getRealPath(workingDirectory);
	}

	/**
	 * 执行文件查询
	 * 
	 * @param path
	 *            文件查询相对路径
	 * @param spread
	 *            是否展开
	 * @param conditions
	 *            查询条件数组
	 * @return 文件描述列表
	 * @throws Exception
	 */
	protected abstract List<Describe> execute(String path, boolean spread,
			Condition... conditions) throws Exception;

	@Override
	public Iterator<Describe> iterator() {
		try {
			return this.list().iterator();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getWorkingDirectory() {
		return this.workingDirectory;
	}

	@Override
	public Query path(String path) {
		this.path = path;
		return this;
	}

	@Override
	public Query eq(Property property, Object value) {
		if (value != null) {
			this.conditions.add(Conditions.eq(property, value));
		}
		return this;
	}

	@Override
	public Query ne(Property property, Object value) {
		if (value != null) {
			this.conditions.add(Conditions.ne(property, value));
		}
		return this;
	}

	@Override
	public Query gt(Property property, Object value) {
		if (value != null) {
			this.conditions.add(Conditions.gt(property, value));
		}
		return this;
	}

	@Override
	public Query ge(Property property, Object value) {
		if (value != null) {
			this.conditions.add(Conditions.ge(property, value));
		}
		return this;
	}

	@Override
	public Query lt(Property property, Object value) {
		if (value != null) {
			this.conditions.add(Conditions.lt(property, value));
		}
		return this;
	}

	@Override
	public Query le(Property property, Object value) {
		if (value != null) {
			this.conditions.add(Conditions.le(property, value));
		}
		return this;
	}

	@Override
	public Query between(Property property, Object low, Object high) {
		if (low != null && high != null) {
			this.conditions.add(Conditions.between(property, low, high));
		}
		return this;
	}

	@Override
	public Query start(Property property, String value) {
		if (value != null) {
			this.conditions.add(Conditions
					.like(property, value, Position.BEGIN));
		}
		return this;
	}

	@Override
	public Query end(Property property, String value) {
		if (value != null) {
			this.conditions.add(Conditions.like(property, value, Position.END));
		}
		return this;
	}

	@Override
	public Query like(Property property, String value) {
		if (value != null) {
			this.conditions.add(Conditions.like(property, value, Position.ANY));
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query custom(String key, Object value) {
		if (value == null) {
			return this;
		}
		if (key.equalsIgnoreCase(PAGE)) {
			this.page = Integer.parseInt(value.toString());
		} else if (key.equalsIgnoreCase(SIZE)) {
			this.size = Integer.parseInt(value.toString());
		} else if (key.equalsIgnoreCase(ORDER)) {
			Collection<String> collection = value instanceof Collection ? (Collection<String>) value
					: value instanceof String[] ? Arrays
							.asList((String[]) value) : Arrays.asList(value
							.toString());
			for (String order : collection) {
				boolean desc = order.charAt(0) == '-';
				String property = desc || order.charAt(0) == '+' ? order
						.substring(1) : order;
				if (desc) {
					this.desc(Describe.Property.valueOf(property.toUpperCase()));
				} else {
					this.asc(Describe.Property.valueOf(property.toUpperCase()));
				}
			}
		} else if (!key.startsWith(MARK)) {
			int index = key.indexOf(MARK);
			String name = index > 0 ? key.substring(0, index) : key; // 属性名称
			String feature = index > 0 ? key.substring(index + MARK.length())
					: EQ; // 特性查询类型
			Property property = null;
			try {
				property = Describe.Property.valueOf(name.toUpperCase());
			} catch (Exception e) {
				return this;
			}
			if (feature.equalsIgnoreCase(START)) {
				this.start(property, value.toString());
			} else if (feature.equalsIgnoreCase(END)) {
				this.end(property, value.toString());
			} else if (feature.equalsIgnoreCase(LIKE)) {
				this.like(property, value.toString());
			} else {
				Class<?> type = property == Describe.Property.NAME ? String.class
						: property == Describe.Property.SIZE ? int.class
								: property == Describe.Property.MODIFIED ? Date.class
										: boolean.class;
				value = Beans.toObject(type, value);
				if (feature.equalsIgnoreCase(GE)) {
					this.ge(property, value);
				} else if (feature.equalsIgnoreCase(GT)) {
					this.gt(property, value);
				} else if (feature.equalsIgnoreCase(LE)) {
					this.le(property, value);
				} else if (feature.equalsIgnoreCase(LT)) {
					this.lt(property, value);
				} else if (feature.equalsIgnoreCase(EQ)) {
					this.eq(property, value);
				} else if (feature.equalsIgnoreCase(NE)) {
					this.ne(property, value);
				}
			}
		}
		return this;
	}

	@Override
	public Query custom(Map<String, Object> parameters) {
		if (parameters != null && !parameters.isEmpty()) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				this.custom(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	@Override
	public Query asc(Property... properties) {
		if (properties != null && properties.length > 0) {
			for (Property property : properties) {
				this.orders.add(Conditions.asc(property));
			}
		}
		return this;
	}

	@Override
	public Query desc(Property... properties) {
		if (properties != null && properties.length > 0) {
			for (Property property : properties) {
				this.orders.add(Conditions.desc(property));
			}
		}
		return this;
	}

	@Override
	public Query paging(int page, int size) {
		if (page < 1) {
			throw new IllegalArgumentException("Illegal page:" + page);
		}
		if (size < 1) {
			throw new IllegalArgumentException("Illegal size:" + size);
		}
		this.page = page;
		this.size = size;
		return this;
	}

	@Override
	public Query spread(boolean spread) {
		this.spread = spread;
		return this;
	}

	@Override
	public List<Describe> list() throws Exception {
		if (!this.loaded) {
			Condition[] conditions = this.conditions.toArray(new Condition[0]);
			List<Describe> describes = this.execute(this.path, this.spread,
					conditions);
			if (!this.orders.isEmpty()) {
				Conditions.sort(describes, this.orders.toArray(new Order[0]));
			}
			this.cache = this.page > 0 && this.size > 0 ? Conditions.paging(
					describes, this.page, this.size) : describes;
			this.loaded = true;
		}
		return this.cache;
	}

}
