package com.bipa4.back_bipatv.repository;

import static com.querydsl.core.types.dsl.Expressions.asNumber;

import com.amazonaws.services.s3.AmazonS3;
import com.bipa4.back_bipatv.dataType.ErrorCode;
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
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.File;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

  // 전체보기 (무한 스크롤)
  @Override
  public List<GetVideoResponseDto> getAllVideos(UUID page, int pageSize) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;

    List<GetVideoResponseDto> dto = jpaQueryFactory.select(
            Projections.bean(
                GetVideoResponseDto.class,
                qChannels.channelName.as("channelName"),
                qChannels.profileUrl.as("channelProfileUrl"),
                qVideos.thumbnail,
                qVideos.title.as("videoTitle"),
                qVideos.createAt,
                qVideos.readCnt.as("readCount"),
                qVideos.videoId
            )
        )
        .from(qVideos).leftJoin(qVideos.channelId, qChannels)
        .where(qVideos.videoId.loe(page).and(qVideos.privateType.eq(false)))
        .orderBy(qVideos.videoId.desc())
        .limit(pageSize).fetch();

    return dto;
  }


  // Default UUID (무한 스크롤 시작점 찾기)
  @Override
  public UUID lastUUID() {
    QVideos qVideos = QVideos.videos;

    return jpaQueryFactory.select(qVideos.videoId).from(qVideos)
        .where(qVideos.privateType.eq(false))
        .orderBy(qVideos.videoId.desc()).limit(1)
        .fetchOne();
  }

  @Override
  public UUID lastUUIDInChannel(UUID channelId) {
    QVideos qVideos = QVideos.videos;

    return jpaQueryFactory.select(qVideos.videoId).from(qVideos)
        .where(qVideos.channelId.channelId.eq(channelId).and(qVideos.privateType.eq(false)))
        .orderBy(qVideos.videoId.desc()).limit(1)
        .fetchOne();
  }

  @Override
  public UUID lastUUIDInMyChannel(UUID channelId) {
    QVideos qVideos = QVideos.videos;

    return jpaQueryFactory.select(qVideos.videoId).from(qVideos)
        .where(qVideos.channelId.channelId.eq(channelId))
        .orderBy(qVideos.videoId.desc()).limit(1)
        .fetchOne();
  }

  @Override
  public List<Integer> lastUUIDSearchVideoInMyChannel(UUID channelId, String searchQuery) {
    List<Integer> uuid = entityManager.createNativeQuery(
            "SELECT ranking\n"
                + "FROM (\n"
                + "    SELECT ROW_NUMBER() OVER () AS ranking\n"
                + "    FROM videos\n"
                + "    WHERE videos.channel_id = ?\n"
                + "    AND MATCH (videos.title, videos.content) AGAINST (? IN NATURAL LANGUAGE MODE)\n"
                + "    ORDER BY ranking DESC\n"
                + "    LIMIT 1\n"
                + ") AS ranked_results;\n"
        ).setParameter(1, channelId)
        .setParameter(2, searchQuery)
        .getResultList();
    return uuid;
  }

  @Override
  public List<Integer> lastUUIDSearchVideoInChannel(UUID channelId, String searchQuery) {
    System.out.println("lastUUIDSearchVideoInChannel 메소드 channelId값:" + channelId);
    System.out.println("lastUUIDSearchVideoInChannel 메소드 searchQuery값:" + searchQuery);
    List<Integer> uuid = entityManager.createNativeQuery("SELECT ranking\n"
            + "FROM (\n"
            + "    SELECT ROW_NUMBER() OVER () AS ranking\n"
            + "    FROM videos\n"
            + "    WHERE videos.channel_id = ?\n"
            + "    AND MATCH (videos.title, videos.content) AGAINST (? IN NATURAL LANGUAGE MODE)\n"
            + "    AND videos.private_type = false\n"
            + "    ORDER BY ranking DESC\n"
            + "    LIMIT 1\n"
            + ") AS ranked_results;\n "
        ).setParameter(1, channelId)
        .setParameter(2, searchQuery)
        .getResultList();
    System.out.println(uuid);

    return uuid;
  }

  // 다음 페이지의 UUID 찾기
  @Override
  public String getNextUUID(UUID uuid) {
    QVideos qVideos = QVideos.videos;

    UUID nextUUID = jpaQueryFactory.select(qVideos.videoId).from(qVideos)
        .where(qVideos.videoId.lt(uuid).and(qVideos.privateType.eq(false)))
        .orderBy(qVideos.videoId.desc())
        .limit(1).fetchOne();

    if (nextUUID == null) {
      return "";
    }

    return nextUUID.toString();
  }


  // 카테고리별 전체보기
  @Override
  public List<GetVideoResponseDto> findByCategory(UUID category, UUID page, int pageSize) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    QCategorys qCategorys = QCategorys.categorys;
    QCategoryName qCategoryName = QCategoryName.categoryName;

    List<GetVideoResponseDto> responseDto =  jpaQueryFactory.select(
            Projections.bean(
                GetVideoResponseDto.class,
                qChannels.channelName.as("channelName"),
                qChannels.profileUrl.as("channelProfileUrl"),
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
    return responseDto;
  }

  @Override
  public UUID lastCategoryUUID(UUID category) {
    QVideos qVideos = QVideos.videos;
    QCategorys qCategorys = QCategorys.categorys;
    QCategoryName qCategoryName = QCategoryName.categoryName;

    return jpaQueryFactory.select(qVideos.videoId).from(qCategorys)
        .leftJoin(qCategorys.videoId, qVideos)
        .leftJoin(qCategorys.categoryNameId, qCategoryName)
        .where(
            qCategoryName.categoryNameId.eq(category)
                .and(qVideos.privateType.eq(false))
        )
        .orderBy(qVideos.videoId.desc()).limit(1)
        .fetchOne();
  }

  // 카테고리 이름 리스트
  @Override
  public List<GetCategoryNameRequestDto> getCategoryNames() {
    QCategoryName qCategoryName = QCategoryName.categoryName;

    return jpaQueryFactory.select(
        Projections.bean(
            GetCategoryNameRequestDto.class,
            qCategoryName.categoryNameId,
            qCategoryName.name.as("categoryName")
        )
    ).from(qCategoryName).fetch();
  }


  // 조회수 급상승 TOP 10 + 디비 1시간 전 정보 저장
  @Override
  public List<GetVideoResponseDto> findByViews() {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    QViewLog qViewLog = QViewLog.viewLog;

    return jpaQueryFactory.select(
            Projections.bean(
                GetVideoResponseDto.class,
                qChannels.channelName.as("channelName"),
                qChannels.profileUrl.as("channelProfileUrl"),
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
  }


  @Override
  public int updateViews() {
    return entityManager.createNativeQuery(
        "update view_log vl join videos v on vl.video_id = v.video_id set view_cnt = read_cnt where v.video_id = vl.video_id"
    ).executeUpdate();
  }


  // 상세보기
  @Override
  public GetDetailResponseDto getDetail(UUID id) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    QFavorite qFavorite = QFavorite.favorite;
    QCategorys qCategorys = QCategorys.categorys;

    GetDetailResponseDto dto = jpaQueryFactory.select(
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

    Channels channel = new Channels();
    channel.setChannelId(dto.getChannelId());

    // 추천 영상 리스트 추출
    List<GetVideoResponseDto> recommendedVideos = jpaQueryFactory.select(
            Projections.bean(
                GetVideoResponseDto.class,
                qChannels.channelName.as("channelName"),
                qChannels.profileUrl.as("channelProfileUrl"),
                qVideos.thumbnail,
                qVideos.title.as("videoTitle"),
                qVideos.createAt,
                qVideos.readCnt.as("readCount"),
                qVideos.videoId
            )
        )
        .from(qVideos).leftJoin(qVideos.channelId, qChannels)
        .where(qVideos.channelId.eq(channel).and(qVideos.videoId.ne(dto.getVideoId())))
        .orderBy(qVideos.readCnt.desc())
        .limit(10).fetch();

    dto.setRecommendedList(recommendedVideos);

    // 영상의 좋아요 총 개수
    Videos videos = new Videos();
    videos.setVideoId(dto.getVideoId());

    long favoriteCnt = jpaQueryFactory.select(qFavorite.count()).from(qFavorite)
        .where(qFavorite.favoritePK.videos.eq(videos)).fetchFirst();

    dto.setLikeCount(favoriteCnt);

    // 영상의 카테고리 저장
    List<UUID> uuid = jpaQueryFactory.select(qCategorys.categoryNameId.categoryNameId)
        .from(qCategorys)
        .where(qCategorys.videoId.videoId.eq(dto.getVideoId())).fetch();
    dto.setCategoryId(uuid);

    return dto;
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
    if (video.getVideoUrl() != videoResponseDto.getVideoUrl()) {
      deleteS3(video.getVideoUrl());
    }
    if (video.getThumbnail() != videoResponseDto.getThumbnailUrl()) {
      deleteS3(video.getThumbnail());
    }

    LocalDateTime now = LocalDateTime.now();

    // 영상 업데이트
    try {
      video.setContent(videoResponseDto.getContent());
      video.setCreateAt(Timestamp.valueOf(now));
      video.setPrivateType(videoResponseDto.isPrivate_type());
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
  public int plusViews(UUID videoId) {
    Videos video = entityManager.find(Videos.class, videoId);

    if (video != null) {
      video.setReadCnt(video.getReadCnt() + 1);
    } else {
      return 0;
    }
    return 1;
  }


  // 좋아요 버튼 눌렀는지 여부
  @Override
  public Long getFavorite(UUID videoId, Accounts account) {
    QFavorite qFavorite = QFavorite.favorite;

    Videos videos = new Videos();
    videos.setVideoId(videoId);
    Accounts account = securityService.getSubjectAccount(token);

    return jpaQueryFactory.select(qFavorite.count()).from(qFavorite)
        .where(
            qFavorite.favoritePK.videos.eq(videos).and(qFavorite.favoritePK.accounts.eq(account)))
        .fetchFirst();
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


  @Override
  public List<GetVideoResponseDto> getVideosInChannel(UUID channelId, UUID page, int pageSize) {
    Channels channel = new Channels();
    channel.setChannelId(channelId);
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;

    // 추천 영상 리스트 추출
    return jpaQueryFactory.select(
            Projections.bean(
                GetVideoResponseDto.class,
                qChannels.channelName.as("channelName"),
                qChannels.profileUrl.as("channelProfileUrl"),
                qVideos.thumbnail,
                qVideos.title.as("videoTitle"),
                qVideos.createAt,
                qVideos.readCnt.as("readCount"),
                qVideos.videoId
            )
        )
        .from(qVideos).leftJoin(qVideos.channelId, qChannels)
        .where(qVideos.channelId.eq(channel).and(qVideos.videoId.loe(page))
            .and(qVideos.privateType.eq(false)))
        .orderBy(qVideos.videoId.desc())
        .limit(pageSize).fetch();
  }

  @Override
  public List<GetVideoResponseDto> getVideosInMyChannel(UUID channelId, UUID uuid, int pageSize) {
    Channels channel = new Channels();
    channel.setChannelId(channelId);
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;

    // 추천 영상 리스트 추출
    return jpaQueryFactory.select(
            Projections.bean(
                GetVideoResponseDto.class,
                qChannels.channelName.as("channelName"),
                qChannels.profileUrl.as("channelProfileUrl"),
                qVideos.thumbnail,
                qVideos.title.as("videoTitle"),
                qVideos.createAt,
                qVideos.readCnt.as("readCount"),
                qVideos.videoId
            )
        )
        .from(qVideos).leftJoin(qVideos.channelId, qChannels)
        .where(qVideos.channelId.eq(channel).and(qVideos.videoId.loe(uuid)))
        .orderBy(qVideos.videoId.desc())
        .limit(pageSize).fetch();
  }

  @Override
  public List<GetSearchVideoINChannelDTO> getSearchVideoInMyChannel(UUID channelId,
      Integer currentPage,
      int pageSize, String searchQuery) {
    List<GetSearchVideoINChannelDTO> searchList = entityManager.createNativeQuery(
            "SELECT c.name AS channelName, c.profile_url AS channelProfileUrl, v.thumbnail AS thumbnail, v.title AS videoTitle, v.create_at AS createAt, v.read_cnt AS readCnt, BIN_TO_UUID(v.video_id) AS videoId, ROW_NUMBER() OVER () AS ranking\n"
                + "FROM channels c\n"
                + "LEFT JOIN videos v ON c.channel_id = v.channel_id \n"
                + "WHERE BIN_TO_UUID(v.channel_id) = ? \n"
                + "AND MATCH (v.title, v.content) AGAINST (? IN NATURAL LANGUAGE MODE)\n"
                + "AND v.video_id IN (\n"
                + "  SELECT video_id\n"
                + "  FROM (\n"
                + "    SELECT video_id, ROW_NUMBER() OVER () AS ranking\n"
                + "    FROM videos\n"
                + "    WHERE MATCH (title, content) AGAINST (? IN NATURAL LANGUAGE MODE)\n"
                + "  ) AS ranked\n"
                + "  WHERE ranking <= ?\n"
                + ")\n"
                + "LIMIT ?;"
        ).setParameter(1, channelId)
        .setParameter(2, searchQuery)
        .setParameter(3, searchQuery)
        .setParameter(4, currentPage)
        .setParameter(5, pageSize).getResultList();
    System.out.println("searchList값:" + searchList);
    return searchList;
  }

  @Override
  public List<GetSearchVideoINChannelDTO> getSearchVideoInChannel(UUID channelId,
      Integer currentPage,
      int pageSize, String searchQuery) {
    System.out.println("channelId: " + channelId);
    System.out.println("currentPage: " + currentPage);
    System.out.println("pageSize: " + pageSize);
    System.out.println("searchQuery: " + searchQuery);

    List<Object[]> resultList = entityManager.createNativeQuery(
            "SELECT c.name AS channelName, c.profile_url AS channelProfileUrl, v.thumbnail AS thumbnail, v.title AS videoTitle, v.create_at AS createAt, v.read_cnt AS readCnt, BIN_TO_UUID(v.video_id) AS videoId, ROW_NUMBER() OVER () AS ranking\n"
                + "FROM channels c\n"
                + "LEFT JOIN videos v ON c.channel_id = v.channel_id \n"
                + "WHERE v.channel_id = ? \n"
                + "AND MATCH (v.title, v.content) AGAINST (? IN NATURAL LANGUAGE MODE)\n"
                + "AND v.private_type = false "
                + "AND v.video_id IN (\n"
                + "  SELECT video_id\n"
                + "  FROM (\n"
                + "    SELECT video_id, ROW_NUMBER() OVER () AS ranking\n"
                + "    FROM videos\n"
                + "    WHERE MATCH (title, content) AGAINST (? IN NATURAL LANGUAGE MODE)\n"
                + "  ) AS ranked\n"
                + "  WHERE ranking <= ?\n"
                + ")\n"
                + "LIMIT ?;"
        ).setParameter(1, channelId)
        .setParameter(2, searchQuery)
        .setParameter(3, searchQuery)
        .setParameter(4, currentPage)
        .setParameter(5, pageSize).getResultList();

    List<GetSearchVideoINChannelDTO> searchList = new ArrayList<>();
    for (Object[] row : resultList) {
      GetSearchVideoINChannelDTO dto = new GetSearchVideoINChannelDTO();
      dto.setChannelName((String) row[0]);
      dto.setChannelProfileUrl((String) row[1]);
      dto.setThumbnail((String) row[2]);
      dto.setVideoTitle((String) row[3]);
      dto.setCreateAt((Timestamp) row[4]);
      dto.setReadCnt((int) row[5]);
      dto.setVideoId(UUID.fromString((String) row[6]));
      dto.setRanking(((BigInteger) row[7]).intValue());
      // 나머지 필드 설정
      searchList.add(dto);
    }
    System.out.println("searchList값:" + searchList);
    return searchList;
  }

  @Override
  public List<Integer> lastUUIDSearchVideoInMyChannel(UUID channelId, String searchQuery) {
    List<Integer> uuid = entityManager.createNativeQuery(
            "select ROW_NUMBER() OVER () AS ranking  \n"
                + "from videos \n"
                + "where videos.channel_id = ? \n"
                + " and MATCH (videos.title, videos.content) AGAINST ( ? IN NATURAL LANGUAGE MODE) \n"
                + "order By videos.video_id desc \n"
                + "limit 1 "
        ).setParameter(1, channelId)
        .setParameter(2, searchQuery)
        .getResultList();
    return uuid;
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
}
