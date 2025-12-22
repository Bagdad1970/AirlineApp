package io.github.bagdad.models.requests;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record FlightCreateRequest(

    @NotBlank(message = "Flight number is required")
    @Size(max = 10)
    String number,

    @NotBlank(message = "Departure city is required")
    @Size(max = 100)
    String fromCity,

    @NotBlank(message = "Arrival city is required")
    @Size(max = 100)
    String toCity,

    @NotNull(message = "Departure time is required")
    OffsetDateTime departure,

    @NotNull(message = "Arrival time is required")
    OffsetDateTime arrival,

    @NotNull
    @Min(value = 1, message = "Passenger count must be positive")
    @Max(value = 500, message = "Max capacity is 500")
    Integer passengerCount,

    @NotNull
    @DecimalMin(value = "0.01", message = "Ticket price must be positive")
    @Digits(integer = 8, fraction = 2)
    BigDecimal ticketPrice
) {
    public FlightCreateRequest {
        if (arrival.isBefore(departure)) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }
    }
}