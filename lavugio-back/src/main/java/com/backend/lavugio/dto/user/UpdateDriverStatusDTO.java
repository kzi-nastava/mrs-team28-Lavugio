package com.backend.lavugio.dto.user;

import com.backend.lavugio.dto.CoordinatesDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverStatusDTO {
    @Valid
    @NotNull
    private CoordinatesDTO driverLocation;

    private boolean available;
}
