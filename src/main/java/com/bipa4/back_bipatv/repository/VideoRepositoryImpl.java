package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.entity.QChannels;
import com.bipa4.back_bipatv.entity.QVideos;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class VideoRepositoryImpl implements VideoRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  // 전체보기 (무한 스크롤)
  @Override
  public List<GetVideoResponseDto> getAllVideos(int page, int pageSize) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    Long total = jpaQueryFactory.select(qVideos.count()).from(qVideos).fetchFirst();

    return jpaQueryFactory
        .select(
            Projections.bean(
                GetVideoResponseDto.class,
                qChannels.name,
                qChannels.profileUrl,
                qVideos.thumbnail,
                qVideos.title,
                qVideos.createAt,
                qVideos.readCnt,
                qVideos.videoId
            )
        )
        .from(qVideos)
        .leftJoin(qVideos.channelId, qChannels)
        .where(qVideos.videoId.loe(total - (page - 1)))
        .orderBy(qVideos.videoId.desc())
        .limit(pageSize)
        .fetch();
  }


  // 상세보기
  @Override
  public List<GetDetailResponseDto> getDetail(Long id) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;

    return jpaQueryFactory
        .select(
            Projections.bean(
                GetDetailResponseDto.class,
                qChannels.name,
                qChannels.profileUrl,
                qVideos.videoUrl,
                qVideos.commentPermission,
                qVideos.title,
                qVideos.content,
                qVideos.createAt,
                qVideos.readCnt,
                qVideos.videoId
            )
        )
        .from(qVideos)
        .leftJoin(qVideos.channelId, qChannels)
        .where(qVideos.videoId.eq(id))
        .fetch();
  }

  @Override
  public Long remove(Long id, Channels channelId) {
    QVideos qVideos = QVideos.videos;

    return jpaQueryFactory
        .delete(qVideos)
        .where(qVideos.videoId.eq(id)
            .and(qVideos.channelId.eq(channelId)))
        .execute();
  }
}
