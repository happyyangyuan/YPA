# YPA


##What is YPA?
YPA is a simple extension of JPA. It helps you to create reusable conditional queries,to simplify your DAO code a lot. In fact you need to write only 3 classes to fulfill a powerfull DAO:
* An entity class: entity class is always a must for JPA.
* A condition class: it holds all the query conditions as properties in it.
* A DAO class extends AbstractYpaDao.

##Preconditions
* YPA is for JPA developers.The basic concepts of JPA is a requirement.You can go to <a href="http://docs.oracle.com/javaee/6/tutorial/doc/bnbpz.html">here for JPA tutorial</a>.
* <a href="http://docs.oracle.com/javaee/6/tutorial/doc/bnbtg.html">JPQL language</a>.JPQL is short for Java Persistence query language.You should be familiar with it!
* The springframework JPA integration usage.(This is my suggested way to use YPA).

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
public class CustomerDaoTest ...{
    //properties ignored ...
    @Test
    public void test(){
    	//query customers whos name equals "Joe" and phone equals "911",both conditions should be perfect matched.
        List<Customer> cs1 = customerDao.queryCustomers(new CustomerCondition().setName("Joe").setPhone('911')
        );
        //query all the customers in the customer table.
        List<Customer> cs2 = customerDao.queryCustomers(null);
    }
    ...
}
```

Let's take a look at the condition class.Properties in it have the same name to the entity's.This is the simplest way to tell YPA we want perfect match with the db.The condition query follows the principle of "null to query all" and "null to ignore".
If you want to run the unit test,you have to provide your persistence.xml and the datasource with table 'customer'.




###Example 2 : a simple fuzzy query (like query)

Entity class :  ```The same as above```  

Condition class:
```
public class CustomerCondition implements Serializable {
    private Long id;
    @DirectJPQL(jpqlFragments = "{alias}.name like :name")
    private String name;
    private String phone;
...
}
```

Dao class:  ```The same as CustomerDao```  

Test class:
```
public class CustomerDaoTest extends AbstractDaoJUnitTest{
    @Test
    public void test(){
        //like query, query all customers who's name like "%Yang%"
        customerDao.queryCustomers(new CustomerCondition().setName("Yang"));
    }
    ...
}
```

Please take a look at the annotation on class CustomerCondition's name property:
 ```@DirectJPQL(jpqlFragments = "{alias}.name like :name")private String name;```
* The```@DirectJPQL``` annotation: if name is not an empty string or null, the like jpql fragment shall placed in the where clause to make a like query;
* ```{alias}```: represents the entity class;
* ```:name```: is the named parameter;
* The final JPQL: ```select alias from Customer as alias where alias.name like :name.```







###Example 3 : process a full text query (like query)

```
public class CustomerCondition implements Serializable {
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
Imagine that the customer has a one to many relation to Order entity.Now we want to get the customer who has the order with number "20151009".

Condition class:
```
@InnerJoin(innerJoinAliases = "order",propertyNames = "orders")
public class CustomerCondition implements Serializable {
	@DirectJPQL(jpqlFragments = "order.orderNumber = :orderNumber")
	private String orderNumber;
	...
}
```
Order entity class:
```
@Entity
public class Order implements Serializable {

    @Id
    private String orderNumber;
    ...
}
```
Customer entity class:
```
@Entity
public class Customer {
    ...
	@OneToMany
	private List<Order> orders;
	...
}
```
Test:
```
//join query
customerDao.queryCustomers(new CustomerCondition().setOrderNumber("20151009"));
```
Please take a look at the annotation  
 ```@InnerJoin(innerJoinAliases = "order",propertyNames = "orders")```.  
 It tell ypa to generate a jpql like this: ```inner join alias.orders as order```.  
Annotation```@DirectJPQL(jpqlFragments = "order.orderNumber = :orderNumber")```will give you the jpql:  
 ```where order.orderNumber = :orderNumber```.  
The final jpql is :
```
select distinct alias 
    from Customer as alias 
    inner join alias.orders as order 
where 
    order.orderNumber = :orderNumber
```
Sometimes we want to add 2 or more joined entities:  
 ```select ... from ... as alias inner join alias.orders as order inner join alias.xxxs as xxx where xxx.yyy = ...```  
To make this happen,just give array value to @InnerJoin annotation's parameters:  
 ```@InnerJoin(innerJoinAliases = {"order","xxx"},propertyNames = {"orders","xxxs")``` 
Multi-join query is illustrated below in example 5.
Note that, the join-annotations should be placed before the condition class definition,neither on properties nor methods.
And the jpql-annotations should and only should be placed before properties rather than methods.





###Example 5 multi-join
As mentioned above,we do sometimes need multi-join!
Imagine each of our customer has many orders,and each order has many products.Now we want to query the customers who has ordered product named "doll".How should we process the query with that 3 table relationship?
Check the annotated condition below:
```
//multi-join condition
@InnerJoin(innerJoinAliases = {"order", "product"}, propertyNames = {"orders", "order.products"})
//the propertyName "orders" is equal to "{alias}.orders" and "alias.orders",
//"{alias}" and "alias" represents the Customer entity as mentioned above,and if you omit it,ypa will add it for you.
//Property name "order.products" depends on alias "order",it should be placed after "orders".
public class CustomerCondition implements Serializable {
    ...
    @DirectJPQL(jpqlFragments = "product.name = :productName")
    private String productName;
}
```
Product entity class:
```
@Entity
public class Product implements Serializable {
    @Id
    private String name;
    ...
}
```
Order entity class:
```
@Entity
public class Order implements Serializable {
    ...
    @ManyToMany
    private List<Product> products;
    ...
}
```
Test:
```
//multi-join query
customerDao.queryCustomers(new ypa.model.customer.advanced.CustomerCondition().setProductName("doll"));
```
The final jpql is :
```
select distinct alias 
    from Customer as alias 
    inner join alias.orders as order
    inner join order.products as product
where 
    product.name = :productName
```
###Attention!  
1. Please pay attention to the "distinct" key word.Without it,we'll get repeated customers in the query result!
2. It is different with or without inner join clause in queries in JPA!  
Because of the oop entity in JPA,without join clause we can also query and get the customers and we can get the related order entities by accessing 
its "orders" property. It seems to be same with or without join clause.But many of the times they are different!
This is because when without any join clause,JPA will get you the related entities when present. Those main entities(Customer)who's related entities are absent are still
 involved in the query result list. The related entities are there as empty collections or null .But when the inner join clause is present,those main entities without the 
 joined relation will not be contained in the query result!
Eg. in example 1, the query result will involve customers who have or have no orders.But in example 4 and 5,only customers having orders are returned.
Sometimes we do not merely need inner join but also outer join!(left/right join as example).I'll illustrate how to use left join in example 6.





###Example 6 left(outer)join
First you should know that in JPA with or without inner join clause the result may be different,but with or without outer join the result is same!
Just use @LeftOuterJoin annotation:
```
//left-join condition
@LeftOuterJoin(outerJoinAliases = {"order", "product"}, propertyNames = {"orders", "order.products"})
public class CustomerCondition implements Serializable {
    ...
    @DirectJPQL(jpqlFragments = "product.name = :productName")
    private String productName;
}
```




###When shall we use left join instead of inner join?
* inner join omits results without joined records in database.If we don't want this happen,use left join!
* In a Composite condition class, we get a property on joined entity.And many of the times,we want ypa to ignore the where 
restriction on it so we set the property with a null value,but still we want those records who's joined records is empty.In such situation,left join is a must.
* In fact,in YPA,inner join query is subset function of left join query!That is to say we can use @LeftOuterJoin instead of @InnerJoin for all cases.Please refer to 
example 7.




###Example 7 boolean property in query condition


```
@LeftOuterJoin(outerJoinAliases = {"order", "product"}, propertyNames = {"orders", "order.products"})
public class CustomerCondition implements Serializable {
    ...
    @DirectJPQLForBoolean(jpqlFragmentsWhenTrue = "order is not empty",jpqlFragmentsWhenFalse = "orders is empty")
    private Boolean withOrders;
    
    @DirectJPQLForBoolean(jpqlFragmentsWhenTrue = "product is not empty",jpqlFragmentsWhenFalse = "product is empty")
    private Boolean withProducts;
}
```
Test:
```
//left join instead of inner join
customerDao.queryCustomers(new ypa.model.customer.advanced0.CustomerCondition().setWithOrders(true).setWithProducts(true));
```
The above left join in fact act the same functionality of inner join which can omit those customers who has no orders and no products:  
````@InnerJoin(innerJoinAliases = {"order", "product"}, propertyNames = {"orders", "order.products"})````  


More powerful queries will be described later. Coming soon...
