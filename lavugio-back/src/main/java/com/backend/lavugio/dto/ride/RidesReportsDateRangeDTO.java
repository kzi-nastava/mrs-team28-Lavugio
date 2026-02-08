package com.backend.lavugio.dto.ride;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RidesReportsDateRangeDTO {
    private String startDate;
    private String endDate;
}
