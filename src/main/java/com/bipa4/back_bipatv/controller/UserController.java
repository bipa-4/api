package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.exception.ResourceNotFoundException;
import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.PresignedUrlService;
import com.bipa4.back_bipatv.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "AccountController v1")
@RequestMapping(produces = "application/json")
public class UserController {

  @Autowired
  private UserService userService;
  @Autowired
  private SecurityService securityService;
  @Autowired
  private PresignedUrlService presignedUrlService;
  private final int EXP_TIME = 2 * 1000 * 60 * 60;


  @ApiOperation(value = "Social_Login", notes = "소셜 로그인")
  @GetMapping("/auth/{registrationId}/callback")
  public String doLogin(@RequestParam String code, @PathVariable String registrationId,
      HttpServletResponse httpresponse) {

    Map<String, Cookie> cookie = userService.socialLogin(code, registrationId);
    Cookie refreshCookie = cookie.get("refreshToken");
    refreshCookie.setPath("/");
    refreshCookie.setHttpOnly(true);
    refreshCookie.setSecure(true);

    Cookie accessCookie = cookie.get("accessToken");
    accessCookie.setHttpOnly(true);
    accessCookie.setSecure(true);
    accessCookie.setPath("/");

    ResponseCookie test_cookie = ResponseCookie.from("refreshToken",
            cookie.get("refreshToken").getValue())
        .path("/")
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .build();
    httpresponse.addHeader("Set-Cookie", test_cookie.toString());
    return accessCookie.getValue();
  }

  @ApiOperation(value = "Get_Account", notes = "유저 정보 받기")
  @GetMapping("/account/{code}")
  public ResponseEntity<Accounts> getAccountInfo(@PathVariable String code) {
    Accounts loginAccounts = securityService.getSubjectAccount(code);
    if (loginAccounts != null) {
      return new ResponseEntity<>(loginAccounts, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);// front에서 인증 재요청을 요청하기
    }
  }

  @ApiOperation(value = "userUpdate", notes = "유저 정보 수정")
  @PutMapping("/account/{code}")
  public ResponseEntity<Accounts> updateAccount(@PathVariable String code,
      @RequestBody Accounts accounts) {

    Accounts loginAccount = securityService.getSubjectAccount(code);
    System.out.println(accounts);
    try {
      Accounts updatedAccount = userService.updateAccount(loginAccount.getAccountId(), accounts);
      return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    } catch (ResourceNotFoundException e) {
      // 리소스를 찾지 못한 경우 404 에러를 반환
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

  }

  @ApiOperation(value = "I Want S3 URI", notes = "S3 URI요청")
  @PostMapping("/account/presigned")
  public ResponseEntity<String> saveFile(@RequestParam("imageName") String imageName) {
    String url = presignedUrlService.getPreSignedUrl(imageName);
    if (url != null) {
      return new ResponseEntity<>(url, HttpStatus.OK);
    }
    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
  }
}
