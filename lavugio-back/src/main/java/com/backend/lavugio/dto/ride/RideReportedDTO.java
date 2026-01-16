package com.backend.lavugio.dto.ride;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideReportedDTO {
    Long reportId;
    Long rideId;
    Long reporterId;
    String reportText;
}
