package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.entity.Videos;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comments, Integer> {

    Optional<List<Comments>> findAllByVideoId(int videoId);
}
