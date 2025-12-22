package io.github.bagdad.models.requests;


import jakarta.validation.constraints.NotNull;

public record FlightDeleteRequest(

    @NotNull
    Long id

) {

}
