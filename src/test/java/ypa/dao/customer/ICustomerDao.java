package ypa.dao.customer;

import ypa.model.customer.Customer;
import ypa.model.customer.CustomerCondition;

import java.util.List;

/**
 * Created by happyyangyuan on 15/9/18.
 */
public interface ICustomerDao {

    List<Customer> queryCustomers(CustomerCondition condition);

    List<Customer> queryCustomers(ypa.model.customer.advanced.CustomerCondition condition);
}
