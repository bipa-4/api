package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.AccountDAO;
import com.bipa4.back_bipatv.dao.CommentDAO;
import com.bipa4.back_bipatv.dto.CommentRequest;
import com.bipa4.back_bipatv.dto.CommentResponse;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.entity.Videos;
import com.bipa4.back_bipatv.repository.VideoRepository;
import com.bipa4.back_bipatv.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentDAO commentDAO;
    private final AccountDAO accountDAO;
    private final VideoRepository videoRepository;
    private final SecurityService securityService;

    public List<CommentResponse> findAllComments(int videoId){
        List<CommentResponse> list = commentDAO.findAllComments(videoId);

        return list;

    }

    public boolean saveComment(CommentRequest commentRequest) {
        /**
         * 로그인 아이디가 DB에 없는데도 그냥 존재만 하면 허용을 해줄껀가?
         * 그거를 해봐라.. 현재는 ID만 있으면 그냥 허용임.
         * redis session에 담긴 ID와 DB에 있는 ID가 동일한지 확인해야한다.
         * - 프론트에 있따.
         *
         */

        if(Objects.nonNull(commentRequest.getAccountId())){
            Comments comments = convertDtoToEntity(commentRequest);
            return commentDAO.saveComment(comments);
        } else {
            return false;
        }

    }


    private Comments convertDtoToEntity(CommentRequest commentRequest){
        Comments comments = null;
        /**
         * 1. request에 commentId가 있으면 Update 상황이다.
         *  - 수정은 원래 있던 Comments를 불러와서 바꿀 내용 바꾸고 저장하는게 수정이다.
         * 2. request에 commentId가 없으면 Insert 상황이다.
         *  - 생성은 Id는 AutoIncrement로 생성될꺼고(Null로 나둬도 되는거고) 값을 넣어줘야한다.
         */
        if(Objects.isNull(commentRequest.getCommentId())){
            //insert의 경우
            comments = new Comments();
            if(Objects.nonNull(commentRequest.getVideoId())){
                Videos videos = videoRepository.findById((long) commentRequest.getVideoId()).get();
                comments.setVideos(videos);
            }
            if(Objects.nonNull(commentRequest.getAccountId())){
                //Account DAO에 ID로 Accounts를 가져오는게 없고 객체 안에 ID로 가져오는거밖에 없어서
                //빈 객체 생성하고 그 안에 ID를 넣었음.
                Accounts tempAccounts = new Accounts();
                tempAccounts.setAccountId((long) commentRequest.getAccountId());

                Accounts accounts = accountDAO.selectAccount(tempAccounts);
                comments.setAccounts(accounts);
            }
            if(Objects.nonNull(commentRequest.getParentChild())){
                comments.setParentChild(commentRequest.getParentChild());
            }
            if(Objects.nonNull(commentRequest.getSort())){
                comments.setSort(commentRequest.getSort());
            }
            if(Objects.nonNull(commentRequest.getGroupIndex())){
                comments.setGroupIndex(commentRequest.getGroupIndex());
            }
            if(Objects.nonNull(commentRequest.getCreateAt())){
                comments.setCreateAt(commentRequest.getCreateAt());
            }
        }else {
            //update의 경우(commentId가 있을 경우)
            comments = commentDAO.findByCommentId(commentRequest.getCommentId());
        }
        //insert update 공통 상황
        if (Objects.nonNull(commentRequest.getContent())){
            comments.setContent(commentRequest.getContent());
        }
        return comments;
    }

}
