package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  private final int EXP_TIME = 2 * 1000 * 60 * 60;


  @ApiOperation(value = "Social_Login", notes = "소셜 로그인")
  @GetMapping("/auth/{registrationId}/callback")
  public String doLogin(@RequestParam String code, @PathVariable String registrationId,
      HttpServletResponse httpresponse) {

    Map<String, Cookie> cookie = userService.socialLogin(code, registrationId);
    httpresponse.addCookie(cookie.get("refreshToken"));
    httpresponse.addCookie(cookie.get("accessToken"));

    return cookie.get("refreshToken").getName();
  }


}
