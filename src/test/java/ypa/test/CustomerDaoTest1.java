package ypa.test;


import org.junit.Test;
import ypa.common.IJpaDao;
import ypa.dao.customer.CustomerDao;
import ypa.model.customer.Customer;
import ypa.model.customer.CustomerCondition_fuzzyName;

import java.util.List;

/**
 * This is unit test for customerDao
 * Created by happyyangyuan on 15/9/18.
 */
public class CustomerDaoTest1 extends AbstractDaoJUnitTest{

    @Test
    public void test(){
        //query customers whos name contains "Yang".Please refer to class CustomerCondition_fuzzyName's name property for jpql detail.
        List<Customer> cs = dao.query(Customer.class, new CustomerCondition_fuzzyName().setName("Yang"));
        System.out.println(cs.size());
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
