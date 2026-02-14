package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.dto.ride.RideOverviewDTO;
import com.backend.lavugio.dto.ride.RideOverviewUpdateDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.service.ride.ReviewService;
import com.backend.lavugio.service.ride.RideOverviewService;
import com.backend.lavugio.service.ride.RideReportService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.route.RideDestinationService;
import com.backend.lavugio.service.user.RegularUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RideOverviewServiceImpl implements RideOverviewService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RideService rideService;
    private final RideDestinationService rideDestinationService;
    private final ReviewService reviewService;
    private final RideReportService rideReportService;
    private final RegularUserService regularUserService;

    @Autowired
    public RideOverviewServiceImpl(RideService rideService,
                                   RideDestinationService rideDestinationService,
                                   ReviewService reviewService,
                                   RideReportService rideReportService,
                                   SimpMessagingTemplate simpMessagingTemplate,
                                   RegularUserService regularUserService) {
        this.rideService = rideService;
        this.rideDestinationService = rideDestinationService;
        this.reviewService = reviewService;
        this.rideReportService = rideReportService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.regularUserService = regularUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public RideOverviewDTO getRideOverviewDTO(Long rideId, Long userId) {
        Ride ride = rideService.getRideById(rideId);
        if (ride == null) {
            throw new NoSuchElementException("Ride not found");
        }
        if (ride.getPassengers().stream().noneMatch(p -> p.getId().equals(userId))) {
            throw new IllegalStateException("User is not participating in this ride");
        }

        List<RideDestination> checkpoints = rideDestinationService.getOrderedDestinationsByRideId(rideId);
        if (checkpoints == null || checkpoints.isEmpty()) {
            throw new NoSuchElementException("Ride has no destinations");
        }

        List<CoordinatesDTO> coordinates = checkpoints.stream()
                .map(checkpoint ->
                        new CoordinatesDTO(checkpoint.getAddress().getLatitude(), checkpoint.getAddress().getLongitude())
                ).toList();
        boolean hasReported = rideReportService.hasReported(userId, rideId);
        boolean hasReviewed = reviewService.hasReviewed(userId, rideId);
        return new RideOverviewDTO(ride, coordinates, checkpoints.getFirst().getAddress().toString(),
                checkpoints.getLast().getAddress().toString(), hasReviewed, hasReported);
    }

    @Override
    public void sendRideOverviewUpdateDTO(Long rideId, String endAddress, CoordinatesDTO coordinates) {
        Ride ride =  rideService.getRideById(rideId);
        if (ride == null) {
            throw new NoSuchElementException("Ride not found");
        }
        System.out.println("Sending ride overview update...");
        RideOverviewUpdateDTO rideOverviewUpdateDTO = new RideOverviewUpdateDTO(endAddress, coordinates, ride);
        simpMessagingTemplate.convertAndSend("/socket-publisher/rides/" + rideId + "/update", rideOverviewUpdateDTO);
    }

    @Override
    public void sendRideOverviewUpdateDTO(RideOverviewUpdateDTO rideOverviewUpdateDTO, Long rideId) {
        simpMessagingTemplate.convertAndSend("/socket-publisher/rides/" + rideId + "/update", rideOverviewUpdateDTO);
    }

    @Override
    public boolean canAccessRideOverview(Long userId, Long rideId) {
        RegularUser user =  regularUserService.getRegularUserById(userId);
        Ride ride =  rideService.getRideById(rideId);
        if (ride == null) {
            throw new NoSuchElementException("Ride not found");
        }
        if (user == null){
            throw new  NoSuchElementException("User not found");
        }
        return ride.getPassengers().stream().anyMatch(p -> p.getId().equals(userId));
    }

}
