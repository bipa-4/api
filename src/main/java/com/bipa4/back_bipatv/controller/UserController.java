package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.video.GetImageUrlResponseDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequiredArgsConstructor
public class UserController {


  private final UserService userService;
  private final SecurityService securityService;
  private final PresignedUrlService presignedUrlService;
  private final int ACCESSTOKEN_EXP_TIME = 60 * 60 * 2;//2시간
  private final int REFRESHTOKEN_EXP_TIME = 60 * 60 * 6;//6시간


  @ApiOperation(value = "Social_Login", notes = "소셜 로그인")
  @GetMapping("/auth/{registrationId}/callback")
  public void doLogin(@RequestParam String code, @PathVariable String registrationId,
      HttpServletResponse httpresponse) {

    Map<String, Cookie> cookieMap = userService.socialLogin(code, registrationId);

    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken",
            cookieMap.get("refreshToken").getValue())
        .path("/")
        .maxAge(REFRESHTOKEN_EXP_TIME)// refreshToken도 6시간
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .build();
    httpresponse.addHeader("Set-Cookie", refreshCookie.toString());
    System.out.println("acc=" + cookieMap.get("accessToken").getValue());
    ResponseCookie accessCookie = ResponseCookie.from("accessToken",
            cookieMap.get("accessToken").getValue())
        .path("/")
        .maxAge(ACCESSTOKEN_EXP_TIME)//기간 2시간
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .build();
    httpresponse.addHeader("Set-Cookie", accessCookie.toString());
  }

  @ApiOperation(value = "userUpdate", notes = "유저 정보 수정")
  @PutMapping("/account")
  public ResponseEntity<Accounts> updateAccount(@CookieValue(value = "accessToken") String code,
      @RequestBody @Validated Accounts accounts) {

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
  public ResponseEntity<GetImageUrlResponseDto> saveFile(
      @RequestParam("imageName") String imageName) {
    GetImageUrlResponseDto responseDto = presignedUrlService.getPreSignedUrl(imageName);
    if (responseDto != null) {
      return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
  }

  @ApiOperation(value = "CheckAccount", notes = "accessToken에 맞는 Account반환")
  @GetMapping("/account/check")
  public ResponseEntity<Accounts> checkAccessTokenToAccount(
      @CookieValue(name = "accessToken", required = false) String accessToken) {
    if (accessToken == null) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    System.out.println("accessToken:" + accessToken);
    Accounts findAccount = securityService.getSubjectAccount(accessToken);
    return findAccount == null ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(findAccount, HttpStatus.OK);
  }

  @ApiOperation(value = "Logout", notes = "로그아웃기능")
  @PostMapping("/account/logout")
  public ResponseEntity<Boolean> doLogout(HttpServletResponse httpresponse,
      @CookieValue(value = "refreshToken") String refreshToken,
      @CookieValue(value = "accessToken") String accessToken) {

    boolean result = userService.logout(refreshToken, accessToken);
    //쿠키를 삭제하기 위해선 쿠키의 이름을 같게하고 유효기간을 0을 주어 삭제한다.
    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken",
            "")
        .path("/")
        .maxAge(0)
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .build();
    httpresponse.addHeader("Set-Cookie", refreshCookie.toString());

    ResponseCookie accessCookie = ResponseCookie.from("accessToken",
            "")
        .path("/")
        .maxAge(0)
        .httpOnly(true)
        .secure(true)
        .sameSite("None")
        .build();
    httpresponse.addHeader("Set-Cookie", accessCookie.toString());
    return result ? new ResponseEntity<>(true, HttpStatus.OK)
        : new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
  }

  @ApiOperation(value = "Delete Account", notes = "회원 탈퇴")
  @DeleteMapping("/account")
  public void deleteAccount(
      @CookieValue("accessToken") String accessToken) {
    userService.deleteAccount(accessToken);
  }
}
