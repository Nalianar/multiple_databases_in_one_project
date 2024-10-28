package com.example.test.task.databases.multiple.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id; //in case if we do not know type of id it should remain String
    private String username;
    private String name;
    private String surname;

}