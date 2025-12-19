package io.github.bagdad.flightmanagement.messaging;

import io.github.bagdad.models.events.BookingConfirmed;
import io.github.bagdad.models.events.BookingRejected;
import io.github.bagdad.models.events.BookingUpdated;
import io.github.bagdad.models.events.FlightCancelled;

public class FlightEventFactory {

    public static BookingConfirmed bookingConfirmed(Long bookingId, Long flightId) {
        return new BookingConfirmed(
                bookingId,
                flightId
        );
    }

    public static BookingRejected bookingRejected(Long bookingId) {
        return new BookingRejected(
                bookingId
        );
    }

    public static BookingRejected bookingCancelled(Long bookingId) {
        return new BookingRejected(
                bookingId
        );
    }

    public static FlightCancelled flightCancelled(Long flightId) {
        return new FlightCancelled(
                flightId
        );
    }

    public static BookingUpdated bookingUpdated(Long bookingId, Long flightId, Integer passengerCountChange) {
        return new BookingUpdated(
                bookingId,
                flightId,
                passengerCountChange
        );
    }
}
