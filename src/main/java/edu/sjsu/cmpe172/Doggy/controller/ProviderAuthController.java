package edu.sjsu.cmpe172.Doggy.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import edu.sjsu.cmpe172.Doggy.model.Provider;
import edu.sjsu.cmpe172.Doggy.service.ProviderService;

@Controller
@RequestMapping("/provider")
public class ProviderAuthController {

    private final ProviderService providerService;

    public ProviderAuthController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @GetMapping("/login")
    public String showProviderLoginPage() {
        return "provider-login";
    }

    @PostMapping("/login")
    public String loginProvider(@RequestParam String email,
                                @RequestParam String password,
                                Model model,
                                HttpSession session) {

        Provider provider = providerService.loginAndReturnProvider(email, password);

        if (provider != null) {
            session.setAttribute("loggedInProviderId", provider.getProviderId());
            session.setAttribute("loggedInProviderFirstName", provider.getFirstName());
            session.setAttribute("loggedInProviderBusinessName", provider.getName());
            return "redirect:/provider/dashboard";
        }

        model.addAttribute("error", "Invalid email or password.");
        return "provider-login";
    }

    @GetMapping("/register")
    public String showProviderRegisterPage() {
        return "provider-register";
    }

    @PostMapping("/register")
    public String registerProvider(@RequestParam String firstName,
                                   @RequestParam String lastName,
                                   @RequestParam(required = false) String phoneNumber,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam(required = false) String address,
                                   @RequestParam String name,
                                   Model model) {
        try {
            Provider provider = new Provider();
            provider.setFirstName(firstName);
            provider.setLastName(lastName);
            provider.setPhoneNumber(phoneNumber);
            provider.setEmail(email);
            provider.setPassword(password);
            provider.setAddress(address);
            provider.setName(name);

            providerService.registerProvider(provider);
            model.addAttribute("message", "Provider account created successfully.");
            return "provider-login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "provider-register";
        }
    }

    @GetMapping("/logout")
    public String logoutProvider(HttpSession session) {
        session.removeAttribute("loggedInProviderId");
        session.removeAttribute("loggedInProviderFirstName");
        session.removeAttribute("loggedInProviderBusinessName");
        return "redirect:/";
    }
}