package com.backend.lavugio.repository.user;

import com.backend.lavugio.model.user.UserRegistrationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRegistrationTokenRepository extends JpaRepository<UserRegistrationToken, Long> {
    Optional<UserRegistrationToken> findByToken(String token);
    Optional<UserRegistrationToken> findByUserId(Long userId);
}
