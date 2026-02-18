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
public class RideReportDTO {
    @NotNull
    Long rideId;

    @NotNull
    Long reporterId;

    @NotBlank
    String comment;
}
