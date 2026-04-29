package edu.sjsu.cmpe172.Doggy.controller;

import edu.sjsu.cmpe172.Doggy.model.ProviderBookingView;
import edu.sjsu.cmpe172.Doggy.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/provider/bookings")
public class ProviderBookingController {

    private final BookingService bookingService;

    public ProviderBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private Long getProviderIdFromSession(HttpSession session) {
        Object providerIdObj = session.getAttribute("loggedInProviderId");
        if (providerIdObj == null) {
            return null;
        }
        return Long.valueOf(providerIdObj.toString());
    }

    @GetMapping
    public String showBookings(HttpSession session, Model model,
                               @RequestParam(value = "message", required = false) String message,
                               @RequestParam(value = "error", required = false) String error) {

        Long providerId = getProviderIdFromSession(session);
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        model.addAttribute("bookings", bookingService.getBookingsByProviderId(providerId));
        model.addAttribute("message", message);
        model.addAttribute("error", error);

        return "provider-bookings";
    }

    @GetMapping("/edit/{appointmentId}")
    public String showEditBookingPage(@PathVariable Long appointmentId,
                                      HttpSession session,
                                      Model model) {

        Long providerId = getProviderIdFromSession(session);
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        ProviderBookingView booking = bookingService.getBookingByAppointmentId(appointmentId, providerId);
        if (booking == null) {
            return "redirect:/provider/bookings?error=Booking not found";
        }

        model.addAttribute("booking", booking);
        model.addAttribute("services", bookingService.getServicesByProviderId(providerId));
        model.addAttribute("slots", bookingService.getEditableSlotsByProviderId(providerId, booking.getSlotId()));

        return "provider-edit-booking";
    }

    @PostMapping("/edit")
    public String updateBooking(@RequestParam Long appointmentId,
                                @RequestParam Long serviceId,
                                @RequestParam Long slotId,
                                HttpSession session) {

        Long providerId = getProviderIdFromSession(session);
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        try {
            bookingService.updateProviderBooking(appointmentId, providerId, serviceId, slotId);
            return "redirect:/provider/bookings?message=Booking updated successfully";
        } catch (IllegalArgumentException e) {
            return "redirect:/provider/bookings?error=" + e.getMessage().replace(" ", "%20");
        }
    }

    @PostMapping("/cancel")
    public String cancelBooking(@RequestParam Long appointmentId, HttpSession session) {
        Long providerId = getProviderIdFromSession(session);
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        try {
            bookingService.cancelProviderBooking(appointmentId, providerId);
            return "redirect:/provider/bookings?message=Booking canceled successfully";
        } catch (IllegalArgumentException e) {
            return "redirect:/provider/bookings?error=" + e.getMessage().replace(" ", "%20");
        }
    }

    @PostMapping("/complete")
    public String completeBooking(@RequestParam Long appointmentId, HttpSession session) {
        Long providerId = getProviderIdFromSession(session);
        if (providerId == null) {
            return "redirect:/provider/login";
        }

        try {
            bookingService.completeProviderBooking(appointmentId, providerId);
            return "redirect:/provider/bookings?message=Booking marked completed";
        } catch (IllegalArgumentException e) {
            return "redirect:/provider/bookings?error=" + e.getMessage().replace(" ", "%20");
        }
    }
}