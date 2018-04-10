package ars.invoke.remote;

import java.util.Map;
import java.util.Locale;

import ars.invoke.Channel;
import ars.invoke.request.Token;
import ars.invoke.request.Requester;
import ars.invoke.request.StandardRequester;

/**
 * 基于ICE请求对象标准实现
 *
 * @author wuyongqiang
 */
public class StandardRemoteRequester extends StandardRequester implements RemoteRequester {
    private static final long serialVersionUID = 1L;

    private transient Ice.Current current;

    public StandardRemoteRequester(Channel channel, Ice.Current current, Requester parent, Locale locale, String client,
                                   String host, Token token, String uri, Map<String, Object> parameters) {
        super(channel, parent, locale, client, host, token, uri, parameters);
        if (current == null) {
            throw new IllegalArgumentException("Current must not be null");
        }
        this.current = current;
    }

    @Override
    public Ice.Current getCurrent() {
        return this.current;
    }

    @Override
    public Requester build(String uri, Map<String, Object> parameters) {
        return new StandardRemoteRequester(this.getChannel(), this.current, this, this.getLocale(), this.getClient(),
            this.getHost(), this.getToken(), uri, parameters);
    }

}
