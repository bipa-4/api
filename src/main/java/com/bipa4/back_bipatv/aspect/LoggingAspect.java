package com.bipa4.back_bipatv.aspect;

import com.bipa4.back_bipatv.service.LogService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {


  private final LogService logService;


  @After("@annotation(org.springframework.web.bind.annotation.PutMapping)")
  public void putLogAfterRequest() {
    // 요청을 처리한 후 로깅
    // 예: 로깅 메서드를 호출하여 응답 로그 기록
    try {
      // 요청을 처리한 후 로깅
      // 예: 로깅 메서드를 호출하여 응답 로그 기록
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
      HttpServletResponse httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
      logService.saveLog(request, httpServletResponse, "수정");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @After("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
  public void deleteLogAfterRequest() {
    // 요청을 처리한 후 로깅
    // 예: 로깅 메서드를 호출하여 응답 로그 기록
    try {
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
      HttpServletResponse httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
      logService.saveLog(request, httpServletResponse, "삭제");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @After("@annotation(org.springframework.web.bind.annotation.PostMapping)")
  public void insertLogAfterRequest() {
    // 요청을 처리한 후 로깅
    // 예: 로깅 메서드를 호출하여 응답 로그 기록
    try {
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
      HttpServletResponse httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
      if (!request.getRequestURI().contains("logout")) {
        logService.saveLog(request, httpServletResponse, "요청");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }


  }
}
