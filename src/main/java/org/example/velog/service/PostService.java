package org.example.velog.service;

import lombok.RequiredArgsConstructor;
import org.example.velog.dto.PostDTO;
import org.example.velog.entity.Post;
import org.example.velog.entity.User;
import org.example.velog.repository.PostRepository;
import org.example.velog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public PostDTO createPost(PostDTO postDTO) {
        Post post = convertToEntity(postDTO);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        return convertToDTO(savedPost);
    }

    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PostDTO getPostById(Long postId) {
        return postRepository.findById(postId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
    }

    public PostDTO findById(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        return post != null ? convertToDTO(post) : null;
    }

    public Post findPostEntityById(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public void save(PostDTO postDTO) {
        Post post = postRepository.findById(postDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setUpdatedAt(postDTO.getUpdatedAt());

        postRepository.save(post);
    }

    public void delete(PostDTO postDTO) {
        Post post = postRepository.findById(postDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        postRepository.delete(post);
    }

    private PostDTO convertToDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(post.getPostId());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setUpdatedAt(post.getUpdatedAt());
        postDTO.setAuthorId(post.getAuthor().getUserId());
        postDTO.setAuthorName(post.getAuthor().getUsername());
        return postDTO;
    }

    private Post convertToEntity(PostDTO postDTO) {
        Post post = new Post();
        post.setPostId(postDTO.getPostId());
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setCreatedAt(postDTO.getCreatedAt());
        post.setUpdatedAt(postDTO.getUpdatedAt());

        User author = userService.findUserEntityById(postDTO.getAuthorId());
        post.setAuthor(author);
        return post;
    }
}
