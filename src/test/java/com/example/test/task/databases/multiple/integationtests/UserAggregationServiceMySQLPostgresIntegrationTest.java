package com.example.test.task.databases.multiple.integationtests;

import com.example.test.task.databases.multiple.configuration.DataSourceConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class UserAggregationServiceMySQLPostgresIntegrationTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer1 = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("testdb1")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    private static final PostgreSQLContainer<?> mysqlContainer2 = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb2")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        System.setProperty("data.sources[0].name", "mysql");
        System.setProperty("data.sources[0].strategy", "");
        System.setProperty("data.sources[0].url", mysqlContainer1.getJdbcUrl());
        System.setProperty("data.sources[0].user", mysqlContainer1.getUsername());
        System.setProperty("data.sources[0].password", mysqlContainer1.getPassword());
        System.setProperty("data.sources[0].table", "users");
        System.setProperty("data.sources[0].mapping.id", "user_id");
        System.setProperty("data.sources[0].mapping.username", "login");
        System.setProperty("data.sources[0].mapping.name", "first_name");
        System.setProperty("data.sources[0].mapping.surname", "last_name");

        System.setProperty("data.sources[1].name", "postgres");
        System.setProperty("data.sources[1].strategy", "");
        System.setProperty("data.sources[1].url", mysqlContainer2.getJdbcUrl());
        System.setProperty("data.sources[1].user", mysqlContainer2.getUsername());
        System.setProperty("data.sources[1].password", mysqlContainer2.getPassword());
        System.setProperty("data.sources[1].table", "user_table");
        System.setProperty("data.sources[1].mapping.id", "user_id");
        System.setProperty("data.sources[1].mapping.username", "login");
        System.setProperty("data.sources[1].mapping.name", "first_name");
        System.setProperty("data.sources[1].mapping.surname", "last_name");
    }

    @BeforeAll
    public static void setUp(@Autowired DataSourceConfig dataSourceConfig) {


        Map<String, String> mapping1 = dataSourceConfig.sources().get(0).mapping();
        Map<String, String> mapping2 = dataSourceConfig.sources().get(1).mapping();

        try (Connection connection1 = mysqlContainer1.createConnection("");
             Statement statement1 = connection1.createStatement()) {
            statement1.execute("CREATE TABLE users (" +
                    mapping1.get("id") + " VARCHAR(50), " +
                    mapping1.get("username") + " VARCHAR(50), " +
                    mapping1.get("name") + " VARCHAR(50), " +
                    mapping1.get("surname") + " VARCHAR(50))");

            statement1.execute("INSERT INTO users VALUES ('1', 'user1', 'John', 'Doe')");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection connection2 = mysqlContainer2.createConnection("");
             Statement statement2 = connection2.createStatement()) {
            statement2.execute("CREATE TABLE user_table (" +
                    mapping2.get("id") + " VARCHAR(50), " +
                    mapping2.get("username") + " VARCHAR(50), " +
                    mapping2.get("name") + " VARCHAR(50), " +
                    mapping2.get("surname") + " VARCHAR(50))");

            statement2.execute("INSERT INTO user_table VALUES ('2', 'user2', 'Jane', 'Doe')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllUsers_returnsUsersFromFirstDatabase() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].surname").value("Doe"));
    }

    @Test
    public void getAllUsers_returnsUsersFromSecondDatabase() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$[1].name").value("Jane"))
                .andExpect(jsonPath("$[1].surname").value("Doe"));
    }

    @Test
    public void getAllUsers_combinesUsersFromBothDatabases() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    public void getAllUsers_withIdFilter() throws Exception {
        mockMvc.perform(get("/api/users").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].username").value("user1"));
    }

    @Test
    public void getAllUsers_withUsernameFilter() throws Exception {
        mockMvc.perform(get("/api/users").param("username", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"));
    }

    @Test
    public void getAllUsers_withCombinedFilters() throws Exception {
        mockMvc.perform(get("/api/users").param("username", "user1").param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].name").value("John"));
    }
}
