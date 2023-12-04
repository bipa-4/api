package com.bipa4.back_bipatv.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class SpeedCheckAspect {

  @Around("execution(* com.bipa4.back_bipatv.controller..*(..)) ")
  public Object executionAspect(ProceedingJoinPoint joinPoint) throws Throwable {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    Object result = joinPoint.proceed();

    stopWatch.stop();
    System.out.println("걸린시간[" + joinPoint + "] : " + stopWatch.getTotalTimeSeconds() + "ms");

    return result;
  }
}
