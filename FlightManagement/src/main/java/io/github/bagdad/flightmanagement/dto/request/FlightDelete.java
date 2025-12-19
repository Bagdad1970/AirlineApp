package io.github.bagdad.flightmanagement.dto.request;

import jakarta.validation.constraints.NotNull;

public record FlightDelete(

    @NotNull
    Long id

) {

}
