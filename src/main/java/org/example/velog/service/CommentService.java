package org.example.velog.service;

import org.example.velog.dto.CommentDTO;
import org.example.velog.dto.UserDTO;
import org.example.velog.entity.Comment;
import org.example.velog.entity.Post;
import org.example.velog.entity.User;
import org.example.velog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    public List<CommentDTO> getCommentByPostId(Long postId) {
        return commentRepository.findByPost_PostId(postId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO saveComment(CommentDTO commentDTO) {
        Comment comment = convertToEntity(commentDTO);
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setCommentId(commentDTO.getCommentId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        commentDTO.setUpdatedAt(comment.getUpdatedAt());
        commentDTO.setPostId(comment.getPost().getPostId());
        commentDTO.setUserId(comment.getUser().getUserId());
        commentDTO.setUsername(comment.getUser().getUsername());
        return commentDTO;
    }

    private Comment convertToEntity(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setCommentId(commentDTO.getCommentId());
        comment.setContent(commentDTO.getContent());
        comment.setCreatedAt(commentDTO.getCreatedAt());
        comment.setUpdatedAt(commentDTO.getUpdatedAt());

        Post post = postService.findPostEntityById(commentDTO.getPostId());;
        comment.setPost(post);

        User user = userService.findUserEntityById(commentDTO.getUserId());
        comment.setUser(user);

        return comment;
    }
}


