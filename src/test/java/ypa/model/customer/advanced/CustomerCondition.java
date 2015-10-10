package ypa.model.customer.advanced;


import ypa.annotation.jpql.DirectJPQL;
import ypa.annotation.jpql.InnerJoin;
import ypa.utils.JpqlUtils;

import java.io.Serializable;

//multi-join condition
@InnerJoin(innerJoinAliases = {"order", "product"}, propertyNames = {"orders", "order.products"})
//the propertyName "orders" is equal to "{alias}.orders" and "alias.orders",
//"{alias}" and "alias" represents the Customer entity as mentioned above,and if you omit it,ypa will add it for you.
//Property name "order.products" depends on alias "order",it should be placed after "orders".
public class CustomerCondition implements Serializable {

    private Long id;

    @DirectJPQL(jpqlFragments = "{alias}.name like :name")
    private String name;

    private String phone;

    @DirectJPQL(
            jpqlFragments = "{alias}.code like :any " +
                    "or {alias}.name like :any " +
                    "or {alias}.address like :any " +
                    "or {alias}.phone like :any" +
                    "or {alias}.postCode like :any"
    )
    private String any;

    @DirectJPQL(jpqlFragments = "order.orderNumber = :orderNumber")
    private String orderNumber;

    @DirectJPQL(jpqlFragments = "product.name = :productName")
    private String productName;

    public Long getId() {
        return id;
    }

    public CustomerCondition setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CustomerCondition setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public CustomerCondition setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAny() {
        return any;
    }

    public CustomerCondition setAny(String any) {
        this.any = JpqlUtils.addFuzzyness(any);
        return this;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public CustomerCondition setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public CustomerCondition setProductName(String productName) {
        this.productName = productName;
        return this;
    }
}
