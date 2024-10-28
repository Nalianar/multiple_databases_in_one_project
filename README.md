# Get users from multiple databases

This is the test task for getting all the users from multiple databases. Currentlu working only with MySQL and PostgreSQL databases.
You can provide your database connection and table structure using ```application.yaml```.

### ```application.yaml``` structure:

```
data:
  sources:
    -   name: {datasorce_naming}
        strategy: {mysql/postgres} # optional parametr used for undertanding connection souce
        url: {your_database_link}
        table: {table_name}
        user: {database_user_name}
        password: {database_user_password}
        mapping:
          id: {id_colunm_name}
          username: {username_column_name}
          name: {name_column_name}
          surname: {surname_column_name}
# Example of usage 
    -   name: data-base-2
        strategy: mysql
        url: jdbc:mysql://localhost:3306/locationapi
        table: users_new
        user: root
        password:
        mapping:
          id: id
          username: user_login
          name: name
          surname: surname
```
You can add as much database connections as you want, just use provided template.
If you want to add other relational database connections(not MySQL/PostgreSQL) do not forget to add corresponding driver to ```pom.xml```

## Endpoints

Currently this app contains only one endpoint ```GET /api/users``` with some request query params:

```id``` - returns all users with corresponding id

```username``` - returns all users with corresponding username

```name``` - returns all users with corresponding name

```surname``` - returns all users with corresponding surname

All this params can be used together or in some combinations. 

Responce is returned in json format and looks like this:

``` 
[
  {
    "id": "1",
    "username": "user1",
    "name": "John",
    "surname": "Doe"
  },
  {
    "id": "2",
    "username": "user2",
    "name": "Jane",
    "surname": "Doe"
}
] 
```

For more info of endpoint check [swagger url](http://localhost:8080/swagger-ui/index.html).

### The application runs on port ```8080```
