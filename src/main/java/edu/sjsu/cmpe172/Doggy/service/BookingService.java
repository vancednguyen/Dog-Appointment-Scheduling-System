package edu.sjsu.cmpe172.Doggy.service;

import edu.sjsu.cmpe172.Doggy.model.AvailabilitySlot;
import edu.sjsu.cmpe172.Doggy.model.Provider;
import edu.sjsu.cmpe172.Doggy.model.ProviderBookingView;
import edu.sjsu.cmpe172.Doggy.model.ServiceOffering;
import edu.sjsu.cmpe172.Doggy.repository.BookingRepository;
import org.springframework.stereotype.Service;
import edu.sjsu.cmpe172.Doggy.model.UserBookingView;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final NotificationClientService notificationClientService;

    public BookingService(BookingRepository bookingRepository, NotificationClientService notificationClientService)
    {
        this.bookingRepository = bookingRepository;
        this.notificationClientService = notificationClientService;
    }

    public List<Provider> getProvidersByDate(String date) {
        return bookingRepository.findProvidersByDate(date);
    }

    public Provider getProviderById(Long providerId) {
        return bookingRepository.findProviderById(providerId);
    }

    public List<AvailabilitySlot> getSlotsByDateAndProvider(String date, Long providerId) {
        return bookingRepository.findAvailableSlotsByDateAndProvider(date, providerId);
    }

    public List<ServiceOffering> getServicesByDateAndProvider(String date, Long providerId) {
        return bookingRepository.findServicesByDateAndProvider(date, providerId);
    }

    public String getDogNameByUserId(Long userId) {
        return bookingRepository.findDogNameByUserId(userId);
    }

    @Transactional
    public void createBooking(Long userId, Long providerId, Long serviceId, Long slotId, String date) {

        if (!bookingRepository.isSlotValidForProviderAndDate(slotId, providerId, date)) {
            throw new IllegalArgumentException("Selected time slot does not belong to that provider/date.");
        }

        if (!bookingRepository.isServiceValidForProviderAndDate(serviceId, providerId, date)) {
            throw new IllegalArgumentException("Selected service does not belong to that provider/date.");
        }

        AvailabilitySlot slot = bookingRepository.findSlotById(slotId);

        if (slot == null) {
            throw new IllegalArgumentException("Slot not found.");
        }

        if (!"AVAILABLE".equalsIgnoreCase(slot.getStatus())) {
            throw new IllegalArgumentException("That slot is no longer available.");
        }

        int updatedRows = bookingRepository.bookSlotWithVersion(
                slotId,
                providerId,
                serviceId,
                slot.getVersion()
        );

        if (updatedRows == 0) {
            throw new IllegalStateException("That slot was just booked by another user. Please choose another time.");
        }

        bookingRepository.insertAppointment(userId, providerId, serviceId, slotId);
    }

    public List<String> getAvailableDates() {
        return bookingRepository.findAvailableDates();
    }

    public List<ProviderBookingView> getBookingsByProviderId(Long providerId) {
        return bookingRepository.findBookingsByProviderId(providerId);
    }
    public ProviderBookingView getBookingByAppointmentId(Long appointmentId, Long providerId) {
        return bookingRepository.findBookingByAppointmentIdAndProviderId(appointmentId, providerId);
    }

    public List<AvailabilitySlot> getEditableSlotsByProviderId(Long providerId, Long currentSlotId) {
        return bookingRepository.findEditableSlotsByProviderId(providerId, currentSlotId);
    }

    public List<ServiceOffering> getServicesByProviderId(Long providerId) {
        return bookingRepository.findServicesByProviderId(providerId);
    }

    public void updateProviderBooking(Long appointmentId, Long providerId, Long serviceId, Long newSlotId) {
        ProviderBookingView currentBooking =
                bookingRepository.findBookingByAppointmentIdAndProviderId(appointmentId, providerId);

        if (currentBooking == null) {
            throw new IllegalArgumentException("Booking not found.");
        }

        if (!"BOOKED".equalsIgnoreCase(currentBooking.getStatus())) {
            throw new IllegalArgumentException("Only booked appointments can be edited.");
        }

        if (newSlotId.equals(currentBooking.getSlotId())) {
            if (!currentBooking.getServiceId().equals(serviceId)) {
                throw new IllegalArgumentException("That time slot does not match the selected service.");
            }
            bookingRepository.updateAppointment(appointmentId, providerId, serviceId, newSlotId);
            return;
        }

        if (!bookingRepository.isSlotStillAvailable(newSlotId)) {
            throw new IllegalArgumentException("Selected new time slot is no longer available.");
        }

        if (!bookingRepository.isSlotValidForProviderAndService(newSlotId, providerId, serviceId)) {
            throw new IllegalArgumentException("Selected slot does not belong to that provider/service.");
        }

        bookingRepository.markSlotAvailable(currentBooking.getSlotId());
        bookingRepository.updateAppointment(appointmentId, providerId, serviceId, newSlotId);
        bookingRepository.markSlotBooked(newSlotId);
    }

    public void cancelProviderBooking(Long appointmentId, Long providerId) {
        ProviderBookingView currentBooking =
                bookingRepository.findBookingByAppointmentIdAndProviderId(appointmentId, providerId);

        if (currentBooking == null) {
            throw new IllegalArgumentException("Booking not found.");
        }

        if (!"BOOKED".equalsIgnoreCase(currentBooking.getStatus())) {
            throw new IllegalArgumentException("Only booked appointments can be canceled.");
        }

        bookingRepository.updateAppointmentStatus(appointmentId, providerId, "CANCELED");
        bookingRepository.markSlotAvailable(currentBooking.getSlotId());
    }

    public void completeProviderBooking(Long appointmentId, Long providerId) {
        ProviderBookingView currentBooking =
                bookingRepository.findBookingByAppointmentIdAndProviderId(appointmentId, providerId);

        if (currentBooking == null) {
            throw new IllegalArgumentException("Booking not found.");
        }

        if (!"BOOKED".equalsIgnoreCase(currentBooking.getStatus())) {
            throw new IllegalArgumentException("Only booked appointments can be completed.");
        }

        bookingRepository.updateAppointmentStatus(appointmentId, providerId, "COMPLETED");
    }
    public List<UserBookingView> getBookingsByUserId(Long userId) {
        return bookingRepository.findBookingsByUserId(userId);
    }

    public void cancelUserBooking(Long appointmentId, Long userId) {
        UserBookingView booking = bookingRepository.findBookingByAppointmentIdAndUserId(appointmentId, userId);

        if (booking == null) {
            throw new IllegalArgumentException("Booking not found.");
        }

        if (!"BOOKED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Only booked appointments can be canceled.");
        }

        Long slotId = bookingRepository.findSlotIdByAppointmentIdAndUserId(appointmentId, userId);
        if (slotId == null) {
            throw new IllegalArgumentException("Associated slot not found.");
        }

        bookingRepository.updateAppointmentStatusByUser(appointmentId, userId, "CANCELED");
        bookingRepository.markSlotAvailable(slotId);
    }
}
