package ars.spring.context;

import org.apache.http.conn.ClientConnectionManager;
import org.springframework.context.event.ContextClosedEvent;

import ars.invoke.channel.http.Https;
import ars.spring.context.ApplicationDestroyer;

/**
 * Http调用应用配置
 * 
 * @author yongqiangwu
 *
 */
public class HttpApplicationConfiguration extends ApplicationDestroyer {

	public void setManager(ClientConnectionManager manager) {
		Https.setManager(manager);
	}

	@Override
	protected void execute(ContextClosedEvent event) {
		Https.destroy();
	}

}
