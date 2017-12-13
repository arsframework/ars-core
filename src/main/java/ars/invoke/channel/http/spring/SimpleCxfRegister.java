package ars.invoke.channel.http.spring;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 基于CXF框架的WebService数据接口服务简单实现
 * 
 * @author yongqiangwu
 * 
 */
public class SimpleCxfRegister implements DisposableBean, InitializingBean {
	private Server server;
	private String address;
	private Object service;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Object getService() {
		return service;
	}

	public void setService(Object service) {
		this.service = service;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.address == null) {
			throw new RuntimeException("Address has not been initialize");
		}
		if (this.service == null) {
			throw new RuntimeException("Service has not been initialize");
		}
		JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();
		factory.setAddress(this.address);
		factory.setServiceBean(this.service);
		this.server = factory.create();
		this.server.start();
	}

	@Override
	public void destroy() {
		this.server.stop();
		this.server.destroy();
	}

}
