package edu.sjsu.cmpe172.Doggy.service;
import edu.sjsu.cmpe172.Doggy.model.User;
import edu.sjsu.cmpe172.Doggy.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        User existing = userRepository.findByEmail(user.getEmail());
        if (existing != null) {
            throw new IllegalArgumentException("Email already exists.");
        }

        userRepository.insertUser(user);
    }
    public User loginAndReturnUser(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }

        return null;
    }

    public boolean login(String email, String password) {
        User user = userRepository.findByEmail(email);
        return user != null && user.getPassword().equals(password);
    }
}
