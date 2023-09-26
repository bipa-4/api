package com.bipa4.back_bipatv.dto.video;

import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Videos;

public class FavoritePKDto {

  private Videos video;
  private Accounts accounts;

  public FavoritePKDto(Videos video, Accounts accounts) {
    this.video = video;
    this.accounts = accounts;
  }
}
