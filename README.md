# YPA
This is simply an extension of JPA usage which helps us to create reusable condition query.I name it YPA, short cut for YY'JPA.

Once upon a time I was used to the JPA criteria query, only to find that it is rather boring to write repeated query method for every different query condition.
Even worse, every time you want to extend the query method with one or more named parameters,you would find that it is barely possible to make it done without
polluting the former query,thus you had to add a new method to make your work done.Time and time again,your dao class became a mess.

With YPA, you don't need to use JPA criteria query anymore, and you don't need to write every JPQL for each query method.The only thing you need is a single reusable
condition class for all the queries under certain JPA entity.
You just add properties in your
query condition class for common query scenes without polluting the former query.


##Example usage

Coming soon...