package com.shuru.project.model;

import com.shuru.project.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long reservationId;

    private Long tableId;

    private String customerName;

    private Integer guestCount;

    private LocalDate reservationDate;

    private LocalTime slotTime;

    private ReservationStatus status;
}