package ars.server.timer;

import ars.util.Beans;
import ars.server.timer.AbstractTimerServer;

/**
 * 定时处理服务普通实现
 * 
 * @author wuyq
 * 
 */
public class GeneralTimerServer extends AbstractTimerServer {
	private Object target; // 对象或实例
	private String method; // 方法名称
	private Object[] parameters = Beans.EMPTY_ARRAY; // 运行参数

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object... parameters) {
		this.parameters = parameters;
	}

	@Override
	protected void initialize() {
		if (this.target == null) {
			throw new RuntimeException("Target has not been initialize");
		}
		if (this.method == null) {
			throw new RuntimeException("Method has not been initialize");
		}
		super.initialize();
	}

	@Override
	protected void execute() {
		Beans.invoke(this.target, this.method, this.parameters);
	}

}
