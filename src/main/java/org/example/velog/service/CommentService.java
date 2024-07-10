package org.example.velog.service;

import org.example.velog.entity.Comment;
import org.example.velog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getCommentByPostId(Long postId) {
        return commentRepository.findByPost_PostId(postId);
    }

    public Comment saveComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }
}


