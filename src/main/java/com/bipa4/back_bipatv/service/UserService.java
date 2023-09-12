package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.AccountDAO;
import com.bipa4.back_bipatv.dataType.ELogin_Type;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.model.KakaoProfile;
import com.bipa4.back_bipatv.model.OAuthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import javax.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

  @Value("${kakao.restApiKey}")
  private String client_id;
  @Value("${kakao.authUrl}")
  private String authUrl;
  @Value("${kakao.redirectUrl}")
  private String redirect_url;
  @Value("${kakao.userApiUrl}")
  private String userApiUrl;

  @Autowired
  private AccountDAO accountDAO;


  public OAuthToken create_oauthToken(String code) {
    //Retrofit2 -->안드로이드에서 사용
    //Okhttp
    //RestTemplate
    //POST방식으로 key:value데이터를 요청(카카오 쪽으로)해야한다.
    RestTemplate rt = new RestTemplate();//-->http요청을 엄청 편하게 할 수 있다.

    //Httpheader 오브젝트 생성
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type",
        "application/x-www-form-urlencoded;charset=utf-8");//-->http데이터가 key:value형식이라는 것을 알려주는거임

    //Httpbody 오브젝트 생성
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();//body에 담을 데이터
    params.add("grant_type", "authorization_code");//-->아래 값들을 다 변수로 만들어서 사용하는 것이 좋음
    params.add("client_id", client_id);
    params.add("redirect_uri", redirect_url);
    params.add("code", code);

    //Httpheader와 body를 하나의 오브젝트에 담기
    HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params,
        headers);//kakaoTokenRequest 이 정보는 body와 header 값을 가진 정보가 됨

    ResponseEntity<String> response = rt.exchange(authUrl,// 요청할 url 주소
        HttpMethod.POST,//방식
        kakaoTokenRequest,//이 데이터를
        String.class // 이 형식으로 response에 받음
    );

    //이제 받아온 데이터(JSON)를 그냥 처리하기 어려우니 Object에 담기
    //Gson, Json Simple, ObjectMapper
    ObjectMapper objectMapper = new ObjectMapper();
    OAuthToken oauthToken = null;
    try {
      oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return oauthToken;
  }

  public KakaoProfile getUser_info(OAuthToken oauthToken) {
    RestTemplate rt = new RestTemplate();//-->http요청을 엄청 편하게 할 수 있다.

    //Httpheader 오브젝트 생성
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + oauthToken.getAccess_token());
    headers.add("Content-type",
        "application/x-www-form-urlencoded;charset=utf-8");//-->http데이터가 key:value형식이라는 것을 알려주는거임

    //Httpheader를 오브젝트에 담기
    HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(
        headers);//kakaoProfileRequest header 값을 가진 정보가 됨

    ResponseEntity<String> response = rt.exchange(userApiUrl,// 요청할 url 주소
        HttpMethod.POST,//방식
        kakaoProfileRequest,//이 데이터를
        String.class // 이 형식으로 response에 받음
    );
    ObjectMapper objectMapper = new ObjectMapper();
    KakaoProfile kakaoProfile = null;
    try {
      kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return kakaoProfile;
  }

  public void kakao_createUser(KakaoProfile kakaoProfile, String refreshToken) {

    Timestamp now = new Timestamp(System.currentTimeMillis());
    System.out.println(now);
    Accounts accounts = new Accounts();
    accounts.setName(kakaoProfile.getKakao_account().getProfile()
        .getNickname());//이름 권한을 카카오 문서 동의가 안됨-> 닉네임으로 대체
    accounts.setLoginId("kakao_" + kakaoProfile.getId());//Id 형식은 kakao
    accounts.setJoinDate(now);//날짜 형식이 이게 맞는지 확인
    accounts.setLoginType(ELogin_Type.KAKAO);
    accounts.setRefreshToken(refreshToken);
//        accounts.setProfileUrl();
    accountDAO.createAccount(accounts);

  }

  public void kakao_createUser(KakaoProfile kakaoProfile) {

    Timestamp now = new Timestamp(System.currentTimeMillis());
    System.out.println(now);
    Accounts accounts = new Accounts();
    accounts.setName(kakaoProfile.getKakao_account().getProfile()
        .getNickname());//이름 권한을 카카오 문서 동의가 안됨-> 닉네임으로 대체
    accounts.setLoginId("kakao_" + kakaoProfile.getId());//Id 형식은 kakao
    accounts.setJoinDate(now);//날짜 형식이 이게 맞는지 확인
    accounts.setLoginType(ELogin_Type.KAKAO);

//        accounts.setProfileUrl();
    accountDAO.createAccount(accounts);

  }

  public boolean findAccount(KakaoProfile kakaoProfile) {
    return accountDAO.findAccount(kakaoProfile);
  }

  public Accounts selectAccount(KakaoProfile kakaoProfile) {
    return accountDAO.selectAccount(kakaoProfile);
  }

  public Cookie createCookie(String name, String token) {
    Cookie cookie = new Cookie(name, token);

    return cookie;
  }
}
