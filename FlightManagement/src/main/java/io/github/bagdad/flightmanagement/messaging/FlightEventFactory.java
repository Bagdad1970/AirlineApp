package io.github.bagdad.flightmanagement.messaging;

import io.github.bagdad.models.events.*;

public class FlightEventFactory {

    public static BookingConfirmed bookingConfirmed(Long bookingId, Long flightId) {
        return new BookingConfirmed(
                bookingId
        );
    }

    public static BookingRejected bookingRejected(Long bookingId) {
        return new BookingRejected(
                bookingId
        );
    }

    public static FlightCancelled flightCancelled(Long flightId) {
        return new FlightCancelled(
                flightId
        );
    }

    public static BookingUpdateRejected bookingUpdateRejected(Long bookingId, Long flightId, Integer currentPassengerCount, Integer newPassengerCount) {
        return new BookingUpdateRejected(
                bookingId,
                flightId,
                currentPassengerCount,
                newPassengerCount
        );
    }

    public static BookingUpdateConfirmed bookingUpdateConfirmed(Long bookingId, Long flightId) {
        return new BookingUpdateConfirmed(
                bookingId,
                flightId
        );
    }

}
