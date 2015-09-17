package ypa.annotation.jpql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 被注解的属性值将用来直接拼接jpql语句，被注解的属性值会被用作jpql内的查询条件的变量值
 * @author yangyuan
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DirectJPQL{
	/**
	 * jpql条件语句片，这里可以定义多个条件语句，每个条件语句将会使用“and”连接，如果需要使用到“or”等逻辑符，请直接在一个jpql片段内直接使用“or”等。
	 * @return jpql条件语句片数组
	 */
	String[] jpqlFragments() default {};

	/**
	 * 查询是默认忽略空条件的，但是如果将此注解配置为false，则强制让查询去匹配那个空条件
	 * TODO 考虑在下个版本删掉此配置，即，只要有jpqlFragmentsWhenNull配置不为空，那么就启用
	 */
	boolean ignoreNull() default true;

	/**
	 * 当被注解的属性值为null时，启用此jpql片段，而不其要你管jpqlFragments片段
	 * 需要注意的是，ignoreNull配置为false，此配置才会生效
	 * @return
	 */
	String[] jpqlFragmentsWhenNull() default {};

}
