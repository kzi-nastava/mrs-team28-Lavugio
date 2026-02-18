package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.ride.RideReport;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideReportedDTO {
    @NotNull
    Long reportId;

    @NotNull
    Long rideId;

    @NotNull
    Long reporterId;

    @NotBlank
    String comment;

    public RideReportedDTO(RideReport report){
        reportId = report.getReportId();
        rideId = report.getRide().getId();
        reporterId = report.getReporter().getId();
        comment = report.getReportMessage();
    }
}
