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
public class RideReportedDTO {
    Long reportId;
    Long rideId;
    Long reporterId;
    String comment;

    public RideReportedDTO(RideReport report){
        reportId = report.getReportId();
        rideId = report.getRide().getId();
        reporterId = report.getReporter().getId();
        comment = report.getReportMessage();
    }
}
