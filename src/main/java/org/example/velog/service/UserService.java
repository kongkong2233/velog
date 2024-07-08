package org.example.velog.service;

import lombok.extern.slf4j.Slf4j;
import org.example.velog.entity.User;
import org.example.velog.repository.UserRepository;
import org.example.velog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        user.setRegistrationDate(LocalDateTime.now());
        user.setBlogNameFromEmail();
        return userRepository.save(user);
    }



    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElse(null);
    }
}
