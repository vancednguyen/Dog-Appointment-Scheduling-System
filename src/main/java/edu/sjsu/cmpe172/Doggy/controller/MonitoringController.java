package edu.sjsu.cmpe172.Doggy.controller;

import edu.sjsu.cmpe172.Doggy.integration.SystemMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class MonitoringController {

    private final SystemMetrics systemMetrics;

    public MonitoringController(SystemMetrics systemMetrics) {
        this.systemMetrics = systemMetrics;
    }

    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("bookingAttempts", systemMetrics.getBookingAttempts());
        metrics.put("successfulBookings", systemMetrics.getSuccessfulBookings());
        metrics.put("failedBookings", systemMetrics.getFailedBookings());
        metrics.put("successfulLogins", systemMetrics.getSuccessfulLogins());
        return metrics;
    }
}