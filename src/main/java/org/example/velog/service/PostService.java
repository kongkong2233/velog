package org.example.velog.service;

import lombok.RequiredArgsConstructor;
import org.example.velog.entity.Post;
import org.example.velog.entity.User;
import org.example.velog.repository.PostRepository;
import org.example.velog.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createPost(String title, String content, Long userId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(user);
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
    }

    public Post findById(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElse(null);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public void save(Post post) {
        postRepository.save(post);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }
}
