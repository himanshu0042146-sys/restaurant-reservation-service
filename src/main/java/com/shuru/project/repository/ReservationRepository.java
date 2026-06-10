package com.shuru.project.repository;

import com.shuru.project.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    @Query("""
        SELECT COALESCE(SUM(r.guestCount),0)
        FROM Reservation r
        WHERE r.table.id = :tableId
        AND r.reservationDate = :date
        AND r.slotTime = :slotTime
        AND r.status = 'CONFIRMED'
        """)
    Integer getBookedSeats(
            @Param("tableId") Long tableId,
            @Param("date") LocalDate date,
            @Param("slotTime") LocalTime slotTime);

}