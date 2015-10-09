# YPA
##What is YPA?
YPA is a simple extension of JPA. It helps you to create reusable conditional queries,to simplify your DAO code a lot. In fact you need to write only 3 classes to fulfill a powerfull DAO:
* An entity class: entity class is always a must for JPA.
* A condition class: it holds all the query conditions as properties in it.
* A DAO class extends AbstractYpaDao.

##Advantages of YPA
1. The CRUD methods in YPA DAO are strongly reusable and extensible.
2. Safe to extend without polluting the former CRUD especially the 'R'(the query).
3. Very flexible,by now it can meet all my needs in my business projects.
4. Less code to write.Only 3 classes as mentioned above.

Once upon a time I was used to the JPA criteria query, only to find that it is rather boring to write repeated query method for every different query condition.
Even worse, every time you want to extend the query method with one or more named parameters,you would find that it is barely possible to make it done without
polluting the former query,thus you had to add a new method to make your work done.Time and time again,your dao class became a mess.

With YPA, you don't need to use JPA criteria query anymore, you don't need to write every JPQL for each query method.The only thing you need is a single reusable annotated condition class for all the queries under certain JPA entity.
The condition class is highly reusable,extensible and flexible.

##Example usage
###Example 1 : basic query for customers.
Assume we get a table "customer" in the database.Below is an simple dao structure:  entity + condition + dao.  
Entity class:
```
@Entity
public class Customer extends Serializable{
	@Id
	private Long id;
	private String code;
	private String name;
	private String phone;
	private String address;
	private String postCode;
	private String fax;
	private String contactMan;
	private String email;
    ...
}
```
Condition class:
```
public class CustomerCondition implements Serializable {
    private Long id;
    private String name;
    private String phone;
    //getters and setters ignored here ...
}
```
DAO class:
```
public class CustomerDao extends AbstractJpaDao implements ICustomerDao {
    public List<Customer> queryCustomers(CustomerCondition condition) {
        return query(Customer.class,condition);
    }
}
```
Test:
```
public class CustomerDaoJUnitTest ...{
    //properties ignored ...
    @Test
    public void test(){
    	//query customers whos name equals "Joe" and phone equals "911",both conditions should be perfect matched.
        List<Customer> cs1 = customerDao.query(
            Customer.class, 
            new CustomerCondition().setName("Joe").setPhone('911')
        );
        //query all the customers in the customer table.
        List<Customer> cs2 = customerDao.query(Customer.class, null);
    }
    ...
}
```
Let's take a look at the condition class.Properties in it have the same name to the entity's.This is the simplest way to tell YPA we want perfect match with the db.The condition query follows the principle of "null to query all" and "null to ignore".
If you want to run the unit test,you have to provide your persistence.xml and the datasource with table 'customer'.
###Example 2 : a simple fuzzy query (like query)
Entity class :  
```
The same as above
```  
Condition class:
```
public class CustomerCondition_fuzzyName implements Serializable {
    private Long id;
    @DirectJPQL(jpqlFragments = "{alias}.name like :name")
    private String name;
    private String phone;
...
}
```
Dao class:  
```
The same as CustomerDao
```  
Test class:
```
public class CustomerDaoTest1 extends AbstractDaoJUnitTest{
    @Test
    public void test(){
        //query customers whos name contains "Yang".Please refer to class CustomerCondition_fuzzyName's name property for jpql detail.
        List<Customer> cs = dao.query(Customer.class, new CustomerCondition_fuzzyName().setName("Yang"));
        System.out.println(cs.size());
    }
    ...
}
```
Please take a look at the annotation on class CustomerCondition_fuzzyName's name property:
```@DirectJPQL(jpqlFragments = "{alias}.name like :name") private String name;```
* The```@DirectJPQL``` annotation: if name is not an empty string or null, the like jpql fragment shall placed in the where clause to make a like query;
* ```{alias}```: represents the entity class;
* ```:name```: is the named parameter;
* The final JPQL: ```select alias from Customer as alias where alias.name like :name```.

###Example 3 : process a full text query (like query)
```
public class CustomerCondition_fuzzyAny implements Serializable {

    @DirectJPQL(
            jpqlFragments = "{alias}.code like :any " +
                    "or {alias}.name like :any " +
                    "or {alias}.address like :any " +
                    "or {alias}.phone like :any"+
                    "or {alias}.postCode like :any"
    )
    private String any;
    ...
}

```
Ypa will generate the JPQL:
```
select alias 
from Customer as alias 
where alias.name like :any
or alias.address like :any
or alias.phone like :any
or alias.postCode like :any
```


###Example 4 (inner) join


More powerful queries will be described later. Coming soon...
