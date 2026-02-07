package com.backend.lavugio.service.ride;

import com.backend.lavugio.dto.ride.RidesReportsAdminFiltersDTO;
import com.backend.lavugio.dto.ride.RidesReportsDateRangeDTO;
import com.backend.lavugio.dto.ride.RidesReportsResponseDTO;

public interface RidesReportsService {
    RidesReportsResponseDTO getRidesReportsDriver(RidesReportsDateRangeDTO dateRange, Long driverId);
    RidesReportsResponseDTO getRidesReportsRegularUser(RidesReportsDateRangeDTO dateRange, Long regularUserId);
    RidesReportsResponseDTO getRidesReportsAdmin(RidesReportsAdminFiltersDTO adminFilters);
}
