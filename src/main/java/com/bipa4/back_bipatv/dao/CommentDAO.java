package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.dataType.ErrorCode;
import com.bipa4.back_bipatv.dto.comment.ChildCommentResponse;
import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.exception.CustomApiException;
import com.bipa4.back_bipatv.repository.CommentRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentDAO {

  private final CommentRepository commentRepository;


  public List<CommentResponse> findParentComments(UUID videoId) {
    List<CommentResponse> list = commentRepository.findParentComments(videoId);
    return list;
  }

  public List<ChildCommentResponse> findChildComments(UUID videoId, int groupIndex) {
    List<ChildCommentResponse> list = commentRepository.findChildComments(videoId, groupIndex);
    return list;
  }

  public boolean saveParentComment(Comments comment) {
    try {
      commentRepository.save(comment);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.INSERT_ERROR);
    }
    return true;
  }

  public boolean saveChildComment(Comments comment) {
    try {
      commentRepository.save(comment);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.INSERT_ERROR);
    }
    return true;
  }


  public Comments findByCommentId(UUID commentId) {
    return commentRepository.findById(commentId).orElse(null);
  }


  public boolean deleteComment(UUID commentId) {
    try {
      commentRepository.deleteById(commentId);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.DELETE_ERROR);
    }
    return true;
  }
}




