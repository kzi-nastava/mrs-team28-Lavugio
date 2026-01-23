package com.backend.lavugio.repository.user;

import com.backend.lavugio.model.user.DriverUpdateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverUpdateRequestRepository extends JpaRepository<DriverUpdateRequest, Long> {
    List<DriverUpdateRequest> findByValidatedFalse();
}
