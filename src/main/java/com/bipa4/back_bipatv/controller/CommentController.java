package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.comment.CommentRequest;
import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.CommentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

  @GetMapping("/{videoId}/comment-parent")
  public List<CommentResponse> findParentComments(@PathVariable Long videoId) {
    List<CommentResponse> list = commentService.findParentComments(videoId);

    return list;
  }

  @GetMapping("/{videoId}/comment-child")
  public List<CommentResponse> findChildComments(@PathVariable Long videoId,
      @RequestParam int groupIndex) {
    List<CommentResponse> list = commentService.findChildComments(videoId, groupIndex);

    return list;
  }

  @PostMapping("/comment")//insert
  public String insertComment(@RequestBody CommentRequest commentRequest,
      @CookieValue(name = "accessToken") String accessToken) {
    /**
     * 서비스의 return 값이 boolean이고 Controller의 리턴값이 String이어서
     * String.valueOf()를 써서 boolean => String 바꿔줌
     */
    System.out.println("commentRequest"+commentRequest);

    if (securityService.getSubject(accessToken)) {

      return String.valueOf(commentService.saveComment(commentRequest));


    } else {
      return "권한없음";
    }

  }

  @PutMapping("/comment")//update
  public String updateComment(@RequestBody CommentRequest commentRequest,
      @CookieValue(name = "accessToken") String accessToken) {
    if(commentRequest.getAccountId()==securityService.getSubjectAccount(accessToken).getAccountId()){
      return String.valueOf(commentService.updateComment(commentRequest));

    } else {
      return "권한없음";
    }
  }

  @DeleteMapping("/{videoId}/comment/{commentId}")
  public ResponseEntity<String> deleteComment(@RequestBody CommentRequest commentRequest, @PathVariable Long videoId, @PathVariable int commentId,
      @CookieValue(name = "accessToken") String accessToken) {
    if (commentRequest.getAccountId() == securityService.getSubjectAccount(accessToken)
        .getAccountId()) {
      commentService.deleteComment(commentId);
    }
    return ResponseEntity.ok("댓글이 삭제되었습니다.");
  }
}

