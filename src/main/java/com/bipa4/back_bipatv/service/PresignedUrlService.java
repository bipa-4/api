package com.bipa4.back_bipatv.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresignedUrlService {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;

  public String getPreSignedUrl(String fileName) {

    // 파일이름
    String fileId = UUID.randomUUID().toString();

    // 만료 기간 정하기
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MINUTE, 10);

    // presigned-url 생성
    GeneratePresignedUrlRequest generateVideoPresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucket, fileId + fileName)
            .withMethod(HttpMethod.PUT)
            .withExpiration(calendar.getTime());

    generateVideoPresignedUrlRequest.addRequestParameter(
        Headers.S3_CANNED_ACL,
        CannedAccessControlList.PublicRead.toString());

    return amazonS3.generatePresignedUrl(generateVideoPresignedUrlRequest).toString();
  }
}
