package com.bipa4.back_bipatv.advisor;

import com.bipa4.back_bipatv.exception.JwtNotFoundException;
import com.bipa4.back_bipatv.service.UserService;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class CustomRestControllerAdvice {

  private final UserService userService;
  private final int ACCESSTOKEN_EXP_TIME = 60 * 60 * 2;//2시간

  @ExceptionHandler(JwtNotFoundException.class)
  public void jwtExceptionHandler(@CookieValue("refreshToken") String refreshToken,
      HttpServletResponse httpresponse) {
    String accessToken = userService.createAccessTokenToRefreshToken(refreshToken);
    if (accessToken != null) {
      ResponseCookie accessCokkie = ResponseCookie.from("accessToken", accessToken)
          .path("/")
          .maxAge(ACCESSTOKEN_EXP_TIME)// refreshToken도 6시간
          .httpOnly(true)
          .secure(true)
          .sameSite("None")
          .build();
      httpresponse.addHeader("Set-Cookie", accessCokkie.toString());
    }
  }

}
