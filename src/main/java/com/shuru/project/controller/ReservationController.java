package com.shuru.project.controller;

import com.shuru.project.model.ReservationRequest;
import com.shuru.project.model.ReservationResponse;
import com.shuru.project.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> bookTable(
             @RequestBody ReservationRequest request) {

        ReservationResponse response =
                reservationService.bookTable(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id) {

        reservationService.cancelReservation(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> getReservations(
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
                reservationService.getReservations(pageable));
    }
}