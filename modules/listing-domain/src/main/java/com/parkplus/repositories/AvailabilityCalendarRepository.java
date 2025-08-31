package com.parkplus.repositories;

import com.parkplus.entities.AvailabilityCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvailabilityCalendarRepository extends JpaRepository<AvailabilityCalendar, UUID> {

    List<AvailabilityCalendar> findByListingIdAndDateBetweenOrderByDateAsc(
            UUID listingId, LocalDate start, LocalDate end);

    @Query("""
            SELECT COUNT(a) FROM AvailabilityCalendar a
            WHERE a.listingId = :listingId
            AND a.date >= :start AND a.date < :end
            AND a.availableSpaces >= :availableSpaces
            """)
    long countDaysAvailable(@Param("listingId") UUID listingId,
                            @Param("start") LocalDate start,
                            @Param("end") LocalDate end,
                            @Param("availableSpaces") int availableSpaces);

    Optional<AvailabilityCalendar> findByListingIdAndDate(UUID listingId, LocalDate date);

    @Modifying
    @Query("DELETE FROM AvailabilityCalendar a WHERE a.listingId = :listingId AND a.date >= :start AND a.date < :end")
    int deleteRange(@Param("listingId") UUID listingId,
                    @Param("start") LocalDate start,
                    @Param("end") LocalDate end);
}
