package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.dto.comment.ChildCommentResponse;
import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.repository.CommentRepository;
import com.bipa4.back_bipatv.repository.VideoRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentDAO {

  private final CommentRepository commentRepository;


  @Autowired
  VideoRepository videoRepository;
  @Autowired
  AccountDAO accountDAO;


  public List<CommentResponse> findParentComments(UUID videoId) {
    List<CommentResponse> list = commentRepository.findParentComments(videoId);
    return list;
  }

  public List<ChildCommentResponse> findChildComments(UUID videoId, int groupIndex) {
    List<ChildCommentResponse> list = commentRepository.findChildComments(videoId, groupIndex);
    return list;
  }

  public boolean saveComment(Comments comments) {
    Comments comments1 = commentRepository.save(comments);
    if (comments1 != null) {
      return true;
    }
    return false;
  }

  public Comments findByCommentId(UUID commentId) {
    return commentRepository.findById(commentId).orElse(null);
  }


  public boolean deleteComment(UUID commentId) {
    Comments comments = commentRepository.findById(commentId).orElse(null);
    if (comments != null) {
      commentRepository.deleteById(commentId);
      return true; // 댓글 삭제 성공
    } else {
      return false; // 댓글이 없거나 삭제 실패
    }
  }


}
