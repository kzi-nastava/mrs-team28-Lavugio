package com.backend.lavugio.repository.user;

import com.backend.lavugio.model.user.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegularUserRepository extends JpaRepository<RegularUser, Long> {
    Optional<RegularUser> findByEmail(String email);
    boolean existsByEmail(String email);
}