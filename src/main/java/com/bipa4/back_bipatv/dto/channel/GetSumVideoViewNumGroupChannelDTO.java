package com.bipa4.back_bipatv.dto.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
public class GetSumVideoViewNumGroupChannelDTO {

    private UUID channelId;
    private Integer totalScore;//1시간 동안 본 시청 수 합
}
