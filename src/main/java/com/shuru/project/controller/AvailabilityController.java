package com.shuru.project.controller;

import com.shuru.project.model.AvailabilityResponse;
import com.shuru.project.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping
    public ResponseEntity<AvailabilityResponse> getAvailableSlots(
            @RequestParam LocalDate date,
            @RequestParam Integer guestCount) {

        AvailabilityResponse response =
                availabilityService.getAvailableSlots(
                        date,
                        guestCount);

        return ResponseEntity.ok(response);
    }
}