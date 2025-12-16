package io.github.bagdad.ticketbooking.exception;

import lombok.Getter;

@Getter
public class BookingNotFoundException extends RuntimeException {
    private String message;
    private int statusCode;

    public BookingNotFoundException(Long id) {
        super("Booking with id " + id + " not found");
        this.message = "Booking with id " + id + " not found";
        this.statusCode = 404;
    }

}