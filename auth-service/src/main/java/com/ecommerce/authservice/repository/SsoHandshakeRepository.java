package com.ecommerce.authservice.repository;

import com.ecommerce.authservice.entity.SsoHandshake;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface SsoHandshakeRepository extends JpaRepository<SsoHandshake, String> {

    // Row lock so two concurrent requests cannot both consume the same single-use handle.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SsoHandshake> findByHandleAndKind(String handle, SsoHandshake.Kind kind);

    @Modifying
    @Query("DELETE FROM SsoHandshake h WHERE h.expiresAt < :now")
    int deleteExpired(@Param("now") Instant now);
}
