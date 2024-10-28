package com.example.test.task.databases.multiple.service.impl;

import com.example.test.task.databases.multiple.configuration.DataSourceConfig;
import com.example.test.task.databases.multiple.configuration.DataSourceProperties;
import com.example.test.task.databases.multiple.model.User;
import com.example.test.task.databases.multiple.service.UserAggregationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserAggregationServiceImpl implements UserAggregationService {

    private static final Logger logger = LoggerFactory.getLogger(UserAggregationService.class);

    @Autowired
    private DataSourceConfig dataSourceConfig;

    public ResponseEntity<List<User>> getAllUsers(String id, String username, String name, String surname) {
        List<User> users = new ArrayList<>();
        for (DataSourceProperties source : dataSourceConfig.sources()) {
            users.addAll(fetchUsersFromDataSource(source, id, username, name, surname));
        }
        logger.info("Successfully aggregated {} users from all sources.", users.size());
        return ResponseEntity.ok(users);
    }

    private List<User> fetchUsersFromDataSource(DataSourceProperties source, String id, String username, String name, String surname) {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(source.url(), source.user(), source.password());
             Statement statement = connection.createStatement()) {

            StringBuilder queryBuilder = new StringBuilder("SELECT ");
            queryBuilder.append(source.mapping().get("id")).append(" AS id, ");
            queryBuilder.append(source.mapping().get("username")).append(" AS username, ");
            queryBuilder.append(source.mapping().get("name")).append(" AS name, ");
            queryBuilder.append(source.mapping().get("surname")).append(" AS surname ");
            queryBuilder.append("FROM ").append(source.table());

            boolean hasCondition = false;
            if (id != null) {
                queryBuilder.append(" WHERE ");
                queryBuilder.append(source.mapping().get("id")).append(" = '").append(id).append("'");
                hasCondition = true;
            }
            if (username != null) {
                queryBuilder.append(hasCondition ? " AND " : " WHERE ");
                queryBuilder.append(source.mapping().get("username")).append(" LIKE '%").append(username).append("%'");
                hasCondition = true;
            }
            if (name != null) {
                queryBuilder.append(hasCondition ? " AND " : " WHERE ");
                queryBuilder.append(source.mapping().get("name")).append(" LIKE '%").append(name).append("%'");
                hasCondition = true;
            }
            if (surname != null) {
                queryBuilder.append(hasCondition ? " AND " : " WHERE ");
                queryBuilder.append(source.mapping().get("surname")).append(" LIKE '%").append(surname).append("%'");
            }

            ResultSet resultSet = statement.executeQuery(queryBuilder.toString());
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getString("id"),
                        resultSet.getString("username"),
                        resultSet.getString("name"),
                        resultSet.getString("surname")
                ));
            }
            logger.info("Successfully fetched data from database: {}", source.name());
        } catch (Exception e) {
            logger.error("Failed to fetch data from database {}: {}", source.name(), e.getMessage());
        }
        return users;
    }
}

