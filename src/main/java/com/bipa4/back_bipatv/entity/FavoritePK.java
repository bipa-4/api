package com.bipa4.back_bipatv.entity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class FavoritePK implements Serializable {

  private int videoId;
  private int accountId;
}
