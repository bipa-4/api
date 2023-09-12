package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{videoId}/comment")
    public List<Comments> findAllComments(@PathVariable int videoId){
        return commentService.findAllComments(videoId);

    }

    @PostMapping("/{videoId}/comment/create")
    public String insertComment(@PathVariable int videoId, HttpServletRequest request){
        if(Objects.isNull(request.getSession().getAttribute("id"))){

        }
        return null;
    }
}
