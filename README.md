# YPA
##What is YPA?
YPA is a simple extension of JPA. It helps you to create reusable conditional queries,to simplify your DAO code a lot. In fact you need to write only 3 classes to fulfill a powerfull DAO:
* An entity class: entity class is always a must for JPA.
* A condition class: it holds all the query conditions as properties in it.
* A DAO class exends AbstractYpaDao.

##Advantages of YPA
1. The CRUD methods in YPA DAO are strongly resuable and extensiable.
2. Safe to extend without polluting the former CRUD especially the 'R'(the query).
3. Very flexiable,by now it can meet all my needs in my business projects.
4. Less code to write.Only 3 classes as mentioned above.

Once upon a time I was used to the JPA criteria query, only to find that it is rather boring to write repeated query method for every different query condition.
Even worse, every time you want to extend the query method with one or more named parameters,you would find that it is barely possible to make it done without
polluting the former query,thus you had to add a new method to make your work done.Time and time again,your dao class became a mess.

With YPA, you don't need to use JPA criteria query anymore, you don't need to write every JPQL for each query method.The only thing you need is a single reusable annotated condition class for all the queries under certain JPA entity.
The condition class is highly resuable,extensiable and flexiable.

##Example usage
###Example 1 : basic query for customers.
Asumme we get a table "customer" in the database.Belowe is an simple dao structure:  entity + condition + dao.
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
public class CustomerDaoJUnitTest extends AbstractDaoJUnitTest{

    private ICustomerDao customerDao;
    
    @Test
    public void test(){
    	//query customers whos name equals "Joe" and phone equals "911",both conditions should be perfect matched.
        List<Customer> cs1 = customerDao.query(
            Customer.class, 
            new CustomerCondition()
                .setName("Joe")
                .setPhone('911')
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
``` The same as above```  
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
Dao class:```The same as CustomerDao```  
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

More powerfull queries will be discribed later. Coming soon...
