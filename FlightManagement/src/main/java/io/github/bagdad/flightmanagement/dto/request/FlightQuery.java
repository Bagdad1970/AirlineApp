package io.github.bagdad.flightmanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record FlightQuery (

        @Size(max = 10)
        String number,

        @Size(max = 100)
        String fromCity,

        @Size(max = 100)
        String toCity,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        OffsetDateTime departureMin,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        OffsetDateTime departureMax,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        OffsetDateTime arrivalMin,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        OffsetDateTime arrivalMax,

        @Min(value = 0, message = "Min passenger count cannot be negative")
        @Max(value = 500, message = "Min passenger count must be less or equal than 500")
        Integer passengerCountMin,

        @Min(value = 0, message = "Max passenger count cannot be negative")
        @Max(value = 500, message = "Max passenger count must be less or equal than 500")
        Integer passengerCountMax,

        @DecimalMin(value = "0.01", message = "Ticket price must be positive")
        @Digits(integer = 8, fraction = 2)
        BigDecimal ticketPriceMin,

        @DecimalMin(value = "0.01", message = "Ticket price must be positive")
        @Digits(integer = 8, fraction = 2)
        BigDecimal ticketPriceMax
) {

}
