package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.CommentDTO;
import com.bipa4.back_bipatv.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    private final CommentService commentService;

    @GetMapping("/{videoId}")
    public List<CommentDTO> findAllComments(@PathVariable int videoId){
        List<CommentDTO> list = commentService.findAllComments(videoId);

        return list;
    }


}
