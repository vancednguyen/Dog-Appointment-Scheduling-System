package edu.sjsu.cmpe172.Doggy.repository;
import edu.sjsu.cmpe172.Doggy.model.ServiceOffering;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ServiceOfferingRepository {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public void insertService(ServiceOffering service) {
        String sql = "INSERT INTO services (provider_id, service_name, service_duration, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, service.getProviderId());
            stmt.setString(2, service.getServiceName());
            stmt.setInt(3, service.getServiceDuration());
            stmt.setDouble(4, service.getPrice());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting service", e);
        }
    }

    public List<ServiceOffering> findByProviderId(Long providerId) {
        String sql = "SELECT service_id, provider_id, service_name, service_duration, price " +
                "FROM services WHERE provider_id = ? ORDER BY service_name";

        List<ServiceOffering> services = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, providerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ServiceOffering service = new ServiceOffering();
                service.setServiceId(rs.getLong("service_id"));
                service.setProviderId(rs.getLong("provider_id"));
                service.setServiceName(rs.getString("service_name"));
                service.setServiceDuration(rs.getInt("service_duration"));
                service.setPrice(rs.getDouble("price"));
                services.add(service);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding services by providerId", e);
        }

        return services;
    }
}