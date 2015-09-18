package ypa.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import ypa.model.exception.ExceptionCode;
import ypa.model.exception.YpaRuntimeException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

    private static Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

    /**
     * @param condition
     * @param field
     * @return 返回指定域的在指定对象内的值
     */
    public static Object getPropertyValue(Object condition, Field field) {
        Object value;
        String name = field.getName();
        if (field.isAccessible()) {// 可访问的属性，直接读取其值
            try {
                value = field.get(condition);
            } catch (Throwable e) {
                throw new YpaRuntimeException(ExceptionCode.REFLECTION_ERR, condition, e);
            }
        } else {// 属性不可直接访问，通过getter来读取其值 TODO
            // 只实现了getXxx形式的getter，但是漏掉了布尔型的isXxx getter方法！
            // 对于大Boolean 生成的getter形式是getXXX,因此目前还没有出现问题
            String getterName = "get" + StringUtils.capitalize(name);
            Method getter;
            try {
                getter = condition.getClass().getMethod(getterName);
            } catch (NoSuchMethodException e) {
                log.debug("no getter '" + getterName + "()' in class '" + condition.getClass().getName() + "'", e);
                //没有getter，跳过
                return null;
            }
            try {
                value = getter.invoke(condition);
            } catch (Throwable e) {
                throw new YpaRuntimeException(ExceptionCode.REFLECTION_ERR, condition, e);
            }
        }
        return value;
    }

    /**
     * 判断是否是getter方法
     *
     * @param method
     * @return
     */
    public static boolean isGetter(Method method) {
        if (method.getParameterTypes().length == 0) {
            if ((method.getReturnType() == boolean.class || method
                    .getReturnType() == Boolean.class)
                    && method.getName().startsWith("is")) {
                // 布尔类型的getter
                return true;
            }
            if (method.getName().startsWith("get")
                    && method.getReturnType() != void.class) {
                // get型的getter
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定的方法是不是指定的属性的getter
     *
     * @param method        要判定的方法
     * @param propertyNames 属性名数组
     * @return 如果是返回true，否则返回false
     */
    public static boolean isGetter(Method method, String... propertyNames) {
        if (!isGetter(method)) return false;
        Class clazz = method.getDeclaringClass();
        for (String propertyName : propertyNames) {
            if (method.equals(getGetter(clazz, propertyName))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSetter(Method method) {
        return method.getParameterTypes().length == 1 && method.getReturnType() == void.class && method.getName().startsWith("set");
    }

    public static List<Method> getAllSetters(Class clazz) {
        List<Method> setters = new ArrayList<Method>();
        for (Method method : clazz.getMethods()) {
            if (isSetter(method)) {
                setters.add(method);
            }
        }
        return setters;
    }

    /**
     * 根据getter找对应的setter
     *
     * @param getter
     * @return setter
     */
    public static Method getSetter(Method getter) {
        Assert.isTrue(isGetter(getter), "param 'getter' must be a getter!");
        String setterName = "";
        if (getter.getName().startsWith("is")) {
            setterName = "set" + getter.getName().substring(2);
        }
        if (getter.getName().startsWith("get")) {
            setterName = "set" + getter.getName().substring(3);
        }
        try {
            return getter.getDeclaringClass().getMethod(setterName, getter.getReturnType());
        } catch (Throwable e) {
            throw new YpaRuntimeException(ExceptionCode.REFLECTION_ERR, getter, e);
        }
    }

    /**
     * 从指定的类中获取指定属性的getter方法
     *
     * @param clazz
     * @param propertyName
     * @return 如果指定的属性不存在，那么返回null
     */
    public static Method getGetter(Class clazz, String propertyName) {
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(clazz, propertyName);
        if (propertyDescriptor == null) {
            return null;//不存在该属性
        } else {
            return propertyDescriptor.getReadMethod();
        }
    }

    /**
     * 从指定的类中获取指定属性的setter方法
     *
     * @param clazz
     * @param propertyName
     * @return 如果指定的属性不存在，那么返回null
     */
    public static Method getSetter(Class clazz, String propertyName) {
        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(clazz, propertyName);
        if (propertyDescriptor == null) {
            return null;//不存在该属性
        } else {
            return propertyDescriptor.getWriteMethod();
        }
    }

}
