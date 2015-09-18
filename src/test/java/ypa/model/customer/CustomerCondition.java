package ypa.model.customer;


import ypa.annotation.jpql.DirectJPQL;
import ypa.utils.JpqlUtils;

import java.io.Serializable;

public class CustomerCondition implements Serializable {

    private Long id;

    @DirectJPQL(jpqlFragments = "{alias}.name like :name or {alias}.fullNameInChinese like :name or  {alias}.fullNameInEnglish like :name")
    private String name;

    private String phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = JpqlUtils.addFuzzyness(name);
    }

    public String getPhone() {
        return phone;
    }

    public CustomerCondition setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
