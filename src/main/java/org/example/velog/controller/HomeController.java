package org.example.velog.controller;

import org.example.velog.dto.PostDTO;
import org.example.velog.entity.Post;
import org.example.velog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
        List<PostDTO> postDTOs = postService.getAllPosts();
        model.addAttribute("posts", postDTOs);

        model.addAttribute("_csrfToken", csrfToken);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)){
            if (authentication.getPrincipal() instanceof OAuth2User){
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                model.addAttribute("username", oAuth2User.getAttribute("login"));
            } else {
                model.addAttribute("username", authentication.getName());
            }
        }
        return "home";
    }
}
