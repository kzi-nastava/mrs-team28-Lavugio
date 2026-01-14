package com.backend.lavugio.service.ride;

import com.backend.lavugio.dto.ride.ScheduledRideDTO;

import java.util.List;

public interface ScheduledRideService {

    List<ScheduledRideDTO> getScheduledRidesForDriver(Long driverId);
}
