package edu.sjsu.cmpe172.Doggy.controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import edu.sjsu.cmpe172.Doggy.model.User;
import edu.sjsu.cmpe172.Doggy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.sjsu.cmpe172.Doggy.integration.SystemMetrics;
@Controller
public class AuthController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final SystemMetrics systemMetrics;
    public AuthController(UserService userService, SystemMetrics systemMetrics) {
        this.userService = userService;
        this.systemMetrics = systemMetrics;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            Model model, HttpSession session) {

        User user = userService.loginAndReturnUser(email, password);
        if (user != null) {
            logger.info("Logging was successful: user_email = {}", email);
            systemMetrics.incrementSuccessfulLogins();
            session.setAttribute("loggedInUserFirstName", user.getFirstName());
            session.setAttribute("loggedInUserEmail", user.getEmail());
            session.setAttribute("loggedInUserId", user.getUserId());
            return "redirect:/";
        } else {
            logger.error("As email has failed logging, email= {}, password = {}", email, password);
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               @RequestParam(required = false) String phoneNumber,
                               @RequestParam String password,
                               @RequestParam(required = false) String address,
                               @RequestParam(required = false) String dogName,
                               @RequestParam(required = false) String dogBreed,
                               @RequestParam(required = false) Integer dogAge,
                               Model model) {
        try {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setPassword(password);
            user.setAddress(address);
            user.setDogName(dogName);
            user.setDogBreed(dogBreed);
            user.setDogAge(dogAge);

            userService.registerUser(user);

            model.addAttribute("message", "Account created successfully!");
            return "login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}