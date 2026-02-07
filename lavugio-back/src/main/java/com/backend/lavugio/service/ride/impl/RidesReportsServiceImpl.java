package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.ride.RidesReportsAdminFilterEnum;
import com.backend.lavugio.dto.ride.RidesReportsAdminFiltersDTO;
import com.backend.lavugio.dto.ride.RidesReportsDateRangeDTO;
import com.backend.lavugio.dto.ride.RidesReportsResponseDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.ride.RidesReportsService;
import com.backend.lavugio.service.user.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RidesReportsServiceImpl implements RidesReportsService {
    @Autowired
    private RideService rideService;
    @Autowired
    private AccountService accountService;

    private final String DATE_FORMAT = "dd/MM/yyyy";
    private final String SHORT_DATE_FORMAT = "dd/MM";
    private final String[] DRIVER_TITLES = {"Total Rides", "Total Mileage", "Total Earnings"};
    private final String[] USER_TITLES = {"Total Rides", "Total Mileage", "Total Spent"};
    private final String[] DRIVER_LABELS = {"Rides", "Mileage (km)", "Earnings (RSD)"};
    private final String[] USER_LABELS = {"Rides", "Mileage (km)", "Spent (RSD)"};

    @Override
    public RidesReportsResponseDTO getRidesReportsDriver(RidesReportsDateRangeDTO dateRange, Long driverId) {
        LocalDateTime startDate = parseDate(dateRange.getStartDate());
        LocalDateTime endDate = parseDate(dateRange.getEndDate());
        List<Ride> rideList = rideService.getFinishedRidesForDriverInDateRange(driverId, startDate, endDate);
        Map<String, Double> ridesCountMap = getDateRangeMap(startDate, endDate);
        Map<String, Double> mileageMap = getDateRangeMap(startDate, endDate);
        Map<String, Double> earningsMap = getDateRangeMap(startDate, endDate);
        for (Ride ride : rideList) {
            String dateKey = formatDateShortString(ride.getStartDateTime());
            ridesCountMap.put(dateKey, ridesCountMap.get(dateKey) + 1);
            mileageMap.put(dateKey, mileageMap.get(dateKey) + ride.getDistance());
            earningsMap.put(dateKey, earningsMap.get(dateKey) + ride.getPrice());
        }
        RidesReportsResponseDTO response = this.getCharts(ridesCountMap, mileageMap, earningsMap, DRIVER_TITLES, DRIVER_LABELS);
        return response;
    }

    @Override
    public RidesReportsResponseDTO getRidesReportsRegularUser(RidesReportsDateRangeDTO dateRange, Long regularUserId) {
        LocalDateTime startDate = parseDate(dateRange.getStartDate());
        LocalDateTime endDate = parseDate(dateRange.getEndDate());
        List<Ride> rideList = rideService.getFinishedRidesForCreatorInDateRange(regularUserId, startDate, endDate);
        Map<String, Double> ridesCountMap = getDateRangeMap(startDate, endDate);
        Map<String, Double> mileageMap = getDateRangeMap(startDate, endDate);
        Map<String, Double> earningsMap = getDateRangeMap(startDate, endDate);
        for (Ride ride : rideList) {
            String dateKey = formatDateShortString(ride.getStartDateTime());
            ridesCountMap.put(dateKey, ridesCountMap.get(dateKey) + 1);
            mileageMap.put(dateKey, mileageMap.get(dateKey) + ride.getDistance());
            earningsMap.put(dateKey, earningsMap.get(dateKey) + ride.getPrice());
        }
        RidesReportsResponseDTO response = this.getCharts(ridesCountMap, mileageMap, earningsMap, USER_TITLES, USER_LABELS);
        return response;
    }

    @Override
    public RidesReportsResponseDTO getRidesReportsAdmin(RidesReportsAdminFiltersDTO adminFilters) {
        if (adminFilters.getSelectedFilter().equals(RidesReportsAdminFilterEnum.oneUser)) {
            Account account = this.accountService.getAccountByEmail(adminFilters.getAccountEmail());
            if (account == null) {
                throw new RuntimeException("Account with email " + adminFilters.getAccountEmail() + " not found.");
            }
            if (account instanceof Driver) {
                return getRidesReportsDriver(new RidesReportsDateRangeDTO(adminFilters.getStartDate(), adminFilters.getEndDate()), account.getId());
            } else if (account instanceof RegularUser){
                return getRidesReportsRegularUser(new RidesReportsDateRangeDTO(adminFilters.getStartDate(), adminFilters.getEndDate()), account.getId());
            } else {
                throw new RuntimeException("Account with email " + adminFilters.getAccountEmail() + " is neither a driver nor a regular user.");
            }
        }
        LocalDateTime startDate = parseDate(adminFilters.getStartDate());
        LocalDateTime endDate = parseDate(adminFilters.getEndDate());
        List<Ride> rideList = rideService.getFinishedRidesInDateRange(parseDate(adminFilters.getStartDate()), parseDate(adminFilters.getEndDate()));
        Map<String, Double> ridesCountMap = getDateRangeMap(startDate, endDate);
        Map<String, Double> mileageMap = getDateRangeMap(startDate, endDate);
        Map<String, Double> earningsMap = getDateRangeMap(startDate, endDate);
        for (Ride ride : rideList) {
            String dateKey = formatDateShortString(ride.getStartDateTime());
            ridesCountMap.put(dateKey, ridesCountMap.get(dateKey) + 1);
            mileageMap.put(dateKey, mileageMap.get(dateKey) + ride.getDistance());
            earningsMap.put(dateKey, earningsMap.get(dateKey) + ride.getPrice());
        }
        if (adminFilters.getSelectedFilter().equals(RidesReportsAdminFilterEnum.allDrivers)) {
            return this.getCharts(ridesCountMap, mileageMap, earningsMap, DRIVER_TITLES, DRIVER_LABELS);
        } else {
            return this.getCharts(ridesCountMap, mileageMap, earningsMap, USER_TITLES, USER_LABELS);
        }

    }

    private RidesReportsResponseDTO getCharts(Map<String, Double> ridesCountMap, Map<String, Double> mileageMap, Map<String, Double> earningsMap, String[] titles, String[] labels) {
        List<RidesReportsResponseDTO.ChartData> charts = new ArrayList<>();
        charts.add(createChartData(titles[0], "Date", labels[0], ridesCountMap));
        charts.add(createChartData(titles[1], "Date", labels[1], mileageMap));
        charts.add(createChartData(titles[2], "Date", labels[2], earningsMap));
        return new RidesReportsResponseDTO(charts);
    }

    private RidesReportsResponseDTO.ChartData createChartData(String title, String xAxisLabel, String yAxisLabel, Map<String, Double> dataMap) {
        List<String> labels = new ArrayList<>(dataMap.keySet());
        List<Double> data = new ArrayList<>(dataMap.values());

        double sum = 0d;
        for (Double value : data) {
            if (value != null) {
                sum += value;
            }
        }
        double average = data.isEmpty() ? 0d : sum / data.size();

        return new RidesReportsResponseDTO.ChartData(title, xAxisLabel, yAxisLabel, labels, data, sum, average);
    }

    public LocalDateTime parseDate(String dateStr) {
        java.time.LocalDate date = java.time.LocalDate.parse(dateStr, java.time.format.DateTimeFormatter.ofPattern(DATE_FORMAT));
        return date.atStartOfDay();
    }

    private String formatDateShortString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.SHORT_DATE_FORMAT);
        return date.format(formatter);
    }

    private Map<String, Double> getDateRangeMap(LocalDateTime start, LocalDateTime end) {
        Map<String, Double> dateMap = new LinkedHashMap<>();

        LocalDateTime current = start;
        while (!current.isAfter(end)) {
            dateMap.put(formatDateShortString(current), 0d);
            current = current.plusDays(1);
        }

        return dateMap;
    }

    public List<String> getDateRangeAsShortStrings(LocalDateTime start, LocalDateTime end) {
        List<String> dates = new ArrayList<>();

        LocalDateTime current = start;

        while (!current.isAfter(end)) {
            dates.add(formatDateShortString(current));
            current = current.plusDays(1);
        }

        return dates;
    }
}
