package ypa.model.order;

import ypa.model.customer.Customer;
import ypa.model.product.Product;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by happyyangyuan on 15/10/9.
 * The order entity.
 */
@Entity
public class Order implements Serializable {

    @Id
    private String orderNumber;

    private Date date;

    @ManyToMany
    private List<Product> products;

    @ManyToOne
    private Customer customer;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
