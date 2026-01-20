package com.backend.lavugio.service.ride;

import com.backend.lavugio.dto.ride.FinishRideDTO;

public interface RideCompletionService {
    void finishRide(FinishRideDTO rideDTO);
}
