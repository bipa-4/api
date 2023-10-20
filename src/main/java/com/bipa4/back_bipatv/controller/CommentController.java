package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dataType.ErrorCode;
import com.bipa4.back_bipatv.dto.comment.CommentRequest;
import com.bipa4.back_bipatv.exception.CustomApiException;
import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.CommentService;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  private final SecurityService securityService;

  //insert
  @PostMapping("/comment")
  public ResponseEntity<String> insertComment(@RequestBody CommentRequest commentRequest,
      @CookieValue(name = "accessToken") String accessToken) {
    if (securityService.getSubject(accessToken)) {
      boolean saved = commentService.saveComment(commentRequest);
      if (!saved) {
        throw new CustomApiException(ErrorCode.UPLOAD_ERROR);
      }
      return ResponseEntity.ok("댓글 등록 성공");
    } else {
      throw new CustomApiException(ErrorCode.AUTHORITY_ERROR);
    }
  }


  //update
  @PutMapping("/comment")
  public ResponseEntity<String> updateComment(@RequestBody CommentRequest commentRequest,
      @CookieValue(name = "accessToken") String accessToken) {
    if (commentRequest.getAccountId().equals(securityService.getSubjectAccount(accessToken)
        .getAccountId())) {
      boolean updated = commentService.updateComment(commentRequest);
      if (!updated) {
        throw new CustomApiException(ErrorCode.UPDATE_ERROR);
      }
      return ResponseEntity.ok("댓글 수정 성공");
    } else {
      throw new CustomApiException(ErrorCode.AUTHORITY_ERROR);
    }
  }

  //delete
  @DeleteMapping("/{videoId}/comment/{commentId}")
  public ResponseEntity<String> deleteComment(@RequestBody CommentRequest commentRequest,
      @PathVariable UUID videoId, @PathVariable UUID commentId,
      @CookieValue(name = "accessToken") String accessToken) {
    if (Objects.equals(commentRequest.getAccountId(), securityService.getSubjectAccount(accessToken)
        .getAccountId())) {
      if (!commentService.deleteComment(commentId)) {
        throw new CustomApiException(ErrorCode.DELETE_ERROR);
      }
    }
    return ResponseEntity.ok("댓글 삭제 성공");
  }
}

