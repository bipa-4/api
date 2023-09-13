package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.CommentDTO;
import com.bipa4.back_bipatv.entity.Comments;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comments, Integer> {

    //
    @Query(value = "SELECT accounts.profile_url, accounts.name, comments.content, comments.create_at " +
            "FROM comments comments " +
            "JOIN accounts accounts " +
            "ON comments.account_id = accounts.account_id " +
            "WHERE video_id = :videoId " +
            "ORDER BY comments.create_at", nativeQuery = true)
    List<CommentDTO> findAllComments(@Param("videoId") int videoId);

    //QueryDsl ->
}
