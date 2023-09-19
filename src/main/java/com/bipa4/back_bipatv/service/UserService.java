package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.AccountDAO;
import com.bipa4.back_bipatv.dao.ChannelDAO;
import com.bipa4.back_bipatv.dataType.ELogin_Type;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.entity.RefreshToken;
import com.bipa4.back_bipatv.repository.RedisRepository;
import com.bipa4.back_bipatv.security.SecurityService;
import com.fasterxml.jackson.databind.JsonNode;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

  private final int EXP_TIME = 2 * 1000 * 60 * 60;
  private final Environment env;
  private final RestTemplate restTemplate = new RestTemplate();

  public UserService(Environment env) {
    this.env = env;
  }

  @Autowired
  private AccountDAO accountDAO;
  @Autowired
  private ChannelDAO channelDAO;
  @Autowired
  private SecurityService securityService;
  @Autowired
  private RedisRepository redisRepository;
  @Autowired
  private ChannelService channelService;

  private void insertUser(Accounts accounts, String refreshToken) {

    Timestamp now = new Timestamp(System.currentTimeMillis());
//    accounts.setName(accounts.getName());//이름 권한을 카카오 문서 동의가 안됨-> 닉네임으로 대체
//    accounts.setLoginId(accounts.getLoginId());//Id 형식은 kakao
//    accounts.setLoginType(accounts.getLoginType());
    accounts.setJoinDate(now);
//    accounts.setRefreshToken(refreshToken);
    System.out.println(accounts);
    accountDAO.createAccount(accounts);

  }

  private void insertChannels(Accounts accounts) {
    Channels channels = new Channels();
    channels.setName(accounts.getLoginId() + "_Channel");
    channels.setAccounts(accounts);
    channelDAO.createChannel(channels);
  }

  private boolean findAccount(Accounts accounts) {
    return accountDAO.findAccount(accounts);
  }

  private Accounts selectAccount(Accounts accounts) {
    return accountDAO.selectAccount(accounts);
  }

  private Cookie createCookie(String name, String token) {
    Cookie cookie = new Cookie(name, token);

    return cookie;
  }


  private String getAccessToken(String authorizationCode, String registrationId) {
    String clientId = env.getProperty("oauth2." + registrationId + ".client-id");
    String clientSecret = env.getProperty("oauth2." + registrationId + ".client-secret");
    String redirectUri = env.getProperty("oauth2." + registrationId + ".redirect-uri");
    String tokenUri = env.getProperty("oauth2." + registrationId + ".token-uri");
//  Httpheader 오브젝트 생성
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type",
        "application/x-www-form-urlencoded;charset=utf-8");//-->http데이터가 key:value형식이라는 것을 알려주는거임
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//  Httpbody 오브젝트 생성
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", authorizationCode);
    params.add("client_id", clientId);
    params.add("client_secret", clientSecret);
    params.add("redirect_uri", redirectUri);
    params.add("grant_type", "authorization_code");

//  Httpheader와 body를 하나의 오브젝트에 담기
    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

    ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity,
        JsonNode.class);
    JsonNode accessTokenNode = responseNode.getBody();
    return accessTokenNode.get("access_token").asText();
  }

  private JsonNode getUserResource(String accessToken, String registrationId) {
    String resourceUri = env.getProperty("oauth2." + registrationId + ".resource-uri");

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    HttpEntity entity = new HttpEntity(headers);
    return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
  }

  public Map<String, Cookie> socialLogin(String code,
      String registrationId) {//구글, 카카오에게 로그인 정보를 받은 후 실행되는거임 인증은 카카오랑 구글이 함
    String accessToken = getAccessToken(code, registrationId);
    String refreshToken = securityService.createRefreshToken();
    JsonNode userResourceNode = getUserResource(accessToken, registrationId);

    Accounts accounts = new Accounts();

    switch (registrationId) {
      case "google": {
        accounts.setLoginId("google_" + userResourceNode.get("id").asText());
        accounts.setEMail(userResourceNode.get("email").asText());
        accounts.setName(userResourceNode.get("name").asText());
        accounts.setLoginType(ELogin_Type.GOOGLE);
        System.out.println(accounts);
        break;
      }
      case "kakao": {
        accounts.setLoginId("kakao_" + userResourceNode.get("id").asText());
        accounts.setEMail(userResourceNode.get("kakao_account").get("email").asText());
        accounts.setName(
            userResourceNode.get("kakao_account").get("profile").get("nickname").asText());
        accounts.setLoginType(ELogin_Type.KAKAO);
        System.out.println(accounts);
        break;
      }
      default: {
        throw new RuntimeException("UNSUPPORTED SOCIAL TYPE");
      }
    }
    //유저 아이디에 대한 리프레쉬 토큰 검샘
    if (!findAccount(accounts)) {//
      insertUser(accounts, refreshToken); //db에 그냥 refreshToken저장하려면 사용할 메소드
      insertChannels(accounts);
    }
    if (redisRepository.findById(refreshToken).isEmpty()) {
      RefreshToken Rtoken = new RefreshToken(refreshToken, accounts.getLoginId());
      redisRepository.save(Rtoken);
    }
//    System.out.println(
//        "redis에서 꺼낸 애: " + redisRepository.findById(refreshToken).get().getRefreshToken());
//    System.out.println(
//        "redis에서 꺼낸 애: " + redisRepository.findById(refreshToken).get().getMemberId());
    System.out.println("refreshToken: " + refreshToken);
    String loginAccountToken = securityService.createToken(accounts, EXP_TIME);
    System.out.println("accessToken: " + loginAccountToken);
    Cookie refreshCookie = createCookie("RefreshToken", refreshToken);
    Cookie accessCookie = createCookie("AccessToken", loginAccountToken);

    System.out.println(refreshCookie.getName());
    System.out.println(loginAccountToken);
  
    // 재로그인 요청

    System.out.println("AccessToken 재요청 값:" + createAccessTokenToRefreshToken(refreshToken));
    Map<String, Cookie> map = new HashMap<>();
    map.put("refreshToken", refreshCookie);
    map.put("accessToken", accessCookie);

    return map;
  }

  public String createAccessTokenToRefreshToken(
      String refreshToken) {//리플레시 토큰으로 엑세스 토큰을 다시 만들어달라는 것을 요청할 때 사용할 메소드
    Optional<RefreshToken> optRefreshToken = redisRepository.findById(refreshToken);
    if (optRefreshToken.isPresent()) {
      Accounts dummyAccount = new Accounts();
      dummyAccount.setLoginId(optRefreshToken.get().getMemberId());
      return securityService.createToken(accountDAO.selectAccount(dummyAccount), EXP_TIME);
    } else {//재 로그인 요청
      return "재로그인해주세요";
    }
  }

}
