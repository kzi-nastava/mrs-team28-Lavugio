package com.backend.lavugio.repository.user;

import com.backend.lavugio.model.user.BlockableAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockableAccountRepository extends JpaRepository<BlockableAccount, Long> {
    Optional<BlockableAccount> findByEmail(String email);
    List<BlockableAccount> findByBlockedTrue();
    List<BlockableAccount> findByBlockedFalse();
    long countByBlockedTrue();
}