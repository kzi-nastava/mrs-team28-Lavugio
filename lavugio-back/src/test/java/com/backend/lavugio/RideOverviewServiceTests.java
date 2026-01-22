package com.backend.lavugio;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.dto.ride.RideOverviewUpdateDTO;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.service.ride.RideOverviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

@SpringBootTest
public class RideOverviewServiceTests {

    @Autowired
    private RideOverviewService rideOverviewService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void testSendRideOverviewUpdateDTO() throws InterruptedException {
        RideOverviewUpdateDTO dto = new RideOverviewUpdateDTO();
        String endAddress;

        CoordinatesDTO destinationCoordinates;

        LocalDateTime departureTime;

        LocalDateTime arrivalTime;

        RideStatus status;

        Double price;

        Thread.sleep(10000);
        dto.setEndAddress("End address");
        dto.setDestinationCoordinates(destinationCoordinates = new CoordinatesDTO(0.0, 0.0));
        dto.setDepartureTime(departureTime = LocalDateTime.now().minusHours(1));
        dto.setArrivalTime(arrivalTime = LocalDateTime.now());
        dto.setStatus(RideStatus.ACTIVE);
        dto.setPrice(1000D);
        rideOverviewService.sendRideOverviewUpdateDTO(dto, 1L);
    }
}
