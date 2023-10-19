package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.comment.CommentRequest;
import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.CommentService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  private final SecurityService securityService;

  @PostMapping("/commentParent")//insert
  public ResponseEntity<String> insertParentComment(@RequestBody CommentRequest commentRequest,
      @CookieValue(name = "accessToken") String accessToken) {
    if (securityService.getSubject(accessToken)) {
      boolean saved = commentService.saveParentComment(commentRequest);
      if (saved) {
        return ResponseEntity.ok("댓글 등록 성공");
      } else {
        return ResponseEntity.badRequest().body("댓글 등록 실패");
      }
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한없음");
    }
  }

  @PostMapping("/commentChild")//insert
  public ResponseEntity<String> insertChildComment(@RequestBody CommentRequest commentRequest,
      @RequestParam("groupIndex") Integer groupIndex, @CookieValue(name = "accessToken") String accessToken) {
    if (securityService.getSubject(accessToken)) {
      boolean saved = commentService.saveChildComment(commentRequest,groupIndex);
      if (saved) {
        return ResponseEntity.ok("댓글 등록 성공");
      } else {
        return ResponseEntity.badRequest().body("댓글 등록 실패");
      }
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한없음");
    }
  }


  @PutMapping("/comment")//update
  public ResponseEntity<String> updateComment(@RequestBody CommentRequest commentRequest,
      @CookieValue(name = "accessToken") String accessToken) {
    if (commentRequest.getAccountId().equals(securityService.getSubjectAccount(accessToken)
        .getAccountId())) {
      boolean updated = commentService.updateComment(commentRequest);
      if (updated) {
        return ResponseEntity.ok("댓글 수정 성공");
      } else {
        return ResponseEntity.badRequest().body("댓글 수정 실패");
      }
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한없음");
    }
  }

  @DeleteMapping("/{videoId}/comment/{commentId}")//delete
  public ResponseEntity<String> deleteComment(@RequestBody CommentRequest commentRequest,
      @PathVariable UUID videoId, @PathVariable UUID commentId,
      @CookieValue(name = "accessToken") String accessToken) {
    if (Objects.equals(commentRequest.getAccountId(), securityService.getSubjectAccount(accessToken)
        .getAccountId())) {
      commentService.deleteComment(commentId);
    }
    return ResponseEntity.ok("댓글 삭제 성공");
  }



}

