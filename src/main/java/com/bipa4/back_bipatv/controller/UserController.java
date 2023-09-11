package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.model.KakaoProfile;
import com.bipa4.back_bipatv.model.OAuthToken;
import com.bipa4.back_bipatv.service.UserService;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserService service;

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
    OAuthToken oauthToken = service.create_oauthToken(code);//oauthToken만들기

//        System.out.println("카카오 엑세스 토큰: "+oauthToken.getAccess_token());

    KakaoProfile kakaoProfile = service.getUser_info(oauthToken);//Token에서 account에 대한 정보 들고오기

    System.out.println("아이디: " + kakaoProfile.getId());
    System.out.println("카카오 이메일: " + kakaoProfile.getKakao_account().getEmail());
    System.out.println("이름: " + kakaoProfile.getKakao_account().getProfile().getNickname());

    if (service.findAccount(kakaoProfile)) {//db에 회원 정보가 저장되어 있는가
      //--회원이 있는 경우--
      System.out.println("이미 있는 회원입니다.");
    } else {
      //--회원이 없는 경우--
      service.kakao_createUser(kakaoProfile);
    }

    //db에 유저 정보 저장, 조회
//        Jwts
//        httpresponse.addCookie(new Cookie("jwt", ));

    {
      return kakaoProfile.getKakao_account()
          .getEmail();//response에는 accesstoken, token_type, refresh_token, id_token, expires_in, scope, refresh_token_expires_in 값이 담김
    }
  }


  @GetMapping("/user/updateForm")
  public String updateForm() {
    return "user/updateForm";
  }

}
