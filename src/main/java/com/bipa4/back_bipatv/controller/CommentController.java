package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.comment.CommentRequest;
import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    private final CommentService commentService;

    @Autowired
    private SecurityService securityService;

    @GetMapping("/{videoId}/comment-parent")
    public List<CommentResponse> findParentComments(@PathVariable int videoId){
        List<CommentResponse> list = commentService.findParentComments(videoId);

        return list;
    }

    @GetMapping("/{videoId}/comment-child")
    public List<CommentResponse> findChildComments(@PathVariable int videoId, @RequestParam int groupIndex){
        List<CommentResponse> list = commentService.findChildComments(videoId,groupIndex);

        return list;
    }

    @PostMapping("/comment")//insert
    public String insertComment(@RequestBody CommentRequest commentRequest, @RequestParam("code") String code){
        /**
         * 서비스의 return 값이 boolean이고 Controller의 리턴값이 String이어서
         * String.valueOf()를 써서 boolean => String 바꿔줌
         */

        if(securityService.getSubject(code)) {
            return String.valueOf(commentService.saveComment(commentRequest));

        }else{
            return "권한없음";
        }

    }

    @PutMapping("/comment")
    public String updateComment(@RequestBody CommentRequest commentRequest, @RequestParam("code") String code){

        if(securityService.getSubject(code)) {
            return String.valueOf(commentService.saveComment(commentRequest));

        }else{
            return "권한없음";
        }
    }


}
