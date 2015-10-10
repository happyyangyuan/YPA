package ypa.model.customer.advanced0;


import ypa.annotation.jpql.DirectJPQL;
import ypa.annotation.jpql.DirectJPQLForBoolean;
import ypa.annotation.jpql.InnerJoin;
import ypa.annotation.jpql.LeftOuterJoin;
import ypa.utils.JpqlUtils;

import java.io.Serializable;

//left-join condition
@LeftOuterJoin(outerJoinAliases = {"order", "product"}, propertyNames = {"orders", "order.products"})
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

    @DirectJPQLForBoolean(jpqlFragmentsWhenTrue = "order is not empty",jpqlFragmentsWhenFalse = "order is empty")
    private Boolean withOrders;

    @DirectJPQLForBoolean(jpqlFragmentsWhenTrue = "product is not empty",jpqlFragmentsWhenFalse = "product is empty")
    private Boolean withProducts;

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

    public Boolean getWithOrders() {
        return withOrders;
    }

    public CustomerCondition setWithOrders(Boolean withOrders) {
        this.withOrders = withOrders;
        return this;
    }

    public Boolean getWithProducts() {
        return withProducts;
    }

    public CustomerCondition setWithProducts(Boolean withProducts) {
        this.withProducts = withProducts;
        return this;
    }
}
