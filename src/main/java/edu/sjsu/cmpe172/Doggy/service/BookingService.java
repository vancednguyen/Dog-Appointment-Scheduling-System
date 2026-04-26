package edu.sjsu.cmpe172.Doggy.service;

import edu.sjsu.cmpe172.Doggy.model.AvailabilitySlot;
import edu.sjsu.cmpe172.Doggy.model.Provider;
import edu.sjsu.cmpe172.Doggy.model.ServiceOffering;
import edu.sjsu.cmpe172.Doggy.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Provider> getProvidersByDate(String date) {
        return bookingRepository.findProvidersByDate(date);
    }

    public List<AvailabilitySlot> getSlotsByDate(String date) {
        return bookingRepository.findAvailableSlotsByDate(date);
    }

    public List<ServiceOffering> getServicesByDate(String date) {
        return bookingRepository.findServicesByDate(date);
    }

    public String getDogNameByUserId(Long userId) {
        return bookingRepository.findDogNameByUserId(userId);
    }

    public void createBooking(Long userId, Long providerId, Long serviceId, Long slotId) {
        if (!bookingRepository.isSlotStillAvailable(slotId)) {
            throw new IllegalArgumentException("That slot is no longer available.");
        }

        bookingRepository.insertAppointment(userId, providerId, serviceId, slotId);
        bookingRepository.markSlotBooked(slotId);
    }
    public List<String> getAvailableDates() {
        return bookingRepository.findAvailableDates();
    }
}