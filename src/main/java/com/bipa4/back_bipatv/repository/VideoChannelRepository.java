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
      "SELECT video_id, v.content, create_at, v.private_type, read_cnt, thumbnail, title as video_title, profile_url as channel_profile_url, name as channel_name FROM videos v\n"
          + "    left outer join channels c on v.channel_id = c.channel_id\n"
          + "    WHERE MATCH (v.title, v.content)\n"
          + "    AGAINST (:searchQuery WITH QUERY EXPANSION)", nativeQuery = true)
  List<GetSearchResponseDto> findBySearchQuery(@Param("searchQuery") String searchQuery);

}
