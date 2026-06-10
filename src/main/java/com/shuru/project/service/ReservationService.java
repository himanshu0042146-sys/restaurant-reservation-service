package com.shuru.project.service;

import com.shuru.project.entity.Reservation;
import com.shuru.project.entity.ReservationStatus;
import com.shuru.project.entity.RestaurantTable;
import com.shuru.project.model.ReservationRequest;
import com.shuru.project.model.ReservationResponse;
import com.shuru.project.repository.ReservationRepository;
import com.shuru.project.repository.RestaurantTableRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.ResourceClosedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;

    @Transactional
    public ReservationResponse bookTable(
            ReservationRequest request) {

        RestaurantTable table =
                tableRepository
                        .findByIdForUpdate(
                                request.getTableId())
                        .orElseThrow(() ->
                                new ResourceClosedException(
                                        "Table not found"));

        Integer bookedSeats =
                reservationRepository
                        .getBookedSeats(
                                table.getId(),
                                request.getReservationDate(),
                                request.getSlotTime());

        bookedSeats =
                bookedSeats == null ? 0 : bookedSeats;

        int remainingSeats =
                table.getCapacity() - bookedSeats;

        if (remainingSeats < request.getGuestCount()) {

            throw new RuntimeException(
                    "Not enough seats available");
        }

        Reservation reservation =
                Reservation.builder()
                        .customerName(
                                request.getCustomerName())
                        .customerEmail(
                                request.getCustomerEmail())
                        .customerPhone(
                                request.getCustomerPhone())
                        .guestCount(
                                request.getGuestCount())
                        .specialRequest(
                                request.getSpecialRequest())
                        .reservationDate(
                                request.getReservationDate())
                        .slotTime(
                                request.getSlotTime())
                        .table(table)
                        .status(
                                ReservationStatus.CONFIRMED)
                        .createdAt(
                                LocalDateTime.now())
                        .build();

        Reservation saved =
                reservationRepository.save(reservation);

        return mapToResponse(saved);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {

        Reservation reservation =
                reservationRepository.findById(
                        reservationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reservation not found"));

        LocalDateTime reservationDateTime =
                LocalDateTime.of(
                        reservation.getReservationDate(),
                        reservation.getSlotTime());

        if (LocalDateTime.now()
                .isAfter(
                        reservationDateTime.minusHours(2))) {

            throw new IllegalStateException(
                    "Cancellation not allowed within 2 hours");
        }

        reservation.setStatus(
                ReservationStatus.CANCELLED);
    }

    public Page<ReservationResponse> getReservations(
            Pageable pageable) {

        return reservationRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    private ReservationResponse mapToResponse(
            Reservation reservation) {

        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .customerName(
                        reservation.getCustomerName())
                .tableId(
                        reservation.getTable().getId())
                .guestCount(
                        reservation.getGuestCount())
                .reservationDate(
                        reservation.getReservationDate())
                .slotTime(
                        reservation.getSlotTime())
                .status(
                        reservation.getStatus())
                .build();
    }
}