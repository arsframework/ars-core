package ars.invoke.channel.http;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ars.util.Strings;
import ars.invoke.Context;

/**
 * 多Http请求通道包装实现
 *
 * @author wuyq
 */
public class MultiHttpChannelWrapper implements HttpChannel {
    protected final Map<String, HttpChannel> channels; // 资源/请求通道映射表

    public MultiHttpChannelWrapper(Map<String, HttpChannel> channels) {
        if (channels == null || channels.isEmpty()) {
            throw new IllegalArgumentException("Channels must not be empty");
        }
        this.channels = channels;
    }

    /**
     * 根据原生Http请求对象查找对应Http请求通道
     *
     * @param request 原生Http请求对象
     * @return Http请求通道
     */
    protected HttpChannel lookupChannel(HttpServletRequest request) {
        String uri = request.getRequestURI().trim();
        for (Entry<String, HttpChannel> entry : this.channels.entrySet()) {
            if (Strings.matches(uri, entry.getKey())) {
                return entry.getValue();
            }
        }
        throw new RuntimeException("Http channel not found:" + uri);
    }

    @Override
    public Context getContext() {
        return this.channels.entrySet().iterator().next().getValue().getContext();
    }

    @Override
    public void setContext(Context context) {
        for (Entry<String, HttpChannel> entry : this.channels.entrySet()) {
            entry.getValue().setContext(context);
        }
    }

    @Override
    public void dispatch(ServletConfig config, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        this.lookupChannel(request).dispatch(config, request, response);
    }

}
