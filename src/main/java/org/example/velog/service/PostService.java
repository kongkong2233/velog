package org.example.velog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.velog.dto.PostDTO;
import org.example.velog.entity.Image;
import org.example.velog.entity.Post;
import org.example.velog.entity.User;
import org.example.velog.repository.ImageRepository;
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
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ImageRepository imageRepository;

    @Transactional
    public PostDTO createPost(PostDTO postDTO) {
        Post post = convertToEntity(postDTO);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);

        if (postDTO.getImageUrls() != null) {
            List<Image> images = postDTO.getImageUrls().stream()
                    .map(url -> new Image(url, post))
                    .collect(Collectors.toList());
            imageRepository.saveAll(images);
            savedPost.setImages(images);
        }

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
        Post post = postRepository.findByIdWithImages(postId).orElse(null);
        PostDTO postDTO = post != null ? convertToDTO(post) : null;

        if (postDTO != null) {
            log.info("PostDTO: {}", postDTO);
            if (postDTO.getImageUrls() != null) {
                log.info("ImageUrl: {}", postDTO.getImageUrls());
            }
        }

        return postDTO;
    }

    public Post findPostEntityById(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public List<PostDTO> findPostsByUsername(String username) {
        List<Post> posts = postRepository.findByAuthorUsername(username);
        return posts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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

        if (postDTO.getImageUrls() != null) {
            List<Image> images = postDTO.getImageUrls().stream()
                    .map(url -> new Image(url, post))
                    .collect(Collectors.toList());
            post.setImages(images);
            imageRepository.saveAll(images);
        }
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

        if (post.getImages() != null) {
            List<String> imageUrls = post.getImages().stream()
                    .map(Image::getImageUrl)
                    .collect(Collectors.toList());
            postDTO.setImageUrls(imageUrls);
        }

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
