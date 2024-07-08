package org.example.velog.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.velog.entity.User;
import org.example.velog.repository.UserRepository;
import org.example.velog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository UserRepository;

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

    @GetMapping("/userProfile")
    public String userProfile(@AuthenticationPrincipal OAuth2User principal, Model model) {
        String username = principal.getAttribute("login");
        String email = principal.getAttribute("email");

        model.addAttribute("username", username);
        model.addAttribute("email", email);

        return "userProfile";
    }
}
