package com.example.test.task.databases.multiple.service;

import com.example.test.task.databases.multiple.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserAggregationService {

    ResponseEntity<List<User>> getAllUsers(String id, String username, String name, String surname);
}
