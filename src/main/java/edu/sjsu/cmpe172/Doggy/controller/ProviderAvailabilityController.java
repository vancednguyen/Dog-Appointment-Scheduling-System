package edu.sjsu.cmpe172.Doggy.controller;
import edu.sjsu.cmpe172.Doggy.model.AvailabilitySlot;
import edu.sjsu.cmpe172.Doggy.service.AvailabilitySlotService;
import edu.sjsu.cmpe172.Doggy.service.ServiceOfferingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/provider/availability")
public class ProviderAvailabilityController {

    private final AvailabilitySlotService availabilitySlotService;
    private final ServiceOfferingService serviceOfferingService;

    public ProviderAvailabilityController(AvailabilitySlotService availabilitySlotService,
                                          ServiceOfferingService serviceOfferingService) {
        this.availabilitySlotService = availabilitySlotService;
        this.serviceOfferingService = serviceOfferingService;
    }

    @GetMapping
    public String showAvailabilityPage(HttpSession session, Model model) {
        Object providerIdObj = session.getAttribute("loggedInProviderId");

        if (providerIdObj == null) {
            return "redirect:/provider/login";
        }

        Long providerId = Long.valueOf(providerIdObj.toString());

        model.addAttribute("services", serviceOfferingService.getServicesByProviderId(providerId));
        model.addAttribute("slots", availabilitySlotService.getProviderSlots(providerId));
        return "provider-availability";
    }

    @PostMapping("/add")
    public String addAvailability(@RequestParam Long serviceId,
                                  @RequestParam String slotDate,
                                  @RequestParam String startTime,
                                  @RequestParam String endTime,
                                  HttpSession session,
                                  Model model) {
        Object providerIdObj = session.getAttribute("loggedInProviderId");

        if (providerIdObj == null) {
            return "redirect:/provider/login";
        }

        Long providerId = Long.valueOf(providerIdObj.toString());

        try {
            AvailabilitySlot slot = new AvailabilitySlot();
            slot.setProviderId(providerId);
            slot.setServiceId(serviceId);
            slot.setSlotDate(slotDate);
            slot.setStartTime(startTime);
            slot.setEndTime(endTime);

            availabilitySlotService.addSlot(slot);
            return "redirect:/provider/availability";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("services", serviceOfferingService.getServicesByProviderId(providerId));
            model.addAttribute("slots", availabilitySlotService.getProviderSlots(providerId));
            return "provider-availability";
        }
    }
}
