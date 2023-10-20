package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.dataType.ErrorCode;
import com.bipa4.back_bipatv.dto.comment.ChildCommentResponse;
import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.exception.CustomApiException;
import com.bipa4.back_bipatv.repository.CommentRepository;
import com.bipa4.back_bipatv.security.SecurityService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentDAO {

  private final CommentRepository commentRepository;
  private final SecurityService securityService;


  public List<CommentResponse> findParentComments(UUID videoId) {
    List<CommentResponse> list = commentRepository.findParentComments(videoId);
    return list;
  }

  public List<ChildCommentResponse> findChildComments(UUID videoId, int groupIndex) {
    List<ChildCommentResponse> list = commentRepository.findChildComments(videoId, groupIndex);
    return list;
  }

  public boolean saveParentComment(Comments comments) {
    try {
      commentRepository.save(comments);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.INSERT_ERROR);
    }
    return true;
  }

  public boolean saveChildComment(Comments comments) {
    try {
      commentRepository.save(comments);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.INSERT_ERROR);
    }
    return true;
  }


  public Comments findByCommentId(UUID commentId) {
    return commentRepository.findById(commentId).orElse(null);
  }


  public boolean deleteComment(UUID commentId, String accessToken) {
    Comments comments = commentRepository.findById(commentId).orElse(null);
    if (comments != null) {
      if (Objects.equals(comments.getAccounts().getAccountId(),
          securityService.getSubjectAccount(accessToken)
              .getAccountId())) {
        commentRepository.deleteById(commentId);
      }
      return true; // 댓글 삭제 성공
    } else {
      return false; // 댓글이 없거나 삭제 실패
    }
  }
}




