package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.AccountDAO;
import com.bipa4.back_bipatv.dao.CommentDAO;
import com.bipa4.back_bipatv.dto.CommentRequest;
import com.bipa4.back_bipatv.dto.CommentResponse;
import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.entity.Videos;
import com.bipa4.back_bipatv.repository.VideoRepository;
import com.bipa4.back_bipatv.security.SecurityService;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentDAO commentDAO;
  private final AccountDAO accountDAO;
  private final VideoRepository videoRepository;
  private final SecurityService securityService;

  public List<CommentResponse> findAllComments(int videoId) {
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
    System.out.println("service" + commentRequest);
    System.out.println(Objects.nonNull(commentRequest.getAccountId()));
    if (Objects.nonNull(commentRequest.getAccountId())) {
      Comments comments = convertDtoToEntityForInsert(commentRequest);
      return commentDAO.saveComment(comments);
    } else {
      return false;
    }

  }

  public boolean updateComment(CommentRequest commentRequest) {

    if (Objects.nonNull(commentRequest.getAccountId())) {
      Comments comments = convertDtoToEntityForUpdate(commentRequest);
      return commentDAO.saveComment(comments);
    } else {
      return false;
    }

  }

  private Comments convertDtoToEntityForInsert(CommentRequest commentRequest) {
    System.out.println(commentRequest);
    Comments comments = new Comments();
    System.out.println("비디오 id:" + commentRequest.getVideoId());
    if (Objects.nonNull(commentRequest.getVideoId())) {
      Videos videos = videoRepository.findById((long) commentRequest.getVideoId()).get();
      comments.setVideos(videos);
    }

//        if (Objects.nonNull(commentRequest.getAccountId())) {
//            Accounts tempAccounts = new Accounts();
//            tempAccounts.setAccountId((long) commentRequest.getAccountId());
//
//            Accounts accounts = accountDAO.selectAccount1(tempAccounts);
//            System.out.println(accounts);
//            comments.setAccounts(accounts);
//        }

    if (Objects.nonNull(commentRequest.getContent())) {
      comments.setContent(commentRequest.getContent());
    }

    if (Objects.nonNull(commentRequest.getParentChild())) {
      comments.setParentChild(commentRequest.getParentChild());
    }

    if (Objects.nonNull(commentRequest.getGroupIndex())) {
      comments.setGroupIndex(commentRequest.getGroupIndex());
    }

    Instant instant = Instant.now();
    commentRequest.setCreateAt(Timestamp.from(instant));
    comments.setCreateAt(commentRequest.getCreateAt());

    return comments;
  }

  private Comments convertDtoToEntityForUpdate(CommentRequest commentRequest) {
    Comments comments = commentDAO.findByCommentId(commentRequest.getCommentId());

    if (Objects.nonNull(commentRequest.getContent())) {
      comments.setContent(commentRequest.getContent());
    }

    Instant instant = Instant.now();
    commentRequest.setCreateAt(Timestamp.from(instant));
    comments.setCreateAt(commentRequest.getCreateAt());

    return comments;
  }
}
