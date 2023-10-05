package com.bipa4.back_bipatv.entity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import lombok.Data;

@Data
@Embeddable
public class FavoritePK implements Serializable {

  @MapsId("video_id")
  @ManyToOne
  @JoinColumn(name = "video_id")
  private Videos videos;

  @MapsId("account_id")
  @ManyToOne
  @JoinColumn(name = "account_id")
  private Accounts accounts;
}
