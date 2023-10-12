package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.AccountDAO;
import com.bipa4.back_bipatv.dao.CommentDAO;
import com.bipa4.back_bipatv.dto.comment.CommentRequest;
import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.entity.Videos;
import com.bipa4.back_bipatv.exception.ResourceNotFoundException;
import com.bipa4.back_bipatv.repository.VideoRepository;
import com.bipa4.back_bipatv.security.SecurityService;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentDAO commentDAO;
  private final AccountDAO accountDAO;
  private final VideoRepository videoRepository;
  private final SecurityService securityService;

  public List<CommentResponse> findParentComments(UUID videoId) {
    List<CommentResponse> list = commentDAO.findParentComments(videoId);

    return list;

  }


  public List<CommentResponse> findChildComments(UUID videoId, int groupIndex) {
    List<CommentResponse> list = commentDAO.findChildComments(videoId, groupIndex);

    return list;
  }

  public boolean saveComment(CommentRequest commentRequest) {

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

  public boolean deleteComment(UUID commentId) {
    return commentDAO.deleteComment(commentId);
  }


  private Comments convertDtoToEntityForInsert(CommentRequest commentRequest) {

    Comments comments = new Comments();

    if (commentRequest.getVideoId()!= null) {
      System.out.println(commentRequest.getVideoId());
      Videos videos = videoRepository.findById(commentRequest.getVideoId()).orElse(null);
      System.out.println("asadasd"+videos);
      comments.setVideos(videos);
    }

    if (Objects.nonNull(commentRequest.getAccountId())) {
      Accounts tempAccounts = new Accounts();
      tempAccounts.setAccountId((UUID) commentRequest.getAccountId());

      Accounts accounts = accountDAO.selectAccountId(tempAccounts);
      System.out.println(accounts);
      comments.setAccounts(accounts);
    }

    if (Objects.nonNull(commentRequest.getContent())) {
      comments.setContent(commentRequest.getContent());
    }

    if (Objects.nonNull(commentRequest.getParentChild())) {
      comments.setParentChild(commentRequest.getParentChild());
    }

    if (Objects.nonNull(commentRequest.getGroupIndex())) {
      comments.setGroupIndex(commentRequest.getGroupIndex());
    }

    LocalDateTime now = LocalDateTime.now();

    commentRequest.setCreateAt(Timestamp.valueOf(
        now.plusHours(9).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    comments.setCreateAt(commentRequest.getCreateAt());

    return comments;
  }

  private Comments convertDtoToEntityForUpdate(CommentRequest commentRequest) {
    Comments comments = commentDAO.findByCommentId(commentRequest.getCommentId());
    if (comments == null) {
      throw new ResourceNotFoundException("해당하는 댓글을 찾을 수 없음");
    }
    if (Objects.nonNull(commentRequest.getContent())) {
      comments.setContent(commentRequest.getContent());
    }

    LocalDateTime now = LocalDateTime.now();

    commentRequest.setCreateAt(Timestamp.valueOf(
        now.plusHours(9).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
    comments.setCreateAt(commentRequest.getCreateAt());

    return comments;
  }


}
