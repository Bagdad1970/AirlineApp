package io.github.bagdad.models.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record FlightUpdateRequest(

    @NotNull
    Long id,

    @Size(max = 10)
    String number,

    @Size(max = 100)
    String fromCity,

    @Size(max = 100)
    String toCity,

    @JsonProperty("departure")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime departure,

    @JsonProperty("arrival")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime arrival,

    @DecimalMin(value = "0.01", message = "Ticket price must be positive")
    @Digits(integer = 8, fraction = 2)
    BigDecimal ticketPrice
) {
    public FlightUpdateRequest {
        if (departure != null && arrival != null && arrival.isBefore(departure)) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }
    }
}