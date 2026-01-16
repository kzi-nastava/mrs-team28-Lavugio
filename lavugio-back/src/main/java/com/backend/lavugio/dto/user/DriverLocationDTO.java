package com.backend.lavugio.dto.user;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationDTO {
    private Long id;
    private CoordinatesDTO location;
    private DriverStatusEnum status;
}
