package ypa.annotation.jpql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 被注解的属性值将用来直接拼接jpql语句，被注解的属性必须是Boolean型的
 * @author yangyuan
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DirectJPQLForBoolean {

	/**
	 * 当被注解的属性值为true时的jpql语句片段数组
	 * 这里可以定义多个条件语句，每个条件语句将会使用“and”连接，如果需要使用到“or”等逻辑符，请直接在一个jpql片段内直接使用“or”等
	 */
	String[] jpqlFragmentsWhenTrue();

	/**
	 * 当被注解的属性值为false时的jpql语句片段数组
	 * 这里可以定义多个条件语句，每个条件语句将会使用“and”连接，如果需要使用到“or”等逻辑符，请直接在一个jpql片段内直接使用“or”等
	 */
	String[] jpqlFragmentsWhenFalse();
	
}
