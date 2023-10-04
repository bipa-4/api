package com.bipa4.back_bipatv.service;

//import com.amazonaws.services.s3.AmazonS3Client;

import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetSearchResponseDto;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.dto.video.PutUpdateRequestDto;
import com.bipa4.back_bipatv.repository.VideoChannelRepository;
import com.bipa4.back_bipatv.repository.VideoRepository;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VideoService {

  private final VideoRepository videoRepository;
  private final VideoChannelRepository videoChannelRepository;

  // Upload to db.
  @Transactional
  public int upload(PostUploadRequestDto requestDto) {
    return videoRepository.insert(requestDto);
  }

  @Transactional
  public List<GetSearchResponseDto> search(String searchQuery) {
    return videoChannelRepository.findBySearchQuery(searchQuery);
  }

  public Long check(String token, Long videoId) {
    return videoRepository.checkOwner(token, videoId);
  }

  @Transactional
  public Long removeVideo(Long videoId) {
    return videoRepository.remove(videoId);
  }

  @Transactional
  public int uploadVideo(PostUploadRequestDto requestdto) {
    return videoRepository.insert(requestdto);
  }

  @Transactional
  public int updateVideo(Long id, PutUpdateRequestDto requestDto) {
    return videoRepository.update(id, requestDto);
  }

  public List<GetVideoResponseDto> getAllViideos(int page, int pageSize) {
    System.out.println(page + " " + pageSize);
    return videoRepository.getAllVideos(page, pageSize);
  }

  public List<GetVideoResponseDto> getCategoryVideos(String category, int page, int pageSize) {
    return videoRepository.findByCategory(category, page, pageSize);
  }

  public List<GetVideoResponseDto> getCategoryNames() {
    return videoRepository.getCategoryNames();
  }

  @Transactional
  public List<GetVideoResponseDto> getViewsTop10Videos() {
    List<GetVideoResponseDto> videos = videoRepository.findByViews();
    int result = videoRepository.updateViews();
    return videos;
  }

  public GetDetailResponseDto getVideoDetail(Long id) {
    return videoRepository.getDetail(id);
  }

  @Transactional
  public int plusViews(Long videoId) {
    return videoRepository.plusViews(videoId);
  }

  public boolean getFavorite(Long videoId, String token) {
    if (videoRepository.getFavorite(videoId, token) == 1) {
      return true;
    }
    return false;
  }

  @Transactional
  public int like(Long videoId, String token) {
    return videoRepository.plusLike(videoId, token);
  }

  @Transactional
  public int cancelLike(Long videoId, String token) {
    return videoRepository.minusLike(videoId, token);
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
