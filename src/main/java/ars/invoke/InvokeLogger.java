package ars.invoke;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ars.util.Dates;
import ars.util.Strings;
import ars.invoke.request.Requester;
import ars.invoke.event.InvokeListener;
import ars.invoke.event.InvokeCompleteEvent;

/**
 * 请求调用日志记录器
 * 
 * @author wuyq
 * 
 */
public class InvokeLogger implements InvokeListener<InvokeCompleteEvent> {
	public static final Logger logger = LoggerFactory.getLogger(InvokeLogger.class);

	private String pattern; // 资源地址匹配模式

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * 日志记录
	 * 
	 * @param requester
	 *            请求对象
	 * @param value
	 *            请求结果值
	 * @param timestamp
	 *            时间戳
	 */
	protected void log(Requester requester, Object value, Date timestamp) {
		boolean debug = logger.isDebugEnabled();
		boolean success = !(value instanceof Throwable);
		if (!success || (debug && (this.pattern == null || Strings.matches(requester.getUri(), this.pattern)))) {
			StringBuilder info = new StringBuilder().append('\n').append(Dates.format(requester.getCreated(), true))
					.append(" [").append(requester.getHost()).append(']');
			if (requester.getUser() != null) {
				info.append(" [").append(requester.getUser()).append(']');
			}
			info.append(" [").append(requester.getUri()).append("] [")
					.append(Dates.getUnitTime(timestamp.getTime() - requester.getCreated().getTime())).append("]\n")
					.append(requester.getParameters());
			if (success) {
				info.append('\n').append(value);
			}
			info.append('\n');
			if (!success) {
				logger.error(info.toString(), (Throwable) value);
			} else if (debug) {
				logger.debug(info.toString());
			}
		}
	}

	@Override
	public void onInvokeEvent(InvokeCompleteEvent event) {
		this.log(event.getSource(), event.getValue(), event.getTimestamp());
	}

}
