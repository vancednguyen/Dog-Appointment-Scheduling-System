package edu.sjsu.cmpe172.Doggy.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import edu.sjsu.cmpe172.Doggy.model.ServiceOffering;
import edu.sjsu.cmpe172.Doggy.service.ServiceOfferingService;
@Controller
@RequestMapping("/provider/services")
public class ProviderServiceController {

    private final ServiceOfferingService serviceOfferingService;

    public ProviderServiceController(ServiceOfferingService serviceOfferingService) {
        this.serviceOfferingService = serviceOfferingService;
    }

    @GetMapping
    public String showProviderServicesPage(HttpSession session, Model model) {
        Object providerIdObj = session.getAttribute("loggedInProviderId");

        if (providerIdObj == null) {
            return "redirect:/login";
        }

        Long providerId = Long.valueOf(providerIdObj.toString());

        model.addAttribute("services", serviceOfferingService.getServicesByProviderId(providerId));
        return "provider-services";
    }

    @PostMapping("/add")
    public String addService(@RequestParam String serviceName,
                             @RequestParam Integer serviceDuration,
                             @RequestParam Double price,
                             HttpSession session,
                             Model model) {
        Object providerIdObj = session.getAttribute("loggedInProviderId");

        if (providerIdObj == null) {
            return "redirect:/login";
        }

        Long providerId = Long.valueOf(providerIdObj.toString());

        try {
            ServiceOffering service = new ServiceOffering();
            service.setProviderId(providerId);
            service.setServiceName(serviceName);
            service.setServiceDuration(serviceDuration);
            service.setPrice(price);

            serviceOfferingService.addService(service);

            return "redirect:/provider/services";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("services", serviceOfferingService.getServicesByProviderId(providerId));
            return "provider-services";
        }
    }
}