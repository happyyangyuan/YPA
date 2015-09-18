package ypa.dao.customer;

import ypa.model.customer.Customer;
import ypa.model.customer.CustomerCondition_1;

import java.util.List;

/**
 * Created by happyyangyuan on 15/9/18.
 */
public interface ICustomerDao {

    List<Customer> queryCustomers(CustomerCondition_1 condition_1);
}
