package edu.sjsu.cmpe172.Doggy.service;

import edu.sjsu.cmpe172.Doggy.integration.NotificationClient;
import edu.sjsu.cmpe172.Doggy.model.Appointment;
import edu.sjsu.cmpe172.Doggy.model.AppointmentBookingResponse;
import edu.sjsu.cmpe172.Doggy.model.NotificationRequest;
import edu.sjsu.cmpe172.Doggy.model.NotificationResponse;
import edu.sjsu.cmpe172.Doggy.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository repo;
    private final NotificationClient notificationClient;

    public AppointmentService(AppointmentRepository repo, NotificationClient notificationClient) {
        this.repo = repo;
        this.notificationClient = notificationClient;
    }

    public List<Appointment> getAllAppointments() {
        return repo.findAll();
    }

    public Appointment getAppointmentOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + id));
    }

    public Appointment addAppointment(Appointment appointment) {
        validateAppointment(appointment);
        applyDefaults(appointment);
        return repo.save(appointment);
    }

    public AppointmentBookingResponse bookAndSendConfirmation(Appointment request) {
        validateAppointment(request);
        applyDefaults(request);

        Appointment savedAppointment = repo.save(request);

        NotificationRequest notificationRequest = new NotificationRequest(
                savedAppointment.getId(),
                savedAppointment.getUserId(),
                savedAppointment.getProviderId(),
                savedAppointment.getSlotId(),
                "Your appointment has been confirmed."
        );

        NotificationResponse notificationResponse =
                notificationClient.sendAppointmentConfirmation(notificationRequest);

        return new AppointmentBookingResponse(savedAppointment, notificationResponse);
    }

    private void validateAppointment(Appointment appointment) {
        if (appointment.getUserId() == null || appointment.getUserId().isBlank()
                || appointment.getProviderId() == null || appointment.getProviderId().isBlank()
                || appointment.getServiceId() == null || appointment.getServiceId().isBlank()
                || appointment.getSlotId() == null || appointment.getSlotId().isBlank()) {
            throw new IllegalArgumentException("userId, providerId, serviceId, and slotId are required");
        }
    }

    private void applyDefaults(Appointment appointment) {
        if (appointment.getStatus() == null || appointment.getStatus().isBlank()) {
            appointment.setStatus("BOOKED");
        }
    }
}
