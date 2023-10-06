package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.repository.CommentRepository;
import com.bipa4.back_bipatv.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentDAO {

    private final CommentRepository commentRepository;


    public List<CommentResponse> findParentComments(Long videoId){
        List<CommentResponse> list = commentRepository.findParentComments(videoId);
        return list;
    }

    public List<CommentResponse> findChildComments(Long videoId, int groupIndex){
        List<CommentResponse> list = commentRepository.findChildComments(videoId,groupIndex);
        return list;
    }

    public boolean saveComment(Comments comments){
        Comments comments1 = commentRepository.save(comments);
        if(comments1 !=null){
            return true;
        }
        return false;
    }

    public boolean deleteComment(int commentId){
        Comments comments = commentRepository.findById(commentId).orElse(null);
        if (comments != null) {
            commentRepository.deleteById(commentId);
            return true; // 댓글 삭제 성공
        } else {
            return false; // 댓글이 없거나 삭제 실패
        }
    }





    public Comments findByCommentId(int commentId){
        return commentRepository.findById(commentId).orElse(null);
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
