package ypa.annotation.jpql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * left outer join
 * created by happyyangyuan 15/9/17
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface LeftOuterJoin {

    /**
     * outter join的属性别名
     */
    String[] outerJoinAliases();

    /**
     * 此属性与outterJoinAlias结合使用，用来指明left outer join的是哪个属性名，如果outerJoinAlias为空数组，那么此属性被忽略，
     * 支持带path的属性名！
     */
    String[] propertyNames();

}
