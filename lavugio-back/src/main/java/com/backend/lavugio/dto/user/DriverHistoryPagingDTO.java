package com.backend.lavugio.dto.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DriverHistoryPagingDTO {
    @Valid
    private DriverHistoryDTO[] driverHistory;

    @PositiveOrZero
    private Long totalElements;

    private boolean reachedEnd;
}
