package com.backend.lavugio.dto.ride;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RidesReportsAdminFiltersDTO {
        @NotNull(message = "Start date is required")
        private String startDate;
        @NotNull(message = "End date is required")
        private String endDate;
        private String email;
        @NotNull(message = "Selected filter is required")
        private RidesReportsAdminFilterEnum selectedFilter;
}
