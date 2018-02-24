package ars.invoke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ars.util.Strings;
import ars.invoke.Invokes;
import ars.invoke.event.InvokeListener;
import ars.invoke.event.InvokeCompleteEvent;

/**
 * 请求调用日志记录器
 * 
 * @author yongqiangwu
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

	@Override
	public void onInvokeEvent(InvokeCompleteEvent event) {
		Object value = event.getValue();
		if (value instanceof Throwable || (logger.isDebugEnabled()
				&& (this.pattern == null || Strings.matches(event.getSource().getUri(), this.pattern)))) {
			String message = Invokes.getLog(event.getSource(), value, event.getTimestamp());
			if (value instanceof Throwable) {
				logger.error(message, (Throwable) value);
			} else {
				logger.debug(message);
			}
		}
	}

}
