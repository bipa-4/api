package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.comment.ChildCommentResponse;
import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.entity.Comments;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface CommentRepository extends JpaRepository<Comments, UUID> {


  //
  @Query(value = "SELECT channels.channel_id AS channelId, channels.profile_url AS channelProfileUrl, channels.name AS channelName, comments.content AS content, comments.create_at AS createAt, INSERT(INSERT(INSERT(INSERT(HEX(comments.comment_id), 9, 0, '-'), 14, 0, '-'), 19, 0, '-'), 24, 0, '-') AS commentId, comments.group_index AS groupIndex, childCount\n" +
          "FROM (SELECT *, COUNT(*) over (PARTITION BY group_index) -1 AS childCount FROM bipaTV.comments) comments\n" +
          "LEFT JOIN accounts\n" +
          "ON comments.account_id = accounts.account_id\n" +
          "LEFT JOIN channels\n" +
          "ON accounts.account_id = channels.account_id\n" +
          "WHERE video_id = :videoId AND parent_child = 0 \n" +
          "ORDER BY comments.create_at;", nativeQuery = true)
  List<CommentResponse> findParentComments(@Param("videoId") UUID videoId);

  @Query(value = "SELECT channels.channel_id AS channelId, channels.profile_url AS channelProfileUrl, channels.name AS channelName, comments.content AS content, comments.create_at AS createAt, INSERT(INSERT(INSERT(INSERT(HEX(comments.comment_id), 9, 0, '-'), 14, 0, '-'), 19, 0, '-'), 24, 0, '-') AS commentId, comments.group_index AS groupIndex\n" +
          "FROM comments \n" +
          "LEFT JOIN accounts\n" +
          "ON comments.account_id = accounts.account_id\n" +
          "LEFT JOIN channels\n" +
          "ON accounts.account_id = channels.account_id\n" +
          "WHERE video_id = :videoId AND parent_child = 1 AND group_index = :groupIndex\n" +
          "ORDER BY comments.create_at", nativeQuery = true)
  List<ChildCommentResponse> findChildComments(@Param("videoId") UUID videoId,
                                               @Param("groupIndex") int groupIndex);


}
