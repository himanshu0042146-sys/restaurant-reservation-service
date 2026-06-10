package com.shuru.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableAvailabilityDto {

    private Long tableId;

    private Integer capacity;

    private Integer bookedSeats;

    private Integer remainingSeats;
}