package com.backend.lavugio.repository.user;

import com.backend.lavugio.model.user.DriverRegistrationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRegistrationTokenRepository extends JpaRepository<DriverRegistrationToken, Long> {
    Optional<DriverRegistrationToken> findByToken(String token);
}
