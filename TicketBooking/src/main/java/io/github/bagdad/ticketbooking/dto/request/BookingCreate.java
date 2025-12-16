package io.github.bagdad.ticketbooking.dto.request;

import jakarta.validation.constraints.*;

public record BookingCreate(

        @NotNull
        Long flightId,

        @NotNull(message = "Passenger count is required")
        @Min(value = 1, message = "Passenger count must be positive")
        @Max(value = 500, message = "Passenger count must be equal or less than 500")
        Integer passengerCount
) {

}
