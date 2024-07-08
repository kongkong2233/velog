package org.example.velog.service;

import lombok.RequiredArgsConstructor;
import org.example.velog.entity.Post;
import org.example.velog.entity.User;
import org.example.velog.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public void createPost(String title, String content, User userId) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(userId);
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);
    }
}
