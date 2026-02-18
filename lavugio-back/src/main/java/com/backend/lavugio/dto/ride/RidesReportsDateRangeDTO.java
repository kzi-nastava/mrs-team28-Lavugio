package com.backend.lavugio.dto.ride;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RidesReportsDateRangeDTO {
    @NotNull(message = "Start date is required")
    private String startDate;
    @NotNull(message = "End date is required")
    private String endDate;
}
