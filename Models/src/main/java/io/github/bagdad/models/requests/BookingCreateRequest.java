package io.github.bagdad.models.requests;

import jakarta.validation.constraints.*;

public record BookingCreateRequest(

        @NotNull
        Long flightId,

        @NotNull(message = "Passenger count is required")
        @Min(value = 1, message = "Passenger count must be positive")
        @Max(value = 500, message = "Passenger count must be equal or less than 500")
        Integer passengerCount
) {

}
