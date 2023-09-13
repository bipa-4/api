package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.dto.CommentDTO;
import com.bipa4.back_bipatv.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentDAO {

    private final CommentRepository commentRepository;

    public List<CommentDTO> findAllComments(int videoId){
        List<CommentDTO> list = commentRepository.findAllComments(videoId);
        return list;
    }


}
