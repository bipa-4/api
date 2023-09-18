package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.channel.CustomChannelTop10;
import com.bipa4.back_bipatv.entity.Channels;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<Channels, Long> {

  @Query(value = "select a.* "
      + "from channels a "
      + "join accounts b "
      + "on a.account_id = b.account_id "
      + "where b.account_id = :accountId", nativeQuery = true)
  Optional<Channels> findByChannelName(Long accountId);

  @Modifying
  @Query(value =
      "select c.name as channelName, c.profile_url as profileUrl, c.content as content, sum(read_cnt - view_cnt) as timeLimitSumCnt\n"
          + "from videos as v\n"
          + "left join channels as c on c.channel_id = v.channel_id\n"
          + "left join view_log vl on v.video_id = vl.video_id\n"
          + "where v.private_type = false\n"
          + "and c.private_type = false\n"
          + "group by c.name\n"
          + "order by sum(read_cnt - view_cnt) desc\n"
          + "limit 10 ", nativeQuery = true)
  List<CustomChannelTop10> findLimitTimeSumCnt();

  List<Channels> findAll();
}
