package com.bipa4.back_bipatv.dto;

import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Videos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    private int commentId;
    private String content;
    private int parentChild;
    private int groupIndex;
    private Timestamp createAt;
    private int videoId;
    private int accountId;
    @Override
    public String toString() {
        return "CommentRequest{" +
                "commentId=" + commentId +
                ", content='" + content + '\'' +
                ", parentChild=" + parentChild +
                ", groupIndex=" + groupIndex +
                ", createAt=" + createAt +
                ", videoId=" + videoId +
                ", accountId=" + accountId +
                '}';
    }

}
