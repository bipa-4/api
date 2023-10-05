package com.bipa4.back_bipatv.repository;

import static com.querydsl.core.types.dsl.Expressions.asNumber;

import com.bipa4.back_bipatv.dto.video.GetCategoryNameRequestDto;
import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
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
import com.bipa4.back_bipatv.security.SecurityService;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class VideoRepositoryImpl implements VideoRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final SecurityService securityService;
  private final EntityManager entityManager;

  // 전체보기 (무한 스크롤)
  @Override
  public List<GetVideoResponseDto> getAllVideos(int page, int pageSize) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    Long total = jpaQueryFactory.select(qVideos.count()).from(qVideos).fetchFirst();

    return jpaQueryFactory.select(
            Projections.bean(
                GetVideoResponseDto.class,
                qChannels.channelName.as("channelName"),
                qChannels.profileUrl.as("channelProfileUrl"),
                qVideos.thumbnail,
                qVideos.title.as("videoTitle"),
                qVideos.createAt,
                qVideos.readCnt,
                qVideos.videoId
            )
        )
        .from(qVideos).leftJoin(qVideos.channelId, qChannels)
        .where(qVideos.videoId.loe(total - (page - 2))).orderBy(qVideos.videoId.desc())
        .limit(pageSize).fetch();
  }


  // 카테고리별 전체보기
  @Override
  public List<GetVideoResponseDto> findByCategory(String category, int page, int pageSize) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    QCategorys qCategorys = QCategorys.categorys;
    QCategoryName qCategoryName = QCategoryName.categoryName;

    Long total = jpaQueryFactory.select(qVideos.count()).from(qVideos).fetchFirst();

    return jpaQueryFactory.select(
            Projections.bean(
                GetVideoResponseDto.class,
                qChannels.channelName.as("channelName"),
                qChannels.profileUrl.as("channelProfileUrl"),
                qVideos.thumbnail,
                qVideos.title.as("videoTitle"),
                qVideos.createAt,
                qVideos.readCnt,
                qVideos.videoId
            )
        )
        .from(qCategorys)
        .leftJoin(qCategorys.videoId, qVideos)
        .leftJoin(qVideos.channelId, qChannels)
        .leftJoin(qCategorys.categoryNameId, qCategoryName)
        .where(qCategoryName.name.eq(category).and(qVideos.videoId.loe(total - (page - 2))))
        .orderBy(qVideos.videoId.desc())
        .limit(pageSize).fetch();
  }


  // 카테고리 이름 리스트
  @Override
  public List<GetCategoryNameRequestDto> getCategoryNames() {
    QCategoryName qCategoryName = QCategoryName.categoryName;

    return jpaQueryFactory.select(
        Projections.bean(
            GetCategoryNameRequestDto.class,
            qCategoryName.categoryNameId.as("categoryNameId"),
            qCategoryName.name.as("categoryName"),
            qCategoryName.path.as("categoryPath")
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
                qVideos.readCnt,
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
    QVideos qVideos = QVideos.videos;
    QViewLog qViewLog = QViewLog.viewLog;

    return entityManager.createNativeQuery(
        "update view_log vl join videos v on vl.video_id = v.video_id set view_cnt = read_cnt where v.video_id = vl.video_id"
    ).executeUpdate();
  }


  // 상세보기
  @Override
  public GetDetailResponseDto getDetail(Long id) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    QFavorite qFavorite = QFavorite.favorite;

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
                qVideos.readCnt,
                qVideos.videoId,
                qVideos.thumbnail.as("thumbnailUrl")
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
                qVideos.readCnt,
                qVideos.videoId
            )
        )
        .from(qVideos).leftJoin(qVideos.channelId, qChannels)
        .where(qVideos.channelId.eq(channel).and(qVideos.videoId.ne(dto.getVideoId())))
        .orderBy(qVideos.readCnt.desc())
        .limit(10).fetch();

    dto.setRecommendedList(recommendedVideos);

    // 좋아요 개수
    Videos videos = new Videos();
    videos.setVideoId(dto.getVideoId());

    long favoriteCnt = jpaQueryFactory.select(qFavorite.count()).from(qFavorite)
        .where(qFavorite.favoritePK.videos.eq(videos)).fetchFirst();

    dto.setFavoriteCnt(favoriteCnt);

    return dto;
  }

  // 영상 삭제
  @Override
  public Long remove(Long id) {
    QVideos qVideos = QVideos.videos;

    Videos video = entityManager.find(Videos.class, id);

    if (video != null) {
      entityManager.remove(video);
      return 1L;
    } else {
      return 0L;
    }
  }

  // 영상 수정
  @Override
  public int update(Long id, PutUpdateRequestDto videoResponseDto) {
    Videos video = entityManager.find(Videos.class, id);

    if (video != null) {
      video.setContent(videoResponseDto.getContent());
      video.setCreateAt(new Timestamp(System.currentTimeMillis()));
      video.setPrivateType(videoResponseDto.getPrivate_type());
      video.setThumbnail(videoResponseDto.getThumbnailUrl());
      video.setTitle(videoResponseDto.getTitle());
      video.setVideoUrl(videoResponseDto.getVideoUrl());
      video.setContent(videoResponseDto.getContent());
    } else {
      return 0;
    }
    return 1;
  }

  // 영상 본인 글인지 확인하기
  @Override
  public Long checkOwner(String token, Long videoId) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;

    Accounts account = securityService.getSubjectAccount(token);
    Channels channel = jpaQueryFactory.selectFrom(qChannels)
        .where(qChannels.accounts.eq(account)).fetchOne();

    System.out.println(account);
    System.out.println(channel);

    Long result = jpaQueryFactory.select(qVideos.count()).from(qVideos)
        .where(qVideos.channelId.eq(channel)).fetchFirst();

    System.out.println(result);

    return result;
  }


  // 영상 업로드
  @Override
  public int insert(PostUploadRequestDto videoResponseDto, String token) {
    QChannels qChannels = QChannels.channels;

    // chcannelId 가져오기.
    Accounts account = securityService.getSubjectAccount(token);
    Long channelId = jpaQueryFactory.select(qChannels.channelId).from(qChannels)
        .where(qChannels.accounts.eq(account)).fetchOne();

    Channels channel = new Channels();
    channel.setChannelId(channelId);

    // Videos 테이블 create.
    int result = entityManager.createNativeQuery(
            "INSERT INTO videos (video_url, thumbnail, title, content, private_type, create_at, channel_id, read_cnt) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
        .setParameter(1, videoResponseDto.getVideoUrl())
        .setParameter(2, videoResponseDto.getThumbnailUrl())
        .setParameter(3, videoResponseDto.getTitle())
        .setParameter(4, videoResponseDto.getContent())
        .setParameter(5, videoResponseDto.getPrivateType())
        .setParameter(6, new Timestamp(System.currentTimeMillis()))
        .setParameter(7, channel)
        .setParameter(8, 0).executeUpdate();

    if (result != 0) {
      // ViewLog 테이블 create.
      entityManager.createNativeQuery("INSERT INTO view_log VALUES ((SELECT LAST_INSERT_ID()), ?);")
          .setParameter(1, 0).executeUpdate();

      // category 테이블 create.
      entityManager.createNativeQuery(
              "INSERT INTO categorys (video_id, category_name_id) VALUES ((SELECT LAST_INSERT_ID()), ?)")
          .setParameter(1, videoResponseDto.getCategory()).executeUpdate();
    }

    return result;
  }


  // 조회수 상승
  @Override
  public int plusViews(Long videoId) {
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
  public Long getFavorite(Long videoId, String token) {
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
  public int plusLike(Long videoId, String token) {
    Accounts account = securityService.getSubjectAccount(token);

    int result = entityManager.createNativeQuery(
            "INSERT INTO favorite VALUES (?, ?)")
        .setParameter(1, account.getAccountId())
        .setParameter(2, videoId)
        .executeUpdate();

    return result;
  }


  // 좋아요 취소
  @Override
  public int minusLike(Long videoId, String token) {
    Videos video = new Videos();
    video.setVideoId(videoId);
    Accounts account = securityService.getSubjectAccount(token);

    FavoritePK favoritePK = new FavoritePK();
    favoritePK.setVideos(video);
    favoritePK.setAccounts(account);

    Favorite favorite = entityManager.find(Favorite.class, favoritePK);

    if (favorite != null) {
      entityManager.remove(favorite);
      return 1;
    } else {
      return 0;
    }
  }

  @Override
  public List<GetVideoResponseDto> getVideosInChannel(Long channelId) {
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
                qVideos.readCnt,
                qVideos.videoId
            )
        )
        .from(qVideos).leftJoin(qVideos.channelId, qChannels)
        .where(qVideos.channelId.eq(channel))
        .fetch();
  }
}
