package com.backend.lavugio.config;

import com.backend.lavugio.repository.user.DriverRepository;
import com.backend.lavugio.service.user.DriverActivityService;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DriverStatusResetConfig {

    private final DriverActivityService driverService;

    public DriverStatusResetConfig(DriverActivityService driverService) {
        this.driverService = driverService;
    }

//    @Bean
//    public ApplicationRunner resetDriverStatus() {
//        return args -> driverService.endActivityForAllDrivers();
//    }
}
