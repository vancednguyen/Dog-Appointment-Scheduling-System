package edu.sjsu.cmpe172.Doggy.repository;

import edu.sjsu.cmpe172.Doggy.model.AvailabilitySlot;
import edu.sjsu.cmpe172.Doggy.model.Provider;
import edu.sjsu.cmpe172.Doggy.model.ServiceOffering;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookingRepository {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public List<Provider> findProvidersByDate(String date) {
        String sql = "SELECT DISTINCT p.provider_id, p.first_name, p.last_name, p.phone_number, p.email, p.password, p.address, p.name " +
                "FROM providers p " +
                "JOIN availability_slot av ON p.provider_id = av.provider_id " +
                "WHERE av.slot_date = ? AND av.status = 'AVAILABLE' " +
                "ORDER BY p.name";

        List<Provider> providers = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Provider provider = new Provider();
                provider.setProviderId(rs.getLong("provider_id"));
                provider.setFirstName(rs.getString("first_name"));
                provider.setLastName(rs.getString("last_name"));
                provider.setPhoneNumber(rs.getString("phone_number"));
                provider.setEmail(rs.getString("email"));
                provider.setPassword(rs.getString("password"));
                provider.setAddress(rs.getString("address"));
                provider.setName(rs.getString("name"));
                providers.add(provider);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading providers by date", e);
        }

        return providers;
    }

    public List<AvailabilitySlot> findAvailableSlotsByDate(String date) {
        String sql = "SELECT slot_id, provider_id, service_id, slot_date, start_time, end_time, status " +
                "FROM availability_slot " +
                "WHERE slot_date = ? AND status = 'AVAILABLE' " +
                "ORDER BY start_time";

        List<AvailabilitySlot> slots = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AvailabilitySlot slot = new AvailabilitySlot();
                slot.setSlotId(rs.getLong("slot_id"));
                slot.setProviderId(rs.getLong("provider_id"));
                slot.setServiceId(rs.getLong("service_id"));
                slot.setSlotDate(rs.getDate("slot_date").toString());
                slot.setStartTime(rs.getTime("start_time").toString());
                slot.setEndTime(rs.getTime("end_time").toString());
                slot.setStatus(rs.getString("status"));
                slots.add(slot);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading available slots", e);
        }

        return slots;
    }

    public List<ServiceOffering> findServicesByDate(String date) {
        String sql = "SELECT DISTINCT s.service_id, s.provider_id, s.service_name, s.service_duration, s.price " +
                "FROM services s " +
                "JOIN availability_slot av ON s.service_id = av.service_id " +
                "WHERE av.slot_date = ? AND av.status = 'AVAILABLE' " +
                "ORDER BY s.service_name";

        List<ServiceOffering> services = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
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
            throw new RuntimeException("Error loading services by date", e);
        }

        return services;
    }

    public String findDogNameByUserId(Long userId) {
        String sql = "SELECT dog_name FROM users WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("dog_name");
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error loading dog name", e);
        }
    }

    public void insertAppointment(Long userId, Long providerId, Long serviceId, Long slotId) {
        String sql = "INSERT INTO appointments (user_id, provider_id, service_id, slot_id, status) " +
                "VALUES (?, ?, ?, ?, 'BOOKED')";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setLong(2, providerId);
            stmt.setLong(3, serviceId);
            stmt.setLong(4, slotId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting appointment", e);
        }
    }

    public void markSlotBooked(Long slotId) {
        String sql = "UPDATE availability_slot SET status = 'BOOKED' WHERE slot_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, slotId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error marking slot booked", e);
        }
    }

    public boolean isSlotStillAvailable(Long slotId) {
        String sql = "SELECT COUNT(*) FROM availability_slot WHERE slot_id = ? AND status = 'AVAILABLE'";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, slotId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error checking slot availability", e);
        }
    }
    public List<String> findAvailableDates() {
        String sql = "SELECT DISTINCT slot_date " +
                "FROM availability_slot " +
                "WHERE status = 'AVAILABLE' " +
                "ORDER BY slot_date";

        List<String> dates = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dates.add(rs.getDate("slot_date").toString());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading available dates", e);
        }

        return dates;
    }
}