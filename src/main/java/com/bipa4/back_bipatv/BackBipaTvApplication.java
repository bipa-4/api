package com.bipa4.back_bipatv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackBipaTvApplication {

  public static void main(String[] args) {
    SpringApplication.run(BackBipaTvApplication.class, args);
  }

}
