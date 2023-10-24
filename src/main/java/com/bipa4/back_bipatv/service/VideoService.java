package com.bipa4.back_bipatv.service;

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
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VideoService {

  private final VideoRepository videoRepository;
  private final SecurityService securityService;
  private final VideoChannelRepository videoChannelRepository;

  @Transactional
  public List<GetSearchResponseDto> search(String searchQuery) {
    return videoChannelRepository.findBySearchQuery(searchQuery);
  }

  public Boolean check(Accounts account, UUID videoId) {
    return videoRepository.checkOwner(account, videoId);
  }

  @Transactional
  public boolean removeVideo(UUID videoId, Accounts account) {
    return videoRepository.remove(videoId, account);
  }

  @Transactional
  public boolean uploadVideo(PostUploadRequestDto requestdto, Accounts account) {
    UUID uuid = generateUUIDv1(requestdto.getContent());
    return videoRepository.insert(requestdto, account, uuid);
  }

  @Transactional
  public boolean updateVideo(UUID id, PutUpdateRequestDto requestDto, Accounts account) {
    return videoRepository.update(id, requestDto, account);
  }

  public List<GetVideoResponseDto> getAllVideos(UUID page, int pageSize) {
    if (page == null) {
      page = videoRepository.lastUUID();
    }
    return videoRepository.getAllVideos(page, pageSize);
  }

  public UUID getNextUUID(UUID uuid) {
    return videoRepository.getNextUUID(uuid);
  }

  public UUID getNextCategoryUUID(UUID uuid, UUID category) {
    return videoRepository.getNextCategoryUUID(uuid, category);
  }


  public List<GetVideoResponseDto> getCategoryVideos(UUID category, UUID page,
      int pageSize) {
    if (page == null) {
      page = videoRepository.lastCategoryUUID(category);
    }
    return (List<GetVideoResponseDto>) videoRepository.findByCategory(category, page, pageSize);
  }

  public List<GetCategoryNameRequestDto> getCategoryNames() {
    return videoRepository.getCategoryNames();
  }


  public List<GetVideoResponseDto> getViewsTop10Videos() {
    return videoRepository.findByViews();
  }

  @Transactional
  public boolean updateViews() {
    return videoRepository.updateViews();
  }

  public GetDetailResponseDto getVideoDetail(UUID id) {
    return videoRepository.getDetail(id);
  }

  @Transactional
  public boolean plusViews(UUID videoId) {
    return videoRepository.plusViews(videoId);
  }

  public boolean getLike(UUID videoId, Accounts account) {
    return videoRepository.getFavorite(videoId, account);
  }

  @Transactional
  public boolean like(UUID videoId, Accounts account) {
    return videoRepository.plusLike(videoId, account);
  }

  @Transactional
  public boolean cancelLike(UUID videoId, Accounts account) {
    return videoRepository.minusLike(videoId, account);
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
