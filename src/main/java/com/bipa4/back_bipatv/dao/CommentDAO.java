package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class CommentDAO {
    private final CommentRepository commentRepository;


    /*
    TODO 1: 댓글 작성
        1. 사용자가 유요한 사용자인지 알아야한다.
            1.1 유효한 사용자면 댓글을 등록한다.
            1.2 유요하지 않은 사용자면 댓글을 작성하게 하지 않아야한다 -> Exception
     */
    @Transactional
    public void createComment(){
      //들어가야할 변수를 생각한다.
        /*
        1. video 정보
            -> vide 내용을검사해야한다.
        2. 사용자 정보
            -> 사용자 정보를 검사해야한다.
        3. 댓글을 작성한다
         */
    }


    //TODO 2: 댓글 조회
}
