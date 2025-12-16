package io.github.bagdad.flightmanagement.exception;

import lombok.Getter;

@Getter
public class FlightNotFoundException extends RuntimeException {
    private String message;
    private int statusCode;

    public FlightNotFoundException(long id) {
        super("Flight with id " + id + " not found");
        this.message = "Flight with id " + id + " not found";
        this.statusCode = 404;
    }

}