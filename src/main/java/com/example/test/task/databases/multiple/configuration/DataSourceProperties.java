package com.example.test.task.databases.multiple.configuration;

import java.util.Map;

public record DataSourceProperties(String name,
                                   String strategy,
                                   String url,
                                   String table,
                                   String user,
                                   String password,
                                   Map<String, String> mapping) {
}
