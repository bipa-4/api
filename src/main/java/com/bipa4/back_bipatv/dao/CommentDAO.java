package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.dto.CommentResponse;
import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentDAO {

    private final CommentRepository commentRepository;

    public List<CommentResponse> findAllComments(int videoId){
        List<CommentResponse> list = commentRepository.findAllComments(videoId);
        return list;
    }

    public boolean saveComment(Comments comments){
        Comments comments1 = commentRepository.save(comments);
        if(comments1 !=null){
            return true;
        }
        return false;
    }

    public Comments findByCommentId(int commentId){
        return commentRepository.findById(commentId).get();
    }




}
