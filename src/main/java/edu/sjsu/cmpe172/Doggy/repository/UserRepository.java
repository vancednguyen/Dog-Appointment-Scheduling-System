package edu.sjsu.cmpe172.Doggy.repository;
import edu.sjsu.cmpe172.Doggy.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class UserRepository {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public User findByEmail(String email) {
        String sql = "SELECT user_id, first_name, last_name, email, phone_number, password, address, dog_name, dog_breed, dog_age " +
                "FROM users WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setPassword(rs.getString("password"));
                user.setAddress(rs.getString("address"));
                user.setDogName(rs.getString("dog_name"));
                user.setDogBreed(rs.getString("dog_breed"));
                int dogAge = rs.getInt("dog_age");
                user.setDogAge(rs.wasNull() ? null : dogAge);
                return user;
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email", e);
        }
    }

    public void insertUser(User user) {
        String sql = "INSERT INTO users " +
                "(first_name, last_name, email, phone_number, password, address, dog_name, dog_breed, dog_age) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getAddress());
            stmt.setString(7, user.getDogName());
            stmt.setString(8, user.getDogBreed());

            if (user.getDogAge() != null) {
                stmt.setInt(9, user.getDogAge());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user", e);
        }
    }
}
