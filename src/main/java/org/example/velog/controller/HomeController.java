package org.example.velog.controller;

import org.example.velog.entity.Post;
import org.example.velog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private PostService postService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OAuth2User principal, CsrfToken csrfToken) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);

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
