package io.github.bagdad.ticketbooking.dto.request;

import jakarta.validation.constraints.NotNull;

public record BookingDelete(

    @NotNull
    Long id

) {

}
