package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.CustomApiException;
import com.bipa4.back_bipatv.dto.ErrorResult;
import com.bipa4.back_bipatv.exception.ResourceNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
@RestControllerAdvice()
public class ExceptionController {

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(CustomApiException.class)
  public ErrorResult customExHandler(CustomApiException e) {
    return new ErrorResult(e.getErrorMessage().getCode(), e.getErrorMessage().getMessage());
  }


  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(DataAccessException.class)
  public ErrorResult dataAccessExHandler(DataAccessException e) {
    return new ErrorResult("DataAccessException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(IllegalStateException.class)
  public ErrorResult illegalStateExHandler(IllegalStateException e) {
    return new ErrorResult("IllegalStateException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(IllegalArgumentException.class)
  public ErrorResult illegalArgumentExHandler(IllegalArgumentException e) {
    return new ErrorResult("IllegalArgumentException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(NullPointerException.class)
  public ErrorResult nullPointerExHandler(NullPointerException e) {
    return new ErrorResult("NullPointerException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(IOException.class)
  public ErrorResult IOExHandler(IOException e) {
    return new ErrorResult("IOException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(SecurityException.class)
  public ErrorResult securityExHandler(SecurityException e) {
    return new ErrorResult("SecurityException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(AuthenticationException.class)
  public ErrorResult authenticationExHandler(AuthenticationException e) {
    return new ErrorResult("AuthenticationException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(AccessDeniedException.class)
  public ErrorResult accessDeniedExHandler(AccessDeniedException e) {
    return new ErrorResult("AccessDeniedException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(HttpClientErrorException.class)
  public ErrorResult httpClientErrorExHandler(HttpClientErrorException e) {
    return new ErrorResult("HttpClientErrorException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(HttpServerErrorException.class)
  public ErrorResult httpServerErrorExHandler(HttpServerErrorException e) {
    return new ErrorResult("HttpServerErrorException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(ResourceNotFoundException.class)
  public ErrorResult resourceNotFoundExHandler(ResourceNotFoundException e) {
    return new ErrorResult("ResourceNotFoundException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(ConversionFailedException.class)
  public ErrorResult conversionFailedExHandler(ConversionFailedException e) {
    return new ErrorResult("ConversionFailedException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResult methodArgumentNotValidExHandler(MethodArgumentNotValidException e) {
    return new ErrorResult("MethodArgumentNotValidException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler(ConstraintViolationException.class)
  public ErrorResult constraintViolationExHandler(ConstraintViolationException e) {
    return new ErrorResult("ConstraintViolationException", e.getMessage());
  }

  @ResponseStatus(HttpStatus.ACCEPTED)
  @ExceptionHandler
  public ErrorResult exHandler(Exception e) {
    return new ErrorResult("EX", e.getMessage());
  }
}
