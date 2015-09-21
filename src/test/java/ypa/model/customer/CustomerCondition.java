package ypa.model.customer;


import ypa.annotation.jpql.DirectJPQL;
import ypa.utils.JpqlUtils;

import java.io.Serializable;

public class CustomerCondition implements Serializable {
    private Long id;
    @DirectJPQL(jpqlFragments = "{alias}.name like :name")
    private String name;
    private String phone;
    private String any;

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
        this.name = JpqlUtils.addFuzzyness(name);
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

    public void setAny(String any) {
        this.any = any;
    }
}
