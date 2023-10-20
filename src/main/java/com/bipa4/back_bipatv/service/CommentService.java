package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.AccountDAO;
import com.bipa4.back_bipatv.dao.CommentDAO;
import com.bipa4.back_bipatv.dataType.ErrorCode;
import com.bipa4.back_bipatv.dto.comment.ChildCommentResponse;
import com.bipa4.back_bipatv.dto.comment.CommentRequest;
import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Comments;
import com.bipa4.back_bipatv.entity.Videos;
import com.bipa4.back_bipatv.exception.CustomApiException;
import com.bipa4.back_bipatv.exception.ResourceNotFoundException;
import com.bipa4.back_bipatv.repository.VideoRepository;
import com.bipa4.back_bipatv.security.SecurityService;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.transaction.Transactional;
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


  public List<ChildCommentResponse> findChildComments(UUID videoId, int groupIndex) {
    List<ChildCommentResponse> list = commentDAO.findChildComments(videoId, groupIndex);
    return list;
  }

  @Transactional
  public boolean saveParentComment(Accounts account, CommentRequest commentRequest) {
    Comments comments = convertDtoToEntityForInsert(account, commentRequest, null);
    return commentDAO.saveParentComment(comments);
  }

  public boolean saveChildComment(Accounts account, CommentRequest commentRequest,
      Integer groupIndex) {

    Comments comments = convertDtoToEntityForInsert(account, commentRequest, groupIndex);
    return commentDAO.saveChildComment(comments);

  }

  public boolean updateComment(CommentRequest commentRequest) {

    Comments comments = convertDtoToEntityForUpdate(commentRequest);
    return commentDAO.saveParentComment(comments);
  }

  public boolean deleteComment(UUID commentId, String accessToken) {
    return commentDAO.deleteComment(commentId, accessToken);
  }


  private Comments convertDtoToEntityForInsert(Accounts account, CommentRequest commentRequest,
      Integer groupIndex) {
    Comments comments = new Comments();

    Videos video = videoRepository.findById(commentRequest.getVideoId()).orElse(null);

    // 비디오가 없다면.
    if (video == null) {
      throw new CustomApiException(ErrorCode.NO_EXIST_VIDEO);
    }

    Timestamp now = Timestamp.valueOf(
        LocalDateTime.now().plusHours(9)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    groupIndex = commentRequest.getParentChild() == 0 ?
        findParentComments(commentRequest.getVideoId()).size() + 1 : groupIndex;

    // Comments insert할 부분.
    try {
      comments.setVideos(video);
      comments.setAccounts(account);
      comments.setGroupIndex(groupIndex);
      comments.setContent(commentRequest.getContent());
      comments.setParentChild(commentRequest.getParentChild());
      comments.setCreateAt(now);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.INSERT_DTO_ERROR);
    }
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
