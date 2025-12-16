package io.github.bagdad.ticketbooking.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingUpdate(

        @NotNull
        Long id,

        @Min(value = 1, message = "Passenger count must be positive")
        @Max(value = 500, message = "Passenger count must be equal or less than 500")
        Integer passengerCount
) {

}