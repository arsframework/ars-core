package ars.spring.context;

import java.util.Map;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import ars.invoke.remote.Remotes;
import ars.spring.context.ApplicationListener;

/**
 * 远程调用应用配置
 * 
 * @author yongqiangwu
 *
 */
public class RemoteApplicationConfiguration extends ApplicationListener {

	public void setClient(String client) {
		Remotes.setClient(client);
	}

	public void setDirectory(String directory) {
		Remotes.setDirectory(directory);
	}

	public void setConfigure(Map<String, String> configure) {
		Remotes.setConfigure(configure);
	}

	public void setCommunicator(Ice.Communicator communicator) {
		Remotes.setCommunicator(communicator);
	}

	@Override
	protected void initialize(ContextRefreshedEvent event) {

	}

	@Override
	protected void destroy(ContextClosedEvent event) {
		Remotes.destroy();
	}

}
