package ypa.test;


import ypa.common.IJpaDao;
import ypa.dao.customer.CustomerDao;

/**
 * This is unit test for customerDao
 * Created by happyyangyuan on 15/9/18.
 */
public class CustomerDaoJUnitTest extends AbstractDaoJUnitTest{

    private CustomerDao customerDao;


    @Override
    Class<? extends IJpaDao> getDaoImplClass() {
        return CustomerDao.class;
    }

    @Override
    String getPersistenceUnitName() {
        return "test";
    }
}
