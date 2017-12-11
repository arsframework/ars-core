package ars.spring.context;

import java.util.Collection;

import org.springframework.aop.support.AopUtils;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ars.util.Beans;

/**
 * 从Spring上下文中查找对象实体工厂类
 * 
 * @author wuyq
 * 
 */
public class LookupEntityFactoryBean implements FactoryBean<Object>, ApplicationContextAware {
	private Object entity; // 对象实体
	private Class<?> type; // 实体类型
	private boolean proxy = true; // 是否代理
	private boolean loaded; // 是否已加载
	private boolean required; // 实体是否必须存在
	private boolean multiple; // 是否为多个实体
	private Object defaultEntity; // 默认对象实体
	private ApplicationContext applicationContext;

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public Object getDefaultEntity() {
		return defaultEntity;
	}

	public void setDefaultEntity(Object defaultEntity) {
		this.defaultEntity = defaultEntity;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public Object getObject() throws Exception {
		if (!this.loaded) {
			if (this.type == null) {
				throw new RuntimeException("Type has not been initialize");
			}
			this.loaded = true;
			if (this.multiple) {
				Collection<?> objects = this.applicationContext.getBeansOfType(this.type).values();
				if (objects.isEmpty()) {
					boolean supplied = !Beans.isEmpty(this.defaultEntity);
					if (this.required && !supplied) {
						throw new NoSuchBeanDefinitionException(this.type);
					}
					this.entity = supplied ? this.defaultEntity : Beans.getArray(this.type, 0);
				} else {
					this.entity = objects.toArray(Beans.getArray(this.type, 0));
				}
			} else {
				try {
					this.entity = this.applicationContext.getBean(this.type);
				} catch (NoSuchBeanDefinitionException e) {
					if (this.required && this.defaultEntity == null) {
						throw e;
					}
					this.entity = this.defaultEntity;
				}
			}
			if (!this.proxy) {
				if (this.multiple) {
					Object[] entities = (Object[]) this.entity;
					for (int i = 0; i < entities.length; i++) {
						Object object = entities[i];
						if (AopUtils.isAopProxy(object)) {
							entities[i] = ((Advised) object).getTargetSource().getTarget();
						}
					}
				} else if (this.entity != null && AopUtils.isAopProxy(this.entity)) {
					this.entity = ((Advised) this.entity).getTargetSource().getTarget();
				}
			}
		}
		return this.entity;
	}

	@Override
	public Class<?> getObjectType() {
		return this.multiple ? Beans.getArray(this.type, 0).getClass() : this.type;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
