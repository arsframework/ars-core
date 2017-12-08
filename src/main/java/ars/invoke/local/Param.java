package ars.invoke.local;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Inherited;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import ars.util.Strings;
import ars.invoke.local.ParamAdapter;

/**
 * 接口参数注解
 * 
 * @author wuyq
 * 
 */
@Inherited
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
	/**
	 * 获取参数名称
	 * 
	 * @return 参数名称
	 */
	public String name() default Strings.EMPTY_STRING;

	/**
	 * 参数默认值
	 * 
	 * @return 参数默认值
	 */
	public String value() default Strings.EMPTY_STRING;

	/**
	 * 参数正则表达式
	 * 
	 * @return 正则表达式
	 */
	public String regex() default Strings.EMPTY_STRING;

	/**
	 * 参数是否必须
	 * 
	 * @return true/false
	 */
	public boolean required() default false;

	/**
	 * 获取参数适配器
	 * 
	 * @return 参数适配器
	 */
	public Class<? extends ParamAdapter> adapter() default ParamAdapter.class;

}
