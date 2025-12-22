package io.github.bagdad.models.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingUpdateRequest(

        @NotNull
        Long id,

        @Min(value = 1, message = "Passenger count must be positive")
        @Max(value = 500, message = "Passenger count must be equal or less than 500")
        Integer passengerCount
) {

}