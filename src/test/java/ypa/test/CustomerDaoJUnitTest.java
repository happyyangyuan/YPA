package ypa.test;


import org.junit.Test;
import ypa.common.IJpaDao;
import ypa.dao.customer.CustomerDao;
import ypa.model.customer.Customer;
import ypa.model.customer.CustomerCondition;

import java.util.List;

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

    @Test
    public void test(){
        List<Customer> cs = customerDao.query(Customer.class, new CustomerCondition().setId(1L));
    }
}
