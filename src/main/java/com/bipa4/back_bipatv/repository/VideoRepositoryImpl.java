package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetSearchResponseDto;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.entity.QAccounts;
import com.bipa4.back_bipatv.entity.QChannels;
import com.bipa4.back_bipatv.entity.QVideos;
import com.bipa4.back_bipatv.entity.Videos;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class VideoRepositoryImpl implements VideoRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final EntityManager entityManager;

  // 전체보기 (무한 스크롤)
  @Override
  public List<GetVideoResponseDto> getAllVideos(int page, int pageSize) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    Long total = jpaQueryFactory.select(qVideos.count()).from(qVideos).fetchFirst();

    return jpaQueryFactory.select(
            Projections.bean(GetVideoResponseDto.class, qChannels.name, qChannels.profileUrl,
                qVideos.thumbnail, qVideos.title, qVideos.createAt, qVideos.readCnt, qVideos.videoId))
        .from(qVideos).leftJoin(qVideos.channelId, qChannels)
        .where(qVideos.videoId.loe(total - (page - 1))).orderBy(qVideos.videoId.desc())
        .limit(pageSize).fetch();
  }


  // 상세보기
  @Override
  public List<GetDetailResponseDto> getDetail(Long id) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;

    return jpaQueryFactory.select(
            Projections.bean(GetDetailResponseDto.class, qChannels.name, qChannels.profileUrl,
                qVideos.videoUrl, qVideos.commentPermission, qVideos.title, qVideos.content,
                qVideos.createAt, qVideos.readCnt, qVideos.videoId)).from(qVideos)
        .leftJoin(qVideos.channelId, qChannels).where(qVideos.videoId.eq(id)).fetch();
  }

  // 영상 삭제
  @Override
  public Long remove(Long id, Channels channelId) {
    QVideos qVideos = QVideos.videos;

    return jpaQueryFactory.delete(qVideos)
        .where(qVideos.videoId.eq(id).and(qVideos.channelId.eq(channelId))).execute();
  }

  // 영상 업로드
  @Override
  public int insert(PostUploadRequestDto videoResponseDto) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    QAccounts qAccounts = QAccounts.accounts;

    // TODO : chcannelId 가져오기.
    Channels channel = new Channels();
    channel.setChannelId(1L);
    //jpaExpressions.select(account_id).from(qAccounts).where(videoResponseDto.getUserToken())

    // Videos 테이블 create.
    int result = entityManager.createNativeQuery(
            "INSERT INTO Videos (video_url, thumbnail, title, content, private_type, create_at, channel_id, read_cnt) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
        .setParameter(1, videoResponseDto.getVideoUrl())
        .setParameter(2, videoResponseDto.getThumbnailUrl())
        .setParameter(3, videoResponseDto.getTitle())
        .setParameter(4, videoResponseDto.getContent())
        .setParameter(5, videoResponseDto.getPrivate_type())
        .setParameter(6, new Timestamp(System.currentTimeMillis())).setParameter(7, channel)
        .setParameter(8, 0).executeUpdate();

    // ViewLog 테이블 create.
    if (result != 0) {
      entityManager.createNativeQuery("INSERT INTO view_log VALUES ((SELECT LAST_INSERT_ID()), ?);")
          .setParameter(1, 0).executeUpdate();
    }

    return result;
  }

  // 영상 검색
  @Override
  public List<GetSearchResponseDto> findBysearchQuery(String searchQuery) {
    QVideos qVideos = QVideos.videos;

    Query q = entityManager.createNativeQuery(
        "SELECT video_id, v.content, create_at, v.private_type, read_cnt, thumbnail, title, profile_url, name FROM videos v\n"
            + "    left outer join channels c on v.channel_id = c.channel_id\n"
            + "    WHERE MATCH (v.title, v.content)\n"
            + "    AGAINST (? WITH QUERY EXPANSION)", Videos.class);
    q.setParameter(1, searchQuery);
    return q.getResultList();
  }
}
