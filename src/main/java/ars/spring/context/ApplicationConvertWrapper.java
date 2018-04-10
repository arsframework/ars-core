package ars.spring.context;

import java.util.Map;

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
 * @author wuyongqiang
 */
public class ApplicationConvertWrapper extends StandardConvertWrapper implements Converter, ApplicationContextAware {

    public ApplicationConvertWrapper() {
        this(new JsonConverter());
    }

    public ApplicationConvertWrapper(Converter converter) {
        super(converter);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ThrowableResolver> throwableResolvers = applicationContext.getBeansOfType(ThrowableResolver.class);
        if (!throwableResolvers.isEmpty()) {
            this.setThrowableResolvers(throwableResolvers.values().toArray(new ThrowableResolver[0]));
        }
    }

}
