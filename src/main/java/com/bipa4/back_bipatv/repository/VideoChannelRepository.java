package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.video.GetSearchResponseDto;
import com.bipa4.back_bipatv.entity.Videos;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoChannelRepository extends JpaRepository<Videos, Long> {

  @Modifying
  @Query(value =
      "SELECT BIN_TO_UUID(video_id) as videoId, v.content, create_at as createAt, v.private_type as privateType, read_cnt as readCnt, thumbnail, title as videoTitle, profile_url as channelProfileUrl, name as channelName FROM videos v\n"
          + "    left outer join channels c on v.channel_id = c.channel_id\n"
          + "    WHERE MATCH (v.title, v.content)\n"
          + "    AGAINST (:searchQuery WITH QUERY EXPANSION)", nativeQuery = true)
  List<GetSearchResponseDto> findBySearchQuery(@Param("searchQuery") String searchQuery);

}
