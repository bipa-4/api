package com.bipa4.back_bipatv.dto.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
public class GetTop5StatisticsDTO {
    private UUID channelId;
    private Integer statistic;
}
