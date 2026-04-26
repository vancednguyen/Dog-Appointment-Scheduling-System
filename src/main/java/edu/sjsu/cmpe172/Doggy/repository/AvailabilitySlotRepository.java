package edu.sjsu.cmpe172.Doggy.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import edu.sjsu.cmpe172.Doggy.model.AvailabilitySlot;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AvailabilitySlotRepository {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public void insertAvailabilitySlot(AvailabilitySlot slot) {
        String sql = "INSERT INTO availability_slot " +
                "(provider_id, service_id, slot_date, start_time, end_time, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, slot.getProviderId());
            stmt.setLong(2, slot.getServiceId());
            stmt.setDate(3, Date.valueOf(slot.getSlotDate()));
            stmt.setTime(4, Time.valueOf(slot.getStartTime() + ":00"));
            stmt.setTime(5, Time.valueOf(slot.getEndTime() + ":00"));
            stmt.setString(6, slot.getStatus());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting availability slot", e);
        }
    }

    public List<AvailabilitySlot> findByProviderId(Long providerId) {
        String sql = "SELECT slot_id, provider_id, service_id, slot_date, start_time, end_time, status " +
                "FROM availability_slot WHERE provider_id = ? ORDER BY slot_date, start_time";

        List<AvailabilitySlot> slots = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, providerId);
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
            throw new RuntimeException("Error loading provider slots", e);
        }

        return slots;
    }
}
