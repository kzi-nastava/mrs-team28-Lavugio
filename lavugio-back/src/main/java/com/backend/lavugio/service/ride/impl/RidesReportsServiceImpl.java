package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.ride.RidesReportsDateRangeDTO;
import com.backend.lavugio.dto.ride.RidesReportsResponseDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RidesReportsServiceImpl {
    @Autowired
    private RideService rideService;
    @Autowired
    private AccountService accountService;

    private final String DATE_FORMAT = "dd/MM/yyyy";
    private final String[] DRIVER_TITLES = {"Total Rides", "Total Mileage", "Total Earnings"};
    private final String[] USER_TITLES = {"Total Rides", "Total Mileage", "Total Spent"};

    public RidesReportsResponseDTO getRidesReportsDriver(RidesReportsDateRangeDTO dateRange) {
        List<Ride> rideList = rideService.
    }
}
