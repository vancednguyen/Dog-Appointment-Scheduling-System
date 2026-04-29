package edu.sjsu.cmpe172.Doggy.repository;

import edu.sjsu.cmpe172.Doggy.model.AvailabilitySlot;
import edu.sjsu.cmpe172.Doggy.model.Provider;
import edu.sjsu.cmpe172.Doggy.model.ServiceOffering;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import edu.sjsu.cmpe172.Doggy.model.ProviderBookingView;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import edu.sjsu.cmpe172.Doggy.model.UserBookingView;

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
    public List<ProviderBookingView> findBookingsByProviderId(Long providerId) {

        String sql = "SELECT a.appointment_id, a.provider_id, a.service_id, a.slot_id, " +
                "CONCAT(u.first_name, ' ', u.last_name) AS user_name, " +
                "s.service_name, av.slot_date, av.start_time, av.end_time, a.status " +
                "FROM appointments a " +
                "JOIN users u ON a.user_id = u.user_id " +
                "JOIN services s ON a.service_id = s.service_id " +
                "JOIN availability_slot av ON a.slot_id = av.slot_id " +
                "WHERE a.provider_id = ? " +
                "ORDER BY av.slot_date, av.start_time";

        List<ProviderBookingView> bookings = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, providerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProviderBookingView b = new ProviderBookingView();

                b.setAppointmentId(rs.getLong("appointment_id"));
                b.setProviderId(rs.getLong("provider_id"));
                b.setServiceId(rs.getLong("service_id"));
                b.setSlotId(rs.getLong("slot_id"));
                b.setUserName(rs.getString("user_name"));
                b.setServiceName(rs.getString("service_name"));
                b.setSlotDate(rs.getDate("slot_date").toString());
                b.setStartTime(rs.getTime("start_time").toString());
                b.setEndTime(rs.getTime("end_time").toString());
                b.setStatus(rs.getString("status"));

                bookings.add(b);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading provider bookings", e);
        }

        return bookings;
    }
    public Provider findProviderById(Long providerId) {
        String sql = "SELECT provider_id, first_name, last_name, phone_number, email, password, address, name " +
                "FROM providers WHERE provider_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, providerId);
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
            throw new RuntimeException("Error loading provider", e);
        }
    }
    public List<AvailabilitySlot> findAvailableSlotsByDateAndProvider(String date, Long providerId) {
        String sql = "SELECT slot_id, provider_id, service_id, slot_date, start_time, end_time, status " +
                "FROM availability_slot " +
                "WHERE slot_date = ? AND provider_id = ? AND status = 'AVAILABLE' " +
                "ORDER BY start_time";

        List<AvailabilitySlot> slots = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setLong(2, providerId);
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
            throw new RuntimeException("Error loading available slots by provider/date", e);
        }

        return slots;
    }
    public List<ServiceOffering> findServicesByDateAndProvider(String date, Long providerId) {
        String sql = "SELECT DISTINCT s.service_id, s.provider_id, s.service_name, s.service_duration, s.price " +
                "FROM services s " +
                "JOIN availability_slot av ON s.service_id = av.service_id " +
                "WHERE av.slot_date = ? AND av.provider_id = ? AND av.status = 'AVAILABLE' " +
                "ORDER BY s.service_name";

        List<ServiceOffering> services = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setLong(2, providerId);
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
            throw new RuntimeException("Error loading services by provider/date", e);
        }

        return services;
    }
    public boolean isSlotValidForProviderAndDate(Long slotId, Long providerId, String date) {
        String sql = "SELECT COUNT(*) FROM availability_slot " +
                "WHERE slot_id = ? AND provider_id = ? AND slot_date = ? AND status = 'AVAILABLE'";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, slotId);
            stmt.setLong(2, providerId);
            stmt.setDate(3, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error validating slot/provider/date", e);
        }
    }
    public boolean isServiceValidForProviderAndDate(Long serviceId, Long providerId, String date) {
        String sql = "SELECT COUNT(*) " +
                "FROM availability_slot av " +
                "WHERE av.service_id = ? AND av.provider_id = ? AND av.slot_date = ? AND av.status = 'AVAILABLE'";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, serviceId);
            stmt.setLong(2, providerId);
            stmt.setDate(3, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error validating service/provider/date", e);
        }
    }
    public ProviderBookingView findBookingByAppointmentIdAndProviderId(Long appointmentId, Long providerId) {
        String sql = "SELECT a.appointment_id, a.provider_id, a.service_id, a.slot_id, " +
                "CONCAT(u.first_name, ' ', u.last_name) AS user_name, " +
                "s.service_name, av.slot_date, av.start_time, av.end_time, a.status " +
                "FROM appointments a " +
                "JOIN users u ON a.user_id = u.user_id " +
                "JOIN services s ON a.service_id = s.service_id " +
                "JOIN availability_slot av ON a.slot_id = av.slot_id " +
                "WHERE a.appointment_id = ? AND a.provider_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, appointmentId);
            stmt.setLong(2, providerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ProviderBookingView b = new ProviderBookingView();
                b.setAppointmentId(rs.getLong("appointment_id"));
                b.setProviderId(rs.getLong("provider_id"));
                b.setServiceId(rs.getLong("service_id"));
                b.setSlotId(rs.getLong("slot_id"));
                b.setUserName(rs.getString("user_name"));
                b.setServiceName(rs.getString("service_name"));
                b.setSlotDate(rs.getDate("slot_date").toString());
                b.setStartTime(rs.getTime("start_time").toString());
                b.setEndTime(rs.getTime("end_time").toString());
                b.setStatus(rs.getString("status"));
                return b;
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error loading booking", e);
        }
    }
    public List<AvailabilitySlot> findEditableSlotsByProviderId(Long providerId, Long currentSlotId) {
        String sql = "SELECT slot_id, provider_id, service_id, slot_date, start_time, end_time, status " +
                "FROM availability_slot " +
                "WHERE provider_id = ? AND (status = 'AVAILABLE' OR slot_id = ?) " +
                "ORDER BY slot_date, start_time";

        List<AvailabilitySlot> slots = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, providerId);
            stmt.setLong(2, currentSlotId);
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
            throw new RuntimeException("Error loading editable slots", e);
        }

        return slots;
    }
    public List<ServiceOffering> findServicesByProviderId(Long providerId) {
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
            throw new RuntimeException("Error loading provider services", e);
        }

        return services;
    }
    public boolean isSlotValidForProviderAndService(Long slotId, Long providerId, Long serviceId) {
        String sql = "SELECT COUNT(*) FROM availability_slot " +
                "WHERE slot_id = ? AND provider_id = ? AND service_id = ? " +
                "AND (status = 'AVAILABLE' OR status = 'BOOKED')";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, slotId);
            stmt.setLong(2, providerId);
            stmt.setLong(3, serviceId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error validating slot/service", e);
        }
    }
    public void updateAppointment(Long appointmentId, Long providerId, Long serviceId, Long slotId) {
        String sql = "UPDATE appointments SET service_id = ?, slot_id = ? " +
                "WHERE appointment_id = ? AND provider_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, serviceId);
            stmt.setLong(2, slotId);
            stmt.setLong(3, appointmentId);
            stmt.setLong(4, providerId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating appointment", e);
        }
    }
    public void updateAppointmentStatus(Long appointmentId, Long providerId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ? AND provider_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setLong(2, appointmentId);
            stmt.setLong(3, providerId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating appointment status", e);
        }
    }
    public void markSlotAvailable(Long slotId) {
        String sql = "UPDATE availability_slot SET status = 'AVAILABLE' WHERE slot_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, slotId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error marking slot available", e);
        }
    }
    public List<UserBookingView> findBookingsByUserId(Long userId) {

        String sql = "SELECT a.appointment_id, p.name AS provider_name, s.service_name, " +
                "av.slot_date, av.start_time, av.end_time, a.status " +
                "FROM appointments a " +
                "JOIN providers p ON a.provider_id = p.provider_id " +
                "JOIN services s ON a.service_id = s.service_id " +
                "JOIN availability_slot av ON a.slot_id = av.slot_id " +
                "WHERE a.user_id = ? " +
                "ORDER BY av.slot_date, av.start_time";

        List<UserBookingView> bookings = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UserBookingView b = new UserBookingView();
                b.setAppointmentId(rs.getLong("appointment_id"));
                b.setProviderName(rs.getString("provider_name"));
                b.setServiceName(rs.getString("service_name"));
                b.setSlotDate(rs.getDate("slot_date").toString());
                b.setStartTime(rs.getTime("start_time").toString());
                b.setEndTime(rs.getTime("end_time").toString());
                b.setStatus(rs.getString("status"));
                bookings.add(b);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error loading user bookings", e);
        }

        return bookings;
    }
    public UserBookingView findBookingByAppointmentIdAndUserId(Long appointmentId, Long userId) {

        String sql = "SELECT a.appointment_id, p.name AS provider_name, s.service_name, " +
                "av.slot_date, av.start_time, av.end_time, a.status " +
                "FROM appointments a " +
                "JOIN providers p ON a.provider_id = p.provider_id " +
                "JOIN services s ON a.service_id = s.service_id " +
                "JOIN availability_slot av ON a.slot_id = av.slot_id " +
                "WHERE a.appointment_id = ? AND a.user_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, appointmentId);
            stmt.setLong(2, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UserBookingView b = new UserBookingView();
                b.setAppointmentId(rs.getLong("appointment_id"));
                b.setProviderName(rs.getString("provider_name"));
                b.setServiceName(rs.getString("service_name"));
                b.setSlotDate(rs.getDate("slot_date").toString());
                b.setStartTime(rs.getTime("start_time").toString());
                b.setEndTime(rs.getTime("end_time").toString());
                b.setStatus(rs.getString("status"));
                return b;
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error loading booking for user", e);
        }
    }
    public Long findSlotIdByAppointmentIdAndUserId(Long appointmentId, Long userId) {

        String sql = "SELECT slot_id FROM appointments WHERE appointment_id = ? AND user_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, appointmentId);
            stmt.setLong(2, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("slot_id");
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error loading appointment slot id", e);
        }
    }
    public void updateAppointmentStatusByUser(Long appointmentId, Long userId, String status) {

        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ? AND user_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setLong(2, appointmentId);
            stmt.setLong(3, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user appointment status", e);
        }
    }

}