package com.bipa4.back_bipatv.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.bipa4.back_bipatv.dto.video.GetCategoryNameRequestDto;
import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetSearchResponseDto;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.dto.video.PutUpdateRequestDto;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.repository.VideoChannelRepository;
import com.bipa4.back_bipatv.repository.VideoRepository;
import com.bipa4.back_bipatv.security.SecurityService;
import java.io.File;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VideoService {

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  private final VideoRepository videoRepository;
  private final SecurityService securityService;
  private final VideoChannelRepository videoChannelRepository;
  private final AmazonS3 amazonS3;

  @Transactional
  public List<GetSearchResponseDto> search(String searchQuery) {
    List<GetSearchResponseDto> dto = videoChannelRepository.findBySearchQuery(searchQuery);
    return dto;
  }

  public Long check(String accessToken, UUID videoId) {
    Accounts account = securityService.getSubjectAccount(accessToken);
    return videoRepository.checkOwner(account, videoId);
  }

  @Transactional
  public Long removeVideo(UUID videoId, String accessToken) {
    Accounts account = securityService.getSubjectAccount(accessToken);
    return videoRepository.remove(videoId, account);
  }

  @Transactional
  public int uploadVideo(PostUploadRequestDto requestdto, String token) {
    UUID uuid = generateUUIDv1(requestdto.getContent());
    return videoRepository.insert(requestdto, token, uuid);
  }

  @Transactional
  public int updateVideo(UUID id, PutUpdateRequestDto requestDto, String accessToken) {
    Accounts account = securityService.getSubjectAccount(accessToken);
    return videoRepository.update(id, requestDto, account);
  }

  public List<GetVideoResponseDto> getAllVideos(String page, int pageSize) {
    UUID uuid;
    if (page == null) {
      uuid = videoRepository.lastUUID();
    } else {
      uuid = UUID.fromString(page);
    }
    return videoRepository.getAllVideos(uuid, pageSize);
  }

  public String getNextUUID(UUID uuid) {
    return videoRepository.getNextUUID(uuid);
  }

  public List<GetVideoResponseDto> getCategoryVideos(UUID category, String page, int pageSize) {
    UUID uuid;
    if (page == null) {
      uuid = videoRepository.lastCategoryUUID(category);
    } else {
      uuid = UUID.fromString(page);
    }
    return videoRepository.findByCategory(category, uuid, pageSize);
  }

  public List<GetCategoryNameRequestDto> getCategoryNames() {
    return videoRepository.getCategoryNames();
  }


  public List<GetVideoResponseDto> getViewsTop10Videos() {
    return videoRepository.findByViews();
  }

  @Transactional
  public int updateViews() {
    return videoRepository.updateViews();
  }

  public GetDetailResponseDto getVideoDetail(UUID id) {
    return videoRepository.getDetail(id);
  }

  @Transactional
  public int plusViews(UUID videoId) {
    return videoRepository.plusViews(videoId);
  }

  public boolean getLike(UUID videoId, String token) {
    if (videoRepository.getFavorite(videoId, token) == 1) {
      return true;
    }
    return false;
  }

  @Transactional
  public int like(UUID videoId, String token) {
    return videoRepository.plusLike(videoId, token);
  }

  @Transactional
  public int cancelLike(UUID videoId, String token) {
    return videoRepository.minusLike(videoId, token);
  }

  public UUID generateUUIDv1(String content) {
    // Generate a UUID version 1 using current time and MAC address
    long timestamp = System.currentTimeMillis();
    long timeLow = timestamp & 0xFFFFFFFFL;
    long timeMid = (timestamp >> 32) & 0xFFFFL;
    long timeHigh = (timestamp >> 48) & 0x0FFF0L;
    long customNode = content.hashCode() & 0xFFFFFFFFFFFFL;

    long mostSigBits = (timeLow << 32) | (timeMid << 16) | timeHigh | 0x1000L;
    long leastSigBits = (customNode << 16) | 0x800000000000L;

    return new UUID(mostSigBits, leastSigBits);
  }

  public boolean deleteS3File(String fileName) {
    try {
      amazonS3.deleteObject(bucketName, (fileName).replace(File.separatorChar, '/'));
      return true;
    } catch (AmazonServiceException e) {
      System.out.println(e);
      return false;
    }
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
