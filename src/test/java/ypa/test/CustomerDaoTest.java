package ypa.test;

import org.junit.Test;
import ypa.common.IJpaDao;
import ypa.dao.customer.CustomerDao;
import ypa.dao.customer.ICustomerDao;
import ypa.model.customer.CustomerCondition;

/**
 * Created by happyyangyuan on 15/9/21.
 */
public class CustomerDaoTest extends AbstractDaoJUnitTest {

    @Test
    public void test() {
        ICustomerDao customerDao = (ICustomerDao) dao;
        customerDao.queryCustomers(new CustomerCondition().setName("Yang"));
    }

    @Override
    Class<? extends IJpaDao> getDaoImplClass() {
        return CustomerDao.class;
    }

    @Override
    String getPersistenceUnitName() {
        return "test";
    }
}
