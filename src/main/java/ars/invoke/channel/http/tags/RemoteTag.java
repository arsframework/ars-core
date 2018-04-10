package ars.invoke.channel.http.tags;

import java.util.Map;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import ars.util.Jsons;
import ars.util.Strings;
import ars.invoke.remote.Remotes;
import ars.invoke.remote.Protocol;
import ars.invoke.channel.http.Https;
import ars.invoke.channel.http.HttpRequester;

/**
 * 远程资源调用自定义标签
 *
 * @author wuyongqiang
 */
public class RemoteTag extends ResourceTag {
    private Protocol protocol = Protocol.tcp; // 接口协议
    private String host = Strings.DEFAULT_LOCALHOST_ADDRESS; // 主机地址
    private int port; // 主机端口

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    protected Object execute() throws Exception {
        HttpRequester requester = this.getRequester();
        Map<String, Object> parameters = this.getParameters();
        if (this.protocol == Protocol.http || this.protocol == Protocol.https) {
            String url = Https.getUrl(this.protocol, this.host, this.port, this.getApi());
            HttpUriRequest uriRequest = Https.getHttpUriRequest(url, Https.Method.POST, parameters);
            HttpServletRequest servletRequest = requester.getHttpServletRequest();
            Enumeration<String> headers = servletRequest.getHeaderNames();
            while (headers.hasMoreElements()) {
                String header = headers.nextElement();
                uriRequest.setHeader(header, servletRequest.getHeader(header));
            }
            uriRequest.addHeader(Https.CONTEXT_TOKEN, requester.getToken().getCode());
            HttpClient client = Https.getClient(this.protocol == Protocol.https);
            try {
                HttpEntity entity = client.execute(uriRequest).getEntity();
                try {
                    return Jsons.parse(EntityUtils.toString(entity));
                } finally {
                    EntityUtils.consumeQuietly(entity);
                }
            } finally {
                uriRequest.abort();
            }
        }
        Ice.ObjectPrx proxy = Remotes.getProxy(this.protocol, this.host, this.port);
        return Remotes.invoke(proxy, requester.getToken(), this.getApi(), parameters);
    }

}
