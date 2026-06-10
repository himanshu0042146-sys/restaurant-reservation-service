package com.shuru.project.service;

import com.shuru.project.entity.RestaurantTable;
import com.shuru.project.model.AvailabilityResponse;
import com.shuru.project.model.SlotAvailabilityDto;
import com.shuru.project.model.TableAvailabilityDto;
import com.shuru.project.repository.ReservationRepository;
import com.shuru.project.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final RestaurantTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public AvailabilityResponse getAvailableSlots(
            LocalDate date,
            Integer guestCount) {

        List<SlotAvailabilityDto> slots = new ArrayList<>();

        for (LocalTime slot : TimeSlots.SLOTS) {

            List<TableAvailabilityDto> availableTables =
                    getAvailableTables(
                            date,
                            slot,
                            guestCount);

            if (!availableTables.isEmpty()) {

                slots.add(
                        SlotAvailabilityDto.builder()
                                .slotTime(slot)
                                .tables(availableTables)
                                .build()
                );
            }
        }

        LocalTime nextAvailableSlot =
                slots.isEmpty()
                        ? findNextAvailableSlot(
                                date,
                                guestCount)
                        : null;

        return AvailabilityResponse.builder()
                .date(date)
                .slots(slots)
                .nextAvailableSlot(nextAvailableSlot)
                .build();
    }

    private List<TableAvailabilityDto> getAvailableTables(
            LocalDate date,
            LocalTime slot,
            Integer guestCount) {

        List<RestaurantTable> tables =
                tableRepository.findAll();

        return tables.stream()
                .map(table -> {

                    Integer bookedSeats =
                            reservationRepository
                                    .getBookedSeats(
                                            table.getId(),
                                            date,
                                            slot);

                    bookedSeats =
                            bookedSeats == null ? 0 : bookedSeats;

                    int remainingSeats =
                            table.getCapacity() - bookedSeats;

                    if (remainingSeats >= guestCount) {

                        return TableAvailabilityDto.builder()
                                .tableId(table.getId())
                                .capacity(table.getCapacity())
                                .bookedSeats(bookedSeats)
                                .remainingSeats(remainingSeats)
                                .build();
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private LocalTime findNextAvailableSlot(
            LocalDate date,
            Integer guestCount) {

        for (LocalTime slot : TimeSlots.SLOTS) {

            if (!getAvailableTables(
                    date,
                    slot,
                    guestCount).isEmpty()) {

                return slot;
            }
        }

        return null;
    }
}