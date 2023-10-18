package com.bipa4.back_bipatv.dataType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

  DELETE_ERROR("CANNOT_DELETE", "해당 콘텐츠를 삭제할 수 없습니다."),
  PRESIGNED_URL_ERROR("CANNOT_GET_PRESIGNED_URL", "presigned-url 발급에 실패하였습니다."),
  UPLOAD_ERROR("CANNOT_UPLOAD", "업로드가 실패했습니다."),
  UPDATE_ERROR("CANNOT_UPDATE", "업데이트가 실패했습니다."),
  LIKE_ERROR("CANNOT_LIKE", "좋아요 추가를 실패했습니다."),
  UNLIKE_ERROR("CANNOT_UNLIKE", "좋아요 취소를 실패했습니다."),
  VIEW_LOG_ERROR("CANNOT_UPDATE_VIEWLOG_TABLE", "한 시간 전의 테이블 정보를 업데이트하는 것을 실패했습니다."),
  READ_ERROR("CANNOT_READ", "해당 값이 없습니다."),
  AUTHORITY_ERROR("AUTHORITY_ERROR", "권한이 없습니다."),
  LOGIN_ERROR("LOGIN_ERROR", "로그인 에러입니다."),
  LOGOUT_ERROR("LOGOUT_ERROR", "로그아웃 에러입니다."),
  ACCESSTOKEN_ERROR("ACCESSTOKEN_ERROR", "엑세스 토큰 발급이 실패했습니다.");

  private final String code;
  private final String message;
}
