package com.backend.lavugio.service.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.dto.ride.RideOverviewDTO;
import com.backend.lavugio.dto.ride.RideOverviewUpdateDTO;

public interface RideOverviewService {
    RideOverviewDTO getRideOverviewDTO(Long rideId, Long userId);
    void sendRideOverviewUpdateDTO(Long rideId, String endAddress, CoordinatesDTO coordinates);
    void sendRideOverviewUpdateDTO(RideOverviewUpdateDTO rideOverviewUpdateDTO, Long rideId);
    boolean canAccessRideOverview(Long userId,  Long rideId);
}
