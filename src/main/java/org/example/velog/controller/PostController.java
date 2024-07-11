package org.example.velog.controller;

import lombok.RequiredArgsConstructor;
import org.example.velog.entity.Comment;
import org.example.velog.entity.Post;
import org.example.velog.entity.User;
import org.example.velog.service.CommentService;
import org.example.velog.service.PostService;
import org.example.velog.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    @GetMapping("/posts/createform")
    public String createForm() {
        return "createform";
    }

    @PostMapping("/posts/createform")
    public String createPost(@RequestParam(name = "title") String title,
                             @RequestParam(name = "content") String content,
                             Authentication authentication, Model model) {
        if (authentication != null) {
            String username;

            if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                //formLogin
                username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            } else if (authentication.getPrincipal() instanceof OAuth2User) {
                //OAuth2
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                username = oAuth2User.getAttribute("login");
            } else {
                return "redirect:/loginform";
            }

            Long userId = userService.findUserIdByUsername(username);
            postService.createPost(title, content, userId);

            return  "redirect:/";
        }
        return "redirect:/loginform";
    }

    @GetMapping("/posts/{postId}")
    public String getPostDetail(@PathVariable(name = "postId") Long postId, Model model) {
        Post post = postService.findById(postId);
        if (post == null) {
            return "error/404";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)){
            if (authentication.getPrincipal() instanceof OAuth2User){
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                model.addAttribute("username", oAuth2User.getAttribute("login"));
            } else {
                model.addAttribute("username", authentication.getName());
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        String formattedDate = post.getCreatedAt().format(formatter);

        model.addAttribute("post", post);
        model.addAttribute("formattedDate", formattedDate);

        List<Comment> comments = commentService.getCommentByPostId(postId);
        model.addAttribute("comments", comments);
        return "postDetail";
    }

    @GetMapping("/posts/edit/{id}")
    public String editForm(@PathVariable(name = "id") Long postId, Model model
    , @AuthenticationPrincipal OAuth2User oAuth2User, Authentication authentication) {
        Post post = postService.findById(postId);
        if (post == null) {
            return "error/404";
        }

        User currentUser;
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            //formLogin
            String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            currentUser = userService.getUserByUsername(username);
        } else if (oAuth2User != null) {
            //OAuth2
            String username = oAuth2User.getAttribute("login");
            currentUser = userService.getUserByUsername(username);
        } else {
            return "redirect:/loginform";
        }

        if (!post.getAuthor().equals(currentUser)) {
            return "error/403";
        }

        model.addAttribute("post", post);
        return "editform";
    }

    @PostMapping("/posts/edit/{id}")
    public String editPost(@PathVariable(name = "id") Long postId, @ModelAttribute Post updatedPost,
                           @AuthenticationPrincipal OAuth2User oAuth2User, Authentication authentication) {
        Post post = postService.findById(postId);
        if (post == null) {
            return "error/404";
        }

        User currentUser;
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            //formLogin
            String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            currentUser = userService.getUserByUsername(username);
        } else if (oAuth2User != null) {
            //OAuth2
            String username = oAuth2User.getAttribute("login");
            currentUser = userService.getUserByUsername(username);
        } else {
            return "redirect:/loginform";
        }

        if (!post.getAuthor().equals(currentUser)) {
            return "error/403";
        }

        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        postService.save(post);

        return "redirect:/posts/" + post.getPostId();
    }

    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable(name = "id") Long postId, @AuthenticationPrincipal OAuth2User oAuth2User,
                             Authentication authentication) {
        Post post = postService.findById(postId);
        if (post == null) {
            return "error/404";
        }

        User currentUser;
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            //formLogin
            String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            currentUser = userService.getUserByUsername(username);
        } else if (oAuth2User != null) {
            //OAuth2
            String username = oAuth2User.getAttribute("login");
            currentUser = userService.getUserByUsername(username);
        } else {
            return "redirect:/loginform";
        }

        if (!post.getAuthor().equals(currentUser)) {
            return "error/403";
        }

        postService.delete(post);
        return "redirect:/";
    }
}
