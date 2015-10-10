package ypa.test;

import org.junit.After;
import org.junit.Before;
import ypa.common.IJpaDao;
import ypa.utils.PersistenceContextHelperForTest;

import javax.persistence.EntityManager;

/**
 * dao单元测试类的基类
 * Created by happyyangyuan on 15/9/18.
 */
public abstract class AbstractDaoJUnitTest {

    protected IJpaDao dao;

    private EntityManager em;

    /**
     * inject the entity manager into dao
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Before
    public void setup() throws IllegalAccessException, InstantiationException {
        dao = getDaoImplClass().newInstance();
        em = new PersistenceContextHelperForTest().getEnttiyManager(getPersistenceUnitName());
        ypa.utils.ReflectionUtils.setFieldValue(dao, "em", em);
    }

    abstract Class<? extends IJpaDao> getDaoImplClass();

    abstract String getPersistenceUnitName();

    @After
    public void destroy() {
        em.close();
    }

}
