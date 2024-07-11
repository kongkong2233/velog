package org.example.velog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.velog.entity.User;
import org.example.velog.repository.UserRepository;
import org.example.velog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository UserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/oauth2/login")
    @ResponseBody
    public String user(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            log.info("user is null");
            return "user is null";
        }

        // OAuth2User 객체에서 전체 사용자 정보 추출
        Map<String, Object> attributes = oauth2User.getAttributes();

        // 전체 사용자 정보 로그로 출력
        attributes.forEach((key, value) -> {
            System.out.println("Key: " + key + ", Value: " + value);
        });

        String username = (String) attributes.get("login");
        String email = (String) attributes.get("email");
        String userNick = (String) attributes.get("name");

        if (username == null || email == null || userNick == null) {
            return "Error: Missing user information from OAuth2 provider";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setUserNick(userNick);
        user.setPassword("");
        user.setProvider("github");
        user.setBlogNameFromEmail();

        userService.saveUser(user);

        return "home";
    }

    @GetMapping("/@{username}")
    public String userProfile(@PathVariable(name = "username") String username,
            @AuthenticationPrincipal OAuth2User principal, Model model) {
       if (principal == null) {
           return "redirect:/login";
       }

       String currentUsername = principal.getAttribute("login");

       if (!currentUsername.equals(username)) {
           return "redirect:/error";
       }

       String email = principal.getAttribute("email");

       model.addAttribute("username", currentUsername);
       model.addAttribute("email", email);

       return "userProfile";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/loginform";
    }

    @GetMapping("/loginform")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        Optional<User> exisitingUser = userRepository.findByEmail(user.getEmail());
        System.out.println("Exisiting user: " + exisitingUser);

        if (exisitingUser.isPresent()) {
            model.addAttribute("error", "해당 이메일은 존재하는 이메일입니다.");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRegistrationDate(LocalDateTime.now());
        user.setBlogNameFromEmail();
        userRepository.save(user);
        return "redirect:/loginform";
    }
}
