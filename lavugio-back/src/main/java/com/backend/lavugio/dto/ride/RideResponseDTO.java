package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.user.Driver;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RideResponseDTO {
    private String assignedDriverName;
    private String assignedDriverLastName;
}
