package edu.sjsu.cmpe172.Doggy.controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import edu.sjsu.cmpe172.Doggy.model.User;
import edu.sjsu.cmpe172.Doggy.service.UserService;
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            Model model, HttpSession session) {

      //  boolean valid = userService.login(email, password);

      //  if (valid) {
      //      model.addAttribute("message", "Login successful!");
      //      return "login-success";
      //  }
      //  else {
      //      model.addAttribute("error", "Invalid email or password.");
      //      return "login";
      //  }
        User user = userService.loginAndReturnUser(email, password);
        if (user != null) {
            session.setAttribute("loggedInUserFirstName", user.getFirstName());
            session.setAttribute("loggedInUserEmail", user.getEmail());
            session.setAttribute("loggedInUserId", user.getUserId());
            return "redirect:/";
        } else {
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