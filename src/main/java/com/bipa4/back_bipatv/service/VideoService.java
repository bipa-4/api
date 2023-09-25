package com.bipa4.back_bipatv.service;

//import com.amazonaws.services.s3.AmazonS3Client;

import com.bipa4.back_bipatv.dto.video.GetSearchResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.repository.VideoChannelRepository;
import com.bipa4.back_bipatv.repository.VideoRepository;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VideoService {

  private static VideoRepository videoRepository;
  private final VideoChannelRepository videoChannelRepository;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  // Upload to db.
  @Transactional
  public int upload(PostUploadRequestDto requestDto) {
    return videoRepository.insert(requestDto);
  }

  @Transactional
  public List<GetSearchResponseDto> search(String searchQuery) {
    return videoChannelRepository.findBySearchQuery(searchQuery);
  }

  // Upload to storage.
//  public String uploadFile(MultipartFile file, String dirName, String fileName) throws IOException {
//    String filePath = dirName + "/" + fileName;
//
//    amazonS3Client.putObject(
//        new PutObjectRequest(bucket, filePath, file.getInputStream(), null).withCannedAcl(
//            CannedAccessControlList.PublicRead));
//    return amazonS3Client.getUrl(bucket, filePath).toString();
//  }

//  public List<GetAllResponseDto> pageViews(int page, int size) {
//    return videoChannelRepository.findAllWithChannelUsingJoin();
//  }
}
