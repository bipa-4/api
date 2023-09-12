package com.bipa4.back_bipatv.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bipa4.back_bipatv.dao.VideoRepository;
import com.bipa4.back_bipatv.dto.PostSaveRequestDto;
import java.io.IOException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class VideoService {

  private final VideoRepository videoRepository;
  private final AmazonS3Client amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  // Upload to db.
  @Transactional
  public void upload(PostSaveRequestDto requestDto) {
    videoRepository.save(requestDto.toEntity());
  }

  // Upload to storage.
  public String uploadFile(MultipartFile file, String dirName, String fileName) throws IOException {
    String filePath = dirName + "/" + fileName;

    amazonS3Client.putObject(
        new PutObjectRequest(bucket, filePath, file.getInputStream(), null).withCannedAcl(
            CannedAccessControlList.PublicRead));
    return amazonS3Client.getUrl(bucket, filePath).toString();
  }
}
