package edu.sjsu.cmpe172.Doggy.controller;

import edu.sjsu.cmpe172.Doggy.dto.BookingConfirmationRequest;
import edu.sjsu.cmpe172.Doggy.dto.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mock-notification")
public class NotificationServiceController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceController.class);

    @PostMapping("/booking-confirmation")
    public NotificationResponse sendBookingConfirmation(@RequestBody BookingConfirmationRequest request) {

        logger.info("MOCK NOTIFICATION SERVICE called for appointmentId={}, userEmail={}, provider={}, service={}, date={}, startTime={}",
                request.getAppointmentId(),
                request.getUserEmail(),
                request.getProviderName(),
                request.getServiceName(),
                request.getSlotDate(),
                request.getStartTime());

        return new NotificationResponse(
                "SENT",
                "Mock booking confirmation sent successfully."
        );
    }
}