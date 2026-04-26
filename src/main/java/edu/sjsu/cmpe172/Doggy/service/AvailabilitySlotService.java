package edu.sjsu.cmpe172.Doggy.service;

import org.springframework.stereotype.Service;
import edu.sjsu.cmpe172.Doggy.repository.AvailabilitySlotRepository;
import edu.sjsu.cmpe172.Doggy.model.AvailabilitySlot;
import java.util.List;

@Service
public class AvailabilitySlotService {

    private final AvailabilitySlotRepository availabilitySlotRepository;

    public AvailabilitySlotService(AvailabilitySlotRepository availabilitySlotRepository) {
        this.availabilitySlotRepository = availabilitySlotRepository;
    }

    public void addSlot(AvailabilitySlot slot) {
        if (slot.getProviderId() == null) {
            throw new IllegalArgumentException("Provider is required.");
        }
        if (slot.getServiceId() == null) {
            throw new IllegalArgumentException("Service is required.");
        }
        if (slot.getSlotDate() == null || slot.getSlotDate().isBlank()) {
            throw new IllegalArgumentException("Date is required.");
        }
        if (slot.getStartTime() == null || slot.getStartTime().isBlank()) {
            throw new IllegalArgumentException("Start time is required.");
        }
        if (slot.getEndTime() == null || slot.getEndTime().isBlank()) {
            throw new IllegalArgumentException("End time is required.");
        }

        slot.setStatus("AVAILABLE");
        availabilitySlotRepository.insertAvailabilitySlot(slot);
    }

    public List<AvailabilitySlot> getProviderSlots(Long providerId) {
        return availabilitySlotRepository.findByProviderId(providerId);
    }
}