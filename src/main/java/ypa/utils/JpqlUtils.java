package ypa.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ypa.annotation.jpql.DirectJPQL;
import ypa.annotation.jpql.DirectJPQLForBoolean;
import ypa.annotation.jpql.InnerJoin;
import ypa.annotation.jpql.LeftOuterJoin;

import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.*;

import static ypa.utils.MyReflectionUtils.getPropertyValue;

/**
 * jpql工具类
 *
 * @author yangyuan
 */
public class JpqlUtils {


    public static String DESC = "desc";

    public static String ASC = "asc";

    /**
     * 默认使用distinct唯一约束，包可见性
     */
    public static String SELECT_ALIAS_FROM = "select distinct alias from ";
    /**
     * 默认使用distinct唯一约束，包可见性
     */
    public static String SELECT_COUNT_DISTINCT_FROM = "select count(distinct alias) from ";

    private static Logger log = LoggerFactory.getLogger(JpqlUtils.class);

    /**
     * 直接忽略condition中的空属性值，以达到常用的多功能查询的目的： 不设值则不限制，不设值则查全部。
     *
     * @param condition
     * @return 我们关系的查询条件集合
     */
    public static Set<String> getCaredProperties(Object condition) {
        Set<String> properties = new HashSet<String>();
        if (condition == null) {
            return properties;
        }
        Set<Field> fieldSet = getAccessibleFields(condition);
        for (Field field : fieldSet) {
            Object value = getPropertyValue(condition, field);
            String name = field.getName();
            if (!StringUtils.isEmpty(value)// 属性值非空，这里认为空串“”也是空，因此使用StringUtils.isEmpty()做判断
                    && field.getAnnotation(DirectJPQLForBoolean.class) == null // 并且未被注解@DirectJPQLForBoolean
                    && field.getAnnotation(DirectJPQL.class) == null) {// 并且未被注解@DirectJPQL
                properties.add(name);
            }
        }
        return properties;
    }

    /**
     * 将查询条件转换为键值对map，我们只取出非空属性作为map的键值对，直接忽略condition中的空属性值，以达到常用的多功能查询的目的：
     * 不设值则不限制，不设值则查全部。 TODO 该方法不支持对父类的私有域进行封装(即使父类中定义了公有的getter，也不支持)
     *
     * @param condition 查询条件
     * @return 查询条件的非空属性名-属性值的map
     */
    public static Map<String, Object> conditionToMap(Object condition) {
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        if (condition == null) {
            return conditionMap;
        }
        Set<Field> fieldSet = getAccessibleFields(condition);
        for (Field field : fieldSet) {
            Object value = getPropertyValue(condition, field);
            String name = field.getName();

            if (!StringUtils.isEmpty(value)) {// 属性值非空，这里认为空串“”也是空，因此使用StringUtils.isEmpty()做判断
                conditionMap.put(name, value);
            }
        }
        return conditionMap;
    }

    /**
     * @param condition 查询条件
     * @param alias     实体对象在jpql语句中的别名
     * @return jpql片段list
     */
    @SuppressWarnings("unchecked")
    public static List<String> getDirectJpqlFragments(Object condition,
                                                      String alias) {
        if (condition == null) {
            return new ArrayList<String>();
        }
        List<String> jpqlFragments = new ArrayList<String>();
        for (Field field : getAccessibleFields(condition)) {
            Object value = getPropertyValue(condition, field);
            Map<String, Object> keyValues = new HashMap<String, Object>();
            keyValues.put("{alias}", alias);

            DirectJPQLForBoolean jpqlForBoolean = field.getAnnotation(DirectJPQLForBoolean.class);
            if (jpqlForBoolean != null) {// 当前域被注解@DirectJPQLForBoolean
                if (null != value) {
                    if ((Boolean) value) {
                        jpqlFragments.addAll(CollectionUtils.arrayToList(replace(jpqlForBoolean.jpqlFragmentsWhenTrue(), keyValues)));
                    } else {
                        jpqlFragments.addAll(CollectionUtils.arrayToList(replace(jpqlForBoolean.jpqlFragmentsWhenFalse(), keyValues)));
                    }
                }
            }

            DirectJPQL jpqlForValue = field.getAnnotation(DirectJPQL.class);
            if (jpqlForValue != null) {// 当前域被注解了@DirectJPQL
                if (jpqlForValue.ignoreNull()) {
                    //如果忽略空值条件，那么检查查询条件属性值是否为空，为空就忽略
                    if (!StringUtils.isEmpty(value)
                            && (!(value instanceof Collection) || !CollectionUtils.isEmpty((Collection) value))) {
                        // 属性值非空，这里认为空串“”也是空，因此使用StringUtils.isEmpty()做判断
                        // 属性值是一个集合，那么如果集合为size为0，那么直接忽略此查询条件
                        jpqlFragments.addAll(CollectionUtils.arrayToList(replace(jpqlForValue.jpqlFragments(), keyValues)));
                    }
                } else {//若不忽略就直接加入到查询语句中
                    String[] jpqlfragments;
                    if (value == null) {//不忽略null条件，但是jpql里与null的匹配是使用“is null”  /   "is not null"的，这只是一个容错处理
                        jpqlfragments = replaceEqualNullWithIsNull(field.getName(), jpqlForValue.jpqlFragmentsWhenNull());
                    } else {
                        jpqlfragments = jpqlForValue.jpqlFragments();
                    }
                    jpqlFragments.addAll(CollectionUtils.arrayToList(replace(jpqlfragments, keyValues)));
                }
            }
        }
        return jpqlFragments;
    }

    /**
     * 将形如 xxx = :fieldName 的语句全部转为 xxx is null
     * 注意，该算法修改了数组参数jpqlFragments内的数组元素
     *
     * @param fieldName
     * @param jpqlFragments
     * @return replace后的结果
     */
    private static String[] replaceEqualNullWithIsNull(String fieldName, String[] jpqlFragments) {
        String namedParam = ":" + fieldName;
        for (int i = 0; i < jpqlFragments.length; i++) {
            String fragment = jpqlFragments[i];
            String equalNull = findEquaNamedParamPattern(fragment, namedParam);
            while (equalNull != null) {
                fragment = fragment.replaceAll(equalNull, " is null");//注意这里is null 前面的空格，很重要
                jpqlFragments[i] = fragment;
                equalNull = findEquaNamedParamPattern(fragment, namedParam);
            }
        }
        return jpqlFragments;
    }

    /**
     * 不会正则表达式 的 悲哀
     *
     * @param fragment
     * @param namedParam
     * @return 如果包含形如 "= :namedParam"的子字符串，返回第一个出现的那个字符串，反之返回null
     */
    private static String findEquaNamedParamPattern(String fragment, String namedParam) {
        if (fragment.contains(namedParam) && fragment.contains("=")) {
            int equqlOperatorIndex = fragment.indexOf('=');
            int namedParameterIndex = fragment.indexOf(namedParam);
            if (equqlOperatorIndex < namedParameterIndex) {
                String stringOfWhiteSpaces = fragment.substring(equqlOperatorIndex + 1, namedParameterIndex);
                if (stringOfWhiteSpaces.trim().equals("")) {
                    String equalNull = fragment.substring(equqlOperatorIndex, namedParameterIndex + namedParam.length());
                    return equalNull;
                }
            }
        }
        return null;
    }

    /**
     * @param condition 查询条件
     * @param alias     目标实体别名
     * @return 外关联子句
     */
    public static String getOuterJoinClause(Object condition, String alias) {
        if (condition == null) {
            return "";
        }
        String outerJoinClause = "";
        LeftOuterJoin outerJoin = condition.getClass().getAnnotation(LeftOuterJoin.class);
        if (outerJoin != null) {
            String[] outerJoinAliasArray = outerJoin.outerJoinAliases();
            String[] propertyNameArray = outerJoin.propertyNames();
            if (!ArrayUtils.isEmpty(outerJoinAliasArray)) { // 外关联的别名配置不为空
                for (int i = 0; i < outerJoinAliasArray.length; i++) {
                    String outerJoinAlias = outerJoinAliasArray[i], propertyName = propertyNameArray[i];
                    if (containsPath(propertyName)) {
                        outerJoinClause += " left outer join " + propertyName + " as " + outerJoinAlias;
                    } else {
                        outerJoinClause += " left outer join " + alias + "." + propertyName + " as " + outerJoinAlias;
                    }
                }
            }
        }
        return outerJoinClause;
    }

    /**
     * @param condition 查询条件
     * @param alias     目标实体别名
     * @return 外关联子句
     */
    public static String getInnerJoinClause(Object condition, String alias) {
        if (condition == null) {
            return "";
        }
        String innerJoinClause = "";
        InnerJoin innerJoin = condition.getClass().getAnnotation(
                InnerJoin.class);
        if (innerJoin != null) {
            String[] innerJoinAliasArray = innerJoin.innerJoinAliases();
            String[] propertyNameArray = innerJoin.propertyNames();
            if (!ArrayUtils.isEmpty(innerJoinAliasArray)) { // 内关联的别名配置不为空
                for (int i = 0; i < innerJoinAliasArray.length; i++) {
                    String innerJoinAlias = innerJoinAliasArray[i], propertyName = propertyNameArray[i];
                    if (containsPath(propertyName)) {
                        innerJoinClause += " inner join " + propertyName
                                + " as " + innerJoinAlias;
                    } else {
                        innerJoinClause += " inner join " + alias + "."
                                + propertyName + " as " + innerJoinAlias;
                    }
                }
            }
        }
        return innerJoinClause;
    }

    /**
     * 将fragments数组内的字符串内的通配符全部替换成指定的别名
     *
     * @param fragments
     * @param map       键值对
     * @return 被替换后的数组
     */
    public static String[] replace(String[] fragments, Map<String, Object> map) {
        for (int i = 0; i < fragments.length; i++) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                fragments[i] = fragments[i].replace(key, map.get(key)
                        .toString());
            }
        }
        return fragments;
    }

    /**
     * 将字符串内的named parameters全部替换为map中的value
     * @param fragmet 含named param的sql或者jpql
     * @param map named param key-value键值对
     * @return 被替换后的sql或jpql等
     */
    public static String replace(String fragmet,Map<String,Object> map){
        return replace(new String[]{fragmet}, map)[0];
    }

    /**
     * 判断属性名是否是一个含路径的属性名
     *
     * @param propertyName
     * @return 如果包含“.”，那么返回true，否则返回false
     */
    private static boolean containsPath(String propertyName) {
        return propertyName.contains("\\.");
    }

    /**
     * TODO 该方法不支持对父类的私有域进行封装(即使父类中定义了公有的getter，也不支持)
     *
     * @return condition内直接申明的所有域，以及condition的所有父类中可直接访问的域
     */
    private static Set<Field> getAccessibleFields(Object condition) {
        Set<Field> fieldSet = new HashSet<Field>();
        CollectionUtils.mergeArrayIntoCollection(condition.getClass()
                .getFields(), fieldSet);// 父子类所有的public域
        CollectionUtils.mergeArrayIntoCollection(condition.getClass()
                .getDeclaredFields(), fieldSet);// 子类定义的所有域
        return fieldSet;
    }

    /**
     * 添加模糊查询逻辑，
     */
    public static String addFuzzyness(String value) {
        if (!StringUtils.isEmpty(value)) {
            if (!value.startsWith("%")) {
                value = "%" + value;
            }
            if (!value.endsWith("%")) {
                value += "%";
            }
        }
        return value;
    }

    /**
     * 将变量设置到JPA query对象中
     *
     * @param query
     * @param condition
     */
    public static void setParameters(Query query, Object condition) {
        Map<String, Object> map = JpqlUtils.conditionToMap(condition);
        for (String key : map.keySet()) {
            Object value = map.get(key);
            try {
                query.setParameter(key, value);
            } catch (IllegalArgumentException e) {// 忽略一些值，只打印警告
                log.debug("", e);
            }
        }
    }

    public static void main(String args[]) {
        System.out.println(StringUtils.isEmpty(1L));
    }

}
