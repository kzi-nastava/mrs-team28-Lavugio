package com.backend.lavugio.repository.user;

import com.backend.lavugio.model.user.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Account> findByEmailAndPassword(String email, String password);
    @Query("SELECT ba.email FROM BlockableAccount ba WHERE LOWER(ba.email) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY ba.email")
    List<String> findTop5EmailsByPrefix(@Param("prefix") String prefix, Pageable pageable);

}