package com.bipa4.back_bipatv.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class FavoritePK implements Serializable {
    private int videoId;
    private int accountId;
}
