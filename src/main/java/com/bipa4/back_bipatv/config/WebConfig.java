package com.bipa4.back_bipatv.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins(
            "http://localhost:3000")//addAllowedOrigin : 허용할 출처를 입력(프론트엔드의 도메인과 포트를 입력하면 된다.)
        .allowedMethods("*")//addAllowedMethod : 허용할 Http Method를 입력
        .allowedHeaders("*")//addAllowedHeader : 허용할 헤더를 입력
        .allowCredentials(true).maxAge(3600);//setAllowCredentials : 쿠키 요청을 허용하도록 true로 설정
  }
}
