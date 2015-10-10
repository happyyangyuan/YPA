package ypa.test;

import org.junit.Test;
import ypa.common.IJpaDao;
import ypa.dao.customer.CustomerDao;
import ypa.dao.customer.ICustomerDao;
import ypa.model.customer.CustomerCondition;

/**
 * Created by happyyangyuan on 15/9/21.
 * Test for customer dao.
 */
public class CustomerDaoTest extends AbstractDaoJUnitTest {

    @Test
    public void test() {
        ICustomerDao customerDao = (ICustomerDao) dao;

        //like query, query all customers who's name like "%Yang%"
        customerDao.queryCustomers(new CustomerCondition().setName("Yang"));

        //full text query
        customerDao.queryCustomers(new CustomerCondition().setAny("911"));

        //join query
        customerDao.queryCustomers(new CustomerCondition().setOrderNumber("20151009"));

        //multi-join query
        customerDao.queryCustomers(new ypa.model.customer.advanced.CustomerCondition().setProductName("doll"));

        //left join instead of inner join
        customerDao.queryCustomers(new ypa.model.customer.advanced0.CustomerCondition().setWithOrders(true).setWithProducts(true));

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
