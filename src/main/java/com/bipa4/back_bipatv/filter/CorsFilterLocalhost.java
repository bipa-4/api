//package com.bipa4.back_bipatv.filter;
//
//import java.io.IOException;
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CorsFilterLocalhost implements Filter {
//
//  @Override
//  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//      throws IOException, ServletException {
//    HttpServletResponse response = (HttpServletResponse) res;
//    HttpServletRequest request = (HttpServletRequest) req;
//    String origin = request.getHeader("origin");
//    System.out.println("origin값:" + origin);
//    if (origin.equals("http://localhost:8080") || origin.equals(
//        "https://https://bipa-streamwave.vercel.app")) {
//      response.setHeader("Access-Control-Allow-Origin", origin);
//    }
//    response.setHeader("Access-Control-Allow-Credentials", "true");
//    response.setHeader("Access-Control-Allow-Methods", "*");
//    response.setHeader("Access-Control-Max-Age", "3600");
//    response.setHeader("Access-Control-Allow-Headers",
//        "Origin, X-Requested-With, Content-Type, Accept, Authorization");
//    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//      response.setStatus(HttpServletResponse.SC_OK);
//    } else {
//      chain.doFilter(req, res);
//    }
//  }
//
//  @Override
//  public void init(FilterConfig filterConfig) {
//  }
//
//  @Override
//  public void destroy() {
//  }
//}
