package com.bipa4.back_bipatv.repository;

import static com.querydsl.core.types.dsl.Expressions.asNumber;
import static org.aspectj.runtime.internal.Conversions.intValue;

import com.amazonaws.services.s3.AmazonS3;
import com.bipa4.back_bipatv.dataType.ErrorCode;
import com.bipa4.back_bipatv.dataType.HandleCode;
import com.bipa4.back_bipatv.dto.video.GetCategoryNameRequestDto;
import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetSearchVideoINChannelDTO;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.dto.video.PutUpdateRequestDto;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.entity.Favorite;
import com.bipa4.back_bipatv.entity.FavoritePK;
import com.bipa4.back_bipatv.entity.QCategoryName;
import com.bipa4.back_bipatv.entity.QCategorys;
import com.bipa4.back_bipatv.entity.QChannels;
import com.bipa4.back_bipatv.entity.QFavorite;
import com.bipa4.back_bipatv.entity.QVideos;
import com.bipa4.back_bipatv.entity.QViewLog;
import com.bipa4.back_bipatv.entity.Videos;
import com.bipa4.back_bipatv.exception.AuthorizationException;
import com.bipa4.back_bipatv.exception.CustomApiException;
import com.bipa4.back_bipatv.exception.NoContentException;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.File;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class VideoRepositoryImpl implements VideoRepositoryCustom {

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;
  private final AmazonS3 amazonS3;

  //----------------------------------------------VIDEO---------------------------------------------

  // 전체보기 (무한 스크롤)
  @Override
  public List<GetVideoResponseDto> getAllVideos(UUID page, int pageSize) {
    List<GetVideoResponseDto> responseDtos;

    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;

    if (page == null) {
      return new ArrayList<>();
    }

    try {
      responseDtos = jpaQueryFactory.select(
              Projections.bean(
                  GetVideoResponseDto.class,
                  qChannels.channelName.as("channelName"),
                  qChannels.profileUrl.as("channelProfileUrl"),
                  qChannels.channelId,
                  qVideos.thumbnail,
                  qVideos.title.as("videoTitle"),
                  qVideos.createAt,
                  qVideos.readCnt.as("readCount"),
                  qVideos.videoId
              )
          )
          .from(qVideos).leftJoin(qVideos.channelId, qChannels)
          .where(qVideos.videoId.loe(page).and(qVideos.privateType.eq(false))
              .and(qVideos.channelId.privateType.eq(false)))
          .orderBy(qVideos.videoId.desc())
          .limit(pageSize).fetch();
    } catch (NullPointerException e) {
      throw new NoContentException();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_ERROR);
    }
    return responseDtos;
  }


  // Default UUID (무한 스크롤 시작점 찾기)
  @Override
  public UUID lastUUID() {
    UUID defaultUUID;

    QVideos qVideos = QVideos.videos;

    try {
      defaultUUID = jpaQueryFactory.select(qVideos.videoId).from(qVideos)
          .where(qVideos.privateType.eq(false)).orderBy(qVideos.videoId.desc()).limit(1).fetchOne();
    } catch (NullPointerException e) {
      throw new NoContentException();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_LAST_UUID_ERRROR);
    }

    return defaultUUID;
  }


  // 다음 페이지의 UUID 찾기
  @Override
  public UUID getNextUUID(UUID uuid) {
    QVideos qVideos = QVideos.videos;
    UUID nextUUID = null;

    try {
      nextUUID = jpaQueryFactory.select(qVideos.videoId).from(qVideos)
          .where(qVideos.videoId.lt(uuid).and(qVideos.privateType.eq(false)))
          .orderBy(qVideos.videoId.desc()).limit(1).fetchOne();
    } catch (NullPointerException e) {
      throw new NoContentException();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_NEXT_UUID_ERRROR);
    }

    return nextUUID;
  }


  // 카테고리별 전체보기
  @Override
  public List<GetVideoResponseDto> findByCategory(UUID category, UUID page, int pageSize) {
    List<GetVideoResponseDto> responseDto;

    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    QCategorys qCategorys = QCategorys.categorys;
    QCategoryName qCategoryName = QCategoryName.categoryName;

    if (page == null) {
      return new ArrayList<>();
    }

    try {
      responseDto = jpaQueryFactory.select(
              Projections.bean(
                  GetVideoResponseDto.class,
                  qChannels.channelName.as("channelName"),
                  qChannels.profileUrl.as("channelProfileUrl"),
                  qChannels.channelId,
                  qVideos.thumbnail,
                  qVideos.title.as("videoTitle"),
                  qVideos.createAt,
                  qVideos.readCnt.as("readCount"),
                  qVideos.videoId
              )
          )
          .from(qCategorys)
          .leftJoin(qCategorys.videoId, qVideos)
          .leftJoin(qVideos.channelId, qChannels)
          .leftJoin(qCategorys.categoryNameId, qCategoryName)
          .where(
              qCategoryName.categoryNameId.eq(category)
                  .and(qVideos.videoId.loe(page))
                  .and(qVideos.privateType.eq(false)
                      .and(qChannels.privateType.eq(false))))
          .orderBy(qVideos.videoId.desc())
          .limit(pageSize).fetch();
    } catch (NullPointerException e) {
      throw new NoContentException(HandleCode.NO_CONTENT);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_ERROR);
    }
    return responseDto;
  }

  // 카테고리 Default UUID (무한 스크롤 시작점 찾기)
  @Override
  public UUID lastCategoryUUID(UUID category) {
    UUID defaultUUID = null;

    QVideos qVideos = QVideos.videos;
    QCategorys qCategorys = QCategorys.categorys;
    QCategoryName qCategoryName = QCategoryName.categoryName;

    try {
      defaultUUID = jpaQueryFactory.select(qVideos.videoId).from(qCategorys)
          .leftJoin(qCategorys.videoId, qVideos)
          .leftJoin(qCategorys.categoryNameId, qCategoryName)
          .where(
              qCategoryName.categoryNameId.eq(category)
                  .and(qVideos.privateType.eq(false))
                  .and(qVideos.channelId.privateType.eq(false))
          )
          .orderBy(qVideos.videoId.desc()).limit(1).fetchOne();
    } catch (NullPointerException e) {
      throw new NoContentException();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_NEXT_UUID_ERRROR);
    }

    return defaultUUID;
  }

  // 카테고리 다음 페이지의 UUID 찾기
  @Override
  public UUID getNextCategoryUUID(UUID uuid, UUID category) {
    UUID nextUUID = null;

    QVideos qVideos = QVideos.videos;
    QCategorys qCategorys = QCategorys.categorys;
    QCategoryName qCategoryName = QCategoryName.categoryName;

    try {
      nextUUID = jpaQueryFactory.select(qVideos.videoId).from(qCategorys)
          .leftJoin(qCategorys.videoId, qVideos)
          .leftJoin(qCategorys.categoryNameId, qCategoryName)
          .where(qCategoryName.categoryNameId.eq(category).and(qVideos.videoId.lt(uuid))
              .and(qVideos.privateType.eq(false)))
          .orderBy(qVideos.videoId.desc()).limit(1).fetchOne();
    } catch (NullPointerException e) {
      throw new NoContentException();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_NEXT_UUID_ERRROR);
    }

    return nextUUID;
  }

  // 카테고리 이름 리스트
  @Override
  public List<GetCategoryNameRequestDto> getCategoryNames() {
    List<GetCategoryNameRequestDto> responseDtos = null;

    QCategoryName qCategoryName = QCategoryName.categoryName;

    try {
      responseDtos = jpaQueryFactory.select(
          Projections.bean(
              GetCategoryNameRequestDto.class,
              qCategoryName.categoryNameId,
              qCategoryName.name.as("categoryName")
          )
      ).from(qCategoryName).fetch();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_CATEGORY_ERROR);
    }

    return responseDtos;
  }


  // 조회수 급상승 TOP 10 + 디비 1시간 전 정보 저장
  @Override
  public List<GetVideoResponseDto> findByViews() {
    List<GetVideoResponseDto> responseDtos = null;

    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    QViewLog qViewLog = QViewLog.viewLog;

    try {
      responseDtos = jpaQueryFactory.select(
              Projections.bean(
                  GetVideoResponseDto.class,
                  qChannels.channelName.as("channelName"),
                  qChannels.profileUrl.as("channelProfileUrl"),
                  qChannels.channelId,
                  qVideos.thumbnail,
                  qVideos.title.as("videoTitle"),
                  qVideos.createAt,
                  qVideos.readCnt.as("readCount"),
                  qVideos.videoId
              )
          )
          .from(qViewLog)
          .leftJoin(qViewLog.videoId, qVideos)
          .leftJoin(qVideos.channelId, qChannels)
          .where(qVideos.privateType.eq(false))
          .orderBy(
              asNumber(qVideos.readCnt.subtract(qViewLog.viewCnt)).doubleValue().desc()
          )
          .limit(10).fetch();
    } catch (NullPointerException e) {
      throw new NoContentException();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_TOP10_ERROR);
    }
    return responseDtos;
  }


  @Override
  public boolean updateViews() {
    int result;

    try {
      result = entityManager.createNativeQuery(
          "update view_log vl join videos v on vl.video_id = v.video_id set view_cnt = read_cnt where v.video_id = vl.video_id"
      ).executeUpdate();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.UPDATE_ERROR);
    }

    return result > 0 ? true : false;
  }


  // 상세보기
  @Override
  public GetDetailResponseDto getDetail(UUID id) {
    GetDetailResponseDto responseDto = null;

    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    QFavorite qFavorite = QFavorite.favorite;
    QCategorys qCategorys = QCategorys.categorys;

    try {
      responseDto = jpaQueryFactory.select(
              Projections.bean(
                  GetDetailResponseDto.class,
                  qChannels.channelName.as("channelName"),
                  qChannels.profileUrl.as("channelProfileUrl"),
                  qChannels.channelId,
                  qVideos.videoUrl,
                  qVideos.title.as("videoTitle"),
                  qVideos.content,
                  qVideos.createAt,
                  qVideos.readCnt.as("readCount"),
                  qVideos.videoId,
                  qVideos.thumbnail
              )).from(qVideos)
          .leftJoin(qVideos.channelId, qChannels)
          .where(qVideos.videoId.eq(id))
          .fetchOne();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_DETAIL_ERROR);
    }

    if (responseDto == null) {
      throw new CustomApiException(ErrorCode.NO_EXIST_VIDEO);
    }

    // 추천 영상 리스트 추출
    try {
      List<GetVideoResponseDto> recommendedVideos = jpaQueryFactory.select(
              Projections.bean(
                  GetVideoResponseDto.class,
                  qChannels.channelName.as("channelName"),
                  qChannels.profileUrl.as("channelProfileUrl"),
                  qChannels.channelId,
                  qVideos.thumbnail,
                  qVideos.title.as("videoTitle"),
                  qVideos.createAt,
                  qVideos.readCnt.as("readCount"),
                  qVideos.videoId
              )
          )
          .from(qVideos).leftJoin(qVideos.channelId, qChannels)
          .where(qVideos.channelId.channelId.eq(responseDto.getChannelId())
              .and(qVideos.videoId.ne(responseDto.getVideoId())))
          .orderBy(qVideos.readCnt.desc())
          .limit(10).fetch();

      responseDto.setRecommendedList(recommendedVideos);
    } catch (NullPointerException e) {
      throw new NoContentException();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_RECOMMEND_ERROR);
    }

    // 영상의 좋아요 총 개수
    try {
      long favoriteCnt = jpaQueryFactory.select(qFavorite.count()).from(qFavorite)
          .where(qFavorite.favoritePK.videos.videoId.eq(responseDto.getVideoId())).fetchFirst();
      responseDto.setLikeCount(favoriteCnt);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_LIKE_ERROR);
    }

    // 영상의 카테고리 저장
    try {
      List<UUID> uuid = jpaQueryFactory.select(qCategorys.categoryNameId.categoryNameId)
          .from(qCategorys)
          .where(qCategorys.videoId.videoId.eq(responseDto.getVideoId())).fetch();
      responseDto.setCategoryId(uuid);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_CATEGORY_ERROR);
    }

    return responseDto;
  }

  // 영상 삭제
  @Override
  public boolean remove(UUID id, Accounts account) {
    QChannels qChannels = QChannels.channels;

    Videos video = entityManager.find(Videos.class, id);

    // 요청한 영상이 존재하지 않는 경우.
    if (video == null) {
      throw new CustomApiException(ErrorCode.NO_EXIST_VIDEO);
    }

    Channels requestChannel = accountToChannel(account);

    // 요청한 유저의 채널과 video의 채널이 다를 경우.
    if (!video.getChannelId().getChannelId().equals(requestChannel.getChannelId())) {
      throw new AuthorizationException();
    }

    // S3 삭제
    try {
      String videoName = video.getVideoUrl().replace("https://du30t7lolw1uk.cloudfront.net/", "");
      String thumbnailName = video.getThumbnail()
          .replace("https://du30t7lolw1uk.cloudfront.net/", "");
      amazonS3.deleteObject(bucketName, (videoName).replace(File.separatorChar, '/'));
      amazonS3.deleteObject(bucketName, (thumbnailName).replace(File.separatorChar, '/'));
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.S3_DELETE_ERROR);
    }

    // 비디오 삭제
    entityManager.remove(video);
    return true;
  }

  // 영상 수정
  @Override
  public boolean update(UUID id, PutUpdateRequestDto videoResponseDto, Accounts account) {
    QChannels qChannels = QChannels.channels;

    Videos video = entityManager.find(Videos.class, id);

    // 요청한 영상이 존재하지 않는 경우.
    if (video == null) {
      throw new CustomApiException(ErrorCode.NO_EXIST_VIDEO);
    }

    Channels requestChannel = accountToChannel(account);

    // 요청한 유저의 채널과 video의 채널이 다를 경우.
    if (!video.getChannelId().getChannelId().equals(requestChannel.getChannelId())) {
      throw new AuthorizationException();
    }

    // S3 삭제
    if (!video.getVideoUrl().equals(videoResponseDto.getVideoUrl())) {
      deleteS3(video.getVideoUrl());
    }
    if (!video.getThumbnail().equals(videoResponseDto.getThumbnailUrl())) {
      deleteS3(video.getThumbnail());
    }

    LocalDateTime now = LocalDateTime.now();

    // 영상 업데이트
    try {
      video.setContent(videoResponseDto.getContent());
      video.setCreateAt(Timestamp.valueOf(now));
      video.setPrivateType(videoResponseDto.isPrivateType());
      video.setThumbnail(videoResponseDto.getThumbnailUrl());
      video.setTitle(videoResponseDto.getTitle());
      video.setVideoUrl(videoResponseDto.getVideoUrl());
      video.setContent(videoResponseDto.getContent());
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.UPDATE_ERROR);
    }
    return true;
  }

  // 영상 본인 글인지 확인하기
  @Override
  public boolean checkOwner(Accounts account, UUID videoId) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;

    if (account == null) {
      return false;
    }

    Videos requestVideo = jpaQueryFactory.selectFrom(qVideos)
        .where(qVideos.videoId.eq(videoId)).fetchOne();

    // 해당 영상이 존재하지 않는다면.
    if (requestVideo == null) {
      throw new CustomApiException(ErrorCode.NO_EXIST_VIDEO);
    }

    Channels channel = accountToChannel(account);

    if (channel.getChannelId().equals(requestVideo.getChannelId().getChannelId())) {
      return true;
    }
    return false;
  }


  // 영상 업로드
  @Override
  public boolean insert(PostUploadRequestDto videoResponseDto, Accounts account, UUID uuid) {
    QChannels qChannels = QChannels.channels;

    Channels channel = accountToChannel(account);

    // video 테이블 create.
    int videoFlag = entityManager.createNativeQuery(
            "INSERT INTO videos (video_url, thumbnail, title, content, private_type, create_at, channel_id, read_cnt, video_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
        .setParameter(1, videoResponseDto.getVideoUrl())
        .setParameter(2, videoResponseDto.getThumbnailUrl())
        .setParameter(3, videoResponseDto.getTitle())
        .setParameter(4, videoResponseDto.getContent())
        .setParameter(5, videoResponseDto.getPrivateType())
        .setParameter(6, new Timestamp(System.currentTimeMillis()))
        .setParameter(7, channel)
        .setParameter(8, 0)
        .setParameter(9, uuid).executeUpdate();

    if (videoFlag == 0) {
      throw new CustomApiException(ErrorCode.UPLOAD_ERROR);
    }

    // view log 테이블 create.
    int viewLogFlag = entityManager.createNativeQuery(
            "INSERT INTO view_log (video_id, view_cnt) VALUES (?, ?);")
        .setParameter(1, uuid)
        .setParameter(2, 0).executeUpdate();

    if (viewLogFlag == 0) {
      throw new CustomApiException(ErrorCode.VIEW_LOG_CREATE_ERROR);
    }

    // category 테이블 create.
    int categoryFlag;
    for (int i = 0; i < videoResponseDto.getCategory().size(); i++) {
      categoryFlag = entityManager.createNativeQuery(
              "INSERT INTO categorys (video_id, category_name_id) VALUES (?, ?)")
          .setParameter(1, uuid)
          .setParameter(2, UUID.fromString(videoResponseDto.getCategory().get(i)))
          .executeUpdate();

      if (categoryFlag == 0) {
        throw new CustomApiException(ErrorCode.CATEGORY_CREATE_ERROR);
      }
    }
    return true;
  }


  // 조회수 상승
  @Override
  public boolean plusViews(UUID videoId) {
    Videos video = entityManager.find(Videos.class, videoId);

    if (video == null) {
      throw new CustomApiException(ErrorCode.NO_EXIST_VIDEO);
    }

    try {
      video.setReadCnt(video.getReadCnt() + 1);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.UPDATE_VIEW_ERROR);
    }

    return true;
  }


  // 좋아요 버튼 눌렀는지 여부
  @Override
  public boolean getFavorite(UUID videoId, Accounts account) {
    long result;

    QFavorite qFavorite = QFavorite.favorite;

    if (account == null) {
      return false;
    }

    try {
      result = jpaQueryFactory.select(qFavorite.count()).from(qFavorite)
          .where(
              qFavorite.favoritePK.videos.videoId.eq(videoId)
                  .and(qFavorite.favoritePK.accounts.eq(account)))
          .fetchFirst();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_LIKE_ERROR);
    }

    return result > 0 ? true : false;
  }

  // 좋아요
  @Override
  public boolean plusLike(UUID videoId, Accounts account) {
    try {
      entityManager.createNativeQuery(
              "INSERT INTO favorite VALUES (?, ?)")
          .setParameter(1, account.getAccountId())
          .setParameter(2, videoId)
          .executeUpdate();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.LIKE_ERROR);
    }
    return true;
  }


  // 좋아요 취소
  @Override
  public boolean minusLike(UUID videoId, Accounts account) {
    Videos video = entityManager.find(Videos.class, videoId);

    // 요청한 영상이 존재하지 않는 경우.
    if (video == null) {
      throw new CustomApiException(ErrorCode.NO_EXIST_VIDEO);
    }

    FavoritePK favoritePK = new FavoritePK();
    favoritePK.setVideos(video);
    favoritePK.setAccounts(account);

    Favorite favorite = entityManager.find(Favorite.class, favoritePK);

    // 좋아요를 누를지 않았덛라면.
    if (favorite == null) {
      throw new CustomApiException(ErrorCode.CANNOT_UNLIKE_ERROR);
    }

    try {
      entityManager.remove(favorite);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.UNLIKE_ERROR);
    }
    return true;
  }

  //Account to Channel
  private Channels accountToChannel(Accounts account) {
    QChannels qChannels = QChannels.channels;

    return jpaQueryFactory.selectFrom(qChannels)
        .leftJoin(qChannels.accounts).fetchJoin()
        .where(qChannels.accounts.eq(account))
        .fetchOne();
  }

  //Delete S3 File
  private void deleteS3(String videoUrl) {
    String videoName = videoUrl.replace("https://du30t7lolw1uk.cloudfront.net/", "");
    try {
      amazonS3.deleteObject(bucketName, (videoName).replace(File.separatorChar, '/'));
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.NO_EXIST_VIDEO);
    }
  }

  //----------------------------------------------CHANNEL----------------------------------------


  @Override
  public List<GetVideoResponseDto> getVideosInChannel(UUID channelId, UUID page, int pageSize) {
    List<GetVideoResponseDto> responseDtos = null;

    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;

    if (page == null) {
      return new ArrayList<>();
    }

    try {
      responseDtos = jpaQueryFactory.select(
              Projections.bean(
                  GetVideoResponseDto.class,
                  qChannels.channelName.as("channelName"),
                  qChannels.profileUrl.as("channelProfileUrl"),
                  qChannels.channelId,
                  qVideos.thumbnail,
                  qVideos.title.as("videoTitle"),
                  qVideos.createAt,
                  qVideos.readCnt.as("readCount"),
                  qVideos.videoId,
                  qVideos.privateType
              )
          )
          .from(qVideos).leftJoin(qVideos.channelId, qChannels)
          .where(qVideos.channelId.channelId.eq(channelId).and(qVideos.videoId.loe(page))
              .and(qVideos.privateType.eq(false)))
          .orderBy(qVideos.videoId.desc())
          .limit(pageSize).fetch();
    } catch (NullPointerException e) {
      throw new NoContentException();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_ERROR);
    }

    return responseDtos;
  }

  @Override
  public UUID lastUUIDInChannel(UUID channelId) {
    UUID lastUUID = null;

    QVideos qVideos = QVideos.videos;

    try {
      lastUUID = jpaQueryFactory.select(qVideos.videoId).from(qVideos)
          .where(qVideos.channelId.channelId.eq(channelId).and(qVideos.privateType.eq(false)))
          .orderBy(qVideos.videoId.desc()).limit(1)
          .fetchOne();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_LAST_UUID_ERRROR);
    }
    return lastUUID;
  }

  @Override
  public UUID lastUUIDInMyChannel(UUID channelId) {
    UUID lastUUID = null;
    QVideos qVideos = QVideos.videos;

    try {
      lastUUID = jpaQueryFactory.select(qVideos.videoId).from(qVideos)
          .where(qVideos.channelId.channelId.eq(channelId))
          .orderBy(qVideos.videoId.desc()).limit(1)
          .fetchOne();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_LAST_UUID_ERRROR);
    }

    return lastUUID;
  }

  @Override
  public Integer lastUUIDSearchVideoInMyChannel(UUID channelId, String searchQuery) {
    List<Integer> uuid = new ArrayList<>();

    try {
      uuid = entityManager.createNativeQuery(
              "SELECT ranking\n"
                  + "FROM (\n"
                  + "    SELECT ROW_NUMBER() OVER () AS ranking\n"
                  + "    FROM videos\n"
                  + "    WHERE videos.channel_id = ?\n"
                  + "    AND MATCH (videos.title, videos.content) AGAINST (? IN NATURAL LANGUAGE MODE)\n"
                  + "    ORDER BY ranking ASC \n"
                  + "    LIMIT 1\n"
                  + ") AS ranked_results;\n"
          ).setParameter(1, channelId)
          .setParameter(2, searchQuery)
          .getResultList();

      if (!uuid.isEmpty()) {
        return intValue(uuid.get(0));
      }
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_LAST_UUID_ERRROR);
    }
    return null;
  }

  @Override
  public Integer lastUUIDSearchVideoInChannel(UUID channelId, String searchQuery) {
    List<Integer> uuid = new ArrayList<>();
    try {
      uuid = entityManager.createNativeQuery("SELECT ranking\n"
              + "FROM (\n"
              + "    SELECT ROW_NUMBER() OVER () AS ranking\n"
              + "    FROM videos\n"
              + "    WHERE videos.channel_id = ?\n"
              + "    AND MATCH (videos.title, videos.content) AGAINST (? IN NATURAL LANGUAGE MODE)\n"
              + "    AND videos.private_type = false\n"
              + "    ORDER BY ranking ASC \n"
              + "    LIMIT 1\n"
              + ") AS ranked_results;\n "
          ).setParameter(1, channelId)
          .setParameter(2, searchQuery)
          .getResultList();

      if (!uuid.isEmpty()) {
        return intValue(uuid.get(0));
      }
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_LAST_UUID_ERRROR);
    }
    return null;
  }

  @Override
  public List<GetVideoResponseDto> getVideosInMyChannel(UUID channelId, UUID uuid, int pageSize) {
    List<GetVideoResponseDto> responseDtos;

    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;

    if (uuid == null) {
      return new ArrayList<>();
    }

    try {
      responseDtos = jpaQueryFactory.select(
              Projections.bean(
                  GetVideoResponseDto.class,
                  qChannels.channelName.as("channelName"),
                  qChannels.profileUrl.as("channelProfileUrl"),
                  qChannels.channelId,
                  qVideos.thumbnail,
                  qVideos.title.as("videoTitle"),
                  qVideos.createAt,
                  qVideos.readCnt.as("readCount"),
                  qVideos.videoId,
                  qVideos.privateType
              )
          )
          .from(qVideos).leftJoin(qVideos.channelId, qChannels)
          .where(qVideos.channelId.channelId.eq(channelId).and(qVideos.videoId.loe(uuid)))
          .orderBy(qVideos.videoId.desc())
          .limit(pageSize).fetch();
    } catch (NullPointerException e) {
      throw new NoContentException();
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.READ_ERROR);
    }

    return responseDtos;
  }

  @Override
  public List<GetSearchVideoINChannelDTO> getSearchVideoInMyChannel(UUID channelId,
      Integer nextRank,
      int pageSize, String searchQuery) {
    if (nextRank == null) {
      nextRank = 1;
    }
    List<Object[]> resultList = entityManager.createNativeQuery(
            "SELECT ranking, BIN_TO_UUID(videoId) as videoId, videoTitle, BIN_TO_UUID(channelId) as channelId, channelName, channelProfileUrl, thumbnail, createAt, readCount\n"
                + "FROM (\n"
                + "SELECT ROW_NUMBER() OVER () AS ranking, videos.video_id as videoId, videos.title as videoTitle, channels.channel_id as channelId, videos.read_cnt as readCount, videos.create_at as createAt, videos.thumbnail as thumbnail, channels.profile_url as channelProfileUrl, channels.name as channelName\n"
                + "FROM videos \n"
                + "join channels \n"
                + "on videos.channel_id = channels.channel_id\n"
                + "WHERE videos.channel_id = ?\n"
                + "and MATCH (videos.title, videos.content) AGAINST (? IN NATURAL LANGUAGE MODE)\n"
                + ")as ranked\n"
                + "where ranking >= ?\n"
                + "order by ranking asc\n"
                + "limit ?;"
        ).setParameter(1, channelId)
        .setParameter(2, searchQuery)
        .setParameter(3, nextRank)
        .setParameter(4, pageSize).getResultList();

    List<GetSearchVideoINChannelDTO> searchList = new ArrayList<>();
    for (Object[] row : resultList) {
      GetSearchVideoINChannelDTO dto = new GetSearchVideoINChannelDTO();//
      dto.setRanking(((BigInteger) row[0]).intValue());
      UUID videoId = UUID.fromString((String) row[1]);
      dto.setVideoId(videoId);
      dto.setVideoTitle((String) row[2]);

      UUID channelUUID = UUID.fromString((String) row[3]);
      dto.setChannelId(channelUUID);

      dto.setChannelName((String) row[4]);
      dto.setChannelProfileUrl((String) row[5]);
      dto.setThumbnail((String) row[6]);
      dto.setCreateAt((Timestamp) row[7]);
      dto.setReadCount((int) row[8]);
      // 나머지 필드 설정
      searchList.add(dto);

    }
    return searchList;
  }

  @Override
  public List<GetSearchVideoINChannelDTO> getSearchVideoInChannel(UUID channelId,
      Integer nextRank,
      int pageSize, String searchQuery) {

    if (nextRank == null) {
      nextRank = 1;
    }
    List<Object[]> resultList = entityManager.createNativeQuery(
            "SELECT ranking, BIN_TO_UUID(videoId) as videoId, videoTitle, BIN_TO_UUID(channelId) as channelId, channelName, channelProfileUrl, thumbnail, createAt, readCount\n"
                + "FROM (\n"
                + "SELECT ROW_NUMBER() OVER () AS ranking, videos.video_id as videoId, videos.title as videoTitle, channels.channel_id as channelId, videos.read_cnt as readCount, videos.create_at as createAt, videos.thumbnail as thumbnail, channels.profile_url as channelProfileUrl, channels.name as channelName\n"
                + "FROM videos \n"
                + "join channels \n"
                + "on videos.channel_id = channels.channel_id\n"
                + "WHERE videos.channel_id = ?\n"
                + "and MATCH (videos.title, videos.content) AGAINST (? IN NATURAL LANGUAGE MODE)\n"
                + "AND videos.private_type = false\n"
                + ")as ranked\n"
                + "where ranking >= ?\n"
                + "order by ranking asc\n"
                + "limit ?;"
        ).setParameter(1, channelId)
        .setParameter(2, searchQuery)
        .setParameter(3, nextRank)
        .setParameter(4, pageSize).getResultList();

    List<GetSearchVideoINChannelDTO> searchList = new ArrayList<>();
    for (Object[] row : resultList) {
      GetSearchVideoINChannelDTO dto = new GetSearchVideoINChannelDTO();//
      dto.setRanking(((BigInteger) row[0]).intValue());

      UUID videoId = UUID.fromString((String) row[1]);
      dto.setVideoId(videoId);
      dto.setVideoTitle((String) row[2]);

      UUID channelUUID = UUID.fromString((String) row[3]);
      dto.setChannelId(channelUUID);

      dto.setChannelName((String) row[4]);
      dto.setChannelProfileUrl((String) row[5]);
      dto.setThumbnail((String) row[6]);
      dto.setCreateAt((Timestamp) row[7]);
      dto.setReadCount((int) row[8]);
      // 나머지 필드 설정
      searchList.add(dto);
    }
    return searchList;
  }
}
