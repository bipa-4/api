package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
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

  @Override
  public List<GetVideoResponseDto> getAllVideos(int page, int pageSize) {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    Long total = jpaQueryFactory.select(qVideos.count()).from(qVideos).fetchFirst();
    System.out.println(total - (page - 1));

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
}
