package com.briefly.watchlist.dto;

import com.briefly.watchlist.entity.Watchlist;

import java.time.LocalDateTime;

public class WatchlistDto {
    private Long id;
    private Long userId;
    private Long fundId;
    private LocalDateTime createdAt;

    public static WatchlistDto from(Watchlist watchlist) {
        WatchlistDto dto = new WatchlistDto();
        dto.id = watchlist.getId();
        dto.userId = watchlist.getUserId();
        dto.fundId = watchlist.getFundId();
        dto.createdAt = watchlist.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getFundId() { return fundId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
