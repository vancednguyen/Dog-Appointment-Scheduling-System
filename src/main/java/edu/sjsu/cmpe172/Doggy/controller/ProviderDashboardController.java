package edu.sjsu.cmpe172.Doggy.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProviderDashboardController {

    @GetMapping("/provider/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Object providerId = session.getAttribute("loggedInProviderId");

        if (providerId == null) {
            return "redirect:/provider/login";
        }

        model.addAttribute("providerFirstName", session.getAttribute("loggedInProviderFirstName"));
        model.addAttribute("businessName", session.getAttribute("loggedInProviderBusinessName"));
        return "provider-dashboard";
    }
}