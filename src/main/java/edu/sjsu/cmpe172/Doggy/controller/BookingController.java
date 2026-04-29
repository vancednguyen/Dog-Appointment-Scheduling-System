package edu.sjsu.cmpe172.Doggy.controller;

import edu.sjsu.cmpe172.Doggy.integration.SystemMetrics;
import edu.sjsu.cmpe172.Doggy.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
@Controller
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final SystemMetrics systemMetrics;
    private final BookingService bookingService;

    public BookingController(BookingService bookingService, SystemMetrics systemMetrics) {
        this.bookingService = bookingService;
        this.systemMetrics = systemMetrics;
    }

    @GetMapping("/appointments")
    public String showAppointmentsPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUserId") == null) {
            return "redirect:/login";
        }

        model.addAttribute("availableDates", bookingService.getAvailableDates());
        return "appointments";
    }

    @GetMapping("/appointments/providers")
    public String showProvidersPage(@RequestParam("date") String date,
                                    HttpSession session,
                                    Model model) {
        if (session.getAttribute("loggedInUserId") == null) {
            return "redirect:/login";
        }

        model.addAttribute("selectedDate", date);
        model.addAttribute("providers", bookingService.getProvidersByDate(date));
        return "select-provider";
    }

    @GetMapping("/appointments/book")
    public String showBookingPage(@RequestParam("date") String date,
                                  @RequestParam("providerId") Long providerId,
                                  HttpSession session,
                                  Model model) {
        Object userIdObj = session.getAttribute("loggedInUserId");

        if (userIdObj == null) {
            return "redirect:/login";
        }

        Long userId = Long.valueOf(userIdObj.toString());

        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedProviderId", providerId);
        model.addAttribute("provider", bookingService.getProviderById(providerId));
        model.addAttribute("timeSlots", bookingService.getSlotsByDateAndProvider(date, providerId));
        model.addAttribute("services", bookingService.getServicesByDateAndProvider(date, providerId));
        model.addAttribute("dogName", bookingService.getDogNameByUserId(userId));

        return "book-appointment";
    }

    @PostMapping("/appointments/submit")
    public String submitBooking(@RequestParam String date,
                                @RequestParam Long providerId,
                                @RequestParam Long slotId,
                                @RequestParam Long serviceId,
                                @RequestParam String dogName,
                                HttpSession session,
                                Model model) {

        Object userIdObj = session.getAttribute("loggedInUserId");

        if (userIdObj == null) {
            logger.warn("Unauthenticated booking attempt for date={}", date);
            return "redirect:/login";
        }

        Long userId = Long.valueOf(userIdObj.toString());

        logger.info("Booking attempt: userId={}, providerId={}, serviceId={}, slotId={}, date={}",
                userId, providerId, serviceId, slotId, date);

        try {
            bookingService.createBooking(userId, providerId, serviceId, slotId, date);

            logger.info("Booking success: userId={}, slotId={}, providerId={}",
                    userId, slotId, providerId);

            systemMetrics.incrementSuccessfulBookings();
            model.addAttribute("message", "Appointment booked successfully.");
            model.addAttribute("selectedDate", date);
            model.addAttribute("selectedDog", dogName);
            return "booking-confirmation";

        } catch (IllegalArgumentException e) {
            logger.warn("Booking failed: userId={}, slotId={}, reason={}",
                    userId, slotId, e.getMessage());

            model.addAttribute("error", e.getMessage());
            model.addAttribute("selectedDate", date);
            model.addAttribute("selectedProviderId", providerId);
            model.addAttribute("provider", bookingService.getProviderById(providerId));
            model.addAttribute("timeSlots", bookingService.getSlotsByDateAndProvider(date, providerId));
            model.addAttribute("services", bookingService.getServicesByDateAndProvider(date, providerId));
            model.addAttribute("dogName", bookingService.getDogNameByUserId(userId));
            return "book-appointment";

        } catch (Exception e) {
            logger.error("Unexpected booking error: userId={}, slotId={}", userId, slotId, e);

            model.addAttribute("error", "Unexpected system error. Please try again.");
            model.addAttribute("selectedDate", date);
            model.addAttribute("selectedProviderId", providerId);
            model.addAttribute("provider", bookingService.getProviderById(providerId));
            model.addAttribute("timeSlots", bookingService.getSlotsByDateAndProvider(date, providerId));
            model.addAttribute("services", bookingService.getServicesByDateAndProvider(date, providerId));
            model.addAttribute("dogName", bookingService.getDogNameByUserId(userId));
            return "book-appointment";
        }
    }
    @GetMapping("/my-bookings")
    public String showUserBookings(HttpSession session,
                                   Model model,
                                   @RequestParam(value = "message", required = false) String message,
                                   @RequestParam(value = "error", required = false) String error) {

        Object userIdObj = session.getAttribute("loggedInUserId");

        if (userIdObj == null) {
            return "redirect:/login";
        }

        Long userId = Long.valueOf(userIdObj.toString());

        model.addAttribute("bookings", bookingService.getBookingsByUserId(userId));
        model.addAttribute("message", message);
        model.addAttribute("error", error);

        return "user-bookings";
    }
    @PostMapping("/my-bookings/cancel")
    public String cancelUserBooking(@RequestParam Long appointmentId, HttpSession session) {

        Object userIdObj = session.getAttribute("loggedInUserId");

        if (userIdObj == null) {
            return "redirect:/login";
        }

        Long userId = Long.valueOf(userIdObj.toString());

        try {
            bookingService.cancelUserBooking(appointmentId, userId);
            return "redirect:/my-bookings?message=" +
                    URLEncoder.encode("Booking canceled successfully.", StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return "redirect:/my-bookings?error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }
}