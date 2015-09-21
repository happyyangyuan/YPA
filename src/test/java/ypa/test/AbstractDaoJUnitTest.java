package ypa.test;

import org.junit.After;
import org.junit.Before;
import org.springframework.util.ReflectionUtils;
import ypa.common.IJpaDao;
import ypa.model.exception.ExceptionCode;
import ypa.model.exception.YpaRuntimeException;
import ypa.utils.PersistenceContextHelperForTest;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

/**
 * dao单元测试类的基类
 * Created by happyyangyuan on 15/9/18.
 */
public abstract class AbstractDaoJUnitTest {

    protected IJpaDao dao;

    private EntityManager em;


    @Before
    void setup() throws IllegalAccessException, InstantiationException {
        dao = getDaoImplClass().newInstance();
        em = new PersistenceContextHelperForTest().getEnttiyManager(getPersistenceUnitName());
        setFieldValue(dao, "em", em);
    }

    abstract Class<? extends IJpaDao> getDaoImplClass();

    abstract String getPersistenceUnitName();

    @After
    void destroy() {
        em.close();
    }

    /**
     * TODO 建议将此方法放入到扩展的reflectionUtils内
     */
    private void setFieldValue(Object obj, String fieldName, Object value) {
        Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
        ReflectionUtils.makeAccessible(field);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new YpaRuntimeException(ExceptionCode.REFLECTION_ERR, field, e);
        }
    }
}
