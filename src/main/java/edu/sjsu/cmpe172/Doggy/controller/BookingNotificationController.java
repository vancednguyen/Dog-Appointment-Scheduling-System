package edu.sjsu.cmpe172.Doggy.controller;

import edu.sjsu.cmpe172.Doggy.dto.BookingConfirmationRequest;
import edu.sjsu.cmpe172.Doggy.dto.NotificationResponse;
import edu.sjsu.cmpe172.Doggy.service.NotificationClientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingNotificationController {

    private final NotificationClientService notificationClientService;

    public BookingNotificationController(NotificationClientService notificationClientService) {
        this.notificationClientService = notificationClientService;
    }

    @PostMapping("/send-confirmation")
    public NotificationResponse sendConfirmation(@RequestBody BookingConfirmationRequest request) {
        return notificationClientService.sendBookingConfirmation(request);
    }
}