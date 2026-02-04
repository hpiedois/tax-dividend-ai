package com.taxdividend.backend.repository;

import com.taxdividend.backend.model.DividendStatement;
import com.taxdividend.backend.model.DividendStatementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for DividendStatement entity.
 */
@Repository
public interface DividendStatementRepository extends JpaRepository<DividendStatement, UUID> {

    /**
     * Find all statements for a user with pagination.
     * Uses EntityGraph to avoid N+1 queries
     */
    @EntityGraph(attributePaths = {"user"})
    Page<DividendStatement> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find all statements for a user by status with pagination.
     * Uses EntityGraph to avoid N+1 queries
     */
    @EntityGraph(attributePaths = {"user"})
    Page<DividendStatement> findByUserIdAndStatus(UUID userId, DividendStatementStatus status, Pageable pageable);

    /**
     * Find all statements for a user within a period range.
     * Uses EntityGraph to avoid N+1 queries
     */
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT s FROM DividendStatement s WHERE s.user.id = :userId " +
           "AND s.periodStart >= :startDate AND s.periodEnd <= :endDate")
    List<DividendStatement> findByUserIdAndPeriodBetween(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find statement by ID and user ID (for ownership validation).
     */
    Optional<DividendStatement> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Find all statements by broker for a user.
     * Uses EntityGraph to avoid N+1 queries
     */
    @EntityGraph(attributePaths = {"user"})
    List<DividendStatement> findByUserIdAndBroker(UUID userId, String broker);

    /**
     * Count statements by status for a user.
     */
    long countByUserIdAndStatus(UUID userId, DividendStatementStatus status);
}
