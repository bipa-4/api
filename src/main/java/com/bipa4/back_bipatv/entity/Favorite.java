package com.bipa4.back_bipatv.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Favorite")
@Data
public class Favorite{
    @EmbeddedId
    private FavoritePK favoritePK;
}
