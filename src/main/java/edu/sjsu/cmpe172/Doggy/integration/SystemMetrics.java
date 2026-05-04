package edu.sjsu.cmpe172.Doggy.integration;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SystemMetrics {

    private final AtomicInteger bookingAttempts = new AtomicInteger(10);
    private final AtomicInteger successfulBookings = new AtomicInteger(12);
    private final AtomicInteger failedBookings = new AtomicInteger(0);
    private final AtomicInteger successfulLogins = new AtomicInteger(12);

    public void incrementSuccessfulLogins() {
        successfulLogins.incrementAndGet();
    }

    public void incrementBookingAttempts() {
        bookingAttempts.incrementAndGet();
    }

    public void incrementSuccessfulBookings() {
        successfulBookings.incrementAndGet();
    }

    public void incrementFailedBookings() {
        failedBookings.incrementAndGet();
    }

    public int getBookingAttempts() {
        return bookingAttempts.get();
    }

    public int getSuccessfulBookings() {
        return successfulBookings.get();
    }

    public int getFailedBookings() {
        return failedBookings.get();
    }
    public int getSuccessfulLogins() {
        return successfulLogins.get();
    }
}