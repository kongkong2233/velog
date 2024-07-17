package org.example.velog.repository;

import org.example.velog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.images WHERE p.postId = :postId")
    Optional<Post> findByIdWithImages(@Param("postId") Long postId);
    List<Post> findByAuthorUsername(String username);
}
