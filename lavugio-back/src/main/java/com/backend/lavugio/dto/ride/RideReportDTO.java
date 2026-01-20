package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.ride.RideReport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideReportDTO {
    Long rideId;
    Long reporterId;
    String comment;
}
