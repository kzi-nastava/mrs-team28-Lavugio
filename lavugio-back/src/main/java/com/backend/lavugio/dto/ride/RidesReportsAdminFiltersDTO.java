package com.backend.lavugio.dto.ride;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RidesReportsAdminFiltersDTO {
        private String startDate;
        private String endDate;
        private String accountEmail;
        private RidesReportsAdminFilterEnum selectedFilter;
}
