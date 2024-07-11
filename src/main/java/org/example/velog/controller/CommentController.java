package org.example.velog.controller;

import org.example.velog.entity.Comment;
import org.example.velog.entity.Post;
import org.example.velog.entity.User;
import org.example.velog.service.CommentService;
import org.example.velog.service.PostService;
import org.example.velog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable(name = "postId") Long postId,
                             @RequestParam String content,
                             Authentication authentication,
                             @AuthenticationPrincipal OAuth2User oAuth2User) {
        User user;
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            //formLogin
            String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            user = userService.getUserByUsername(username);
        } else if (oAuth2User != null) {
            //OAuth2
            String username = oAuth2User.getAttribute("login");
            user = userService.getUserByUsername(username);
        } else {
            return "redirect:/loginform";
        }

        Post post = postService.getPostById(postId);

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);

        commentService.saveComment(comment);

        return "redirect:/posts/" + postId;
    }
}
