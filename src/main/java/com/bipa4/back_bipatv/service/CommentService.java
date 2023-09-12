package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.entity.Videos;
import com.bipa4.back_bipatv.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }

    public void createComment(Comments comments){
        commentRepository.save(comments);
    }

    public List<Comments> findAllComments(int videoId){
        List<Comments> list = commentRepository.findAllByVideoId(videoId).get();
        return list;

    }
}
