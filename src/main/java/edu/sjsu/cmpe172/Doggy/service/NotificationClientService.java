package edu.sjsu.cmpe172.Doggy.service;

import edu.sjsu.cmpe172.Doggy.dto.BookingConfirmationRequest;
import edu.sjsu.cmpe172.Doggy.dto.NotificationResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationClientService {

    private final RestTemplate restTemplate;

    public NotificationClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NotificationResponse sendBookingConfirmation(BookingConfirmationRequest request) {
        String url = "http://localhost:8081/mock-notification/booking-confirmation";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BookingConfirmationRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<NotificationResponse> response = restTemplate.postForEntity(
                url,
                entity,
                NotificationResponse.class
        );

        return response.getBody();
    }
}