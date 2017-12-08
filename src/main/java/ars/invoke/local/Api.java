package ars.invoke.local;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import ars.util.Strings;

/**
 * 接口映射注解
 * 
 * @author wuyq
 * 
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Api {
	/**
	 * 获取接口映射的uri地址
	 * 
	 * @return uri地址
	 */
	public String value() default Strings.EMPTY_STRING;

}
