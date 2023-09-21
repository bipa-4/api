package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.dto.CommentRequest;
import com.bipa4.back_bipatv.dto.CommentResponse;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.entity.Videos;
import com.bipa4.back_bipatv.repository.CommentRepository;
import com.bipa4.back_bipatv.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.stream.events.Comment;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class CommentDAO {

    private final CommentRepository commentRepository;

    @Autowired
    VideoRepository videoRepository;
    @Autowired
    AccountDAO accountDAO;


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
        System.out.println(commentRepository.findById(commentId).orElse(null));
        return new Comments();
    }
//    public CommentRequest findById(int commentId){
//        Comments comments = commentRepository.findById(commentId).orElse(null);
//        if(comments == null){
//            return null;
//        }
//
//        CommentRequest commentRequest = new CommentRequest();
//        commentRequest.setCommentId(comments.getCommentId());
//        commentRequest.setContent(comments.getContent());
//        commentRequest.setCreateAt(comments.getCreateAt());
//        commentRequest.setParentChild(comments.getParentChild());
//        commentRequest.setGroupIndex(comments.getGroupIndex());
//        return commentRequest;
//
//    }
}
