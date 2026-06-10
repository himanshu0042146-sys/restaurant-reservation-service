package com.shuru.project.model;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {

    @NotNull
    private Long tableId;

    @NotNull
    private LocalDate reservationDate;

    @NotNull
    private LocalTime slotTime;


    private Integer guestCount;

    @NonNull
    private String customerName;


    private String customerEmail;

    private String customerPhone;

    private String specialRequest;
}