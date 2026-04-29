package edu.sjsu.cmpe172.Doggy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new LinkedHashMap<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            result.put("status", "UP");
            result.put("database", "UP");
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("database", "DOWN");
            result.put("error", "Database connection failed");
        }

        result.put("application", "Lu's Doggy Services");
        return result;
    }
}