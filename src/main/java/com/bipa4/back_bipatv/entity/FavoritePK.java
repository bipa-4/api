package com.bipa4.back_bipatv.entity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Embeddable
public class FavoritePK implements Serializable {

  @ManyToOne
  @JoinColumn(name = "video_id")
  private Videos videos;
  @ManyToOne
  @JoinColumn(name = "account_id")
  private Accounts accounts;
}
