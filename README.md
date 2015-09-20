# YPA
##What is YPA?
YPA is a simple extension of JPA. It helps us create reusable conditional query.
YPA helps you simplify your DAO code a lot. In fact you need to write only 3 classes to fulfill a powerfull DAO:
* An entity class: entity class is always a must for JPA.
* A condition class: it holds all the query conditions as properties in it.
* A DAO class exends AbstractYpaDao.

##Advantages of YPA
1. The CRUD methods in YPA DAO is strongly resuable and extensiable.
2. Safe to extend without polluting the former CRUD especially the 'R'(the query).
3. Very flexiable,by now it can meet all my needs in my business projects.
4. Less code to write.Only 3 classes as mentioned above.

Once upon a time I was used to the JPA criteria query, only to find that it is rather boring to write repeated query method for every different query condition.
Even worse, every time you want to extend the query method with one or more named parameters,you would find that it is barely possible to make it done without
polluting the former query,thus you had to add a new method to make your work done.Time and time again,your dao class became a mess.

With YPA, you don't need to use JPA criteria query anymore, you don't need to write every JPQL for each query method.The only thing you need is a single reusable annotated condition class for all the queries under certain JPA entity.
The condition class is highly resuable,extensiable and flexiable.

##Example usage
Entity class:
`    @Entity
    public class Customer extends Serializable{
    	private String code;
    	private String name;
    	private String fullNameInChinese;
    	private String fullNameInEnglish;
    	private String phone;
    	private String address;
    	private String postCode;
    	private String fax;
    	private String contactMan;
    	private String email;
    ...
    }
`
