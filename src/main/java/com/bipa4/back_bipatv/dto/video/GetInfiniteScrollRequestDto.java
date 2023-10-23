package com.bipa4.back_bipatv.dto.video;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public class GetInfiniteScrollRequestDto {

  private List<GetVideoResponseDto> videos;
  private UUID nextUUID;
}
