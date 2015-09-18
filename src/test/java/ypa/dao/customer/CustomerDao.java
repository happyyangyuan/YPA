package ypa.dao.customer;

import ypa.common.AbstractJpaDao;
import ypa.model.customer.Customer;
import ypa.model.customer.CustomerCondition_1;

import java.util.List;

/**
 * This is a demo of dao used for condition query.
 * Created by happyyangyuan on 15/9/18.
 */
public class CustomerDao extends AbstractJpaDao implements ICustomerDao {
    public List<Customer> queryCustomers(CustomerCondition_1 condition_1) {
        return query(Customer.class,condition_1);
    }
}
