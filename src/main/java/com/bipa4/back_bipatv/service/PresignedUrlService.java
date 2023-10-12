package com.bipa4.back_bipatv.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.cloudfront.util.SignerUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresignedUrlService {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Value("${cloud.aws.cloudFront.distributionDomain}")
  private String distributionDomain;

  @Value("${cloud.aws.cloudFront.keyPairId}")
  private String keyPairId;

  @Value("${cloud.aws.path}")
  private String path;

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

  public String getPreSignedUrlCDN(String fileName) {
    String signedURL = "";

    try {
      SignerUtils.Protocol protocol = SignerUtils.Protocol.http;
      File privateKeyFile = ResourceUtils.getFile(path);
      String[] fileNames = fileName.split("[.]");
      System.out.println(fileNames[0]);
      String s3ObjectKey =
          fileName.split("[.]")[0] + LocalDateTime.now() + fileName.split("[.]")[1];

      Date expirationTime = new Date(System.currentTimeMillis() + 3600000); // 1 hour from now
      System.out.println(expirationTime);

      signedURL = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
          protocol,
          distributionDomain,
          privateKeyFile,
          s3ObjectKey,
          keyPairId,
          expirationTime
      );
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(signedURL);
    return signedURL;
  }
}
