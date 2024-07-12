package org.example.velog.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long postId;
    private Long userId;
    private String username;
}
