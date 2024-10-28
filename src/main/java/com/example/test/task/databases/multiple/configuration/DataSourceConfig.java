package com.example.test.task.databases.multiple.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
@ConfigurationProperties("data")
public record DataSourceConfig(List<DataSourceProperties> sources) {

}