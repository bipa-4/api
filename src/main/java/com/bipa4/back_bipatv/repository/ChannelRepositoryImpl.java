package com.bipa4.back_bipatv.repository;

import static com.querydsl.core.types.dsl.Expressions.asNumber;

import com.bipa4.back_bipatv.dto.channel.GetChannelDTO;
import com.bipa4.back_bipatv.dto.channel.GetChannelTop5DTO;
import com.bipa4.back_bipatv.dto.channel.SelectChannelDTO;
import com.bipa4.back_bipatv.entity.QChannels;
import com.bipa4.back_bipatv.entity.QVideos;
import com.bipa4.back_bipatv.entity.QViewLog;
import com.bipa4.back_bipatv.security.SecurityService;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChannelRepositoryImpl implements ChannelRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final SecurityService securityService;
  private final EntityManager entityManager;

  @Override
  public List<GetChannelDTO> getNotPrivateChannel(UUID page, int pageSize) {
    QChannels qChannels = QChannels.channels;
    return jpaQueryFactory.select(
            Projections.bean(
                GetChannelDTO.class,
                qChannels.channelId,
                qChannels.channelName,
                qChannels.profileUrl,
                qChannels.content,
                qChannels.privateType
            )
        )
        .from(qChannels)
        .where(
            qChannels.privateType.eq(false)
                .and(qChannels.accounts.deleteAt.isNull())
                .and(qChannels.channelId.loe(page))
        )
        .orderBy(qChannels.channelId.desc())
        .limit(pageSize).fetch();
  }

  public List<GetChannelTop5DTO> findTop5Channels() {
    QVideos qVideos = QVideos.videos;
    QChannels qChannels = QChannels.channels;
    QViewLog qViewLog = QViewLog.viewLog;

    return jpaQueryFactory.select(
            Projections.bean(
                GetChannelTop5DTO.class,
                qChannels.channelId,
                qChannels.channelName,
                qChannels.profileUrl,
                qChannels.content,
                asNumber(qVideos.readCnt.subtract(qViewLog.viewCnt)).as("timeLimitSumCnt")
            )
        )
        .from(qViewLog)
        .leftJoin(qViewLog.videoId, qVideos)
        .leftJoin(qVideos.channelId, qChannels)
        .where(
            qChannels.privateType.eq(false)
                .and(qChannels.accounts.deleteAt.isNull())
        )
        .groupBy(qChannels.channelId)
        .orderBy(
            asNumber(qVideos.readCnt.subtract(qViewLog.viewCnt)).doubleValue().desc()
        )
        .limit(5).fetch();
  }

  @Override
  public SelectChannelDTO selectChannel(UUID channelId) {
    QChannels qChannels = QChannels.channels;
    return jpaQueryFactory.select(
            Projections.bean(
                SelectChannelDTO.class,
                qChannels.channelId,
                qChannels.channelName,
                qChannels.profileUrl,
                qChannels.content,
                qChannels.privateType
            )
        )
        .from(qChannels)
        .where(
            qChannels.channelId.eq(channelId)
                .and(qChannels.accounts.deleteAt.isNull())
        )
        .fetchOne();
  }

  @Override
  public UUID lastUUID() {
    QChannels qChannels = QChannels.channels;
    return jpaQueryFactory.select(qChannels.channelId).from(qChannels)
        .where(qChannels.privateType.eq(false).and(qChannels.accounts.deleteAt.isNull()))
        .orderBy(qChannels.channelId.desc()).limit(1)
        .fetchOne();
  }

  @Override
  public String getChannelNextUUID(UUID uuid) {
    QChannels qChannels = QChannels.channels;

    UUID nextUUID = jpaQueryFactory.select(qChannels.channelId).from(qChannels)
        .where(qChannels.channelId.lt(uuid).and(qChannels.privateType.eq(false)))
        .orderBy(qChannels.channelId.desc())
        .limit(1).fetchOne();

    if (nextUUID == null) {
      return "";
    }

    return nextUUID.toString();
  }
}
