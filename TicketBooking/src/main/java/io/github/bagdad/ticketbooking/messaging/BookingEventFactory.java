package io.github.bagdad.ticketbooking.messaging;

import io.github.bagdad.models.events.BookingCancelled;
import io.github.bagdad.models.events.BookingCreated;
import io.github.bagdad.models.events.BookingUpdated;
import io.github.bagdad.ticketbooking.model.Booking;

public class BookingEventFactory {

    public static BookingCreated created(Booking booking) {
        return new BookingCreated(
                booking.getId(),
                booking.getFlightId(),
                booking.getPassengerCount());
    }

    public static BookingCancelled bookingCancelled(Booking booking) {
        return new BookingCancelled(
                booking.getFlightId(),
                booking.getPassengerCount()
        );
    }

    public static BookingUpdated bookingUpdated(Long bookingId, Long flightId, Integer currentPassengerCount, Integer nwePassengerCount) {
        return new BookingUpdated(
                bookingId,
                flightId,
                currentPassengerCount,
                nwePassengerCount
        );
    }
}
