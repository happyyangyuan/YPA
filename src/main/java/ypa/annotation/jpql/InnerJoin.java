package ypa.annotation.jpql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 注解外关联别名
 *
 * @author yangyuan
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface InnerJoin {

    /**
     * inner join的属性别名
     */
    String[] innerJoinAliases();

    /**
     * 此属性与innerJoinAlias结合使用，用来指明inner join的是哪个属性名，如果innerJoinAlias为空数组，那么此属性被忽略，
     * 支持带path的属性名！
     */
    String[] propertyNames();

}
