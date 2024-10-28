# Get users from multiple databases

This is the test task for getting all the users from multiple databases. Currently working only with MySQL and PostgreSQL databases.
You can provide your database connection and table structure using ```application.yaml```.

### ```application.yaml``` structure:

```
data:
  sources:
    -   name: {datasorce_naming}
        strategy: {mysql/postgres} # optional parameter used for understanding connection source
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
`surname``` - returns all users with corresponding surname

All this parameters can be used together or in some combinations. 

Response is returned in JSON format and looks like this:

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

For more info of endpoint check [Swagger URL](http://localhost:8080/swagger-ui/index.html) while application is running.

### The application runs on port ```8080```

This application also contains integration tests that utilize the ```Testcontainers``` library. If you want them to work properly, install and run ```Docker``` app in background.
