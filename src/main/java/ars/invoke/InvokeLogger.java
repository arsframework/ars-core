package ars.invoke;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ars.util.Strings;
import ars.util.Servers;
import ars.invoke.request.Requester;
import ars.invoke.event.InvokeListener;
import ars.invoke.event.InvokeCompleteEvent;

/**
 * 请求调用日志记录器
 *
 * @author wuyongqiang
 */
public class InvokeLogger implements InvokeListener<InvokeCompleteEvent> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String pattern; // 资源地址匹配模式
    private boolean async; // 是否异步输出日志
    private boolean console; // 是否在控制台打印日志

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isConsole() {
        return console;
    }

    public void setConsole(boolean console) {
        this.console = console;
    }

    /**
     * 记录日志信息
     *
     * @param requester 请求对象
     * @param timestamp 时间戳
     * @param value     请求结果值
     */
    protected void log(Requester requester, Date timestamp, Object value) {
        String message = Invokes.getLog(requester, timestamp, value);
        if (this.console) {
            System.out.print(message);
            if (value instanceof Throwable) {
                ((Throwable) value).printStackTrace();
            }
        } else if (value instanceof Throwable) {
            logger.error(message, (Throwable) value);
        } else {
            logger.debug(message);
        }
    }

    @Override
    public void onInvokeEvent(final InvokeCompleteEvent event) {
        final Object value = event.getValue();
        if (value instanceof Throwable
            || ((this.console || logger.isDebugEnabled()) && (this.pattern == null || Strings.matches(event
            .getSource().getUri(), this.pattern)))) {
            if (this.async) {
                Servers.submit(new Runnable() {

                    @Override
                    public void run() {
                        log(event.getSource(), event.getTimestamp(), value);
                    }

                });
            } else {
                this.log(event.getSource(), event.getTimestamp(), value);
            }
        }
    }

}
