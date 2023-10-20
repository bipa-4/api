package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dataType.ErrorCode;
import com.bipa4.back_bipatv.dto.comment.CommentRequest;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.exception.CustomApiException;
import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.CommentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
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

  // 부모 댓글 INSERT
  @PostMapping("/commentParent")
  public ResponseEntity<Boolean> insertParentComment(@RequestBody CommentRequest commentRequest,
      @CookieValue(name = "accessToken") String accessToken) {
    Accounts account = securityService.getSubjectAccount(accessToken);
    boolean response = commentService.saveParentComment(account, commentRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  // 자식 댓글 INSERT
  @PostMapping("/commentChild")
  public ResponseEntity<Boolean> insertChildComment(@RequestBody CommentRequest commentRequest,
      @RequestParam("groupIndex") Integer groupIndex,
      @CookieValue(name = "accessToken") String accessToken) {
    Accounts account = securityService.getSubjectAccount(accessToken);
    boolean response = commentService.saveChildComment(account, commentRequest, groupIndex);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }


  //update
  @PutMapping("/comment")
  public ResponseEntity<String> updateComment(@RequestBody CommentRequest commentRequest,
      @CookieValue(name = "accessToken") String accessToken) {
    boolean updated = commentService.updateComment(commentRequest);
    if (!updated) {
      throw new CustomApiException(ErrorCode.UPDATE_ERROR);
    }
    return ResponseEntity.ok("댓글 수정 성공");

  }

  // 댓글 DELETE
  @DeleteMapping("/{videoId}/comment/{commentId}")
  public ResponseEntity<Boolean> deleteComment(
      @PathVariable UUID videoId, @PathVariable UUID commentId,
      @CookieValue(name = "accessToken") String accessToken) {
    Accounts account = securityService.getSubjectAccount(accessToken);
    boolean response = commentService.deleteComment(commentId, account);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}

