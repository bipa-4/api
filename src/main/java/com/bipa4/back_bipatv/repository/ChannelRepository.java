package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.entity.Channels;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChannelRepository extends JpaRepository<Channels, Long> {

  @Query(value = "select a.* - a.account_id "
      + "from channels a "
      + "join accounts b "
      + "on a.account_id = b.account_id "
      + "where b.account_id = :accountId", nativeQuery = true)
  Optional<Channels> findByChannelName(Long accountId);

}
