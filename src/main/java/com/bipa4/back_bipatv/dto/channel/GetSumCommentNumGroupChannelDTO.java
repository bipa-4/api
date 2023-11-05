package com.bipa4.back_bipatv.dto.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
public class GetSumCommentNumGroupChannelDTO {

    private UUID channelId;
    private Integer sumCommentNum;//댓글 총 합
}
