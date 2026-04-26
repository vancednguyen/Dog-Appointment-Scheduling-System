package edu.sjsu.cmpe172.Doggy.controller;

import edu.sjsu.cmpe172.Doggy.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/appointments")
    public String showAppointmentsPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUserId") == null) {
            return "redirect:/login";
        }

        model.addAttribute("availableDates", bookingService.getAvailableDates());
        return "appointments";
    }
    @GetMapping("/appointments/book")
    public String showBookingPage(@RequestParam("date") String date,
                                  HttpSession session,
                                  Model model) {
        Object userIdObj = session.getAttribute("loggedInUserId");

        if (userIdObj == null) {
            return "redirect:/login";
        }

        Long userId = Long.valueOf(userIdObj.toString());

        model.addAttribute("selectedDate", date);
        model.addAttribute("providers", bookingService.getProvidersByDate(date));
        model.addAttribute("timeSlots", bookingService.getSlotsByDate(date));
        model.addAttribute("services", bookingService.getServicesByDate(date));
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
            return "redirect:/login";
        }

        Long userId = Long.valueOf(userIdObj.toString());

        try {
            bookingService.createBooking(userId, providerId, serviceId, slotId);

            model.addAttribute("message", "Appointment booked successfully.");
            model.addAttribute("selectedDate", date);
            model.addAttribute("selectedDog", dogName);
            return "booking-confirmation";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("selectedDate", date);
            model.addAttribute("providers", bookingService.getProvidersByDate(date));
            model.addAttribute("timeSlots", bookingService.getSlotsByDate(date));
            model.addAttribute("services", bookingService.getServicesByDate(date));
            model.addAttribute("dogName", bookingService.getDogNameByUserId(userId));
            return "book-appointment";
        }
    }
}
