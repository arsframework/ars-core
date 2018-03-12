package ars.invoke.channel.http.tags;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import ars.util.Jsons;
import ars.util.Beans;
import ars.util.Strings;
import ars.invoke.channel.http.Https;
import ars.invoke.channel.http.HttpRequester;

/**
 * 自定义JSP标签抽象类
 * 
 * @author yongqiangwu
 * 
 */
public abstract class AbstractTag extends SimpleTagSupport {
	/**
	 * 页面作用域范围
	 */
	public static final String SCOPE_PAGE = "page";

	/**
	 * 请求作用域范围
	 */
	public static final String SCOPE_REQUEST = "request";

	/**
	 * 会话作用域范围
	 */
	public static final String SCOPE_SESSION = "session";

	/**
	 * 应用作用域范围
	 */
	public static final String SCOPE_APPLICATION = "application";

	private String var; // 结果变量名称
	private String scope; // 作用域范围
	private String group; // 对象属性名称，将返回对象按照指定属性值进行分组
	private String property; // 对象属性名称，如果该值不为空则返回对象属性值
	private String mapping; // 映射属性名称，与property配合使用，返回Map对象
	private boolean json; // 是否将对象转换成JSON字符串
	private boolean string; // 是否将对象转换成字符串

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var.trim();
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope.trim();
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group.trim();
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property.trim();
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping.trim();
	}

	public boolean isJson() {
		return json;
	}

	public void setJson(boolean json) {
		this.json = json;
	}

	public boolean isString() {
		return string;
	}

	public void setString(boolean string) {
		this.string = string;
	}

	/**
	 * 执行自定义标签
	 * 
	 * @return 执行结果
	 * @throws Exception
	 *             操作异常
	 */
	protected abstract Object execute() throws Exception;

	/**
	 * 获取当前请求对象
	 * 
	 * @return 请求对象
	 */
	protected HttpRequester getRequester() {
		return (HttpRequester) this.getJspContext().getAttribute(Https.CONTEXT_EXECUTOR, PageContext.REQUEST_SCOPE);
	}

	/**
	 * 设置JSP标签结果上下文
	 * 
	 * @param value
	 *            上下文值对象
	 * @throws IOException
	 *             IO操作异常
	 * @throws JspException
	 *             Jsp操作异常
	 */
	protected void setContextValue(Object value) throws IOException, JspException {
		JspContext context = this.getJspContext();
		if (Strings.isEmpty(this.var)) {
			if (value != null) {
				context.getOut().write(Strings.toString(value));
			}
		} else if (Strings.isEmpty(this.scope)) {
			context.setAttribute(this.var, value);
		} else if (this.scope.equals(SCOPE_PAGE)) {
			context.setAttribute(this.var, value, PageContext.PAGE_SCOPE);
		} else if (this.scope.equals(SCOPE_REQUEST)) {
			context.setAttribute(this.var, value, PageContext.REQUEST_SCOPE);
		} else if (this.scope.equals(SCOPE_SESSION)) {
			context.setAttribute(this.var, value, PageContext.SESSION_SCOPE);
		} else if (this.scope.equals(SCOPE_APPLICATION)) {
			context.setAttribute(this.var, value, PageContext.APPLICATION_SCOPE);
		}
		JspFragment jspBody = this.getJspBody();
		if (jspBody != null) {
			jspBody.invoke(null);
		}
	}

	@Override
	public void doTag() throws JspException, IOException {
		try {
			Object value = this.execute();
			if (value != null) {
				if (!Strings.isEmpty(this.group)) {
					value = Beans.getAssemblePropertyGroups(value, this.group, this.mapping);
				} else if (!Strings.isEmpty(this.property)) {
					value = Strings.isEmpty(this.mapping) ? Beans.getAssemblePropertyValue(value, this.property)
							: Beans.getAssemblePropertyValue(value, this.property, this.mapping);
				}
				if (!(value instanceof CharSequence)) {
					if (this.json) {
						value = Jsons.format(value);
					} else if (this.string) {
						value = Strings.toString(value);
					}
				}
			}
			this.setContextValue(value);
		} catch (Exception e) {
			if (e instanceof IOException) {
				throw (IOException) e;
			} else if (e instanceof JspException) {
				throw (JspException) e;
			} else if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException((Exception) e);
		}
	}

}
