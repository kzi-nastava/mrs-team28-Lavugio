package com.backend.lavugio.dto.ride;


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
    @NotNull(message = "Ride ID is required")
    Long rideId;

    @NotNull
    Long reporterId;

    @NotBlank(message = "Report comment cannot be blank")
    String comment;
}
