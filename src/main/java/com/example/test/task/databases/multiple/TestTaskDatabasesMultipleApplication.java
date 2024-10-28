package com.example.test.task.databases.multiple;

import com.example.test.task.databases.multiple.configuration.DataSourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties(DataSourceConfig.class)
public class TestTaskDatabasesMultipleApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestTaskDatabasesMultipleApplication.class, args);
	}

}
