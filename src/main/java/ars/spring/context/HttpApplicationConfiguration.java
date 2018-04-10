package ars.spring.context;

import org.apache.http.conn.ClientConnectionManager;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import ars.invoke.channel.http.Https;

/**
 * Http调用应用配置
 *
 * @author wuyongqiang
 */
public class HttpApplicationConfiguration extends ApplicationListener {

    public void setManager(ClientConnectionManager manager) {
        Https.setManager(manager);
    }

    @Override
    protected void initialize(ContextRefreshedEvent event) {

    }

    @Override
    protected void destroy(ContextClosedEvent event) {
        Https.destroy();
    }

}
