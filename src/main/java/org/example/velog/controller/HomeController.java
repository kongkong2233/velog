package org.example.velog.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OAuth2User principal, CsrfToken csrfToken) {
        model.addAttribute("_csrfToken", csrfToken);
        if (principal != null) {
            model.addAttribute("username", principal.getAttribute("login"));
            return "homeLoggedIn";
        } else {
            return "homeNotLoggedIn";
        }
    }

    @GetMapping("/loginform")
    public String login() {
        return "login";
    }
}
