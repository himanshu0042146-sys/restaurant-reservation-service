# Restaurant Table Reservation System

## Overview

A RESTful API for managing restaurant table reservations.

The system allows customers to:

* View available reservation slots
* View fully available and partially available tables
* Book tables
* Cancel reservations (subject to business rules)

The application supports shared-table reservations where multiple customers can reserve seats on the same table during the same time slot, provided the table capacity is not exceeded.

---

# Features

## Basic Requirements

### View Available Slots

Retrieve all available reservation slots for a given date.

Business Rules:

* Restaurant operates on fixed 60-minute slots.
* Tables cannot be booked beyond their capacity.
* Double booking beyond capacity is not allowed.

### Book Table

Create a reservation for a selected table and time slot.

Reservation includes:

* Customer information
* Guest count
* Special requests

---

## Advanced Requirements

### Enhanced Availability Search

In addition to fully available tables, return:

* Partially filled tables
* Remaining available seats
* Current booked seats

If no table is available for the requested slot:

* Suggest the next available time slot

### Shared Table Support

Multiple reservations may share the same table within the same slot.

Example:

Table Capacity = 4

Current Reservations:

| Guests |
| ------ |
| 2      |

Remaining Capacity = 2

A new reservation for 2 guests is allowed.

### Reservation Cancellation

Cancellation is permitted only if performed at least 2 hours before the reservation time.

### Concurrency Handling

The system prevents overbooking when multiple customers attempt to reserve seats simultaneously.

---

# Assumptions

## Operating Hours

The restaurant supports the following reservation slots:

* 18:00
* 19:00
* 20:00
* 21:00

These slots are seeded during application startup.

## Shared Table Model

A table is considered:

### Available

Booked Seats = 0

### Partially Available

Booked Seats < Capacity

### Fully Booked

Booked Seats = Capacity

---

# API Documentation

## 1. View Available Slots

### Endpoint

```http
GET /api/v1/availability
```

### Query Parameters

| Parameter  | Type      | Required |
| ---------- | --------- | -------- |
| date       | LocalDate | Yes      |
| guestCount | Integer   | Yes      |

### Sample Request

```http
GET /api/v1/availability?date=2026-06-15&guestCount=2
```

### Sample Response

```json
{
  "date": "2026-06-15",
  "slots": [
    {
      "timeSlot": "19:00",
      "tables": [
        {
          "tableId": 1,
          "capacity": 4,
          "bookedSeats": 2,
          "remainingSeats": 2,
          "status": "PARTIALLY_AVAILABLE"
        },
        {
          "tableId": 2,
          "capacity": 6,
          "bookedSeats": 0,
          "remainingSeats": 6,
          "status": "AVAILABLE"
        }
      ]
    }
  ]
}
```

### No Availability Response

```json
{
  "requestedSlot": "19:00",
  "available": false,
  "nextAvailableSlot": "20:00"
}
```

---

# 2. Book Table

### Endpoint

```http
POST /api/v1/reservations
```

### Request Body

```json
{
  "tableId": 1,
  "reservationDate": "2026-06-15",
  "timeSlot": "19:00",
  "guestCount": 2,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "customerPhone": "9999999999",
  "specialRequest": "Window seat"
}
```

### Success Response

```json
{
  "reservationId": 1001,
  "status": "CONFIRMED",
  "tableId": 1,
  "reservationDate": "2026-06-15",
  "timeSlot": "19:00"
}
```

### Error Response

```json
{
  "message": "Selected table no longer has sufficient capacity."
}
```

HTTP Status:

```http
409 CONFLICT
```

---

# 3. Cancel Reservation

### Endpoint

```http
DELETE /api/v1/reservations/{reservationId}
```

### Success Response

```json
{
  "message": "Reservation cancelled successfully."
}
```

### Cancellation Window Expired

```json
{
  "message": "Cancellation is allowed only up to 2 hours before reservation time."
}
```

HTTP Status:

```http
409 CONFLICT
```

---

# Data Model

## RestaurantTable

| Field       | Type    |
| ----------- | ------- |
| id          | Long    |
| tableNumber | String  |
| capacity    | Integer |

## Reservation

| Field           | Type              |
| --------------- | ----------------- |
| id              | Long              |
| tableId         | Long              |
| reservationDate | LocalDate         |
| timeSlot        | LocalTime         |
| guestCount      | Integer           |
| customerName    | String            |
| customerEmail   | String            |
| customerPhone   | String            |
| specialRequest  | String            |
| status          | ReservationStatus |

## ReservationStatus

```java
public enum ReservationStatus {
    CONFIRMED,
    CANCELLED
}
```

---

# Capacity Calculation

Available capacity is calculated as:

```text
remainingSeats = tableCapacity - totalBookedSeats
```

Reservation validation:

```text
totalBookedSeats + requestedGuestCount <= tableCapacity
```

Only reservations satisfying this condition are accepted.

---

# Concurrency Strategy

## Problem

Two customers may attempt to reserve seats for the same table and time slot simultaneously.

Example:

```text
Table Capacity = 4

Customer A requests 3 seats
Customer B requests 2 seats
```

Without concurrency control, both reservations may succeed and exceed table capacity.

## Solution

Pessimistic database locking is used during reservation creation.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

### Why Pessimistic Locking?

Advantages:

* Prevents overbooking.
* Guarantees data consistency.
* Simple implementation for reservation systems.

Alternative approaches such as optimistic locking require retries and are more suitable for systems with lower contention.

---

# Validation Rules

* Reservation date cannot be in the past.
* Guest count must be greater than zero.
* Table must exist.
* Time slot must exist.
* Capacity cannot be exceeded.
* Cancellation allowed only up to 2 hours before reservation time.

---

# HTTP Status Codes

| Status | Meaning                 |
| ------ | ----------------------- |
| 200    | Success                 |
| 201    | Reservation Created     |
| 400    | Bad Request             |
| 404    | Resource Not Found      |
| 409    | Business Rule Violation |
| 500    | Internal Server Error   |

---

# Testing Strategy

## Unit Tests

* Availability calculation
* Remaining seat calculation
* Capacity validation
* Next available slot suggestion
* Cancellation validation

## Integration Tests

### Successful Reservation

```text
Capacity = 4
Booked = 2
Requested = 2

Result = Success
```

### Overbooking Attempt

```text
Capacity = 4
Booked = 3
Requested = 2

Result = Rejected
```

### Concurrent Reservation Scenario

```text
Thread A -> 3 guests
Thread B -> 2 guests
Capacity -> 4
```

Expected Result:

```text
Only one transaction succeeds.
```

---

# Project Structure

```text
src/main/java

├── controller
│   ├── AvailabilityController
│   └── ReservationController
│
├── service
│   ├── AvailabilityService
│   └── ReservationService
│
├── repository
│   ├── RestaurantTableRepository
│   └── ReservationRepository
│
├── entity
│   ├── RestaurantTable
│   ├── Reservation
│   └── ReservationStatus
│
├── dto
│   ├── request
│   └── response
│
├── exception
│   ├── GlobalExceptionHandler
│   ├── CapacityExceededException
│   └── ReservationNotFoundException
│
└── config
    └── SlotConfiguration
```

---

# Future Enhancements

* Dynamic slot configuration
* Waitlist management
* Email/SMS notifications
* Reservation modification API
* Customer loyalty integration
* Table combination support
* Admin dashboard
