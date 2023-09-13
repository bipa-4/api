package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.CommentDAO;
import com.bipa4.back_bipatv.dto.CommentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentDAO commentDAO;


    public List<CommentDTO> findAllComments(int videoId){
        List<CommentDTO> list = commentDAO.findAllComments(videoId);

        return list;

    }
}
