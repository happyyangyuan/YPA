package ypa.model.customer;

import ypa.annotation.jpql.DirectJPQL;
import ypa.utils.JpqlUtils;

import java.io.Serializable;

/**
 * Created by happyyangyuan on 15/9/21.
 */
public class CustomerCondition_fuzzyAny implements Serializable {

    @DirectJPQL(
            jpqlFragments = "{alias}.code like :any " +
                    "or {alias}.name like :any " +
                    "or {alias}.address like :any " +
                    "or {alias}.phone like :any"+
                    "or {alias}.postCode like :any"
    )
    private String any;

    public String getAny() {
        return any;
    }

    public CustomerCondition_fuzzyAny setAny(String any) {
        this.any = JpqlUtils.addFuzzyness(any);
        return this;
    }
}
