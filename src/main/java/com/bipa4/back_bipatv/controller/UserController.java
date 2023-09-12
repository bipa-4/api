package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.model.KakaoProfile;
import com.bipa4.back_bipatv.model.OAuthToken;
import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.UserService;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class UserController {

  @Autowired
  private UserService userService;
  @Autowired
  private SecurityService securityService;
  private final int EXP_TIME = 2 * 1000 * 60 * 60;

  //  private String url = null;
  @GetMapping("/auth/joinForm")
  public String joinForm() {
    return "user/joinForm";
  }

  @GetMapping("/auth/loginForm")
  public String loginForm() {
    return "user/loginForm";
  }

  @GetMapping("/auth/kakao/callback")//윤서가 카카오 로그인 버튼을 클릭 <button link="login">
  public @ResponseBody String kakaoCallback(String code,
      HttpServletResponse httpresponse) {//Data를 리턴해주는 컨트롤러 함수

    OAuthToken oauthToken = userService.create_oauthToken(code);//oauthToken만들기
    KakaoProfile kakaoProfile = userService.getUser_info(oauthToken);//Token에서 account에 대한 정보 들고오기
    String refreshToken = null;
    if (userService.findAccount(kakaoProfile)) {//db에 회원 정보가 저장되어 있는가  //--회원이 있는 경우--
      refreshToken = userService.selectAccount(kakaoProfile).getRefreshToken();
      System.out.println("이미 있는 회원입니다. 바로 로그인하기");
    } else {//--회원정보가 DB에 없는 경우--
      refreshToken = securityService.createRefreshToken();

//      userService.kakao_createUser(kakaoProfile); //redis사용하려면 사용할 메소드
      userService.kakao_createUser(kakaoProfile, refreshToken); //db에 그냥 refreshToken저장하려면 사용할 메소드

    }
    System.out.println("refreshToken: " + refreshToken);
    String loginAccountToken = securityService.createToken(kakaoProfile,
        EXP_TIME);
    System.out.println("accessToken: " + loginAccountToken);

    Cookie refreshCookie = userService.createCookie("RefreshToken",
        userService.selectAccount(kakaoProfile).getRefreshToken());
    Cookie accessCookie = userService.createCookie("AccessToken", loginAccountToken);

    System.out.println(refreshCookie.getName());
    httpresponse.addCookie(refreshCookie);
    httpresponse.addCookie(accessCookie);

    return securityService.getSubject(loginAccountToken);
  }


  @GetMapping("/user/updateForm")
  public String updateForm() {
    return "user/updateForm";
  }

}
