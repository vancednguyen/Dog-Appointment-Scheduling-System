package edu.sjsu.cmpe172.Doggy.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import edu.sjsu.cmpe172.Doggy.model.Provider;
import java.sql.*;

@Repository
public class ProviderRepository {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public Provider findByEmail(String email) {
        String sql = "SELECT provider_id, first_name, last_name, phone_number, email, password, address, name " +
                "FROM providers WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Provider provider = new Provider();
                provider.setProviderId(rs.getLong("provider_id"));
                provider.setFirstName(rs.getString("first_name"));
                provider.setLastName(rs.getString("last_name"));
                provider.setPhoneNumber(rs.getString("phone_number"));
                provider.setEmail(rs.getString("email"));
                provider.setPassword(rs.getString("password"));
                provider.setAddress(rs.getString("address"));
                provider.setName(rs.getString("name"));
                return provider;
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding provider by email", e);
        }
    }

    public void insertProvider(Provider provider) {
        String sql = "INSERT INTO providers " +
                "(first_name, last_name, phone_number, email, password, address, name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, provider.getFirstName());
            stmt.setString(2, provider.getLastName());
            stmt.setString(3, provider.getPhoneNumber());
            stmt.setString(4, provider.getEmail());
            stmt.setString(5, provider.getPassword());
            stmt.setString(6, provider.getAddress());
            stmt.setString(7, provider.getName());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting provider", e);
        }
    }
}