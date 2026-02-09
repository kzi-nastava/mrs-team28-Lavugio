package com.backend.lavugio.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminHistoryPagingDTO {
    private AdminHistoryDTO[] adminHistory;
    private Long totalElements;
    private boolean reachedEnd;
}
