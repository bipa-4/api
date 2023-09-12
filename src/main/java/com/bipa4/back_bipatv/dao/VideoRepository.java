package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.entity.Videos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Videos, Long> {

}
