package org.example.velog.controller;

import lombok.RequiredArgsConstructor;
import org.example.velog.entity.Post;
import org.example.velog.entity.User;
import org.example.velog.service.PostService;
import org.example.velog.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;

    @GetMapping("/posts/createform")
    public String createForm() {
        return "createform";
    }

    @PostMapping("/posts/createform")
    public String createPost(@RequestParam(name = "title") String title,
                             @RequestParam(name = "content") String content,
                             @AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null) {
            String username = principal.getAttribute("login");
            Long userId = userService.findUserIdByUsername(username);

            postService.createPost(title, content, userId);

            return "redirect:/";
        }
        return "redirect:/login";
    }

    @GetMapping("/posts/{postId}")
    public String getPostDetail(@PathVariable(name = "postId") Long postId, Model model) {
        Post post = postService.findById(postId);
        if (post == null) {
            return "error/404";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        String formattedDate = post.getCreatedAt().format(formatter);

        model.addAttribute("post", post);
        model.addAttribute("formattedDate", formattedDate);
        return "postDetail";
    }
}
