package ars.spring.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ars.invoke.convert.Converter;
import ars.invoke.convert.JsonConverter;
import ars.invoke.convert.ThrowableResolver;
import ars.invoke.convert.StandardConvertWrapper;

/**
 * 基于Spring上下文对象数据转换实现
 * 
 * @author wuyq
 * 
 */
public class ApplicationConvertWrapper implements Converter, ApplicationContextAware {
	protected Converter converter;

	public ApplicationConvertWrapper() {
		this(new JsonConverter());
	}

	public ApplicationConvertWrapper(Converter converter) {
		if (converter == null) {
			throw new IllegalArgumentException("Illegal converter:" + converter);
		}
		this.converter = converter;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ThrowableResolver[] throwableResolvers = applicationContext.getBeansOfType(ThrowableResolver.class).values()
				.toArray(new ThrowableResolver[0]);
		this.converter = new StandardConvertWrapper(this.converter, throwableResolvers);
	}

	@Override
	public String serialize(Object object) {
		return this.converter.serialize(object);
	}

	@Override
	public Object deserialize(String string) {
		return this.converter.deserialize(string);
	}

}
