package edu.sjsu.cmpe172.Doggy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
@Controller
public class HomeController {

    @GetMapping("/")
    public String showHomePage(HttpSession session, Model model) {
        Object firstName = session.getAttribute("loggedInUserFirstName");
        model.addAttribute("loggedInUserFirstName", firstName);
        return "home";
    }
}

