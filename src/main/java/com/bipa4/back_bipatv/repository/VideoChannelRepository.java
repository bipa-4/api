package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.video.GetAllResponseDto;
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
      "select name, profile_url, create_at, read_cnt, thumbnail, title from videos as v\n"
          + "    left join channels as c on c.channel_id = v.channel_id\n"
          + "where v.private_type = 0\n"
          + "and c.private_type = 0\n"
          + "order by create_at desc", nativeQuery = true)
  List<GetAllResponseDto> findAllVideos();

  @Modifying
  @Query(value =
      "select c.name, profile_url, create_at, read_cnt, thumbnail, title from videos as v\n"
          + "    left join channels as c on c.channel_id = v.channel_id\n"
          + "    left join categorys c2 on v.video_id = c2.video_id\n"
          + "where c2.name = :category\n"
          + "and v.private_type = 0\n"
          + "and c.private_type = 0\n"
          + "order by create_at desc;", nativeQuery = true)
  List<GetAllResponseDto> findByCategory(@Param("category") String category);

  @Modifying
  @Query(value =
      "select c.name, profile_url, create_at, read_cnt, thumbnail, title from videos as v\n"
          + "    left join channels as c on c.channel_id = v.channel_id\n"
          + "    left join view_log vl on v.video_id = vl.video_id\n"
          + "where v.private_type = false\n"
          + "and c.private_type = false\n"
          + "order by (read_cnt - view_cnt) desc\n"
          + "limit 10", nativeQuery = true)
  List<GetAllResponseDto> findByViews();


  @Modifying
  @Query(value =
      "update view_log vl\n"
          + "    join videos v on vl.video_id = v.video_id\n"
          + "set view_cnt = read_cnt\n"
          + "where v.video_id = vl.video_id;", nativeQuery = true)
  int updateViews();
}
