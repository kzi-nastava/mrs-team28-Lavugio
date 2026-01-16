package com.backend.lavugio.dto.user;

import com.backend.lavugio.dto.CoordinatesDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverStatusDTO {
    private CoordinatesDTO driverLocation;
    private boolean available;
}
