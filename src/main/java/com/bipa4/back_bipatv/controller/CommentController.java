package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.CommentRequest;
import com.bipa4.back_bipatv.dto.CommentResponse;
import com.bipa4.back_bipatv.entity.Comments;
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
    @GetMapping("/{videoId}/comment")
    public List<CommentResponse> findAllComments(@PathVariable int videoId){
        List<CommentResponse> list = commentService.findAllComments(videoId);

        return list;
    }

    @PostMapping("/comment")
    public String insertComment(@RequestBody CommentRequest commentRequest, @RequestParam("code") String code){
        return String.valueOf(commentService.saveComment(commentRequest));
        /**
         * 서비스의 return 값이 boolean이고 Controller의 리턴값이 String이어서
         * String.valueOf()를 써서 boolean => String 바꿔줌
         */
        if(securityService.getSubject(code)){

        }else{}

    }

    @PutMapping("/comment")
    public String updateComment(@RequestBody CommentRequest commentRequest){

        return String.valueOf(commentService.saveComment(commentRequest));
    }


}
